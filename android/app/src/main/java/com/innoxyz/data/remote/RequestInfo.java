package com.innoxyz.data.remote;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-13
 * Time: 上午9:43
 * To change this template use File | Settings | File Templates.
 */
public class RequestInfo {
    public final String relURI;
    public final String method;

    public RequestInfo(String relURI, String method) {
        this.relURI = relURI;
        this.method = method;
    }
}
