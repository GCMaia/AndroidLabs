package com.example.gabri.androidlabs;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

public class MessageDetails extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        Intent i = getIntent();

        MessageFragment mf = new MessageFragment();
        Bundle fragmentArgs = new Bundle();
        fragmentArgs.putLong("ID",   i.getLongExtra("ID", 99)   );
        fragmentArgs.putString("message", i.getStringExtra("message")  );
        fragmentArgs.putBoolean("phone", true);
        mf.setArguments(fragmentArgs);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.empty_frame, mf);
        ft.commit();
    }
}
