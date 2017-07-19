package com.baidu.duersdkdemo.utils;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.duersdkdemo.R;
import com.baidu.duersdkdemo.data.ItemModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhench1 on 2017/6/19.
 */

public class ChatListAdapter extends BaseAdapter {

    private List<ItemModel> mData = new ArrayList();
    private LayoutInflater mInflater;
    private Context context;

    public ChatListAdapter(Context context, List<ItemModel> data) {
        this.context = context;
        mData = data;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //返回当前布局的样式type
    @Override
    public int getItemViewType(int position) {
        return mData.get(position).type;
    }

    //返回有多少个不同的布局
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void addItem(final ItemModel item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public ItemModel getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println("getView " + position + " " + convertView);
        ItemModel item = mData.get(position);
        int type = item.type;
        FirstViewHolder firstHolder = null;
        SecondViewHolder secondViewHolder = null;
        if (convertView == null) {
            switch (type) {
                case 0:
                    convertView = mInflater.inflate(R.layout.item_chat1, null);
                    firstHolder = new FirstViewHolder();
                    firstHolder.textView = (TextView)convertView.findViewById(R.id.content);
                    convertView.setTag(R.id.firstholder,firstHolder);
                    break;
                case 1:
                    convertView = mInflater.inflate(R.layout.item_chat2, null);
                    secondViewHolder = new SecondViewHolder();
                    secondViewHolder.textView = (TextView)convertView.findViewById(R.id.content);
                    convertView.setTag(R.id.secondholder,secondViewHolder);
                    break;
            }

        } else {
            switch (type) {
                case 0:
                    firstHolder = (FirstViewHolder)convertView.getTag(R.id.firstholder);
                    break;
                case 1:
                    secondViewHolder = (SecondViewHolder)convertView.getTag(R.id.secondholder);
                    break;
            }

        }
        switch (type) {
            case 0:
                firstHolder.textView.setText(mData.get(position).getContent());
                break;
            case 1:
                secondViewHolder.textView.setText(mData.get(position).getContent());
                break;
        }
        return convertView;
    }

    class FirstViewHolder{
        public TextView textView;
    }
    class SecondViewHolder{
        public TextView textView;
    }

}