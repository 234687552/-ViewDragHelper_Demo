package com.example.administrator.apidemotest.dialog;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.example.administrator.apidemotest.R;

//choose　type dialog
public  class EditDialog extends DialogFragment implements View.OnClickListener {


    @Override
    public void onClick(View v) {
        EditListener listener= (EditListener) getActivity();
        switch (v.getId()) {
            case R.id.type_all:
                listener.onEditListener("所有");
                getDialog().dismiss();
                break;
            case R.id.type_study:
                listener.onEditListener("学习");
                getDialog().dismiss();
                break;
            case R.id.type_work:
                listener.onEditListener("工作");
                getDialog().dismiss();
                break;
            case R.id.type_live:
                listener.onEditListener("生活");
                getDialog().dismiss();
                break;
        }
    }

    //接口返回数据给Activity刷新ui
    public interface EditListener {
        void onEditListener(String type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        getDialog().getWindow().setBackgroundDrawable(new
//                ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.edit_dialog, container);

        return view;
    }

}