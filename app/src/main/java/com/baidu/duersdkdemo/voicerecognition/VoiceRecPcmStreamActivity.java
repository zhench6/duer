package com.baidu.duersdkdemo.voicerecognition;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.duersdk.DuerSDK;
import com.baidu.duersdk.DuerSDKFactory;
import com.baidu.duersdk.utils.AppLogger;
import com.baidu.duersdk.voice.VoiceInterface;
import com.baidu.duersdkdemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;

public class VoiceRecPcmStreamActivity extends Activity {

    static final String TAG = "VoiceRecVadActivity";

    private DuerSDK duserSdk = null;
    private TextView recognitionResultTxt;
    private Button mVoicePcmFileBtn;
    private TextView voiceVolumeTxt;
    private TextView partialResultTxt;
    protected Handler handler = new Handler();

    boolean isRecording = false;
    public static RecordThread mRecordingThread = null;

    FileInputStream fi = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_rec_pcm_file);
        duserSdk = DuerSDKFactory.getDuerSDK();
        initView();
        initClickListen();
    }

    private void initView() {
        recognitionResultTxt = (TextView) findViewById(R.id.recognitionResultTxt);
        mVoicePcmFileBtn = (Button) findViewById(R.id.voice_file_btn);
        voiceVolumeTxt = (TextView) findViewById(R.id.voiceVolumeTxt);
        partialResultTxt = (TextView) findViewById(R.id.partialResultTxt);
    }

    private void initClickListen() {
        mVoicePcmFileBtn.setOnClickListener(new VoiceRecPcmStreamActivity.RecognizeStreamClickListener());
        mVoicePcmFileBtn.setTag(new Boolean(false));
    }

    /**
     * 持续塞byte流模式
     */
    public class RecognizeStreamClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if ((Boolean) mVoicePcmFileBtn.getTag()) {
                mVoicePcmFileBtn.setText("byte流持续识别");
                mVoicePcmFileBtn.setTag(false);
                //打断当前的识别流程
                stopRecording();
                duserSdk.getVoiceRecognize().cancelRecognition(VoiceRecPcmStreamActivity.this);
            } else {
                mVoicePcmFileBtn.setText("正在收音识别...");
                mVoicePcmFileBtn.setTag(true);
                //开始调用识别参数
                VoiceInterface.VoiceParam voiceParam = new VoiceInterface.VoiceParam();
                voiceParam.setVoiceMode(VoiceInterface.VOICEMODE.AUTO_REC);
                /**
                 * 支持导出pcm流和尾点设置的函数
                 */
                try {
                    JSONObject debugJson = null;
                    debugJson = new JSONObject();
                    debugJson.put("outfile", "/sdcard/baidu/duersdk/output.pcm");
                    voiceParam.setDebugParam(debugJson.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                voiceParam.setAudioInputMode(VoiceInterface.AUDIOINPUTMODE.INPUTBYTESTREAM);
                //通过下面的方法来写入数据
                duserSdk.getVoiceRecognize().startRecognition(VoiceRecPcmStreamActivity.this, voiceParam, listener);
                startRecording();
            }
        }
    }

    VoiceInterface.IVoiceEventListener listener = new VoiceInterface.IVoiceEventListener() {
        @Override
        public void onVoiceEvent(final VoiceInterface.VoiceResult voiceResult) {
            if (null != voiceResult) {
                AppLogger.v(TAG, "" + voiceResult.getStatus() + " time:" + System.currentTimeMillis());
                switch (voiceResult.getStatus()) {
                    /** 引擎就绪 **/
                    case READY: {
                        recognitionResultTxt.setText("");
                        // 可以加动画
                    }
                    break;
                    /** 引擎开始 **/
                    case BEGIN: {
                        // 可以加动画
                    }
                    break;
                    /** 上屏状态 **/
                    case PARTIAL: {
                        partialResultTxt.setText("上屏状态 :" + voiceResult.getSpeakText());
                    }
                    break;
                    /** 上屏语音音量 **/
                    case VOLUME: {
                        voiceVolumeTxt.setText("语音音量 :" + voiceResult.getVolume());
                    }
                    break;
                    /** 已经检测到语音终点，等待网络返回 **/
                    case REC_END: {
                        // 可以加动画
                    }
                    break;
                    /** 识别结束 **/
                    case FINISH: {
                        Runnable mResultRunnable = new Runnable() {
                            @Override
                            public void run() {
                                // 0:语音输入,1:键盘输入,2:编辑query,3:引导quare,4:重新发送
                                if (!TextUtils.isEmpty(voiceResult.getSpeakText())) {
                                    AppLogger.v(TAG, "发送的文本：" + voiceResult.getSpeakText());
                                    recognitionResultTxt.append("最终结果 :" + voiceResult.getSpeakText() + " \n");
                                    mVoicePcmFileBtn.setText("点击开始识别");
                                    mVoicePcmFileBtn.setTag(false);
                                }
                            }
                        };
                        handler.postDelayed(mResultRunnable, 500);
                        stopRecording();
                    }
                    break;
                    /** 识别错误 **/
                    case ERROR: {
                        String mErrMsg = "";
                        String mErrInfo = "";
                        int errorInfo = voiceResult.getErrorCode();
                        switch (errorInfo) {
                            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                            case SpeechRecognizer.ERROR_NETWORK:
                                mErrMsg = "网络出问题了";
                                mErrInfo = "请检查网络设置";
                                break;
                            case SpeechRecognizer.ERROR_AUDIO:
                            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                                mErrMsg = "麦克风貌似不可用哦";
                                mErrInfo = "请检查麦克风设置";
                                break;
                            case SpeechRecognizer.ERROR_SERVER:
                            case SpeechRecognizer.ERROR_CLIENT:
                            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                            case SpeechRecognizer.ERROR_NO_MATCH:
                            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                                mErrMsg = "抱歉，我没听清";
                                mErrInfo = "请说话大声些或换一个安静的环境再试试";
                                break;
                            default:
                                mErrMsg = "好像哪里不对劲";
                                mErrInfo = "建议再试一次";
                                break;
                        }
                        recognitionResultTxt.append("识别错误---主标题 :" + mErrMsg + " \n");
                        recognitionResultTxt.append("识别错误---附标题 :" + mErrInfo + " \n");
                        stopRecording();
                        mVoicePcmFileBtn.setText("点击开始识别");
                        mVoicePcmFileBtn.setTag(false);
                    }
                    break;
                    default: {
                        break;
                    }
                }
            }
        }
    };

    /**
     * 开始录音
     */
    private void startRecording() {
        if (mRecordingThread == null) {
            Log.i(TAG, "new a thread");
            mRecordingThread = new RecordThread();
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
                            boolean isSucess = DuerSDKFactory.getDuerSDK().getVoiceRecognize().writeAudioByte(buffer,lengyh,16000);
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
