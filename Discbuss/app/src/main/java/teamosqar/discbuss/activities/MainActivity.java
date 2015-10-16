package teamosqar.discbuss.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import teamosqar.discbuss.application.MainController;
import teamosqar.discbuss.fragments.SuggestFragment;
import teamosqar.discbuss.model.Model;
import teamosqar.discbuss.util.Toaster;

public class MainActivity extends AppCompatActivity {

    private MainController mainController;
    private Firebase mref;
    private TextView suggestView;
    //BELOW ONLY FOR TESTING...
    private final String bssidMightos = "bc:ee:7b:55:47:16";
    //ABOVE ONLY FOR TESTING...
    private final String buss1 = "04:f0:21:10:09:df";
    private final String buss2 = "04:f0:21:10:09:b9";
    private final String buss3 = "04:f0:21:10:09:e8";
    private final String buss4 = "04:f0:21:10:09:b7";
    private final String buss5 = "04:f0:21:10:09:53";
    private final String buss6 = "04:f0:21:10:09:5b";
    private final String buss7 = "04:f0:21:10:09:b8";
    private final String buss8 = "04:f0:21:10:09:b9";
    private final String buss9 = "n/a";
    private final String buss10 = "04:f0:21:10:09:b7";
    private EditText fragmentData;
    private WifiInfo wifiInfo;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private SuggestFragment fragment;

    private boolean connectedToWifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_main);
        if(checkWifiState(this)){
            connectedToWifi=true;


        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        Firebase.setAndroidContext(this);
        mref = Model.getInstance().getMRef();
        suggestView = (TextView) findViewById(R.id.textViewStatement);
        fragment = new SuggestFragment();
    }

    public boolean checkWifiState(Context context){
        try {
            WifiManager mWifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            wifiInfo=mWifiManager.getConnectionInfo();
            if (!mWifiManager.isWifiEnabled() || wifiInfo.getSSID() == null || wifiInfo.getBSSID() == null) {
                return false;
            }
            return true;
        }
        catch (  Exception e) {
            return false;
        }
    }

    public void enterDiscussion(View view) {
        String chatRoom = "chatRooms/";
        /*if(checkWifiState(this)){
            switch (wifiInfo.getBSSID()) {
                //temporary
                case bssidMightos:
                    chatRoom = chatRoom + "mightoz";
                    break;
                case buss1:
                    chatRoom = chatRoom + "1";
                    break;
                case buss2:
                    chatRoom = chatRoom + "2";
                    break;
                case buss3:
                    chatRoom = chatRoom + "3";
                    break;
                case buss4:
                    chatRoom = chatRoom + "4";
                    break;
                case buss5:
                    chatRoom = chatRoom + "5";
                    break;
                case buss6:
                    chatRoom = chatRoom + "6";
                    break;
                case buss7:
                    chatRoom = chatRoom + "7";
                    break;
                case buss8:
                    chatRoom = chatRoom + "8";
                    break;
                case buss9:
                    chatRoom = chatRoom + "9";
                    break;
                case buss10:
                    chatRoom = chatRoom + "10";
                    break;
            }
            if(chatRoom!="chatRooms/") {
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("EXTRA_ROOM", chatRoom);
                startActivity(intent);
            } else {
                Toaster.displayToast("Connect to buss WiFi", getApplicationContext(), Toast.LENGTH_SHORT);
            }

        } else {
            Toaster.displayToast("Connect to buss WiFi", getApplicationContext(), Toast.LENGTH_SHORT);
        }*/
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("EXTRA_ROOM", "");
        startActivity(intent);
    }

    public void goToProfile(View view){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void goToMessages(View view){
        Intent intent = new Intent(this, MessageActivity.class);
        startActivity(intent);
    }

    public void suggestStatement(View view){
        fm = getFragmentManager();
        ft = fm.beginTransaction();
        ft.add(R.id.fragmentPlaceholder, fragment);
        ft.commit();
    }
    public void submitStatement(View view){
        fragmentData = (EditText) findViewById(R.id.editTextStatement);
        if(!fragmentData.getText().toString().isEmpty()) {
            mref.child("statements").push().setValue(fragmentData.getText().toString());
            //TODO: Add toast saying it was successful! Remove Fragment.
            FragmentTransaction newFt = getFragmentManager().beginTransaction();
            newFt.remove(fragment);
            newFt.commit();
            Toaster.displayToast("Statement submitted!", getApplicationContext(), Toast.LENGTH_SHORT);
        } else {
            //TODO: Update with new toaster
            Toaster.displayToast("Write a statement", getApplicationContext(), Toast.LENGTH_SHORT);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.logout){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("logout", "logout");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
