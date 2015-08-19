package com.innoxyz.ui.customviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.innoxyz.R;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-1
 * Time: 下午2:16
 * To change this template use File | Settings | File Templates.
 */
public class PeopleGridItem extends LinearLayout {

    private ImageView avatar;
    private TextView name;
    private ImageView checkedIcon;

    private boolean checked;

    public ImageView getAvatarView() {
        return avatar;
    }

    public TextView getNameView() {
        return name;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        if ( checked ) {
            checkedIcon.setImageResource(R.drawable.icon_warn);
        } else {
            checkedIcon.setImageResource(R.color.none);
        }
    }

    public boolean ToggleChecked() {
        setChecked( !this.checked );
        return this.checked;
    }

    private static View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            //To change body of implemented methods use File | Settings | File Templates.
            ((PeopleGridItem)view).ToggleChecked();
        }
    };

    public PeopleGridItem(Context context) {
        super(context);
        ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_griditem_people, this, true);
        setClickable(true);
        setBackgroundResource(R.drawable.clickable_background_blue);
        setLayoutParams(new GridView.LayoutParams((int) (getResources().getDimension(R.dimen.avatar_size) + 2 * getResources().getDimension(R.dimen.avatar_margin)), GridView.LayoutParams.WRAP_CONTENT));
        avatar = (ImageView)findViewById(R.id.people_griditem_avatar);
        name = (TextView)findViewById(R.id.people_grid_item_name);
        checkedIcon = (ImageView)findViewById(R.id.people_griditem_checked);
        setChecked(false);
        setOnClickListener(clickListener);
    }
}
