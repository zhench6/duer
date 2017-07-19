package com.baidu.duersdkdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.baidu.duersdkdemo.R;

/**
 * Created by zhench1 on 2017/7/19.
 */

public class HomeActivity extends BaseActivity {
    private SwitchCompat mQuickSwitch;
    private TextView switchTv;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    public static String SP_NAME = "MyConfig";
    private static final String QUICK_START_ACTION = "android.settings.QUICK_START";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
    }

    private void initView() {
        sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        editor=sp.edit();
        mQuickSwitch= (SwitchCompat) findViewById(R.id.quick_switch);
        switchTv = (TextView)findViewById(R.id.switchTv);
        String homeAssistantTv=getResources().getString(R.string.app_name);
        boolean flag=sp.getBoolean("Quick",true);
        mQuickSwitch.setChecked(flag);
        Intent intent = new Intent();
        intent.setAction(QUICK_START_ACTION);
        intent.putExtra("isChecked", flag);
        this.sendBroadcast(intent);
        mQuickSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent();
                intent.setAction(QUICK_START_ACTION);
                intent.putExtra("isChecked", isChecked);
                HomeActivity.this.sendBroadcast(intent);
                if (editor != null){
                    editor.putBoolean("Quick",isChecked).apply();
                }
                switchTv.setText(isChecked ? getResources().getString(R.string.on) : getResources().getString(R.string.off));
            }
        });
    }
}
