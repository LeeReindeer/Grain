package xyz.leezoom.grain.ui;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.leezoom.grain.R;


public class LibraryFragment extends Fragment
implements ViewPager.OnPageChangeListener, TabLayout.OnTabSelectedListener{

    private FloatingActionButton fab;
    private FloatingActionsMenu fabMenu;
    private BookSearchFragment mSearch;
    private BooksFragment mBooks;
    private List<String> mTitleList;
    private List<Fragment> mFragmentList;

    @BindView(R.id.view_page) ViewPager viewPager;
    @BindView(R.id.lb_tab_top) TabLayout tabLayout;

    public LibraryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        ButterKnife.bind(this,view);
        fab = getActivity().findViewById(R.id.fab_b);
        fabMenu = getActivity().findViewById(R.id.multiple_actions);
        fab.setVisibility(View.VISIBLE);
        fab.setTitle("Search");
        fab.setIcon(R.drawable.ic_search_white_48dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getContext(),"Search",Toast.LENGTH_SHORT).show();
                tabLayout.getTabAt(1).select();
                fabMenu.collapse();
            }
        });
        mFragmentList = new ArrayList<>();
        if (mBooks == null || mBooks.isDetached()) mFragmentList.add(mBooks = new BooksFragment());
        if (mSearch == null || mSearch.isDetached()) mFragmentList.add(mSearch = new BookSearchFragment());

        mTitleList = new ArrayList<>();
        mTitleList.add("My Books");
        mTitleList.add("Search");
        //use getChildFragmentManager
        ViewPageAdapter adapter = new ViewPageAdapter(getChildFragmentManager(),mFragmentList,mTitleList);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager,true);
        viewPager.addOnPageChangeListener(this);
        tabLayout.addOnTabSelectedListener(this);
        return  view;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Toolbar toolbar=getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        fab.setVisibility(View.GONE);
        //destroy child fragment
        if (mBooks != null) {
            mBooks.onDestroy();
            mBooks = null;
        }
        if (mSearch != null) {
            mSearch.onDestroy();
            mSearch = null;
        }
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        //TabLayout里的TabItem被选中的时候触发
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //viewPager滑动之后显示触发
        tabLayout.getTabAt(position).select();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class ViewPageAdapter extends FragmentPagerAdapter{

        private List<Fragment> fragmentList;
        private List<String> titleList;

        public ViewPageAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titleList) {
            super(fm);
            this.fragmentList = fragmentList;
            this.titleList = titleList;
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("fragment",position+"");
            return fragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return  titleList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }


    }

}
