package moe.leer.grain.login

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_login.*
import moe.leer.grain.*
import moe.leer.grain.activity.BaseActivity


class LoginActivity : BaseActivity() {

    private lateinit var loginViewModel: LoginViewModel

    private val LAST_ID_SP = "lastId"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initData()
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        loginBtn.dispose()
    }

    override fun initData() {
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        // restore last try login id
        usernameEdit.setText(this.getSP(App.USER_SP).getString(LAST_ID_SP, ""))
    }

    override fun initView() {
        super.initView()
        // show keyboard at password edittext
        if (usernameEdit.text!!.isNotEmpty()) {
            Util.showKeyboard(this, passwordEdit)
        }

        val handler = Handler()
        loginViewModel.getLoginStatus()
            .observe(this, Observer { status ->
                Log.d(TAG, "statusChange: $status")
                when (status) {
                    FuckSchoolApi.LOGIN_PROCESS -> {
                    }

                    FuckSchoolApi.LOGIN_ID_PASS_ERROR -> {
                        loginBtn.revertAnimation {
                            loginBtn.setBackgroundResource(R.drawable.circular_button)
                        }
                        passwordInputLayout.error = "ID or password error"
                        Util.showKeyboard(this, passwordEdit)
                    }

                    FuckSchoolApi.LOGIN_NET_ERROR -> {
                        handler.postDelayed({
                            loginBtn.revertAnimation {
                                loginBtn.setBackgroundResource(R.drawable.circular_button)
                            }
                            Snackbar.make(
                                loginRootView,
                                getString(R.string.hint_check_network),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }, 600)
                    }

                    FuckSchoolApi.LOGIN_SUCCESS -> {
                        loginBtn.doneLoadingAnimation(
                            ContextCompat.getColor(this, R.color.green),
                            Util.getBitmapFromVectorDrawable(this, R.drawable.ic_check_white_24dp)
                        )
                        (application as App).isLogin = true

                        handler.postDelayed({
                            finishAfterTransition()
                        }, 500)
                    }
                }
            })

        loginBtn.setOnClickListener {
            usernameInputLayout.error = ""
            passwordInputLayout.error = ""

            if (inputValid()) {
                loginBtn.startAnimation()

                this.getSPEdit(App.USER_SP) {
                    putString(LAST_ID_SP, usernameEdit.text.toString())
                    apply()
                }
                loginViewModel.doLogin(usernameEdit.text.toString().toInt(), passwordEdit.text.toString())
            }
        }
        //  If enabled, a button is displayed to toggle between the password being displayed as plain-text or disguised, when your EditText is set to display a password.
        passwordInputLayout.isPasswordVisibilityToggleEnabled = true

        // just for joke
        textPrivacyPolicy.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
                .setTitle(getString(R.string.privacy_policy))
                .setPositiveButton("Ok") { dialog, id ->
                }
                .setMessage(getString(R.string.hint_privacy_policy) + Util.getEmojiByUnicode(0x1F61B))
                .create()
                .show()
        }
    }

    private fun inputValid(): Boolean {
        val idInput = usernameEdit.text.toString()
        val passwordInput = passwordEdit.text.toString()
        if (idInput.isEmpty()) {
            usernameInputLayout.error = "Fill ID field"
            Util.showKeyboard(this, usernameEdit)
            return false
        }

        if (passwordInput.isEmpty()) {
            passwordInputLayout.error = "Fill password field"
            Util.showKeyboard(this, passwordEdit)
            return false
        }
        try {
            idInput.toInt()
        } catch (e: NumberFormatException) {
            usernameInputLayout.error = "ID must be digit"
            Util.showKeyboard(this, usernameEdit)
            return false
        }
        if (idInput.length != 9) {
            usernameInputLayout.error = "ID must be 9 digits"
            Util.showKeyboard(this, usernameEdit)
            return false
        }
        return true
    }
}
