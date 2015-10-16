package teamosqar.discbuss.application;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.List;

import teamosqar.discbuss.activities.R;
import teamosqar.discbuss.model.Model;
import teamosqar.discbuss.util.Message;


/**
 * Created by joakim on 2015-09-29.
 */
public abstract class ChatController extends BaseAdapter{

    private final Context context;
    private LayoutInflater inflater;
    private Firebase chatFireBaseRef;
    private ChildEventListener chatListener;
    private List<Message> messageModels;
    private List<String> messageKeys;
    private int clickedMessage;
    private View clickedView;

    public ChatController(Context context, String chatRoom){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        clickedMessage = -1;
        clickedView = null;

        //chatFireBaseRef = Model.getInstance().getMRef().child("chatRooms").child(chatRoom); //TODO: Use to bind chatrooms to buses
        //activeUserRef = Model.getInstance().getMRef().child("activeUsers").child(chatRoom); //TODO: Use to bind chatrooms to buses
        //chatFireBaseRef = Model.getInstance().getMRef().child("chat");                        //TODO: Remove when done testing
        //activeUserRef = Model.getInstance().getMRef().child("activeUsers").child("chat");     //TODO: Remove when done testing

        chatFireBaseRef = getFirebaseChatRef(chatRoom);

        messageModels = new ArrayList<Message>();
        messageKeys = new ArrayList<String>();

        chatListener = chatFireBaseRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {

                Message message = dataSnapshot.child("message").getValue(Message.class);
                String key = dataSnapshot.getKey();

                //Insert into correct location, based on previous child
                if(previousChildKey == null){
                    messageModels.add(0, message);
                    messageKeys.add(0,key);
                }else{
                    int previousIndex = messageKeys.indexOf(previousChildKey);
                    int nextIndex = previousIndex + 1;
                    if(nextIndex == messageModels.size()){
                        messageModels.add(message);
                        messageKeys.add(key);
                    }else{
                        messageModels.add(nextIndex, message);
                        messageKeys.add(key);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                Message message = dataSnapshot.child("message").getValue(Message.class);
                int index = messageKeys.indexOf(key);

                messageModels.set(index, message);
                notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                int index = messageKeys.indexOf(key);

                messageKeys.remove(index);
                messageModels.remove(index);
                notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildKey) {
                String key = dataSnapshot.getKey();
                Message message = dataSnapshot.child("message").getValue(Message.class);

                int index = messageKeys.indexOf(key);
                messageModels.remove(index);
                messageKeys.remove(index);

                if(previousChildKey == null){
                    messageModels.add(0, message);
                    messageKeys.add(0, key);
                }else{
                    int previousIndex = messageKeys.indexOf(previousChildKey);
                    int nextIndex = previousIndex + 1;
                    if(nextIndex == messageModels.size()){
                        messageModels.add(message);
                        messageKeys.add(key);
                    }else{
                        messageModels.add(nextIndex, message);
                        messageKeys.add(nextIndex, key);
                    }
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur");
            }
        });
    }

    protected abstract Firebase getFirebaseChatRef(String chatRoom);

    protected Message getMessageModel(int location){
        return messageModels.get(location);
    }

    protected String getMessageKey(int location){
        return messageKeys.get(location);
    }

    public void sendMessage(String msg){
        if(!msg.equals("")) {
            Message message = new Message(Model.getInstance().getUid(), msg, Model.getInstance().getUsername());
            chatFireBaseRef.push().child("message").setValue(message);
        }
    }

    @Override
    public int getCount() {
        return messageModels.size();
    }

    @Override
    public Object getItem(int position) {
        return messageModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(clickedMessage == position) {
            if(clickedView != null){
                convertView = clickedView;
            }else {
                convertView = getMessageView(parent);
                View viewExtension = getMessageViewExtension();
                if(viewExtension != null) {
                    ((ViewGroup) convertView).addView(viewExtension, -1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
                clickedView = convertView;
            }
        }else if (convertView == null || clickedView == convertView) {
            convertView = getMessageView(parent);
        }


        Message msg = messageModels.get(position);
        populateView(convertView, msg);


        return convertView;
    }

    protected abstract View getMessageView(ViewGroup parent);

    protected abstract View getMessageViewExtension();

    protected abstract void populateView(View view, Message message);

    public void messageClicked(int position) {
        if(clickedMessage != position) {
            clickedMessage = position;
        }else{
            clickedMessage = -1;
        }
        notifyDataSetChanged();
    }

    public void personalProfileClicked(int position){
        final String otherUid = messageModels.get(position).getUid();
        if(!otherUid.equals(Model.getInstance().getUid())){
            //TODO: Launch profile using otherUid
        }
    }

    public void onEnteredChat(){}

    public void onLeftChat(){}

}
