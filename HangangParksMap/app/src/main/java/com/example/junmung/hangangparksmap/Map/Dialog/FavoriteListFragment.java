package com.example.junmung.hangangparksmap.Map.Dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.junmung.hangangparksmap.ARGuide.Point;
import com.example.junmung.hangangparksmap.DataBase.DBHelper;
import com.example.junmung.hangangparksmap.FavoritePoint;
import com.example.junmung.hangangparksmap.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FavoriteListFragment extends Fragment {
    private ListView listView;
    private List<String> items;
    private DBHelper dbHelper;
    private FavoriteItemOnClickListener favoriteClickListener;

    public FavoriteListFragment() {
    }

    public interface FavoriteItemOnClickListener {
        void onFavoriteItemClicked(FavoritePoint point);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_filter_dialog_favorite_list, container, false);
        listView = view.findViewById(R.id.dialog_favorite_list_recyclerView);
        dbHelper = DBHelper.getInstance();
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        items = new ArrayList<>();
        int favoriteSize = dbHelper.getFavoriteSize();

        if(favoriteSize == 0)
            items.add("즐겨찾는 장소가 없습니다");
        else{
            ArrayList<FavoritePoint> points = dbHelper.getFavoriteItems();
            for(FavoritePoint point : points)
                items.add(point.getName());

            listView.setOnItemClickListener(itemClickListener);
        }

        ArrayAdapter adapter = new ArrayAdapter(getContext().getApplicationContext(), android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
    }


    private ListView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // favorite item 은 realm 을 기반으로 데이터를 가져오기 때문에
            // 이미 만들어져있는 realm 데이터를 클릭했을때 보내줘야한다.
            // 즉 여기서 넘겨줘야 하는 위치인 position 값은 realm 에서의 index 로 구성을 해야한다.
            String itemName = (String)parent.getItemAtPosition(position);
            FavoritePoint point = dbHelper.getFavoriteItem(itemName);
            favoriteClickListener.onFavoriteItemClicked(point);
        }
    };



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToParentFragment(getParentFragment());
    }

    private void onAttachToParentFragment(Fragment parentFragment) {
        try {
            favoriteClickListener = (FavoriteItemOnClickListener)parentFragment;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(
                    parentFragment.toString() + " must implement FavoriteItemOnClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        favoriteClickListener = null;
    }
}
