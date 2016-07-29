package com.example.administrator.apidemotest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.apidemotest.R;
import com.example.administrator.apidemotest.db.ProjectDb;
import com.example.administrator.apidemotest.model.Project;
import com.example.administrator.apidemotest.model.ProjectList;
import com.example.administrator.apidemotest.view.ItemView;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ImageView newProject;
    private EditText projectInput;
    private static int FINISH = 1;
    private static int UNFINISH = 0;
    private String selectType = "所有";
    private ListView listView;
    //未完成的所有projects
    private List<Project> projects;
    private MyAdapter adapter;
    //完成的所有projects
    private MyAdapter finishAdapter;
    private List<Project> finishProjects;
    private ProjectDb db;
    private int today;
    private TextView displayFinish;
    private ListView finishList;

    public void logout(View view) {
        //此方法为异步方法
        EMClient.getInstance().logout(false, new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub

            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        newProject = (ImageView) findViewById(R.id.new_project);
        projectInput = (EditText) findViewById(R.id.project_input);
        listView = (ListView) findViewById(R.id.list_view);
        displayFinish = (TextView) findViewById(R.id.display_finish);

        //进入记录今天时间
        Calendar c = Calendar.getInstance();
        today = c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DAY_OF_MONTH);
        //获取数据库操作
        db = new ProjectDb(this);

        // 显示未完成项目
        projects = db.getProjects(selectType, UNFINISH);
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new MyAdapter(MainActivity.this, projects);
        listView.setAdapter(adapter);
        setListViewHeightBasedOnChildren(listView);
        // 显示已经完成项目
        finishProjects = db.getProjects(selectType, FINISH);
        finishList = (ListView) findViewById(R.id.finish_list);
        finishAdapter = new MyAdapter(MainActivity.this, finishProjects);
        finishList.setAdapter(finishAdapter);
        setListViewHeightBasedOnChildren(finishList);
        //切换显示和隐藏已完成项目
        displayFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finishList.getVisibility() == View.GONE) {
                    finishList.setVisibility(View.VISIBLE);
                    displayFinish.setText("隐藏已完成项目");
                } else {
                    finishList.setVisibility(View.GONE);
                    displayFinish.setText("显示已完成项目" + finishProjects.size());
                }
            }
        });
        //新增project
        newProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(projectInput.getText())) {
                    Project project = new Project();
                    project.setIs_finish(UNFINISH);
                    project.setProjectText(String.valueOf(projectInput.getText()));
                    project.setDay(today);
                    //默认类型为所有；
                    project.setType("学习");
                    projectInput.setText("");
                    db.saveProject(project);
                    refreshProject();
                } else {
                    Toast.makeText(MainActivity.this, "请输入具体事项", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //在清单页面退出回到主页面的时候刷新数据
    @Override
    protected void onResume() {
        refreshProject();
        super.onResume();
    }

    /**
     * 根据类型刷新数据；
     */
    private void refreshProject() {
        projects.clear();
        projects.addAll(db.getProjects(selectType, UNFINISH));
        adapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(listView);
        finishProjects.clear();
        finishProjects.addAll(db.getProjects(selectType, FINISH));
        finishAdapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(finishList);


    }

    private class MyAdapter extends BaseAdapter {
        private Context mContext;
        private List<Project> projects;

        public MyAdapter(Context context, List<Project> projects) {
            mContext = context;
            this.projects = projects;
        }

        @Override
        public int getCount() {
            return projects.size();
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
            view.setProject(projects.get(position));
            //获取porject下面的清单完成条数；
            final List<ProjectList> finishLists = db.getProjectLists(projects.get(position).getId(), FINISH);
            final List<ProjectList> unfinishLists = db.getProjectLists(projects.get(position).getId(), UNFINISH);

            if (finishLists.size() + unfinishLists.size() == 0) {
                view.sumFinish.setText("");
            } else {
                view.sumFinish.setText(finishLists.size() + "/" + unfinishLists.size());
            }


            //设置监听子view是否展开，展开的话就让所有的Item都含有展开的Item；
            view.setOnExpandListener(new ItemView.OnExpandListener() {
                @Override
                public void ExpandListener(boolean isExpand) {

                    for (int i = 0; i < 20; i++) {
                        if (null != ((ItemView) listView.getChildAt(i))) {
                            ((ItemView) listView.getChildAt(i)).openItem = isExpand ? view : null;
                        }
                        if (null != ((ItemView) finishList.getChildAt(i))) {
                            ((ItemView) finishList.getChildAt(i)).openItem = isExpand ? view : null;
                        }
                    }
                }
            });
            view.findViewById(R.id.action_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.closeExpand();
                    db.deleteProject(projects.get(position).getId());
                    refreshProject();
                }
            });
            view.findViewById(R.id.action_isfinish).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "finish", Toast.LENGTH_SHORT).show();
                    view.closeExpand();
                    projects.get(position).setIs_finish(FINISH);
                    db.updateProject(projects.get(position).getId(), projects.get(position));
                    refreshProject();
                    for (ProjectList list : unfinishLists) {
                        list.setIs_finish(FINISH);
                        db.updateProjectList(list.getId(), list);
                    }
                }
            });
            return view;
        }

    }

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


