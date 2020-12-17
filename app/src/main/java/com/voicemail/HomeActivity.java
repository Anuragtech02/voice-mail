package com.voicemail;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private TextToSpeech tts;

    private String listenEmail = "";
    private String listenPassword = "";

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setTitle("Login");

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        sharedPreferences = getSharedPreferences("MyLoginInfo", Context.MODE_PRIVATE);

        String savedEmail = sharedPreferences.getString("email", "");
        String savedPassword = sharedPreferences.getString("password", "");

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listen();
            }
        };

        ConstraintLayout loginLayout = findViewById(R.id.login_layout);
        loginLayout.setOnClickListener(listener);

        if(savedEmail.equals("") || savedPassword.equals("")){

            tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status == TextToSpeech.SUCCESS){
                        int result = tts.setLanguage(Locale.US);
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e("TTS", "This Language is not supported");
                        }
                        speak("Welcome to voice mail. Tell me your login credentials ");
                    } else {
                        Log.e("TTS", "Initilization Failed!");
                    }
                }
            });

        }
        else{
            Intent intent = new Intent(HomeActivity.this, IntermediateActivity.class);
            startActivity(intent);
            finish();
        }

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!(email.getText().toString().equals("") || password.getText().toString().equals(""))){
                    Toast.makeText(HomeActivity.this, email.getText().toString(), Toast.LENGTH_SHORT).show();

                    listenEmail = email.getText().toString();
                    listenPassword = email.getText().toString();
                    saveToSharedPreference();
                }
                else{
                    Toast.makeText(HomeActivity.this, "Please enter credentials", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void speak(String text){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }else{
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void listen(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        try {

            int reqCode = 0;
            if(listenEmail.equals("")){
                reqCode = 100;
            }
            else if(listenPassword.equals("")){
                reqCode = 200;
            }

            startActivityForResult(i, reqCode);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(HomeActivity.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 100:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    assert result != null;
                    listenEmail = result.get(0).toString();
                    email.setText(listenEmail);
                    speak("Tap again to enter Password");
                }
                break;
            case 200:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    assert result != null;
                    listenPassword = result.get(0).toString();
                    password.setText(listenPassword);

                    saveToSharedPreference();
                    speak("Thanks it would be saved for later");
                }
            }
        }

    private void saveToSharedPreference() {

        sharedPreferences.edit().putString("email", listenEmail).apply();
        sharedPreferences.edit().putString("password", listenPassword).apply();
        Intent intent = new Intent(HomeActivity.this, IntermediateActivity.class);
        startActivity(intent);
        finish();
    }
}