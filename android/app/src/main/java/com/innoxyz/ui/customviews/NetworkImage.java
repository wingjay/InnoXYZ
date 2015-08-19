package com.innoxyz.ui.customviews;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.innoxyz.data.remote.RequestBuilder;
import com.innoxyz.data.remote.interfaces.OnResponseListener;
import com.innoxyz.global.InnoXYZApp;
import org.apache.http.HttpResponse;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-24
 * Time: 下午3:27
 * To change this template use File | Settings | File Templates.
 */
public class NetworkImage {
    private ImageView imageView;
    public NetworkImage(ImageView iv) {
        this.imageView = iv;
    }

    public void setURL(String Url) {
        new RequestBuilder().setSubURI(Url).setMethod("get")
                .setOnResponseListener(new OnResponseListener() {
                    @Override
                    public boolean OnResponse(HttpResponse response) throws Exception {
                        if (response.getStatusLine().getStatusCode() == 200){
                            final Bitmap bitmap = BitmapFactory.decodeStream(response.getEntity().getContent());
                            InnoXYZApp.getApplication().getMainThreadHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageBitmap(bitmap);
                                }
                            });
                        }
                        return false;
                    }
                }).request();
    }
}
