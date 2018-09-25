package com.example.gabri.androidlabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {
    protected static final String ACTIVITY_NAME = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        final EditText loginEdt = findViewById(R.id.loginEditText);
        final SharedPreferences prefs = getSharedPreferences("MyNewSaveFile", Context.MODE_PRIVATE);

        String userString = prefs.getString("DefaultEmail","email@domain.com");
        loginEdt.setText(userString);


        Log.i(ACTIVITY_NAME, "In onCreate()");

        final Button button = findViewById(R.id.loginButton);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = loginEdt.getText().toString();

                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("DefaultEmail", input);

                edit.putBoolean("Hello", false);
                edit.commit();


                Intent intent = new Intent(LoginActivity.this, StartActivity.class);

                startActivity(intent);
            }
        });

    }


    public void onStart(){
        super.onStart();
        Log.i(ACTIVITY_NAME, "In onStart()");
    }

    public void onResume(){
        super.onResume();
        Log.i(ACTIVITY_NAME, "In onResume()");
    }

    public void onPause(){
        super.onPause();
        Log.i(ACTIVITY_NAME, "In onPause()");
    }

    public void onStop(){
        super.onStop();
        Log.i(ACTIVITY_NAME, "In onStop()");
    }

    public void onDestroy(){
        super.onDestroy();
        Log.i(ACTIVITY_NAME, "In onDestroy()");

    }


}


