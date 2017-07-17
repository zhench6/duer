package com.baidu.duersdkdemo;

import android.app.Application;
import android.text.TextUtils;
import android.widget.Toast;

import com.baidu.duersdk.DuerSDK;
import com.baidu.duersdk.DuerSDKFactory;
import com.baidu.duersdk.sdkconfig.SdkConfigInterface;
import com.baidu.duersdk.sdkverify.SdkVerifyManager;
import com.baidu.duersdk.utils.FileUtil;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by baidu on 16/8/15.
 */
public class DemoApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化duersdk
        DuerSDK duerSDK= DuerSDKFactory.getDuerSDK();

        //测试appid,appkey
        //dmEADE429AE4D51DB2
        //DF09B877EB23B1D0D35D22E0F63DBA19
//        String appid = "650DEBC2B99A4dA4";
//        String appkey= "2F4B662AF2064323A16122D702160F15";
        String appid = "dmEADE429AE4D51DB2";
        String appkey= "DF09B877EB23B1D0D35D22E0F63DBA19";

        //统计测试，sd卡配置文件动态设置appid,appkey
        File sdkConfigFile = new File(SdkConfigInterface.APP_CONFIGFILE);
        if(sdkConfigFile.isFile() && sdkConfigFile.exists()){
            try {
                String content = FileUtil.getFileOutputString(SdkConfigInterface.APP_CONFIGFILE);
                JSONObject contentJson = new JSONObject(content);
                String fileAppId = contentJson.optString("appid");
                String fileAppKey= contentJson.optString("appkey");
                if(!TextUtils.isEmpty(fileAppId) && !TextUtils.isEmpty(fileAppKey)){
                    appid = fileAppId;
                    appkey = fileAppKey;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //初始化sdk
        duerSDK.initSDK(DemoApplication.this,appid, appkey);
    }
}
