package com.example.junmung.hangangparksmap.Map.Dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.junmung.hangangparksmap.R;

import java.util.List;

public class FilterItemAdapter extends BaseAdapter {

    private List<FilterItem> filterItems;
    private LayoutInflater inflater;

    public FilterItemAdapter(List<FilterItem> filterItems) {
        this.filterItems = filterItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        if(convertView == null){
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.item_filter, parent,false);
        }
        TextView filterName = convertView.findViewById(R.id.filter_gridView_item_textView);
        ImageView filterImg = convertView.findViewById(R.id.filter_gridView_item_imageView);

        filterName.setText(filterItems.get(position).getName());
        filterImg.setImageResource(filterItems.get(position).getImgRes());

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return filterItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getCount() {
        return filterItems.size();
    }

}
