package com.kumar.estimote.homework01;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadmsgActivity extends AppCompatActivity {

    TextView tvFrom,tvRegion;
    //EditText etMessage;
    TextView tvMessage;
    String replyUseremailId,replyUserFullName;

    int region;
    String deleteMesgId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readmsg);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    tvFrom = (TextView)findViewById(R.id.tvFrom);
        tvRegion = (TextView)findViewById(R.id.tvRegionRead);
        tvMessage = (TextView)findViewById(R.id.tvMessageRead);

        Message message = (Message) getIntent().getExtras().getSerializable("message_key");
        replyUseremailId = message.getSender();
        replyUserFullName = message.getSenderName();
        deleteMesgId = message.getObjectId();
        tvFrom.setText(message.getSenderName());
        region = message.getRegion();
        if(message.getRegion()==1)
        tvRegion.setText("Region1");
        else  if(message.getRegion()==2)
            tvRegion.setText("Region2");
        else  if(message.getRegion()==3)
            tvRegion.setText("Region3");

        tvMessage.setText(message.getMessage());

        //update isReadflag
        ParseQuery<ParseObject> updateQuery = ParseQuery.getQuery("messages");
        updateQuery.getInBackground(deleteMesgId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    parseObject.put("isRead", true);
                    parseObject.saveInBackground();
                }
            }
        });

        Map<String, Object> params = new HashMap<>();
        params.put("email", message.getSender());

        params.put("message", "Message Read by: " + ParseUser.getCurrentUser().get("UserFullName"));

        ParseCloud.callFunctionInBackground("pushToUserId", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object o, ParseException e) {
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_read, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id==R.id.action_discard){

            ParseQuery<ParseObject> deleteQuery = ParseQuery.getQuery("messages");
            deleteQuery.whereEqualTo("objectId", deleteMesgId);
            deleteQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {

                        for (ParseObject obj : list) {

                            obj.deleteInBackground();
                        }

                    } else {
                        e.printStackTrace();
                        Log.d("DeleteCityError", e.getLocalizedMessage());
                    }
                }
            });

            finish();

        }
        else if(id==R.id.action_reply){
            Intent intent = new Intent(ReadmsgActivity.this, ComposeAcitivity.class);
            intent.putExtra("touser_key",replyUseremailId);
            intent.putExtra("region_key",region);
            intent.putExtra("tousername_key",replyUserFullName);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}


