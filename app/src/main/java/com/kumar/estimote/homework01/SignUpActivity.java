package com.kumar.estimote.homework01;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    EditText etFirstName, etLastName, etUserEmail, etPass, etPassConfirm;
    Button signUpBtn;
    ParseUser user;
    ProgressDialog pd;
    MyApplication application;
    Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etFirstName = (EditText) findViewById(R.id.editText);
        etLastName = (EditText) findViewById(R.id.editText2);
        etUserEmail = (EditText) findViewById(R.id.editText3);
        etPass = (EditText) findViewById(R.id.editText4);
        etPassConfirm = (EditText) findViewById(R.id.editText5);
        signUpBtn = (Button) findViewById(R.id.btnSignUp);

        signUpBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                pd = new ProgressDialog(SignUpActivity.this);
                pd.setMessage("Signing up");
                pd.setCancelable(false);
                pd.show();
                if (etFirstName.getText().toString().trim().isEmpty()
                        || etLastName.getText().toString().trim().isEmpty()
                        || etUserEmail.getText().toString().trim().isEmpty()
                        || etPass.getText().toString().trim().isEmpty()
                        || etPassConfirm.getText().toString().trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Empty Field/s",
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if (!etPass.getText().toString().equals(etPassConfirm.getText().toString())){
                    Toast.makeText(SignUpActivity.this, "Passwords mismatch",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                user = new ParseUser();
                user.setUsername(etUserEmail.getText()
                        .toString());
                user.setPassword(etPass.getText().toString());
                user.put("UserFullName", etFirstName.getText()
                        .toString() + " " + etLastName.getText().toString());
                user.put("emailId", etUserEmail.getText().toString());
                user.put("lastName", etLastName.getText().toString());

                user.signUpInBackground(new SignUpCallback() {

                    @Override
                    public void done(ParseException e) {
                        if (e == null){
                            //					if (!etUserEmail.getText().toString().equals("")){
                            Toast.makeText(SignUpActivity.this,
                                    "Successful SignUp", Toast.LENGTH_SHORT)
                                    .show();
                            Intent intent = new Intent(SignUpActivity.this, InboxActivity.class);
                            startActivity(intent);
                            finish();
                            Map<String, String> params = new HashMap<>();
                            params.put("message", "New user " + user.getString("UserFullName") + " joined.");
                            ParseCloud.callFunctionInBackground("pushToAll", params, new FunctionCallback<Object>() {
                                @Override
                                public void done(Object o, ParseException e) {
                                    if(e == null) {
                                        Toast.makeText(SignUpActivity.this, "All existing users are notified.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            //					}
                        }else {
                            e.printStackTrace();
                            Log.d("debug", e.getMessage());
                            Toast.makeText(SignUpActivity.this, "Sign Up Error",
                                    Toast.LENGTH_SHORT).show();
                        }
                        pd.cancel();
                    }
                });
            }
        });
        application = (MyApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Setting screen name: " + "Login Screen");
        mTracker.setScreenName("Activity~" + "Login Screen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
    }


