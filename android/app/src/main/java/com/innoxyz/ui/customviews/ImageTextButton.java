package com.innoxyz.ui.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.innoxyz.R;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-7-31
 * Time: 下午2:38
 * To change this template use File | Settings | File Templates.
 */
public class ImageTextButton extends LinearLayout {

    protected ImageView imageView;
    protected TextView textView;

    private void initLayout(Context context) {
        setOrientation(HORIZONTAL);
        setLayoutParams(new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f));
        setClickable(true);
        setBackgroundResource(R.drawable.clickable_background_blue);
        setPadding(1, 3, 1, 3);
        // Inflating Children
        ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.customviews_image_text_button, this, true);
        textView = (TextView)findViewWithTag("inner_textview");
        imageView = (ImageView)findViewWithTag("inner_imageview");
    }

    public ImageTextButton(Context context) {
        super(context);
        initLayout(context);
    }

    public ImageTextButton(Context context, int resId, String text) {
        this(context);
        this.setPadding(4,16,4,16);
        imageView.setImageResource(resId);
        textView.setText(text);
    }

    public ImageTextButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageTextButton);
        imageView.setImageResource(a.getResourceId(R.styleable.ImageTextButton_image_src, R.drawable.icon));
        textView.setText(a.getText(R.styleable.ImageTextButton_text));
        a.recycle();
    }

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getTextView() {
        return textView;
    }
}
