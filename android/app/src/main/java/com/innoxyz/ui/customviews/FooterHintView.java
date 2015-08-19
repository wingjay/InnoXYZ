package com.innoxyz.ui.customviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.innoxyz.R;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-25
 * Time: 下午4:05
 * To change this template use File | Settings | File Templates.
 */
public class FooterHintView extends LinearLayout {
    protected TextView tv;

    public FooterHintView(Context context) {
        super(context);
        setClickable(true);
        ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.customviews_footerhint, this, true);
        tv = (TextView) findViewById(R.id.footerhint_text);
    }

    public void setText(String text) {
        tv.setText(text);
    }
}
