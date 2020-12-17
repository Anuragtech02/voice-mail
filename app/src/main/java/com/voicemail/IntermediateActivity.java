package com.voicemail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class IntermediateActivity extends AppCompatActivity {

    private TextToSpeech tts;
    private String listenEmail = "";
    private String listenPassword = "";

    private SharedPreferences sharedPreferences;
    private ConstraintLayout constraintLayout;
    private int numberOfClicks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intermediate);

        sharedPreferences = getSharedPreferences("MyLoginInfo", Context.MODE_PRIVATE);
        constraintLayout = findViewById(R.id.intermediate_layout);

        String savedEmail = sharedPreferences.getString("email", "");
        String savedPassword = sharedPreferences.getString("password", "");
        numberOfClicks = 0;

        if(savedEmail.equals("") || savedPassword.equals("")){

            Intent intent = new Intent(IntermediateActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();

        }else{
            tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    if(status == TextToSpeech.SUCCESS){
                        int result = tts.setLanguage(Locale.US);
                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e("TTS", "This Language is not supported");
                        }
                        speak("Welcome to voice mail. Please select from, 1Compose,  2 Exit and, 3 Logout ");
                    } else {
                        Log.e("TTS", "Initilization Failed!");
                    }
                }
            });
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listen();
            }
        };

        constraintLayout.setOnClickListener(listener);
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

            int reqCode = 100;

            startActivityForResult(i, reqCode);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(IntermediateActivity.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK && null != data){
            ArrayList result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            assert result != null;
            switch (result.get(0).toString()){
                case "compose":
                        Intent intent = new Intent(IntermediateActivity.this, MainActivity.class);
                        startActivity(intent);
                    break;
                case "exit":
                    exitFromApp();
                    break;
                case "logout":
                case "log out":
                    speak("Logging Out");
                    sharedPreferences.edit().putString("email", "").apply();
                    sharedPreferences.edit().putString("password", "").apply();
                    Intent intentLogout = new Intent(IntermediateActivity.this, HomeActivity.class);
                    startActivity(intentLogout);
                    finish();
                    break;
                default:
                    speak("Please select something from Compose, Exit or Logout");
                    break;
            }
        }
    }

    private void exitFromApp()
    {
        this.finishAffinity();
    }
}