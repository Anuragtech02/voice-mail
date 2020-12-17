package com.voicemail;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;

public class Inbox extends AppCompatActivity {

    private String host = "pop.gmail.com";
    private String port = "pop3";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
//
//        CheckMail checkMail = new CheckMail(this, host, port, uname, pass);
//        checkMail.execute();
    }
}