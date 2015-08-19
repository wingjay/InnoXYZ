package com.innoxyz.data.runtime;

import com.innoxyz.data.runtime.interfaces.IDataObservable;
import com.innoxyz.data.runtime.interfaces.IDataObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-2
 * Time: 下午3:05
 * To change this template use File | Settings | File Templates.
 */
public abstract class ObservedData<T> implements IDataObservable {
    abstract public T getData();
    abstract public void setData(T data, boolean notify);
    //数据观察器列表
    List<IDataObserver> observers = new ArrayList<IDataObserver>();

    //注册观察器，若列表中未包含该观察器，则将其添加到 observers 列表中
    @Override
    public void registerObserver(IDataObserver observer) {
        if ( !observers.contains(observer) ) {
            observers.add(observer);
        }
    }
    //删除某个观察器
    @Override
    public void removeObserver(IDataObserver observer) {
        observers.remove(observer);
    }
    //删除所有观察器
    @Override
    public void removeAllObserver(){
        observers.clear();
    }

    //更新 observers 中的所有observer的数据
    @Override
    public void notifyObservers() {
        for(IDataObserver observer : observers) {
            observer.update(getData());
        }
    }
}
