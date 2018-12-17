package com.example.junmung.hangangparksmap.Map.Dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.junmung.hangangparksmap.ARGuide.Point;
import com.example.junmung.hangangparksmap.FavoritePoint;
import com.example.junmung.hangangparksmap.R;


public class FilterDialogFragment extends DialogFragment implements FilterListFragment.FilterItemOnClickListener, FavoriteListFragment.FavoriteItemOnClickListener{
    private ViewPager viewPager;
    private BottomNavigationView tabLayout;
    private MenuItem tabMenuItem;

    private DialogDismissListener dismissListener;


    public FilterDialogFragment() { }


    public interface DialogDismissListener {
        // 선택안하고 취소했을경우, 필터선택, 즐겨찾기 선택 경우 나눠서 구현
        void onFilterDialogDismiss(String name);
        void onFilterDialogDismiss(FavoritePoint point);
    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_filter_dialog, container, false);

        viewPager = view.findViewById(R.id.fragment_map_filter_dialog_viewPager);
        tabLayout = view.findViewById(R.id.fragment_map_filter_dialog_bottomNavigationView);


        // 뷰페이저 세팅
        viewPager.setAdapter(new PagerAdapter(getChildFragmentManager()));
        viewPager.addOnPageChangeListener(pageChangeListener);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(0);

        tabLayout.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_tab_filter:    viewPager.setCurrentItem(0);    return true;
                    case R.id.item_tab_favorite:   viewPager.setCurrentItem(1);    return true;
                }
                return false;
            }
        });
        return view;


    }


    // 페이지 변경 리스너
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if(tabMenuItem != null)
                tabMenuItem.setChecked(false);

            tabMenuItem = tabLayout.getMenu().getItem(position);
            tabMenuItem.setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    // 뷰페이저 Adapter
    private class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:     return new FilterListFragment();
                case 1:     return new FavoriteListFragment();
                default:    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }








    @Override
    public void onFilterItemClicked(String name) {
        dismissListener.onFilterDialogDismiss(name);
        dismiss();
    }

    @Override
    public void onFavoriteItemClicked(FavoritePoint point) {
        dismissListener.onFilterDialogDismiss(point);
        dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof DialogDismissListener)
            dismissListener = (DialogDismissListener)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dismissListener = null;
    }

    /**
     *  1. Fragment -> Activity 간의 통신
     *  FilterDialog 는 MapActivity 안에서 필터버튼을 눌렀을 때 실행되는 Dialog 로써,
     *  안에는 ViewPager + Fragment 형식으로 구성되어있다.
     *  FilterDialog 자체가 DialogFragment 이기 때문에 Dialog 안에서 버튼을 눌렀을 때,
     *  MapActivity 로 사용자가 선택한 값들을 전달해줘야 하기 때문에
     *  Fragment -> Activity 간의 통신이 이루어져야 한다.
     *  DialogFragment 내의 onAttach() 에서 DialogDismissListener 를
     *  MapActivity 와 이어줌으로써 데이터를 전달하였다.
     *
     *
     *  2. childFragment -> Fragment  간의 통신
     *  FilterDialog 내부의 ViewPager 에서는 2개의 childFragment 를 통해 화면구성을 하였다.
     *  Fragment 간의 통신이기 때문에 위의 방법으로는 되지않고,
     *  childFragment 의 onAttach() 에서 onAttachToParentFragment(getParentFragment()) 를 호출하여
     *  Listener 와 ParentFragment 를 이어주어 데이터를 전달하였다.
     */
}
