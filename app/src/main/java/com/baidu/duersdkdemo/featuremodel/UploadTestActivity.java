package com.baidu.duersdkdemo.featuremodel;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.duersdk.DuerSDKFactory;
import com.baidu.duersdk.upload.UploadInterface;
import com.baidu.duersdkdemo.R;
import com.baidu.duersdkdemo.utils.ContactsChoiceUtil;

import org.json.JSONArray;
import org.json.JSONObject;

public class UploadTestActivity extends Activity {

    //上传联系人按钮
    private Button uploadContactsBtn;
    //上传音乐按钮
    private Button uploadSongsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_test);

        initViews();
    }

    /**
     * 初始化界面
     */
    private void initViews() {
        uploadContactsBtn = (Button) findViewById(R.id.uploadContactsBtn);
        uploadSongsBtn = (Button) findViewById(R.id.uploadSongsBtn);

        uploadContactsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadContacts();
            }
        });

        uploadSongsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadSongs();
            }
        });
    }

    /**
     * 上传音乐
     */
    private void uploadSongs() {
        try {
            JSONArray songsArr = new JSONArray();
            {
                JSONObject song = new JSONObject();
                song.put("songname", "忘情水");
                song.put("singername", "刘德华");
                songsArr.put(song);
            }
            {
                JSONObject song = new JSONObject();
                song.put("songname", "大城小爱");
                song.put("singername", "王力宏");
                songsArr.put(song);
            }

            DuerSDKFactory.getDuerSDK().getUpload().uploadInfo(UploadInterface.UploadType.SONGS, songsArr.toString(),
                    getApplicationContext(), new UploadInterface.UploadCallBack() {
                        @Override
                        public void onSucess() {
                            Toast.makeText(getApplicationContext(), "上传音乐成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int errCode, String msg) {
                            Toast.makeText(getApplicationContext(), "上传音乐失败：errCode：" + errCode, Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传联系人
     */
    private void uploadContacts() {
        try {
            ContactsChoiceUtil contactsChoiceUtil = new ContactsChoiceUtil();
            final String jsonReq = contactsChoiceUtil.getAllContacts(UploadTestActivity.this);

            DuerSDKFactory.getDuerSDK().getUpload().uploadInfo(UploadInterface.UploadType.CONTACTS, jsonReq,
                    getApplicationContext(), new UploadInterface.UploadCallBack() {
                        @Override
                        public void onSucess() {
                            Toast.makeText(getApplicationContext(), "上传联系人成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int errCode, String msg) {
                            Toast.makeText(getApplicationContext(), "上传联系人失败：errCode：" + errCode, Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
