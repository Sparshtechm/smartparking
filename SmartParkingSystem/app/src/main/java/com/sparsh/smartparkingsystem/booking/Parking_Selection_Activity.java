package com.sparsh.smartparkingsystem.booking;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.sparsh.smartparkingsystem.dashboard.DashboardActivity;
import com.sparsh.smartparkingsystem.dashboard.Test_demo_Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Parking_Selection_Activity extends AppCompatActivity implements View.OnClickListener {

// ******* Declaring Variables *******

    String final_start_time="", final_end_time="";
    String resMsg, resCode, current_date, current_time, search_start_time, search_end_time;

    String sel_hour = ""; String sel_min = "";

    Date crnt_date, sel_start_date, sel_end_date, crnt_time, sel_start_time, sel_end_time;

    String format;

// ******* Declaring Text Views *******

    TextView tv_zone_name_lbl; //, tv_start_date, tv_start_time, tv_end_date, tv_end_time;

    EditText edt_start_date, edt_start_time, edt_end_date, edt_end_time;

// ******* Declaring ImageView *******

    ImageView iv_back, iv_start_calendar_icon, iv_start_time_icon, iv_end_calendar_icon, iv_end_time_icon;

// ******* Declaring Spinner *******

    Spinner spnr_vehicle_type, spnr_slot_type;

// ******* Declaring Layouts *******

    //RelativeLayout rl_start_dt_left, rl_start_dt_right, rl_end_dt_left, rl_end_dt_right;

// ******* Declaring Button *******

    Button btn_chk_availability;

// ******* Declaring Alert Dialog *******

    Dialog dialog;

// ******* Declaring Progress Bar *******

    ProgressDialog pDialog;

// ******* Declaring Class Objects *******

    Preferences pref;

// ******* ANIMATION DECLARATION *******

    Animation anim_shake;

// ******* DECLARING ARRAY LIST MAP *******

    ArrayList <HashMap <String, String>> arr_vehicle_type_map_List  = new ArrayList <HashMap<String,String>>();
    ArrayList <HashMap <String, String>> arr_slot_type_map_List     = new ArrayList<HashMap<String, String>>();
    static ArrayList <HashMap <String, String>> arr_levels_map_List = new ArrayList <HashMap<String,String>>();

    List<String> vehicle_type_list = new ArrayList<String>();
    List<String> slot_type_list    = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parking_selection);

        pref = new Preferences(Parking_Selection_Activity.this);

    // ******* Animations *******

        anim_shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

    // ******* Back Button *******

        iv_back = (ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Parking_Selection_Activity.this, DashboardActivity.class));
                finish();
            }
        });

    // ******* TextViews *******

        tv_zone_name_lbl = (TextView)findViewById(R.id.tv_zone_name_lbl);
        tv_zone_name_lbl.setText(pref.get(Constants.kZone_Name));

    // ******* Edit TextViews *******

        edt_start_date    = (EditText)findViewById(R.id.edt_start_date);
        edt_start_time    = (EditText)findViewById(R.id.edt_start_time);
        edt_end_date      = (EditText)findViewById(R.id.edt_end_date);
        edt_end_time      = (EditText)findViewById(R.id.edt_end_time);

    // ******* ImageViews *******

        iv_start_calendar_icon = (ImageView)findViewById(R.id.iv_start_calendar_icon);
        iv_start_time_icon     = (ImageView)findViewById(R.id.iv_start_time_icon);
        iv_end_calendar_icon   = (ImageView)findViewById(R.id.iv_end_calendar_icon);
        iv_end_time_icon       = (ImageView)findViewById(R.id.iv_end_time_icon);

    // ******* Spinner *******

        spnr_vehicle_type = (Spinner)findViewById(R.id.spnr_vehicle_type);
        spnr_vehicle_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {

                pref.set(Constants.kVehicleTypeId,   arr_vehicle_type_map_List.get(position).get("id"));
                pref.set(Constants.kVehicleTypeName, arr_vehicle_type_map_List.get(position).get("type"));
                pref.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnr_slot_type    = (Spinner)findViewById(R.id.spnr_slot_type);
        spnr_slot_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {

                pref.set(Constants.kSlotTypeId,   arr_slot_type_map_List.get(position).get("id"));
                pref.set(Constants.kSlotTypeName, arr_slot_type_map_List.get(position).get("type"));
                pref.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    // ******* Button *******

        btn_chk_availability = (Button)findViewById(R.id.btn_chk_availability);

 /*   // ******* Declaring Layouts *******

        rl_start_dt_left  = (RelativeLayout)findViewById(R.id.rl_start_dt_left);
        rl_start_dt_right = (RelativeLayout)findViewById(R.id.rl_start_dt_right);
        rl_end_dt_left    = (RelativeLayout)findViewById(R.id.rl_end_dt_left);
        rl_end_dt_right   = (RelativeLayout)findViewById(R.id.rl_end_dt_right);*/

    // ******* Add Click Listeners *******

        iv_start_calendar_icon.setOnClickListener(this);
        iv_start_time_icon.setOnClickListener(this);
        iv_end_calendar_icon.setOnClickListener(this);
        iv_end_time_icon.setOnClickListener(this);
        btn_chk_availability.setOnClickListener(this);

        edt_start_date.setOnClickListener(this);
        edt_start_time.setOnClickListener(this);
        edt_end_date.setOnClickListener(this);
        edt_end_time.setOnClickListener(this);

    // ******* Get Current Date *******

        current_date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        edt_start_date.setText(current_date);
        edt_end_date.setText(current_date);

    // ******* Get Current Time *******

        current_time = new SimpleDateFormat("hh:mm a").format(new Date());
        edt_start_time.setText(current_time);
        edt_end_time.setText(current_time);

    // ******* Get vehicle and slot types *******

        if (Common.isConnectingToInternet(Parking_Selection_Activity.this)) {

            get_vehicle_slot_type_list_api();

        } else {
            Common.alert(Parking_Selection_Activity.this, getResources().getString(R.string.no_internet_txt));
        }
    }

// ******* ON CLICK METHOD *******
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.iv_start_calendar_icon:

                datePickerDialog(edt_start_date.getText().toString().trim(), "Select start date" ,1);
                break;

            case R.id.iv_start_time_icon:

                time_picker_dialog(1);
                break;

            case R.id.iv_end_calendar_icon:

                datePickerDialog(edt_end_date.getText().toString().trim(), "Select end date" ,2);
                break;

            case R.id.iv_end_time_icon:

                time_picker_dialog(2);
                break;

            case R.id.edt_start_date:

                datePickerDialog(edt_start_date.getText().toString().trim(), "Select start date" ,1);
                break;

            case R.id.edt_start_time:

                time_picker_dialog(1);
                break;

            case R.id.edt_end_date:

                datePickerDialog(edt_end_date.getText().toString().trim(), "Select end date" ,2);
                break;

            case R.id.edt_end_time:

                time_picker_dialog(2);
                break;

            case R.id.btn_chk_availability:

                try {
                    crnt_date      = new SimpleDateFormat("yyyy-MM-dd").parse(current_date);
                    sel_start_date = new SimpleDateFormat("yyyy-MM-dd").parse(edt_start_date.getText().toString().trim());
                    sel_end_date   = new SimpleDateFormat("yyyy-MM-dd").parse(edt_end_date.getText().toString().trim());

                    crnt_time      = new SimpleDateFormat("hh:mm a").parse(current_time);
                    sel_start_time = new SimpleDateFormat("hh:mm a").parse(edt_start_time.getText().toString().trim());
                    sel_end_time   = new SimpleDateFormat("hh:mm a").parse(edt_end_time.getText().toString().trim());
                }
                catch (ParseException e) {

                    e.printStackTrace();
                }

                if (Common.isConnectingToInternet(Parking_Selection_Activity.this)) {

                    if(Validate()) {

                        final_start_time = change_format(edt_start_time.getText().toString().trim());
                        final_end_time   = change_format(edt_end_time.getText().toString().trim());

                        pref.set(Constants.kStartDateTime,   edt_start_date.getText().toString().trim() + "%20" + final_start_time + ":00");
                        pref.set(Constants.kEndDateTime,     edt_end_date.getText().toString().trim()   + "%20" + final_end_time+ ":00");
                        pref.set(Constants.kStart_Duration,  edt_start_date.getText().toString().trim() + "    " + edt_start_time.getText().toString().trim());
                        pref.set(Constants.kEnd_Duration,    edt_end_date.getText().toString().trim()   + "    " + edt_end_time.getText().toString().trim());
                        pref.set(Constants.kStartDateTime1,  edt_start_date.getText().toString().trim() + " " + final_start_time + ":00");
                        pref.set(Constants.kEndDateTime1,    edt_end_date.getText().toString().trim()   + " " + final_end_time + ":00");

                        pref.commit();

                        get_availability_api(pref.get(Constants.kZone_Id), pref.get(Constants.kVehicleTypeId), pref.get(Constants.kSlotTypeId), edt_start_date.getText().toString().trim() + "%20" + final_start_time + ":00 ", edt_end_date.getText().toString().trim() + "%20" + final_end_time + ":00 ", pref.get(Constants.kTimeZone));

                       /* pref.set(Constants.kStartDateTime,  edt_start_date.getText().toString().trim() + "%20" + edt_start_time.getText().toString().trim().substring(0, edt_start_time.getText().toString().trim().length() - 3));
                        pref.set(Constants.kEndDateTime,    edt_end_date.getText().toString().trim()   + "%20" + edt_end_time.getText().toString().trim().substring(0, edt_end_time.getText().toString().trim().length() - 3));
                        pref.set(Constants.kDuration,       edt_start_date.getText().toString().trim() + " " + edt_start_time.getText().toString().trim() + " - " + edt_end_date.getText().toString().trim() + " " + edt_end_time.getText().toString().trim());
                        pref.set(Constants.kStartDateTime1, edt_start_date.getText().toString().trim() + " " + edt_start_time.getText().toString().trim().substring(0, edt_start_time.getText().toString().trim().length() - 3));
                        pref.set(Constants.kEndDateTime1,   edt_end_date.getText().toString().trim()   + " " + edt_end_time.getText().toString().trim().substring(0, edt_end_time.getText().toString().trim().length() - 3));

                        pref.commit();

                        get_availability_api(pref.get(Constants.kZone_Id), pref.get(Constants.kVehicleTypeId), pref.get(Constants.kSlotTypeId), edt_start_date.getText().toString().trim() + "%20" + edt_start_time.getText().toString().trim().substring(0, edt_start_time.getText().toString().trim().length() - 3) , edt_end_date.getText().toString().trim() + "%20" + edt_end_time.getText().toString().trim().substring(0, edt_end_time.getText().toString().trim().length() - 3), pref.get(Constants.kTimeZone));
                        */
                    }
                } else {
                    Common.alert(Parking_Selection_Activity.this, getResources().getString(R.string.no_internet_txt));
                }
                break;

            default:

                break;
        }
    }

// ******* DATE PICKER *******

    public void datePickerDialog(final String selected_date, String title, final int button_id ){

        final DatePicker datePicker = new DatePicker(getApplicationContext());

        new AlertDialog.Builder(Parking_Selection_Activity.this).setView(datePicker).setTitle(title).setPositiveButton(R.string.btn_set, new DialogInterface.OnClickListener() {{

            int fYear = 0,fMonth = 0,fDay = 0;

            // ******* GET DATE FROM DATEPICKER *******
            try {
                java.util.Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(selected_date);
                fYear = date1.getYear() + 1900;
                fMonth = date1.getMonth();
                fDay = date1.getDate();
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
            datePicker.init(fYear, fMonth, fDay, null);
        }
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Date date = new Date(datePicker.getYear() - 1900, datePicker.getMonth(), datePicker.getDayOfMonth());

             // ******* CHANGE THE DATE FORMAT *******

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date_selected = dateFormat.format(date);

                if(button_id == 1){
                    edt_start_date.setText(date_selected);
                    try {
                        sel_start_date = new SimpleDateFormat("yyyy-MM-dd").parse(edt_start_date.getText().toString().trim());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    edt_end_date.setText(date_selected);
                    try {
                        sel_end_date = new SimpleDateFormat("yyyy-MM-dd").parse(edt_end_date.getText().toString().trim());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        }).show();
    }

    public String change_format(String sel_time){

        String f ="";

        // convert time in 24 hrs format
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
            Date date = parseFormat.parse(sel_time);
            f = displayFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return f;

    }

// ******* TIME PICKER *******

     public void time_picker_dialog(final int val){

        dialog = new Dialog(Parking_Selection_Activity.this);
        dialog.setContentView(R.layout.time_picker_layout);

        TextView tv_set = (TextView)dialog.findViewById(R.id.tv_set);
        tv_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.cancel();

                if(val == 1){
                    edt_start_time.setText(sel_hour + ":" + sel_min /*+ ":00 "*/ + " " + format);
                    search_start_time = sel_hour + ":" + sel_min;// + ":00 ";

                }
                else{
                    edt_end_time.setText(sel_hour + ":" + sel_min /*+ ":00 "*/ + " " + format);
                    search_end_time   = sel_hour + ":" + sel_min; // + ":00 ";
                }
            }
        });

        TimePicker timePicker = (TimePicker)dialog.findViewById(R.id.timePicker);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                if (hourOfDay == 0) {

                    hourOfDay += 12;

                    format = "AM";
                }
                else if (hourOfDay == 12) {

                    format = "PM";
                }
                else if (hourOfDay > 12) {

                    hourOfDay -= 12;

                    format = "PM";
                }
                else {

                    format = "AM";
                }

                if(hourOfDay < 10){

                    sel_hour =  "0" + String.valueOf(hourOfDay);
                }
                else{
                    sel_hour =   String.valueOf(hourOfDay);
                }

                if(minute < 10){

                    sel_min =  "0" + String.valueOf(minute);
                }
                else{
                    sel_min =   String.valueOf(minute);
                }
/*
                if(val == 1){
                    edt_start_time.setText(sel_hour + ":" + sel_min + ":00 " + format);
                }
                else{

                    edt_end_time.setText(sel_hour + ":" + sel_min + ":00 " + format);
                }*/
            }
        });
        dialog.show();
    }

   /* public void time_picker_dialog(final int val){

        Calendar mcurrentTime = Calendar.getInstance();
        int hour   = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog(Parking_Selection_Activity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                String sel_hour = "";
                if(selectedHour < 10){

                    sel_hour =  "0" + String.valueOf(selectedHour);
                }
                else{
                    sel_hour =   String.valueOf(selectedHour);
                }


                String sel_min = "";
                if(selectedMinute < 10){

                    sel_min =  "0" + String.valueOf(selectedMinute);
                }
                else{
                    sel_min =   String.valueOf(selectedMinute);
                }


                if(val == 1){
                    edt_start_time.setText(sel_hour + ":" + sel_min + ":00");
                }
                else{

                    edt_end_time.setText(sel_hour + ":" + sel_min + ":00");
                }

                *//*if(val == 1){
                    edt_start_time.setText(selectedHour + ":" + selectedMinute + ":00");
                }
                else{

                    edt_end_time.setText(selectedHour + ":" + selectedMinute + ":00");
                }*//*

            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }*/

// ******* GET VEHICLE AND SLOT TYPES LIST API *******

    public void get_vehicle_slot_type_list_api() {

        pDialog = new ProgressDialog(Parking_Selection_Activity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, getResources().getString(R.string.get_vehicle_slot_type_api), null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                pDialog.cancel();

                try {
                    JSONObject header_Obj = response.getJSONObject("appHeader");
                    resCode = header_Obj.get("statusCode").toString();
                    resMsg  = header_Obj.get("statusMessage").toString();

                    if (resCode.equals("200")) {

                        // VEHICLE TYPE LIST

                        JSONArray arr_vehicle_type_list_Json = response.getJSONArray("vehicleType");

                        for (int i = 0; i < arr_vehicle_type_list_Json.length(); i++) {

                            JSONObject vehicle_type_list_Obj = arr_vehicle_type_list_Json.getJSONObject(i);

                            HashMap<String, String> vehicle_type_list_Map = new HashMap<String, String>();

                            vehicle_type_list_Map.put("id",   vehicle_type_list_Obj.getString("id"));
                            vehicle_type_list_Map.put("type", vehicle_type_list_Obj.getString("type"));

                            arr_vehicle_type_map_List.add(vehicle_type_list_Map);
                            vehicle_type_list.add(vehicle_type_list_Obj.getString("type"));
                        }

                        // SLOT TYPE LIST

                        JSONArray arr_slot_type_list_Json = response.getJSONArray("slotType");

                        for (int i = 0; i < arr_slot_type_list_Json.length(); i++) {

                            JSONObject slot_type_list_Obj = arr_slot_type_list_Json.getJSONObject(i);

                            HashMap<String, String> slot_type_list_Map = new HashMap<String, String>();

                            slot_type_list_Map.put("id",   slot_type_list_Obj.getString("id"));
                            slot_type_list_Map.put("type", slot_type_list_Obj.getString("type"));

                            arr_slot_type_map_List.add(slot_type_list_Map);
                            slot_type_list.add(slot_type_list_Obj.getString("type"));
                        }

                        ArrayAdapter<String> vehicle_type_adapter = new ArrayAdapter<String>(Parking_Selection_Activity.this, android.R.layout.simple_spinner_item, vehicle_type_list);
                        vehicle_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnr_vehicle_type.setAdapter(vehicle_type_adapter);

                        ArrayAdapter<String> slot_type_adapter = new ArrayAdapter<String>(Parking_Selection_Activity.this, android.R.layout.simple_spinner_item, slot_type_list);
                        slot_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spnr_slot_type.setAdapter(slot_type_adapter);
                    }

                    /*else if (resCode.equals("411")) {
                        Common.alert(Parking_Selection_Activity.this, resMsg);
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
        Volley.newRequestQueue(Parking_Selection_Activity.this).add(jsObjRequest).setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

// ******* CHECK AVAILABILITY API *******

    public void get_availability_api(String zone_id, String vehicle_type_id, String Slot_type_id, String start_time, String end_time, String timezone) {

        pDialog = new ProgressDialog(Parking_Selection_Activity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        String url = getResources().getString(R.string.get_availability_api) +
                "zoneId=" + zone_id + "&vehicleTypeId=" + vehicle_type_id + "&slotTypeId=" + Slot_type_id
                + "&toTime=" + end_time  + "&fromTime=" + start_time + "&timeZone=" + timezone;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, getResources().getString(R.string.get_availability_api) +
                "zoneId=" + zone_id + "&vehicleTypeId=" + vehicle_type_id + "&slotTypeId=" + Slot_type_id
                + "&toTime=" + end_time  + "&fromTime=" + start_time + "&timeZone=" + timezone, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                pDialog.cancel();

                try {
                    JSONObject header_Obj = response.getJSONObject("appHeader");
                    resCode = header_Obj.get("statusCode").toString();
                    resMsg  = header_Obj.get("statusMessage").toString();

                    if (resCode.equals("200")) {

                        arr_levels_map_List.clear();

                        // LEVEL LIST

                        JSONArray arr_level_list_Json = response.getJSONArray("levelList");

                        for (int i = 0; i < arr_level_list_Json.length(); i++) {

                            JSONObject level_list_Obj = arr_level_list_Json.getJSONObject(i);

                            HashMap <String, String> level_list_Map = new HashMap<String, String>();

                            //level_list_Map.put("levelId", level_list_Obj.getString("levelId"));

                            // VEHICLE LIST

                            JSONArray arr_vehicle_list_Json = level_list_Obj.getJSONArray("vehicle");

                            for (int j = 0; j < arr_vehicle_list_Json.length(); j++) {

                                JSONObject vehicle_list_Obj = arr_vehicle_list_Json.getJSONObject(j);

                                //level_list_Map.put("vehicleId",   vehicle_list_Obj.getString("vehicleId"));

                                // SLOT LIST

                                JSONArray arr_slot_list_Json = vehicle_list_Obj.getJSONArray("slotList");

                                for (int k = 0; k < arr_slot_list_Json.length(); k++) {

                                    JSONObject slot_list_Obj = arr_slot_list_Json.getJSONObject(k);

                                    level_list_Map.put("slotId",           slot_list_Obj.getString("slotId"));
                                    level_list_Map.put("overnightPrice",   slot_list_Obj.getString("overnightPrice"));
                                    level_list_Map.put("price",            slot_list_Obj.getString("price"));
                                    level_list_Map.put("longhourPrice",    slot_list_Obj.getString("longhourPrice"));
                                    level_list_Map.put("capacity",         slot_list_Obj.getString("capacity"));
                                    level_list_Map.put("capacityUsed",     slot_list_Obj.getString("capacityUsed"));
                                    level_list_Map.put("capacityRemaning", slot_list_Obj.getString("capacityRemaning"));
                                    level_list_Map.put("levelId",          level_list_Obj.getString("levelId"));
                                    level_list_Map.put("floorId",          level_list_Obj.getString("floor"));
                                    level_list_Map.put("vehicleId",        vehicle_list_Obj.getString("vehicleId"));

                                    arr_levels_map_List.add(level_list_Map);
                                }
                            }
                        }
                        startActivity(new Intent(Parking_Selection_Activity.this, Booking_Availability.class));
                        finish();
                    }
                    else if (resCode.equals("412")) {
                        Common.alert(Parking_Selection_Activity.this, resMsg);
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
        //Volley.newRequestQueue(Parking_Selection_Activity.this).add(jsObjRequest);
        Volley.newRequestQueue(Parking_Selection_Activity.this).add(jsObjRequest).setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

// ******* VALIDATE FIELDS *******

    public boolean Validate() {

        boolean status = true;

        if (edt_start_date.getText().toString().trim().equals("") && edt_start_time.getText().toString().trim().equals("")
                && edt_end_date.getText().toString().trim().equals("") && edt_end_time.getText().toString().trim().equals("")) {

            status = false;

            edt_start_date.startAnimation(anim_shake);
            edt_end_date.startAnimation(anim_shake);
            edt_start_time.startAnimation(anim_shake);
            edt_end_time.startAnimation(anim_shake);

            Common.alert(Parking_Selection_Activity.this, getString(R.string.txt_msg_all_fields));
        }
        else {

            if (edt_start_date.getText().toString().trim().equals("")) {
                status = false;
                edt_start_date.startAnimation(anim_shake);
                Common.alert(Parking_Selection_Activity.this, getString(R.string.blank_txt_start_date));
            }
            else if (edt_end_date.getText().toString().trim().equals("")) {
                status = false;
                edt_start_time.startAnimation(anim_shake);
                Common.alert(Parking_Selection_Activity.this, getString(R.string.blank_txt_end_date));
            }
            else if (edt_start_time.getText().toString().trim().equals("")) {
                status = false;
                edt_start_time.startAnimation(anim_shake);
                Common.alert(Parking_Selection_Activity.this, getString(R.string.blank_txt_start_time));
            }
            else if (edt_end_time.getText().toString().trim().equals("")) {
                status = false;
                edt_start_time.startAnimation(anim_shake);
                Common.alert(Parking_Selection_Activity.this, getString(R.string.blank_txt_end_time));
            }


// date validation

            else if (sel_start_date.before(crnt_date)) {
                status = false;
                edt_start_date.startAnimation(anim_shake);
                Common.alert(Parking_Selection_Activity.this, getString(R.string.txt_back_date));
            }

           /* else if (sel_end_date.before(sel_start_date)) {
                status = false;
                edt_end_date.startAnimation(anim_shake);
                Common.alert(Parking_Selection_Activity.this, getString(R.string.txt_back_date));
            }*/

            else if (sel_end_date.before(sel_start_date)) {
                status = false;
                edt_end_date.startAnimation(anim_shake);
                Common.alert(Parking_Selection_Activity.this, getString(R.string.txt_less_date));
            }

// time validation





            else if (sel_start_time.before(crnt_time)) {
                status = false;
                edt_start_time.startAnimation(anim_shake);
                Common.alert(Parking_Selection_Activity.this, getString(R.string.txt_back_time));
            }

         /*   else if (sel_end_time.before(crnt_time)) {
                status = false;
                edt_end_time.startAnimation(anim_shake);
                Common.alert(Parking_Selection_Activity.this, getString(R.string.txt_back_time));
            }*/

            else if (sel_end_time.before(sel_start_time)) {
                status = false;
                edt_end_time.startAnimation(anim_shake);
                Common.alert(Parking_Selection_Activity.this, getString(R.string.txt_less_time));
            }
            else if (sel_end_time.equals(sel_start_time)) {
                status = false;
                edt_end_time.startAnimation(anim_shake);
                Common.alert(Parking_Selection_Activity.this, getString(R.string.txt_less_time));
            }
        }
        return status;
    }

// ******* CALENDER DIALOG *******

   /* public void date_picker_dialog(final int val){

        // Variable for storing current date and time
        int mYear, mMonth, mDay;

        final Calendar c = Calendar.getInstance();
        mYear  = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay   = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Display Selected date in textbox
                        // edt_from_date.setText(dayOfMonth + "-"  + (monthOfYear + 1) + "-" + year);

                        c.set(year, monthOfYear, dayOfMonth);

                        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                     *//*   SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
                        String formattedDate = sdf.format(c.getTime());
*//*

                        String sel_day = "";
                        if(dayOfMonth<10){

                            sel_day =  "-0" + String.valueOf(dayOfMonth);
                        }else{
                            sel_day =  "-" + String.valueOf(dayOfMonth);
                        }

                        String sel_month = "";
                        if(monthOfYear + 1 < 10){

                            sel_month =  "-0" + String.valueOf((monthOfYear + 1));
                        }
                        else{
                            sel_month =  "-" + String.valueOf((monthOfYear + 1));
                        }


                        if(val == 1){
                            // edt_start_date.setText(dayOfMonth + "-"  + (monthOfYear + 1) + "-" + year);
                            edt_start_date.setText( year + sel_month + sel_day);
                        }
                        else{
                            // edt_end_date.setText(dayOfMonth + "-"  + (monthOfYear + 1) + "-" + year);
                            edt_end_date.setText( year + sel_month + sel_day);
                        }



                        *//*if(val == 1){
                           // edt_start_date.setText(dayOfMonth + "-"  + (monthOfYear + 1) + "-" + year);
                            edt_start_date.setText( year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        }
                        else{
                           // edt_end_date.setText(dayOfMonth + "-"  + (monthOfYear + 1) + "-" + year);
                            edt_end_date.setText( year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        }*//*
                    }
                }, mYear, mMonth, mDay);
        dpd.show();
    }*/


// ******* Time DIALOG *******



    /*public void timePickerDialog(final String selected_time, String title, final int button_id ){

        final DatePicker datePicker = new DatePicker(getApplicationContext());

        new AlertDialog.Builder(Parking_Selection_Activity.this).setView(datePicker).setTitle(title).setPositiveButton(R.string.btn_set, new DialogInterface.OnClickListener() {{

            int fHours = 0,fMin = 0,fSec = 0;

            // ******* GET DATE FROM DATEPICKER *******
            try {
                java.util.Date date1 = new SimpleDateFormat("hh:mm:ss a").parse(selected_time);
                fHours = date1.getHours();
                fMin   = date1.getMinutes();
                fSec   = date1.getSeconds();
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
            datePicker.init(fYear, fMonth, fDay, null);
        }
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Date date = new Date(datePicker.getYear() - 1900, datePicker.getMonth(), datePicker.getDayOfMonth());

                // ******* CHANGE THE DATE FORMAT *******

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String date_selected = dateFormat.format(date);

                if(button_id == 1){
                    edt_start_date.setText(date_selected);
                    try {
                        sel_start_date = new SimpleDateFormat("yyyy-MM-dd").parse(edt_start_date.getText().toString().trim());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    edt_end_date.setText(date_selected);
                    try {
                        sel_end_date = new SimpleDateFormat("yyyy-MM-dd").parse(edt_end_date.getText().toString().trim());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        }).show();
    }*/

}
