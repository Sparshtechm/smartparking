package com.sparsh.smartparkingsystem.registration;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.sparsh.smartparkingsystem.common.Preferences;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

// ******* Declaring Variables *******

    String resMsg, resCode, sel_country_name, sel_country_code;

// ******* Declaring Progress Bar *******

    ProgressDialog pDialog;

// ******* Declaring Text View *******

    TextView tv_login_here, tv_country_code ;

// ******* Declaring Image View *******

    ImageView iv_reg_flag_icon, iv_arrow;

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


// ******* Declaring Alert Dialog *******

    Dialog dialog;

// ******* DECLARING ARRAY LIST MAP *******

    ArrayList <HashMap<String, String>> arr_counntries_map_List  = new ArrayList <HashMap<String,String>>();


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
        //tv_country_code.setText("+91");
        tv_country_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCountryDialog();
            }
        });

       // sel_country_code = "91";
       // sel_country_name = "India";

    // ******* Edit TextView *******

        edt_reg_name     = (EditText) findViewById(R.id.edt_reg_name);
        edt_reg_mobile   = (EditText) findViewById(R.id.edt_reg_mobile);
        edt_reg_email    = (EditText) findViewById(R.id.edt_reg_email);
        edt_reg_pswd     = (EditText) findViewById(R.id.edt_reg_pswd);
        edt_reg_cnf_pswd = (EditText) findViewById(R.id.edt_reg_cnf_pswd);

        /*try {
            edt_reg_mobile.setText(Common.getMyPhoneNO(RegistrationActivity.this));
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        iv_reg_flag_icon = (ImageView)findViewById(R.id.iv_reg_flag_icon);
        iv_arrow         = (ImageView)findViewById(R.id.iv_arrow);
        iv_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCountryDialog();
            }
        });

    // ******* Spinner Country *******

        country_name_list.add("India");
        country_name_list.add("Italy");
        country_name_list.add("US");

        country_code_list.add("91");
        country_code_list.add("39");
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
                else if(position == 1){
                    tv_country_code.setText("+39");
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


       // readXMLofCountries();
        getDefaultCountry();

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
        postParam.put("country",      sel_country_name);//spnr_country.getSelectedItem().toString()); // "India");
        postParam.put("countryCode",  sel_country_code); //"91");

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, Common.getCompleteApiUrl(RegistrationActivity.this, R.string.registration_api), new JSONObject(postParam), new Response.Listener<JSONObject>() {

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

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, Common.getCompleteApiUrl(RegistrationActivity.this, R.string.otp_request_api), new JSONObject(postParam), new Response.Listener<JSONObject>() {

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
            }
            else if (edt_reg_cnf_pswd.getText().toString().trim().equals("")) {
                status = false;
                edt_reg_cnf_pswd.startAnimation(anim_shake);
                edt_reg_cnf_pswd.requestFocus();
                Common.alert(RegistrationActivity.this, getString(R.string.blank_txt_cnf_pswd));
            }
            else if (!Common.isEmailValid(edt_reg_email.getText().toString().trim())) {
                status = false;
                edt_reg_email.startAnimation(anim_shake);
                edt_reg_email.setText("");
                edt_reg_email.requestFocus();
                Common.alert(RegistrationActivity.this, getString(R.string.txt_valid_email));
            }

            else if (edt_reg_mobile.getText().toString().trim().length()<6 || edt_reg_mobile.getText().toString().trim().length()>16) {
                status = false;
                edt_reg_mobile.startAnimation(anim_shake);
                edt_reg_mobile.requestFocus();
                Common.alert(RegistrationActivity.this, getString(R.string.txt_mobile_length));
            }


            else if (edt_reg_mobile.getText().toString().trim().substring(0,3).equals("000")|| edt_reg_mobile.getText().toString().trim().substring(0,2).equals("00")) {
                status = false;
                edt_reg_mobile.startAnimation(anim_shake);
                edt_reg_mobile.setText("");
                edt_reg_mobile.requestFocus();
                Common.alert(RegistrationActivity.this, getString(R.string.txt_mobile_valid));
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

// ******* GET DEFAULT COUNTRY AND COUNTRY CODE *******

    public void getDefaultCountry(){

        // String locale = RegistrationActivity.this.getResources().getConfiguration().locale.getCountry();

        readXMLofCountries();

        try {
            TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String countryCode = tm.getSimCountryIso().toUpperCase();

            int pos = 0;
            for(int i=0; i<arr_counntries_map_List.size();i++){

                if(arr_counntries_map_List.get(i).get("countryCode").toUpperCase().equals(countryCode)){

                     pos = i;
                }
            }

            if(!arr_counntries_map_List.isEmpty()) {
                tv_country_code.setText("(" + arr_counntries_map_List.get(pos).get("countryCode").toUpperCase() + ")" + " + " + arr_counntries_map_List.get(pos).get("countryISDCode"));
                iv_reg_flag_icon.setImageResource(RegistrationActivity.getFlagResID(arr_counntries_map_List.get(pos).get("countryCode")));
                sel_country_code = arr_counntries_map_List.get(pos).get("countryISDCode");
                sel_country_name = arr_counntries_map_List.get(pos).get("countryName");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

// ******* OPEN COUNTRY DIALOG *******

    public void openCountryDialog(){

        dialog = new Dialog(RegistrationActivity.this);
        dialog.setContentView(R.layout.dialog_country_list);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();

      //  readXMLofCountries();

        ListView lv_country_list   = (ListView)dialog.findViewById(R.id.lv_country_list);
        lv_country_list.setAdapter(new CountriesListAdapter(RegistrationActivity.this, arr_counntries_map_List) {
            @Override
            protected void onListItemClick(View v, String position) {

                tv_country_code.setText("("+ arr_counntries_map_List.get(Integer.parseInt(position)).get("countryCode").toUpperCase()+")" +" + " + arr_counntries_map_List.get(Integer.parseInt(position)).get("countryISDCode"));
                iv_reg_flag_icon.setImageResource(RegistrationActivity.getFlagResID(data.get(Integer.parseInt(position)).get("countryCode")));
                sel_country_code = arr_counntries_map_List.get(Integer.parseInt(position)).get("countryISDCode");
                sel_country_name = arr_counntries_map_List.get(Integer.parseInt(position)).get("countryName");
                dialog.dismiss();
            }
        });
    }

// ******* GET COUNTRIES FROM XML *******

    public void readXMLofCountries(){

        arr_counntries_map_List.clear();

        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = xmlFactoryObject.newPullParser();
            InputStream ins = RegistrationActivity.this.getResources().openRawResource(R.raw.countries);
            xmlPullParser.setInput(ins, null);
            int event = xmlPullParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = xmlPullParser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("country")) {

                            HashMap <String, String> countries_list_Map = new HashMap<String, String>();

                            countries_list_Map.put("countryCode",     xmlPullParser.getAttributeValue(null, "code"));//.toUpperCase());
                            countries_list_Map.put("countryISDCode",  xmlPullParser.getAttributeValue(null, "phoneCode"));
                            countries_list_Map.put("countryName",     xmlPullParser.getAttributeValue(null, "name"));

                            arr_counntries_map_List.add(countries_list_Map);
                        }
                        break;
                }
                event = xmlPullParser.next();
            }

            //
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        // return countries;
    }

// ******* GET COUNTRIES FLAG ID *******

    public static int getFlagResID(String countryCode) {

        switch (countryCode) {
            case "af": //afghanistan
                return R.drawable.flag_afghanistan;
            case "al": //albania
                return R.drawable.flag_albania;
            case "dz": //algeria
                return R.drawable.flag_algeria;
            case "ad": //andorra
                return R.drawable.flag_andorra;
            case "ao": //angola
                return R.drawable.flag_angola;
            case "aq": //antarctica // custom
                return R.drawable.flag_antarctica;
            case "ar": //argentina
                return R.drawable.flag_argentina;
            case "am": //armenia
                return R.drawable.flag_armenia;
            case "aw": //aruba
                return R.drawable.flag_aruba;
            case "au": //australia
                return R.drawable.flag_australia;
            case "at": //austria
                return R.drawable.flag_austria;
            case "az": //azerbaijan
                return R.drawable.flag_azerbaijan;
            case "bh": //bahrain
                return R.drawable.flag_bahrain;
            case "bd": //bangladesh
                return R.drawable.flag_bangladesh;
            case "by": //belarus
                return R.drawable.flag_belarus;
            case "be": //belgium
                return R.drawable.flag_belgium;
            case "bz": //belize
                return R.drawable.flag_belize;
            case "bj": //benin
                return R.drawable.flag_benin;
            case "bt": //bhutan
                return R.drawable.flag_bhutan;
            case "bo": //bolivia, plurinational state of
                return R.drawable.flag_bolivia;
            case "ba": //bosnia and herzegovina
                return R.drawable.flag_bosnia;
            case "bw": //botswana
                return R.drawable.flag_botswana;
            case "br": //brazil
                return R.drawable.flag_brazil;
            case "bn": //brunei darussalam // custom
                return R.drawable.flag_brunei;
            case "bg": //bulgaria
                return R.drawable.flag_bulgaria;
            case "bf": //burkina faso
                return R.drawable.flag_burkina_faso;
            case "mm": //myanmar
                return R.drawable.flag_myanmar;
            case "bi": //burundi
                return R.drawable.flag_burundi;
            case "kh": //cambodia
                return R.drawable.flag_cambodia;
            case "cm": //cameroon
                return R.drawable.flag_cameroon;
            case "ca": //canada
                return R.drawable.flag_canada;
            case "cv": //cape verde
                return R.drawable.flag_cape_verde;
            case "cf": //central african republic
                return R.drawable.flag_central_african_republic;
            case "td": //chad
                return R.drawable.flag_chad;
            case "cl": //chile
                return R.drawable.flag_chile;
            case "cn": //china
                return R.drawable.flag_china;
            case "cx": //christmas island
                return R.drawable.flag_christmas_island;
            case "cc": //cocos (keeling) islands
                return R.drawable.flag_cocos;// custom
            case "co": //colombia
                return R.drawable.flag_colombia;
            case "km": //comoros
                return R.drawable.flag_comoros;
            case "cg": //congo
                return R.drawable.flag_republic_of_the_congo;
            case "cd": //congo, the democratic republic of the
                return R.drawable.flag_democratic_republic_of_the_congo;
            case "ck": //cook islands
                return R.drawable.flag_cook_islands;
            case "cr": //costa rica
                return R.drawable.flag_costa_rica;
            case "hr": //croatia
                return R.drawable.flag_croatia;
            case "cu": //cuba
                return R.drawable.flag_cuba;
            case "cy": //cyprus
                return R.drawable.flag_cyprus;
            case "cz": //czech republic
                return R.drawable.flag_czech_republic;
            case "dk": //denmark
                return R.drawable.flag_denmark;
            case "dj": //djibouti
                return R.drawable.flag_djibouti;
            case "tl": //timor-leste
                return R.drawable.flag_timor_leste;
            case "ec": //ecuador
                return R.drawable.flag_ecuador;
            case "eg": //egypt
                return R.drawable.flag_egypt;
            case "sv": //el salvador
                return R.drawable.flag_el_salvador;
            case "gq": //equatorial guinea
                return R.drawable.flag_equatorial_guinea;
            case "er": //eritrea
                return R.drawable.flag_eritrea;
            case "ee": //estonia
                return R.drawable.flag_estonia;
            case "et": //ethiopia
                return R.drawable.flag_ethiopia;
            case "fk": //falkland islands (malvinas)
                return R.drawable.flag_falkland_islands;
            case "fo": //faroe islands
                return R.drawable.flag_faroe_islands;
            case "fj": //fiji
                return R.drawable.flag_fiji;
            case "fi": //finland
                return R.drawable.flag_finland;
            case "fr": //france
                return R.drawable.flag_france;
            case "pf": //french polynesia
                return R.drawable.flag_french_polynesia;
            case "ga": //gabon
                return R.drawable.flag_gabon;
            case "gm": //gambia
                return R.drawable.flag_gambia;
            case "ge": //georgia
                return R.drawable.flag_georgia;
            case "de": //germany
                return R.drawable.flag_germany;
            case "gh": //ghana
                return R.drawable.flag_ghana;
            case "gi": //gibraltar
                return R.drawable.flag_gibraltar;
            case "gr": //greece
                return R.drawable.flag_greece;
            case "gl": //greenland
                return R.drawable.flag_greenland;
            case "gt": //guatemala
                return R.drawable.flag_guatemala;
            case "gn": //guinea
                return R.drawable.flag_guinea;
            case "gw": //guinea-bissau
                return R.drawable.flag_guinea_bissau;
            case "gy": //guyana
                return R.drawable.flag_guyana;
            case "gf": //guyane
                return R.drawable.flag_guyane;
            case "ht": //haiti
                return R.drawable.flag_haiti;
            case "hn": //honduras
                return R.drawable.flag_honduras;
            case "hk": //hong kong
                return R.drawable.flag_hong_kong;
            case "hu": //hungary
                return R.drawable.flag_hungary;
            case "in": //india
                return R.drawable.flag_india;
            case "id": //indonesia
                return R.drawable.flag_indonesia;
            case "ir": //iran, islamic republic of
                return R.drawable.flag_iran;
            case "iq": //iraq
                return R.drawable.flag_iraq;
            case "ie": //ireland
                return R.drawable.flag_ireland;
            case "im": //isle of man
                return R.drawable.flag_isleof_man; // custom
            case "il": //israel
                return R.drawable.flag_israel;
            case "it": //italy
                return R.drawable.flag_italy;
            case "ci": //côte d\'ivoire
                return R.drawable.flag_cote_divoire;
            case "jp": //japan
                return R.drawable.flag_japan;
            case "jo": //jordan
                return R.drawable.flag_jordan;
            case "kz": //kazakhstan
                return R.drawable.flag_kazakhstan;
            case "ke": //kenya
                return R.drawable.flag_kenya;
            case "ki": //kiribati
                return R.drawable.flag_kiribati;
            case "kw": //kuwait
                return R.drawable.flag_kuwait;
            case "kg": //kyrgyzstan
                return R.drawable.flag_kyrgyzstan;
            case "la": //lao people\'s democratic republic
                return R.drawable.flag_laos;
            case "lv": //latvia
                return R.drawable.flag_latvia;
            case "lb": //lebanon
                return R.drawable.flag_lebanon;
            case "ls": //lesotho
                return R.drawable.flag_lesotho;
            case "lr": //liberia
                return R.drawable.flag_liberia;
            case "ly": //libya
                return R.drawable.flag_libya;
            case "li": //liechtenstein
                return R.drawable.flag_liechtenstein;
            case "lt": //lithuania
                return R.drawable.flag_lithuania;
            case "lu": //luxembourg
                return R.drawable.flag_luxembourg;
            case "mo": //macao
                return R.drawable.flag_macao;
            case "mk": //macedonia, the former yugoslav republic of
                return R.drawable.flag_macedonia;
            case "mg": //madagascar
                return R.drawable.flag_madagascar;
            case "mw": //malawi
                return R.drawable.flag_malawi;
            case "my": //malaysia
                return R.drawable.flag_malaysia;
            case "mv": //maldives
                return R.drawable.flag_maldives;
            case "ml": //mali
                return R.drawable.flag_mali;
            case "mt": //malta
                return R.drawable.flag_malta;
            case "mh": //marshall islands
                return R.drawable.flag_marshall_islands;
            case "mr": //mauritania
                return R.drawable.flag_mauritania;
            case "mu": //mauritius
                return R.drawable.flag_mauritius;
            case "yt": //mayotte
                return R.drawable.flag_martinique; // no exact flag found
            case "re": //la reunion
                return R.drawable.flag_martinique; // no exact flag found
            case "mq": //martinique
                return R.drawable.flag_martinique;
            case "mx": //mexico
                return R.drawable.flag_mexico;
            case "fm": //micronesia, federated states of
                return R.drawable.flag_micronesia;
            case "md": //moldova, republic of
                return R.drawable.flag_moldova;
            case "mc": //monaco
                return R.drawable.flag_monaco;
            case "mn": //mongolia
                return R.drawable.flag_mongolia;
            case "me": //montenegro
                return R.drawable.flag_of_montenegro;// custom
            case "ma": //morocco
                return R.drawable.flag_morocco;
            case "mz": //mozambique
                return R.drawable.flag_mozambique;
            case "na": //namibia
                return R.drawable.flag_namibia;
            case "nr": //nauru
                return R.drawable.flag_nauru;
            case "np": //nepal
                return R.drawable.flag_nepal;
            case "nl": //netherlands
                return R.drawable.flag_netherlands;
            case "nc": //new caledonia
                return R.drawable.flag_new_caledonia;// custom
            case "nz": //new zealand
                return R.drawable.flag_new_zealand;
            case "ni": //nicaragua
                return R.drawable.flag_nicaragua;
            case "ne": //niger
                return R.drawable.flag_niger;
            case "ng": //nigeria
                return R.drawable.flag_nigeria;
            case "nu": //niue
                return R.drawable.flag_niue;
            case "kp": //north korea
                return R.drawable.flag_north_korea;
            case "no": //norway
                return R.drawable.flag_norway;
            case "om": //oman
                return R.drawable.flag_oman;
            case "pk": //pakistan
                return R.drawable.flag_pakistan;
            case "pw": //palau
                return R.drawable.flag_palau;
            case "pa": //panama
                return R.drawable.flag_panama;
            case "pg": //papua new guinea
                return R.drawable.flag_papua_new_guinea;
            case "py": //paraguay
                return R.drawable.flag_paraguay;
            case "pe": //peru
                return R.drawable.flag_peru;
            case "ph": //philippines
                return R.drawable.flag_philippines;
            case "pn": //pitcairn
                return R.drawable.flag_pitcairn_islands;
            case "pl": //poland
                return R.drawable.flag_poland;
            case "pt": //portugal
                return R.drawable.flag_portugal;
            case "pr": //puerto rico
                return R.drawable.flag_puerto_rico;
            case "qa": //qatar
                return R.drawable.flag_qatar;
            case "ro": //romania
                return R.drawable.flag_romania;
            case "ru": //russian federation
                return R.drawable.flag_russian_federation;
            case "rw": //rwanda
                return R.drawable.flag_rwanda;
            case "bl": //saint barthélemy
                return R.drawable.flag_saint_barthelemy;// custom
            case "ws": //samoa
                return R.drawable.flag_samoa;
            case "sm": //san marino
                return R.drawable.flag_san_marino;
            case "st": //sao tome and principe
                return R.drawable.flag_sao_tome_and_principe;
            case "sa": //saudi arabia
                return R.drawable.flag_saudi_arabia;
            case "sn": //senegal
                return R.drawable.flag_senegal;
            case "rs": //serbia
                return R.drawable.flag_serbia; // custom
            case "sc": //seychelles
                return R.drawable.flag_seychelles;
            case "sl": //sierra leone
                return R.drawable.flag_sierra_leone;
            case "sg": //singapore
                return R.drawable.flag_singapore;
            case "sk": //slovakia
                return R.drawable.flag_slovakia;
            case "si": //slovenia
                return R.drawable.flag_slovenia;
            case "sb": //solomon islands
                return R.drawable.flag_soloman_islands;
            case "so": //somalia
                return R.drawable.flag_somalia;
            case "za": //south africa
                return R.drawable.flag_south_africa;
            case "kr": //south korea
                return R.drawable.flag_south_korea;
            case "es": //spain
                return R.drawable.flag_spain;
            case "lk": //sri lanka
                return R.drawable.flag_sri_lanka;
            case "sh": //saint helena, ascension and tristan da cunha
                return R.drawable.flag_saint_helena; // custom
            case "pm": //saint pierre and miquelon
                return R.drawable.flag_saint_pierre;
            case "sd": //sudan
                return R.drawable.flag_sudan;
            case "sr": //suriname
                return R.drawable.flag_suriname;
            case "sz": //swaziland
                return R.drawable.flag_swaziland;
            case "se": //sweden
                return R.drawable.flag_sweden;
            case "ch": //switzerland
                return R.drawable.flag_switzerland;
            case "sy": //syrian arab republic
                return R.drawable.flag_syria;
            case "tw": //taiwan, province of china
                return R.drawable.flag_taiwan;
            case "tj": //tajikistan
                return R.drawable.flag_tajikistan;
            case "tz": //tanzania, united republic of
                return R.drawable.flag_tanzania;
            case "th": //thailand
                return R.drawable.flag_thailand;
            case "tg": //togo
                return R.drawable.flag_togo;
            case "tk": //tokelau
                return R.drawable.flag_tokelau; // custom
            case "to": //tonga
                return R.drawable.flag_tonga;
            case "tn": //tunisia
                return R.drawable.flag_tunisia;
            case "tr": //turkey
                return R.drawable.flag_turkey;
            case "tm": //turkmenistan
                return R.drawable.flag_turkmenistan;
            case "tv": //tuvalu
                return R.drawable.flag_tuvalu;
            case "ae": //united arab emirates
                return R.drawable.flag_uae;
            case "ug": //uganda
                return R.drawable.flag_uganda;
            case "gb": //united kingdom
                return R.drawable.flag_united_kingdom;
            case "ua": //ukraine
                return R.drawable.flag_ukraine;
            case "uy": //uruguay
                return R.drawable.flag_uruguay;
            case "us": //united states
                return R.drawable.flag_united_states_of_america;
            case "uz": //uzbekistan
                return R.drawable.flag_uzbekistan;
            case "vu": //vanuatu
                return R.drawable.flag_vanuatu;
            case "va": //holy see (vatican city state)
                return R.drawable.flag_vatican_city;
            case "ve": //venezuela, bolivarian republic of
                return R.drawable.flag_venezuela;
            case "vn": //vietnam
                return R.drawable.flag_vietnam;
            case "wf": //wallis and futuna
                return R.drawable.flag_wallis_and_futuna;
            case "ye": //yemen
                return R.drawable.flag_yemen;
            case "zm": //zambia
                return R.drawable.flag_zambia;
            case "zw": //zimbabwe
                return R.drawable.flag_zimbabwe;

            // Caribbean Islands
            case "ai": //anguilla
                return R.drawable.flag_anguilla;
            case "ag": //antigua & barbuda
                return R.drawable.flag_antigua_and_barbuda;
            case "bs": //bahamas
                return R.drawable.flag_bahamas;
            case "bb": //barbados
                return R.drawable.flag_barbados;
            case "bm": //bermuda
                return R.drawable.flag_bermuda;
            case "vg": //british virgin islands
                return R.drawable.flag_british_virgin_islands;
            case "dm": //dominica
                return R.drawable.flag_dominica;
            case "do": //dominican republic
                return R.drawable.flag_dominican_republic;
            case "gd": //grenada
                return R.drawable.flag_grenada;
            case "jm": //jamaica
                return R.drawable.flag_jamaica;
            case "ms": //montserrat
                return R.drawable.flag_montserrat;
            case "kn": //st kitts & nevis
                return R.drawable.flag_saint_kitts_and_nevis;
            case "lc": //st lucia
                return R.drawable.flag_saint_lucia;
            case "vc": //st vincent & the grenadines
                return R.drawable.flag_saint_vicent_and_the_grenadines;
            case "tt": //trinidad & tobago
                return R.drawable.flag_trinidad_and_tobago;
            case "tc": //turks & caicos islands
                return R.drawable.flag_turks_and_caicos_islands;
            case "vi": //us virgin islands
                return R.drawable.flag_us_virgin_islands;
            default:
                return R.drawable.flag_transparent;
        }
    }

}
