package com.baidu.duersdkdemo.featuremodel;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.duersdk.DuerSDKFactory;
import com.baidu.duersdk.datas.DuerMessage;
import com.baidu.duersdk.message.IReceiveMessageListener;
import com.baidu.duersdk.message.ISendMessageFinishListener;
import com.baidu.duersdk.message.SendMessageData;
import com.baidu.duersdk.utils.AppLogger;
import com.baidu.duersdkdemo.R;

import org.json.JSONObject;


 /**
 * Created by yangrui09 on 2016/8/24.
 */
public class SendMessageTestActivity extends Activity {

    static final String TAG = "SendMessageTestActivity";

    //显示收到消息
    private TextView messageResultTxt;

    //输入query
    private EditText inputQueryEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_msg);

        initView();

        DuerSDKFactory.getDuerSDK().getMessageEngine().setReceiveMessageListener(messageListener);

        
    }

    final IReceiveMessageListener messageListener = new IReceiveMessageListener() {

        @Override
        public void messageReceive(String megSourceString) {
            messageResultTxt.setText("");
            try {
                JSONObject duerMessageJson = new JSONObject(megSourceString);
                messageResultTxt.append(duerMessageJson.toString(4));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };
    private void demo() {
        boolean n = true;
        Log.d(TAG, "demo: ");
        Log.d(TAG, "demo: ");
    }

    private void initView() {
        messageResultTxt = (TextView) findViewById(R.id.messageResultTxt);
        inputQueryEt     = (EditText) (findViewById(R.id.inputQueryEt));
    }

    /**
     * 清空界面r
     */
    public void clearInfo(View view){
        messageResultTxt.setText("");
        inputQueryEt.setText("");
    }

    /**
     *
     * @param view
     */
    public void sendMsg(View view) {
        final String sendText = inputQueryEt.getText().toString();
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
                            AppLogger.i(TAG, "duerMessage:" + sendText + "messageSendStatus:" + status
                                    + " errorJson:" + errorJson);
                            try {
                                if (status == MSG_SENDSTATUS.MSG_SENDFAILURE) {

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "消息不能为空", Toast.LENGTH_SHORT).show();
        }
    }
}
