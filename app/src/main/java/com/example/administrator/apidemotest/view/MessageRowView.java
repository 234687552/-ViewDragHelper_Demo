package com.example.administrator.apidemotest.view;

import android.content.Context;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.apidemotest.R;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

/**
 * Created by Administrator on 2016/7/28 0028.
 */
public class MessageRowView extends LinearLayout {
    private Context context;
    private EMMessage[] messages;
    private EMMessage message;
    private int position;
    private LayoutInflater inflater;
    private TextView timeStampView;
    private ImageView userAvatarView;
    private TextView usernickView;
    private TextView contentView;
    private ProgressBar progressBar;
    private ImageView statusView;
    private TextView ackedView;
    private  TextView deliveredView;


    public MessageRowView(Context context,EMMessage[] messages,int position) {
        super(context);
        this.context=context;
        this.messages=messages;
        this.message=messages[position];
        this.position=position;
        inflater=LayoutInflater.from(context);
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_message : R.layout.ease_row_sent_message, this);
        initView();
    }

    private void initView() {
        timeStampView = (TextView) findViewById(R.id.timestamp);
        usernickView = (TextView) findViewById(R.id.tv_userid);
        contentView = (TextView) findViewById(R.id.tv_chatcontent);

        //以下只对sent可见
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        statusView = (ImageView) findViewById(R.id.msg_status);
        ackedView = (TextView) findViewById(R.id.tv_ack);
        deliveredView = (TextView) findViewById(R.id.tv_delivered);


        // 设置内容
        contentView.setText(message.getBody().toString());
        usernickView.setText(message.direct()== EMMessage.Direct.RECEIVE?message.getFrom():message.getTo());
    }


}
