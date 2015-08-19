package com.innoxyz.ui.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.innoxyz.data.remote.AddressURIs;
import com.innoxyz.data.remote.StringRequestBuilder;
import com.innoxyz.data.remote.response.JsonResponseHandler;
import com.innoxyz.data.runtime.SimpleObservedData;
import com.innoxyz.data.runtime.beans.message.MessageCount;
import com.innoxyz.data.runtime.beans.notify.NotifyCount;
import com.innoxyz.data.runtime.interfaces.IDataObserver;
import com.innoxyz.global.InnoXYZApp;

import java.util.concurrent.TimeUnit;

/**
 * Created by laborish on 14-1-16.
 */
// 后台运行，向服务器获取未读数
// 获取到的数据以广播形式发送（未读消息数 和 未读私信数）。通过设定action，指定类型的BroadcastReceiver才可接收广播
public class GetUnreadNumService extends Service {

    public static final String UNREAD_COUNT ="com.innoxyz.InnoXYZAndroid.broadcast.UNREAD_COUNT";

    public static final String UNREAD_NOTIFIES ="com.innoxyz.InnoXYZAndroid.broadcast.UNREAD_NOTIFIES";
    public static final String UNREAD_MAILS ="com.innoxyz.InnoXYZAndroid.broadcast.UNREAD_MAILS";

    private SimpleObservedData<NotifyCount> unReadNotifies;
    private SimpleObservedData<MessageCount> unReadMails;

    private ScanningThread runner;

    @Override
    public void onCreate() {

        unReadNotifies = new SimpleObservedData<>(new NotifyCount());
        unReadMails = new SimpleObservedData<>(new MessageCount());

        //注册 SimpleObservedData，往里面添加一个observer实例。
        //后面会发送http请求获取未读消息数，在OnResponse中，把获取的数据以NotifyCount类的格式存入到这个observer中。如果还注册了其他Observer，则会把同样的数据存入所有的observer中。
        //此处的流程是，创建一个 SimpleObservedData<NotifyCount> 实例 unReadNotifies，注册一个observer并复写update方法。然后启动工作线程去请求服务器对应数据，利用JsonResponseHandler
        //来获取数据并转化为指定的类，然后在observer中更新，回调下面的update方法，对获取到的数据进行后续操作
        unReadNotifies.registerObserver(new IDataObserver() {
            @Override
            public void update(Object o) {
                //从服务器获取数据对象 o 后，先向下转型为目标类，然后获取里面的数据，进一步操作。
                //此处是将未读消息数以广播形式将数据发送出去。
                int count = ((NotifyCount) o).total;

                Intent i = new Intent();
                //设置Action，只有Intent-Filter属性里包括一个值为UNREAD_COUNT的action的组件才能获取这个Intent
                i.setAction(UNREAD_COUNT);
                //把未读通知数存入intent中
                i.putExtra(UNREAD_NOTIFIES, count);

                sendBroadcast(i);
            }
        });
        unReadMails.registerObserver(new IDataObserver() {
            @Override
            public void update(Object o) {

                int count = ((MessageCount)o).unreads;

                Intent i = new Intent();
                i.setAction(UNREAD_COUNT);
                //未读私信数
                i.putExtra(UNREAD_MAILS, count);

                sendBroadcast(i);
            }
        });


        startThread();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        InnoXYZApp.getApplication().getGetUnreadNumNow().release();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopThread();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //一直扫描未读消息数线程知道服务被关闭
    class ScanningThread extends Thread{
        private volatile boolean stop =  false;

        public void run(){
            while(!stop){
                try{
                    //wait Semaphore or timeout
                    if( InnoXYZApp.getApplication().getGetUnreadNumNow().tryAcquire(1, 30, TimeUnit.SECONDS) ){
                        //InnoXYZApp.getApplication().getGetUnreadNumNow().acquire();
                    }
                }
                catch (InterruptedException e){

                }
                //去服务器获取未读消息数
                new StringRequestBuilder(null).setRequestInfo(AddressURIs.LIST_NOTIFY_COUNT)
                        .addParameter("read", "false")
                        .setOnResponseListener(new JsonResponseHandler(unReadNotifies, NotifyCount.class, null))
                        .request();

                //cannot mark a unread email to read, so comment this
//                new StringRequestBuilder(null).setRequestInfo(AddressURIs.LIST_MESSAGE_COUNT)
//                        .addParameter("read", "false")
//                        .setOnResponseListener(new JsonResponseHandler(unReadMails, MessageCount.class, null))
//                        .request();

            }
        }
        //停止请求数据线程
        public synchronized void requestStop() {
            stop = true;
        }
    }

    public synchronized void startThread(){
        if(runner == null){
            runner = new ScanningThread();
            runner.start();
        }
    }

    public synchronized void stopThread(){
        if(runner != null){
            runner.requestStop();
            runner = null;
        }
    }

}
