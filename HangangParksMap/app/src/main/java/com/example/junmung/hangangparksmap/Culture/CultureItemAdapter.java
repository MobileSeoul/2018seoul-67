package com.example.junmung.hangangparksmap.Culture;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junmung.hangangparksmap.DataBase.DBHelper;
import com.example.junmung.hangangparksmap.Map.MapActivity;
import com.example.junmung.hangangparksmap.R;

import java.util.ArrayList;
import java.util.List;

public class CultureItemAdapter extends RecyclerView.Adapter<CultureItemAdapter.ViewHolder> {
    private List<CultureItem> cultureItems;
    private Context context;
    private int lastPosition = -1;


    public CultureItemAdapter(List<CultureItem> filterItems) {
        this.cultureItems = filterItems;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final CultureItem cultureItem = cultureItems.get(position);

//        String date = new SimpleDateFormat("MM월 dd일").format(memoItem.getDate());

        holder.name.setText(cultureItem.getName());
        holder.date.setText(cultureItem.getDate());
        holder.area.setText(cultureItem.getArea());

        if(!cultureItem.isFavorite())
            holder.btn_favorite.setImageDrawable(context.getResources().getDrawable(R.drawable.star_off));
        else
            holder.btn_favorite.setImageDrawable(context.getResources().getDrawable(R.drawable.star_on));

        holder.btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 즐겨찾기 버튼을 클릭했을때 on, off 에 따라 렘에서 추가 삭제 해줘야함
                if(cultureItem.isFavorite()) {
                    // 현재 즐겨찾기 돼있는 상태 이므로 눌렀을때 삭제해야함
                    DBHelper.getInstance().deleteCultureFavorite(cultureItem.getName());
                    cultureItems.get(position).setFavorite(false);
                    cultureItem.setFavorite(false);
                    holder.btn_favorite.setImageDrawable(context.getResources().getDrawable(R.drawable.star_off));
                    Toast.makeText(context, "즐겨찾기 해제되었습니다", Toast.LENGTH_SHORT).show();
                }
                else{
                    DBHelper.getInstance().insertCultureFavorite(cultureItem.getName());
                    cultureItems.get(position).setFavorite(true);
                    cultureItem.setFavorite(true);
                    holder.btn_favorite.setImageDrawable(context.getResources().getDrawable(R.drawable.star_on));
                    Toast.makeText(context, "즐겨찾기에 추가되었습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);

        holder.itemView.setAnimation(animation);
        holder.itemView.startAnimation(animation);
        lastPosition = position;
    }




    @Override
    public CultureItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        // 새로운 뷰 만들기
        View view = LayoutInflater.from(context).inflate(R.layout.item_culture, parent, false);

        // 뷰사이즈 세팅, 마진, 패딩 등등 세팅하는 곳
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    public void updateList(List<CultureItem> list){
        cultureItems = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cultureItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // 뷰홀더
    class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        TextView name;
        TextView date;
        TextView area;
        ImageButton btn_favorite;

        public ViewHolder(View itemView){
            super(itemView);
            itemView.setOnClickListener(this);
            name = itemView.findViewById(R.id.item_culture_textView_name);
            date = itemView.findViewById(R.id.item_culture_textView_date);
            area = itemView.findViewById(R.id.item_culture_textView_area);
            btn_favorite = itemView.findViewById(R.id.item_culture_button_favorite);
        }


        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, MapActivity.class);
            intent.putExtra("ContentsName", name.getText().toString());
            intent.putExtra("Culture", true);
            context.startActivity(intent);
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }
}
