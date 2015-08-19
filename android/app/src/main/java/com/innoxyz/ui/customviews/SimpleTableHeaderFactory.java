package com.innoxyz.ui.customviews;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.innoxyz.R;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-16
 * Time: 下午1:50
 * To change this template use File | Settings | File Templates.
 */
public class SimpleTableHeaderFactory {
    private LayoutInflater mInflater;
    public SimpleTableHeaderFactory(LayoutInflater inflater) {
        mInflater = inflater;
    }

    public TextView create(String text) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(4,8,4,2);

        TextView textView = (TextView) mInflater.inflate(R.layout.customviews_textheader, null, false);
        textView.setText(text);
        textView.setLayoutParams(lp);
        textView.setPadding(24, 8, 8, 8);
        textView.setGravity(Gravity.LEFT);
        textView.setTextSize(19);
        return textView;
    }
}
