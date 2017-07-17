package com.baidu.duersdkdemo.voicerecognition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.baidu.duersdkdemo.R;

public class VoiceRecognitionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_recognition);

        initViews();
    }

    private void initViews(){
        Button touchMode = (Button) findViewById(R.id.voice_touchmode_btn);
        touchMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),VoiceRecTouchActivity.class);
                startActivity(intent);
            }
        });

        Button vadMode = (Button) findViewById(R.id.voice_vadmode_btn);
        vadMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),VoiceRecVadActivity.class);
                startActivity(intent);
            }
        });

        Button fileMode = (Button) findViewById(R.id.voice_filemode_btn);
        fileMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),VoiceRecPcmStreamActivity.class);
                startActivity(intent);
            }
        });

        Button wakePcm = (Button) findViewById(R.id.voice_wakePcm_btn);
        wakePcm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),VoiceWakeUpActivity.class);
                startActivity(intent);
            }
        });
    }
}
