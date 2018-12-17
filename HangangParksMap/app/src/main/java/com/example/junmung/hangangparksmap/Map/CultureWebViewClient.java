package com.example.junmung.hangangparksmap.Map;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.junmung.hangangparksmap.CulturePoint;

import net.daum.mf.map.api.MapPOIItem;

public class CultureWebViewClient extends WebViewClient {
    private boolean isLoaded, isHtmlClear;
    private String keyword;
    private MapPOIItem item;

    public CultureWebViewClient(MapPOIItem item, String keyword) {
        isLoaded = false;
        this.keyword = keyword;
        this.item = item;
        isHtmlClear = false;
    }


    /**
     *  한강몽땅 자료 파싱
     *  서울시에서는 한강관련한 여름 행사인 '한강몽땅'을 진행하고있다.
     *  이 앱에서는 행사들의 정보를 사용자에게 보여주어야 하는데
     *  서울시가 제공하는 '지도태깅 API' 에서는 행사사진이나 Url 등 자세한 정보를 제공하지 않는다.
     *  먼저, Android WebView 로 '한강몽땅' 웹페이지에 들어가서 검색 Url 을 가져왔다.
     *  사용자가 보고싶어하는 행사의 제목을 검색 하였을 때,
     *  WebView 내의 웹페이지에서는 해당하는 목록이 뜨게 된다.
     *  웹페이지 Html Tag 중 행사제목과 같은 Tag 를 찾아내어, href 주소를 통해 들어가야만
     *  해당하는 행사의 세부정보를 볼 수 있다.
     *  WebView 의 loadUrl() 함수를 사용하여 Javascript + jQuery 문법으로 href 를 찾아낸 후,
     *  최종적으로 window.location.href 를 사용하여 세부정보를 사용자에게 보여준다.
     */

    @Override
    public void onPageFinished(WebView webView, String url) {
        super.onPageFinished(webView, url);
        if(isLoaded){
            webView.setVisibility(View.VISIBLE);
            CulturePoint point = (CulturePoint)item.getUserObject();
            point.setUrl(url);
            item.setUserObject(point);
            isHtmlClear = true;
            webView.loadUrl("javascript:(" +
                    "function($) {" +
                        "$('#wrapper').children().not('#container').remove();" +
                    "}" +
                    ")(jQuery)");

        } else if (isHtmlClear) {

        } else {
            webView.loadUrl("javascript:(" +
                    "function($) {" +
                        "window.location.href = $(\".cnt-theme h4 a span:contains('" + keyword + "')\").parent().attr('href');" +
                    "}" +
                    ")(jQuery)");

            isLoaded = true;
        }
    }

    @Override
    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
        return true;
    }
}
