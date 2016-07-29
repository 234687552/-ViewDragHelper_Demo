package com.example.administrator.apidemotest.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

    }
public void refresh(){
    myAdapter.refresh();
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

    private class MyEMMessageListener implements EMMessageListener {

        @Override
        public void onMessageReceived(final List<EMMessage> list) {
            ((Activity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), list.get(0).getBody().toString(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onMessageReceived: ");
                    myAdapter.refresh();

                }
            });

        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage emMessage, Object o) {

        }

    }


    //===============================================================adapter===========================
    //设置adapter
    private class MyAdapter extends BaseAdapter {
        private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
        private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
        private static final int HANDLER_MESSAGE_SEEK_TO = 2;

        private EMConversation conversation;
        EMMessage[] messages = null;
        private Context context;
        private ListView listView;

        public MyAdapter(Context context, String groupId, ListView listView) {
            this.listView = listView;
            this.context = context;
            this.conversation = EMClient.getInstance().chatManager().getConversation(groupId);
            if (conversation!=null){
                List<EMMessage> lists = conversation.getAllMessages();
                for (int i = 0; i < lists.size(); i++) {
                    if (lists.get(i).getBooleanAttribute("isProjectDetail", false)) {
                        lists.remove(i);
                        i--;
                    }
                }
                messages = (EMMessage[]) lists.toArray(new EMMessage[0]);

            }

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
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new MessageRowView(context, messages, position);
//                convertView= new TextView(context);
//                ((TextView)convertView).setText("haha");
            }

            return convertView;
        }

        public void refresh() {
            if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
                return;
            }
            android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
            handler.sendMessage(msg);
            Log.w(TAG, "refresh: ");
        }

        Handler handler = new Handler() {
            private void refreshList() {
                // you should not call getAllMessages() in UI thread
                // otherwise there is problem when refreshing UI and there is new message arrive
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
                        messages = (EMMessage[]) lists.toArray(new EMMessage[0]);
                        conversation.markAllMessagesAsRead();
                        ((Activity)getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                            }
                        });
                    }
                }).start();

            }

            @Override
            public void handleMessage(android.os.Message message) {
                switch (message.what) {
                    case HANDLER_MESSAGE_REFRESH_LIST:
                        refreshList();
                        break;
                    case HANDLER_MESSAGE_SELECT_LAST:
                        if (messages.length > 0) {
                            listView.setSelection(messages.length - 1);
                        }
                        break;
                    case HANDLER_MESSAGE_SEEK_TO:
                        listView.setSelection(listView.getAdapter().getCount()-1);
                        break;
                    default:
                        break;
                }
            }
        };

    }


}
