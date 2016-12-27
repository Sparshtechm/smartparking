package com.sparsh.smartparkingsystem.registration;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sparsh.smartparkingsystem.R;
import com.sparsh.smartparkingsystem.common.Common;
import com.sparsh.smartparkingsystem.common.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

// ******* Declaring Variables *******

    String resMsg;

// ******* Declaring Progress Bar *******

    ProgressDialog pDialog;

// ******* Declaring Edit Text View *******

    EditText edt_username, edt_reg_mobile, edt_reg_email, edt_reg_pswd, edt_reg_cnf_pswd;

// ******* Declaring Progress Bar *******

    Button btn_register;

// ******* ANIMATION DECLARATION *******

    Animation anim_shake;

// ******* Declaring Class Objects *******

    Preferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);

        // ******* Animations *******

        anim_shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

        pref = new Preferences(RegistrationActivity.this);

        edt_username     = (EditText) findViewById(R.id.edt_username);
        edt_reg_mobile   = (EditText) findViewById(R.id.edt_reg_mobile);
        edt_reg_email    = (EditText) findViewById(R.id.edt_reg_email);
        edt_reg_pswd     = (EditText) findViewById(R.id.edt_reg_pswd);
        edt_reg_cnf_pswd = (EditText) findViewById(R.id.edt_reg_cnf_pswd);

        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectingToInternet(RegistrationActivity.this)) {
                    //  Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_SHORT).show();
                    if (Validate()) {
                        user_registration_api();
                    }
                } else {
                    Common.alert(RegistrationActivity.this, getResources().getString(R.string.no_internet_txt));
                }
            }
        });
    }

// ******* USER REGISTRATION API *******

    public void user_registration_api() {

        pDialog = new ProgressDialog(RegistrationActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        Map<String, String> postParam = new HashMap<String, String>();
        // the POST parameters:

        postParam.put("mobileNumber", "");
        postParam.put("email",        "");
        postParam.put("username",     "");
        postParam.put("password",     "");
        postParam.put("updatedBy",    "");
        postParam.put("country",      "India");
        postParam.put("countryCode",  "91");


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, getResources().getString(R.string.registration_api), null, new Response.Listener<JSONObject>() {

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

                        Common.alert(RegistrationActivity.this, response.get("message").toString());
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
        Volley.newRequestQueue(RegistrationActivity.this).add(jsObjRequest);
    }

// ******* VALIDATE FIELDS *******

    public boolean Validate() {

        boolean status = true;

        if (edt_username.getText().toString().trim().equals("") &&
                edt_reg_mobile.getText().toString().trim().equals("") &&
                edt_reg_email.getText().toString().trim().equals("") &&
                edt_reg_pswd.getText().toString().trim().equals("") &&
                edt_reg_cnf_pswd.getText().toString().trim().equals("")) {

            status = false;

            edt_username.startAnimation(anim_shake);
            edt_reg_mobile.startAnimation(anim_shake);
            edt_reg_email.startAnimation(anim_shake);
            edt_reg_pswd.startAnimation(anim_shake);
            edt_reg_cnf_pswd.startAnimation(anim_shake);

        } else {

            if (edt_username.getText().toString().trim().equals("")) {
                status = false;
                edt_username.startAnimation(anim_shake);
                Common.alert(RegistrationActivity.this, getString(R.string.blank_txt_uname));
            } else if (edt_reg_mobile.getText().toString().trim().equals("")) {
                status = false;
                edt_reg_mobile.startAnimation(anim_shake);
                Common.alert(RegistrationActivity.this, getString(R.string.blank_txt_mobile));
            } else if (edt_reg_email.getText().toString().trim().equals("")) {
                status = false;
                edt_reg_email.startAnimation(anim_shake);
                Common.alert(RegistrationActivity.this, getString(R.string.blank_txt_email));
            } else if (edt_reg_pswd.getText().toString().trim().equals("")) {
                status = false;
                edt_reg_pswd.startAnimation(anim_shake);
                Common.alert(RegistrationActivity.this, getString(R.string.blank_txt_pswd));
            } else if (edt_reg_cnf_pswd.getText().toString().trim().equals("")) {
                status = false;
                edt_reg_cnf_pswd.startAnimation(anim_shake);
                Common.alert(RegistrationActivity.this, getString(R.string.blank_txt_cnf_pswd));
            } else if (Common.isEmailValid(edt_reg_email.getText().toString().trim()) == false) {

                status = false;
                edt_reg_email.startAnimation(anim_shake);
                Common.alert(RegistrationActivity.this, getString(R.string.txt_valid_email));
            }
        }
        return status;
    }
}
