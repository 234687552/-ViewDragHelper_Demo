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
public  class TypeDialog extends DialogFragment implements View.OnClickListener {
    private ImageView typeAll;
    private ImageView typeStudy;
    private ImageView typeWork;
    private ImageView typeLive;
    private TypeClickListener listener;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.type_all:
                listener.onTypeClickListener("所有");
                getDialog().dismiss();
                break;
            case R.id.type_study:
                listener.onTypeClickListener("学习");
                getDialog().dismiss();
                break;
            case R.id.type_work:
                listener.onTypeClickListener("工作");
                getDialog().dismiss();
                break;
            case R.id.type_live:
                listener.onTypeClickListener("生活");
                getDialog().dismiss();
                break;
        }
    }

    //接口返回数据给Activity刷新ui
    public interface TypeClickListener {
        void onTypeClickListener(String type);
    }
    public  void setTypeClickListener(TypeClickListener listener){
        this.listener=listener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new
                ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_type, container);
        typeAll = (ImageView) view.findViewById(R.id.type_all);
        typeStudy = (ImageView) view.findViewById(R.id.type_study);
        typeWork = (ImageView) view.findViewById(R.id.type_work);
        typeLive = (ImageView) view.findViewById(R.id.type_live);
        typeAll.setOnClickListener(this);
        typeStudy.setOnClickListener(this);
        typeWork.setOnClickListener(this);
        typeLive.setOnClickListener(this);
        return view;
    }

}