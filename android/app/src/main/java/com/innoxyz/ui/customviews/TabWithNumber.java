package com.innoxyz.ui.customviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.innoxyz.R;

/**
 * Created by laborish on 14-1-15.
 * 自定义标签视图，继承相对布局，能够显示标题，并在右上角消息数量
 */
public class TabWithNumber extends RelativeLayout{
    //标题
    TextView name;
    //消息数量
    TextView number;
    //右上角显示消息数量的视图
    View badeg;


    public TabWithNumber(Context context){
        super(context);
    }

    public TabWithNumber(Context context, String s){
        super(context);

        ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.customviews_tab_with_number, this, true);

        name = (TextView) findViewById(R.id.tab_text);
        name.setText(s);

        number = (TextView) findViewById(R.id.tab_badge_text);

        badeg = findViewById(R.id.tab_badge);
    }

    public void setName(String s){
        name.setText(s);
    }

    public void setNumber(int i){
        //如果没有未读消息则不显示，否则显示
        if(i <= 0){
            badeg.setVisibility(GONE);
        }
        else {
            badeg.setVisibility(VISIBLE);
            if( i > 99){
                number.setText("99+");
            }
            number.setText(String.valueOf(i));
        }
    }
}
