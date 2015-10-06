package com.kumar.estimote.homework01;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InboxActivity extends AppCompatActivity {
    ArrayList<Message> alMessage;
    ListView llView;
    ArrayAdapter<Message> arrayAdapter;
    private BeaconManager beaconManager;
    private Region region;
    private Map<String, Integer> PLACES_BY_BEACONS;
    //private Handler mHandler = new Handler();
    int tempRegion = 0;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        alMessage = new ArrayList<Message>();
        llView = (ListView)findViewById(R.id.listView);
        Log.d("debug", "findIn2");
       // new DoBringDetails().execute("");
        Log.d("debug", "findIn3");

        llView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if(!alMessage.get(position).isLock) {
                Intent intent = new Intent(InboxActivity.this,
                        ReadmsgActivity.class);
                intent.putExtra("message_key", alMessage.get(position));
                startActivity(intent);
                //               }
            }
        });
        //beacon logic
        Map<String,Integer> placesByBeacons = new HashMap<>();
        placesByBeacons.put("41072:44931", 1);
        placesByBeacons.put("48320:58596", 2);
        placesByBeacons.put("15212:31506", 3);
        PLACES_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);


        beaconManager = new BeaconManager(this);
        // beaconManager.setForegroundScanPeriod(1000,5000);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {

                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);

                        Integer regionNo = placesNearBeacon(nearestBeacon);
                        if(tempRegion == regionNo)
                            count++;
                        else
                            tempRegion = regionNo;

                        if(count==20)
                        {

                            Log.d("debug","region ******* is "+regionNo);
                            ArrayList<Message> alTemp = new ArrayList<Message>();
                            for(Message msg:alMessage){
                                if(msg.getRegion()==regionNo.intValue() && msg.isLock()){

                                    Log.d("debug","message **"+msg.getMessage());
                                    Log.d("debug","region of mes **"+msg.getRegion());
                                    Log.d("debug","lock of mes"+msg.isLock());

                                    msg.setIsLock(false);
                                    ParseQuery<ParseObject> updateQuery = ParseQuery.getQuery("messages");
                                    updateQuery.getInBackground(msg.getObjectId(), new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(ParseObject parseObject, ParseException e) {
                                            if (e == null) {
                                                parseObject.put("isLock", false);
                                                parseObject.saveInBackground();
                                            }
                                        }
                                    });
                                }

                                alTemp.add(msg);

                            }

                            arrayAdapter = new MessageListAdpater(InboxActivity.this, R.layout.message_list_row,
                                    alTemp);
                            arrayAdapter.setNotifyOnChange(true);
                            llView.setAdapter(arrayAdapter);
                            count=0;
                        }
                    }
                }

        });
        region = new Region("Global region",
                "B9407F30-F5F8-466E-AFF9-25556B57FE6D", null, null);


    }

    public class DoBringDetails extends
            AsyncTask<String, Void, ArrayList<Message>> {


        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            Log.d("debug","findIn5");
            super.onPreExecute();
            pd = new ProgressDialog(InboxActivity.this);
            pd.setMessage("Loading Messages..");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected ArrayList<Message> doInBackground(String... params) {
            Log.d("debug","findIn4");


            // Log.d("debug", "length of arraylist " + alist.size());
            return getParseData();
        }



        @Override
        protected void onPostExecute(ArrayList<Message> result) {
            super.onPostExecute(result);
            alMessage = result;
            Log.d("demo", result.toString());

            arrayAdapter = new MessageListAdpater(InboxActivity.this, R.layout.message_list_row,
                    result);
            arrayAdapter.setNotifyOnChange(true);
            pd.dismiss();
            llView.setAdapter(arrayAdapter);

        }
    }


    @Override
    protected void onResume() {

        super.onResume();

        new DoBringDetails().execute();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    @Override
    protected void onPause() {
        try {
            beaconManager.stopRanging(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        super.onPause();
    }
    public ArrayList<Message> getParseData(){
       ArrayList<Message>  aldummy = new ArrayList<Message>();
        List<ParseObject>  alpo = new ArrayList<ParseObject>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("messages");

        query.whereEqualTo("receiver", ParseUser.getCurrentUser().getString("emailId"));

        try {
            alpo =query.find();
            Log.d("debug", "alpo size " + aldummy.size());
            for(ParseObject obj:alpo){
                Message msg = new Message();
//                            msg.setReceiverName(obj.getString("receivername"));
                msg.setSenderName(obj.getString("senderName"));
                msg.setReceiver(obj.getString("receiver"));
                msg.setSender(obj.getString("sender"));
                msg.setMessage(obj.getString("message"));
                msg.setObjectId(obj.getObjectId());
                //Log.d("debug", "Message is**" + obj.getString("message"));
                msg.setIsRead(obj.getBoolean("isRead"));
                msg.setIsLock(obj.getBoolean("isLock"));
                //Log.d("debug", "isRead" + obj.getBoolean("isRead"));
                msg.setRegion(obj.getNumber("region").intValue());
                aldummy.add(msg);
                Log.d("debug", "region value is " + obj.getNumber("region").intValue());
               // Log.d("debug","printing array inside getParse"+aldummy.toString());
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("debug", "alDummy size " + aldummy.size());
        return aldummy;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inbox, menu);
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
        else if(id==R.id.action_refresh){
            new DoBringDetails().execute("");
        }
        else if(id==R.id.action_compose){
            Intent intent = new Intent(InboxActivity.this, ComposeAcitivity.class);
            startActivity(intent);
        } else if (id == R.id.action_logout) {
            ParseUser.logOut();
            // ParseUser currentUser = ParseUser.getCurrentUser();
            Intent intent = new Intent(InboxActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private Integer placesNearBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        //Log.d("debug", "key of beacon is" + beaconKey);
        if (PLACES_BY_BEACONS.containsKey(beaconKey)) {
            Log.d("debug", "key of beacon is" + beaconKey);
            return PLACES_BY_BEACONS.get(beaconKey);
        }
        return 0;
    }
}
