package com.example.junmung.hangangparksmap.RetrofitUtil;

import com.example.junmung.hangangparksmap.CulturePointPOJO.CulturePojo;
import com.example.junmung.hangangparksmap.R;
import com.example.junmung.hangangparksmap.SearchPointPOJO.SearchPoint;
import com.github.filosganga.geogson.model.FeatureCollection;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    String TMAP_APP_KEY = "b6c4c25a-f4d1-46e9-8e8f-15a8132a23b2";
    String KAKAO_REST_KEY = "2f6dfd752b1886105ae3f875d1b6efc6";
    String KAKAO_APP_KEY = "6b0e7cad70e9269af5f7779a8c03903c";
    String CULTURE_KEY = "63634462663931373131304153575970";

    @POST("tmap/routes/pedestrian?version=1" +
            "&format=json" +
            "&reqCoordType=WGS84GEO" +
            "&resCoordType=WGS84GEO")
    Call<FeatureCollection> getGuidePoints(@Header("appKey")String appKey, @Query("startName")String startName, @Query("endName")String endName,
                                           @Query("startX")Number startX, @Query("startY")Number startY,
                                           @Query("endX")Number endX, @Query("endY")Number endY);


    @GET("v2/local/search/keyword.json?")
    Call<SearchPoint> getSearchPoints(@Header("Authorization") String restKey,
                                      @Query("query")String SearchName,
                                      @Query("x") Number x, @Query("y")Number y,
                                      @Query("radius")Integer radius);

    @GET(CULTURE_KEY+"/json/Mgishangang/1/73/")
    Call<CulturePojo> getCulturePoints();


}


