package com.sparsh.smartparkingsystem.dashboard;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.sparsh.smartparkingsystem.R;
import com.sparsh.smartparkingsystem.common.Common;
import com.sparsh.smartparkingsystem.common.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{

// ******* Declaring Variables *******

    double latitude;   // latitude
    double longitude;  // longitude

// ******* Declaring TextView *******

    TextView tv_title;

// ******* Declaring Edit Text View *******

    EditText edt_auto_search;

// ******* Declaring List View *******

    ListView lv_auto_search_list;

// ******* Declaring ImageView *******

    ImageView iv_search_icon, iv_home, iv_booking, iv_setting, iv_cnt;

// ******* Declaring layouts *******

    RelativeLayout rl_home_btn, rl_booking_btn, rl_settings_btn, rl_contact_btn, rl_home, rl_bookings, rl_settings, rl_contact;

// ******* Declaring Map Variables *******

    Map map = null;

// ******* Declaring Progress Bar *******

    ProgressDialog pDialog;

// ******* Declaring request permissions *******

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

// ******* Declaring arrays of request permission *******

    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };

// ******* DECLARING ARRAY LIST MAP *******

    ArrayList <HashMap <String, String>> arr_search_details_map_List = new ArrayList <HashMap<String,String>>();

// ******* DECLARING CLASS OBJECT *******

    AutoSearchAdapter autoSearchAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

    // ******* Check Manifest Permissions *******

        checkPermissions();

    // ******* TextViews *******

        tv_title = (TextView)findViewById(R.id.tv_title);

    // ******* Edit Text Views *******

        edt_auto_search = (EditText)findViewById(R.id.edt_auto_search);
        edt_auto_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(count>=3) {
                    if (Common.isConnectingToInternet(DashboardActivity.this)) {

                        auto_search_places_api(String.valueOf(s));
                    } else {
                        Common.alert(DashboardActivity.this, getResources().getString(R.string.no_internet_txt));
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    // ******* Image Views *******

        iv_search_icon = (ImageView)findViewById(R.id.iv_search_icon);
        iv_home        = (ImageView)findViewById(R.id.iv_home);
        iv_booking     = (ImageView)findViewById(R.id.iv_booking);
        iv_setting     = (ImageView)findViewById(R.id.iv_setting);
        iv_cnt         = (ImageView)findViewById(R.id.iv_cnt);

    // ******* Relative Layouts *******

        rl_home         = (RelativeLayout)findViewById(R.id.rl_home);
        rl_bookings     = (RelativeLayout)findViewById(R.id.rl_bookings);
        rl_settings     = (RelativeLayout)findViewById(R.id.rl_settings);
        rl_contact      = (RelativeLayout)findViewById(R.id.rl_contact);

        rl_home_btn     = (RelativeLayout)findViewById(R.id.rl_home_btn);
        rl_booking_btn  = (RelativeLayout)findViewById(R.id.rl_booking_btn);
        rl_settings_btn = (RelativeLayout)findViewById(R.id.rl_settings_btn);
        rl_contact_btn  = (RelativeLayout)findViewById(R.id.rl_contact_btn);

    // ******* AutoSearch ListView *******

        lv_auto_search_list = (ListView)findViewById(R.id.lv_auto_search_list);

    // ******* Register listeners of onClick *******

        rl_home_btn.setOnClickListener(this);
        rl_booking_btn.setOnClickListener(this);
        rl_settings_btn.setOnClickListener(this);
        rl_contact_btn.setOnClickListener(this);
        iv_search_icon.setOnClickListener(this);
    }

// ******* Get Location Method *******

    public void getLocation(){

        // check if GPS enabled
        GPSTracker gpsTracker = new GPSTracker(DashboardActivity.this);

        if (gpsTracker.canGetLocation()){

            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

            //  String stringLatitude = String.valueOf(gpsTracker.latitude);
           /* textview = (TextView)findViewById(R.id.fieldLatitude);
            textview.setText(stringLatitude);*/

            //String stringLongitude = String.valueOf(gpsTracker.longitude);
           /* textview = (TextView)findViewById(R.id.fieldLongitude);
            textview.setText(stringLongitude);*/

          /*  String country = gpsTracker.getCountryName(this);
            textview = (TextView)findViewById(R.id.fieldCountry);
            textview.setText(country);

            String city = gpsTracker.getLocality(this);
            textview = (TextView)findViewById(R.id.fieldCity);
            textview.setText(city);

            String postalCode = gpsTracker.getPostalCode(this);
            textview = (TextView)findViewById(R.id.fieldPostalCode);
            textview.setText(postalCode);

            String addressLine = gpsTracker.getAddressLine(this);
            textview = (TextView)findViewById(R.id.fieldAddressLine);
            textview.setText(addressLine);*/
        }
        else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }
    }

// ******* On click Method *******
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.rl_home_btn:

                tv_title.setText(getResources().getString(R.string.txt_home_lbl));

                rl_home.setVisibility(View.VISIBLE);
                rl_bookings.setVisibility(View.GONE);
                rl_settings.setVisibility(View.GONE);
                rl_contact.setVisibility(View.GONE);

                iv_home.setImageResource(R.drawable.home_w);
                iv_booking.setImageResource(R.drawable.info_g);
                iv_setting.setImageResource(R.drawable.ic_menu_manage);
                iv_cnt.setImageResource(R.drawable.contact_g);

                break;

            case R.id.rl_booking_btn:

                tv_title.setText(getResources().getString(R.string.txt_booking_lbl));

                rl_home.setVisibility(View.GONE);
                rl_bookings.setVisibility(View.VISIBLE);
                rl_settings.setVisibility(View.GONE);
                rl_contact.setVisibility(View.GONE);

                iv_home.setImageResource(R.drawable.home_g);
                iv_booking.setImageResource(R.drawable.info_w);
                iv_setting.setImageResource(R.drawable.ic_menu_manage);
                iv_cnt.setImageResource(R.drawable.contact_g);

                break;

            case R.id.rl_settings_btn:

                tv_title.setText(getResources().getString(R.string.txt_settings_lbl));

                rl_home.setVisibility(View.GONE);
                rl_bookings.setVisibility(View.GONE);
                rl_settings.setVisibility(View.VISIBLE);
                rl_contact.setVisibility(View.GONE);

                iv_home.setImageResource(R.drawable.home_g);
                iv_booking.setImageResource(R.drawable.info_g);
                iv_setting.setImageResource(R.drawable.ic_menu_manage);
                iv_cnt.setImageResource(R.drawable.contact_g);

                break;

            case R.id.rl_contact_btn:

                tv_title.setText(getResources().getString(R.string.txt_contact_lbl));

                rl_home.setVisibility(View.GONE);
                rl_bookings.setVisibility(View.GONE);
                rl_settings.setVisibility(View.GONE);
                rl_contact.setVisibility(View.VISIBLE);

                iv_home.setImageResource(R.drawable.home_w);
                iv_booking.setImageResource(R.drawable.info_g);
                iv_setting.setImageResource(R.drawable.ic_menu_manage);
                iv_cnt.setImageResource(R.drawable.contact_g);

                break;


            case R.id.iv_search_icon:

                break;

            default:

                break;
        }
    }

// ******* Check Permissions *******

    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions.toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS, grantResults);
        }
    }

// ******* Request Permissions *******

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index] + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // Get Current Location Co-ordinates
                getLocation();
                // all permissions were granted
                initialize(latitude, longitude);
                break;
        }
    }

// ******* Initialize Map *******

    private void initialize(final double latitude, final double longitude) {

        final MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapfragment);

         mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error){

                if (error == OnEngineInitListener.Error.NONE) {
                    // retrieve a reference of the map from the map fragment
                    map = mapFragment.getMap();
                    // Set the map center to the Vancouver region (no animation)
                    //map.setCenter(new GeoCoordinate(49.196261, -123.004773, 0.0), Map.Animation.NONE);
                    map.setCenter(new GeoCoordinate(latitude, longitude, 0.0), Map.Animation.NONE);
                    // Set the zoom level to the average between min and max
                    //map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
                    map.setZoomLevel(16);
                    map.getPositionIndicator().setVisible(true);
                    //Image icon = (R.drawable.home_g);
                    Image icon = new Image();
                    Bitmap bit_icon = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.marker_icon64);
                    icon.setBitmap(bit_icon);
                    map.getPositionIndicator().setMarker(icon);


                } else {
                    System.out.println("ERROR: Cannot initialize Map Fragment");
                }
            }
        });
    }

// ******* AUTO SEARCH PLACES API *******

    public void auto_search_places_api(String auto_search_txt) {

        pDialog = new ProgressDialog(DashboardActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();
/*
        java.util.Map<String, String> postParam = new HashMap<String, String>();
        postParam.put("email",        "");
        postParam.put("password",     "");*/

        String search_url = "https://places.cit.api.here.com/places/v1/autosuggest?at=0.0,0.0&q=" + auto_search_txt +
                "&app_id=wCs1E0fTbD5AEF2v60WW&app_code=o0Kuom5cmlz3udm3SrEPvg&tf=plain&pretty=true";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, search_url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                pDialog.cancel();

                try {
                    arr_search_details_map_List.clear();

                    JSONArray arr_search_details_Json = response.getJSONArray("results");

                    String sfdg = String.valueOf(arr_search_details_Json.length());
                    for (int i = 0; i < arr_search_details_Json.length(); i++) {

                        JSONObject auto_search_Obj = arr_search_details_Json.getJSONObject(i);

                        HashMap<String, String> auto_search_Map = new HashMap<String, String>();

                       // auto_search_Map.put("id",                   auto_search_Obj.getString("id"));
                        auto_search_Map.put("title",                auto_search_Obj.getString("title"));
                       // auto_search_Map.put("highlightedTitle",     auto_search_Obj.getString("highlightedTitle"));
                      //  auto_search_Map.put("vicinity",             auto_search_Obj.getString("vicinity"));
                    //    auto_search_Map.put("highlightedVicinity",  auto_search_Obj.getString("highlightedVicinity"));
                     //   auto_search_Map.put("category",             auto_search_Obj.getString("category"));
                     //   auto_search_Map.put("type",                 auto_search_Obj.getString("type"));

                        arr_search_details_map_List.add(auto_search_Map);
                    }

                    if(!arr_search_details_map_List.isEmpty()){

                        lv_auto_search_list.setVisibility(View.VISIBLE);
                        autoSearchAdapter = new AutoSearchAdapter(DashboardActivity.this, arr_search_details_map_List);
                        lv_auto_search_list.setAdapter(autoSearchAdapter);
                    }else{
                        lv_auto_search_list.setVisibility(View.GONE);
                    }






                  //  resMsg = response.get("success").toString();

                   /* if (resMsg.equals("true")) {

                  *//*  Common.alert(RegistrationActivity.this, getResources().getString(R.string.msg_success));

                    pref.set(Constants.kF_name,     edt_fname.getText().toString().trim());
                    pref.set(Constants.kL_name,     edt_lname.getText().toString().trim());
                    pref.set(Constants.kContact_no, edt_cnt_no.getText().toString().trim());
                    pref.set(Constants.kType,       gender_type);
                    pref.commit();
                  *//*
                    } else {

                        Common.alert(DashboardActivity.this, response.get("message").toString());
                    }*/
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
        Volley.newRequestQueue(DashboardActivity.this).add(jsObjRequest);
    }

// ******* Search dialog *******




}
