package com.innoxyz.data.runtime.model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

/**
 * Created by yj on 14-3-16.
 */
//序列化传递User对象
@Data
public class ParcelableUser implements Parcelable{

    // 成员变量
    private String name;
    private int id;

    public ParcelableUser(String n, int i) {
        this.name = n;
        this.id = i;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(id);
    }

    public static final Creator<ParcelableUser> CREATOR = new Creator<ParcelableUser>() {

        @Override
        public ParcelableUser createFromParcel(Parcel source) {
            return new ParcelableUser(source.readString(), source.readInt());
        }

        @Override
        public ParcelableUser[] newArray(int size) {
            return new ParcelableUser[size];
        }
    };

}
