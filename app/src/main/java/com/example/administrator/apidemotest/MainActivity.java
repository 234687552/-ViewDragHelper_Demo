package com.example.administrator.apidemotest;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;


public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private MyAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        adapter=new MyAdapter(this);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

    private class MyAdapter extends BaseAdapter{
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
            Log.w("getView", position+","+view );
            return view;
        }
    }
}


