package com.example.administrator.apidemotest.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.administrator.apidemotest.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/31 0031.
 */
public class AddMemberDialog extends DialogFragment{
    private static final String TAG = "AddMemberDialog";
    private static final int REFRESH_FRIENDS = 0;
    private static final int REFRESH_MEMBERS = 1;

    private String groupId;
    private RelativeLayout friendsProgress;
    private RelativeLayout membersProgress;
    private Button cancel;
    private Button sure;




    private ListView friendsList;
    private ListView membersList;
    private ListView newMembersList;

    private List<String> friends = new ArrayList<String>();
    private List<String> members = new ArrayList<String>();
    private List<String> newMembers = new ArrayList<String>();
    private ArrayAdapter<String> friendsAdapter;
    private ArrayAdapter<String> membersAdapter;
    private ArrayAdapter<String> newMembersAdapter;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_FRIENDS:
                    friends.clear();
                    friends.addAll((List<String>) msg.obj);
                    for (String member : members) {
                        if (friends.contains(member))
                            friends.remove(member);
                    }
                    friendsAdapter.notifyDataSetChanged();

                    friendsProgress.setVisibility(View.GONE);
                    friendsList.setVisibility(View.VISIBLE);
                    break;
                case REFRESH_MEMBERS:
                    members.clear();
                    members.addAll((List<String>) msg.obj);
                    for (String member : members) {
                        if (friends.contains(member))
                            friends.remove(member);
                    }
                    membersAdapter.notifyDataSetChanged();
                    friendsAdapter.notifyDataSetChanged();

                    membersProgress.setVisibility(View.GONE);
                    membersList.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onResume() {
        newMembers.clear();
        newMembersAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        groupId = getArguments().getString("groupId");
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_add_member, container);
        friendsProgress = (RelativeLayout) view.findViewById(R.id.friends_progress);
        membersProgress = (RelativeLayout) view.findViewById(R.id.members_progress);
        cancel = (Button) view.findViewById(R.id.cancel);
        sure = (Button) view.findViewById(R.id.sure);

        friendsList = (ListView) view.findViewById(R.id.friends_list);
        membersList = (ListView) view.findViewById(R.id.members_list);
        newMembersList = (ListView) view.findViewById(R.id.new_members_list);

        friendsAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, friends);
        membersAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, members);
        newMembersAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, newMembers);

        friendsList.setAdapter(friendsAdapter);
        membersList.setAdapter(membersAdapter);
        newMembersList.setAdapter(newMembersAdapter);

        getFiendsMembers();
        setOnClick();
        return view;
    }

    private void setOnClick() {
        friendsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                newMembers.add(friends.get(position));
                friends.remove(position);
                newMembersAdapter.notifyDataSetChanged();
                friendsAdapter.notifyDataSetChanged();
            }
        });

        newMembersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                friends.add(newMembers.get(position));
                newMembers.remove(position);
                friendsAdapter.notifyDataSetChanged();
                newMembersAdapter.notifyDataSetChanged();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void getFiendsMembers() {
        // 服务器获取好友信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Message msg = handler.obtainMessage(REFRESH_FRIENDS);
                    msg.obj = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    handler.sendMessage(msg);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        //获取群里成员
        if (!TextUtils.isEmpty(groupId)) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        Message msg = handler.obtainMessage(REFRESH_MEMBERS);
                        EMGroup group = EMClient.getInstance().groupManager().getGroupFromServer(groupId);
                        msg.obj = group.getMembers();
                        handler.sendMessage(msg);
                    } catch (HyphenateException e) {
                        e.printStackTrace();

                    }
                }
            }).start();
        }
    }


    //保证哪里都可以关闭该dialog，防止内存溢出
    @Override
    public void onStop() {
        Fragment prev = getFragmentManager().findFragmentByTag("AddMemberDialog");
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
        super.onStop();
    }
}