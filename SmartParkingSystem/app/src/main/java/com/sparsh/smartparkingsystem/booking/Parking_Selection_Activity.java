package com.sparsh.smartparkingsystem.booking;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class Parking_Selection_Activity extends AppCompatActivity implements View.OnClickListener {

// ******* Declaring Variables *******

    String resMsg, resCode;

// ******* Declaring Text Views *******

    TextView tv_zone_name_lbl, tv_start_date, tv_start_time, tv_end_date, tv_end_time;

// ******* Declaring ImageView *******

    ImageView iv_back, iv_start_calendar_icon, iv_start_time_icon, iv_end_calendar_icon, iv_end_time_icon;

// ******* Declaring Spinner *******

    Spinner spnr_vehicle_type, spnr_slot_type;

// ******* Declaring Button *******

    Button btn_chk_availability;

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
        tv_start_date    = (TextView)findViewById(R.id.tv_start_date);
        tv_start_time    = (TextView)findViewById(R.id.tv_start_time);
        tv_end_date      = (TextView)findViewById(R.id.tv_end_date);
        tv_end_time      = (TextView)findViewById(R.id.tv_end_time);

        tv_zone_name_lbl.setText(pref.get(Constants.kZone_Name));

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

    // ******* Add Click Listeners *******

        iv_start_calendar_icon.setOnClickListener(this);
        iv_start_time_icon.setOnClickListener(this);
        iv_end_calendar_icon.setOnClickListener(this);
        iv_end_time_icon.setOnClickListener(this);
        btn_chk_availability.setOnClickListener(this);

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

                date_picker_dialog(1);
                break;

            case R.id.iv_start_time_icon:

                time_picker_dialog(1);
                break;

            case R.id.iv_end_calendar_icon:

                date_picker_dialog(2);
                break;

            case R.id.iv_end_time_icon:

                time_picker_dialog(2);
                break;

            case R.id.btn_chk_availability:

                if (Common.isConnectingToInternet(Parking_Selection_Activity.this)) {

                    //get_availability_api("1", "1", "1", "2017-01-05" + "15:00:00", "2017-01-05" + "15:00:00");
                    get_availability_api(pref.get(Constants.kZone_Id), pref.get(Constants.kVehicleTypeId), pref.get(Constants.kSlotTypeId), tv_start_date.getText().toString().trim() + tv_start_time.getText().toString().trim(), tv_end_date.getText().toString().trim() + tv_end_time.getText().toString().trim());

                } else {
                    Common.alert(Parking_Selection_Activity.this, getResources().getString(R.string.no_internet_txt));
                }

                break;

            default:

                break;
        }
    }

// ******* CALENDER DIALOG *******

    public void date_picker_dialog(final int val){

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
                     /*   SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
                        String formattedDate = sdf.format(c.getTime());
*/
                        if(val == 1){
                           // tv_start_date.setText(dayOfMonth + "-"  + (monthOfYear + 1) + "-" + year);
                            tv_start_date.setText( year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        }
                        else{
                           // tv_end_date.setText(dayOfMonth + "-"  + (monthOfYear + 1) + "-" + year);
                            tv_end_date.setText( year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        }
                    }
                }, mYear, mMonth, mDay);
        dpd.show();
    }

// ******* Time DIALOG *******

    public void time_picker_dialog(final int val){

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog(Parking_Selection_Activity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                if(val == 1){
                    tv_start_time.setText(selectedHour + ":" + selectedMinute + ":00");
                }
                else{

                    tv_end_time.setText(selectedHour + ":" + selectedMinute + ":00");
                }

            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();










       /* // Variable for storing current date and time
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
                        if(val == 1){
                            // tv_start_date.setText(dayOfMonth + "-"  + (monthOfYear + 1) + "-" + year);
                            tv_start_date.setText( year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        }
                        else{
                            // tv_end_date.setText(dayOfMonth + "-"  + (monthOfYear + 1) + "-" + year);
                            tv_end_date.setText( year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        }
                    }
                }, mYear, mMonth, mDay);
        dpd.show();*/
    }

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
        Volley.newRequestQueue(Parking_Selection_Activity.this).add(jsObjRequest);
    }

// ******* CHECK AVAILABILITY API *******

    public void get_availability_api(String zone_id, String vehicle_type_id, String Slot_type_id, String start_time, String end_time) {

        pDialog = new ProgressDialog(Parking_Selection_Activity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, getResources().getString(R.string.get_availability_api) +
                "zoneId=" + zone_id + "&vehicleTypeId=" + vehicle_type_id + "&slotTypeId=" + Slot_type_id
                + "&toTime=" + start_time + "&fromTime=" + end_time, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                pDialog.cancel();

                try {
                    JSONObject header_Obj = response.getJSONObject("appHeader");
                    resCode = header_Obj.get("statusCode").toString();
                    resMsg  = header_Obj.get("statusMessage").toString();

                    if (resCode.equals("200")) {

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
                                    level_list_Map.put("vehicleId",        vehicle_list_Obj.getString("vehicleId"));

                                    arr_levels_map_List.add(level_list_Map);
                                }
                            }
                        }
                    }

                    startActivity(new Intent(Parking_Selection_Activity.this, Booking_Availability.class));
                    finish();
                    /*if(!arr_levels_map_List.isEmpty()){
                        startActivity(new Intent(Parking_Selection_Activity.this, Parking_Selection_Activity.class));
                        finish();
                    }else{
                        Common.alert(Parking_Selection_Activity.this, "");
                    }*/

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
        Volley.newRequestQueue(Parking_Selection_Activity.this).add(jsObjRequest);
    }
}
