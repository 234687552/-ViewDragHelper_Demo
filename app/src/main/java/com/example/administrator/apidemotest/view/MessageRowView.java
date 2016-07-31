package com.example.administrator.apidemotest.view;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.apidemotest.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;

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
    private EMCallBack messageSendCallback;
    private Activity activity;
    private boolean isSend=false;

    public MessageRowView(Context context,EMMessage[] messages,int position) {
        super(context);
        this.context=context;
        this.activity=(Activity)context;
        inflater=LayoutInflater.from(context);
        inflater.inflate(R.layout.ease_row_received_message,this);
        setupView(messages, position);
        isSend=false;
    }
    public MessageRowView(Context context,EMMessage[] messages,int position,boolean send) {
        super(context);
        this.context=context;
        this.activity=(Activity)context;
        inflater=LayoutInflater.from(context);
        inflater.inflate(R.layout.ease_row_sent_message,this);
        setupView(messages, position);
        isSend=true;
    }


    public void setupView(EMMessage[] messages,int position){
        this.messages=messages;
        this.message=messages[position];
        this.position=position;
        inflater.inflate(messages[position].direct() == EMMessage.Direct.SEND ? R.layout.ease_row_sent_message : R.layout.ease_row_received_message, this);
        initView();
    }

    private void initView() {

        timeStampView = (TextView) findViewById(R.id.timestamp);
        usernickView = (TextView) findViewById(R.id.tv_userid);
        contentView = (TextView) findViewById(R.id.tv_chatcontent);

        //以下只对send可见
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        statusView = (ImageView) findViewById(R.id.msg_status);

        // 设置内容
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        contentView.setText(txtBody.getMessage());
        isSend=message.direct() == EMMessage.Direct.RECEIVE?false:true;
        if (!isSend){
            usernickView.setText(message.direct() == EMMessage.Direct.RECEIVE ? message.getFrom() : message.getTo());
        }
        handleTextMessage();
        setClickListener();
    }

    private void setClickListener() {
        if (statusView!=null){
            statusView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    message.setStatus(EMMessage.Status.INPROGRESS);
                    EMClient.getInstance().chatManager().sendMessage(message);
                    handleTextMessage();
                }
            });
        }
    }
    private void handleTextMessage() {
        if (message.direct() == EMMessage.Direct.SEND) {
            setMessageSendCallback();
            switch (message.status()) {
                case CREATE:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.GONE);
                    break;
                case FAIL:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                    progressBar.setVisibility(View.VISIBLE);
                    statusView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }else{
            if(!message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat){
                try {
                    EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void setMessageSendCallback(){
        if(messageSendCallback == null){
            messageSendCallback = new EMCallBack() {

                @Override
                public void onSuccess() {
                    updateView();
                }

                @Override
                public void onProgress(final int progress, String status) {
                }

                @Override
                public void onError(int code, String error) {
                    updateView();
                }
            };
        }
        message.setMessageStatusCallback(messageSendCallback);
    }
    private void updateView() {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (message.status() == EMMessage.Status.FAIL) {

                    if (message.getError() == EMError.MESSAGE_INCLUDE_ILLEGAL_CONTENT) {
                        Toast.makeText(activity,  "error_send_invalid_content", Toast.LENGTH_SHORT).show();
                    } else if (message.getError() == EMError.GROUP_NOT_JOINED) {
                        Toast.makeText(activity,  "error_send_not_in_the_group", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity,  "connect_failuer_toast", Toast.LENGTH_SHORT).show();
                    }
                }
                handleTextMessage();
            }
        });

    }
}
