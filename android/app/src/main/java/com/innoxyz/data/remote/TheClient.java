package com.innoxyz.data.remote;

import android.util.Log;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.RequestFuture;

import com.innoxyz.R;
import com.innoxyz.global.InnoXYZApp;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.util.concurrent.Semaphore;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-7
 * Time: 下午3:39
 * To change this template use File | Settings | File Templates.
 */
public class TheClient {
    private final HttpClient client;
    private Semaphore lock_login = new Semaphore(1);
    public TheClient() {
        HttpParams httpParams = new BasicHttpParams();
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setUseExpectContinue(httpParams, false);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(R.integer.max_connections_per_route));
        ConnManagerParams.setTimeout(httpParams, R.integer.timeout_waiting_connection);
        HttpConnectionParams.setConnectionTimeout(httpParams, R.integer.timeout_connecting);
        HttpConnectionParams.setSoTimeout(httpParams, R.integer.timeout_waiting_response);

        SchemeRegistry sr = new SchemeRegistry();
        sr.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 8080));
        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams,sr);
        client = new DefaultHttpClient(cm, httpParams);
    }

    public void WaitLogin() throws InterruptedException {
        lock_login.acquire();
        lock_login.release();
    }

    public void Login(String username, String password) {
        try {
//            new RequestBuilder().setSubURI("/user/login").addParameter("username", username).addParameter("password", password)
//                    .setOnBeforeSend(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                lock_login.acquire();
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                            }
//                        }
//                    })
//                    .setOnFinish(new Runnable() {
//                        @Override
//                        public void run() {
//                            lock_login.release();
//                        }
//                    })
//                    .post();

            final RequestFuture<String> future = RequestFuture.newFuture();
            StringRequest stringRequest = new StringRequestBuilder(null).setRequestInfo(AddressURIs.LOGIN)
                    .addParameter("username", username)
                    .addParameter("password", password)
//                    .setOnResponseListener(new Response.Listener<String>(){
//                        @Override
//                        public void onResponse(String response){
//                            lock_login.release();
//                            Log.i("login_resp",response);
//                        }
//                    })
                    .setOnResponseListener(future)
                    .getRequest();

            lock_login.acquire();
            InnoXYZApp.getApplication().getRequestQueue().add(stringRequest);

            Runnable r = new Runnable()
            {
                @Override
                public void run()
                {
                    try{
                        String response = future.get(); //synchronous request will block here
                        lock_login.release();
                        Log.i("login_resp",response);
                    }
                    catch (Exception e){
                    }

                }
            };

            Thread t = new Thread(r);
            t.start();

            //Block until get response
            //WaitLogin();

        } catch (Exception e) {
        }
    }

    public HttpClient getClient() {
        return client;
    }
}
