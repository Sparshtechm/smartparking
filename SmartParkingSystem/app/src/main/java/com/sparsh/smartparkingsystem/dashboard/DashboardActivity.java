package com.sparsh.smartparkingsystem.dashboard;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.sparsh.smartparkingsystem.QRcode.QR_Code_Activity;
import com.sparsh.smartparkingsystem.R;
import com.sparsh.smartparkingsystem.booking.Booking_Availability;
import com.sparsh.smartparkingsystem.booking.Booking_Availability_Adapter;
import com.sparsh.smartparkingsystem.booking.Parking_Selection_Activity;
import com.sparsh.smartparkingsystem.common.Common;
import com.sparsh.smartparkingsystem.common.Constants;
import com.sparsh.smartparkingsystem.common.GPSTracker;
import com.sparsh.smartparkingsystem.common.Preferences;
import com.sparsh.smartparkingsystem.profile.ChangePswdActivity;
import com.sparsh.smartparkingsystem.profile.ProfileActivity;
import com.sparsh.smartparkingsystem.registration.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener{

// ******* Declaring Variables *******

    String resMsg, resCode, pay_screen_chk = "0", booking_msg;
    double latitude;   // latitude
    double longitude;  // longitude

// ******* Declaring TextView *******

    TextView tv_title;

// ******* Declaring Edit Text View *******

    EditText edt_auto_search;

// ******* Declaring List View *******

    ListView lv_auto_search_list, lv_booking_list;

// ******* Declaring ImageView *******

    ImageView iv_search_icon, iv_home, iv_booking, iv_setting, iv_cnt;

// ******* Declaring layouts *******

    RelativeLayout rl_home_btn, rl_booking_btn, rl_settings_btn, rl_contact_btn, rl_home, rl_bookings, rl_settings, rl_contact;
    RelativeLayout rl_settings_profile, rl_settings_notify, rl_settings_change_pswd, rl_settings_logout;

// ******* Declaring Map Variables *******

    Map map = null;
    MapFragment mapFragment;

// ******* Declaring Progress Bar *******

    ProgressDialog pDialog;

// ******* Declaring request permissions *******

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

// ******* Declaring arrays of request permission *******

    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };

// ******* DECLARING ARRAY LIST MAP *******

    ArrayList <HashMap <String, String>> arr_search_details_map_List = new ArrayList <HashMap<String,String>>();
    ArrayList <HashMap <String, String>> arr_parking_zones_map_List = new ArrayList<HashMap<String, String>>();
    ArrayList <HashMap <String, String>> arr_booking_history_map_List = new ArrayList<HashMap<String, String>>();

// ******* DECLARING CLASS OBJECT *******

    AutoSearchAdapter autoSearchAdapter;

// ******* Declaring Class Objects *******

    Preferences pref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        pref = new Preferences(DashboardActivity.this);

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

                /* if (Common.isConnectingToInternet(DashboardActivity.this)) {

                    auto_search_places_api(String.valueOf(s));
                } else {
                    Common.alert(DashboardActivity.this, getResources().getString(R.string.no_internet_txt));
                }*/
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

        rl_settings_profile     = (RelativeLayout)findViewById(R.id.rl_settings_profile);
        rl_settings_notify      = (RelativeLayout)findViewById(R.id.rl_settings_notify);
        rl_settings_change_pswd = (RelativeLayout)findViewById(R.id.rl_settings_change_pswd);
        rl_settings_logout      = (RelativeLayout)findViewById(R.id.rl_settings_logout);

    // ******* AutoSearch ListView *******

        lv_auto_search_list = (ListView)findViewById(R.id.lv_auto_search_list);
        lv_booking_list     = (ListView)findViewById(R.id.lv_booking_list);

    // ******* Add Click Listeners *******

        rl_home_btn.setOnClickListener(this);
        rl_booking_btn.setOnClickListener(this);
        rl_settings_btn.setOnClickListener(this);
        rl_contact_btn.setOnClickListener(this);
        iv_search_icon.setOnClickListener(this);

        rl_settings_profile.setOnClickListener(this);
        rl_settings_notify.setOnClickListener(this);
        rl_settings_change_pswd.setOnClickListener(this);
        rl_settings_logout.setOnClickListener(this);

    // ******* Check screens *******

        try {
            Intent intent = getIntent();
           if(intent.hasExtra("CHK")){
               pay_screen_chk = intent.getStringExtra("CHK");
               booking_msg = intent.getStringExtra("MSG");
           }else{
               pay_screen_chk = "0";//intent.getStringExtra("CHK");
               booking_msg = "Booking history";//intent.getStringExtra("MSG");
           }


        } catch (Exception e) {
            e.printStackTrace();
        }
        if(pay_screen_chk.equals("1")){

            tv_title.setText(getResources().getString(R.string.txt_booking_lbl));

            rl_home.setVisibility(View.GONE);
            rl_bookings.setVisibility(View.VISIBLE);
            rl_settings.setVisibility(View.GONE);
            rl_contact.setVisibility(View.GONE);

            iv_search_icon.setVisibility(View.GONE);
            iv_home.setImageResource(R.drawable.home_g);
            iv_booking.setImageResource(R.drawable.info_w);
            iv_setting.setImageResource(R.drawable.ic_menu_manage);
            iv_cnt.setImageResource(R.drawable.contact_g);

            Common.alert(DashboardActivity.this, booking_msg);

            // Show booking history list
            HashMap <String, String> book_history_list_Map = new HashMap<String, String>();
            book_history_list_Map.put("sdgs","sdg");

            arr_booking_history_map_List.add(book_history_list_Map);
            lv_booking_list.setAdapter(new Booking_History_Adapter(DashboardActivity.this, arr_booking_history_map_List) {
                @Override
                protected void onGetPassBtnClick(View v, String position) {
                     startActivity(new Intent(DashboardActivity.this, QR_Code_Activity.class));
                     finish();
                }
            });
        }
    }

// ******* Get Location Method *******

    public void getLocation(){

        // check if GPS enabled
        GPSTracker gpsTracker = new GPSTracker(DashboardActivity.this);

        if (gpsTracker.canGetLocation()){

            latitude  = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

            /* latitude = 23.464311;
            longitude = 17.345611;*/

            /* latitude = 1.00;
            longitude = 1.00;

           */
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

            if (Common.isConnectingToInternet(DashboardActivity.this)) {

                get_parking_zones_list_api();

            } else {
                Common.alert(DashboardActivity.this, getResources().getString(R.string.no_internet_txt));
            }
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

                iv_search_icon.setVisibility(View.VISIBLE);
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

                iv_search_icon.setVisibility(View.GONE);
                iv_home.setImageResource(R.drawable.home_g);
                iv_booking.setImageResource(R.drawable.info_w);
                iv_setting.setImageResource(R.drawable.ic_menu_manage);
                iv_cnt.setImageResource(R.drawable.contact_g);

                /*startActivity(new Intent(DashboardActivity.this, QR_Code_Activity.class));
                finish();*/

                break;

            case R.id.rl_settings_btn:

                tv_title.setText(getResources().getString(R.string.txt_settings_lbl));

                rl_home.setVisibility(View.GONE);
                rl_bookings.setVisibility(View.GONE);
                rl_settings.setVisibility(View.VISIBLE);
                rl_contact.setVisibility(View.GONE);

                iv_search_icon.setVisibility(View.GONE);
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

                iv_search_icon.setVisibility(View.GONE);
                iv_home.setImageResource(R.drawable.home_w);
                iv_booking.setImageResource(R.drawable.info_g);
                iv_setting.setImageResource(R.drawable.ic_menu_manage);
                iv_cnt.setImageResource(R.drawable.contact_g);

                break;

            case R.id.iv_search_icon:

                break;

            case R.id.rl_settings_profile:

                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
                finish();
                break;

            case R.id.rl_settings_change_pswd:

                startActivity(new Intent(DashboardActivity.this, ChangePswdActivity.class));
                finish();

                break;

            case R.id.rl_settings_logout:

                logout_dialog();

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

         mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapfragment);

         //map = mapFragment.getMap();

         mapFragment.init(new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error){

                if (error == OnEngineInitListener.Error.NONE) {
                    // retrieve a reference of the map from the map fragment
                    map = mapFragment.getMap();

                    // map.setCenter(new GeoCoordinate(49.196261, -123.004773, 0.0), Map.Animation.NONE);
                    map.setCenter(new GeoCoordinate(latitude, longitude, 0.0), Map.Animation.NONE);
                    // Set the zoom level to the average between min and max
                    // map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
                    map.setZoomLevel(16);

                    Image icon = new Image();
                    Bitmap bit_icon = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.marker_icon64);
                    icon.setBitmap(bit_icon);
                    map.getPositionIndicator().setMarker(icon);
                    map.getPositionIndicator().setVisible(true);

                    set_markers_on_zones();
                    mapFragment.getMapGesture().addOnGestureListener(new MyOnGestureListener());



                } else {
                    System.out.println("ERROR: Cannot initialize Map Fragment");
                }
            }
        });


        // set all nearest parking zones... with markers.

       //set_markers_on_zones();


       // mapFragment.getMapGesture().addOnGestureListener(mMyGestureHandler);
    }

// ******* SET ALL NEAREST PARKING ZONES WITH MARKERS *******

    public void set_markers_on_zones(){


        Image icon = new Image();
        Bitmap bit_icon = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.mobile_icon);
        icon.setBitmap(bit_icon);
        //mm.setIcon(icon);
      //  mm.setCoordinate(new GeoCoordinate(24.984311,17.345611));
       // mm.setDescription("Hello...!!!");
      //  map.getPositionIndicator().setMarker(icon);

        //map.addMapObject(mm);

        try {
            MapMarker myMapMarker = new MapMarker(new GeoCoordinate(24.984311, 17.345611), icon);
            myMapMarker.setDescription("Hello...!!!");
            map.addMapObject(myMapMarker);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

                    for (int i = 0; i < arr_search_details_Json.length(); i++) {

                        JSONObject auto_search_Obj = arr_search_details_Json.getJSONObject(i);

                        HashMap<String, String> auto_search_Map = new HashMap<String, String>();

                        auto_search_Map.put("title",     auto_search_Obj.getString("title"));

                        //JSONArray arr_location_Json = auto_search_Obj.getJSONArray("position");

                        if(auto_search_Obj.has("position")){
                            auto_search_Map.put("latitude",  String.valueOf(auto_search_Obj.getJSONArray("position").get(0)));
                            auto_search_Map.put("longitude", String.valueOf(auto_search_Obj.getJSONArray("position").get(1)));

                            // auto_search_Map.put("id",                   auto_search_Obj.getString("id"));
                            // auto_search_Map.put("highlightedTitle",     auto_search_Obj.getString("highlightedTitle"));
                            // auto_search_Map.put("vicinity",             auto_search_Obj.getString("vicinity"));
                            // auto_search_Map.put("highlightedVicinity",  auto_search_Obj.getString("highlightedVicinity"));
                            // auto_search_Map.put("category",             auto_search_Obj.getString("category"));
                            // auto_search_Map.put("type",                 auto_search_Obj.getString("type"));

                            arr_search_details_map_List.add(auto_search_Map);
                        }
                    }

                    if(!arr_search_details_map_List.isEmpty()){

                        lv_auto_search_list.setVisibility(View.VISIBLE);
                        autoSearchAdapter = new AutoSearchAdapter(DashboardActivity.this, arr_search_details_map_List) {
                            @Override
                            protected void onRowClick(View v, String position) {

                                latitude  = Double.valueOf(arr_search_details_map_List.get(Integer.parseInt(position)).get("latitude"));
                                longitude = Double.valueOf(arr_search_details_map_List.get(Integer.parseInt(position)).get("longitude"));

                                get_parking_zones_list_api();

                            }
                        };
                        lv_auto_search_list.setAdapter(autoSearchAdapter);
                    }
                    else{
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

// ******* Logout dialog *******

    public void logout_dialog(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashboardActivity.this);


        alertDialog.setTitle("Logout...");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want logout this?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // Write your code here to invoke YES event
                //  Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();

                pref.set(Constants.kcust_id, "");
                pref.commit();


                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                finish();

            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                //  Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();

    }

// ******* GET PARKING ZONES LIST API *******

    public void get_parking_zones_list_api() {

        pDialog = new ProgressDialog(DashboardActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, getResources().getString(R.string.get_parking_zones_api) + "latitude=" + String.valueOf(latitude) + "&longitude=" + String.valueOf(longitude), null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                pDialog.cancel();

                try {
                    JSONObject header_Obj = response.getJSONObject("appHeader");
                    resCode = header_Obj.get("statusCode").toString();
                    resMsg  = header_Obj.get("statusMessage").toString();

                    if (resCode.equals("200")) {

                        JSONArray arr_zone_list_Json = response.getJSONArray("zoneList");

                        for (int i = 0; i < arr_zone_list_Json.length(); i++) {

                            JSONObject zones_list_Obj = arr_zone_list_Json.getJSONObject(i);

                            HashMap<String, String> zones_list_Map = new HashMap<String, String>();

                            zones_list_Map.put("zoneid",       zones_list_Obj.getString("zoneid"));
                            zones_list_Map.put("zoneName",     zones_list_Obj.getString("zoneName"));
                            zones_list_Map.put("addressLine1", zones_list_Obj.getString("addressLine1"));
                            zones_list_Map.put("addressLine2", zones_list_Obj.getString("addressLine2"));
                            zones_list_Map.put("latitude",     zones_list_Obj.getString("latitude"));
                            zones_list_Map.put("longitude",    zones_list_Obj.getString("longitude"));
                            zones_list_Map.put("distance",     zones_list_Obj.getString("distance"));
                            zones_list_Map.put("timeZone",     zones_list_Obj.getString("timeZone"));

                            arr_parking_zones_map_List.add(zones_list_Map);
                        }
                    } else if (resCode.equals("411")) {
                        Common.alert(DashboardActivity.this, resMsg);
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
        Volley.newRequestQueue(DashboardActivity.this).add(jsObjRequest);
    }

// ******* GESTURES LISTENER *******

    private class MyOnGestureListener implements MapGesture.OnGestureListener {

        @Override
        public void onPanStart() {
        }

        @Override
        public void onPanEnd() {
        }

        @Override
        public void onMultiFingerManipulationStart() {
        }

        @Override
        public void onMultiFingerManipulationEnd() {
        }

        @Override
        public boolean onMapObjectsSelected(List<ViewObject> objects) {

            //Common.alert(DashboardActivity.this, "Welcom to here map...!!!");

            pref.set(Constants.kZone_Id,   "1");
            pref.set(Constants.kZone_Name, "Tech Mahindra (NSEZ).");
            pref.set(Constants.kTimeZone,  "IST");
            pref.commit();
            startActivity(new Intent(DashboardActivity.this, Parking_Selection_Activity.class));
            finish();
            return false;
        }

        @Override
        public boolean onTapEvent(PointF p) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(PointF p) {
            return false;
        }

        @Override
        public void onPinchLocked() {
        }

        @Override
        public boolean onPinchZoomEvent(float scaleFactor, PointF p) {
            return false;
        }

        @Override
        public void onRotateLocked() {
        }

        @Override
        public boolean onRotateEvent(float rotateAngle) {
            return false;
        }

        @Override
        public boolean onTiltEvent(float angle) {
            return false;
        }

        @Override
        public boolean onLongPressEvent(PointF p) {
            return false;
        }

        @Override
        public void onLongPressRelease() {
        }

        @Override
        public boolean onTwoFingerTapEvent(PointF p) {
            return false;
        }
    }
}
