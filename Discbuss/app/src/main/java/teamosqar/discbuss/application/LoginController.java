package teamosqar.discbuss.application;

import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Observable;

import teamosqar.discbuss.activities.LoginActivity;
import teamosqar.discbuss.model.Model;

/**
 * Created by joakim on 2015-10-02.
 */
public class LoginController extends Observable{

    private Firebase userRef;
    private boolean loginStatus;
    public LoginController(){
        userRef = Model.getInstance().getMRef().child("users");
    }

    public void tryLogin(final String email, String password){

        userRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                Log.d("Login", "success");
                loginStatus = true;
                Model.getInstance().setUid(authData.getUid());
                Model.getInstance().setEmail(email);
                userRef.child(authData.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Model.getInstance().setUsername(dataSnapshot.child("name").getValue().toString());
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });
                setChanged();
                notifyObservers();

            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                loginStatus = false;
                setChanged();
                notifyObservers();

            }

        });
    }

    public boolean getLoginStatus(){
        return loginStatus;
    }
}
