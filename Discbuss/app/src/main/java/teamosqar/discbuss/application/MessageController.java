package teamosqar.discbuss.application;

import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.List;

import teamosqar.discbuss.activities.R;
import teamosqar.discbuss.model.Model;
import teamosqar.discbuss.util.Message;
import teamosqar.discbuss.util.MessageInbox;

/**
 * Created by joakim on 2015-10-11.
 */
public class MessageController extends BaseAdapter {

    private Firebase messagesFirebaseRef;
    private ChildEventListener messageListener;
    private List<MessageInbox> messageInboxes;
    private List<Message> mostRecentMsg;
    private List<String> keys; //Remember the lists are ordered by their date/time in the messageInbox model, keys does not controll the ordering in this case
    private LayoutInflater inflater;

    public MessageController(LayoutInflater inflater){
        messagesFirebaseRef = Model.getInstance().getMRef().child("duoChats");

        this.inflater = inflater;

        messageInboxes = new ArrayList<MessageInbox>();
        mostRecentMsg = new ArrayList<Message>();
        keys = new ArrayList<String>();

        messageListener = messagesFirebaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            //Remember that seenByY and seenByX will translate directly into messageInbox seenByMe and seenByOther
            //Will need to be switched in case they are incorrect, or controlled in some other way!
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {

                if(dataSnapshot.child("participants").hasChild(Model.getInstance().getUid())){
                    MessageInbox inbox = dataSnapshot.child("inboxInfo").getValue(MessageInbox.class);
                    Message mostRecentMessage = dataSnapshot.child("chat").getChildren().iterator().next().getValue(Message.class);//Should get the most recent message! is this correctly done??
                    String key = dataSnapshot.getKey();

                    int index = getCorrectListSpot(inbox);

                    insertIntoLists(index, inbox, mostRecentMessage, key);

                    notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.child("participants").hasChild(Model.getInstance().getUid())) {
                    String key = dataSnapshot.getKey();
                    int currentIndex = keys.indexOf(key);
                    MessageInbox inbox = dataSnapshot.child("inboxInfo").getValue(MessageInbox.class);
                    Message mostRecentMessage = dataSnapshot.child("chat").getChildren().iterator().next().getValue(Message.class);

                    messageInboxes.remove(currentIndex);
                    mostRecentMsg.remove(currentIndex);
                    keys.remove(currentIndex);

                    int newIndex = getCorrectListSpot(inbox);

                    insertIntoLists(newIndex, inbox, mostRecentMessage, key);

                    notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("participants").hasChild(Model.getInstance().getUid())) {
                    String key = dataSnapshot.getKey();
                    int index = keys.indexOf(key);

                    keys.remove(index);
                    messageInboxes.remove(index);
                    mostRecentMsg.remove(index);

                    notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildKey) {
                if(dataSnapshot.child("participants").hasChild(Model.getInstance().getUid())) {
                    String key = dataSnapshot.getKey();
                    MessageInbox inbox = dataSnapshot.child("inboxInfo").getValue(MessageInbox.class);
                    Message message = dataSnapshot.child("chat").getChildren().iterator().next().getValue(Message.class);

                    int currentIndex = keys.indexOf(key);
                    messageInboxes.remove(currentIndex);
                    mostRecentMsg.remove(currentIndex);
                    keys.remove(currentIndex);

                    insertIntoLists(getCorrectListSpot(inbox), inbox, message, key);

                    notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("FirebaseListAdapter", "Listen was cancelled, no more updates will occur");
            }
        });

    }

    private void insertIntoLists(int position, MessageInbox inbox, Message mostRecentMessage, String key){
        if(position == messageInboxes.size()){
            messageInboxes.add(inbox);
            mostRecentMsg.add(mostRecentMessage);
            keys.add(key);
        }else{
            messageInboxes.add(position, inbox);
            mostRecentMsg.add(position, mostRecentMessage);
            keys.add(position, key);
        }
    }

    private int getCorrectListSpot(MessageInbox inbox){

        for(int i = 0; i < messageInboxes.size(); i++){
            if(messageInboxes.get(i).isBefore(inbox.getLatestActivity())){
                return i;
            }
        }
        return messageInboxes.size();
    }


    @Override
    public int getCount() {
        return messageInboxes.size();
    }

    @Override
    public Object getItem(int position) {
        return messageInboxes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.message_inbox, parent, false);
        }

        populateView(convertView, position);
        return convertView;
    }

    private void populateView(View view, int position){
        String msg = mostRecentMsg.get(position).getMessage();
        String author = mostRecentMsg.get(position).getAuthor();

        TextView authorView = (TextView) view.findViewById(R.id.messageInboxNick);
        TextView messageView = (TextView) view.findViewById(R.id.messageInboxMessage);
        TextView messageReadView = (TextView) view.findViewById(R.id.messageInboxRead);

        authorView.setText(author);
        messageView.setText(msg);
        if(messageInboxes.get(position).isSeenByMe()){
            messageView.setTypeface(null, Typeface.BOLD);
            authorView.setTypeface(null, Typeface.BOLD);
        }else{
            messageView.setTypeface(null, Typeface.NORMAL);
            authorView.setTypeface(null, Typeface.NORMAL);
        }
        if(messageInboxes.get(position).isSeenByOther()) {
            messageReadView.setText("Read");
        }else{
            messageReadView.setText("");
        }
    }
}