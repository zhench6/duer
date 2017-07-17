package com.baidu.duersdkdemo.voicerecognition;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.duersdk.DuerSDK;
import com.baidu.duersdk.DuerSDKFactory;
import com.baidu.duersdk.utils.AppLogger;
import com.baidu.duersdk.voice.VoiceInterface;
import com.baidu.duersdkdemo.R;

import org.json.JSONObject;

import java.io.FileInputStream;

public class VoiceWakeUpActivity extends Activity {
    static final String TAG = "VoiceWakeUpActivity";
    private Button voiceWakeupBtn;
    private DuerSDK duserSdk = null;
    private TextView recognitionResultTxt;
    private Button mVoicePcmFileBtn;
    private TextView voiceVolumeTxt;
    private TextView partialResultTxt;
    protected Handler handler = new Handler();

    boolean isRecording = false;
    public static VoiceWakeUpActivity.RecordThread mRecordingThread = null;

    FileInputStream fi = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_wake_up);

        initViews();
        initClickListen();
    }

    private void initViews(){
        recognitionResultTxt = (TextView) findViewById(R.id.recognitionResultTxt);
        voiceWakeupBtn = (Button)findViewById(R.id.voice_wakeup_btn);
    }

    private void initClickListen() {
        voiceWakeupBtn.setOnClickListener(new VoiceWakeUpActivity.RecognizeStreamClickListener());
        voiceWakeupBtn.setTag(new Boolean(false));
    }

    /**
     * 持续塞byte流模式
     */
    public class RecognizeStreamClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if ((Boolean) voiceWakeupBtn.getTag()) {
                voiceWakeupBtn.setText("开启流唤醒");
                voiceWakeupBtn.setTag(false);
                //打断当前的识别流程
                stopRecording();
                VoiceInterface.VoiceParam voiceParam = new VoiceInterface.VoiceParam();
                voiceParam.setVoiceMode(VoiceInterface.VOICEMODE.AUTO_REC);
                voiceParam.setKeyworld("");
                DuerSDKFactory.getDuerSDK().getVoiceRecognize().stopWakeUp();
            } else {
                voiceWakeupBtn.setText("正在唤醒...");
                voiceWakeupBtn.setTag(true);

                DuerSDKFactory.getDuerSDK().getVoiceRecognize().registerWpEventManagerListener(getApplicationContext(),  new VoiceInterface.IWakeUpEventListener() {
                    @Override
                    public void onEvent(String name, String params, byte[] data, int offset, int length) {
                        try{
                            AppLogger.i(TAG, "唤醒返回数据为：name="+name+" params="+params+" data.size="+(data!= null?data.length:"null")+" offset="+offset+" length="+length);
                            //每次唤醒成功，将会回调name=wp.data的时间，被激活的唤醒词在params的word字段
                            if("wp.data".equals(name)){
                                if(!TextUtils.isEmpty(params)) {
                                    JSONObject jsonObject = new JSONObject(params);
                                    //根据返回的错误码判断是否有正确结果
                                    //拿到唤醒词
                                    String word = jsonObject.getString("word");
                                    AppLogger.i(TAG, "results:" + word.toString() + " " + System.currentTimeMillis());
                                    recognitionResultTxt.append("\n唤醒结果："+word.toString());
                                    Toast.makeText(getApplicationContext(),"语音唤醒："+word.toString(),Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    AppLogger.i(TAG, "results: params = null");
                                }
                            }else if("wp.exit".equals(name)){
                                //唤醒已经停止
                                AppLogger.i(TAG, "唤醒停止");
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            //唤醒已经停止
                            AppLogger.i(TAG, "语音唤醒出错");
                        }
                    }
                });

                VoiceInterface.VoiceParam voiceParam = new VoiceInterface.VoiceParam();
                voiceParam.setWakemode(VoiceInterface.WAKEMODE.WAKEBYTESTREAM);

                DuerSDKFactory.getDuerSDK().getVoiceRecognize().startWakeUp(getApplicationContext(),voiceParam);
                startRecording();
            }
        }
    }

    /**
     * 开始录音
     */
    private void startRecording() {
        if (mRecordingThread == null) {
            Log.i(TAG, "new a thread");
            mRecordingThread = new VoiceWakeUpActivity.RecordThread();
        }
        Log.i(TAG, "start a thread");
        mRecordingThread.start();
        isRecording = true;
    }

    /**
     * 停止录音
     */
    private void stopRecording() {
        if (mRecordingThread != null) {
            mRecordingThread.pause();
            mRecordingThread = null;
        }
        isRecording = false;
    }

    /**
     * 收音线程
     */
    public class RecordThread extends Thread {
        private boolean isRun = false;

        private AudioRecord audioRecord;
        private int bufferSize;
        private int SAMPLE_RATE_HZ = 16000;
        private final Object mLock = new Object();

        public RecordThread() {
            super();
            Log.i(TAG, "thread is initializing");
            bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_HZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_HZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        }

        public void run() {
            Log.i(TAG, "thread run");
            super.run();
            isRun = true;
            try {
                if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                    audioRecord.startRecording();
                    //用于读取的buffer
                    byte[] buffer = new byte[bufferSize];

                    while (isRun) {
                        int lengyh = audioRecord.read(buffer, 0, bufferSize);
                        Log.i(TAG, "microphone write buffer:" + lengyh);
                        try {
                            boolean isSucess = DuerSDKFactory.getDuerSDK().getVoiceRecognize().writeWakeByte(buffer,lengyh,16000);
                            Log.i(TAG, "microphone write sucess:" + isSucess);
                        }catch (Exception error){
                            Log.i(TAG, "microphone write buffer: error");
                        }
                    }

                    audioRecord.stop();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public void pause() {
            isRun = false;

            Log.i(TAG, "thread pause");
        }

        public void start() {
            if (!isRecording) {
                super.start();
            }
        }
    }
}
