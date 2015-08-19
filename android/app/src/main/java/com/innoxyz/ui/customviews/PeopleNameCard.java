package com.innoxyz.ui.customviews;

import android.content.Context;
import android.text.InputFilter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.innoxyz.R;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-1
 * Time: 下午5:30
 * To change this template use File | Settings | File Templates.
 */
public class PeopleNameCard extends TextView {
    public PeopleNameCard(Context context) {
        super(context);
        this.setPadding(12, 6, 12, 6);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(6,6,6,6);
        this.setLayoutParams(params);
        this.setSingleLine();
        this.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(16)
        });
        this.setBackgroundResource(R.drawable.namecard_background);
        this.setText("name");
    }
}
