package com.innoxyz.data.runtime;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-2
 * Time: 下午3:52
 * To change this template use File | Settings | File Templates.
 */
public class SimpleObservedData<T> extends ObservedData<T> {

    T data = null;

    public SimpleObservedData(T data) {
        this.data = data;
    }

    @Override
    public T getData() {
        return data;  //To change body of implemented methods use File | Settings | File Templates.
    }

    //notify表示还要对数据进行进一步操作，回调实例所绑定的 observer 对象中复写的update方法
    @Override
    public synchronized void setData(T data, boolean notify) {
        this.data = data;
        if ( notify ) {
            notifyObservers();
        }
    }
}
