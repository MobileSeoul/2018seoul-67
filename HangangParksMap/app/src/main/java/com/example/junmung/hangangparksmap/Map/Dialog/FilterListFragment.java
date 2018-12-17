package com.example.junmung.hangangparksmap.Map.Dialog;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.junmung.hangangparksmap.R;

import java.util.ArrayList;
import java.util.List;

public class FilterListFragment extends Fragment {
    private static final int LIST_SIZE = 9;
    private GridView gridView;
    private FilterItemOnClickListener filterClickListener ;

    private List<FilterItem> filterItems;

    public FilterListFragment() {
    }

    public interface FilterItemOnClickListener {
        void onFilterItemClicked(String name);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_filter_dialog_filter_list, container, false);
        gridView = view.findViewById(R.id.dialog_filter_list_gridView);
        gridView.setOnItemClickListener(itemClickListener);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        filterItems= new ArrayList<>(LIST_SIZE);

        String[] contents = {
                "음식점", "편의점", "자전거대여",
                "화장실", "캠핑장", "레저",
                "행사장", "수영장",  "주차장"
        };

        int[] imgs = {
                R.drawable.icon_restaurant, R.drawable.icon_market, R.drawable.icon_bicycle,
                R.drawable.icon_toilet, R.drawable.icon_camping, R.drawable.icon_leisure,
                R.drawable.icon_special, R.drawable.icon_swim, R.drawable.icon_park
        };

        for(int i = 0; i < LIST_SIZE; i++){
            FilterItem item = new FilterItem(contents[i], imgs[i]);
            filterItems.add(item);
        }

        FilterItemAdapter adapter = new FilterItemAdapter(filterItems);
        gridView.setOnItemClickListener(itemClickListener);
        gridView.setAdapter(adapter);
    }


    private GridView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 클릭했을때 값을 계산해주는 메소드를 이곳에서 호출한다.
            // 계산한뒤 또 getName() 같은 함수를 메인에서 호출하게 되면 받아올수잇다.
            FilterItem item = (FilterItem)parent.getItemAtPosition(position);

            filterClickListener.onFilterItemClicked(item.getName());
        }
    };





    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToParentFragment(getParentFragment());
    }

    private void onAttachToParentFragment(Fragment parentFragment) {
        try {
            filterClickListener = (FilterItemOnClickListener)parentFragment;

        }
        catch (ClassCastException e) {
            throw new ClassCastException(
                    parentFragment.toString() + " must implement FilterItemOnClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        filterClickListener = null;
    }
}

























