package com.kumar.estimote.homework01;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComposeAcitivity extends AppCompatActivity {

    TextView tvPerson, tvRegion;
    EditText etMessage;
    ImageView ivPerson, ivRegion;
    Button btnSend;
    ListView lviewUsers, lvRegions;
    ArrayList<String> aPersonList;
    ParseObject toUser;
    String Region = "";
    String replyUseremailId = "";
    String replyUserFullName = "";
    int region = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_acitivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvPerson = (TextView) findViewById(R.id.tvPerson);
        tvRegion = (TextView) findViewById(R.id.tvRegion);

        setSupportActionBar(toolbar);
        final ArrayList<String> aRegions = new ArrayList<String>();
        aRegions.add("Region1");
        aRegions.add("Region2");
        aRegions.add("Region3");
        Intent intentFromReadMsg = getIntent();
        if (intentFromReadMsg.getExtras() != null) {
            replyUseremailId = intentFromReadMsg.getExtras().getString("touser_key");
            replyUserFullName = intentFromReadMsg.getExtras().getString("tousername_key");
            tvPerson.setText(replyUserFullName);

            region = intentFromReadMsg.getExtras().getInt("region_key");
            tvRegion.setText(aRegions.get(region - 1));
        }


        ivPerson = (ImageView) findViewById(R.id.ivPerson);
        ivPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<ParseObject> uList = new ArrayList<ParseObject>();
                lviewUsers = new ListView(ComposeAcitivity.this);
                aPersonList = new ArrayList<String>();
                ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        if (e == null) {
                            for (ParseObject po : list) {
                                aPersonList.add(po.getString("UserFullName"));
                                uList.add(po);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                    ComposeAcitivity.this,
                                    android.R.layout.simple_list_item_1, aPersonList);
                            lviewUsers.setAdapter(adapter);
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    ComposeAcitivity.this);
                            builder.setTitle("Users");
                            builder.setCancelable(true);
                            builder.setView(lviewUsers);
                            final AlertDialog dialog = builder.create();
                            dialog.show();

                            lviewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    dialog.dismiss();
                                    tvPerson.setText(uList.get(position).getString("UserFullName"));
                                    toUser = uList.get(position);
                                }
                            });

                        } else {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
        //reading the region

        ivRegion = (ImageView) findViewById(R.id.ivRegion);
        ivRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvRegions = new ListView(ComposeAcitivity.this);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        ComposeAcitivity.this,
                        android.R.layout.simple_list_item_1, aRegions);
                lvRegions.setAdapter(adapter);
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        ComposeAcitivity.this);
                builder.setTitle("Regions");
                builder.setCancelable(true);
                builder.setView(lvRegions);
                final AlertDialog dialog = builder.create();
                dialog.show();
                lvRegions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dialog.dismiss();
                        tvRegion.setText(aRegions.get(position));
                        Region = aRegions.get(position);
                    }
                });
            }
        });
        btnSend = (Button) findViewById(R.id.btnSend);
        etMessage = (EditText) findViewById(R.id.etMessage);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Region.equalsIgnoreCase("Region1"))
                    region = 1;
                else if (Region.equalsIgnoreCase("Region2"))
                    region = 2;
                else if (Region.equalsIgnoreCase("Region3"))
                    region = 3;

                // if (etMessage.getText().length()==0||toUser == null||Region.equals("")) {
                if (etMessage.getText().length() == 0 || tvPerson.getText().length() == 0 || tvRegion.getText().length() == 0) {
                    Toast.makeText(
                            ComposeAcitivity.this, "Message is empty/User not selected/Region is not selected",
                            Toast.LENGTH_SHORT).show();

                    // return;
                } else {
                    ParseObject Message = new ParseObject(
                            "messages");

                    Message.put("message", etMessage.getText().toString());
                    Message.put("sender", ParseUser
                            .getCurrentUser().getString("emailId"));
                    if (toUser != null)
                        Message.put("receiver", toUser.getString("emailId"));
                    else if (!replyUseremailId.equals(""))
                        Message.put("receiver", replyUseremailId);
                    Message.put("isRead", false);
                    Message.put("isLock", true);
                    Message.put("region", region);
                    Message.put("senderName", ParseUser.getCurrentUser().getString("UserFullName"));
                    Message.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(ComposeAcitivity.this, "Sent", Toast.LENGTH_SHORT).show();
                                Map<String, Object> params = new HashMap<>();
                                params.put("email", toUser.get("username"));
                                params.put("message", "Message Received");
                                ParseCloud.callFunctionInBackground("pushToUserId", params, new FunctionCallback<Object>() {
                                    @Override
                                    public void done(Object o, ParseException e) {
                                        if (e == null)
                                            Toast.makeText(ComposeAcitivity.this, "Push sent", Toast.LENGTH_SHORT).show();
                                        else
                                            e.printStackTrace();
                                    }
                                });
                                finish();
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                }


            }
        });


    }

}
