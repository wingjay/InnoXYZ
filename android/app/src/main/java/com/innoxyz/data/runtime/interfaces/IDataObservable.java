package com.innoxyz.data.runtime.interfaces;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-2
 * Time: 下午3:02
 * To change this template use File | Settings | File Templates.
 */
public interface IDataObservable {
    //注册一个数据观察器
    public void registerObserver(IDataObserver observer);
    //删除一个数据观察器
    public void removeObserver(IDataObserver observer);
    //删除所有数据观察器
    public void removeAllObserver();
    //通知观察器
    public void notifyObservers();
}
