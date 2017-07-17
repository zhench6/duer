package com.baidu.duersdkdemo.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class ContactsChoiceUtil {
	
    public String getAllContacts(Context context) throws JSONException {
        JSONArray array = new JSONArray();
        JSONObject object;
        ContentResolver resolver = context.getContentResolver();
        // 获取手机联系人  
        Cursor phoneCursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
                new String[]{ContactsContract.Contacts._ID,ContactsContract.Contacts.DISPLAY_NAME}, null, null, null);


        if (phoneCursor != null) {
            int column = phoneCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            while (phoneCursor.moveToNext() && column > -1) {
                String displayName = phoneCursor.getString(column);
                if(!TextUtils.isEmpty(displayName)) {
                    object = new JSONObject();
                    object.put("name",displayName);
                    array.put(object);
                }
               
            }
        }
        return array.toString();
    }
}
