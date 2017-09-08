package xyz.leezoom.grain.ui;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.leezoom.grain.R;

public class LibraryActivity extends AppCompatActivity
        implements MaterialSearchView.OnQueryTextListener,
MaterialSearchView.SearchViewListener{

    private MyBooksFragment mBookFm;
    private MyFavorBooksFragment mFavorFm;

    @BindView(R.id.lb_toolbar) Toolbar toolbar;
    @BindView(R.id.lb_search_view) MaterialSearchView searchView;
    @BindView(R.id.tab_my_books) ImageView mBooks;
    @BindView(R.id.tab_my_favor) ImageView mFavor;

    @OnClick (R.id.tab_my_books)void books(){
        Toast.makeText(this,"clicked",Toast.LENGTH_SHORT).show();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (mBookFm == null) mBookFm = new MyBooksFragment();
        transaction.replace(R.id.lb_content,mBookFm);
        transaction.commit();
    }

    @OnClick (R.id.tab_my_favor)void favor(){
        Toast.makeText(this,"clicked",Toast.LENGTH_SHORT).show();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if (mFavorFm == null) mFavorFm = new MyFavorBooksFragment();
        transaction.replace(R.id.lb_content,mFavorFm);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.library_layout);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSearchViewListener(this);
    }


    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()){
            searchView.closeSearch();
        }else {
            super.onBackPressed();
        }
    }

    /**
     *search listener
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d("search","submit:"+query);
        // TODO: 9/8/17  do query, show query fragment

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d("search","Text change"+newText);
        return false;
    }

    @Override
    public void onSearchViewShown() {
        Log.d("search","show");
    }

    @Override
    public void onSearchViewClosed() {
        Log.d("search","closed");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return  true;
    }
}
