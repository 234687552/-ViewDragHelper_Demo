package com.example.administrator.apidemotest;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 提供设置修改计划，修改
 */
public class ItemView extends LinearLayout implements View.OnTouchListener {
    private LinearLayout contentView;
    private ImageView contentImg;
    private TextView contentSchedule;
    private LinearLayout actionView;
    private TextView actionIsfinish;
    private TextView actionDelete;
    private DragLayout dragLayout;
    private TextView contentScheduleEdit;
    private CheckBox checkFinish;
    private TextView time;
    private TextView sumFinish;

    private OnExpandListener listener;
    private boolean isClick = true;
    private boolean isEdit = false;
    public ItemView openItem =null;

    public ItemView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.item_view, this);
        init();
    }

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_view, this);
        init();
    }


    private void init() {


        time = (TextView) findViewById(R.id.time);
        sumFinish = (TextView) findViewById(R.id.sum_finish);
        checkFinish = (CheckBox) findViewById(R.id.check_finish);
        contentScheduleEdit = (TextView) findViewById(R.id.content_schedule_edit);
        dragLayout = (DragLayout) findViewById(R.id.drag_layout);
        contentView = (LinearLayout) findViewById(R.id.content_view);
        contentImg = (ImageView) findViewById(R.id.content_img);
        contentSchedule = (TextView) findViewById(R.id.content_schedule);
        actionView = (LinearLayout) findViewById(R.id.action_view);
        actionIsfinish = (TextView) findViewById(R.id.action_isfinish);
        actionDelete = (TextView) findViewById(R.id.action_delete);

        contentView.setOnTouchListener(this);
        actionIsfinish.setOnTouchListener(this);
        actionDelete.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        Log.w("当前的View", this+"" );
        if (null!=listener&&openItem !=null&& openItem !=this){
            openItem.closeExpand();
            listener.ExpandListener(false);
            openItem=null;
            return false;
        }

        switch (v.getId()) {
            case R.id.action_isfinish:
                dragLayout.CloseExpand();
                Toast.makeText(getContext(), "finish", Toast.LENGTH_SHORT).show();
                break;
            case R.id.content_view:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        isClick = false;//防止与滑动冲突
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isClick ) {
                            if (contentView.getLeft() >= 0 &&!isEdit) {
                                Intent intent = new Intent(getContext(), DetailProjectActivity.class);
                                getContext().startActivity(intent);
                            } else {
                                dragLayout.CloseExpand();
                            }
                        }
                        isClick = true;
                        break;
                }
                break;
            case R.id.action_delete:
                dragLayout.CloseExpand();
                Toast.makeText(getContext(), "delete", Toast.LENGTH_SHORT).show();
                break;
        }
        if (null!=listener&&event.getAction()==MotionEvent.ACTION_UP&&dragLayout.isExpand){
            listener.ExpandListener(true);//发出通知 我是展开状态
        }
        return true;
    }


    public void forEdit() {

        isEdit = true;
        contentScheduleEdit.setVisibility(VISIBLE);
        contentSchedule.setVisibility(GONE);
    }

    public void forEditList() {
        isEdit = true;
        actionIsfinish.setVisibility(GONE);
        checkFinish.setVisibility(VISIBLE);
        contentImg.setVisibility(GONE);
        sumFinish.setText("09:21");
    }
    public void closeExpand(){
        dragLayout.CloseExpand();
    }

    public void setOnExpandListener(OnExpandListener listener){
        this.listener=listener;
    }
    public interface OnExpandListener{
        public void ExpandListener(boolean isExpand);
    }
}
