package com.innoxyz.ui.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.innoxyz.R;
import com.innoxyz.global.InnoXYZApp;
import com.innoxyz.network.URL;
import com.innoxyz.util.Factory;
import com.innoxyz.util.Util;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HeaderElement;

import java.io.File;

/**
 * Created by yj on 15-1-18.
 * 文件下载图标，自动检查本地是否存在显示不同图标，支持下载
 * 注意：根据ID来打开文件；
 * SD卡上对应文件的文件名是根据下载时的header中 content-disposition 来获取
 */
public class FileDownloadImageView extends ImageView {

    private final static String HEADER_FILE_REAL_NAME = "Content-Disposition";

    private Context mContext;
    private File mFile;
    private String mFileId;
    private String mFileName;

    //下载后获取的完整文件名
    private String realFileName = "";

    public FileDownloadImageView(Context context) {
        super(context);
    }
    //必须要加，才能在视图初始化时初始化本实例
    public FileDownloadImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //必须初始化
    public void setInit(Context context, String fileId, String fileName){
        this.mContext = context;
        this.mFileId = fileId;
        this.mFileName = fileName;
        init();
    }

    private void init() {
        //先检查是否处于下载中
        if (InnoXYZApp.getApplication().containDownloadingAttachID(mFileId)) {
            //显示下载进度条
            setLoading();
            setScaleType(ScaleType.FIT_CENTER);
            return;
        }
        //不处于下载中，则检查是否存在
        mFile = Util.isFileExistsById(mFileId);
        if (mFile != null) {
            //文件存在，查看
            this.setImageResource(R.drawable.attachment_eye);
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.displayFile(mContext, mFile);
                }
            });
        }else {
            //文件不存在，下载
            this.setImageResource(R.drawable.attachment_download);
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //显示下载进度条
                    setLoading();
                    //正在下载
                    InnoXYZApp.getApplication().addDownloadingAttachID(mFileId);
                    //执行下载
                    RequestParams params = new RequestParams();
                    params.put("id", mFileId);
                    params.put("type", "document");
                    params.put("download", "true");
                    Factory.getHttpClient().get(URL.GET_FILE, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            //停止下载图标
                            stopLoading();
                            if (bytes.length != 0) {
                                //获取完整文件名并存入SD卡
                                for (Header header : headers) {
                                    if (header.getName().equalsIgnoreCase(HEADER_FILE_REAL_NAME)) {
                                        String oldFileName = header.getValue();
                                        //解决中文乱码问题
                                        try {
                                            oldFileName = new String(oldFileName.getBytes("ISO-8859-1"), "GBK");
                                        }catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        oldFileName = oldFileName.replace("filename=", "")
                                                .replaceAll("\"", "");
                                        //获取文件名,出去前后的 " 符号
                                        realFileName += oldFileName;
                                        break;
                                    }
                                }
                                //使用完整名存储
                                boolean saveFile = Util.savaFileFromBinary(bytes, realFileName, mFileId, mContext);
                                if (saveFile) {
                                    //文件存在，查看
                                    setImageResource(R.drawable.attachment_eye);
                                    setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mFile = Util.isFileExistsById(mFileId);
                                            if (mFile != null) {
                                                Util.displayFile(mContext, mFile);
                                            }else {
                                                Toast.makeText(mContext, "文件不存在！", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                    Toast.makeText(mContext, mFileName + ":下载成功！", Toast.LENGTH_SHORT).show();
                                } else {
                                    //未下载
                                    setImageResource(R.drawable.attachment_download);
                                }
                            } else {
                                Toast.makeText(mContext, mFileName + ":下载失败！", Toast.LENGTH_SHORT).show();
                                //未下载
                                setImageResource(R.drawable.attachment_download);
                            }
                            //结束下载
                            InnoXYZApp.getApplication().removeDownloadingAttachID(mFileId);
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                            //停止下载图标
                            stopLoading();
                            Toast.makeText(mContext, "网络连接失败", Toast.LENGTH_SHORT).show();
                            setImageResource(R.drawable.attachment_download);
                            //结束下载
                            InnoXYZApp.getApplication().removeDownloadingAttachID(mFileId);
                        }
                    });
                }
            });
        }

    }

    //由于默认无法显示gif图片，为显示下载中图标旋转效果，要设置图片旋转
    private void setLoading() {
        Animation operatingAnim = AnimationUtils.loadAnimation(mContext, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        setImageResource(R.drawable.loading);
        startAnimation(operatingAnim);
    }

    private void stopLoading() {
        clearAnimation();
    }

}
