package com.sparsh.smartparkingsystem.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sparsh.smartparkingsystem.R;
import com.sparsh.smartparkingsystem.common.Common;
import com.sparsh.smartparkingsystem.common.Constants;
import com.sparsh.smartparkingsystem.common.Preferences;
import com.sparsh.smartparkingsystem.dashboard.DashboardActivity;
import com.sparsh.smartparkingsystem.registration.LoginActivity;
import com.sparsh.smartparkingsystem.registration.RegistrationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePswdActivity extends AppCompatActivity {

// ******* Declaring Variables *******

    String resMsg, resCode;

// ******* Declaring Edit Text Views *******

    EditText edt_current_pswd, edt_new_pswd, edt_cnf_pswd;

// ******* Declaring ImageView *******

    ImageView iv_back;

// ******* Declaring Button *******

    Button btn_submit;

// ******* Declaring Progress Bar *******

    ProgressDialog pDialog;

// ******* Declaring Class Objects *******

    Preferences pref;

// ******* ANIMATION DECLARATION *******

    Animation anim_shake;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_pswd);

        pref = new Preferences(ChangePswdActivity.this);

    // ******* Animations *******

        anim_shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

        edt_current_pswd = (EditText)findViewById(R.id.edt_current_pswd);
        edt_new_pswd     = (EditText)findViewById(R.id.edt_new_pswd);
        edt_cnf_pswd     = (EditText)findViewById(R.id.edt_cnf_pswd);


        iv_back = (ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChangePswdActivity.this, DashboardActivity.class));
                finish();
            }
        });

        btn_submit = (Button)findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectingToInternet(ChangePswdActivity.this)) {

                   if(Validate()) {
                       change_pswd_api(edt_current_pswd.getText().toString().trim(), edt_new_pswd.getText().toString().trim());
                   }
                } else {
                    Common.alert(ChangePswdActivity.this, getResources().getString(R.string.no_internet_txt));
                }
            }
        });
    }

// ******* CHANGE PASSWORD API *******

    public void change_pswd_api(String old_pswd, String new_pswd) {

        pDialog = new ProgressDialog(ChangePswdActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        Map<String, String> postParam = new HashMap<String, String>();
        // the POST parameters:

        postParam.put("customerId",  pref.get(Constants.kcust_id));
        postParam.put("newPassword", new_pswd);
        postParam.put("oldPassword", old_pswd);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, getResources().getString(R.string.change_pswd_api), new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                pDialog.cancel();

                try {
                    JSONObject header_Obj = response.getJSONObject("appHeader");

                    resCode = header_Obj.get("statusCode").toString();
                    resMsg  = header_Obj.get("statusMessage").toString();

                    if (resCode.equals("200")) {

                        Common.alert(ChangePswdActivity.this, resMsg);

                  /*      *//*String verificationCode = response.get("verificationCode").toString();*//*
                        String customerId = response.get("customerId").toString();
                        pref.set(Constants.kcust_id, customerId);
                        pref.set(Constants.kemail, email);
                        pref.set(Constants.kContact_no, mob);
                        pref.commit();*/
                    }
                    else {
                        pDialog.cancel();
                        Common.alert(ChangePswdActivity.this, response.get("message").toString());
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
        Volley.newRequestQueue(ChangePswdActivity.this).add(jsObjRequest);
    }

// ******* VALIDATE FIELDS *******

    public boolean Validate() {

        boolean status = true;

        if (edt_current_pswd.getText().toString().trim().equals("") && edt_new_pswd.getText().toString().trim().equals("") && edt_cnf_pswd.getText().toString().trim().equals("")){

            status = false;
            edt_current_pswd.startAnimation(anim_shake);
            edt_new_pswd.startAnimation(anim_shake);
            edt_cnf_pswd.startAnimation(anim_shake);

        } else {

            if (edt_current_pswd.getText().toString().trim().equals("")) {
                status = false;
                edt_current_pswd.startAnimation(anim_shake);
                Common.alert(ChangePswdActivity.this, getString(R.string.blank_txt_crnt_pswd));

            } else if (edt_new_pswd.getText().toString().trim().equals("")) {
                status = false;
                edt_new_pswd.startAnimation(anim_shake);
                Common.alert(ChangePswdActivity.this, getString(R.string.blank_txt_new_pswd));
            }
            else if (edt_cnf_pswd.getText().toString().trim().equals("")) {
                status = false;
                edt_cnf_pswd.startAnimation(anim_shake);
                Common.alert(ChangePswdActivity.this, getString(R.string.blank_txt_cnf_pswd));
            }
            else if (!edt_new_pswd.getText().toString().trim().equals(edt_cnf_pswd.getText().toString().trim().equals(""))) {
                status = false;
                edt_new_pswd.setText("");
                edt_cnf_pswd.setText("");
                edt_new_pswd.startAnimation(anim_shake);
                edt_cnf_pswd.startAnimation(anim_shake);
                Common.alert(ChangePswdActivity.this, getString(R.string.txt_msg_pswd_not_match));
            }
        }
        return status;
    }
}
