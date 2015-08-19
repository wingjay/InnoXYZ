package com.innoxyz.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.innoxyz.ui.customviews.NetworkImage;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.R;
/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-24
 * Time: 下午3:37
 * To change this template use File | Settings | File Templates.
 */
public class NetworkImageTest extends BaseFragment {
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        View ret = inflater.inflate(R.layout.fragment_network_image_test, container, false);
        new NetworkImage( (ImageView) ret.findViewById(R.id.imageView) ).setURL("/file/file.action?id=7&type=user_avatar");

        final String[] m = new String[32];
        for(int i=0; i<m.length; i++) {
            m[i] = "Item " + i;
        }
        Spinner spinner = (Spinner) ret.findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.customviews_simplespinitem, m));
        spinner.setPrompt("What");

        return ret;
    }
}
