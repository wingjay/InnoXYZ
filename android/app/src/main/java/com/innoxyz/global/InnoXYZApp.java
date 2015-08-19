package com.innoxyz.global;

import java.net.CookieStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.baidu.frontia.FrontiaApplication;
import com.innoxyz.data.json.parser.JsonParsers;
import com.innoxyz.data.remote.TheClient;
import com.innoxyz.data.runtime.RuntimeDataManager;

import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.innoxyz.ui.activities.LoginActivity;
import com.innoxyz.util.Factory;
import com.loopj.android.http.PersistentCookieStore;

import org.apache.http.cookie.Cookie;

import lombok.Data;


/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-7
 * Time: 下午5:15
 * To change this template use File | Settings | File Templates.
 */
public class InnoXYZApp extends FrontiaApplication {

    //上传照片临时目录
    private static final String UPLOAD_FILE_CACHE_DIR = Environment.getExternalStorageDirectory() + "/InnoXYZ/Upload/Cache";
    //上传照片目录 Upload/{fileID}
    private static final String UPLOAD_FILE_DIR = Environment.getExternalStorageDirectory() + "/InnoXYZ/Upload";
    //下载文件目录 Download/{fileID}
    private static final String DOWNLOAD_FILE_DIR = Environment.getExternalStorageDirectory() + "/InnoXYZ/Download";

    //账户名密码
    private static final String USER_NAME = "8S9s-82jdl93Dk8jKid";
    private static final String USER_KEY = "3H739GDksjs03jhsk031SKd";
    private static final String USER_ID = "Aa8ssfdsdS0fJsEr9jJdSaK3";
    private static final String USER_REAL_NAME = "Aa8ssf12skd9sSaK2";

    private static InnoXYZApp instance = null;
    public static InnoXYZApp getApplication() {
        return instance;
    }

    // 推送 channelId
    public static String channelId;

    public String getUploadFileDir() {
        return UPLOAD_FILE_DIR;
    }
    public String getUploadFileCacheDir() {
        return UPLOAD_FILE_CACHE_DIR;
    }
    public String getDownloadFileDir() {
        return DOWNLOAD_FILE_DIR;
    }

    Map<String,String> webCache = new HashMap<String, String>();

    public Map<String,String> getWebCache() {return webCache;}

    private SharedPreferences sharedPreferences;

    private RuntimeDataManager runtimeDataManager;
    private JsonParsers jsonParsers;
    private TheClient theClient;
    private Handler handler;

    private ImageLruCache imageLruCache;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    //集中管理正在下载的附件列表
    private List<String> downloadingAttachID = new ArrayList<>();
    public void addDownloadingAttachID(String attachID) {
        this.downloadingAttachID.add(attachID);
    }
    public boolean containDownloadingAttachID(String attachID) {
        return downloadingAttachID.contains(attachID);
    }
    public void removeDownloadingAttachID(String attachID) {
        this.downloadingAttachID.remove(attachID);
    }

    public Semaphore getGetUnreadNumNow() {
        return getUnreadNumNow;
    }

    private Semaphore getUnreadNumNow = new Semaphore(1);

    private int currentUserId;

    private String userName;

    //手机本地获取用户信息
    public int getCurrentUserId() {
        currentUserId = sharedPreferences.getInt(USER_ID, 0);
        return currentUserId;
    }
    public String getCurrentUserName() {
        return sharedPreferences.getString(USER_NAME, "");
    }
    public String getCurrentUserRealName() {
        return sharedPreferences.getString(USER_REAL_NAME, "");
    }
    public String getCurrentUserKey() {
        return sharedPreferences.getString(USER_KEY, "");
    }

    //将用户信息存入手机本地
    public void setUserInfo(int userId, String userName, String userKey, String userRealName) {
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.putInt(USER_ID, userId);
        prefEditor.putString(USER_NAME, userName);
        prefEditor.putString(USER_KEY, userKey);
        prefEditor.putString(USER_REAL_NAME, userRealName);
        prefEditor.apply();
    }


    public RuntimeDataManager getRuntimeDataManager() {
        return runtimeDataManager;
    }

    public JsonParsers getJsonParsers() {
        return jsonParsers;
    }

    public TheClient getTheClient() {
        return theClient;
    }

    public Handler getMainThreadHandler() {
         return handler;
    }

    public ImageLruCache getImageLruCache() { return imageLruCache; }

    public RequestQueue getRequestQueue() { return requestQueue; }

    public ImageLoader getImageLoader() {return imageLoader; }

    //利用 sharedPreferences 从本地获取存储的用户信息，包括用户名、密码和ID
    public SharedPreferences getSharedPreferences() {return  sharedPreferences; }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //cookie初始化
        Factory.init();
        //用于在手机上存储一些小型数据
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            runtimeDataManager = new RuntimeDataManager();
            jsonParsers = new JsonParsers();
            //theClient = new TheClient();

            imageLruCache = new ImageLruCache(ImageLruCache.getDefaultLruCacheSize());
            //创建一个http请求队列对象
            requestQueue = Volley.newRequestQueue(this);
            //imageLoader 在被从父控件detach时会自动取消网络请求
            imageLoader = new ImageLoader(requestQueue, imageLruCache);
        } catch (Exception e) {
            Log.e("Application creating", "Failed on initializing application data. exiting..." + Log.getStackTraceString(e));
        }
        handler = new Handler();
    }

}
