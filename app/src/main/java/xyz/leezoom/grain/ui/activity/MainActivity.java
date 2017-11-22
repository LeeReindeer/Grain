/*
 * Created by Lee.
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 10/28/17 11:32 AM
 */

package xyz.leezoom.grain.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.pgyersdk.feedback.PgyFeedback;
import com.pgyersdk.update.PgyUpdateManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.leezoom.grain.R;
import xyz.leezoom.grain.module.User;
import xyz.leezoom.grain.ui.fragment.CardFragment;
import xyz.leezoom.grain.ui.fragment.FunctionFragment;
import xyz.leezoom.grain.ui.fragment.LibraryFragment;
import xyz.leezoom.grain.ui.fragment.MarkFragment;
import xyz.leezoom.grain.ui.fragment.NetWorkFailedFragment;
import xyz.leezoom.grain.ui.fragment.TodayFragment;
import xyz.leezoom.grain.util.MyBase64;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FunctionFragment.FOnClickListener {

    private boolean isLogin=false;
    private SharedPreferences info;
    private SharedPreferences query;
    private static User user;

    private IntentFilter intentFilter;
    private NetWorkChangeReceiver netWorkChangeReceiver;
    private NetworkInfo networkInfo;

    private FunctionFragment mainFunction;
    private MarkFragment mMark;
    private CardFragment mCard;
    private LibraryFragment mLibrary;
    private TodayFragment mToday;
    private NetWorkFailedFragment mFailPage;

    private Toolbar toolbar;
    @BindView(R.id.multiple_actions) FloatingActionsMenu mulitiAction;
    @BindView(R.id.fab_a) com.getbase.floatingactionbutton.FloatingActionButton actionA;
    @BindView(R.id.fab_b) com.getbase.floatingactionbutton.FloatingActionButton actionB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkLogin()) {
            initData();
            initUI();
            checkPermission();
        }
        //do after UI load
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                registerNetWork();
                //register pgy update service
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                boolean isAutoCheck = preferences.getBoolean(SettingActivity.KEY_PREF_AUTO, true);
                if (isAutoCheck) {
                    PgyUpdateManager.setIsForced(false);
                    PgyUpdateManager.register(MainActivity.this, "xyz.leezoom.grain");
                }
            }
        });
    }

    @OnClick (R.id.fab_a) void fabA(){
        PgyFeedback.getInstance().showDialog(MainActivity.this);
    }

    @OnClick (R.id.fab_b) void fabB(){

    }

    //if not login ,start login
    private boolean checkLogin(){
        info =getSharedPreferences("info",MODE_PRIVATE);
        isLogin= info.getBoolean("isLogin",false);
        if (isLogin) {
            return true;
        }
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
        return false;
    }

    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            Toast.makeText(MainActivity.this,getString(R.string.app_name)+"need write storage permission to run.",Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else {
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //do nothing
            }else {
                Toast.makeText(MainActivity.this,"You denied me",Toast.LENGTH_SHORT).show();
            }
        }
    }


    public static User getUser() {
        if (MainActivity.user == null) {
            return null;
        }
        return MainActivity.user;
    }

    private void initData(){
        user=new User();
        info = getSharedPreferences("info", Context.MODE_PRIVATE);
        query = getSharedPreferences("query",Context.MODE_PRIVATE);
        String name = MyBase64.BASE64ToString(info.getString("nnn","none"));
        String account = MyBase64.BASE64ToString(info.getString("aaa","none"));
        String pass = MyBase64.BASE64ToString(info.getString("ppp","none"));
        String hostInfo = MyBase64.BASE64ToString(info.getString("hhh","none"));
        String idCard = MyBase64.BASE64ToString(info.getString("ccc","none"));
        user.setName(name);
        user.setAccount(account);
        user.setSchoolId(account);
        user.setCertCard(idCard);
        user.setExtend(account);
        user.setPassword(pass);
        user.setToken(MyBase64.BASE64ToString(query.getString("ttt","none")));
        user.setHostInfo(hostInfo);
    }

    private void registerNetWork() {
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        netWorkChangeReceiver = new NetWorkChangeReceiver();
        registerReceiver(netWorkChangeReceiver, intentFilter);
    }

    private void initUI(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setDefaultFragment();
        ButterKnife.bind(this);
        actionA.setTitle(getString(R.string.drawer_title_feedback));
        actionB.setVisibility(View.GONE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        drawer.openDrawer(GravityCompat.START);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView  navAccountName= (TextView) header.findViewById(R.id.account_name);
        navAccountName.setText(user.getName());
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

        if (mulitiAction.isExpanded()){
            mulitiAction.collapse();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            PgyUpdateManager.unregister();
            unregisterReceiver(netWorkChangeReceiver);
        } catch (NullPointerException e) {
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (networkInfo !=null && networkInfo.isAvailable()) {
            switch (id) {
                case R.id.nav_settings:
                   Intent intent = new Intent(this, SettingActivity.class);
                   startActivity(intent);
                    break;
                case R.id.nav_about:
                    //show about page
                    Intent about = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(about);
                    break;
                case R.id.nav_exit:
                    info = getSharedPreferences("info", MODE_PRIVATE);
                    SharedPreferences.Editor editor = info.edit();
                    editor.putString("aaa", "");
                    editor.putString("nnn", "");
                    editor.putString("ppp", "");
                    editor.putBoolean("isLogin", false);
                    editor.apply();
                    Intent exit = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(exit);
                    finish();
                    break;
                case R.id.nav_mark:
                    toolbar.setTitle(R.string.fun_title_your_mark);
                    if (mMark == null) mMark = new MarkFragment();
                    transaction.replace(R.id.tab_content, mMark);
                    break;
                case R.id.nav_card:
                    toolbar.setTitle(R.string.fun_title_your_card);
                    if (mCard == null) mCard = new CardFragment();
                    transaction.replace(R.id.tab_content, mCard);
                    break;
                case R.id.nav_library:
                    toolbar.setTitle(getString(R.string.fun_title_library));
                    if (mLibrary == null) mLibrary = new LibraryFragment();
                    transaction.replace(R.id.tab_content, mLibrary);
                    break;
                case R.id.nav_schedule:
                    toolbar.setTitle(getString(R.string.fun_title_schedule));
                    if (mToday == null) mToday = new TodayFragment();
                    transaction.replace(R.id.tab_content, mToday);
                    //Toast.makeText(this, "This function is unavailable now", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            transaction.addToBackStack(null);
        } else {
            if (mFailPage == null) {
                mFailPage = new NetWorkFailedFragment();
                transaction.replace(R.id.tab_content, mFailPage);
                // TODO: 10/26/17 send error message to failedPage
                //mFailPage.setArguments((Bundle) );
            }
            transaction.replace(R.id.tab_content, mFailPage);
        }
        transaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFClick(int id) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (networkInfo != null && networkInfo.isAvailable()) {
            switch (id) {
                case R.id.card_mark:
                    toolbar.setTitle(R.string.fun_title_your_mark);
                    if (mMark == null) mMark = new MarkFragment();
                    transaction.replace(R.id.tab_content, mMark);
                    break;
                case R.id.card_class:
                    toolbar.setTitle(R.string.fun_title_schedule);
                    if (mToday == null) mToday = new TodayFragment();
                    transaction.replace(R.id.tab_content, mToday);
                    //Toast.makeText(this, "This function is unavailable now", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.card_card:
                    toolbar.setTitle(R.string.fun_title_your_card);
                    if (mCard == null) mCard = new CardFragment();
                    transaction.replace(R.id.tab_content, mCard);
                    break;
                case R.id.card_library:
                    //Toast.makeText(this,"Coming soon",Toast.LENGTH_SHORT).show();
                    //Intent libraryIntent = new Intent(MainActivity.this,LibraryActivity.class);
                    //startActivity(libraryIntent);
                    toolbar.setTitle(getString(R.string.fun_title_library));
                    if (mLibrary == null) mLibrary = new LibraryFragment();
                    transaction.replace(R.id.tab_content, mLibrary);
                    break;
            }
            transaction.addToBackStack(null);
        } else {
            if (mFailPage == null) mFailPage = new NetWorkFailedFragment();
            transaction.replace(R.id.tab_content, mFailPage);
        }
        transaction.commit();
        //add to stack
    }

    class NetWorkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo= connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
                //have network, do nothing.
                //Toast.makeText(context, "Network is available",Toast.LENGTH_SHORT).show();
                //Log.d("Network", "1");
            } else {
                //show failed fragment
                Toast.makeText(context, "Network is unavailable",Toast.LENGTH_SHORT).show();
                //Log.d("Network", "0");
            }
        }
    }
}
