package com.vasavidiaries.campusdiariesbeta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vasavidiaries.campusdiariesbeta.Communications.NetworkUtils;
//import com.vasavidiaries.campusdiariesbeta.LoginActivity;

import org.w3c.dom.Text;

import java.net.URL;

public class FirstTimeActivity extends AppCompatActivity implements View.OnClickListener{

    Button mLogin_activity, mNewuser_activity, mGuest_activity;
    EditText mRoll_number, mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);

        checkSharedPreferences();
    }

    public void checkSharedPreferences()
    {
        //TODO:Check if the user logged in previosuly and take him to that account

        SharedPreferences sharedPreferences = getSharedPreferences("logininfo", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username","");
        String password = sharedPreferences.getString("password","");

        if(username.equals("") || password.equals("")) {}
        else {
            if (username.length() == 12) {
                LoginCredentials(username, password);
                return;
            }
        }

        mapGUI();
        Toast.makeText(getApplicationContext(),"Mapping GUI", Toast.LENGTH_LONG);
    }

    public void mapGUI()
    {
        mLogin_activity   = (Button) findViewById(R.id.dLogin);
        mNewuser_activity = (Button) findViewById(R.id.dSignup);
        mGuest_activity   = (Button) findViewById(R.id.dContd_as_guest);

        mRoll_number      = (EditText) findViewById(R.id.drollno);
        mPassword         = (EditText) findViewById(R.id.dPassword);

//        mLogin_activity.setVisibility(View.VISIBLE);
//        mNewuser_activity.setVisibility(View.VISIBLE);
//        mGuest_activity.setVisibility(View.VISIBLE);
//        mRoll_number.setVisibility(View.VISIBLE);
//        mPassword.setVisibility(View.VISIBLE);
        
        mLogin_activity.setOnClickListener(this);
        mNewuser_activity.setOnClickListener(this);
        mGuest_activity.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        try {
            switch (view.getId()) {
                case R.id.dLogin: {
                    String rollnumber = mRoll_number.getText().toString();
                    String password = mPassword.getText().toString();

                    if (rollnumber != null && password != null) {
                        if (rollnumber.length() != 12) {
                            Toast.makeText(getApplicationContext(), "Invalid Login Credentials", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_SHORT).show();
                            LoginCredentials(rollnumber, password);
                        }
                    }
                    break;
                }
                case R.id.dSignup: {
                    Thread splashscreen = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                Intent goToSignup = new Intent(FirstTimeActivity.this, SignUpActivity.class);
                                startActivity(goToSignup);
                            } catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                    });
                    splashscreen.start();
                    break;
                }
                case R.id.dContd_as_guest: {
                    Thread splashscreen = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                Intent goToPosts = new Intent(FirstTimeActivity.this, PostsActivity.class);
                                startActivity(goToPosts);
                            } catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                    });
                    splashscreen.start();
                    break;
                }
                default: {

                }
            }
        }catch (Exception e){
            Log.e("View","Exception",e);
        }
    }

    public void LoginCredentials(String rollnumber, String password)
    {
        URL validate_credentials = NetworkUtils.buildUrl(rollnumber, password);
        Log.d("URL",validate_credentials.toString());
        new QueryTask().execute(validate_credentials);
    }

    public class QueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtils.getResponseFromHttpUrlLogin(searchUrl);
            } catch (Exception e) {
                Log.e("MYAPP", "Exception", e);
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String searchResults) {
            if (searchResults != null && !searchResults.equals("")) {
                if (searchResults.equals("True") || searchResults.equals("MTrue")) {
                    Toast.makeText(getApplicationContext(), "Login Successful",
                            Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = getSharedPreferences("logininfo", Context.MODE_PRIVATE);

                    try {
                        if (sharedPreferences.getString("username", "").equals(mRoll_number.getText().toString())) {

                        } else {
                            Log.d("Position", "Debugging");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", mRoll_number.getText().toString());
                            editor.putString("password", mPassword.getText().toString());
                            if(searchResults.equals("MTrue"))
                                editor.putString("ismoderator", "true");
                            else
                                editor.putString("ismododerator", "false");
                            editor.apply();
                        }
                    }catch(Exception e){
                        Log.e("CampusDiaries","Exception",e);
                    }

                    Thread splashscreen = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                Intent goToPosts = new Intent(FirstTimeActivity.this, PostsActivity.class);
                                startActivity(goToPosts);
                            } catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                    });
                    splashscreen.start();
                    finish();

                }
                else{
                    Toast.makeText(getApplicationContext(), "Invalid login credentials",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}

