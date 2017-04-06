package com.sparsh.smartparkingsystem.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.sparsh.smartparkingsystem.registration.VerificationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Forget_Pswd_Activity extends AppCompatActivity implements View.OnClickListener {

// ******* Declaring Variables *******

    String resMsg, resCode;

// ******* Declaring TextView *******

    TextView tv_title;

// ******* Declaring EDIT TextView *******

    EditText edt_forgot_email, edt_forgot_OTP, edt_new_reset_pswd, edt_cnf_reset_pswd;

// ******* Declaring ImageView *******

    ImageView iv_back;

// ******* Declaring layouts *******

    RelativeLayout rl_forgot, rl_verify_code, rl_reset_pswd;

// ******* Declaring Button *******

    Button btn_forgot, btn_verify, btn_submit;

// ******* Declaring Progress Bar *******

    ProgressDialog pDialog;

// ******* ANIMATION DECLARATION *******

    Animation anim_shake;

// ******* Declaring Class Objects *******

    Preferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forget_pswd);

    // ******* Animations *******

        anim_shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

        pref = new Preferences(Forget_Pswd_Activity.this);

    // ******* TextView *******

        tv_title = (TextView)findViewById(R.id.tv_title);

    // ******* ImageView *******

        iv_back  = (ImageView)findViewById(R.id.iv_back);

    // ******* Edit TextView *******

        edt_forgot_email   = (EditText)findViewById(R.id.edt_forgot_email);
        edt_forgot_OTP     = (EditText)findViewById(R.id.edt_forgot_OTP);
        edt_new_reset_pswd = (EditText)findViewById(R.id.edt_new_reset_pswd);
        edt_cnf_reset_pswd = (EditText)findViewById(R.id.edt_cnf_reset_pswd);

    // ******* Relative layout *******

        rl_forgot      = (RelativeLayout)findViewById(R.id.rl_forgot);
        rl_verify_code = (RelativeLayout)findViewById(R.id.rl_verify_code);
        rl_reset_pswd  = (RelativeLayout)findViewById(R.id.rl_reset_pswd);

    // ******* Button Forgot *******

        btn_forgot = (Button)findViewById(R.id.btn_forgot);
        btn_verify = (Button)findViewById(R.id.btn_verify);
        btn_submit = (Button)findViewById(R.id.btn_submit);

        iv_back.setOnClickListener(this);
        btn_forgot.setOnClickListener(this);
        btn_verify.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
    }

// ******* On click Method *******

    @Override

    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.iv_back:

                startActivity(new Intent(Forget_Pswd_Activity.this, LoginActivity.class));
                finish();

                break;

            case R.id.btn_forgot:

                if (Common.isConnectingToInternet(Forget_Pswd_Activity.this)) {

                    if (Validate(1)) {
                        OTP_request_api(edt_forgot_email.getText().toString().trim());
                    }
                } else {
                    Common.alert(Forget_Pswd_Activity.this, getResources().getString(R.string.no_internet_txt));
                }

                break;

            case R.id.btn_verify:

                if (Common.isConnectingToInternet(Forget_Pswd_Activity.this)) {

                    if (Validate(2)) {
                        user_verification_api(edt_forgot_email.getText().toString().trim(), edt_forgot_OTP.getText().toString().trim());
                    }
                } else {
                    Common.alert(Forget_Pswd_Activity.this, getResources().getString(R.string.no_internet_txt));
                }

                break;

            case R.id.btn_submit:

                if (Common.isConnectingToInternet(Forget_Pswd_Activity.this)) {

                    if (Validate(3)) {
                        reset_pswd_api(edt_forgot_email.getText().toString().trim(),edt_new_reset_pswd.getText().toString().trim());
                    }
                } else {
                    Common.alert(Forget_Pswd_Activity.this, getResources().getString(R.string.no_internet_txt));
                }

                break;

            default:

                break;
        }
    }

// ******* OTP REQUEST API *******

    public void OTP_request_api(final String user_email) {

        pDialog = new ProgressDialog(Forget_Pswd_Activity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        Map <String, String> postParam = new HashMap<String, String>();
        // the POST parameters:
        postParam.put("email",     user_email);
        postParam.put("nonceType", "F");

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, getResources().getString(R.string.otp_request_api), new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject header_Obj = response.getJSONObject("appHeader");

                    resCode = header_Obj.get("statusCode").toString();
                    resMsg  = header_Obj.get("statusMessage").toString();
                    pDialog.cancel();
                    if (resCode.equals("200")) {

                        Common.alert(Forget_Pswd_Activity.this, resMsg);

                        String OTP_code   = response.get("verificationCode").toString();
                       /* String customerId = response.get("customerId").toString();
                        pref.set(Constants.kemail, user_email);
                        pref.set(Constants.kcust_id, customerId);
                        pref.commit();*/

                        tv_title.setText(getResources().getString(R.string.txt_title_verification));
                        rl_forgot.setVisibility(View.GONE);
                        rl_verify_code.setVisibility(View.VISIBLE);
                        rl_reset_pswd.setVisibility(View.GONE);
                        edt_forgot_OTP.setText(OTP_code);

                    } else {
                        Common.alert(Forget_Pswd_Activity.this, resMsg);
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
        Volley.newRequestQueue(Forget_Pswd_Activity.this).add(jsObjRequest);
    }

// ******* USER VERIFICATION API *******

    public void user_verification_api(String user_email, String OTP_code) {

        pDialog = new ProgressDialog(Forget_Pswd_Activity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("email",            user_email); // pref.get(Constants.kemail));
        postParam.put("nonceType",        "F");
        postParam.put("verificationCode", OTP_code);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, getResources().getString(R.string.verification_api), new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                pDialog.cancel();

                try {
                    JSONObject header_Obj = response.getJSONObject("appHeader");
                    resCode = header_Obj.get("statusCode").toString();
                    resMsg  = header_Obj.get("statusMessage").toString();

                    if (resCode.equals("200")) {

                        Toast.makeText(Forget_Pswd_Activity.this, resMsg,Toast.LENGTH_SHORT).show();

                        tv_title.setText(getResources().getString(R.string.txt_title_reset_pswd));
                        rl_forgot.setVisibility(View.GONE);
                        rl_verify_code.setVisibility(View.GONE);
                        rl_reset_pswd.setVisibility(View.VISIBLE);
                    }
                    else {

                        Common.alert(Forget_Pswd_Activity.this, response.get("message").toString());
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
        Volley.newRequestQueue(Forget_Pswd_Activity.this).add(jsObjRequest);
    }

// ******* RESET PASSWORD API *******

    public void reset_pswd_api(String user_email, String pswd) {

        pDialog = new ProgressDialog(Forget_Pswd_Activity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        Map <String, String> postParam = new HashMap<String, String>();

        postParam.put("email",     user_email);// pref.get(Constants.kemail));
        postParam.put("password",  pswd);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, getResources().getString(R.string.reset_pswd_api), new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                pDialog.cancel();

                try {
                    JSONObject header_Obj = response.getJSONObject("appHeader");

                    resCode = header_Obj.get("statusCode").toString();
                    resMsg  = header_Obj.get("statusMessage").toString();

                    if (resCode.equals("200")) {

                        //Common.alert(RegistrationActivity.this, resMsg);

                        /*String verificationCode = response.get("verificationCode").toString();*/
                        String customerId = response.get("customerId").toString();
                        pref.set(Constants.kcust_id,    customerId);
                        //pref.set(Constants.kemail,      email);
                        //pref.set(Constants.kContact_no, mob);
                        pref.commit();

                        startActivity(new Intent(Forget_Pswd_Activity.this, DashboardActivity.class));
                        finish();


                    } else {
                        Common.alert(Forget_Pswd_Activity.this, response.get("message").toString());
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
        Volley.newRequestQueue(Forget_Pswd_Activity.this).add(jsObjRequest);
    }

// ******* VALIDATE FIELDS *******

    public boolean Validate(int val) {

        boolean status = true;

        if(val==1) {
            if (edt_forgot_email.getText().toString().trim().equals("")) {

                status = false;
                edt_forgot_email.startAnimation(anim_shake);
                Common.alert(Forget_Pswd_Activity.this, getString(R.string.blank_txt_email));
            }
            else if (!Common.isEmailValid(edt_forgot_email.getText().toString().trim())) {
                status = false;
                edt_forgot_email.startAnimation(anim_shake);
                Common.alert(Forget_Pswd_Activity.this, getString(R.string.txt_valid_email));
            }
        }
        else if(val==2){

            if (edt_forgot_OTP.getText().toString().trim().equals("")) {

                status = false;
                edt_forgot_OTP.startAnimation(anim_shake);
                Common.alert(Forget_Pswd_Activity.this, getString(R.string.txt_otp));
            }
        }
        else if(val==3) {

            if (edt_new_reset_pswd.getText().toString().trim().equals("") && edt_cnf_reset_pswd.getText().toString().trim().equals("")) {

                status = false;

                edt_new_reset_pswd.startAnimation(anim_shake);
                edt_cnf_reset_pswd.startAnimation(anim_shake);
                Common.alert(Forget_Pswd_Activity.this, getString(R.string.txt_msg_all_fields));

            } else {

                if (edt_new_reset_pswd.getText().toString().trim().equals("")) {
                    status = false;
                    edt_new_reset_pswd.startAnimation(anim_shake);
                    Common.alert(Forget_Pswd_Activity.this, getString(R.string.blank_txt_new_pswd));
                }
                else if (edt_cnf_reset_pswd.getText().toString().trim().equals("")) {
                    status = false;
                    edt_cnf_reset_pswd.startAnimation(anim_shake);
                    Common.alert(Forget_Pswd_Activity.this, getString(R.string.blank_txt_cnf_pswd));
                }
                else if (!edt_new_reset_pswd.getText().toString().trim().equals(edt_cnf_reset_pswd.getText().toString().trim())) {

                    status = false;
                    edt_new_reset_pswd.setText("");
                    edt_cnf_reset_pswd.setText("");
                    edt_new_reset_pswd.startAnimation(anim_shake);
                    edt_cnf_reset_pswd.startAnimation(anim_shake);
                    edt_new_reset_pswd.requestFocus();
                    Common.alert(Forget_Pswd_Activity.this, getString(R.string.txt_msg_pswd_not_match));
                }
            }
        }

        return status;
    }
}
