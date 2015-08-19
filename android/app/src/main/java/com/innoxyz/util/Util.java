package com.innoxyz.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


import com.innoxyz.data.runtime.beans.attachment.Attachment;
import com.innoxyz.global.InnoXYZApp;
import com.innoxyz.network.URL;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import com.innoxyz.R;
/**
 * 通用方法
 * Created by yj on 2014/9/18.
 */
public class Util {

    // 获取ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return apiKey;
    }

    //保存二进制文件到SD卡,为每一个文件建立一个独立文件夹，以ID命名
    public static boolean savaFileFromBinary(byte[] fileData, String fileName, String fileID, Context context){
        //判断是否由SD卡
        if (!hasSdCard()){
            Toast.makeText(context, "无SD卡！", Toast.LENGTH_SHORT).show();
            return false;
        }
        //创建要下载的file目录
        String dirName = InnoXYZApp.getApplication().getDownloadFileDir() + File.separator + fileID;
        File dirFile = new File(dirName);
        if (!dirFile.exists()){
            boolean mkDir = dirFile.mkdirs();
            if (!mkDir){
                Toast.makeText(context, "创建目录失败！", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        //创建下载文件的File对象
        String filePath = dirName  + File.separator + fileName;
        File file = new File(filePath);
        //判断是否存在，若存在，是否是已经下载完成的
        if (file.exists()){
            if (file.length() == fileData.length){
                Toast.makeText(context, fileName + "文件已存在于：InnoXYZ/Download/" + fileID + "目录下！",Toast.LENGTH_SHORT).show();
                return true;
            }else{
                //文件不存在或者文件更新了,则删除原有文件
                boolean deleteOldFile = file.delete();
            }
        }
        //重新下载文件
        try {
            //输入流-fileData
            InputStream is = new ByteArrayInputStream(fileData);
            //输出流
            FileOutputStream fos = new FileOutputStream(file);
            byte[] fileBuffer = new byte[fileData.length];
            int len = 0;
            //先把fileData 逐个读到 fileBuffer，并写到输出流中
            while((len = is.read(fileBuffer)) != -1){
                fos.write(fileBuffer,0, len);
            }
            //关闭连接减少资源开销
            is.close();
            fos.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    //判断SD卡存在
    public static boolean hasSdCard() {
        String status = Environment.getExternalStorageState();
        return status.equals(Environment.MEDIA_MOUNTED);
    }

    //调整html代码 \n\t 字符以显示
    public static String unescape(String description) {
        description = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + description;
        description = description.replaceAll("\t","&nbsp;");
        return description.replaceAll("\n", "</p><p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    }

    //调整引用:[quote][strong]小明：[/strong]这是小明说的[/quote]这是我自己说的
    //所返回的文本用 html 显示:Html.fromHtml(string)
    public static String quoteModified(String content) {
        content = content.replaceAll("\\[quote]", "<p>@");
        content = content.replaceAll("\\[strong]", "<b>");
        content = content.replaceAll("\\[/strong]", "</b></p><blockquote style=\"border:1px solid #ccc;background-color:#eee;margin:2px;\">");
        content = content.replaceAll("\\[/quote]", "</blockquote>");
        content = content.replaceAll("\\[b]", "<b>");
        content = content.replaceAll("\\[/b]", "</b><blockquote style=\"border:1px solid #ccc;background-color:#eee;margin:2px;\">");
        return content;
    }

    //根据Url和ImageView显示图片
//    public static void setImage(String imgUrl, final ImageView imageView){
//        //如果使用newHttpClient或者getHttpClient，那么就会自动带上特殊头，向服务器请求json格式的数据。而此处需要二进制数据
//        //imgUrl格式为：URL.URL_FILE_GET_ID + userAvatarId
//        (new AsyncHttpClient()).get(imgUrl,new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length);
//                imageView.setImageBitmap(bitmap);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                //Toast.makeText(, "对不起，无法加载图片！", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    //将媒体库Uri转化为PATH文件路径
    public static String uriToRealPath(Context context, Uri uri) {
        String path = "";
        if (uri != null){
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[] {MediaStore.Images.Media.DATA},
                    null,
                    null,
                    null);
            cursor.moveToFirst();
            path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
        }
        return path;
    }

    //将bitmap图片保存到SD卡,path包含文件名，参照UserFragment里的FULL_AVATAR_PATH
    public static void saveBitmapToSD(Bitmap photoBitmap, String path) {
        if (hasSdCard()) {
            File photoFile = new File(path); //在指定路径下创建文件
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(photoFile);
                if (photoBitmap != null) {
                    if (photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100,fileOutputStream)) {
                        fileOutputStream.flush();
                    }
                }
            } catch (FileNotFoundException e) {
                photoFile.delete();
                e.printStackTrace();
            } catch (IOException e) {
                photoFile.delete();
                e.printStackTrace();
            } finally {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //移动文件到其他目录,文件名不变
    public static boolean moveFileToAnotherDir(File file, String destDirPath) {
        //判断目标目录是否存在，不存在则创建
        File destDirFile = new File(destDirPath);
        if (!destDirFile.exists()) {
            try{
                boolean mkdir = destDirFile.mkdirs();
                if (!mkdir){
                    return false;
                }
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        //移动
        return file.renameTo(new File(destDirPath + File.separator + file.getName()));
    }

    //当两个listView嵌套时，通过此方法可根据子listView来设置父listView的高度防止子listView显示不全
    //要求子listView的每个item必须是线性布局。因为其它布局的onMeasure()会导致异常
    public static void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        if (listAdapter == null) {
            params.height = 0;
            listView.setLayoutParams(params);
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    //根据文件ID判断文件是否存在，如果文件夹存在但内部为空，则删除该文件夹
    public static File isFileExistsById(String fileID) {
        File directory = new File(InnoXYZApp.getApplication().getUploadFileDir() + File.separator + fileID);
        //判断目录是否存在
        if (directory.exists()) {
            //判断是否为空文件夹
            if (directory.isDirectory() && directory.length() == 0) {
                directory.delete();
                return null;
            }
            //目录存在且不为空
            File[] contents = directory.listFiles();
            if (contents != null && contents.length > 0) {
                return contents[0];
            }
            //目录不存在
            return null;
        }else {
            directory = new File(InnoXYZApp.getApplication().getDownloadFileDir() + File.separator + fileID);
            //判断目录是否存在
            if (directory.exists()) {
                //判断是否为空文件夹
                if (directory.isDirectory() && directory.length() == 0) {
                    directory.delete();
                    return null;
                }
                File[] contents = directory.listFiles();
                if (contents != null && contents.length > 0) {
                    return contents[0];
                }
            }
            //目录不存在
            return null;
        }
    }
    //查看文件
    public static void displayFile(Context context, File theFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String fileEnding = theFile.getName().substring(theFile.getName().indexOf(".") + 1);
        String type = mime.getMimeTypeFromExtension(fileEnding);
        intent.setDataAndType(Uri.fromFile(theFile), type);

        context.startActivity(intent);
    }

    //删除文件，只需要ID即可，因为一个ID文件夹里唯一包含一个文件
    public static int deleteFile(String fileId) {
        //先判断是否存在
        if (isFileExistsById(fileId) == null)
            return -1;
        //现在Upload文件夹找，找到则删除
        File theFile = new File(InnoXYZApp.getApplication().getUploadFileDir() + File.separator + fileId);
        if (theFile.exists()) {
            return deleteFileOrDirectory(theFile);
        }else {
            theFile = new File(InnoXYZApp.getApplication().getDownloadFileDir() + File.separator + fileId);
            if (theFile.exists()) {
                return deleteFileOrDirectory(theFile);
            }
        }
        return -1;

    }


    /**
     * 删除文件夹所有内容
     * 成功返回1，失败返回0
     */
    public static int deleteFileOrDirectory(File file) {
        if (file.exists()) {
            if (file.isFile()) { // 是文件
               return file.delete() ? 1 : 0;
            } else if (file.isDirectory()) { // 是目录
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    if (!files[i].delete())
                        return 0;
                }
                //删除目录
                return file.delete() ? 1 : 0;
            }
            return -1;
        }
        return -1;
    }


    //根据原始图片的路径获取图片，经过压缩存入目标到SD卡对应路径
//    public static void StoreAvatarFromPath(String originalPath, String destPath, int widthScale, int heightScale){
//        Bitmap bitmap = BitmapFactory.decodeFile(originalPath);
//        StoreAvatarFromBitmap(bitmap, destPath, widthScale, heightScale);
//    }

    /** 根据原始图片的bitmap数据，经过压缩存入目标到SD卡对应路径 **/
    public static void StorePicFromPath(Bitmap bitmap, String destPath, int widthScale, int heightScale){
        Bitmap newbitmap = Util.zoomBitmap(bitmap, bitmap.getWidth() / widthScale, bitmap.getHeight() / heightScale);
        //Bitmap回收内存
        bitmap.recycle();
        //保存bitmap到本地SD卡
        saveBitmapToSD(newbitmap, destPath);
        newbitmap.recycle();
    }

    /** 缩放Bitmap图片 **/
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);// 利用矩阵进行缩放不会造成内存溢出
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    /** 获取屏幕宽度 **/
    public static int getScreenWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 设置状态栏透明效果
     */
    @TargetApi(19)
    public static void setTranslucentStatus(Activity activity, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


}
