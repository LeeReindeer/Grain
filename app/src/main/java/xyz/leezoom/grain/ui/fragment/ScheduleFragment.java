package xyz.leezoom.grain.ui.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.leezoom.grain.R;
import xyz.leezoom.grain.util.MyBase64;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {

    // TODO: 9/14/17 动态注入js
    @BindView(R.id.sd_web_view) WebView webView;

    private final static String JS = "";
    private final static String URL = "http://172.16.25.30";
    private String sourceCode = "";
    private String scheduleUrl = "";
    private boolean isLogin = false;
    private SharedPreferences info;
    private WebChromeClient mWebChromeClient = new WebChromeClient(){
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            if (newProgress > 95){
                view.loadUrl(JS);
                view.loadUrl("javascript:myFunction()");
            }

            super.onProgressChanged(view, newProgress);
        }

    };

    private WebViewClient mWebViewClient = new WebViewClient(){
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //get html source
            if (CookieManager.getInstance().getCookie(url) != null){
                isLogin = true;
                info = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = info.edit();
                editor.putBoolean("isSDLogin", isLogin);
                editor.apply();
            }
            super.onPageFinished(view, url);
        }
    };

    public ScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        ButterKnife.bind(this, view);
        setting(webView);
        webView.setWebViewClient(mWebViewClient);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView,true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }
        info = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        String userAccount = MyBase64.BASE64ToString(info.getString("aaa","none"));
        String userName = MyBase64.BASE64ToString(info.getString("nnn","none"));
        isLogin = info.getBoolean("isSDLogin", false);
        //try login
        webView.loadUrl(URL);
        if (isLogin){
            webView.loadUrl(URL+"/xskbcx.aspx?xh="+userAccount+"&xm="+userName+"&gnmkdm=N121601");
            //getScheduleUrl();
            //load schedule page
            //webView.loadUrl(scheduleUrl);
            //inject js to change style
            //webView.setWebChromeClient(mWebChromeClient);
        }
        return view;
    }

    private void setting(WebView webView){
        WebSettings settings = webView.getSettings();
        settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setDefaultTextEncodingName("utf-8");
    }
    
    private void getScheduleUrl(){
        //use jsoup to get url
        String url = "";
        this.scheduleUrl = url;
    }
}
