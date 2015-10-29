package teamosqar.discbuss.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import teamosqar.discbuss.application.DuoChatController;
import teamosqar.discbuss.application.ProfileController;

/**
 * Created by oskar on 16/10/2015.
 *
 * Activity class for viewing other users profiles
 */
public class OtherProfileActivity extends ProfileActivity implements Observer {

    private TextView userInfo, karma;
    private String chatUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String uid = intent.getStringExtra("uid");
        chatUID = uid;
        profileController = new ProfileController(this, uid);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        profileController.updateContext(this);
        profileController.addObserver(this);
        userInfo = (TextView) findViewById(R.id.textViewDisplayName);
        karma = (TextView) findViewById(R.id.textViewUserKarma);


    }

    @Override
    public void onStart(){
        super.onStart();
        profileController.updateContext(this);
        //profileController.updateNextBusStop();
    }


    public void update(Observable observable, Object data){
        userInfo.setText(profileController.getName() + ", " + profileController.getGender() + "(" + profileController.getAge()+")");
        karma.setText(profileController.getKarma());
        displayTopComments();
    }

    /**
     * launches a private chat with the user whose profile is being viewed
     * @param view
     */
    public void launchDuoChat(View view){
        DuoChatController.launchDuoChat(this, chatUID);
    }
}

