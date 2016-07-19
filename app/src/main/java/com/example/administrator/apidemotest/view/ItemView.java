package com.example.administrator.apidemotest.view;

import android.app.Activity;
import android.app.FragmentTransaction;
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

import com.example.administrator.apidemotest.activity.DetailProjectActivity;
import com.example.administrator.apidemotest.R;
import com.example.administrator.apidemotest.dialog.EditDialog;
import com.example.administrator.apidemotest.model.Project;
import com.example.administrator.apidemotest.model.ProjectList;

import java.util.Calendar;

/**
 * 提供设置修改计划，修改
 */
public class ItemView extends LinearLayout implements View.OnTouchListener {
    private LinearLayout contentView;
    private ImageView contentImg;
    private TextView contentSchedule;
    private ImageView actionIsfinish;
    private ImageView actionDelete;
    private DragLayout dragLayout;
    private TextView contentScheduleEdit;
    private CheckBox checkFinish;
    private TextView day;
    private TextView sumFinish;

    private OnExpandListener listener;
    private boolean isClick = true;
    private boolean isEdit = false;
    public ItemView openItem = null;
    private int projectId;
    private int listId;

    public EditDialog editDialog;

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

        editDialog = new EditDialog();

        day = (TextView) findViewById(R.id.day);
        sumFinish = (TextView) findViewById(R.id.sum_finish);
        checkFinish = (CheckBox) findViewById(R.id.check_finish);
        contentScheduleEdit = (TextView) findViewById(R.id.content_schedule_edit);
        dragLayout = (DragLayout) findViewById(R.id.drag_layout);
        contentView = (LinearLayout) findViewById(R.id.content_view);
        contentImg = (ImageView) findViewById(R.id.content_img);
        contentSchedule = (TextView) findViewById(R.id.content_schedule);
        actionIsfinish = (ImageView) findViewById(R.id.action_isfinish);
        actionDelete = (ImageView) findViewById(R.id.action_delete);

        contentView.setOnTouchListener(this);
        actionIsfinish.setOnTouchListener(this);
        actionDelete.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (null != listener && openItem != null && openItem != this) {
            openItem.closeExpand();
            return false;
        }

        switch (v.getId()) {
            case R.id.action_isfinish:
                return false;
            case R.id.action_delete:
                return false;
            case R.id.content_view:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        isClick = false;//防止与滑动冲突
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isClick) {
                            if (contentView.getLeft() >= 0 && !isEdit) {
                                //进入清单列表
                                Intent intent = new Intent(getContext(), DetailProjectActivity.class);
                                intent.putExtra("project_id", projectId);
                                getContext().startActivity(intent);
                            } else if (contentView.getLeft() >= 0 && isEdit) {
                                //进入备注dialog
                                Activity activity = (Activity) getContext();

                                editDialog.setListId(listId);
                                FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
                                ft.remove( editDialog);
                                ft.commit();
                                editDialog.show(activity.getFragmentManager(), "EditDialog");
//                                Log.w("detailActivity", String.valueOf(editDialog.listText.getText()) + "," + String.valueOf(editDialog.listRemark.getText()));

                            } else {
                                closeExpand();
                            }
                        }
                        isClick = true;
                        break;
                }
                break;

        }
        if (null != listener && event.getAction() == MotionEvent.ACTION_UP && dragLayout.isExpand) {
            listener.ExpandListener(true);//发出通知 我是展开状态
        } else if (null != listener && event.getAction() == MotionEvent.ACTION_UP && !dragLayout.isExpand) {
            listener.ExpandListener(false);
        }
        return true;
    }

    //设置project内容
    public void setProject(Project project) {
        projectId = project.getId();
        //设置内容
        contentSchedule.setText(project.getProjectText());
        //设置type
        int resourceId = R.mipmap.icon_all;
        switch (project.getType()) {
            case "所有":
                resourceId = R.mipmap.icon_all;
                break;
            case "工作":
                resourceId = R.mipmap.icon_work;
                break;
            case "生活":
                resourceId = R.mipmap.icon_live;
                break;
            case "学习":
                resourceId = R.mipmap.icon_study;
                break;
        }
        contentImg.setBackgroundResource(resourceId);
        //设置时间
        String day;
        Calendar c = Calendar.getInstance();
        int today = c.get(Calendar.YEAR) * 10000 + c.get(Calendar.MONTH) * 100 + c.get(Calendar.DAY_OF_MONTH);
        if (project.getDay() == today) {
            day = "今天";
        } else if (project.getDay() == today + 1) {
            day = "明天";
        } else if (project.getDay() / 10000 == c.get(Calendar.YEAR)) {
            day = String.valueOf(project.getDay()).substring(4, 6) + "月"
                    + String.valueOf(project.getDay()).substring(6, 8) + "日";
        } else {
            day = String.valueOf(project.getDay()).substring(0, 4) + "年"
                    + String.valueOf(project.getDay()).substring(4, 6) + "月"
                    + String.valueOf(project.getDay()).substring(6, 8) + "日";
        }
        this.day.setText(day);
    }

    //设置清单list内容
    public void setList(ProjectList list) {
        listId=list.getId();
        contentSchedule.setText(list.getListText());
//        checkFinish.
        String day;
        Calendar c = Calendar.getInstance();
        int today = c.get(Calendar.YEAR) * 10000 + c.get(Calendar.MONTH) * 100 + c.get(Calendar.DAY_OF_MONTH);
        if (list.getDay() == today) {
            day = "今天";
        } else if (list.getDay() == today + 1) {
            day = "明天";
        } else {
            day = list.getDay() + "";
        }
        //在list视图sumFinish显示当前时间
        String hour = list.getTime() / 100 > 9 ? (list.getTime() / 100 + "") : ("0" + list.getTime() / 100);
        String min = list.getTime() % 100 > 9 ? (list.getTime() % 100 + "") : ("0" + list.getTime() % 100);
        sumFinish.setText(hour + ":" + min);
        this.day.setText(day);

    }


    public void forEditList() {
        isEdit = true;
        actionIsfinish.setVisibility(GONE);
        checkFinish.setVisibility(VISIBLE);
        contentImg.setVisibility(GONE);
    }

    public void closeExpand() {
        if (null != listener) {
            listener.ExpandListener(false);
        }
        dragLayout.CloseExpand();
    }

    //接口回调让上级知道自己展开还是关闭；
    public void setOnExpandListener(OnExpandListener listener) {
        this.listener = listener;
    }

    public interface OnExpandListener {
        public void ExpandListener(boolean isExpand);
    }
}
