package com.kumar.estimote.homework01;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinodkumar on 10/4/2015.
 */
public class MessageListAdpater extends ArrayAdapter<Message> {
    private int mResource;
    private Context mContext;
    ArrayList<Message> alMsgDetails;

    public MessageListAdpater(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        alMsgDetails = (ArrayList<Message>) objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(mResource, parent, false);
        }

        Message msg = alMsgDetails.get(position);
        TextView tvName = (TextView) convertView
                .findViewById(R.id.tvName);



        tvName.setText(msg.getSenderName());
        TextView tvRegion = (TextView) convertView
                .findViewById(R.id.tvRegion);

        if(msg.getRegion()==1)
            tvRegion.setText("Region:1");
        else if(msg.getRegion()==2)
            tvRegion.setText("Region:2");
        else if(msg.getRegion()==3)
            tvRegion.setText("Region:3");

        TextView tvMsgPreview = (TextView) convertView
                .findViewById(R.id.tvmsgPreview);
        tvMsgPreview.setText(msg.getMessage());

        ImageView ivRead = (ImageView) convertView.findViewById(R.id.ivRead);
        if(msg.isRead()){
            ivRead.setImageResource(R.drawable.circle_grey);
        }

        ImageView ivLock = (ImageView) convertView.findViewById(R.id.ivLock);
        if(!msg.isLock()){

            ivLock.setImageResource(R.drawable.lock_open);
        } else {
            ivLock.setImageResource(R.drawable.lock);
        }



        TextView tvDate = (TextView) convertView
                .findViewById(R.id.tvDate);
        tvDate.setText("11/8/14, 4:55pm");





        return convertView;
    }
}
