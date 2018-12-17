package com.example.junmung.hangangparksmap.Map;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.junmung.hangangparksmap.ARGuide.ARGuideActivity;
import com.example.junmung.hangangparksmap.ARGuide.Point;
import com.example.junmung.hangangparksmap.CommonPoint;
import com.example.junmung.hangangparksmap.CulturePoint;
import com.example.junmung.hangangparksmap.DataBase.DBHelper;
import com.example.junmung.hangangparksmap.FavoritePoint;
import com.example.junmung.hangangparksmap.Map.Dialog.FilterDialogFragment;
import com.example.junmung.hangangparksmap.R;
import com.example.junmung.hangangparksmap.RetrofitUtil.ApiService;
import com.example.junmung.hangangparksmap.RetrofitUtil.RetrofitClient;
import com.example.junmung.hangangparksmap.SearchPointPOJO.Document;
import com.example.junmung.hangangparksmap.SearchPointPOJO.SearchPoint;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.LocationTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;
import com.mahc.custombottomsheetbehavior.BottomSheetBehaviorGoogleMapsLike;
import com.mahc.custombottomsheetbehavior.MergedAppBarLayout;
import com.mahc.custombottomsheetbehavior.MergedAppBarLayoutBehavior;

import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MapActivity extends AppCompatActivity implements FilterDialogFragment.DialogDismissListener{
    private int SEARCH_RADIUS = 500;
    private DBHelper dbHelper = DBHelper.getInstance();

    private AnimatingLayout fabContainer;
    private FloatingActionButton fab_currentLocation, fab_filter, fab_ARGuide;

    private ViewGroup mapViewContainer;
    private MapView mapView;
    private BottomSheetBehaviorGoogleMapsLike bottomSheetBehavior;
    private MergedAppBarLayoutBehavior mergedAppBarLayoutBehavior;

    // Bottom Sheet Header
    private RelativeLayout layout_bottomHeader;
    private TextView text_pointName, text_pointAddress, text_pointDistance;

    private NestedWebView webView;
    private View bottomSheet;

    private Point currentPoint;
    private MapPOIItem selectedPOIItem;

    private Toolbar toolbar;

    private GoogleApiClient mGoogleApiClient;

    private ImageButton btn_favorite, btn_sharing;

    private boolean isSharing = false;
    private boolean isExiting = false;
    private boolean isCulture = false;

    private String contentsName;


    public interface POICallback<T> {
        void onNotFound();
        void onError(Error error);
        void onSuccess(T receivedData);
        void onFailure(Error error);
    }

    public interface ExitCallback<T> {
        void onNotFound();
        void onComplete(T receivedData);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        intentCheck();
        initLocationService();
        getID_SetListener();
    }

    private void intentCheck() {
        Intent intent = getIntent();

        boolean isSharingIntent = intent.getBooleanExtra("Sharing", false);
        boolean isExitingIntent = intent.getBooleanExtra("Exiting", false);
        boolean isCultureIntent = intent.getBooleanExtra("Culture", false);

        if(isSharingIntent){
            Uri uri = intent.getData();
            String name = uri.getQueryParameter("name").toString();
            String address = uri.getQueryParameter("address").toString();
            double latitude = Double.parseDouble(uri.getQueryParameter("latitude").toString());
            double longitude = Double.parseDouble(uri.getQueryParameter("longitude").toString());

            CommonPoint point = new CommonPoint(name, latitude, longitude, address);
            selectedPOIItem = new MapPOIItem();
            selectedPOIItem.setUserObject(point);

            isSharing = true;

            Log.d("intentCheck()", ""+isSharing);
        }
        else if(isExitingIntent)
            isExiting = true;
        else if(isCultureIntent) {
            contentsName = intent.getStringExtra("ContentsName");
            isCulture = true;
        }
    }

    private void getID_SetListener(){
        mapViewContainer = findViewById(R.id.activity_Map_mapView);
        mapView = new MapView(MapActivity.this);
        mapView.setMapViewEventListener(mapViewEventListener);
        mapView.setCurrentLocationEventListener(mapLocationListener);
        mapView.setPOIItemEventListener(mapPOIEventListener);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
        mapView.setMapTilePersistentCacheEnabled(false);

        mapViewContainer.addView(mapView);


        // Floating Button
        fabContainer = findViewById(R.id.activity_Map_fabContainer);
        fab_currentLocation = findViewById(R.id.activity_Map_fab_currentLocation);
        fab_currentLocation.setOnClickListener(fabClickListener);
        fab_filter = findViewById(R.id.activity_Map_fab_filter);
        fab_filter.setOnClickListener(fabClickListener);
        fab_ARGuide = findViewById(R.id.activity_Map_fab_ARGuide);
        fab_ARGuide.setOnClickListener(fabClickListener);


        // 최상위 뷰에서 스크롤뷰 가져오기
        CoordinatorLayout rootView = findViewById(R.id.activity_Map_rootView);
        bottomSheet = rootView.findViewById(R.id.activity_Map_bottomSheet);
        bottomSheetBehavior = BottomSheetBehaviorGoogleMapsLike.from(bottomSheet);
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback);
        bottomSheetBehavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_HIDDEN);

        // Button
        btn_favorite = bottomSheet.findViewById(R.id.activity_Map_bottomSheet_Button_Favorite);
        btn_favorite.setOnClickListener(btnClickListener);
        btn_sharing = bottomSheet.findViewById(R.id.activity_Map_bottomSheet_Button_Sharing);
        btn_sharing.setOnClickListener(btnClickListener);

        // 바텀시트를 올렸을때 내려오는 AppBar
        MergedAppBarLayout mergedAppBarLayout = findViewById(R.id.activity_Map_mergedAppbarLayout);
        mergedAppBarLayoutBehavior = MergedAppBarLayoutBehavior.from(mergedAppBarLayout);


        // BottomSheet Header
        layout_bottomHeader = bottomSheet.findViewById(R.id.activity_Map_bottomSheet_header);
        text_pointName = bottomSheet.findViewById(R.id.activity_Map_textView_pointName);
        text_pointAddress = bottomSheet.findViewById(R.id.activity_Map_textView_pointAddress);
        text_pointDistance = bottomSheet.findViewById(R.id.activity_Map_textView_pointDistance);

        setWebView();

        setSearchBar();

    }

    // 검색창 툴바세팅
    private void setSearchBar(){
        toolbar = findViewById(R.id.activity_Map_toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SearchView searchView = findViewById(R.id.activity_map_searchView);
        EditText searchBox = searchView.findViewById (android.support.v7.appcompat.R.id.search_src_text);
        searchBox.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        searchBox.setHintTextColor(getResources().getColor(R.color.colorHalfInvisibleBlack));
        searchBox.setTextColor(getResources().getColor(R.color.colorBlack));
        searchBox.setBackground(getDrawable(R.drawable.rounded_edittext));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getPOIItemsByApi(query, poiCallback);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }


    // BottomSheet 상태가 바뀔때마다 호출되는 콜백 함수
    private BottomSheetBehaviorGoogleMapsLike.BottomSheetCallback bottomSheetCallback =
            new BottomSheetBehaviorGoogleMapsLike.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            switch (newState){
                // 살짝 보임
                case BottomSheetBehaviorGoogleMapsLike.STATE_COLLAPSED:
                    bottomHeaderColorChange(false);
                    break;

                // 드래그
                case BottomSheetBehaviorGoogleMapsLike.STATE_DRAGGING:
                    bottomHeaderColorChange(true);
                    break;


                // 숨김
                case BottomSheetBehaviorGoogleMapsLike.STATE_HIDDEN:
                    // 맵뷰에서 POI 가 선택된 상태라면 상태를 해제한다.
                    break;

                // 전체화면
                case BottomSheetBehaviorGoogleMapsLike.STATE_EXPANDED:
                    bottomSheet.setScrollY(100);

                    break;

                case BottomSheetBehaviorGoogleMapsLike.STATE_ANCHOR_POINT:

                    break;

            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            // 1 STATE_EXPANDED
            // 0 peekHeight
            // -1 STATE_HIDDEN
            if(slideOffset > 0.7f)
                fabContainer.hide();
            else
                fabContainer.show();
        }
    };

    // BottomSheet 클릭할때 마다 색 바꿔주는 함수
    private void bottomHeaderColorChange(boolean isDragging) {
        if(isDragging){
            layout_bottomHeader.setBackgroundColor(getResources().getColor(R.color.colorRiver));
            text_pointAddress.setTextColor(getResources().getColor(R.color.colorWhite));
            text_pointName.setTextColor(getResources().getColor(R.color.colorWhite));
            fab_ARGuide.getBackground().mutate().setTint(getResources().getColor(R.color.colorWhite));
            fab_ARGuide.getDrawable().mutate().setTint(getResources().getColor(R.color.colorRiver));
        }else{
            layout_bottomHeader.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            text_pointAddress.setTextColor(getResources().getColor(R.color.colorBlack));
            text_pointName.setTextColor(getResources().getColor(R.color.colorBlack));
            fab_ARGuide.getBackground().mutate().setTint(getResources().getColor(R.color.colorRiver));
            fab_ARGuide.getDrawable().mutate().setTint(getResources().getColor(R.color.colorWhite));
        }
    }


    // 플로팅버튼 클릭리스너
    private FloatingActionButton.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.activity_Map_fab_currentLocation:
                    // 현재위치를 받아오고 맵뷰를 현재위치로 바꾼다.
                    fab_currentLocation.getDrawable().mutate().setTint(getResources().getColor(R.color.colorRiver));
                    moveMapCamera(currentPoint.latitude, currentPoint.longitude, 200, 100);
                    break;

                case R.id.activity_Map_fab_filter:
                    // 애니메이션 처리된 FilterDialog 띄우기
                    FilterDialogFragment dialogFragment = new FilterDialogFragment();
                    dialogFragment.show(getSupportFragmentManager(), dialogFragment.getTag());
                    break;

                case R.id.activity_Map_fab_ARGuide:
                    // 현재 보여지고 있는 POI Item 에서 좌표값을 얻어낸다
                    // Intent 에 값을 넣은 후 ARGuide 액티비티를 실행
                    Intent intent = new Intent(MapActivity.this, ARGuideActivity.class);
                    removeMarkers();

                    if( selectedPOIItem != null ){
                        Point point = (Point)selectedPOIItem.getUserObject();
                        intent.putExtra("Destination", selectedPOIItem.getItemName());
                        intent.putExtra("Latitude", point.latitude);
                        intent.putExtra("Longitude", point.longitude);
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(getApplicationContext(), "선택된 장소가 없습니다", Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };

    private Button.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){

                // 즐겨찾기 버튼 클릭
                case R.id.activity_Map_bottomSheet_Button_Favorite:
                    String itemName = selectedPOIItem.getItemName();

                    if(dbHelper.isExistFavorite(itemName)){
                        btn_favorite.setImageDrawable(getResources().getDrawable(R.drawable.star_off));
                        dbHelper.deleteFavoriteItem(itemName);
                        Toast.makeText(getApplicationContext(),"즐겨찾기 해제되었습니다", Toast.LENGTH_SHORT).show();
                    }
                    else if(itemName.equals("선택위치")){
                        showFavoriteRegisterDialog();
                    }
                    else{
                        Object object = selectedPOIItem.getUserObject();
                        if(object instanceof CommonPoint){
                            dbHelper.insertFavoriteInfo((CommonPoint)object);
                            Toast.makeText(getApplicationContext(),"즐겨찾기 추가되었습니다", Toast.LENGTH_SHORT).show();
                            btn_favorite.setImageDrawable(getResources().getDrawable(R.drawable.star_on));
                        }
                        else{
                            if(dbHelper.insertFavoriteInfo((CulturePoint)object) == false)
                                Toast.makeText(getApplicationContext(), "잠시 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                            else {
                                Toast.makeText(getApplicationContext(), "즐겨찾기 추가되었습니다", Toast.LENGTH_SHORT).show();
                                btn_favorite.setImageDrawable(getResources().getDrawable(R.drawable.star_on));
                            }
                        }
                    }
                    break;

                    // 공유하기 버튼 클릭
                case R.id.activity_Map_bottomSheet_Button_Sharing:
                    Object object = selectedPOIItem.getUserObject();
                    Point point = (Point)object;
                    String address = getAddressFromObject(object);
                    startKakaoLink(point.getName(), address, point.latitude, point.longitude);
                    break;
            }
        }
    };


    // 카카오톡 공유하기 기능
    private void startKakaoLink(String name, String address, double latitude, double longitude) {
        LocationTemplate params = LocationTemplate.newBuilder(address,
                ContentObject.newBuilder(name,
                        "http://www.kakaocorp.com/images/logo/og_daumkakao_151001.png",
                        LinkObject.newBuilder()
                                .setWebUrl("https://developers.kakao.com")
                                .setMobileWebUrl("https://developers.kakao.com")
                                .setAndroidExecutionParams("name="+name+"&address="+address+"&latitude="+latitude+"&longitude="+longitude)
                                .build())
                        .build())
                .addButton(new ButtonObject("앱에서 확인", LinkObject.newBuilder()
                        .setWebUrl("https://developers.kakao.com")
                        .setMobileWebUrl("https://developers.kakao.com")
                        .setAndroidExecutionParams("name="+name+"&address="+address+"&latitude="+latitude+"&longitude="+longitude)
                        .build()))
                .setAddressTitle(name)
                .build();

        KakaoLinkService.getInstance().sendDefault(this, params, new ResponseCallback<KakaoLinkResponse>() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e(errorResult.toString());
            }

            @Override
            public void onSuccess(KakaoLinkResponse result) {

            }
        });
    }

    private String getAddressFromObject(Object object){
        if(object instanceof CommonPoint)
            return ((CommonPoint) object).getAddress();
        else if(object instanceof CulturePoint)
            return ((CulturePoint)object).getAddress();
        else
            return ((FavoritePoint)object).getAddress();
    }


    // 즐겨찾기 등록 다이얼로그
    private void showFavoriteRegisterDialog(){
        final EditText edittext = new EditText(this);
        edittext.setPadding(16, 8,  16, 8);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("즐겨찾기 등록");
        builder.setView(edittext);
        builder.setPositiveButton("등록",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(edittext.getText() != null){
                            String locationName = edittext.getText().toString();

                            CommonPoint point = (CommonPoint)selectedPOIItem.getUserObject();
                            point.setName(locationName);

                            selectedPOIItem.setItemName(locationName);
                            selectedPOIItem.setUserObject(point);

                            dbHelper.insertFavoriteInfo(point);
                            btn_favorite.setImageDrawable(getResources().getDrawable(android.R.drawable.btn_star_big_on));
                            Toast.makeText(getApplicationContext(),"즐겨찾기 추가되었습니다", Toast.LENGTH_SHORT).show();
                        }else
                            Toast.makeText(getApplicationContext(),"장소명을 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }


    // WebView Setting
    private void setWebView() {
        webView = findViewById(R.id.activity_Map_bottomSheet_WebView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setAllowContentAccess(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDomStorageEnabled(true);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.clearHistory();
        webView.clearCache(true);
    }


    @Override
    public void onBackPressed() {
        // 뒤로가기 눌렀을때 bottomSheet 접기
        // POI Item 선택 해제
        // Bottom Sheet Scroll 맨위로 올리기
        // Bottom Sheet state change.
        int state = bottomSheetBehavior.getState();

        if (state == BottomSheetBehaviorGoogleMapsLike.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_COLLAPSED);
            bottomSheet.setScrollY(0);
        }
        else if(state == BottomSheetBehaviorGoogleMapsLike.STATE_COLLAPSED){
            bottomSheetBehavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_HIDDEN);
        }
        else if(state == BottomSheetBehaviorGoogleMapsLike.STATE_ANCHOR_POINT){
            bottomSheetBehavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_HIDDEN);
        }

        else {
            super.onBackPressed();
        }
    }


    /**
     *  다음 맵 관련
     */

    // View Event 리스너
    private MapView.MapViewEventListener mapViewEventListener = new MapView.MapViewEventListener() {
        @Override
        public void onMapViewInitialized(MapView mapView) {
            // intent(카톡공유)가 없을시엔 GoogleApi 를 사용해서 현재위치 얻어오기

            if(isSharing){
                CommonPoint point = (CommonPoint)selectedPOIItem.getUserObject();
                selectedPOIItem.setItemName(point.getName());
                selectedPOIItem.setMapPoint(MapPoint.mapPointWithGeoCoord(point.latitude, point.longitude));
                selectedPOIItem.setMarkerType(MapPOIItem.MarkerType.BluePin);
                selectedPOIItem.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                mapView.addPOIItem(selectedPOIItem);

                mapView.selectPOIItem(selectedPOIItem, true);
                mapPOIEventListener.onPOIItemSelected(mapView, selectedPOIItem);
                moveMapCamera(point.latitude, point.longitude, SEARCH_RADIUS, 100);
            }

        }

        @Override
        public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewZoomLevelChanged(MapView mapView, int i) {

        }

        @Override
        public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
            // 바텀시트 가리기
            if(bottomSheetBehavior.getState() != BottomSheetBehaviorGoogleMapsLike.STATE_HIDDEN)
                onBackPressed();
        }

        @Override
        public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewLongPressed(MapView mapView, MapPoint point) {
            // 마커가 생성되며 지도 말풍선이 생성된다
            // 누르게되면 즐겨찾기 할수있게 기능 넣자.
            // 좌표에서 주소가져오는 함수 써야할듯
            removeMarkers();

            double pointLat = point.getMapPointGeoCoord().latitude;
            double pointLon = point.getMapPointGeoCoord().longitude;
            int distance = Point.distance(new LatLng(currentPoint.latitude, currentPoint.longitude), new LatLng(pointLat, pointLon));

            CommonPoint commonPoint = new CommonPoint("선택위치", pointLat, pointLon, "");
            commonPoint.setDistance(distance);

            MapPOIItem poiItem = addMarker(commonPoint);
            poiItem.setItemName("선택위치");
            poiItem.setUserObject(commonPoint);

            mapView.addPOIItem(poiItem);
            mapView.selectPOIItem(poiItem, true);
            mapPOIEventListener.onPOIItemSelected(mapView, poiItem);


            // 1. 찾기전 바텀시트를 collapse 상태로 만든다.
            bottomSheetBehavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_COLLAPSED);

            new MapReverseGeoCoder(ApiService.KAKAO_APP_KEY, point, new MapReverseGeoCoder.ReverseGeoCodingResultListener(){
                        @Override
                        public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String address) {
                            text_pointName.setText(address);
                            CommonPoint commonPoint_ = (CommonPoint)selectedPOIItem.getUserObject();
                            commonPoint_.setAddress(address);
                            selectedPOIItem.setUserObject(commonPoint_);
                            mergedAppBarLayoutBehavior.setToolbarTitle(address);
                        }

                        @Override
                        public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {
                            Toast.makeText(getApplicationContext(), "주소를 찾는데 실패했습니다", Toast.LENGTH_SHORT).show();
                        }
                    }, MapActivity.this
            ).startFindingAddress();
        }

        @Override
        public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
            fab_currentLocation.getDrawable().mutate().setTint(getResources().getColor(R.color.colorBlack));
        }

        @Override
        public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

        }

        @Override
        public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

        }
    };

    // Location 리스너
    private MapView.CurrentLocationEventListener mapLocationListener = new MapView.CurrentLocationEventListener() {
        @Override
        public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        }

        @Override
        public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

        }

        @Override
        public void onCurrentLocationUpdateFailed(MapView mapView) {

        }

        @Override
        public void onCurrentLocationUpdateCancelled(MapView mapView) {

        }
    };

    // POI Event 리스너
    private MapView.POIItemEventListener mapPOIEventListener = new MapView.POIItemEventListener() {
        @Override
        public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
            // MapView 에 표시 돼 있는 마커를 선택했을 경우
            // 마커 위쪽에 말풍선으로 이름이 나오며 마커 색이 변한다.
            // 바텀시트에는 해당하는 마커의 이름과 주소가 나오고
            // 올렸을경우 webView 가 표시된다.

            selectedPOIItem = mapPOIItem;
            Object object = mapPOIItem.getUserObject();
            String pointName = mapPOIItem.getItemName();
            setBottomSheetContents(object, pointName);
        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

        }

        @Override
        public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

        }

        @Override
        public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

        }
    };


    // 바텀시트의 내용물을 바꾼다.
    private void setBottomSheetContents(Object object, String locationName) {
        String pointAddress, distance;
        boolean isFavoritePoint = dbHelper.isExistFavorite(locationName);

        if(object instanceof CommonPoint) {
            final CommonPoint point = (CommonPoint)object;

            pointAddress = point.getAddress();
            distance = String.format("%d", currentPoint.distanceTo(point));

            if(point.hasUrl()) {
                webView.setVisibility(View.VISIBLE);
                webView.loadUrl(point.getUrl());
            }
            else
                webView.setVisibility(View.INVISIBLE);
        }
        else if(object instanceof CulturePoint){
            CulturePoint point = (CulturePoint)object;

            pointAddress = point.getAddress();
            distance = String.format("%d", currentPoint.distanceTo(point));

            webView.setVisibility(View.INVISIBLE);
            webView.setWebViewClient(new CultureWebViewClient(selectedPOIItem, locationName));
            webView.loadUrl("http://hangang.seoul.go.kr/project2018/search?keyword="+ locationName +"&search_type=title_content");


        }
        else{
            FavoritePoint point = (FavoritePoint)object;

            pointAddress = point.getAddress();
            distance = String.format("%d", currentPoint.distanceTo(point));

            if(point.hasUrl()) {
                webView.setVisibility(View.VISIBLE);
                webView.loadUrl(point.getUrl());
            }
            else
                webView.setVisibility(View.INVISIBLE);
        }

        if(isFavoritePoint)
            btn_favorite.setImageDrawable(getResources().getDrawable(R.drawable.star_on));
        else
            btn_favorite.setImageDrawable(getResources().getDrawable(R.drawable.star_off));


        text_pointName.setText(locationName);
        text_pointAddress.setText(pointAddress);
        text_pointDistance.setText(distance+" m");

        mergedAppBarLayoutBehavior.setToolbarTitle(locationName);
        fab_ARGuide.show();

        bottomSheetBehavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_COLLAPSED);
    }


    // 모든 마커를 제거한다.
    private void removeMarkers(){
        mapView.removeAllPOIItems();
    }

    // 한개의 마커 추가
    private MapPOIItem addMarker(Point point){
        MapPOIItem poiItem = new MapPOIItem();
        poiItem.setItemName(point.getName());
        poiItem.setMapPoint(MapPoint.mapPointWithGeoCoord(point.latitude, point.longitude));
        poiItem.setMarkerType(MapPOIItem.MarkerType.BluePin);
        poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(poiItem);

        return poiItem;
    }

    // 입력받은 위치로 카메라를 이동시킨다.
    private void moveMapCamera(double latitude, double longitude, float range, int padding) {
        mapView.moveCamera(CameraUpdateFactory.newMapPointAndDiameter(
                MapPoint.mapPointWithGeoCoord(latitude, longitude), range, padding));
    }





    /**
     *  Filter Dialog Dismiss CallBack
     *
     *  다이얼로그가 닫히게 되면 name, point 일 경우로 나눠진다.
     *
     *  name 일 경우 RestApi 를 사용해서 키워드 검색이 이루어지고, 얻은 목록들을 지도에 마커로 표시해준다.
     *
     *  point 일 경우 지도에 마커표시, point 의 이름, 주소만을 바텀시트에 표시한다.
     *
     */

    @Override
    public void onFilterDialogDismiss(FavoritePoint point) {
        removeMarkers();
        MapPOIItem poiItem = addMarker(point);
        poiItem.setUserObject(point);
        mapView.selectPOIItem(poiItem, true);
        mapPOIEventListener.onPOIItemSelected(mapView, poiItem);
        moveMapCamera(point.latitude, point.longitude, SEARCH_RADIUS, 100);
    }

    @Override
    public void onFilterDialogDismiss(String keyword) {
        if(keyword.equals("행사장"))
            getPOIItemsByDB(poiCallback);
        else
            getPOIItemsByApi(keyword, poiCallback);
    }

    // POI CallBack
    private POICallback<MapPOIItem[]> poiCallback = new POICallback<MapPOIItem[]>() {
        @Override
        public void onNotFound() {
            removeMarkers();
            bottomSheetBehavior.setState(BottomSheetBehaviorGoogleMapsLike.STATE_HIDDEN);
            Toast.makeText(getApplicationContext(), "근처에 해당하는 장소가 없습니다.",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Error error) {

        }

        @Override
        public void onSuccess(MapPOIItem[] receivedData) {
            // 이부분에 지도로 마커 표시해주기
            removeMarkers();
            mapView.addPOIItems(receivedData);
            MapPoint.GeoCoordinate centerPoint = mapView.getMapCenterPoint().getMapPointGeoCoord();
            moveMapCamera(centerPoint.latitude, centerPoint.longitude, SEARCH_RADIUS, 100);
        }

        @Override
        public void onFailure(Error error) {
            Log.d("MapActivity Keyword", error.getLocalizedMessage());

        }
    };

    // DB 에서 해당하는 행사장 하나를 얻어온다.
    private void getPOIItemByDB(POICallback<MapPOIItem[]> poiCallback, String contentsName){
        DBHelper dbHelper = DBHelper.getInstance();
        CulturePoint culturePoint = dbHelper.getCultureItem(contentsName);

        MapPoint.GeoCoordinate centerPoint = mapView.getMapCenterPoint().getMapPointGeoCoord();
        LatLng mapLocation = new LatLng(centerPoint.latitude, centerPoint.longitude);

        ArrayList<MapPOIItem> poiItems = new ArrayList<>();
        LatLng itemLocation = new LatLng(culturePoint.latitude, culturePoint.longitude);

        int distance = Point.distance(mapLocation, itemLocation);
        MapPOIItem poiItem = addMarker(culturePoint);
        culturePoint.setDistance(distance);
        poiItem.setUserObject(culturePoint);
        poiItems.add(poiItem);
        poiCallback.onSuccess(poiItems.toArray(new MapPOIItem[poiItems.size()]));

        mapView.selectPOIItem(poiItem, true);
        mapPOIEventListener.onPOIItemSelected(mapView, poiItem);
        moveMapCamera(culturePoint.latitude, culturePoint.longitude, SEARCH_RADIUS, 100);
    }

    // DB 에서 행사장을 얻어와 POI Items 를 구한다.
    private void getPOIItemsByDB(POICallback<MapPOIItem[]> poiCallback) {
        DBHelper dbHelper = DBHelper.getInstance();
        ArrayList<CulturePoint> culturePoints = dbHelper.getCultureItems();
        int size = culturePoints.size();

        MapPoint.GeoCoordinate centerPoint = mapView.getMapCenterPoint().getMapPointGeoCoord();
        LatLng mapLocation = new LatLng(centerPoint.latitude, centerPoint.longitude);

        ArrayList<MapPOIItem> poiItems = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            CulturePoint culturePoint = culturePoints.get(i);
            LatLng itemLocation = new LatLng(culturePoint.latitude, culturePoint.longitude);

            int distance = Point.distance(mapLocation, itemLocation);
            if (distance < SEARCH_RADIUS) {
                MapPOIItem poiItem = addMarker(culturePoint);
                culturePoint.setDistance(distance);
                poiItem.setUserObject(culturePoint);
                poiItems.add(poiItem);
            }
        }

        if(poiItems.size() == 0)
            poiCallback.onNotFound();
        else
            poiCallback.onSuccess(poiItems.toArray(new MapPOIItem[poiItems.size()]));

    }

    // Keyword RestApi 를 사용해 PoiItems 를 가져온다.
    private void getPOIItemsByApi(String keyword, final POICallback<MapPOIItem[]> poiCallback){
        Retrofit retrofit = RetrofitClient.getSearchClient();
        ApiService apiService = retrofit.create(ApiService.class);


        // 현재 화면에서 중심점을 기준 Pixel 값을 기준으로 MapPoint 를 생성해서 대입한다.
        MapPoint.GeoCoordinate centerPoint = mapView.getMapCenterPoint().getMapPointGeoCoord();

        Call<SearchPoint> call = apiService.getSearchPoints("KakaoAK " +ApiService.KAKAO_REST_KEY,
                keyword, centerPoint.longitude, centerPoint.latitude, SEARCH_RADIUS);

        call.enqueue(new Callback<SearchPoint>() {
            @Override
            public void onResponse(Call<SearchPoint> call, Response<SearchPoint> response) {
                if(response.isSuccessful()){
                    SearchPoint searchPoint = response.body();
                    ArrayList<Document> documents = (ArrayList<Document>) searchPoint.getDocuments();
                    int size = documents.size();

                    if(size == 0)
                        poiCallback.onNotFound();
                    else{
                        MapPOIItem[] poiItems = new MapPOIItem[size];

                        for(int i = 0; i < size; i++) {
                            Document document = documents.get(i);
                            Log.d("getPOIItemsByApi", ""+document.getDistance());

                            CommonPoint commonPoint = new CommonPoint(document);
                            poiItems[i] = addMarker(commonPoint);
                            poiItems[i].setUserObject(commonPoint);
                        }
                        poiCallback.onSuccess(poiItems);
                    }
                }
                else
                    poiCallback.onError(new Error(response.message()));
            }

            @Override
            public void onFailure(Call<SearchPoint> call, Throwable t) {
                poiCallback.onError(new Error(t.getLocalizedMessage()));
                Log.d("Retrofit Fail", "실패");
            }
        });
    }



    // 출입구안내 Callback
    private ExitCallback exitCallback = new ExitCallback<MapPOIItem>() {
        @Override
        public void onComplete(MapPOIItem poiItem) {
            removeMarkers();
            mapView.addPOIItem(poiItem);
            mapView.selectPOIItem(poiItem, true);
            mapPOIEventListener.onPOIItemSelected(mapView, poiItem);
            CommonPoint point = (CommonPoint)poiItem.getUserObject();
            moveMapCamera(point.latitude, point.longitude, SEARCH_RADIUS, 100);

            Log.d("exitCallback", "");
        }

        @Override
        public void onNotFound() {
            Toast.makeText(getApplicationContext(), "근처에 출구를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
        }
    };

    // Keyword RestApi 를 사용해 가장 가까운 출입구를 가져온다.
    private void getExitItemByApi(String keyword, final ExitCallback exitCallback){
        Retrofit retrofit = RetrofitClient.getSearchClient();
        ApiService apiService = retrofit.create(ApiService.class);

        Call<SearchPoint> call = apiService.getSearchPoints("KakaoAK " +ApiService.KAKAO_REST_KEY,
                keyword, currentPoint.longitude, currentPoint.latitude, 10000);
        call.enqueue(new Callback<SearchPoint>() {
            @Override
            public void onResponse(Call<SearchPoint> call, Response<SearchPoint> response) {
                if(response.isSuccessful()){
                    SearchPoint searchPoint = response.body();
                    ArrayList<Document> documents = (ArrayList<Document>) searchPoint.getDocuments();
                    int size = documents.size();

                    if(size == 0)
                        exitCallback.onNotFound();
                    else{
                        int closedDistance = 10000;

                        HashMap<Integer, CommonPoint> map  = new HashMap<>();

                        for(int i = 0; i < size; i++) {
                            Document document = documents.get(i);

                            CommonPoint commonPoint = new CommonPoint(document);
                            int distance = currentPoint.distanceTo(commonPoint);

                            map.put(distance, commonPoint);

                            if(distance < closedDistance){
                                closedDistance = distance;
                            }
                        }
                        MapPOIItem poiItem = addMarker(map.get(closedDistance));
                        poiItem.setUserObject(map.get(closedDistance));
                        exitCallback.onComplete(poiItem);
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchPoint> call, Throwable t) {
                Log.d("getExitItemByApi", "실패");
            }
        });
    }


    private void markExitingGate() {
        // 출입구중 가장 가까운 장소를 찾아 지도에 표시
        getExitItemByApi("한강공원출입구", exitCallback);
    }


    /**
     *  Google Location Api Service
     *   - 현재위치를 얻어 오는 Api
     */
    private void initLocationService() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(googleConnectionCallbacksListener)
                .addOnConnectionFailedListener(googleConnectionFailListener)
                .addApi(LocationServices.API)
                .build();
    }

    @SuppressWarnings("deprecation")
    private GoogleApiClient.ConnectionCallbacks googleConnectionCallbacksListener = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(1000)
                    .setFastestInterval(500);
            if ( Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED)
                return  ;

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, googleLocationListener);
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            currentPoint = new Point("현재위치", location.getLatitude(), location.getLongitude());
            moveMapCamera(currentPoint.latitude, currentPoint.longitude, 200, 100);

            if(isExiting)
                markExitingGate();
            else if(isCulture)
                getPOIItemByDB(poiCallback, contentsName);
            Log.d("GoogleLocationApiClient", "onConnected");
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d("GoogleLocationApiClient", "onSuspended");
        }
    };


    private GoogleApiClient.OnConnectionFailedListener googleConnectionFailListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        }
    };

    private LocationListener googleLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            currentPoint.updateLatLon(location.getLatitude(), location.getLongitude());
        }
    };

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mapView == null){
            mapView = new MapView(MapActivity.this);
            mapView.setMapViewEventListener(mapViewEventListener);
            mapView.setCurrentLocationEventListener(mapLocationListener);
            mapView.setPOIItemEventListener(mapPOIEventListener);
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving);
            mapView.setMapTilePersistentCacheEnabled(false);
            mapViewContainer.addView(mapView);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapViewContainer.removeAllViews();
        mapView = null;
    }
}
