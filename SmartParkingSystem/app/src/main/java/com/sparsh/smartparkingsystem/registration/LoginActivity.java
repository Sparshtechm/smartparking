package com.sparsh.smartparkingsystem.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import com.sparsh.smartparkingsystem.profile.Forget_Pswd_Activity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

// ******* Declaring variables *******

    String resMsg, resCode;

// ******* Declaring text views *******

    TextView tv_forgot_pswd, tv_sign_up;

// ******* Declaring Edit text views *******

    EditText edt_login_email, edt_login_pswd;

// ******* Declaring Buttons *******

    Button btn_login;

// ******* Declaring Progress Bar *******

    ProgressDialog pDialog;

// ******* ANIMATION DECLARATION *******

    Animation anim_shake;

// ******* Declaring Class Objects *******

    Preferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

    // ******* Animations *******

        anim_shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

        pref = new Preferences(LoginActivity.this);

    // ******* Edit Text View *******

        edt_login_email = (EditText) findViewById(R.id.edt_login_email);
        edt_login_pswd  = (EditText) findViewById(R.id.edt_login_pswd);

    // ******* Forgot Password *******

        tv_forgot_pswd = (TextView) findViewById(R.id.tv_forgot_pswd);
        tv_forgot_pswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, Forget_Pswd_Activity.class));
                finish();
            }
        });

    // ******* Sign up  *******

        tv_sign_up = (TextView) findViewById(R.id.tv_sign_up);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                finish();
            }
        };

        Spannable wordtoSpan = new SpannableString("Don't have an account? Sign Up!");
        wordtoSpan.setSpan(clickableSpan, 23, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_theme_color)), 23, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 23,31, 0);
        tv_sign_up.setText(wordtoSpan);
        tv_sign_up.setMovementMethod(LinkMovementMethod.getInstance());

    // ******* Button Login *******

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectingToInternet(LoginActivity.this)) {

                    if (Validate()) {
                        user_login_api(edt_login_email.getText().toString().trim(), edt_login_pswd.getText().toString().trim());
                    }
                } else {
                    Common.alert(LoginActivity.this, getResources().getString(R.string.no_internet_txt));
                }
            }
        });
    }

// ******* USER LOGIN API *******

    public void user_login_api(final String email, String pswd) {

        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        Map <String, String> postParam = new HashMap <String, String>();
        postParam.put("email",        email);
        postParam.put("logInSession", "true");
        postParam.put("password",     pswd);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, Common.getCompleteApiUrl(LoginActivity.this, R.string.login_api), new JSONObject(postParam), new Response.Listener<JSONObject>() {

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

                        if(pref.get(Constants.kloginChk).equals("1")){
                           /* pref.set(Constants.kloginChk,"0");
                            pref.commit();*/
                            startActivity(new Intent(LoginActivity.this, Booking_Availability.class));
                            finish();
                        }else{
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        }
                    }
                    // If email id is not verified
                    else if(resCode.equals("410")){

                        String user_mob          = response.get("mdnNumber").toString();
                        String sel_country_code  = response.get("countryCode").toString();

                        //startActivity(new Intent(LoginActivity.this, VerificationActivity.class));//.putExtra("OTP", OTP_code));
                        startActivity(new Intent(LoginActivity.this, VerificationActivity.class)
                                .putExtra("OTP", "").putExtra("user_email", email)
                                .putExtra("Cnt_no", user_mob).putExtra("Country_code", sel_country_code));
                        finish();
                    }
                    else {

                        Common.alert(LoginActivity.this, resMsg);
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

// ******* VALIDATE FIELDS *******

    public boolean Validate() {

        boolean status = true;

        if (edt_login_email.getText().toString().trim().equals("") && edt_login_pswd.getText().toString().trim().equals("")) {

            status = false;
            edt_login_email.startAnimation(anim_shake);
            edt_login_pswd.startAnimation(anim_shake);
            Common.alert(LoginActivity.this, getString(R.string.txt_msg_all_fields));
        }
        else {

            if (edt_login_email.getText().toString().trim().equals("")) {
                status = false;
                edt_login_email.requestFocus();
                edt_login_email.startAnimation(anim_shake);
                Common.alert(LoginActivity.this, getString(R.string.blank_txt_email));
            }
            else if (edt_login_pswd.getText().toString().trim().equals("")) {
                status = false;
                edt_login_pswd.startAnimation(anim_shake);
                edt_login_pswd.requestFocus();
                Common.alert(LoginActivity.this, getString(R.string.blank_txt_pswd));
            }
           /* else if (edt_login_pswd.getText().toString().trim().length()<8 || edt_login_pswd.getText().toString().trim().length()>16) {
                status = false;
                edt_login_pswd.setText("");
                edt_login_pswd.requestFocus();
                edt_login_pswd.startAnimation(anim_shake);
                Common.alert(LoginActivity.this, getString(R.string.txt_pswd_invalid));
            }*/
            else if (!Common.isEmailValid(edt_login_email.getText().toString().trim())) {
                status = false;
                edt_login_email.startAnimation(anim_shake);
                Common.alert(LoginActivity.this, getString(R.string.txt_valid_email));
            }
        }
        return status;
    }
}