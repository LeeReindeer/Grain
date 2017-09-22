package xyz.leezoom.grain.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
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
import android.view.Menu;
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
import xyz.leezoom.grain.ui.fragment.ScheduleFragment;
import xyz.leezoom.grain.util.MyBase64;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FunctionFragment.FOnClickListener {

    private boolean isLogin=false;
    private SharedPreferences info;
    private SharedPreferences query;
    private User user;
    private FunctionFragment mainFunction;
    private MarkFragment mMark;
    private CardFragment mCard;
    private LibraryFragment mLibrary;
    private ScheduleFragment mSchedule;
    private Toolbar toolbar;
    @BindView(R.id.multiple_actions) FloatingActionsMenu mulitiAction;
    @BindView(R.id.fab_a) com.getbase.floatingactionbutton.FloatingActionButton actionA;
    @BindView(R.id.fab_b) com.getbase.floatingactionbutton.FloatingActionButton actionB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PgyUpdateManager.setIsForced(false);
        PgyUpdateManager.register(this,"xyz.leezoom.grain");
        checkLogin();
        loadData();
        initUI();
        checkPermission();
    }

    @OnClick (R.id.fab_a) void fabA(){
        PgyFeedback.getInstance().showDialog(MainActivity.this);
    }

    @OnClick (R.id.fab_b) void fabB(){

    }

    //if not login ,start login
    private void checkLogin(){
        info =getSharedPreferences("info",MODE_PRIVATE);
        isLogin= info.getBoolean("isLogin",false);
        if (isLogin) return;
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
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

    private void loadData(){
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


    private void initUI(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

        if (mulitiAction.isExpanded()){
            mulitiAction.collapse();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PgyUpdateManager.unregister();
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
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        switch (id){
            case R.id.nav_feedback:
                PgyFeedback.getInstance().showDialog(MainActivity.this);
                break;
            case R.id.nav_update:
                //check update
                //Toast.makeText(this,"This is the last version",Toast.LENGTH_SHORT).show();
                Intent update = new Intent(Intent.ACTION_VIEW,Uri.parse(getString(R.string.update_page)));
                startActivity(update);
                break;
            case R.id.nav_about:
                //show about page
                Intent about = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(about);
                break;
            case R.id.nav_exit:
                //finish();
                info = getSharedPreferences("info",MODE_PRIVATE);
                SharedPreferences.Editor editor = info.edit();
                editor.putString("aaa","");
                editor.putString("nnn","");
                editor.putString("ppp","");
                editor.putBoolean("isLogin",false);
                editor.apply();
                Intent exit = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(exit);
                finish();
                break;
            case R.id.nav_mark:
                toolbar.setTitle(R.string.fun_title_your_mark);
                if (mMark == null) mMark = new MarkFragment();
                transaction.replace(R.id.tab_content,mMark);
                break;
            case R.id.nav_card:
                toolbar.setTitle(R.string.fun_title_your_card);
                if (mCard == null) mCard = new CardFragment();
                transaction.replace(R.id.tab_content,mCard);
                break;
            case R.id.nav_library:
                toolbar.setTitle(getString(R.string.fun_title_library));
                if (mLibrary == null) mLibrary = new LibraryFragment();
                transaction.replace(R.id.tab_content,mLibrary);
                break;
            case R.id.nav_schedule:
                //toolbar.setTitle(getString(R.string.fun_title_schedule));
                //if (mSchedule == null) mSchedule = new ScheduleFragment();
                //transaction.replace(R.id.tab_content,mSchedule);
                break;
            default:
                break;
        }
        transaction.addToBackStack(null);
        transaction.commit();
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
                if (mMark == null) mMark = new MarkFragment();
                transaction.replace(R.id.tab_content,mMark);
                break;
            case R.id.card_class:
                //toolbar.setTitle("Course Table");
                Toast.makeText(this,"This function is unavailable now",Toast.LENGTH_SHORT).show();
                break;
            case R.id.card_card:
                toolbar.setTitle(R.string.fun_title_your_card);
                if (mCard == null) mCard = new CardFragment();
                transaction.replace(R.id.tab_content,mCard);
                break;
            case R.id.card_library:
                //Toast.makeText(this,"Coming soon",Toast.LENGTH_SHORT).show();
                //Intent libraryIntent = new Intent(MainActivity.this,LibraryActivity.class);
                //startActivity(libraryIntent);
                toolbar.setTitle(getString(R.string.fun_title_library));
                if (mLibrary == null) mLibrary = new LibraryFragment();
                transaction.replace(R.id.tab_content,mLibrary);
                break;
        }
        //add to stack
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
