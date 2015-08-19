package com.innoxyz.ui.customviews;

import android.widget.ImageView;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-11-29
 * Time: 上午12:10
 * To change this template use File | Settings | File Templates.
 */
public class ProfileImage extends NetworkImage {
    private int id;
    public ProfileImage(ImageView iv, int id) {
        super(iv);
        this.id = id;
    }

    public void showProfileImage (){
        String url = "/file/file.action?id=" + id + "&type=user_avatar";
        super.setURL(url);

    }
}
