package com.innoxyz.data.remote;

import android.util.Log;
import android.widget.Toast;
import com.innoxyz.data.remote.interfaces.OnErrorListener;
import com.innoxyz.data.remote.interfaces.OnResponseListener;
import com.innoxyz.data.remote.interfaces.Request;
import com.innoxyz.data.remote.response.TextResponseHandler;
import com.innoxyz.global.Constants;
import com.innoxyz.global.InnoXYZApp;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-6
 * Time: 下午2:27
 * To change this template use File | Settings | File Templates.
 */

class DefaultResponseListener extends TextResponseHandler {

    @Override
    public boolean OnResponseContent(int responseCode, String content) {
        Log.i(Constants.LOGTAG, content);
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    static OnResponseListener inst = new DefaultResponseListener();
}

class DefaultErrorListener implements OnErrorListener {

    @Override
    public void OnError(Exception e) {
        final String msg = e.getClass() + ":" + e.getMessage();
        Log.e("RequestException", Log.getStackTraceString(e));
        InnoXYZApp.getApplication().getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(InnoXYZApp.getApplication().getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    static OnErrorListener inst = new DefaultErrorListener();
}

public class RequestBuilder implements Request {
    private String method;
    private String baseURI = AddressURIs.HOST;
    private String relURI = "";
    private List<NameValuePair> params = new ArrayList<NameValuePair>();

    private HttpUriRequest request;
    private HttpResponse response;

    private OnResponseListener responseListener = DefaultResponseListener.inst;
    private OnErrorListener errorListener = DefaultErrorListener.inst;
    private Runnable beforeSend = null;
    private Runnable onFinish = null;

    public RequestBuilder() {
    }

    public RequestBuilder setRequestInfo(RequestInfo ri) {
        setSubURI(ri.relURI);
        setMethod(ri.method);
        return this;
    }

    public RequestBuilder setMethod(String method) {
        this.method = method;
        return this;
    }

    protected RequestBuilder setBaseURI(String uri) {
        this.baseURI = uri;
        return this;
    }

    public RequestBuilder setSubURI(String uri) {
        this.relURI = uri;
        return this;
    }

    public RequestBuilder addParameter(String name, String value) {
        params.add(new BasicNameValuePair(name, value));
        return this;
    }

    public RequestBuilder setOnResponseListener(OnResponseListener listener) {
        this.responseListener = listener;
        return this;
    }

    public RequestBuilder setOnErrorListener(OnErrorListener listener) {
        this.errorListener = listener;
        return this;
    }

    public RequestBuilder setOnBeforeSend(Runnable beforeSend) {
        this.beforeSend = beforeSend;
        return this;
    }

    public RequestBuilder setOnFinish(Runnable runnable) {
        this.onFinish = runnable;
        return this;
    }

    public void get() throws IOException {
        HttpGet get = new HttpGet(baseURI + relURI + (params.size() > 0 ? ("?" + getString(params)) : "") );
        get.addHeader("Accept", "application/json");
        get.addHeader("Referer", baseURI);
        get.addHeader("X-Requested-With", "XMLHttpRequest");
        request = get;
        AsyncExecuteThread.createAndStart(this);
    }

    public void post() throws IOException {
        HttpPost post = new HttpPost(baseURI + relURI);
        post.addHeader("Accept", "application/json");
        post.addHeader("Referer", baseURI);
        post.addHeader("X-Requested-With", "XMLHttpRequest");
        post.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
        request = post;
        AsyncExecuteThread.createAndStart(this);
    }

    protected String getString(List<NameValuePair> params) throws IOException {
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params);
        byte[] buff = new byte[(int)entity.getContentLength()];
        entity.getContent().read(buff);
        return new String(buff);
    }

    @Override
    public void request() {
        try {
            if ( method.compareToIgnoreCase("get")==0 ) {
                get();
            } else {
                post();
            }
        } catch (IOException e) {
        }
    }

    static class AsyncExecuteThread extends Thread {

        RequestBuilder rb;

        public static void createAndStart(RequestBuilder rb) {
            new AsyncExecuteThread(rb).start();
        }

        public AsyncExecuteThread(RequestBuilder rb) {
            this.rb = rb;
        }

        @Override
        public void run() {
            try {
                InnoXYZApp.getApplication().getTheClient().WaitLogin();
                if ( rb.beforeSend!=null) {
                    rb.beforeSend.run();
                }
                Log.i("NETWORK", "requesting: " + rb.request.getURI().toString());
                org.apache.http.Header[] headerFields = rb.request.getAllHeaders();
                for(int e = 0; e<headerFields.length; e++){
                    Log.i("NETWORK", headerFields[e].getName() + ": " + headerFields[e].getValue());
                }

                HttpResponse response = InnoXYZApp.getApplication().getTheClient().getClient().execute(rb.request);
                Log.i("NETWORK", "responding: " + rb.request.getURI().toString());
                if ( rb.responseListener==null || rb.responseListener.OnResponse(response) ) {
                    response.getEntity().consumeContent();
                }
            } catch (Exception e) {
                if ( rb.errorListener!=null ) {
                    rb.errorListener.OnError(e);
                }
            } finally {
                if ( rb.onFinish != null ) {
                    rb.onFinish.run();
                }
            }
        }
    }
}
