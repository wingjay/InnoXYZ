package com.innoxyz.util;


import android.content.Context;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.innoxyz.R;
import com.innoxyz.global.InnoXYZApp;
import com.innoxyz.network.URL;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

import org.apache.http.cookie.Cookie;

import java.net.CookieStore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yj on 2014/12/13.
 * 工厂模式，对外生产类
 */
public class Factory {

    private static final String IC_COOKIE = "ic";
    private static AsyncHttpClient getClient;
    private static PersistentCookieStore cookieStore;


    /**
     * 获取client
     */
    public static AsyncHttpClient getHttpClient(){

        if (getClient == null) {
            getClient = new AsyncHttpClient();

            getClient.addHeader("Referer", URL.host);
            //请求json xml格式数据
            getClient.addHeader("X-Requested-With","XMLHttpRequest");
            getClient.addHeader("Accept","application/json");
            //注意，不可添加 autologin项，会导致ic的cookie无法自动添加到PersistentCookieStore
            //切换网络就会导致cookie失效。为啥？

            getClient.setCookieStore(cookieStore);
        }
        return getClient;

    }

    public static PersistentCookieStore init() {
        cookieStore = new PersistentCookieStore(InnoXYZApp.getApplication().getApplicationContext());
        return cookieStore;
    }

    public static PersistentCookieStore getCookieStore() {
        return cookieStore;
    }
    //获取本地cookie
    public static Cookie getIcCookie() {
        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(IC_COOKIE)) {
                return cookie;
            }
        }
        return null;
    }
    //本地是否存在IcCookie
    public static boolean hasIcCookie(){
        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(IC_COOKIE)) {
                return true;
            }
        }
        return false;
    }
    //删除本地cookie
    public static void deleteLocalCookie() {
        cookieStore.clear();
    }
    /**
     * 获取POST请求的client
     */
    public static AsyncHttpClient getPostHttpClient () {
        return getHttpClient();
    }

    //弹出框类
    public static NiftyDialogBuilder getNiftyDialog(Context context) {
        NiftyDialogBuilder dialogBuilder;
        dialogBuilder = NiftyDialogBuilder.getInstance(context);
        dialogBuilder
                .withDialogColor("#1772c4")
                .withEffect(Effectstype.RotateBottom)
                .withIcon(R.drawable.tip)
                .isCancelableOnTouchOutside(true)
                .withDuration(300);
        return dialogBuilder;
    }


}
