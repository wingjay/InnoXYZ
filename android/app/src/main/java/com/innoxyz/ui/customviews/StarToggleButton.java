package com.innoxyz.ui.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ToggleButton;
import com.innoxyz.R;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-1
 * Time: 上午11:49
 * To change this template use File | Settings | File Templates.
 */
public class StarToggleButton extends ToggleButton {

    public StarToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.color.none);
    }

    static Bitmap bmpStar = null;
    static Rect srcOn = new Rect();
    static Rect srcOff = new Rect();

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);    //To change body of overridden methods use File | Settings | File Templates.
        /*if ( bmpStar == null ) {
            Drawable drawable = getResources().getDrawable(R.drawable.icon_star);
            bmpStar = ((BitmapDrawable)drawable).getBitmap();
            srcOn.set(bmpStar.getWidth()/2, 0, bmpStar.getWidth(), bmpStar.getHeight()/2);
            srcOff.set(0, 0, bmpStar.getWidth()/2, bmpStar.getHeight()/2);
        }*/
        Rect dst = canvas.getClipBounds();
        if ( dst.width() > dst.height() ) {
            dst.inset( (dst.width() - dst.height()) / 2, 0 );
        } else {
            dst.inset(0, (dst.height() - dst.height()) / 2);
        }
        dst.inset(dst.width()/4, dst.height()/4);

        if ( isChecked() ) {
            canvas.drawBitmap( ((BitmapDrawable)getResources().getDrawable(R.drawable.star_light)).getBitmap(), null, dst, null);
        } else {
            canvas.drawBitmap( ((BitmapDrawable)getResources().getDrawable(R.drawable.star_dark)).getBitmap(), null, dst, null);
        }

    }
}
