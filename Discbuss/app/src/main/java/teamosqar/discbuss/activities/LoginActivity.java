package teamosqar.discbuss.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.Observable;
import java.util.Observer;

import teamosqar.discbuss.application.LoginController;
import teamosqar.discbuss.fragments.LoadingFragment;
import teamosqar.discbuss.util.Toaster;

/**
 * Activity class for the login view
 */
public class LoginActivity extends AppCompatActivity implements Observer {

    //Used for retrieving and saving to SharedPreferences
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String AUTO_LOGIN = "autoLogin";

    private LoginController loginController;
    private EditText editEmail, editPassword;
    private CheckBox autoLoginCheckbox;
    private SharedPreferences sharedPref;
    private Button buttonLogin, buttonRegister;

    private LoadingFragment loadingFragment;
    private boolean tryingLogin;

    @Override
    protected void onStart(){
        super.onStart();
    }

    /**
     * Sets all instance variables which are graphical elements, also checks if autologin is checked and autologins in that case
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_login);

        editEmail = (EditText)findViewById(R.id.editTextLoginEmail);
        editPassword = (EditText)findViewById(R.id.editTextLoginPassword);
        autoLoginCheckbox = (CheckBox)findViewById(R.id.autoLoginCheckBox);
        buttonLogin = (Button)findViewById(R.id.buttonLogin);
        buttonRegister = (Button)findViewById(R.id.buttonNotRegistered);

        loginController = new LoginController();
        loginController.addObserver(this);
        tryingLogin = false;
        loadingFragment = new LoadingFragment();

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        Boolean autoLogin = sharedPref.getBoolean(AUTO_LOGIN, false);
        Intent intent = getIntent();
        if(intent.getStringExtra("logout") != null){
            String activity = intent.getStringExtra("logout");
            if(activity.equals("logout")){
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(AUTO_LOGIN, false);
                editor.apply();
                autoLogin = false;
            }
        }
        if(autoLogin){
            editEmail.setText(sharedPref.getString(EMAIL, "email"));
            editPassword.setText(sharedPref.getString(PASSWORD, "pass"));
            autoLoginCheckbox.setChecked(autoLogin);
            initiateLogin();
        }
    }

    /**
     * Represents the login-buttons functionality
     * @param view
     */
    public void loginPressed(View view){
        initiateLogin();
    }

    /**
     * Takes you to the RegisterActivity
     * @param view
     */
    public void notRegisteredPressed(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Represents the cancel-button during authentication process
     * @param view
     */
    public void cancelLoadingPressed(View view){
        cancelLogin();
    }

    /**
     * Tries logging in and puts the loading fragment on the screen
     */
    public void initiateLogin(){
        tryingLogin = true;
        editEmail.setVisibility(View.GONE);
        editPassword.setVisibility(View.GONE);
        autoLoginCheckbox.setVisibility(View.GONE);
        buttonLogin.setVisibility(View.GONE);
        buttonRegister.setVisibility(View.GONE);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.loadingFragmentPlaceholder, loadingFragment);
        ft.commit();
        loginController.tryLogin(editEmail.getText().toString(), editPassword.getText().toString());
    }

    /**
     * Cancels the login and removes the loading screen fragment
     */
    public void cancelLogin(){
        tryingLogin = false;
        editEmail.setVisibility(View.VISIBLE);
        editPassword.setVisibility(View.VISIBLE);
        autoLoginCheckbox.setVisibility(View.VISIBLE);
        buttonLogin.setVisibility(View.VISIBLE);
        buttonRegister.setVisibility(View.VISIBLE);
        FragmentTransaction newFt = getFragmentManager().beginTransaction();
        newFt.remove(loadingFragment);
        newFt.commit();
        //setContentView(R.layout.activity_login);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void update(Observable observable, Object data) {
        if(tryingLogin) {
            if (loginController.getLoginStatus()) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(EMAIL, editEmail.getText().toString());
                editor.putString(PASSWORD, editPassword.getText().toString());
                editor.putBoolean(AUTO_LOGIN, autoLoginCheckbox.isChecked());
                editor.commit();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toaster.displayToast("Inloggning misslyckades", getApplicationContext(), Toast.LENGTH_SHORT);
                cancelLogin();
            }
        }
    }

    @Override
    public void onStop(){
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        super.onStop();
    }
}
