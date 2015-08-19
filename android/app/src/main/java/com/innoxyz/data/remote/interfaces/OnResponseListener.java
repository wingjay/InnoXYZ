package com.innoxyz.data.remote.interfaces;

import org.apache.http.HttpResponse;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-7
 * Time: 下午2:19
 * To change this template use File | Settings | File Templates.
 */
public interface OnResponseListener {
    // return true will release the connection
    boolean OnResponse(HttpResponse response) throws Exception;
}
