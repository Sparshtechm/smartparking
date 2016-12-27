package com.sparsh.smartparkingsystem.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sparsh.smartparkingsystem.R;
import com.sparsh.smartparkingsystem.common.Common;
import com.sparsh.smartparkingsystem.dashboard.DashboardActivity1;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


// ******* Declaring variables *******

    String resMsg;

// ******* Declaring text views *******

    TextView tv_forgot_pswd;

// ******* Declaring Edit text views *******

    EditText edt_user_id, edt_pswd;

// ******* Declaring Buttons *******

    Button btn_login, btn_register;

// ******* Declaring Progress Bar *******

    ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        edt_user_id = (EditText)findViewById(R.id.edt_user_id);
        edt_pswd    = (EditText)findViewById(R.id.edt_pswd);

        tv_forgot_pswd = (TextView)findViewById(R.id.tv_forgot_pswd);
        tv_forgot_pswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    // ******* Button Login *******

        btn_login = (Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, DashboardActivity1.class));
                finish();
            }
        });

    // ******* Button Register *******

        btn_register = (Button)findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                finish();
            }
        });
    }

// ******* USER LOGIN API *******

    public void user_login_api() {

        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("email",        "");
        postParam.put("password",     "");

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, getResources().getString(R.string.login_api), null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                pDialog.cancel();

                try {

                    resMsg = response.get("success").toString();

                    if (resMsg.equals("true")) {

                  /*  Common.alert(RegistrationActivity.this, getResources().getString(R.string.msg_success));

                    pref.set(Constants.kF_name,     edt_fname.getText().toString().trim());
                    pref.set(Constants.kL_name,     edt_lname.getText().toString().trim());
                    pref.set(Constants.kContact_no, edt_cnt_no.getText().toString().trim());
                    pref.set(Constants.kType,       gender_type);
                    pref.commit();
                  */
                    } else {

                        Common.alert(LoginActivity.this, response.get("message").toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                pDialog.cancel();
                error.printStackTrace();
            }
        });
        Volley.newRequestQueue(LoginActivity.this).add(jsObjRequest);
    }

// ******* FORGOT PASSWORD API *******

    public void forgot_pswd_api() {

        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("email",        "");


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, getResources().getString(R.string.forgot_pswd_api), null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                pDialog.cancel();

                try {

                    resMsg = response.get("success").toString();

                    if (resMsg.equals("true")) {

                  /*  Common.alert(RegistrationActivity.this, getResources().getString(R.string.msg_success));

                    pref.set(Constants.kF_name,     edt_fname.getText().toString().trim());
                    pref.set(Constants.kL_name,     edt_lname.getText().toString().trim());
                    pref.set(Constants.kContact_no, edt_cnt_no.getText().toString().trim());
                    pref.set(Constants.kType,       gender_type);
                    pref.commit();
                  */
                    } else {

                        Common.alert(LoginActivity.this, response.get("message").toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                pDialog.cancel();
                error.printStackTrace();
            }
        });
        Volley.newRequestQueue(LoginActivity.this).add(jsObjRequest);
    }

}
