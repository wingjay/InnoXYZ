package com.innoxyz.data.runtime.model.attachment;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

/**
 * User: yj
 * Date: 14-12-19
 * Time: 晚上21:18
 */
//附件序列化，以便在activity间传递
@Data
public class ParcelableAttachment implements Parcelable {

    //附件ID
    private String id;
    //附件名称
    private String name;

    public ParcelableAttachment(String i, String n) {
        this.id = i;
        this.name = n;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
    }

    public static final Creator<ParcelableAttachment> CREATOR = new Creator<ParcelableAttachment>() {

        @Override
        public ParcelableAttachment createFromParcel(Parcel source) {
            return new ParcelableAttachment(source.readString(), source.readString());
        }

        @Override
        public ParcelableAttachment[] newArray(int size) {
            return new ParcelableAttachment[size];
        }
    };

}

