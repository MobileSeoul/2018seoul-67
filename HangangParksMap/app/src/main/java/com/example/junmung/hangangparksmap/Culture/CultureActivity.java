package com.example.junmung.hangangparksmap.Culture;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.junmung.hangangparksmap.CulturePoint;
import com.example.junmung.hangangparksmap.DataBase.DBHelper;
import com.example.junmung.hangangparksmap.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CultureActivity extends AppCompatActivity {
    // 행사목록 아이템
    // 아이템내에 즐겨찾기버튼, 기간 지난건 보여주지않기,
    // 최근일자 순서로 보여주고
    // 필터에 즐겨찾기를 넣는게 나을듯 - 액션바에서 컨트롤 하기 위함

    // 플로팅버튼 추가 ?

    // 검색기능 추가해야함

    private RecyclerView recyclerView;
    private CultureItemAdapter itemAdapter;

    private Spinner spinner;
    private DBHelper dbHelper = DBHelper.getInstance();
    private List<CultureItem> items = new ArrayList();
    private List<CultureItem> tempItems = new ArrayList<>();

    private InputMethodManager inputManager;
    private EditText edit_search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_culture);
        setToolBar();
        getID_SetListener();

        List<CulturePoint> points = dbHelper.getCultureItems();

        for(CulturePoint point : points) {
            // 즐겨찾기에 존재하지않으면 단순추가
            if(!dbHelper.isExistCultureFavorite(point.getName()))
                items.add(new CultureItem(point));
            else
                items.add(new CultureItem(point, true));
        }


        itemAdapter = new CultureItemAdapter(items);
        recyclerView.setAdapter(itemAdapter);

    }

    private void setToolBar(){
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorSky));
        Toolbar toolbar = findViewById(R.id.activity_culture_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    private void getID_SetListener() {
        recyclerView = findViewById(R.id.activity_culture_recyclerView);

        spinner = findViewById(R.id.activity_culture_spinner);
        spinner.setOnItemSelectedListener(spinnerItemClickListener);
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, createAreas());
        spinner.setAdapter(areaAdapter);

        edit_search = findViewById(R.id.activity_culture_editText_search);
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(edit_search.getText().toString());
            }
        });
        inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(edit_search.getWindowToken(), 0);
    }

    private void filter(String text) {
        tempItems.clear();
        if(text.length() == 0)
            tempItems.addAll(items);
        else{
            for(CultureItem item : items){
                if(item.getName().contains(text))
                    tempItems.add(item);
            }
        }

        itemAdapter.updateList(tempItems);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

            case R.id.menu_culture_favorite:
                // 즐겨찾기

                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    private AdapterView.OnItemSelectedListener spinnerItemClickListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selectedArea = parent.getItemAtPosition(position).toString();
            // 새로운 items 를 만든다.
            if(selectedArea.equals("지역선택")){
                itemAdapter.updateList(items);
            }
            else if(selectedArea.equals("즐겨찾기")){
                tempItems.clear();
                for(CultureItem point : items){
                    if(point.isFavorite())
                        tempItems.add(point);
                }
                itemAdapter.updateList(tempItems);
            }
            else{
                tempItems.clear();
                for(CultureItem point : items){
                    if(point.getArea().contains(selectedArea))
                        tempItems.add(point);
                }
                itemAdapter.updateList(tempItems);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    private ArrayList<String> createAreas(){
        ArrayList<String> list = new ArrayList<>();
        String[] areas = {"지역선택", "즐겨찾기", "강서", "광나루", "난지", "뚝섬", "망원", "반포", "양화", "여의도", "이촌", "잠실", "잠원"};
        list.addAll(Arrays.asList(areas));

        return list;
    }
}
