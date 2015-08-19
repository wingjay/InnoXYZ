package com.innoxyz.data.runtime.model.user;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by laborish on 14-3-16.
 */
//用于在Activity中使用Intent传递对象
public class ParcelableUser implements Parcelable{

    // 成员变量
    private int id;
    private String name;

    // 1.必须实现Parcelable.Creator接口,否则在获取Person数据的时候，会报错，如下：
    // android.os.BadParcelableException:
    // Parcelable protocol requires a Parcelable.Creator object called  CREATOR on class com.um.demo.Person
    // 2.这个接口实现了从Parcel容器读取Person数据，并返回Person对象给逻辑层使用
    // 3.实现Parcelable.Creator接口对象名必须为CREATOR，不如同样会报错上面所提到的错；
    // 4.在读取Parcel容器里的数据事，必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
    // 5.反序列化对象
    public static final Creator<ParcelableUser> CREATOR = new Creator(){

        @Override
        public ParcelableUser createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            // 必须按成员变量声明的顺序读取数据，不然会出现获取数据出错
            ParcelableUser p = new ParcelableUser();
            p.setId(source.readInt());
            p.setName(source.readString());
            return p;
        }

        @Override
        public ParcelableUser[] newArray(int size) {
            // TODO Auto-generated method stub
            return new ParcelableUser[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        // 1.必须按成员变量声明的顺序封装数据，不然会出现获取数据出错
        // 2.序列化对象
        dest.writeInt(id);
        dest.writeString(name);
    }
}
