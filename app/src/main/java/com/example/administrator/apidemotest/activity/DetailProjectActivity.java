package com.example.administrator.apidemotest.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.apidemotest.R;
import com.example.administrator.apidemotest.db.ProjectDb;
import com.example.administrator.apidemotest.dialog.AddMemberDialog;
import com.example.administrator.apidemotest.dialog.EditDialog;
import com.example.administrator.apidemotest.dialog.TypeDialog;
import com.example.administrator.apidemotest.model.Project;
import com.example.administrator.apidemotest.model.ProjectList;
import com.example.administrator.apidemotest.view.ConversationListView;
import com.example.administrator.apidemotest.view.ItemView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2016/7/11 0011.
 */
public class DetailProjectActivity extends Activity {
    private static final String TAG = "DetailProjectActivity";
    private static final int FINISH = 1;
    private static final int UNFINISH = 0;

    private String GROUPID;
    private ListView detailList;
    private MyAdapter adapter;
    private ImageView newSchedule;
    private ProjectDb db;
    private ImageView detailIcon;
    private EditText detailProject;
    private Project curProject;
    private int projectId;
    private TypeDialog dialog;
    private AddMemberDialog addMemberDialog;
    private int today;
    private EditText listInput;
    private List<ProjectList> lists;
    private ImageView dataInfo;
    private ViewPager container;
    private List<View> pageViews;
    private View frameLists;
    private View frameConversation;

    private TextView displayFinishDetails;

    private List<ProjectList> finishLists;
    private ListView finishDetails;
    private MyAdapter finishAdapter;

    private SwipeRefreshLayout swipeRefresh;


    private ConversationListView conversationList;

    private TextView userName;
    private EditText sendText;

    private LinearLayout dataProject;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        GROUPID="221619073581056428";
        //进入记录今天时间
        Calendar c = Calendar.getInstance();
        today = c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DAY_OF_MONTH);

        db = new ProjectDb(this);
        dialog = new TypeDialog();
        addMemberDialog=new AddMemberDialog();
        Bundle bundle = new Bundle();
        bundle.putString("groupId", GROUPID);
        addMemberDialog.setArguments(bundle);
        detailProject = (EditText) findViewById(R.id.detail_project);
        dataInfo = (ImageView) findViewById(R.id.data_info);
        container = (ViewPager) findViewById(R.id.container);
        detailIcon = (ImageView) findViewById(R.id.detail_icon);
        dataProject = (LinearLayout) findViewById(R.id.data_project);

        frameLists = LayoutInflater.from(this).inflate(R.layout.frame_lists, null);
        newSchedule = (ImageView) frameLists.findViewById(R.id.new_list);
        detailList = (ListView) frameLists.findViewById(R.id.details_list);
        listInput = (EditText) frameLists.findViewById(R.id.list_input);
        displayFinishDetails = (TextView) frameLists.findViewById(R.id.display_finish_details);
        finishDetails = (ListView) frameLists.findViewById(R.id.finish_details);

        frameConversation = LayoutInflater.from(this).inflate(R.layout.frame_coversation, null);
        swipeRefresh = (SwipeRefreshLayout) frameConversation.findViewById(R.id.swipe_refresh);
        conversationList = (ConversationListView) frameConversation.findViewById(R.id.conversation_list);
        conversationList.init(GROUPID);
        userName = (TextView) frameConversation.findViewById(R.id.user_name);
        userName.setText(EMClient.getInstance().getCurrentUser());
        sendText = (EditText) frameConversation.findViewById(R.id.send_text);

        init();
    }
    //新加成员
    public void addMember(View view){
        addMemberDialog.show(getFragmentManager(),"AddMemberDialog");
    }
    //发送消息
    public void send(View view) {
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id
        EMMessage message = EMMessage.createTxtSendMessage(sendText.getText().toString().trim(), "221619073581056428");
        message.setChatType(EMMessage.ChatType.GroupChat);
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        sendText.setText("");
        conversationList.refresh();
    }

    private void init() {
        //对话列表下滑刷新
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                conversationList.refreshWithoutSink();
                swipeRefresh.setRefreshing(false);
            }
        });
        //title设置
        projectId = getIntent().getIntExtra("project_id", 0);
        curProject = db.getProject(projectId);
        detailProject.setText(curProject.getProjectText());
        int resourceId = R.mipmap.icon_all;
        switch (curProject.getType()) {
            case "所有":
                break;
            case "生活":
                resourceId = R.mipmap.icon_live;
                break;
            case "学习":
                resourceId = R.mipmap.icon_study;
                break;
            case "工作":
                resourceId = R.mipmap.icon_work;
                break;
        }
        detailIcon.setBackgroundResource(resourceId);

        //viewpager 设置 坐标为list清单列表，右边为聊天；

        pageViews = new ArrayList<View>();
        pageViews.add(frameLists);
        pageViews.add(frameConversation);
        PagerAdapter pagerAdapter = new PagerAdapter() {

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ((ViewPager) container).addView(pageViews.get(position));
                return pageViews.get(position);
            }
        };
        container.setAdapter(pagerAdapter);
        container.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                conversationList.refresh();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //未完成listview设置
        lists = db.getProjectLists(projectId, UNFINISH);
        adapter = new MyAdapter(this, lists);
        detailList.setAdapter(adapter);
        setListViewHeightBasedOnChildren(detailList);
        //完成listview设置
        finishLists = db.getProjectLists(projectId, FINISH);
        finishAdapter = new MyAdapter(this, finishLists);
        finishDetails.setAdapter(finishAdapter);
        setListViewHeightBasedOnChildren(finishDetails);
        //切换显示和隐藏已完成项目
        displayFinishDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finishDetails.getVisibility() == View.GONE) {
                    finishDetails.setVisibility(View.VISIBLE);
                    displayFinishDetails.setText("隐藏已完成项目");
                } else {
                    finishDetails.setVisibility(View.GONE);
                    displayFinishDetails.setText("显示已完成项目" + finishLists.size());
                }
            }
        });
        //新建一个清单；
        newSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(listInput.getText())) {
                    ProjectList list = new ProjectList();
                    list.setListText(String.valueOf(listInput.getText()));
                    list.setProject_id(projectId);
                    list.setDay(today);
                    list.setTime(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) * 100 + Calendar.getInstance().get(Calendar.MINUTE));
                    listInput.setText("");
                    db.saveProjectList(list);
                    refreshList();
                } else {
                    Toast.makeText(DetailProjectActivity.this, "请输入具体清单", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //更改project的类型；
        detailIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show(getFragmentManager(), "TypeDialog");
                dialog.setTypeClickListener(new TypeDialog.TypeClickListener() {
                    @Override
                    public void onTypeClickListener(String type) {
                        curProject.setType(type);
                        int resourceId = R.mipmap.icon_all;
                        switch (curProject.getType()) {
                            case "所有":
                                break;
                            case "生活":
                                resourceId = R.mipmap.icon_live;
                                break;
                            case "学习":
                                resourceId = R.mipmap.icon_study;
                                break;
                            case "工作":
                                resourceId = R.mipmap.icon_work;
                                break;
                        }
                        detailIcon.setBackgroundResource(resourceId);
                    }
                });
            }
        });
        dataInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DatePickerDialog dpd = new DatePickerDialog(DetailProjectActivity.this, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                        curProject.setDay(year * 10000 + (monthOfYear + 1) * 100 + dayOfMonth);
//                    }
//                }, curProject.getDay() / 10000, curProject.getDay() % 10000 / 100 - 1, curProject.getDay() % 10000 % 100);
//                dpd.show();
                dataProject.setVisibility(dataProject.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                dataInfo.setBackgroundResource(dataProject.getVisibility() == View.VISIBLE ?R.mipmap.data_info_press:R.mipmap.data_info);
            }
        });


    }

    //刷新
    private void refreshList() {
        lists.clear();
        lists.addAll(db.getProjectLists(projectId, UNFINISH));
        adapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(detailList);

        finishLists.clear();
        finishLists.addAll(db.getProjectLists(projectId, FINISH));
        finishAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(finishDetails);

    }

    //退出的时候自动保存
    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(detailProject.getText())) {
            curProject.setProjectText(String.valueOf(detailProject.getText()));
            curProject.setIs_finish(lists.size() == 0 ? curProject.getIs_finish() : 1);
            for (int i = 0; i < lists.size(); i++) {
                if (lists.get(i).getIs_finish() == 0) {
                    curProject.setIs_finish(0);
                }
            }
            db.updateProject(projectId, curProject);
            super.onBackPressed();
        } else {
            Toast.makeText(DetailProjectActivity.this, "请输入项目事项", Toast.LENGTH_SHORT).show();
        }

    }

    //details里的adapter
    private class MyAdapter extends BaseAdapter {
        private Context mContext;
        private List<ProjectList> lists;

        public MyAdapter(Context context, List<ProjectList> lists) {
            mContext = context;
            this.lists = lists;
        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ItemView view;
            if (convertView == null) {
                view = new ItemView(mContext);
            } else {
                view = (ItemView) convertView;
            }
            view.forEditList();
            view.setList(lists.get(position));
            //设置监听子view是否展开，展开的话就让所有的Item都含有展开的Item；
            view.setOnExpandListener(new ItemView.OnExpandListener() {
                @Override
                public void ExpandListener(boolean isExpand) {
                    for (int i = 0; i < 20; i++) {
                        if (null != ((ItemView) detailList.getChildAt(i))) {
                            ((ItemView) detailList.getChildAt(i)).openItem = isExpand ? view : null;
                        }
                    }
                }
            });
            view.findViewById(R.id.action_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.closeExpand();
                    db.deleteProjectList(lists.get(position).getId());
                    refreshList();
                }
            });
            view.checkFinish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    lists.get(position).setIs_finish(isChecked ? 1 : 0);
                    db.updateProjectList(lists.get(position).getId(), lists.get(position));
                    refreshList();
                }
            });
            view.editDialog.setOnSaveListener(new EditDialog.SaveListener() {
                @Override
                public void onSaveListener(boolean save) {
                    if (save) {
                        refreshList();
                    }
                }
            });
            return view;
        }
    }

    //设置listview的高度不让其滚动
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


}

