package com.innoxyz.ui.fragments.common;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-26
 * Time: 下午8:58
 * To change this template use File | Settings | File Templates.
 */
public class BaseFragment extends Fragment {

    @Override
    public void onAttach (Activity activity){
        super.onAttach(activity);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume (){
        super.onResume();
    }

    @Override
    public void onDestroyView (){
        super.onDestroyView();
    }

    @Override
    public void onDestroy (){
        super.onDestroy();
    }

    @Override
    public void onDetach (){
        super.onDetach();
    }
}
