package com.example.administrator.apidemotest.dialog;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.administrator.apidemotest.R;
import com.example.administrator.apidemotest.db.ProjectDb;
import com.example.administrator.apidemotest.model.ProjectList;

//edit dialog
public class EditDialog extends DialogFragment {
    private SaveListener listener;
    private ImageView save;
    public EditText listText;
    public EditText listRemark;
    private int listId;
    private ProjectDb db;
    private ProjectList curList;

    public void setListId(int listId) {
        this.listId = listId;
    }

    //接口返回数据给Activity刷新ui
    public interface SaveListener {
        void onSaveListener(boolean save);
    }

    public void setOnSaveListener(SaveListener listener) {
        this.listener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = new ProjectDb(getActivity());
        curList = db.getList(listId);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_edit, container);
        save = (ImageView) view.findViewById(R.id.save);
        listText = (EditText) view.findViewById(R.id.list_text);
        listRemark = (EditText) view.findViewById(R.id.list_remark);

        String title = curList.getListText();
        String detail = curList.getRemark();
        listText.setText(title);
        listRemark.setText(null == detail ? "" : detail);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curList.setRemark(String.valueOf(listRemark.getText()));
                curList.setListText(String.valueOf(listText.getText()));
                db.updateProjectList(listId, curList);
                listener.onSaveListener(true);
                dismiss();
            }
        });
        return view;
    }

    //保证哪里都可以关闭该dialog，防止内存溢出
    @Override
    public void onStop() {
        Fragment prev = getFragmentManager().findFragmentByTag("EditDialog");
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
        super.onStop();
    }
}