package com.baidu.duersdkdemo.voicerecognition;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.duersdk.DuerSDK;
import com.baidu.duersdk.DuerSDKFactory;
import com.baidu.duersdk.utils.AppLogger;
import com.baidu.duersdk.voice.VoiceInterface;
import com.baidu.duersdkdemo.R;

import org.json.JSONObject;

public class VoiceRecVadActivity extends Activity {
    static final String TAG = "VoiceRecVadActivity";

    private DuerSDK duserSdk = null;
    private TextView recognitionResultTxt;
    private Button mVoiceVadBtn;
    private Button mVoiceResultBtn;
    private TextView voiceVolumeTxt;
    private TextView partialResultTxt;
    protected Handler handler = new Handler();

    private VoiceInterface.VOICERESULTMODE voiceResultMode = VoiceInterface.VOICERESULTMODE.VOICE_ONLY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_rec_vad);
        duserSdk = DuerSDKFactory.getDuerSDK();
        initView();
        initClickListen();
    }

    private void initView() {
        recognitionResultTxt = (TextView) findViewById(R.id.recognitionResultTxt);
        mVoiceVadBtn = (Button) findViewById(R.id.voice_vad_btn);
        mVoiceResultBtn = (Button) findViewById(R.id.voice_voiceresult_btn);
        voiceVolumeTxt = (TextView) findViewById(R.id.voiceVolumeTxt);
        partialResultTxt = (TextView) findViewById(R.id.partialResultTxt);
    }

    private void initClickListen() {
        mVoiceVadBtn.setOnClickListener(new RecognizeVadListener());
        mVoiceVadBtn.setTag(new Boolean(false));
        mVoiceResultBtn.setOnClickListener(new RecognizeResultListener());
    }

    public class RecognizeVadListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if((Boolean) mVoiceVadBtn.getTag()) {
                mVoiceVadBtn.setText("点一下说话-vad（度秘sdk录音）");
                mVoiceVadBtn.setTag(false);
                duserSdk.getVoiceRecognize().cancelRecognition(VoiceRecVadActivity.this);
            }else {
                mVoiceVadBtn.setText("正在聆听...");
                mVoiceVadBtn.setTag(true);
                VoiceInterface.VoiceParam voiceParam = new VoiceInterface.VoiceParam();
                voiceParam.setVoiceMode(VoiceInterface.VOICEMODE.AUTO_REC);
                voiceParam.setVoiceResultMode(voiceResultMode);
                if(voiceResultMode== VoiceInterface.VOICERESULTMODE.VOICE_DUER){
                    //增加一些参数
                    try {
                        JSONObject duerParam = new JSONObject();
                        //设置位置信息
                        // 坐标系名称 wgs84为标准经纬度
                        duerParam.put("location_system","wgs84");
                        // 经度；double类型
                        duerParam.put("longitude",116.388171f);
                        // 纬度；double类型
                        duerParam.put("latitude",39.931535f);
                        voiceParam.setExtraParam(duerParam.toString());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                duserSdk.getVoiceRecognize().startRecognition(VoiceRecVadActivity.this, voiceParam, listener);
            }
        }
    }

    public class RecognizeResultListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(voiceResultMode == VoiceInterface.VOICERESULTMODE.VOICE_ONLY){
                voiceResultMode = VoiceInterface.VOICERESULTMODE.VOICE_DUER;
                mVoiceResultBtn.setText("返回Duer识别结果(点击切换)");
            }
            else if(voiceResultMode == VoiceInterface.VOICERESULTMODE.VOICE_DUER){
                voiceResultMode = VoiceInterface.VOICERESULTMODE.VOICE_ONLY;
                mVoiceResultBtn.setText("返回语音识别结果(点击切换)");
            }
        }
    }

    VoiceInterface.IVoiceEventListener listener = new VoiceInterface.IVoiceEventListener() {
        @Override
        public void onVoiceEvent(final VoiceInterface.VoiceResult voiceResult) {
            if (null != voiceResult) {
                AppLogger.v(TAG, "sn:"+voiceResult.getSpeechId()+" status:" + voiceResult.getStatus() +" speak:"+voiceResult.getSpeakText()
                        +" duertxt:"+voiceResult.getDuerResult()+" time:"+System.currentTimeMillis());
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
                                    recognitionResultTxt.append("最终语音结果 :" + voiceResult.getSpeakText() + " \n");
                                    try {
                                        if(TextUtils.isEmpty(voiceResult.getDuerResult())) {
                                            recognitionResultTxt.append("最终Duer结果 : 无结果" + " \n");
                                        }
                                        else {
                                            JSONObject duerJson = new JSONObject(voiceResult.getDuerResult());
                                            String duerFormate  = duerJson.toString(4);
                                            recognitionResultTxt.append("最终Duer结果 :" + duerFormate + " \n");
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    mVoiceVadBtn.setText("点一下说话-vad（度秘sdk录音）");
                                    mVoiceVadBtn.setTag(false);
                                }
                            }
                        };
                        handler.postDelayed(mResultRunnable, 500);
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

                        mVoiceVadBtn.setText("点一下说话-vad（度秘sdk录音）");
                        mVoiceVadBtn.setTag(false);
                    }
                    break;
                    default: {
                        break;
                    }
                }
            }
        }
    };
}

