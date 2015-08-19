package com.innoxyz.data.remote;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-13
 * Time: 上午9:39
 * To change this template use File | Settings | File Templates.
 */
public class RemoteRequest extends RequestBuilder {
    public RemoteRequest setRequestInfo(RequestInfo requestInfo) {
        super.setSubURI(requestInfo.relURI);
        super.setMethod(requestInfo.method);
        return this;
    }
}
