package xyz.leezoom.grain.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.leezoom.grain.R;
import xyz.leezoom.grain.module.QueryType;
import xyz.leezoom.grain.module.ServerIp;
import xyz.leezoom.grain.module.User;
import xyz.leezoom.grain.util.MyBase64;
import xyz.leezoom.grain.util.PackMessage;
import xyz.leezoom.grain.util.TcpUtil;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private SharedPreferences info;
    private SharedPreferences query;

    // UI references.
    @BindView(R.id.account) EditText mAccountView;
    @BindView(R.id.password) EditText mPasswordView;
    @BindView(R.id.name) EditText mNameView;
    @BindView(R.id.login_form) View mLoginFormView;
    @BindView(R.id.login_progress) View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        ButterKnife.bind(this);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button SignInButton = (Button) findViewById(R.id.sign_in_button);
        SignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }



    private String getDeviceInfo(){
        String devName = Build.PRODUCT;
        String devMac = "F4:CB:52:13:05:49";
        String localIP = "192.168.1.100";
        StringBuilder sb = new StringBuilder();
        String deviceInfo = sb.append(devName).append(",").append(localIP).append(",").append(devMac).append(",,中国移动,460022147545798,898600C1111456040898").toString();
        return deviceInfo;
    }

    /**
     * errors are presented ,
     * no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mAccountView.setError(null);
        mPasswordView.setError(null);
        mNameView.setError(null);

        // Store values at the time of the login attempt.
        String account = mAccountView.getText().toString();
        String password = mPasswordView.getText().toString();
        String name = mNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(name)){
            mNameView.setError(getString(R.string.error_field_required));
            focusView = mNameView;
            cancel = true;
        }

        // Check for a valid password
        if (TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel=true;
        }else if (!isPasswordValid(password)){
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid account
        if (TextUtils.isEmpty(account)) {
            mAccountView.setError(getString(R.string.error_field_required));
            focusView = mAccountView;
            cancel = true;
        } else if (!isAccountValid(account)) {
            mAccountView.setError(getString(R.string.error_invalid_schoolId));
            focusView = mAccountView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(account, password,name);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     *
     * @param account schoolId
     * @return
     */
    private boolean isAccountValid(String account) {
        return account.length() == 9;
    }

    /**
     *
     * @param password password is your id card number
     * @return
     */
    private boolean isPasswordValid(String password) {
        return password.length() == 9;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mAccount;
        private final String mPassword;
        private final String mName;

        UserLoginTask(String account, String password, String name) {
            mAccount = account;
            mPassword = password;
            mName = name;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Simulate network access.
                Thread.sleep(2000);
                //first time validation
                User user=new User(mName,mAccount,mPassword,"","",getDeviceInfo(),""); // get token
                PackMessage packMessage=new PackMessage(QueryType.Validation.name(), mName, mAccount, user.getAccount(), user.getPassword(),
                        user.getPhoneNumber(), user.getCertCard(), user.getToken(), user.getExtend(),user.getHostInfo(),user.getVersion(),user.getOthers());
                TcpUtil tcp = new TcpUtil(ServerIp.baseServerPort,packMessage);
                String [] tokens = tcp.receiveString().split(PackMessage.SplitFields);
                String firstToken = tokens[3];
                String certCard = tokens[1];
                //second validation, get real token
                User realUser = new User(mName,mAccount,mPassword,certCard,mAccount,getDeviceInfo(),firstToken);
                PackMessage realMessage=new PackMessage(QueryType.Validation.name(), mName, mAccount, realUser.getAccount(), realUser.getPassword(),
                        realUser.getPhoneNumber(), realUser.getCertCard(), realUser.getToken(), realUser.getExtend(),realUser.getHostInfo(),realUser.getVersion(),realUser.getOthers());
                TcpUtil realTcp = new TcpUtil(ServerIp.baseServerPort,realMessage);
                String realToken = realTcp.receiveString().split(PackMessage.SplitFields)[3];
                info = getSharedPreferences("info",MODE_PRIVATE);
                query = getSharedPreferences("query",MODE_PRIVATE);
                SharedPreferences.Editor editor=info.edit();
                SharedPreferences.Editor editor1 = query.edit();
                editor1.putString("ttt",MyBase64.stringToBASE64(realToken)); //token
                editor1.apply();
                editor.putString("aaa", MyBase64.stringToBASE64(mAccount)); //school id
                editor.putString("ppp",MyBase64.stringToBASE64(mPassword)); //pass
                editor.putString("nnn",MyBase64.stringToBASE64(mName));  //name
                editor.putString("hhh",MyBase64.stringToBASE64(getDeviceInfo())); //device info
                editor.putString("ccc",MyBase64.stringToBASE64(certCard)); //cert card
                Log.d("device",getDeviceInfo());
                editor.apply();
            } catch (InterruptedException e) {
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                info=getSharedPreferences("info",MODE_PRIVATE);
                SharedPreferences.Editor editor=info.edit();
                editor.putBoolean("isLogin",true);
                editor.apply();
                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}