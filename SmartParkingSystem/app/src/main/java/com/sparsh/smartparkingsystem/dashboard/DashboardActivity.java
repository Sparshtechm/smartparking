package com.sparsh.smartparkingsystem.dashboard;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sparsh.smartparkingsystem.QRcode.QR_Code_Activity;
import com.sparsh.smartparkingsystem.R;
import com.sparsh.smartparkingsystem.booking.Parking_Selection_Activity;
import com.sparsh.smartparkingsystem.common.Common;
import com.sparsh.smartparkingsystem.common.Constants;
import com.sparsh.smartparkingsystem.common.GPSTracker;
import com.sparsh.smartparkingsystem.common.Preferences;
import com.sparsh.smartparkingsystem.payment.Payment_Activity;
import com.sparsh.smartparkingsystem.profile.ChangePswdActivity;
import com.sparsh.smartparkingsystem.profile.ProfileActivity;
import com.sparsh.smartparkingsystem.registration.LoginActivity;
import com.sparsh.smartparkingsystem.registration.RegistrationActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener{

// ******* Declaring Variables *******

    String resMsg, resCode, screen_chk = "0", booking_msg;
    double latitude;   // latitude
    double longitude;  // longitude

// ******* Declaring TextView *******

    TextView tv_title, tv_previous_tab, tv_upcoming_tab, tv_cancelled_tab, tv_logout_lbl;

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

    // Map map = null;
    // MapFragment mapFragment;

    GoogleMap mMap;

// ******* Declaring Progress Bar *******

    ProgressDialog pDialog;

// ******* Declaring request permissions *******

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

// ******* Declaring arrays of request permission *******

    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE };

// ******* DECLARING ARRAY LIST MAP *******

    ArrayList <HashMap <String, String>> arr_search_details_map_List  = new ArrayList <HashMap<String,String>>();
    ArrayList <HashMap <String, String>> arr_parking_zones_map_List   = new ArrayList<HashMap<String, String>>();
    ArrayList <HashMap <String, String>> arr_booking_history_map_List = new ArrayList<HashMap<String, String>>();
    ArrayList <HashMap <String, String>> arr_upcoming_booking_history_map_List  = new ArrayList <HashMap <String, String>>();
    ArrayList <HashMap <String, String>> arr_cancelled_booking_history_map_List = new ArrayList <HashMap <String, String>>();

// ******* DECLARING CLASS OBJECT *******

    AutoSearchAdapter autoSearchAdapter;

// ******* Declaring Class Objects *******

    Preferences pref;

    GoogleApiClient mGoogleApiClient;

// ******* Declaring Alert Dialog *******

    Dialog dialog;

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Places.GEO_DATA_API).addApi(Places.PLACE_DETECTION_API)
            .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                }
            }).build();

        pref = new Preferences(DashboardActivity.this);

    // ******* TextViews *******

        tv_title         = (TextView)findViewById(R.id.tv_title);
        tv_previous_tab  = (TextView)findViewById(R.id.tv_previous_tab);
        tv_upcoming_tab  = (TextView)findViewById(R.id.tv_upcoming_tab);
        tv_cancelled_tab = (TextView)findViewById(R.id.tv_cancelled_tab);
        tv_logout_lbl    = (TextView)findViewById(R.id.tv_logout_lbl);

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

        if(pref.get(Constants.kcust_id).equals("")){

            tv_logout_lbl.setText("Login");
        }
        else{
            tv_logout_lbl.setText("Logout");
        }

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

    // tabs button

        tv_previous_tab.setOnClickListener(this);
        tv_upcoming_tab.setOnClickListener(this);
        tv_cancelled_tab.setOnClickListener(this);

    // ******* Check Manifest Permissions *******

        checkPermissions();

        // Get Current Location Co-ordinates
        // getLocation();

        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
            mapFragment.getMapAsync(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    // ******* Check screens *******

        try {
             intent = getIntent();
           if(intent.hasExtra("CHK")){
               screen_chk = intent.getStringExtra("CHK");
               // booking_msg    = intent.getStringExtra("MSG");
           }else{
               screen_chk = "0";
               booking_msg = "Booking history";
           }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        if(screen_chk.equals("1")){

            booking_msg = intent.getStringExtra("MSG");

            tv_title.setText(getResources().getString(R.string.txt_booking_lbl));

            rl_home.setVisibility(View.GONE);
            rl_bookings.setVisibility(View.VISIBLE);
            rl_settings.setVisibility(View.GONE);
            rl_contact.setVisibility(View.GONE);

           /* iv_search_icon.setVisibility(View.GONE);
            iv_home.setImageResource(R.drawable.home_g);
            iv_booking.setImageResource(R.drawable.info_w);
            iv_setting.setImageResource(R.drawable.ic_menu_manage);
            iv_cnt.setImageResource(R.drawable.contact_g);*/

            if (Common.isConnectingToInternet(DashboardActivity.this)) {
                //Common.alert(DashboardActivity.this, booking_msg);
                get_booking_history_list_api();
            }
            else {
                Common.alert(DashboardActivity.this, getResources().getString(R.string.no_internet_txt));
            }
        }
        else if(screen_chk.equals("2")){
            tv_title.setText(getResources().getString(R.string.txt_settings_lbl));

            rl_home.setVisibility(View.GONE);
            rl_bookings.setVisibility(View.GONE);
            rl_settings.setVisibility(View.VISIBLE);
            rl_contact.setVisibility(View.GONE);

            rl_home_btn.setBackgroundColor(getResources().getColor(R.color.bg_txt_blue));
            rl_booking_btn.setBackgroundColor(getResources().getColor(R.color.bg_txt_blue));
            rl_settings_btn.setBackgroundColor(getResources().getColor(R.color.app_theme_color));
            rl_contact_btn.setBackgroundColor(getResources().getColor(R.color.bg_txt_blue));
        }
        else{
            getLocation();
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

                rl_home_btn.setBackgroundColor(getResources().getColor(R.color.app_theme_color));
                rl_booking_btn.setBackgroundColor(getResources().getColor(R.color.bg_txt_blue));
                rl_settings_btn.setBackgroundColor(getResources().getColor(R.color.bg_txt_blue));
                rl_contact_btn.setBackgroundColor(getResources().getColor(R.color.bg_txt_blue));
               // getLocation();
               // iv_search_icon.setVisibility(View.GONE);
               /* iv_home.setImageResource(R.drawable.home_w);
                iv_booking.setImageResource(R.drawable.info_g);
                iv_setting.setImageResource(R.drawable.ic_menu_manage);
                iv_cnt.setImageResource(R.drawable.contact_g);*/

                break;

            case R.id.rl_booking_btn:

                tv_title.setText(getResources().getString(R.string.txt_booking_history_lbl));

                rl_home.setVisibility(View.GONE);
                rl_bookings.setVisibility(View.VISIBLE);
                rl_settings.setVisibility(View.GONE);
                rl_contact.setVisibility(View.GONE);

                rl_home_btn.setBackgroundColor(getResources().getColor(R.color.bg_txt_blue));
                rl_booking_btn.setBackgroundColor(getResources().getColor(R.color.app_theme_color));
                rl_settings_btn.setBackgroundColor(getResources().getColor(R.color.bg_txt_blue));
                rl_contact_btn.setBackgroundColor(getResources().getColor(R.color.bg_txt_blue));

                /*iv_search_icon.setVisibility(View.GONE);
                iv_home.setImageResource(R.drawable.home_g);
                iv_booking.setImageResource(R.drawable.info_w);
                iv_setting.setImageResource(R.drawable.ic_menu_manage);
                iv_cnt.setImageResource(R.drawable.contact_g);
*/
                if (Common.isConnectingToInternet(DashboardActivity.this)) {

                    get_booking_history_list_api();
                }
                else {
                    Common.alert(DashboardActivity.this, getResources().getString(R.string.no_internet_txt));
                }

                break;

            case R.id.rl_settings_btn:

                tv_title.setText(getResources().getString(R.string.txt_settings_lbl));

                rl_home.setVisibility(View.GONE);
                rl_bookings.setVisibility(View.GONE);
                rl_settings.setVisibility(View.VISIBLE);
                rl_contact.setVisibility(View.GONE);

                rl_home_btn.setBackgroundColor(getResources().getColor(R.color.bg_txt_blue));
                rl_booking_btn.setBackgroundColor(getResources().getColor(R.color.bg_txt_blue));
                rl_settings_btn.setBackgroundColor(getResources().getColor(R.color.app_theme_color));
                rl_contact_btn.setBackgroundColor(getResources().getColor(R.color.bg_txt_blue));

                /*iv_search_icon.setVisibility(View.GONE);
                iv_home.setImageResource(R.drawable.home_g);
                iv_booking.setImageResource(R.drawable.info_g);
                iv_setting.setImageResource(R.drawable.ic_menu_manage);
                iv_cnt.setImageResource(R.drawable.contact_g);*/

                break;

            case R.id.rl_contact_btn:

                tv_title.setText(getResources().getString(R.string.txt_contact_lbl));

                rl_home.setVisibility(View.GONE);
                rl_bookings.setVisibility(View.GONE);
                rl_settings.setVisibility(View.GONE);
                rl_contact.setVisibility(View.VISIBLE);

                rl_home_btn.setBackgroundColor(getResources().getColor(R.color.bg_txt_blue));
                rl_booking_btn.setBackgroundColor(getResources().getColor(R.color.bg_txt_blue));
                rl_settings_btn.setBackgroundColor(getResources().getColor(R.color.bg_txt_blue));
                rl_contact_btn.setBackgroundColor(getResources().getColor(R.color.app_theme_color));

               /* iv_search_icon.setVisibility(View.GONE);
                iv_home.setImageResource(R.drawable.home_w);
                iv_booking.setImageResource(R.drawable.info_g);
                iv_setting.setImageResource(R.drawable.ic_menu_manage);
                iv_cnt.setImageResource(R.drawable.contact_g);*/

                /*startActivity(new Intent(DashboardActivity.this, Parking_Selection_Activity.class));
                finish();*/

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

            case R.id.tv_previous_tab:

                tv_previous_tab.setBackgroundColor(getResources().getColor(R.color.app_theme_color));
                tv_upcoming_tab.setBackgroundColor(getResources().getColor(R.color.bg_txt_blue));
                tv_cancelled_tab.setBackgroundColor(getResources().getColor(R.color.bg_txt_blue));

                lv_booking_list.setAdapter(new Booking_History_Adapter(DashboardActivity.this, arr_booking_history_map_List) {
                    @Override
                    protected void onShowPassBtnClick(View v, String position){
                       /* startActivity(new Intent(DashboardActivity.this, QR_Code_Activity.class));
                        finish();*/
                    }

                    @Override
                    protected void onCancelBtnClick(View v, String position) {

                    }
                });

                break;

            case R.id.tv_upcoming_tab:

                tv_previous_tab.setBackgroundColor(getResources().getColor(R.color.bg_txt_blue));
                tv_upcoming_tab.setBackgroundColor(getResources().getColor(R.color.app_theme_color));
                tv_cancelled_tab.setBackgroundColor(getResources().getColor(R.color.bg_txt_blue));

                lv_booking_list.setAdapter(new Booking_History_Adapter(DashboardActivity.this, arr_upcoming_booking_history_map_List) {
                    @Override
                    protected void onShowPassBtnClick(View v, String position){

                        // Call gate pass validation api

                        gatePassValidation_api(arr_upcoming_booking_history_map_List.get(Integer.parseInt(position)).get("bookingId"),
                                arr_upcoming_booking_history_map_List.get(Integer.parseInt(position)).get("zoneName"),
                                arr_upcoming_booking_history_map_List.get(Integer.parseInt(position)).get("fromTime"),
                                arr_upcoming_booking_history_map_List.get(Integer.parseInt(position)).get("toTime"),
                                arr_upcoming_booking_history_map_List.get(Integer.parseInt(position)).get("fromTime"),
                                arr_upcoming_booking_history_map_List.get(Integer.parseInt(position)).get("fromTime"),
                                arr_upcoming_booking_history_map_List.get(Integer.parseInt(position)).get("timezone"));
                    }

                    @Override
                    protected void onCancelBtnClick(View v, String position) {
                        cancel_booking_api(arr_upcoming_booking_history_map_List.get(Integer.parseInt(position)).get("bookingId"));
                    }
                });

                break;

            case R.id.tv_cancelled_tab:

                tv_previous_tab.setBackgroundColor(getResources().getColor(R.color.bg_txt_blue));
                tv_upcoming_tab.setBackgroundColor(getResources().getColor(R.color.bg_txt_blue));
                tv_cancelled_tab.setBackgroundColor(getResources().getColor(R.color.app_theme_color));

                lv_booking_list.setAdapter(new Booking_History_Adapter(DashboardActivity.this, arr_cancelled_booking_history_map_List) {
                    @Override
                    protected void onShowPassBtnClick(View v, String position){
                       /* startActivity(new Intent(DashboardActivity.this, QR_Code_Activity.class));
                        finish();*/
                    }

                    @Override
                    protected void onCancelBtnClick(View v, String position) {

                    }
                });


                break;

            case R.id.rl_settings_logout:

                if(tv_logout_lbl.getText().toString().trim().equals("Login")){

                    startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                    finish();

                }else{
                    logout_dialog();
                }



                break;

            default:

                break;
        }
    }

// ******* On Map Ready *******

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(latitude, longitude);
        // LatLng sydney = new LatLng(28.5392257, 77.3963628);
        Marker crn_loc = mMap.addMarker(new MarkerOptions().position(sydney).title("You are here.").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_zone_logo)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        crn_loc.showInfoWindow();
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
               // getLocation();
                // all permissions were granted

                break;
        }
    }

// ******* Get Location Method *******

    public void getLocation(){

        // check if GPS enabled
        GPSTracker gpsTracker = new GPSTracker(DashboardActivity.this);

        if (gpsTracker.canGetLocation()){

            latitude  = gpsTracker.getLatitude();//28.5393154;//
            longitude = gpsTracker.getLongitude();//77.3963628;//

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

// ******* Logout dialog *******

    public void logout_dialog(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashboardActivity.this);

        alertDialog.setTitle("Logout...");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want logout?");

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

        String url = getResources().getString(R.string.get_parking_zones_api) + "latitude=" + String.valueOf(latitude) + "&longitude=" + String.valueOf(longitude);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, getResources().getString(R.string.get_parking_zones_api) + "latitude=" + String.valueOf(latitude) + "&longitude=" + String.valueOf(longitude), null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                pDialog.cancel();

                try {
                    JSONObject header_Obj = response.getJSONObject("appHeader");
                    resCode = header_Obj.get("statusCode").toString();
                    resMsg  = header_Obj.get("statusMessage").toString();

                    if (resCode.equals("200")) {

                        arr_parking_zones_map_List.clear();

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

                        // Add multiple markers on zones.

                        for(int i = 0; i < arr_parking_zones_map_List.size(); i++){

                            LatLng point = null;
                            try {
                                point = new LatLng(Double.valueOf(arr_parking_zones_map_List.get(i).get("latitude")), Double.valueOf(arr_parking_zones_map_List.get(i).get("longitude")));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                            Marker marker = mMap.addMarker(new MarkerOptions().position(point).title(arr_parking_zones_map_List.get(i).get("zoneName")).icon(BitmapDescriptorFactory.fromResource(R.drawable.parking_loc)));
                            marker.setTag(i);
                            marker.showInfoWindow();
                          /*  mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                @Override
                                public View getInfoWindow(Marker marker) {
                                    return null;
                                }

                                @Override
                                public View getInfoContents(Marker marker) {
                                    return null;
                                }
                            });*/
                        }

                        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {

                                int pos = 0;
                                try {
                                    pos = (int)marker.getTag();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                pref.set(Constants.kZone_Id,   arr_parking_zones_map_List.get(pos).get("zoneid"));
                                pref.set(Constants.kZone_Name, arr_parking_zones_map_List.get(pos).get("zoneName"));
                                pref.set(Constants.kTimeZone,  arr_parking_zones_map_List.get(pos).get("timeZone"));
                                pref.commit();
                                startActivity(new Intent(DashboardActivity.this, Parking_Selection_Activity.class));
                                finish();
                            }
                        });

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
        Volley.newRequestQueue(DashboardActivity.this).add(jsObjRequest).setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

// ******* GET BOOKING HISTORY LIST API *******

    public void get_booking_history_list_api() {

        pDialog = new ProgressDialog(DashboardActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, getResources().getString(R.string.get_booking_history_api) + "customerId=" + pref.get(Constants.kcust_id), null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                pDialog.cancel();

                try {
                    JSONObject header_Obj = response.getJSONObject("appHeader");
                    resCode = header_Obj.get("statusCode").toString();
                    resMsg  = header_Obj.get("statusMessage").toString();

                    if (resCode.equals("200")) {

                        arr_booking_history_map_List.clear();
                        arr_upcoming_booking_history_map_List.clear();
                        arr_cancelled_booking_history_map_List.clear();
                    // GET PAST BOOKING API

                        JSONArray arr_past_booking_list_Json = response.getJSONArray("pastBookingHistoryList");

                        for (int i = 0; i < arr_past_booking_list_Json.length(); i++) {

                            JSONObject past_booking_list_Obj = arr_past_booking_list_Json.getJSONObject(i);

                            HashMap <String, String> past_booking_list_Map = new HashMap <String, String>();

                            past_booking_list_Map.put("bookingId",          past_booking_list_Obj.getString("bookingId"));
                            past_booking_list_Map.put("zoneName",           past_booking_list_Obj.getString("zoneName"));
                            past_booking_list_Map.put("status",             past_booking_list_Obj.getString("status"));
                            past_booking_list_Map.put("fromTime",           past_booking_list_Obj.getString("fromTime"));
                            past_booking_list_Map.put("toTime",             past_booking_list_Obj.getString("toTime"));
                            past_booking_list_Map.put("amount",             past_booking_list_Obj.getString("amount"));
                            past_booking_list_Map.put("qrcodeGeneratable",  past_booking_list_Obj.getString("qrcodeGeneratable"));
                            past_booking_list_Map.put("timezone",           past_booking_list_Obj.getString("timezone"));
                            past_booking_list_Map.put("cancellable",        past_booking_list_Obj.getString("cancellable"));

                            arr_booking_history_map_List.add(past_booking_list_Map);
                        }

                    // GET UPCOMING BOOKING API

                        JSONArray arr_upcoming_booking_list_Json = response.getJSONArray("upCommingBookingHistoryList");

                        for (int i = 0; i < arr_upcoming_booking_list_Json.length(); i++) {

                            JSONObject upcoming_booking_list_Obj = arr_upcoming_booking_list_Json.getJSONObject(i);

                            HashMap <String, String> upcoming_booking_list_Map = new HashMap <String, String>();

                            upcoming_booking_list_Map.put("bookingId",          upcoming_booking_list_Obj.getString("bookingId"));
                            upcoming_booking_list_Map.put("zoneName",           upcoming_booking_list_Obj.getString("zoneName"));
                            upcoming_booking_list_Map.put("status",             upcoming_booking_list_Obj.getString("status"));
                            upcoming_booking_list_Map.put("fromTime",           upcoming_booking_list_Obj.getString("fromTime"));
                            upcoming_booking_list_Map.put("toTime",             upcoming_booking_list_Obj.getString("toTime"));
                            upcoming_booking_list_Map.put("amount",             upcoming_booking_list_Obj.getString("amount"));
                            upcoming_booking_list_Map.put("qrcodeGeneratable",  upcoming_booking_list_Obj.getString("qrcodeGeneratable"));
                            upcoming_booking_list_Map.put("timezone",           upcoming_booking_list_Obj.getString("timezone"));
                            upcoming_booking_list_Map.put("cancellable",        upcoming_booking_list_Obj.getString("cancellable"));

                            arr_upcoming_booking_history_map_List.add(upcoming_booking_list_Map);
                        }

                        // GET CANCEL BOOKING API

                        JSONArray arr_cancel_booking_list_Json = response.getJSONArray("cancelBookingHistoryList");

                        for (int i = 0; i < arr_cancel_booking_list_Json.length(); i++) {

                            JSONObject cancel_booking_list_Obj = arr_cancel_booking_list_Json.getJSONObject(i);

                            HashMap <String, String> cancel_booking_list_Map = new HashMap <String, String>();

                            cancel_booking_list_Map.put("bookingId",          cancel_booking_list_Obj.getString("bookingId"));
                            cancel_booking_list_Map.put("zoneName",           cancel_booking_list_Obj.getString("zoneName"));
                            cancel_booking_list_Map.put("status",             cancel_booking_list_Obj.getString("status"));
                            cancel_booking_list_Map.put("fromTime",           cancel_booking_list_Obj.getString("fromTime"));
                            cancel_booking_list_Map.put("toTime",             cancel_booking_list_Obj.getString("toTime"));
                            cancel_booking_list_Map.put("amount",             cancel_booking_list_Obj.getString("amount"));
                            cancel_booking_list_Map.put("qrcodeGeneratable",  cancel_booking_list_Obj.getString("qrcodeGeneratable"));
                            cancel_booking_list_Map.put("timezone",           cancel_booking_list_Obj.getString("timezone"));
                            cancel_booking_list_Map.put("cancellable",        cancel_booking_list_Obj.getString("cancellable"));

                            arr_cancelled_booking_history_map_List.add(cancel_booking_list_Map);
                        }

                        // Bind Adapter for upcoming booking history

                        lv_booking_list.setAdapter(new Booking_History_Adapter(DashboardActivity.this, arr_upcoming_booking_history_map_List) {
                            @Override
                            protected void onShowPassBtnClick(View v, String position){

                                //final String booking_id, final String zname, final String s_time, final String e_time, final String vType, final String sType, final String tZone) {

                                    gatePassValidation_api(arr_upcoming_booking_history_map_List.get(Integer.parseInt(position)).get("bookingId"),
                                            arr_upcoming_booking_history_map_List.get(Integer.parseInt(position)).get("zoneName"),
                                            arr_upcoming_booking_history_map_List.get(Integer.parseInt(position)).get("fromTime"),
                                            arr_upcoming_booking_history_map_List.get(Integer.parseInt(position)).get("toTime"),
                                            arr_upcoming_booking_history_map_List.get(Integer.parseInt(position)).get("fromTime"),
                                            arr_upcoming_booking_history_map_List.get(Integer.parseInt(position)).get("fromTime"),
                                            arr_upcoming_booking_history_map_List.get(Integer.parseInt(position)).get("timezone"));
                            }

                            @Override
                            protected void onCancelBtnClick(View v, String position) {
                                cancel_booking_api(arr_upcoming_booking_history_map_List.get(Integer.parseInt(position)).get("bookingId"));
                            }
                        });

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
        Volley.newRequestQueue(DashboardActivity.this).add(jsObjRequest).setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

// ******* CANCEL BOOKING API *******

    public void cancel_booking_api(String booking_id) {

        pDialog = new ProgressDialog(DashboardActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.DELETE, getResources().getString(R.string.cancel_booking_api)  + booking_id +"/"+ pref.get(Constants.kcust_id), null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                pDialog.cancel();

                try {
                    JSONObject header_Obj = response.getJSONObject("appHeader");

                    resCode = header_Obj.get("statusCode").toString();
                    resMsg  = header_Obj.get("statusMessage").toString();

                    if (resCode.equals("200")) {

                        get_booking_history_list_api();

                    } else {
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
        Volley.newRequestQueue(DashboardActivity.this).add(jsObjRequest).setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

// ******* CHECK GATE PASS VALIDATION API *******

    public void gatePassValidation_api(final String booking_id, final String zname, final String s_time, final String e_time, final String vType, final String sType, final String tZone) {

        pDialog = new ProgressDialog(DashboardActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        Map<String, String> postParam = new HashMap <String, String>();

        postParam.put("customerId",  pref.get(Constants.kcust_id));
        postParam.put("bookingId",   booking_id);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, getResources().getString(R.string.gate_pass_validation_api), new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                pDialog.cancel();

                try {
                    JSONObject header_Obj = response.getJSONObject("appHeader");

                    resCode = header_Obj.get("statusCode").toString();
                    resMsg  = header_Obj.get("statusMessage").toString();

                    if (resCode.equals("200")) {
                        if(response.get("valid").toString().equals("true")){

                            // startActivity(new Intent(DashboardActivity.this, QR_Code_Activity.class).putExtra("b_id", booking_id).putExtra("cst_id",pref.get(Constants.kcust_id)).putExtra("st", response.get("fromTime").toString()).putExtra("et",response.get("toTime").toString()).putExtra("tz",response.get("timeZone").toString()));
                            startActivity(new Intent(DashboardActivity.this, QR_Code_Activity.class).putExtra("b_id", booking_id).putExtra("cst_id",pref.get(Constants.kcust_id)).putExtra("st", s_time).putExtra("et", e_time).putExtra("tz",tZone));
                            finish();
                        }
                        else{
                            pref.set(Constants.kParking_amount, response.get("price").toString());
                            pref.commit();
                            //String zName, String sTime, String eTime, String vType, String slotType, String price
                            booking_extended_dialog(zname, s_time, e_time, vType, sType, response.get("price").toString());
                        }
                    } else {

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
        Volley.newRequestQueue(DashboardActivity.this).add(jsObjRequest).setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

// ******* AUTO SEARCH PLACES GOOGLE MAP API *******

    public void auto_search_places_api(String auto_search_txt) {

        String key = "key=" + getString(R.string.google_maps_key);
        String types = "types=geocode";
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = auto_search_txt + "&" + types + "&" + sensor + "&" + key;
        String search_url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+ parameters;
        // https://maps.googleapis.com/maps/api/place/autocomplete/json?input=noida+&types=geocode&sensor=false&key=AIzaSyAYkRcA0wfHLgbsMKWivzl13OzAv5f-P_A

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, search_url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    arr_search_details_map_List.clear();

                    JSONArray arr_search_details_Json = response.getJSONArray("predictions");

                    for (int i = 0; i < arr_search_details_Json.length(); i++) {

                        JSONObject auto_search_Obj = arr_search_details_Json.getJSONObject(i);

                        HashMap<String, String> auto_search_Map = new HashMap<String, String>();

                        auto_search_Map.put("description",  auto_search_Obj.getString("description"));
                        auto_search_Map.put("id",           auto_search_Obj.getString("id"));
                        auto_search_Map.put("place_id",     auto_search_Obj.getString("place_id"));

                        arr_search_details_map_List.add(auto_search_Map);
                    }

                    if(!arr_search_details_map_List.isEmpty()){

                        lv_auto_search_list.setVisibility(View.VISIBLE);
                        autoSearchAdapter = new AutoSearchAdapter(DashboardActivity.this, arr_search_details_map_List) {
                            @Override
                            protected void onRowClick(View v, String position) {

                                //latitude  = Double.valueOf(arr_search_details_map_List.get(Integer.parseInt(position)).get("latitude"));
                               // longitude = Double.valueOf(arr_search_details_map_List.get(Integer.parseInt(position)).get("longitude"));
                                //edt_auto_search.setText(arr_search_details_map_List.get(Integer.parseInt(position)).get("description"));
                                lv_auto_search_list.setVisibility(View.GONE);

                                String place_id = arr_search_details_map_List.get(Integer.parseInt(position)).get("place_id");
                                Places.GeoDataApi.getPlaceById(mGoogleApiClient, place_id).setResultCallback(new ResultCallback<PlaceBuffer>() {
                                    @Override
                                    public void onResult(PlaceBuffer places) {
                                        if (places.getStatus().isSuccess()) {
                                            final Place myPlace = places.get(0);
                                            LatLng queriedLocation = myPlace.getLatLng();
                                           // Log.v("Latitude is", "" + queriedLocation.latitude);
                                           // Log.v("Longitude is", "" + queriedLocation.longitude);
                                            latitude  = queriedLocation.latitude;
                                            longitude = queriedLocation.longitude;
                                            get_parking_zones_list_api();
                                        }
                                        places.release();
                                    }
                                });





                            }
                        };
                        lv_auto_search_list.setAdapter(autoSearchAdapter);
                    }
                    else{
                        lv_auto_search_list.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

               // pDialog.cancel();
                error.printStackTrace();
            }
        });
        Volley.newRequestQueue(DashboardActivity.this).add(jsObjRequest);
    }

// ******* BOOKING EXTENDED DIALOG *******

    public void booking_extended_dialog(String zName, String sTime, String eTime, String vType, String slotType, String price){

        dialog = new Dialog(DashboardActivity.this);
        dialog.setContentView(R.layout.extended_booking_dialog);

        TextView tv_zone_name       = (TextView)dialog.findViewById(R.id.tv_zone_name);
        TextView tv_start_duration  = (TextView)dialog.findViewById(R.id.tv_start_duration);
        TextView tv_end_duration    = (TextView)dialog.findViewById(R.id.tv_end_duration);

        TextView tv_amount       = (TextView)dialog.findViewById(R.id.tv_amount);

        tv_zone_name.setText(zName);
        tv_start_duration.setText(sTime);
        tv_end_duration.setText(eTime);
        tv_amount.setText("$" + price);

        //TextView tv_vehicle_type = (TextView)dialog.findViewById(R.id.tv_vehicle_type);
        //TextView tv_slot_type    = (TextView)dialog.findViewById(R.id.tv_slot_type);
       // tv_vehicle_type.setText(vType);
       // tv_slot_type.setText(slotType);


        Button btn_pay = (Button)dialog.findViewById(R.id.btn_pay);
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if(pref.get(Constants.kcust_id).equals("")){
                    startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(DashboardActivity.this, Payment_Activity.class));
                    finish();
                }
            }
        });

        Button btn_cancel = (Button)dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }
























// ******* using Here map data start *******



// ******* Initialize Map *******

    /*private void initialize(final double latitude, final double longitude) {

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
    }*/

// ******* AUTO SEARCH PLACES HERE MAP API *******

/*    public void auto_search_places_api(String auto_search_txt) {

        pDialog = new ProgressDialog(DashboardActivity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

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
    }*/

// ******* SET ALL NEAREST PARKING ZONES WITH MARKERS *******

   /* public void set_markers_on_zones(){

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
    }*/

// ******* GESTURES LISTENER *******

    /*private class MyOnGestureListener implements MapGesture.OnGestureListener {

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
    }*/

// ******* using Here map data end *******
}
