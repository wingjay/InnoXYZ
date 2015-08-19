package com.innoxyz.ui.fragments.message;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.reflect.TypeToken;
import com.innoxyz.data.remote.AddressURIs;
import com.innoxyz.data.runtime.model.Pager;
import com.innoxyz.data.runtime.model.Protocol;
import com.innoxyz.data.runtime.model.message.Message;
import com.innoxyz.data.runtime.model.user.User;
import com.innoxyz.global.InnoXYZApp;
import com.innoxyz.network.URL;
import com.innoxyz.ui.activities.DetailActivity;
import com.innoxyz.ui.activities.NewActivity;
import com.innoxyz.ui.commands.ActivityCommand;
import com.innoxyz.ui.customviews.RefreshListView;
import com.innoxyz.ui.fragments.common.BaseFragment;
import com.innoxyz.ui.interfaces.OnRefreshListener;
import com.innoxyz.ui.utils.DateFunctions;

import java.util.ArrayList;
import java.util.List;

import com.innoxyz.R;
import com.innoxyz.util.Factory;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-16
 * Time: 上午10:38
 * To change this template use File | Settings | File Templates.
 */
public class MessageList extends BaseFragment  implements OnRefreshListener {

    private RefreshListView messageListView;
    private MessageListViewAdapter messageAdapter;
    //当前显示的项目
    List<Message> visibleMessageList;
    //收件箱
    List<Message> inMessageList;
    //发件箱
    List<Message> outMessageList;

    //网络请求参数
    RequestParams params;
    //请求第几页，从0开始
    int inPage = 0;
    int outPage = 0;
    //是否还有更多，允许继续加载标志
    boolean hasMoreInMessage = false;
    boolean hasMoreOutMessage = false;

    //与服务器通信数据对象
    Protocol<Message> theProtocol;
    Protocol<Message> protocol;
    Pager<Message> pager;

    //当前状态 刷新或加载
    private static final int NORMAL = 0;
    private static final int REFRESH = 1;
    private static final int LOAD = 2;
    //首次即进入刷新状态

    //当前的ActionBar，修改显示文字
    ActionBar actionBar;

    //当前是否为收件箱
    boolean inBoxFlag = true;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Activity a = getActivity();
        actionBar = a.getActionBar();

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // 创建下拉刷新
        messageListView = new RefreshListView(getActivity(), null);
        // 创建adapter
        messageAdapter = new MessageListViewAdapter();
        // 项目
        visibleMessageList = new ArrayList<>();
        inMessageList = new ArrayList<>();
        outMessageList = new ArrayList<>();
        // 设置网络请求参数
        params = new RequestParams();
        params.add("pageSize", "10");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_messagelist_home, container, false);
        messageListView = (RefreshListView) rootView.findViewById(R.id.messagelist_itemlist);
        messageListView.setOnRefreshListener(this);
        //添加Menu
        setHasOptionsMenu(true);
        //绑定adapter
        messageListView.setAdapter(messageAdapter);
        messageListView.setOnItemClickListener(new ClickToMessageListener());
        //首次进入刷新
        messageListView.startRefresh();
        //刷新收件箱
        updateInMessageList();

        return rootView;
    }

    //刷新收件箱
    private void updateInMessageList() {
        params.put("page", String.valueOf(inPage));

        Factory.getHttpClient().get(URL.LIST_MESSAGE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                theProtocol = new Protocol<Message>();
                protocol = theProtocol.fromJson(response, new TypeToken<Protocol<Message>>(){}.getType());
                if (protocol.isOk()) {
                    pager = protocol.getPager();
//                    hasMoreInMessage = pager.hasMore();
                    //如果是刷新，则清空；加载则不清空
                    if (messageListView.isRefreshing()) {
                        inMessageList.clear();
                    }
                    inMessageList.addAll(pager.getData());
                    //可见列表指向它
                    visibleMessageList = inMessageList;
                    //刷新视图
                    messageAdapter.notifyDataSetChanged();
                    //告诉listView的pager内容
                    messageListView.setDataPager(pager);
                } else {
                    Toast.makeText(getActivity(), "网络故障，请重试", Toast.LENGTH_SHORT).show();
                }
                //修改视图
                messageListView.hideHeaderFooter();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "网络连接失败。。。", Toast.LENGTH_SHORT).show();
                //可见列表指向它
                visibleMessageList = inMessageList;
                //修改视图
                messageListView.hideHeaderFooter();
            }
        });

    }

    //刷新发件箱
    private void updateOutMessageList() {
        params.put("page", String.valueOf(outPage));

        Factory.getHttpClient().get(URL.LIST_SENT_MESSAGE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                theProtocol = new Protocol<Message>();
                protocol = theProtocol.fromJson(response, new TypeToken<Protocol<Message>>(){}.getType());
                if (protocol.isOk()) {
                    pager = protocol.getPager();
//                    hasMoreOutMessage = pager.hasMore();
                    //如果是刷新，则清空；加载则不清空
                    if (messageListView.isRefreshing()) {
                        outMessageList.clear();
                    }
                    outMessageList.addAll(pager.getData());
                    //可见列表指向它
                    visibleMessageList = outMessageList;
                    //刷新视图
                    messageAdapter.notifyDataSetChanged();
                    //告诉listView的pager内容
                    messageListView.setDataPager(pager);
                } else {
                    Toast.makeText(getActivity(), "网络故障，请重试", Toast.LENGTH_SHORT).show();
                }
                //修改视图
                messageListView.hideHeaderFooter();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "网络连接失败。。。", Toast.LENGTH_SHORT).show();
                //可见列表指向它
                visibleMessageList = outMessageList;
                //修改视图
                messageListView.hideHeaderFooter();
            }
        });

    }

    //当下拉刷新时触发
    @Override
    public void onDownPullRefresh() {
        //刷新当前状态的第0页
        //收件箱
        if (inBoxFlag) {
            inPage = 0;
            updateInMessageList();
        }else {
            outPage = 0;
            updateOutMessageList();
        }
    }
    //当上拉加载时触发
    @Override
    public void onLoadingMore() {
        //收件箱
        if (inBoxFlag) {
            inPage++;
            updateInMessageList();
        }else {
            outPage++;
            updateOutMessageList();
        }
    }

    private class MessageListViewAdapter extends BaseAdapter {
        class ViewHolder{
            TextView message_item_username, message_item_subject, message_item_date;
            NetworkImageView message_user_avatar;
        }

        @Override
        public int getCount() {
            return visibleMessageList.size();
        }

        @Override
        public Object getItem(int position) {
            return visibleMessageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {
            if(view == null){
                LayoutInflater inflater = getActivity().getLayoutInflater();
                view = inflater.inflate(R.layout.listitem_message, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.message_item_username = (TextView)view.findViewById(R.id.message_item_username);
                viewHolder.message_item_subject = (TextView)view.findViewById(R.id.message_item_subject);
                viewHolder.message_item_date = (TextView)view.findViewById(R.id.message_item_date);
                viewHolder.message_user_avatar = (NetworkImageView)view.findViewById(R.id.message_user_avatar);
                view.setTag(viewHolder);
            }
            ViewHolder holder = (ViewHolder) view.getTag();

            //获取当前message
            Message message = visibleMessageList.get(i);
            //给该视图绑定数据Tag
            view.setTag(R.id.item_object, message);

            //显示主题 时间
            holder.message_item_subject.setText(message.getSubject());
            holder.message_item_date.setText(DateFunctions.RewriteDate(message.getSendTime(), "MM月dd日", "unknown"));
            //收件人/发件人类型，收（发）件人名字
            String messageUserName = "";

            if (inBoxFlag) {
                //收件箱
                messageUserName = message.getSenderName();
                //显示头像
                holder.message_user_avatar.setImageUrl(AddressURIs.getUserAvatarURL(message.getSenderId()), InnoXYZApp.getApplication().getImageLoader());
            }else {
                //发件箱
                if (message.getRecivers() != null) {
                    //如果只有一个收件人，则显示收件人头像，否则显示默认头像
                    if (1 == message.getRecivers().size()) {
                        messageUserName += message.getRecivers().get(0).getRealName();
                        holder.message_user_avatar.setImageUrl(AddressURIs.getUserAvatarURL(message.getRecivers().get(0).getId()), InnoXYZApp.getApplication().getImageLoader());
                    }else {
                        for (User user : message.getRecivers()) {
                            messageUserName += user.getRealName() + ";";
                        }
                        holder.message_user_avatar.setImageResource(R.drawable.icon_group);
                    }

                }

            }

            //显示名字
            holder.message_item_username.setText(messageUserName);

            return view;
        }
    }

    //点击进入邮件详情，检测是否处于刷新状态，若是，则屏蔽本事件
    private class ClickToMessageListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (!messageListView.isHeaderHidden())
                return;
            //获取当前View中绑定的Tag数据-即该message数据
            Message message = (Message)view.getTag(R.id.item_object);
            //跳转到DetailActivity中的MessageHome中
            Bundle bundle = new Bundle();
            bundle.putString("subject", message.getSubject());
            bundle.putString("senderName", message.getSenderName());
            bundle.putString("sendTime", message.getSendTime());
            bundle.putString("content", message.getContent());
            String recivers = "";
            if (message.getRecivers() != null) {
                for (User user : message.getRecivers()) {
                    recivers += user.getRealName() + ";";
                }
            }
            bundle.putString("recivers", recivers);
            bundle.putBoolean("inBox", inBoxFlag);
            new ActivityCommand(DetailActivity.class, MessageHome.class, MessageList.this.getActivity(), bundle, null).Execute();
        }
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.message_fragment_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_add:
                new ActivityCommand(NewActivity.class, MessageNew.class, MessageList.this.getActivity(), null, null).Execute();
                break;
            case R.id.action_messagebox_switch:
                inBoxFlag = !inBoxFlag;
                if (inBoxFlag) {
                    //收件箱
                    actionBar.setTitle(R.string.title_message_inbox);
                    //设置图标
                    item.setIcon(R.drawable.mail_sent);


                    //刷新第一页
                    inPage = 0;
                    messageListView.startRefresh();
                    updateInMessageList();
                } else {
                    //发件箱
                    actionBar.setTitle(R.string.title_message_outbox);
                    //设置图标
                    item.setIcon(R.drawable.mail_inbox);

                    //刷新第一页
                    outPage = 0;
                    messageListView.startRefresh();
                    updateOutMessageList();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
