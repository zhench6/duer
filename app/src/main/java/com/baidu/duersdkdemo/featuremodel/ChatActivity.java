package com.baidu.duersdkdemo.featuremodel;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.duersdk.DuerSDK;
import com.baidu.duersdk.DuerSDKFactory;
import com.baidu.duersdk.datas.DuerMessage;
import com.baidu.duersdk.message.IReceiveMessageListener;
import com.baidu.duersdk.message.ISendMessageFinishListener;
import com.baidu.duersdk.message.SendMessageData;
import com.baidu.duersdk.utils.AppLogger;
import com.baidu.duersdk.voice.VoiceInterface;
import com.baidu.duersdkdemo.R;
import com.baidu.duersdkdemo.data.ItemModel;
import com.baidu.duersdkdemo.utils.ChatListAdapter;
import com.baidu.duersdkdemo.utils.ResultDecoder;
import com.baidu.duersdkdemo.voicerecognition.VoiceRecTouchActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhench1 on 2017/6/19.
 */

public class ChatActivity extends Activity implements View.OnClickListener{

    private Button sendBtn;
    private Button changeToVoiceBtn;
    private Button changeToTextBtn;
    private Button speakBtn;
    private EditText editText;
    private TextView voiceStatusTV;
    private ListView chatListView;
    private List<ItemModel> chatlist;
    private ChatListAdapter mAdapter;

    private LinearLayout bottomText;
    private LinearLayout bottomSpeak;

    private DuerSDK duserSdk = null;

    private float currentPosY = 0;
    protected Handler handler = new Handler();

    public static VoiceInterface.VOICERESULTMODE  voiceResultMode = VoiceInterface.VOICERESULTMODE.VOICE_DUER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        duserSdk = DuerSDKFactory.getDuerSDK();
        init();

    }

    private void init(){
        chatlist = new ArrayList();
        sendBtn = (Button) findViewById(R.id.send_tbn);
        chatListView = (ListView) findViewById(R.id.listview);
        editText = (EditText) findViewById(R.id.input);
        chatListView = (ListView) findViewById(R.id.listview);

        changeToVoiceBtn = (Button) findViewById(R.id.change_btn);
        changeToTextBtn = (Button) findViewById(R.id.change_btn2);
        speakBtn = (Button) findViewById(R.id.speak_tbn);

        bottomSpeak = (LinearLayout) findViewById(R.id.bottom2);
        bottomText = (LinearLayout) findViewById(R.id.bottom1);
        voiceStatusTV = (TextView) findViewById(R.id.voicestatus);

//        bottomSpeak.setVisibility(View.GONE);
//        bottomText.setVisibility(View.VISIBLE);
        mAdapter = new ChatListAdapter(this, chatlist);
        chatListView.setAdapter(mAdapter);
        sendBtn.setOnClickListener(this);
        changeToTextBtn.setOnClickListener(this);
        changeToVoiceBtn.setOnClickListener(this);
        speakBtn.setOnTouchListener(touchListener);

        duserSdk.getMessageEngine().setReceiveMessageListener(messageListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_tbn:
                sendMsg();
                editText.setText("");
                break;
            case R.id.change_btn:
                bottomText.setVisibility(View.GONE);
                bottomSpeak.setVisibility(View.VISIBLE);
                break;
            case R.id.change_btn2:
                bottomSpeak.setVisibility(View.GONE);
                bottomText.setVisibility(View.VISIBLE);
                break;
        }
    }

    final IReceiveMessageListener messageListener = new IReceiveMessageListener() {

        @Override
        public void messageReceive(String megSourceString) {
            ItemModel result = new ItemModel();
            try {
                JSONObject duerMessageJson = new JSONObject(megSourceString);
                result.setContent(ResultDecoder.decodeJson(duerMessageJson.toString(4)));
//                result.setContent(duerMessageJson.toString(4));
            } catch (Exception e) {
                e.printStackTrace();
            }
            result.setType(0);
            mAdapter.addItem(result);
        }

    };

    public void sendMsg() {
        final String sendText = editText.getText().toString();
        if (!TextUtils.isEmpty(sendText)) {
            SendMessageData sendMessageData = new SendMessageData();
            //需要查下的 query
            sendMessageData.setQuery(sendText);
            //设置经纬度信息,坐标系名称
            sendMessageData.setLocalSystemName("wgs84");
            //经度
            sendMessageData.setLocalLongitude(116.388171f);
            //纬度
            sendMessageData.setLocalLatitude(39.931535f);

            DuerSDKFactory.getDuerSDK().getMessageEngine().sendMessage(sendMessageData,
                    new ISendMessageFinishListener() {
                        @Override
                        public void messageSendStatus(MSG_SENDSTATUS status, DuerMessage duerMessage,
                                                      JSONObject errorJson) {
//                            AppLogger.i(TAG, "duerMessage:" + sendText + "messageSendStatus:" + status
//                                    + " errorJson:" + errorJson);
                            try {
                                if (status == MSG_SENDSTATUS.MSG_SENDFAILURE) {

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            ItemModel result = new ItemModel();
            result.setType(1);
            result.setContent(sendText);
            mAdapter.addItem(result);
        } else {
            Toast.makeText(getApplicationContext(), "消息不能为空", Toast.LENGTH_SHORT).show();
        }
    }


    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
//                    AppLogger.i(TAG,"MotionEvent.ACTION_DOWN");
                    speakBtn.setText("正在识别");
                    VoiceInterface.VoiceParam voiceParam = new VoiceInterface.VoiceParam();
                    voiceParam.setVoiceMode(VoiceInterface.VOICEMODE.TOUCH);
                    voiceParam.setVoiceResultMode(voiceResultMode);
                    if(voiceResultMode== VoiceInterface.VOICERESULTMODE.VOICE_DUER){
                        //增加一些参数
                        try {
                            JSONObject duerParam = new JSONObject();
                            //设置位置洗洗脑
                            duerParam.put("location_system","wgs84");
                            // 经度；float
                            duerParam.put("longitude",116.388171f);
                            // 纬度；float
                            duerParam.put("latitude",39.931535f);
                            voiceParam.setExtraParam(duerParam.toString());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    voiceParam.setKeyworld("");
                    try {
                        JSONObject debugJson = null;
                        debugJson = new JSONObject();
                        debugJson.put("outfile","/sdcard/baidu/duersdk/output.pcm");
                        voiceParam.setDebugParam(debugJson.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    duserSdk.getVoiceRecognize().startRecognition(ChatActivity.this, voiceParam, listener);
                    break;
                case MotionEvent.ACTION_MOVE:
//                    AppLogger.i(TAG,"MotionEvent.ACTION_MOVE");
                    currentPosY = event.getY();
                    AppLogger.v("voice", "currentPosY y:" + currentPosY);
                    speakBtn.getHeight();
                    if (currentPosY < 0) {
                        speakBtn.setText("取消 识别");
                    } else {
                        speakBtn.setText("松开 识别");
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
//                    AppLogger.i(TAG,"MotionEvent.ACTION_UP");
                    speakBtn.setText("Press to speak");
                    currentPosY = event.getY();
                    if (currentPosY < 0) {
                        // 取消搜索
                        duserSdk.getVoiceRecognize().cancelRecognition(ChatActivity.this);
                    } else {
                        // 需要结束搜索
                        duserSdk.getVoiceRecognize().recognitionFinish(ChatActivity.this);
                    }
                    break;
            }
            return false;
        }
    };


    VoiceInterface.IVoiceEventListener listener = new VoiceInterface.IVoiceEventListener() {
        @Override
        public void onVoiceEvent(final VoiceInterface.VoiceResult voiceResult) {
            if (null != voiceResult) {
//                AppLogger.v(TAG, "" + voiceResult.getStatus() +" time:"+System.currentTimeMillis());
                switch (voiceResult.getStatus()) {
                    /** 引擎就绪 **/
                    case READY: {
//                        recognitionResultTxt.setText("");
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
                        voiceStatusTV.setText("上屏状态 :" + voiceResult.getSpeakText());
                    }
                    break;
                    /** 上屏语音音量 **/
                    case VOLUME: {
//                        voiceVolumeTxt.setText("语音音量 :" + voiceResult.getVolume());
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
//                                    AppLogger.v(TAG, "发送的文本：" + voiceResult.getSpeakText());
//                                    recognitionResultTxt.append("最终结果 :" + voiceResult.getSpeakText() + " \n");
                                    ItemModel voice = new ItemModel();
                                    voice.setType(1);
                                    voice.setContent(voiceResult.getSpeakText());
                                    mAdapter.addItem(voice);
                                    try {
                                        if(TextUtils.isEmpty(voiceResult.getDuerResult())) {
//                                            recognitionResultTxt.append("最终Duer结果 : 无结果" + " \n");
                                            ItemModel voiceback = new ItemModel();
                                            voiceback.setType(0);
                                            voiceback.setContent("no result");
                                            mAdapter.addItem(voiceback);
                                        }
                                        else {
                                            JSONObject duerJson = new JSONObject(voiceResult.getDuerResult());
                                            String duerFormate  = duerJson.toString(4);
//                                            recognitionResultTxt.append("最终Duer结果 :" + duerFormate + " \n");
                                            ItemModel voiceback = new ItemModel();
                                            voiceback.setType(0);
                                            voiceback.setContent(ResultDecoder.decodeJson(duerFormate));
                                            mAdapter.addItem(voiceback);
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
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

                        StringBuilder errorsb = new StringBuilder();

                        errorsb.append("识别错误---errorCode - subErrorCode :" + (voiceResult.getErrorCode() +" - " +voiceResult.getSubErrorCode()) + " \n");
                        errorsb.append("识别错误---主标题 :" + mErrMsg + " \n");
                        errorsb.append("识别错误---附标题 :" + mErrInfo + " \n");

                        ItemModel error = new ItemModel();
                        error.setType(0);
                        error.setContent(errorsb.toString());
                        mAdapter.addItem(error);
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
