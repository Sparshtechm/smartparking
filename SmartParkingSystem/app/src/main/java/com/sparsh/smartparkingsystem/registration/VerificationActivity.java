package com.sparsh.smartparkingsystem.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sparsh.smartparkingsystem.R;
import com.sparsh.smartparkingsystem.booking.Booking_Availability;
import com.sparsh.smartparkingsystem.common.Common;
import com.sparsh.smartparkingsystem.common.Constants;
import com.sparsh.smartparkingsystem.common.Preferences;
import com.sparsh.smartparkingsystem.dashboard.DashboardActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VerificationActivity extends AppCompatActivity {

// ******* Declaring variables *******

    String resMsg, resCode;
    String user_email, cnt_no, otp, country_code;

// ******* Declaring Text View *******

    TextView /*tv_vfy_user_mob*/ tv_country_code, tv_resend;

// ******* Declaring Edit Text View *******

    EditText vfy_edt_user_code, edt_mobile;

// ******* Declaring Buttons *******

    Button btn_verify;

// ******* Declaring Progress Bar *******

    ProgressDialog pDialog;

// ******* ANIMATION DECLARATION *******

    Animation anim_shake;

// ******* Declaring Class Objects *******

    Preferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_verification);

        pref = new Preferences(VerificationActivity.this);

    // ******* Animations *******

        anim_shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

    // ******* Getting Value from Intent *******

        try {
            Intent intent = getIntent();
            cnt_no     = intent.getStringExtra("Cnt_no");
            otp        = intent.getStringExtra("OTP");
            user_email = intent.getStringExtra("user_email");
            country_code = intent.getStringExtra("Country_code");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    // ******* TEXT VIEWS *******

        /*tv_vfy_user_mob   = (TextView)findViewById(R.id.tv_vfy_user_mob);
        tv_vfy_user_mob.setText(cnt_no);*/

        tv_country_code = (TextView)findViewById(R.id.tv_country_code);
        tv_country_code.setText(country_code);

        if(country_code.equals("91")){
            tv_country_code.setText("+91");
        }
        else{
            tv_country_code.setText("+1");
        }

        tv_resend = (TextView)findViewById(R.id.tv_resend);
        tv_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectingToInternet(VerificationActivity.this)) {

                    OTP_request_api(user_email);
                }
                else {
                    Common.alert(VerificationActivity.this, getResources().getString(R.string.no_internet_txt));
                }
            }
        });

    // ******* EDIT TEXT VIEW *******

        edt_mobile = (EditText)findViewById(R.id.edt_mobile);
        edt_mobile.setText(cnt_no);

        vfy_edt_user_code = (EditText)findViewById(R.id.vfy_edt_user_code);
        vfy_edt_user_code.setText(otp);

    // ******* Button Verify *******

        btn_verify = (Button)findViewById(R.id.btn_verify);
        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectingToInternet(VerificationActivity.this)) {

                    if (Validate()) {
                        user_verification_api(vfy_edt_user_code.getText().toString().trim());
                    }
                } else {
                    Common.alert(VerificationActivity.this, getResources().getString(R.string.no_internet_txt));
                }
            }
        });
    }

// ******* USER VERIFICATION API *******

    public void user_verification_api(String OTP_code) {

        pDialog = new ProgressDialog(VerificationActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("email",            user_email); // pref.get(Constants.kemail));
        postParam.put("nonceType",        "R");
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

                        String customerId = response.get("customerId").toString();
                        pref.set(Constants.kcust_id, customerId);
                        pref.commit();

                        Toast.makeText(VerificationActivity.this, resMsg, Toast.LENGTH_SHORT).show();

                       /* startActivity(new Intent(VerificationActivity.this, DashboardActivity.class));
                        finish();
*/
                        if(pref.get(Constants.kloginChk).equals("1")){
                           /* pref.set(Constants.kloginChk,"0");
                            pref.commit();*/
                            startActivity(new Intent(VerificationActivity.this, Booking_Availability.class));
                            finish();
                        }else{
                            startActivity(new Intent(VerificationActivity.this, DashboardActivity.class));
                            finish();
                        }

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

// ******* RESEND VERIFICATION CODE API *******

    public void OTP_request_api(final String user_email) {

        pDialog = new ProgressDialog(VerificationActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        Map <String, String> postParam = new HashMap<String, String>();
        postParam.put("email",     user_email);
        postParam.put("nonceType", "R");

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, getResources().getString(R.string.otp_request_api), new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject header_Obj = response.getJSONObject("appHeader");

                    resCode = header_Obj.get("statusCode").toString();
                    resMsg  = header_Obj.get("statusMessage").toString();

                    pDialog.cancel();

                    if (resCode.equals("200")) {

                        String OTP_code   = response.get("verificationCode").toString();
                        vfy_edt_user_code.setText(OTP_code);
                    }
                    else {
                        Common.alert(VerificationActivity.this, resMsg);
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

// ******* VALIDATE FIELDS *******

    public boolean Validate() {

        boolean status = true;

        if (vfy_edt_user_code.getText().toString().trim().equals("")) {
            status = false;
            vfy_edt_user_code.startAnimation(anim_shake);
            Common.alert(VerificationActivity.this, getString(R.string.txt_otp));
        }
        return status;
    }
}
