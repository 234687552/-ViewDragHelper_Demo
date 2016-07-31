package com.example.administrator.apidemotest.dialog;

import android.app.DialogFragment;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.administrator.apidemotest.R;
import com.example.administrator.apidemotest.db.ProjectDb;
import com.example.administrator.apidemotest.model.ProjectList;

/**
 * Created by Administrator on 2016/7/31 0031.
 */
public class AddMemberDialog extends DialogFragment {

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {

        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_add_member, container);

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