package xyz.leezoom.grain.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import xyz.leezoom.grain.R;
import xyz.leezoom.grain.module.User;
import xyz.leezoom.grain.util.MyBase64;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FunctionFragment.FOnClickListener {

    private boolean isLogin=false;
    private SharedPreferences preferences;
    private User user;
    private FunctionFragment mainFunction;
    private MarkFragment mMark;
    private CardFragment mCard;
    private Toolbar toolbar;
    private EditText editToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkLogin();
        loadData();
        initUI();

    }


    //if not login ,start login
    private void checkLogin(){
        preferences=getSharedPreferences("info",MODE_PRIVATE);
        isLogin=preferences.getBoolean("isLogin",false);
        if (isLogin) return;
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadData(){
        user=new User();
        preferences=getSharedPreferences("info",MODE_PRIVATE);
        user.setAccount(preferences.getString("aaa","none"));
    }


    private void initUI(){
        //ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Enter your token",Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                AlertDialog dialog= builder
                        .setTitle("Your Token")
                        .setIcon(R.mipmap.ic_launcher)
                        .setCancelable(true)
                        .setView(editToken=new EditText(MainActivity.this))
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String token = editToken.getText().toString();
                                Log.d("Main",token);
                                Log.d("Main",i+"");
                                //token
                                preferences = getSharedPreferences("query",MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                if (token!=null&&!token.isEmpty()&&token.length()==32) {
                                    editor.putString("ttt", MyBase64.stringToBASE64(token));
                                    Log.d("Main",token);
                                    editor.apply();
                                }
                                editor.clear();
                            }
                        })
                        .setNegativeButton("取消",null)
                        .create();
                dialog.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        drawer.openDrawer(GravityCompat.START);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setDefaultFragment();
    }

    private void setDefaultFragment(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        mainFunction = new FunctionFragment();
        transaction.replace(R.id.tab_content, mainFunction);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this,"Coming soon",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.nav_feedback:
                //send email to me
                Intent data = new Intent(Intent.ACTION_SENDTO);
                data.setData(Uri.parse("mailto:reindeerlee.work@gmail.com"));
                data.putExtra(Intent.EXTRA_SUBJECT, "Grain feedback");
                data.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(data);
                break;
            case R.id.nav_update:
                //check update
                Toast.makeText(this,"This is the last version",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_about:
                //show about page
                Intent net = new Intent(Intent.ACTION_VIEW,Uri.parse("https://github.com/LeeReindeer/Grain/blob/master/README.md"));
                startActivity(net);
                break;
            case R.id.nav_exit:
                //finish();
                preferences = getSharedPreferences("info",MODE_PRIVATE);
                SharedPreferences.Editor editor =preferences.edit();
                editor.putString("aaa","");
                editor.putString("nnn","");
                editor.putString("ppp","");
                editor.putBoolean("isLogin",false);
                editor.apply();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_other_app:
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFClick(int id) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch (id){
            case R.id.card_mark:
                toolbar.setTitle(R.string.fun_title_your_mark);
                Toast.makeText(this,"mark,call on mainActivity",Toast.LENGTH_SHORT).show();
                if (mMark == null) mMark = new MarkFragment();
                transaction.replace(R.id.tab_content,mMark);
                break;
            case R.id.card_class:
                //toolbar.setTitle("Course Table");
                Toast.makeText(this,"Coming soon",Toast.LENGTH_SHORT).show();
                break;
            case R.id.card_card:
                Toast.makeText(this,"Coming soon",Toast.LENGTH_SHORT).show();
                //toolbar.setTitle(R.string.fun_title_your_card);
                //if (mCard == null) mCard = new CardFragment();
                //transaction.replace(R.id.tab_content,mCard);
                break;
            case R.id.card_library:
                Toast.makeText(this,"Coming soon",Toast.LENGTH_SHORT).show();
                //toolbar.setTitle(R.string.fun_title_library);
                break;
        }
        //add to stack
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
