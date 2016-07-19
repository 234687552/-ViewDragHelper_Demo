package com.example.administrator.apidemotest.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.apidemotest.R;
import com.example.administrator.apidemotest.db.ProjectDb;
import com.example.administrator.apidemotest.dialog.EditDialog;
import com.example.administrator.apidemotest.dialog.TypeDialog;
import com.example.administrator.apidemotest.model.Project;
import com.example.administrator.apidemotest.model.ProjectList;
import com.example.administrator.apidemotest.view.ItemView;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2016/7/11 0011.
 */
public class DetailProjectActivity extends Activity {
    private ListView detailList;
    private MyAdapter adapter;
    private ImageView newSchedule;
    private ProjectDb db;
    private ImageView detailIcon;
    private EditText detailProject;
    private Project curProject;
    private int projectId;
    private TypeDialog dialog;
    private int today;
    private EditText listInput;
    private List<ProjectList> lists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_project);
        //进入记录今天时间
        Calendar c = Calendar.getInstance();
        today = c.get(Calendar.YEAR) * 10000 + c.get(Calendar.MONTH) * 100 + c.get(Calendar.DAY_OF_MONTH);

        db = new ProjectDb(this);
        dialog = new TypeDialog();
        newSchedule = (ImageView) findViewById(R.id.new_list);
        detailList = (ListView) findViewById(R.id.detail_list);
        detailIcon = (ImageView) findViewById(R.id.detail_icon);
        detailProject = (EditText) findViewById(R.id.detail_project);
        listInput = (EditText) findViewById(R.id.list_input);
        init();
    }


    private void init() {
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
        //listview设置
        lists = db.getProjectLists(projectId);
        adapter = new MyAdapter(this, lists);
        detailList.setAdapter(adapter);
        //新建一个清单；
        newSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(listInput.getText())) {
                    ProjectList list = new ProjectList();
                    list.setListText(String.valueOf(listInput.getText()));
                    list.setProject_id(projectId);
                    list.setDay(today);
                    list.setTime(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)*100+Calendar.getInstance().get(Calendar.MINUTE));
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
    }

    private void refreshList() {
        lists.clear();
        lists.addAll(db.getProjectLists(projectId));
        adapter.notifyDataSetChanged();
    }

    //退出的时候自动保存
    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(detailProject.getText())) {
            curProject.setProjectText(String.valueOf(detailProject.getText()));
            db.updateProject(projectId, curProject);
        }
        super.onBackPressed();
    }

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
            view.findViewById(R.id.action_isfinish).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(DetailProjectActivity.this, "finish", Toast.LENGTH_SHORT).show();
                    view.closeExpand();
                }
            });
            view.setList(lists.get(position));
            view.editDialog.setOnSaveListener(new EditDialog.SaveListener() {
                @Override
                public void onSaveListener(boolean save) {
                    if (save){
                        refreshList();
                    }
                }
            });
            return view;
        }
    }
}

