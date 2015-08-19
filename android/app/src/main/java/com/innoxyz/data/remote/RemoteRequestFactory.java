package com.innoxyz.data.remote;

import com.innoxyz.data.remote.interfaces.Request;
import com.innoxyz.data.remote.interfaces.RequestBuilderFactory;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-13
 * Time: 上午9:38
 * To change this template use File | Settings | File Templates.
 */
class RemoteRequestFactory implements RequestBuilderFactory {

    @Override
    public Request createRequest() {
        return new RemoteRequest();
    }
}
