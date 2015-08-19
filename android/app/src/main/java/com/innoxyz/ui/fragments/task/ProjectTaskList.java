package com.innoxyz.ui.fragments.task;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.innoxyz.data.runtime.model.Pager;
import com.innoxyz.data.runtime.model.Protocol;
import com.innoxyz.data.runtime.model.task.Task;
import com.innoxyz.data.runtime.model.user.User;
import com.innoxyz.network.URL;
import com.innoxyz.ui.activities.DetailActivity;
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
 * Created with yj
 * User: InnoXYZ
 * Date: 15-1-29
 * Time: 下午4:33
 */
public class ProjectTaskList extends BaseFragment implements OnRefreshListener {
    //显示项目的所有通知
    List<Task> taskList;
    RefreshListView taskListView;
    TaskListViewAdapter  taskAdapter;

    //网络请求参数-任务回复
    private RequestParams params = new RequestParams();
    //项目信息
    String projectName;
    String projectId;
    //获取页数
    private int page = 0;
    private Protocol<Task> theProtocol;
    private Protocol<Task> protocol;
    private Pager<Task> pager;

    ActionBar actionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getActivity().getActionBar();
        //获取参数
        projectName = getArguments().getString("projectName");
        projectId = String.valueOf(getArguments().getInt("projectId"));
        //初始化参数
        taskListView = new RefreshListView(getActivity(), null);
        taskAdapter = new TaskListViewAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);
        if (actionBar != null) {
            actionBar.setTitle(R.string.title_task_list);
        }

        View rootview = inflater.inflate(R.layout.project_announcement_list, container, false);
        taskListView = (RefreshListView) rootview.findViewById(R.id.project_announcement_listview);

        //初始化数据参数，如果在onCreate中初始化，当页面切换到A，再回来本页面时，会发生数据重复
        taskList = new ArrayList<>();

        //绑定下拉刷新监听
        taskListView.setOnRefreshListener(this);
        //绑定adapter
        taskListView.setAdapter(taskAdapter);

        //每个item绑定点击监听
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //如果处于刷新状态，则不响应
                if (taskListView.isRefreshing())
                    return;
                //进入任务详情
                //以后可优化为直接传递对象 序列化传递,参考ProjectAnnouncementList.java
                Task task = (Task) view.getTag(R.id.item_object);
                Bundle bundle = new Bundle();
                bundle.putAll(getArguments());
                bundle.putString("taskId", task.getId());
                bundle.putString("taskName", task.getName());
                bundle.putInt("creatorId", task.getCreatorId());
                bundle.putString("creatorName", task.getCreatorName());
                String assignees = "";
                if (task.getAssignees() != null && task.getAssignees().size() > 0) {
                    for(User u : task.getAssignees()) {
                        assignees += u.getName() + " ";
                    }
                }
                bundle.putString("assignees", assignees);
                bundle.putString("priority", task.getPriority().toString());
                bundle.putString("description", task.getDescription());
                bundle.putString("deadline", task.getDeadline());
                bundle.putString("state", task.getState().toString());
                new ActivityCommand(DetailActivity.class, TaskHome.class, ProjectTaskList.this.getActivity(), bundle, null).Execute();

            }
        });
        //启动刷新
        taskListView.startRefresh();
        //显示任务列表
        showTaskList();

        return rootview;

    }


    //显示任务列表
    private void showTaskList() {
        //参数
        params.put("hostId", projectId);
        params.put("page", String.valueOf(page));

        Factory.getHttpClient().get(URL.LIST_TASK_IN_LPROJECT, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                theProtocol = new Protocol<>();
                protocol = theProtocol.fromJson(response, new TypeToken<Protocol<Task>>(){}.getType());
                if (protocol.isOk()) {
                    pager = protocol.getPager();
                    if (taskListView.isRefreshing()) {
                        taskList.clear();
                    }
                    taskList.addAll(pager.getData());
                    //刷新数据
                    taskAdapter.notifyDataSetChanged();
                    taskListView.setDataPager(pager);
                }else {
                    Toast.makeText(getActivity(), "数据获取错误!", Toast.LENGTH_SHORT).show();
                }
                //隐藏刷新/加载头
                taskListView.hideHeaderFooter();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "网络连接失败，请检查网络连接！", Toast.LENGTH_SHORT).show();
                //隐藏刷新/加载头
                taskListView.hideHeaderFooter();
            }
        });

    }

    @Override
    public void onDownPullRefresh() {
        page = 0;
        showTaskList();
    }

    @Override
    public void onLoadingMore() {
        page ++;
        showTaskList();
    }



    private class TaskListViewAdapter extends BaseAdapter {

        //子控件
        class ViewHolder{
            TextView task_item_name, task_item_assignee, task_item_duedate, task_item_status;
        }
        @Override
        public int getCount() {
            return taskList.size();
        }

        @Override
        public Object getItem(int position) {
            return taskList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                view = inflater.inflate(R.layout.listitem_task, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.task_item_name = (TextView)view.findViewById(R.id.task_item_name);
                viewHolder.task_item_assignee = (TextView)view.findViewById(R.id.task_item_assignee);
                viewHolder.task_item_duedate = (TextView)view.findViewById(R.id.task_item_duedate);
                viewHolder.task_item_status = (TextView)view.findViewById(R.id.task_item_status);

                view.setTag(viewHolder);
            }

            ViewHolder holder = (ViewHolder) view.getTag();

            //存储当成item数据
            Task task = taskList.get(position);
            view.setTag(R.id.item_object, task);

            //任务名称
            holder.task_item_name.setText(task.getName());
            //指派
            String assignees = "";
            if (task.getAssignees() != null && task.getAssignees().size() != 0) {
                for(User u : task.getAssignees()) {
                    assignees += u.getName() + " ";
                }
            }
            holder.task_item_assignee.setText(
                    getResources().getString(R.string.template_taskitem_assignees).
                            replace("%assignees%", assignees)
            );
            holder.task_item_duedate.setText(
                            getResources().getString(R.string.template_taskitem_duedate).
                                    replace("%duedate%", DateFunctions.RewriteDate(task.getDeadline(), "yy-M-d", "unknown"))
            );

            holder.task_item_status.setText(task.getState().getText());


            return view;
        }
    }
}
