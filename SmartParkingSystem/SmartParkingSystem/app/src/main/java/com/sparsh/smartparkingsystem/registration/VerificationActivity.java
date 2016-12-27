package com.sparsh.smartparkingsystem.registration;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sparsh.smartparkingsystem.R;
import com.sparsh.smartparkingsystem.common.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VerificationActivity extends AppCompatActivity {

// ******* Declaring variables *******

    String resMsg;

// ******* Declaring Progress Bar *******

    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
    }

// ******* USER VERIFICATION API *******

    public void user_verification_api() {

        pDialog = new ProgressDialog(VerificationActivity.this);
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

                        Common.alert(VerificationActivity.this, response.get("message").toString());
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
        Volley.newRequestQueue(VerificationActivity.this).add(jsObjRequest);
    }
}
