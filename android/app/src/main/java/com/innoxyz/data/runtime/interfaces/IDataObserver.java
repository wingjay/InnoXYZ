package com.innoxyz.data.runtime.interfaces;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-2
 * Time: 下午3:03
 * To change this template use File | Settings | File Templates.
 */
//数据观察器,创建一个observer时要实现里面的数据更新方法
//一般是结合 JsonResponseHandler 使用。在 JsonResponseHandler 中获取服务器数据并转化为对应类型对象后，调用最初的observer对象来对数据进行操作。如显示/存储
public interface IDataObserver {
    public void update(Object o);
}
