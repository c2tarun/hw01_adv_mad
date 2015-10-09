package com.kumar.estimote.homework01;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button loginBtn, createBtn;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Map<String , Object> params = new HashMap<>();
        params.put("message", "testpush to tracy");

        etEmail = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        loginBtn = (Button) findViewById(R.id.btnLogin);
        createBtn = (Button) findViewById(R.id.btnNewUser);
//        if (ParseUser.getCurrentUser() != null) {
//            Intent i = new Intent(LoginActivity.this, AppsActivity.class);
//            startActivity(i);
//            finish();
//        }
        if (!isConnectedOnline()) {
            Toast.makeText(LoginActivity.this, "Network/Bluetooth is missing.", Toast.LENGTH_SHORT).show();
        } else if (ParseUser.getCurrentUser() != null) {
            Intent i = new Intent(LoginActivity.this, InboxActivity.class);
            startActivity(i);
            finish();
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(LoginActivity.this);
                pd.setCancelable(false);
                pd.setMessage("Logging in...");
                if (!isConnectedOnline()) {
                    Toast.makeText(LoginActivity.this, "Network/Bluetooth is missing.", Toast.LENGTH_SHORT).show();
                    return;
                }
                pd.show();

                if (etEmail.getText().toString().trim().isEmpty()
                        || etPassword.getText().toString().trim().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Blank Field/s",
                            Toast.LENGTH_SHORT).show();
                } else
                    ParseUser.logInInBackground(etEmail.getText().toString(),
                            etPassword.getText().toString(),
                            new LogInCallback() {
                                public void done(ParseUser user,
                                                 ParseException e) {
                                    if (user != null) {
                                        Toast.makeText(LoginActivity.this,
                                                "Success Login",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(
                                                LoginActivity.this,
                                                InboxActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this,
                                                "Invalid Login. Try Again",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    if (e != null)
                                        e.printStackTrace();

                                    ParseInstallation.getCurrentInstallation().put("user", user);
                                    ParseInstallation.getCurrentInstallation().saveEventually();
                                    pd.cancel();
                                }
                            });
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        LoginActivity.this,
                        SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

        return super.onOptionsItemSelected(item);
    }

    private boolean isConnectedOnline() {
        boolean isReady = true;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected()) {
            isReady = true;
        } else {
            isReady = false;
        }

//        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (mBluetoothAdapter == null) {
//            Toast.makeText(LoginActivity.this, "Device not supported", Toast.LENGTH_SHORT).show();
//        } else {
//            if (!mBluetoothAdapter.isEnabled()) {
//                isReady = false;
//            } else {
//                isReady = true;
//            }
//        }

        return isReady;
    }
}
