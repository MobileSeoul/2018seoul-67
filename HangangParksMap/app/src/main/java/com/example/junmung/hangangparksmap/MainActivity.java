package com.example.junmung.hangangparksmap;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import com.example.junmung.hangangparksmap.ARGuide.ARGuideActivity;
import com.example.junmung.hangangparksmap.ARGuide.Point;
import com.example.junmung.hangangparksmap.Culture.CultureActivity;
import com.example.junmung.hangangparksmap.CulturePointPOJO.CulturePojo;
import com.example.junmung.hangangparksmap.CulturePointPOJO.Mgishangang;
import com.example.junmung.hangangparksmap.CulturePointPOJO.Row;
import com.example.junmung.hangangparksmap.DataBase.DBHelper;
import com.example.junmung.hangangparksmap.Map.MapActivity;
import com.example.junmung.hangangparksmap.RetrofitUtil.ApiService;
import com.example.junmung.hangangparksmap.RetrofitUtil.RetrofitClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import info.hoang8f.widget.FButton;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private FButton btn_culture, btn_map, btn_gate;
    private DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUI();
        permissionCheck();
        DataBaseInit();
        firstRunCheck();
        sharingCheck();

//        dbHelper.deleteFavoriteAll();
    }

    private void getUI(){
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorRiver));

        btn_culture = findViewById(R.id.activity_main_button_culture);
        btn_culture.setOnClickListener(btnClickListener);
        btn_map = findViewById(R.id.activity_main_button_map);
        btn_map.setOnClickListener(btnClickListener);
        btn_gate = findViewById(R.id.activity_main_button_gate);
        btn_gate.setOnClickListener(btnClickListener);
        btn_culture.setButtonColor(getResources().getColor(R.color.colorRiver));
        btn_culture.setCornerRadius(25);
        btn_map.setButtonColor(getResources().getColor(R.color.colorSun));
        btn_map.setCornerRadius(25);
        btn_gate.setButtonColor(getResources().getColor(R.color.colorGrass));
        btn_map.setCornerRadius(25);
    }

    private void firstRunCheck() {
        SharedPreferences prefs = getSharedPreferences("MyData", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);
        if (isFirstRun) {
            prefs.edit();
            prefs.edit().putBoolean("isFirstRun", false).apply();
            prefs.edit().commit();
            getCultureInfos();
        }
    }


    private Button.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.activity_main_button_culture:
                    startActivity(new Intent(MainActivity.this, CultureActivity.class));
                    break;

                case R.id.activity_main_button_map:
                    startActivity(new Intent(MainActivity.this, MapActivity.class));
                    break;

                case R.id.activity_main_button_gate:
                    Intent exitIntent = new Intent(MainActivity.this, MapActivity.class);
                    exitIntent.putExtra("Exiting", true);
                    startActivity(exitIntent);
                    break;
            }
        }
    };



    private void DataBaseInit() {
        Realm.init(getBaseContext());
        dbHelper = DBHelper.getInstance();
    }

    // 공유하기 기능에 의해 실행됐는지 확인하기
    private void sharingCheck() {
        Intent sharingIntent= getIntent();
        if(sharingIntent.getAction() == Intent.ACTION_VIEW){
            Uri uri = sharingIntent.getData();

            Intent mapIntent = new Intent(MainActivity.this, MapActivity.class);
            mapIntent.setData(uri);
            mapIntent.putExtra("Sharing", true);
            startActivity(mapIntent);
        }
    }



    // 문화정보 가져오기
    private void getCultureInfos(){
        Retrofit retrofit = RetrofitClient.getCultureCilent();
        ApiService apiService = retrofit.create(ApiService.class);

        Call<CulturePojo> call = apiService.getCulturePoints();

        call.enqueue(new Callback<CulturePojo>() {
            @Override
            public void onResponse(Call<CulturePojo> call, Response<CulturePojo> response) {
                if(response.isSuccessful()){
                    CulturePojo culturePoint = response.body();
                    Mgishangang mgishangang = culturePoint.getMgishangang();
                    ArrayList<Row> rows = (ArrayList<Row>) mgishangang.getRow();

                    dbHelper.insertCultureInfos(rows);
                }
            }

            @Override
            public void onFailure(Call<CulturePojo> call, Throwable t) {
                Log.d("Retrofit Fail", "실패");
                t.printStackTrace();
            }
        });
    }


    // 권한체크
    private void permissionCheck(){
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("앱을 원활히 이용하기 위해선 권한이 필요합니다")
                .setDeniedMessage("거부하시면 앱의 사용이 어렵습니다")
                .setPermissions(
                        Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION
                )
                .check();
    }

    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
//            Toast.makeText(getApplicationContext(), "권한허가", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한거부\n"+deniedPermissions.toString(), Toast.LENGTH_SHORT).show();

        }
    };

}
