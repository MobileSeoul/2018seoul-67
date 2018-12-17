package com.example.junmung.hangangparksmap.DataBase;

import android.util.Log;

import com.example.junmung.hangangparksmap.CommonPoint;
import com.example.junmung.hangangparksmap.CulturePoint;
import com.example.junmung.hangangparksmap.CulturePointPOJO.Row;
import com.example.junmung.hangangparksmap.FavoritePoint;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class DBHelper {
    private static DBHelper ourInstance = new DBHelper();

    public static DBHelper getInstance() {
        return ourInstance;
    }

    Realm realm;

    public DBHelper() {
        realm = Realm.getDefaultInstance();
    }

    public Realm getRealmInstance() {
        return realm;
    }

    // 새로 추가하는 부분
    public void insertCultureInfos(ArrayList<Row> rows) {
        RealmList<CultureInfo> infos = new RealmList<>();

        int i =0;
        for(Row row: rows) {
            infos.add(new CultureInfo(i, row.getContentsName(), row.getAddress(), row.getLatitude(), row.getLongitude(),
                    row.getEventPark(), row.getEventDate(), row.getEventTime(), row.getCoordinateType()));
            i++;
        }
        Log.d("Culture Size", ""+infos.size());
        realm.beginTransaction();
        realm.insert(infos);
        realm.commitTransaction();
    }

    // 현제 테이블에 있는 모든 Member를 리스트로 받는 부분
    public ArrayList<CulturePoint> getCultureItems() {
        RealmResults<CultureInfo> results = realm.where(CultureInfo.class).findAll().sort("index", Sort.DESCENDING);
        List<CultureInfo> infos = realm.copyFromRealm(results);
        ArrayList<CulturePoint> culturePoints = new ArrayList<>();

        // 질의한 결과를 RecyclerView에서 이용할 수 있도록 arrayList에 넣어주는 부분
        for( CultureInfo info : infos){
            culturePoints.add(new CulturePoint(info.getContentsName(), info.getLatitude(), info.getLongitude(), info.getAddress(),
                    info.getParkName(), info.getEventDate(), info.getEventTime(), info.getPointType()));
        }

        return culturePoints;
    }

    // 같은 contentsName 을 가진 CulturePoint 를 가져온다.
    public CulturePoint getCultureItem(String contentsName){
        CultureInfo cultureInfo = realm.where(CultureInfo.class).equalTo("contentsName", contentsName).findFirst();
        CultureInfo info = realm.copyFromRealm(cultureInfo);
        CulturePoint point = new CulturePoint(info.getContentsName(), info.getLatitude(), info.getLongitude(), info.getAddress(),
                info.getParkName(), info.getEventDate(), info.getEventTime(), info.getPointType());

        return point;
    }

    public void deleteAll(){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<CultureInfo> memos = realm.where(CultureInfo.class).findAll();

                memos.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d("Realm _ ", "삭제되었습니다.");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });

    }

    public void insertFavoriteInfo(CommonPoint commonPoint){
        insertFavoriteInfo(new FavoritePoint(commonPoint));
    }

    public boolean insertFavoriteInfo(CulturePoint culturePoint){
        if(culturePoint.hasUrl()){
            insertFavoriteInfo(new FavoritePoint(culturePoint));
            return true;
        }
           return false;
    }

    public void insertFavoriteInfo(FavoritePoint point) {
        FavoriteInfo favoriteInfo;

        if(favoriteIsEmpty())
            favoriteInfo = new FavoriteInfo(1, point.getName(), point.getAddress(), point.latitude, point.longitude, point.getDate());
        else {
            int lastIndex = getFavoriteSize() + 1;
            favoriteInfo = new FavoriteInfo(lastIndex, point.getName(), point.getAddress(), point.latitude, point.longitude, point.getDate());
        }
        if(point.hasUrl())
            favoriteInfo.setUrl(point.getUrl());

        realm.beginTransaction();
        realm.insert(favoriteInfo);
        realm.commitTransaction();
    }

    public ArrayList<FavoritePoint> getFavoriteItems() {
        RealmResults<FavoriteInfo> infos = realm.where(FavoriteInfo.class).findAll().sort("date", Sort.DESCENDING);
        ArrayList<FavoritePoint> favoritePoints = new ArrayList<>();

        // 질의한 결과를 RecyclerView에서 이용할 수 있도록 arrayList에 넣어주는 부분
        for( FavoriteInfo info : infos){
            favoritePoints.add(new FavoritePoint(info.getName(),
                    info.getLatitude(), info.getLongitude(), info.getAddress(), info.getDate()));
        }

        return favoritePoints;
    }

    public void deleteFavoriteItem(final String name){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(FavoriteInfo.class).equalTo("name", name).findFirst().deleteFromRealm();
            }
        });
    }

    public FavoritePoint getFavoriteItem(String name){
        FavoriteInfo queryResult = realm.where(FavoriteInfo.class).equalTo("name", name).findFirst();
        FavoriteInfo info = realm.copyFromRealm(queryResult);
        Log.d("info", info.getName()+"\n"+info.getLatitude()+"\n"+ info.getLongitude()+"\n"+ info.getAddress()+"\n"+ info.getDate());
        return new FavoritePoint(info.getName(), info.getLatitude(), info.getLongitude(), info.getAddress(), info.getDate());
    }

    public boolean favoriteIsEmpty(){
        return realm.where(FavoriteInfo.class).findAll().isEmpty();
    }

    public boolean isExistFavorite(String name){
        if(realm.where(FavoriteInfo.class).equalTo("name", name).findFirst() == null)
            return false;
        else
            return true;
    }

    public int getFavoriteSize() {
        return realm.where(FavoriteInfo.class).findAll().size();
    }



    public void deleteFavoriteAll(){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<FavoriteInfo> memos = realm.where(FavoriteInfo.class).findAll();

                memos.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d("Realm _ ", "삭제되었습니다.");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });
    }

    public void insertCultureFavorite(String contentName) {
        CultureFavorite cultureFavorite = new CultureFavorite(contentName);

        realm.beginTransaction();
        realm.insert(cultureFavorite);
        realm.commitTransaction();
    }

    public void deleteCultureFavorite(final String name){
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(CultureFavorite.class).equalTo("contentName", name).findFirst().deleteFromRealm();
            }
        });
    }

    public boolean cultureFavoriteIsEmpty(){
        return realm.where(CultureFavorite.class).findAll().isEmpty();
    }

    public boolean isExistCultureFavorite(String name){
        if(realm.where(CultureFavorite.class).equalTo("contentName", name).findFirst() == null)
            return false;
        else
            return true;
    }

    public int getCultureFavoriteSize() {
        return realm.where(CultureFavorite.class).findAll().size();
    }

}
