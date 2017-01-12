package com.sparsh.smartparkingsystem.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

// ******* Declaring variables *******

    String resMsg, resCode;

// ******* Declaring Edit TextView *******

    EditText edt_prf_name, edt_prf_email, edt_prf_mob;

// ******* Declaring Button *******

    Button btn_prf_update;

// ******* Declaring Progress Bar *******

    ProgressDialog pDialog;

// ******* ANIMATION DECLARATION *******

    Animation anim_shake;

// ******* Declaring Class Objects *******

    Preferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        pref = new Preferences(ProfileActivity.this);

        ImageView iv_back = (ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, DashboardActivity.class));
                finish();
            }
        });

        edt_prf_name  = (EditText)findViewById(R.id.edt_prf_name);
        edt_prf_email = (EditText)findViewById(R.id.edt_prf_email);
        edt_prf_mob   = (EditText)findViewById(R.id.edt_prf_mob);

        btn_prf_update = (Button)findViewById(R.id.btn_prf_update);
        btn_prf_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectingToInternet(ProfileActivity.this)) {

                    if (Validate()) {
                        update_user_profile_api();
                    }
                } else {
                    Common.alert(ProfileActivity.this, getResources().getString(R.string.no_internet_txt));
                }
            }
        });


        if (Common.isConnectingToInternet(ProfileActivity.this)) {

            get_user_profile_api();

        } else {
            Common.alert(ProfileActivity.this, getResources().getString(R.string.no_internet_txt));
        }
    }

// ******* GET USER PROFILE API *******

    public void get_user_profile_api() {

        pDialog = new ProgressDialog(ProfileActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

/*        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("email",            pref.get(Constants.kemail));
        postParam.put("nonceType",        "R");*/

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, getResources().getString(R.string.get_user_profile_api) + pref.get(Constants.kcust_id), null/*new JSONObject(postParam)*/, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                pDialog.cancel();

                try {
                    JSONObject header_Obj = response.getJSONObject("appHeader");
                    resCode = header_Obj.get("statusCode").toString();
                    resMsg  = header_Obj.get("statusMessage").toString();

                    if (resCode.equals("200")) {

                        String customerId = response.get("customerId").toString();

                        edt_prf_mob.setText(response.get("mobileNumber").toString());
                        edt_prf_email.setText(response.get("email").toString());
                        edt_prf_name.setText(response.get("username").toString());

                        pref.set(Constants.kcust_id, customerId);
                        pref.commit();

                    } else {

                        Common.alert(ProfileActivity.this, response.get("message").toString());
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
        Volley.newRequestQueue(ProfileActivity.this).add(jsObjRequest);
    }

// ******* UPDATE USER PROFILE API *******

    public void update_user_profile_api() {

        pDialog = new ProgressDialog(ProfileActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("customerId",    pref.get(Constants.kcust_id));
        postParam.put("mobileNumber",  edt_prf_mob.getText().toString().trim());
        postParam.put("username",      edt_prf_name.getText().toString().trim());


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, getResources().getString(R.string.update_profile_api), new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                pDialog.cancel();

                try {
                    JSONObject header_Obj = response.getJSONObject("appHeader");
                    resCode = header_Obj.get("statusCode").toString();
                    resMsg  = header_Obj.get("statusMessage").toString();

                    if (resCode.equals("200")) {
                        Common.alert(ProfileActivity.this, resMsg);
                    }
                    else {
                        Common.alert(ProfileActivity.this, resMsg);
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
        Volley.newRequestQueue(ProfileActivity.this).add(jsObjRequest);
    }

// ******* VALIDATE FIELDS *******

    public boolean Validate() {

        boolean status = true;

        if (edt_prf_name.getText().toString().trim().equals("") && edt_prf_mob.getText().toString().trim().equals("")){

            status = false;
            edt_prf_name.startAnimation(anim_shake);
            edt_prf_mob.startAnimation(anim_shake);

        } else {

            if (edt_prf_name.getText().toString().trim().equals("")) {
                status = false;
                edt_prf_name.startAnimation(anim_shake);
                Common.alert(ProfileActivity.this, getString(R.string.blank_txt_name));

            } else if (edt_prf_mob.getText().toString().trim().equals("")) {
                status = false;
                edt_prf_mob.startAnimation(anim_shake);
                Common.alert(ProfileActivity.this, getString(R.string.blank_txt_mobile));
            }
        }
        return status;
    }
}
