package com.example.administrator.apidemotest.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.example.administrator.apidemotest.model.Project;
import com.example.administrator.apidemotest.view.ItemView;

import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ImageView newProject;
    private EditText projectInput;


    private ListView listView;
    private MyAdapter adapter;
    private List<Project> projects;
    private ProjectDb db;
    private int today;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        //进入记录今天时间
        Calendar c = Calendar.getInstance();
        today=c.get(Calendar.YEAR)*10000+c.get(Calendar.MONTH)*100+c.get(Calendar.DAY_OF_MONTH);

        db=new ProjectDb(this);
        projects=db.getProjects("所有");

        listView = (ListView) findViewById(R.id.list_view);
        adapter=new MyAdapter(MainActivity.this, projects);
        listView.setAdapter(adapter);
        newProject = (ImageView) findViewById(R.id.new_project);
        projectInput = (EditText) findViewById(R.id.project_input);
        newProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(projectInput.getText())){
                    Project project=new Project();
                    project.setProjectText(String.valueOf(projectInput.getText()));
                    project.setDay(today);
                    //默认类型为所有；
                    project.setType("学习");
                    projectInput.setText("");
                    db.saveProject(project);
                    refreshProject("所有");
                }else {
                    Toast.makeText(MainActivity.this, "请输入具体事项", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        refreshProject("所有");
        super.onResume();
    }

    /**
     * 根据类型刷新数据；
     * @param type
     */
    private void refreshProject(String type){
        projects.clear();
        projects.addAll(db.getProjects(type));
        adapter.notifyDataSetChanged();

    }
    private class MyAdapter extends BaseAdapter {
        private Context mContext;
        private List<Project> projects;
        public MyAdapter(Context context,List<Project> projects) {
            mContext = context;
            this.projects=projects;
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
            }
            else {
                view= (ItemView) convertView;
            }
            //设置监听子view是否展开，展开的话就让所有的Item都含有展开的Item；
            view.setOnExpandListener(new ItemView.OnExpandListener() {
                @Override
                public void ExpandListener(boolean isExpand) {
                    for (int i = 0; i < 20; i++) {
                        if (null != ((ItemView) listView.getChildAt(i))) {
                            ((ItemView) listView.getChildAt(i)).openItem = isExpand ? view : null;
                        }
                    }
                }
            });
            view.findViewById(R.id.action_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.closeExpand();
                    db.deleteProject(projects.get(position).getId());
                    refreshProject("所有");
                }
            });
            view.findViewById(R.id.action_isfinish).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "finish", Toast.LENGTH_SHORT).show();
                    view.closeExpand();
                }
            });
            view.setProject(projects.get(position));
            return view;
        }

    }

}


