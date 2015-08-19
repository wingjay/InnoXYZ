package com.innoxyz.data.remote.exceptions;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-6
 * Time: 下午3:11
 * To change this template use File | Settings | File Templates.
 */
public class ActionFailedException extends Exception {
    public ActionFailedException() {
    }

    public ActionFailedException(String detailMessage) {
        super(detailMessage);
    }

    public ActionFailedException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ActionFailedException(Throwable throwable) {
        super(throwable);
    }
}
