package com.example.administrator.apidemotest.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Administrator on 2016/7/27 0027.
 */
public class ConversationListView extends ListView {
    private static final String TAG = "ConversationListView";
    private String groupId;
    private Context mContext;
    private MyAdapter myAdapter;
    private MyEMMessageListener msgListener;

    public ConversationListView(Context context) {
        super(context);
        mContext = context;
    }

    public ConversationListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void init(String groupId) {
        this.groupId = groupId;
        myAdapter = new MyAdapter(mContext, groupId, this);
        setAdapter(myAdapter);
        msgListener = new MyEMMessageListener();
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        refresh();
    }

    public void refresh() {
        myAdapter.refresh(true);
    }

    public void refreshWithoutSink() {
        myAdapter.refresh(false);
    }

    //listview 消失的时候
    //view的生命周期
    //View 的关键生命周期为    [改变可见性]  -->   构造View   -->      onFinishInflate  -->   onAttachedToWindow  -->  onMeasure  -->  onSizeChanged  -->  onLayout  -->   onDraw  -->  onDetackedFromWindow
    @Override
    protected void onDetachedFromWindow() {
        //注销信息监听器
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
        super.onDetachedFromWindow();
    }

    //接收信息监听器
    private class MyEMMessageListener implements EMMessageListener {

        @Override
        public void onMessageReceived(final List<EMMessage> list) {
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myAdapter.refresh(true);
                }
            });
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {
            //收到透传信息
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> list) {
            //收到已读回执
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> list) {
            //收到已送达回执
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myAdapter.refresh(true);
                }
            });

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {
            //消息状态变动
        }

    }

    //===============================================================adapter====================================
    //设置adapter
    private class MyAdapter extends BaseAdapter {
        private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
        private static final int HANDLER_MESSAGE_REFRESH_LIST_WITHOUT_SINK = 1;

        private int pageSize = 20;
        private boolean hasHistory = true;

        private EMConversation conversation;
        EMMessage[] messages = null;
        private Context context;
        private String groupId;
        private ListView listView;

        public MyAdapter(Context context, String groupId, ListView listView) {
            this.listView = listView;
            this.context = context;
            this.groupId = groupId;
            refresh(true);
        }


        @Override
        public int getCount() {
            return messages == null ? 0 : messages.length;
        }

        @Override
        public EMMessage getItem(int position) {
            if (messages != null && position < messages.length) {
                return messages[position];
            }
            return null;
        }

        @Override
        public int getItemViewType(int position) {
            return messages[position].direct() == EMMessage.Direct.RECEIVE ? 1 : 0;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                if (getItemViewType(position) == 1) {
                    convertView = new MessageRowView(context, messages, position);
                } else {
                    convertView = new MessageRowView(context, messages, position, true);
                }
            }
            ((MessageRowView) convertView).setupView(messages, position);
            return convertView;
        }

        public void refresh(final boolean isSink) {

            conversation = EMClient.getInstance().chatManager().getConversation(groupId);
            // you should not call getAllMessages() in UI thread
            // otherwise there is problem when refreshing UI and there is new message arrive
            if (conversation != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<EMMessage> lists = conversation.getAllMessages();
                        for (int i = 0; i < lists.size(); i++) {
                            if (lists.get(i).getBooleanAttribute("isProjectDetail", false)) {
                                lists.remove(i);
                                i--;
                            }
                        }
                        EMMessage[] tempMessages = (EMMessage[]) lists.toArray(new EMMessage[0]);
                        conversation.markAllMessagesAsRead();
                        android.os.Message msg = handler.obtainMessage(isSink ? HANDLER_MESSAGE_REFRESH_LIST :
                                HANDLER_MESSAGE_REFRESH_LIST_WITHOUT_SINK);
                        msg.obj = tempMessages;
                        msg.arg1 = isSink ? 0 : conversation.loadMoreMsgFromDB(messages[0].getMsgId(), pageSize).size();
                        handler.sendMessage(msg);
                    }
                }).start();
            } else {
                Log.w(TAG, "refresh: " + conversation);
            }

        }

        Handler handler = new Handler() {
            @Override
            public void handleMessage(android.os.Message message) {
                switch (message.what) {
                    case HANDLER_MESSAGE_REFRESH_LIST:
                        messages = (EMMessage[]) message.obj;
                        notifyDataSetChanged();
                        if (messages.length > 0) {
                            listView.setSelection(messages.length - 1);
                        }
                        break;
                    case HANDLER_MESSAGE_REFRESH_LIST_WITHOUT_SINK:
                        if (hasHistory) {
                            messages = (EMMessage[]) message.obj;
                            notifyDataSetChanged();
                            if (messages.length > message.arg1) {
                                listView.setSelection(message.arg1 < pageSize ? 0 : message.arg1 - 1);
                                if (message.arg1 < pageSize)
                                    hasHistory = false;
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        };

    }


}
