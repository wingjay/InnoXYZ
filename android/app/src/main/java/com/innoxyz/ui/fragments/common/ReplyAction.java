package com.innoxyz.ui.fragments.common;

/**
 * Created by laborish on 14-3-16.
 * 让所有回复fragment实现该接口，当NewActivity中点击完成按钮时，执行对应fragment中的reply方法，来处理回复
 */
public interface ReplyAction {
    public void reply();
}
