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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sparsh.smartparkingsystem.R;
import com.sparsh.smartparkingsystem.common.Common;
import com.sparsh.smartparkingsystem.common.Constants;
import com.sparsh.smartparkingsystem.common.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

// ******* Declaring Variables *******

    String resMsg, resCode, sel_country_code;

// ******* Declaring Progress Bar *******

    ProgressDialog pDialog;

// ******* Declaring Text View *******

    TextView tv_login_here, tv_country_code ;

// ******* Declaring Spinner *******

    Spinner spnr_country;

// ******* Declaring Edit Text View *******

    EditText edt_reg_name, edt_reg_mobile, edt_reg_email, edt_reg_pswd, edt_reg_cnf_pswd;

// ******* Declaring Progress Bar *******

    Button btn_register;

// ******* ANIMATION DECLARATION *******

    Animation anim_shake;

// ******* Declaring Class Objects *******

    Preferences pref;

    List<String> country_name_list = new ArrayList<String>();
    List<String> country_code_list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);

    // ******* Animations *******

        anim_shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

        pref = new Preferences(RegistrationActivity.this);

    // ******* Login Here *******

        tv_login_here = (TextView) findViewById(R.id.tv_login_here);
        Spannable wordtoSpan = new SpannableString("Already have an account? Login!");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
            }
        };
        wordtoSpan.setSpan(clickableSpan, 24, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_theme_color)), 24, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD_ITALIC), 24,31, 0);
        tv_login_here.setText(wordtoSpan);
        tv_login_here.setMovementMethod(LinkMovementMethod.getInstance());

        tv_country_code = (TextView) findViewById(R.id.tv_country_code);
        tv_country_code.setText("+91");

    // ******* Edit TextView *******

        edt_reg_name     = (EditText) findViewById(R.id.edt_reg_name);
        edt_reg_mobile   = (EditText) findViewById(R.id.edt_reg_mobile);
        edt_reg_email    = (EditText) findViewById(R.id.edt_reg_email);
        edt_reg_pswd     = (EditText) findViewById(R.id.edt_reg_pswd);
        edt_reg_cnf_pswd = (EditText) findViewById(R.id.edt_reg_cnf_pswd);

    // ******* Spinner Country *******

        country_name_list.add("India");
        country_name_list.add("US");

        country_code_list.add("91");
        country_code_list.add("001");

        spnr_country = (Spinner)findViewById(R.id.spnr_country);
        ArrayAdapter<String> vehicle_type_adapter = new ArrayAdapter<String>(RegistrationActivity.this, android.R.layout.simple_spinner_item, country_name_list);
        vehicle_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnr_country.setAdapter(vehicle_type_adapter);

        spnr_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {

                if(position == 0){
                    tv_country_code.setText("+91");
                    sel_country_code = country_code_list.get(position);
                }
                else{
                    tv_country_code.setText("+1");
                    sel_country_code = country_code_list.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    // ******* Button Register *******

        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectingToInternet(RegistrationActivity.this)) {

                    if (Validate()) {
                        user_registration_api(edt_reg_name.getText().toString().trim(), edt_reg_mobile.getText().toString().trim(),
                                             edt_reg_email.getText().toString().trim(), edt_reg_pswd.getText().toString().trim());
                    }
                } else {
                    Common.alert(RegistrationActivity.this, getResources().getString(R.string.no_internet_txt));
                }
            }
        });
    }

// ******* USER REGISTRATION API *******

    public void user_registration_api(String user_name, final String mob, final String email, String pswd) {

        pDialog = new ProgressDialog(RegistrationActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        Map <String, String> postParam = new HashMap<String, String>();

        postParam.put("mobileNumber", mob);
        postParam.put("email",        email);
        postParam.put("username",     user_name);
        postParam.put("password",     pswd);
        postParam.put("updatedBy",    user_name);
        postParam.put("country",      spnr_country.getSelectedItem().toString()); // "India");
        postParam.put("countryCode",  sel_country_code); //"91");

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, getResources().getString(R.string.registration_api), new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

               // pDialog.cancel();

                try {
                    JSONObject header_Obj = response.getJSONObject("appHeader");

                    resCode = header_Obj.get("statusCode").toString();
                    resMsg  = header_Obj.get("statusMessage").toString();

                    if (resCode.equals("200")) {

                        //pref.set(Constants.kcust_id,    response.get("customerId").toString());
                        /* pref.set(Constants.kemail,      email);
                        pref.set(Constants.kContact_no, mob);
                        pref.commit();*/

                        // Call api for getting verification code
                        OTP_request_api(resMsg, email, mob);
                    }
                    else {
                        pDialog.cancel();
                        Common.alert(RegistrationActivity.this, resMsg);
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
        Volley.newRequestQueue(RegistrationActivity.this).add(jsObjRequest).setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

// ******* OTP REQUEST API *******

    public void OTP_request_api(final String registration_msg, final String user_email, final String user_mob) {

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

                        Toast.makeText(RegistrationActivity.this, registration_msg, Toast.LENGTH_LONG).show();

                        String OTP_code = response.get("verificationCode").toString();
                        /* String customerId = response.get("customerId").toString();
                        pref.set(Constants.kcust_id, customerId);
                        pref.commit();*/
                        startActivity(new Intent(RegistrationActivity.this, VerificationActivity.class).putExtra("OTP", OTP_code).putExtra("user_email", user_email).putExtra("Cnt_no", user_mob).putExtra("Country_code", sel_country_code));
                        finish();

                    } else {
                        Common.alert(RegistrationActivity.this, resMsg);
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

        if (edt_reg_name.getText().toString().trim().equals("")       &&
                edt_reg_mobile.getText().toString().trim().equals("") &&
                edt_reg_email.getText().toString().trim().equals("")  &&
                edt_reg_pswd.getText().toString().trim().equals("")   &&
                edt_reg_cnf_pswd.getText().toString().trim().equals("")) {

            status = false;

            edt_reg_name.startAnimation(anim_shake);
            edt_reg_mobile.startAnimation(anim_shake);
            edt_reg_email.startAnimation(anim_shake);
            edt_reg_pswd.startAnimation(anim_shake);
            edt_reg_cnf_pswd.startAnimation(anim_shake);
            Common.alert(RegistrationActivity.this, getString(R.string.txt_msg_all_fields));

        } else {

            if (edt_reg_name.getText().toString().trim().equals("")) {
                status = false;
                edt_reg_name.startAnimation(anim_shake);
                edt_reg_name.requestFocus();
                Common.alert(RegistrationActivity.this, getString(R.string.blank_txt_name));
            }
            else if (edt_reg_mobile.getText().toString().trim().equals("")) {
                status = false;
                edt_reg_mobile.startAnimation(anim_shake);
                edt_reg_mobile.requestFocus();
                Common.alert(RegistrationActivity.this, getString(R.string.blank_txt_mobile));
            }
            else if (edt_reg_email.getText().toString().trim().equals("")) {
                status = false;
                edt_reg_email.startAnimation(anim_shake);
                edt_reg_email.requestFocus();
                Common.alert(RegistrationActivity.this, getString(R.string.blank_txt_email));
            }
            else if (edt_reg_pswd.getText().toString().trim().equals("")) {
                status = false;
                edt_reg_pswd.startAnimation(anim_shake);
                edt_reg_pswd.requestFocus();
                Common.alert(RegistrationActivity.this, getString(R.string.blank_txt_pswd));
            } else if (edt_reg_cnf_pswd.getText().toString().trim().equals("")) {
                status = false;
                edt_reg_cnf_pswd.startAnimation(anim_shake);
                edt_reg_cnf_pswd.requestFocus();
                Common.alert(RegistrationActivity.this, getString(R.string.blank_txt_cnf_pswd));
            } else if (!Common.isEmailValid(edt_reg_email.getText().toString().trim())) {
                status = false;
                edt_reg_email.startAnimation(anim_shake);
                edt_reg_email.setText("");
                edt_reg_email.requestFocus();
                Common.alert(RegistrationActivity.this, getString(R.string.txt_valid_email));
            }

            else if (edt_reg_mobile.getText().toString().trim().length()<10 || edt_reg_mobile.getText().toString().trim().length()>16) {
                status = false;
                edt_reg_mobile.startAnimation(anim_shake);
                edt_reg_mobile.requestFocus();
                Common.alert(RegistrationActivity.this, getString(R.string.txt_mobile_length));
            }

            else if (edt_reg_pswd.getText().toString().trim().length()<8 || edt_reg_pswd.getText().toString().trim().length()>16) {
                status = false;
                edt_reg_pswd.setText("");
                edt_reg_pswd.requestFocus();
                edt_reg_pswd.startAnimation(anim_shake);
                Common.alert(RegistrationActivity.this, getString(R.string.txt_pswd_length));
            }
            else if (!edt_reg_pswd.getText().toString().trim().equals(edt_reg_cnf_pswd.getText().toString().trim())) {
                status = false;
                edt_reg_pswd.setText("");
                edt_reg_cnf_pswd.setText("");
                edt_reg_pswd.startAnimation(anim_shake);
                edt_reg_cnf_pswd.startAnimation(anim_shake);
                edt_reg_pswd.requestFocus();
                Common.alert(RegistrationActivity.this, getString(R.string.txt_msg_pswd_not_match));
            }
        }
        return status;
    }
}
