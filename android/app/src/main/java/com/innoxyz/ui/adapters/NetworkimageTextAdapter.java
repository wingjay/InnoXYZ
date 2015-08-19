package com.innoxyz.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import com.innoxyz.data.remote.AddressURIs;
import com.innoxyz.global.InnoXYZApp;
import com.innoxyz.ui.customviews.CheckableRelativeLayout;
import com.innoxyz.R;
/**
 * Created by laborish on 2014-4-14.
 */

public class NetworkimageTextAdapter extends BaseAdapter {

    private Context context;
    private String[] items;
    private Integer[] ids;

    public NetworkimageTextAdapter(Context context, String[] items, Integer[] ids){
        this.context = context;
        this.items = items;
        this.ids = ids;
    }

    @Override
    public int getCount(){
        return items.length;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup){
        ViewHolder holder;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.listitem_involver_spinner, null);
            holder = new ViewHolder();
            holder.listitem_involver_name = (TextView)view.findViewById(R.id.listitem_involver_name);
            holder.listitem_involver_avatar = (NetworkImageView)view.findViewById(R.id.listitem_involver_avatar);
            holder.listitem_involver_layout = (CheckableRelativeLayout)view.findViewById(R.id.listitem_involver_layout);

            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }

        holder.listitem_involver_name.setText(items[i]);
        if(ids[i]!=-1) {
            holder.listitem_involver_avatar.setImageUrl(AddressURIs.getUserAvatarURL(ids[i]), InnoXYZApp.getApplication().getImageLoader());
        }

        holder.listitem_involver_layout.setChecked(((ListView) viewGroup).isItemChecked(i));

        return view;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    static class ViewHolder{
        TextView listitem_involver_name;
        NetworkImageView listitem_involver_avatar;
        CheckableRelativeLayout listitem_involver_layout;
    }


}
