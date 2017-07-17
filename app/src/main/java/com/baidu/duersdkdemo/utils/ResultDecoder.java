package com.baidu.duersdkdemo.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by zhench1 on 2017/6/20.
 */

public class ResultDecoder {
    public static String decodeJson(String input) {
        StringBuilder sb = new StringBuilder();
        try {
            JSONObject resultInput = new JSONObject(input);
            String botId = null;
            String type = null;
            String nlu = null;
            String repply = null;

            if (resultInput.has("result")) {
                JSONObject result = (JSONObject) resultInput.get("result");
                botId = (String) result.getString("bot_id");
                sb.append("bot_id: "+botId+"\n");
                if (result.has("resource")) {
                    type = result.getJSONObject("resource").getString("type");
                }
                sb.append("type: "+type+"\n");
                if (result.has("nlu")) {
                    nlu = result.getJSONObject("nlu").getString("domain");
                }
                sb.append("nluDomain: "+nlu+"\n");
                if (result.has("views")) {
                    JSONArray array = result.getJSONArray("views");
                    for (int i = 0; i < array.length(); i++){
                        JSONObject obj = array.getJSONObject(i);
                        if("txt".equals(obj.getString("type"))){
                            repply = obj.getString("content");
                            sb.append(repply+"\n");
                        }
                        if("list".equals(obj.getString("type"))){
                            JSONArray ar = obj.getJSONArray("list");
                            for (int j = 0; j < ar.length(); j++){
                                JSONObject objj = ar.getJSONObject(j);
                                repply = obj.getString("title");
                                repply = repply + "\n" +obj.getString("summary");
                                sb.append(repply+"\n");
                            }
                        }
                        if("image".equals(obj.getString("type"))){
                            JSONArray ar = obj.getJSONArray("list");
                            repply = "please view image on:";
                            for (int j = 0; j < ar.length(); j++){
                                JSONObject objj = ar.getJSONObject(j);
                                repply = repply + "\n" +objj.getString("src");
                                sb.append(repply+"\n");
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
