package com.baidu.duersdkdemo.featuremodel;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.android.common.logging.Log;
import com.baidu.duersdk.DuerSDKFactory;
import com.baidu.duersdk.tts.TTSInterface;
import com.baidu.duersdk.utils.AppLogger;
import com.baidu.duersdkdemo.R;

import java.util.UUID;

/**
 * Created by yangrui09 on 2016/8/24.
 */
public class TTSTestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tts_test);
        //设置tts引擎参数
        setTTSEngine();

        Button playBtn = (Button)findViewById(R.id.playBtn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = ((EditText)findViewById(R.id.sigle_line_input)).getText().toString();
                text = "<?bd_tts_xml ver=\"1.0\" enc=\"gb2312\"?>您好，欢迎试用<voice name=\"std-F7-HQ\">百度语音合成系统。</voice>欢迎提出您的建议。";
                DuerSDKFactory.getDuerSDK().getSpeech().openTTS();
                String uuid = UUID.randomUUID().toString();
                DuerSDKFactory.getDuerSDK().getSpeech().play(text,uuid);
                Log.i("TTSTestActivity","utteranceId:"+uuid);
            }
        });

        Button stopBtn = (Button)findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DuerSDKFactory.getDuerSDK().getSpeech().stop();
            }
        });

        Button playMultiBtn = (Button)findViewById(R.id.playMultiBtn);
        playMultiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DuerSDKFactory.getDuerSDK().getSpeech().openTTS();
                playMultiLine();
            }
        });
    }

    public void setTTSEngine(){
        //设置参数
        TTSInterface.TTSParam ttsParam = new TTSInterface.TTSParam();
        ttsParam.setVolume(5);
        ttsParam.setSpeed(5);
        ttsParam.setSpeeaker(1);

        //请在yuyin.baidu.com上申请自己的appid，apikey，scretkey，选中语音合成服务
        ttsParam.setAppId("9150861");
        ttsParam.setApikey("iv1FKHmdNjcQfQhFphvm7R1s");
        ttsParam.setSecretkey("540e9bba19d418d2290c40015542d2d7");
        ttsParam.setAudioRate(TTSInterface.TTSParam.AUDIO_BITRATE_AMR_6K6);

        DuerSDKFactory.getDuerSDK().getSpeech().initTTS(getApplicationContext(), ttsParam, new TTSInterface.InitTTSListener() {
            @Override
            public void onInitResult(int errorCode, String errorMessage) {
                Toast.makeText(getApplicationContext(),"初始化结果:errorCode="+errorCode+" errorMessage="+errorMessage,Toast.LENGTH_SHORT).show();
            }
        });

        //注册事件回调
        DuerSDKFactory.getDuerSDK().getSpeech().addTTSStateListener(new TTSInterface.ITTSListener() {
            @Override
            public void onSynthesizeStart(String utteranceId){
                AppLogger.i("TTSTestActivity","onSynthesizeStart: utteranceId="+utteranceId);
            }

            @Override
            public void onSynthesizeDataArrived(String utteranceId, byte[] audioData, int progress) {
                AppLogger.i("TTSTestActivity","onSynthesizeDataArrived: utteranceId="+utteranceId);
            }

            @Override
            public void onSynthesizeFinish(String utteranceId) {
                AppLogger.i("TTSTestActivity","onSynthesizeFinish: utteranceId="+utteranceId);
            }

            @Override
            public void onSpeechStart(String utteranceId) {
                AppLogger.i("TTSTestActivity","onSpeechStart: utteranceId="+utteranceId);
            }

            @Override
            public void onSpeechProgressChanged(String utteranceId, int progress) {
                AppLogger.i("TTSTestActivity","onSpeechProgressChanged: utteranceId="+utteranceId);
            }

            @Override
            public void onSpeechFinish(String utteranceId) {
                AppLogger.i("TTSTestActivity","onSpeechFinish: utteranceId="+utteranceId);
            }

            @Override
            public void onError(String utteranceId, TTSInterface.TtsError error) {
                AppLogger.i("TTSTestActivity","onError: utteranceId="+utteranceId + " error:"+error.toString());
            }
        });
    }

    public void playMultiLine(){
        String text1 = ((EditText)findViewById(R.id.multi_line_input_1)).getText().toString();
        String text2 = ((EditText)findViewById(R.id.multi_line_input_2)).getText().toString();
        String text3 = ((EditText)findViewById(R.id.multi_line_input_3)).getText().toString();
        String text4 = ((EditText)findViewById(R.id.multi_line_input_4)).getText().toString();
        DuerSDKFactory.getDuerSDK().getSpeech().play(text1);
        DuerSDKFactory.getDuerSDK().getSpeech().play(text2);
        DuerSDKFactory.getDuerSDK().getSpeech().play(text3);
        DuerSDKFactory.getDuerSDK().getSpeech().play(text4);
    }
}
