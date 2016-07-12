package com.example.administrator.apidemotest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Created by Administrator on 2016/7/11 0011.
 */
public class DetailProjectActivity extends Activity {
    private ItemView detailProject;
    private ListView detailList;
    private MyAdapter adapter;
    private ImageView newSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_project);
        newSchedule = (ImageView) findViewById(R.id.new_schedule);
        newSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailList.invalidate();
            }
        });
        detailProject = (ItemView) findViewById(R.id.detail_project);
        detailProject.forEdit();
        detailList = (ListView) findViewById(R.id.detail_list);
        adapter=new MyAdapter(this);
        detailList.setAdapter(adapter);

    }
    private class MyAdapter extends BaseAdapter {
        private Context mContext;
        public  MyAdapter(Context context){
            mContext=context;
        }
        @Override
        public int getCount() {
            return 20;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemView view= (ItemView) convertView;
            if (view==null){
                view=new ItemView(mContext);
            }
            view.forEditList();
            return view;
        }
    }

}
