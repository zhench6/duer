package com.baidu.duersdkdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.duersdk.DuerSDKFactory;
import com.baidu.duersdk.DuerSDKImpl;
import com.baidu.duersdk.utils.PermissionUtil;
import com.baidu.duersdk.utils.Tools;
import com.baidu.duersdkdemo.featuremodel.ChatActivity;
import com.baidu.duersdkdemo.featuremodel.SendMessageTestActivity;
import com.baidu.duersdkdemo.featuremodel.TTSTestActivity;
import com.baidu.duersdkdemo.featuremodel.UploadTestActivity;
import com.baidu.duersdkdemo.voicerecognition.VoiceRecognitionActivity;

public class MainActivity extends Activity {

    Intent intent = new Intent();
    Class<?> cls;

    private Button ttsButton;
    private Button voiceBtn;
    private Button uploadBtn;
    private Button testBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        if (!Tools.isGpsOPen(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),"没有打开GPS",Toast.LENGTH_SHORT).show();
        }

        String[] permissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        PermissionUtil.getInstance().getPermission(MainActivity.this, permissions, new PermissionUtil.PermissionHandles() {
            @Override
            //点击允许时的回调
            public void allowed() {
                //只有当sdk中引入了定位模块请求定位才会有效,开启定位后需要在合适的时刻调用停止定位接口
                DuerSDKImpl.getLocation().requestLocation(false);
            }

            @Override
            //点击拒绝时的回调
            public void denied() {
                Toast.makeText(getApplicationContext(), "我被你拒绝了...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView(){
        ttsButton = (Button) findViewById(R.id.tts_btn);
        voiceBtn  = (Button) findViewById(R.id.voice_btn);
        uploadBtn = (Button) findViewById(R.id.upload_btn);
        testBtn = (Button) findViewById(R.id.test_btn);

        //是否有tts的能力
        boolean ttsEnable = DuerSDKFactory.getDuerSDK().getSpeech().isAvailable();
        if(!ttsEnable){
            ttsButton.setVisibility(View.GONE);
        }
        // 是否有语音识别的能力
        boolean voiceEnable = DuerSDKFactory.getDuerSDK().getVoiceRecognize().isAvailable();
        if(!voiceEnable){
            voiceBtn.setVisibility(View.GONE);
        }

        //是否有上传能力
        boolean uploadEnbale = DuerSDKFactory.getDuerSDK().getUpload().isAvailable();
        if (!uploadEnbale){
            uploadBtn.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //app退出后，停止定位
        DuerSDKImpl.getLocation().stopLocation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.gc();
    }

    public void ttsOnClick(View view){
        cls = TTSTestActivity.class;
        startActivity();
    }

    public void voiceSearOnClick(View view){
        cls = VoiceRecognitionActivity.class;
        startActivity();
    }

    public void sendMsgOnClick(View view){
        cls= SendMessageTestActivity.class;
        startActivity();
    }

    public void uploadOnClick(View view){
        cls= UploadTestActivity.class;
        startActivity();
    }

    public void testOnClick(View view){
        cls= ChatActivity.class;
        startActivity();
    }

    public void startActivity(){
        intent.setClass(this,cls);
        startActivity(intent);
    }
}
