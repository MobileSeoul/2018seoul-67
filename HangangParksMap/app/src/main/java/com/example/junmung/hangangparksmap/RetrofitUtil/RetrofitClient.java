package com.example.junmung.hangangparksmap.RetrofitUtil;

import com.google.gson.Gson;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit guideRetrofit = null;
    private static Retrofit searchRetrofit = null;
    private static Retrofit cultureRetrofit = null;


    // TMap 길찾기 Api, GeoJson 형식을 쓰기 때문에 형식에 맞는 Gson 이 필요하다
    public static Retrofit getGuideClient(Gson gson){
        if(guideRetrofit == null){
            guideRetrofit = new Retrofit.Builder()
                    .baseUrl("https://api2.sktelecom.com/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        return guideRetrofit;
    }

    // 카카오 RestApi, 키워드를 기반으로 장소를 검색하여 정보를 얻을 수 있다.
    public static Retrofit getSearchClient(){
        if(searchRetrofit == null){
            searchRetrofit = new Retrofit.Builder()
                    .baseUrl("https://dapi.kakao.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return searchRetrofit;
    }

    public static Retrofit getCultureCilent(){
        if(cultureRetrofit == null){
            cultureRetrofit = new Retrofit.Builder()
                    .baseUrl("http://openapi.seoul.go.kr:8088/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return cultureRetrofit;
    }
}
