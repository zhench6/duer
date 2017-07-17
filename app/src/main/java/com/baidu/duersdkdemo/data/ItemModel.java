package com.baidu.duersdkdemo.data;

/**
 * Created by zhench1 on 2017/6/19.
 */

public class ItemModel {
    public int type;
    private String content;

    public ItemModel() {
        content = "";
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
