package com.sparsh.smartparkingsystem.booking;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sparsh.smartparkingsystem.R;
import com.sparsh.smartparkingsystem.common.Common;
import com.sparsh.smartparkingsystem.common.Constants;
import com.sparsh.smartparkingsystem.common.Preferences;
import com.sparsh.smartparkingsystem.payment.Payment_Activity;
import com.sparsh.smartparkingsystem.registration.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Booking_Availability extends AppCompatActivity {

// ******* Declaring variables *******

    String resMsg, resCode;

    ImageView iv_back;

    TextView tv_zone_name;

// ******* DECLARING LIST VIEWS *******

    ListView lv_availability_list;

// ******* Declaring Class Objects *******

    Preferences pref;

// ******* Declaring Progress Bar *******

    ProgressDialog pDialog;

// ******* Declaring Alert Dialog *******

    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_booking_availability);

        pref = new Preferences(Booking_Availability.this);

        iv_back = (ImageView)findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Booking_Availability.this, Parking_Selection_Activity.class));
                finish();

            }
        });

        tv_zone_name = (TextView)findViewById(R.id.tv_zone_name);
        tv_zone_name.setText(pref.get(Constants.kZone_Name));

        lv_availability_list = (ListView)findViewById(R.id.lv_availability_list);
        lv_availability_list.setAdapter(new Booking_Availability_Adapter(Booking_Availability.this, Parking_Selection_Activity.arr_levels_map_List) {
            @Override
            protected void onBookBtnClick(View v, String position) {

                pref.set(Constants.kLevel_Id, Parking_Selection_Activity.arr_levels_map_List.get(Integer.parseInt(position)).get("levelId"));
                pref.commit();

                cal_parking_cost_api(pref.get(Constants.kLevel_Id), pref.get(Constants.kVehicleTypeId), pref.get(Constants.kSlotTypeId), pref.get(Constants.kStartDateTime), pref.get(Constants.kEndDateTime));

            }
        });
    }

// ******* CONFIRMATION DIALOG *******

    public void booking_cnf_dialog(String price){

        dialog = new Dialog(Booking_Availability.this);
        dialog.setContentView(R.layout.confirmation_dialog);

        TextView tv_zone_name      = (TextView)dialog.findViewById(R.id.tv_zone_name);
        TextView tv_start_duration = (TextView)dialog.findViewById(R.id.tv_start_duration);
        TextView tv_end_duration   = (TextView)dialog.findViewById(R.id.tv_end_duration);
        TextView tv_vehicle_type   = (TextView)dialog.findViewById(R.id.tv_vehicle_type);
        TextView tv_slot_type      = (TextView)dialog.findViewById(R.id.tv_slot_type);
        TextView tv_amount         = (TextView)dialog.findViewById(R.id.tv_amount);

        tv_zone_name.setText(pref.get(Constants.kZone_Name));
        tv_start_duration.setText(pref.get(Constants.kStart_Duration));
        tv_end_duration.setText(pref.get(Constants.kEnd_Duration));
        tv_vehicle_type.setText(pref.get(Constants.kVehicleTypeName));
        tv_slot_type.setText(pref.get(Constants.kSlotTypeName));
        tv_amount.setText("$" + price);

        Button btn_pay = (Button)dialog.findViewById(R.id.btn_pay);
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pref.get(Constants.kcust_id).equals("")){
                    startActivity(new Intent(Booking_Availability.this, LoginActivity.class));
                    finish();
                }else {
                    slot_blocking_api();
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

// ******* CALCULATE PARKING COSTING API *******

    public void cal_parking_cost_api(String level_id, String vehicle_type_id, String Slot_type_id, String start_time, String end_time) {

        pDialog = new ProgressDialog(Booking_Availability.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();


        String url = getResources().getString(R.string.get_parking_cost_api) +
                "levelId=" + level_id + "&vehicleTypeId=" + vehicle_type_id + "&slotTypeId=" + Slot_type_id
                + "&toTime=" + end_time + "&fromTime=" + start_time;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, getResources().getString(R.string.get_parking_cost_api) +
                "levelId=" + level_id + "&vehicleTypeId=" + vehicle_type_id + "&slotTypeId=" + Slot_type_id
                + "&toTime=" + end_time + "&fromTime=" + start_time , null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                pDialog.cancel();

                try {
                    JSONObject header_Obj = response.getJSONObject("appHeader");
                    resCode = header_Obj.get("statusCode").toString();
                    resMsg  = header_Obj.get("statusMessage").toString();

                    if (resCode.equals("200")) {

                        pref.set(Constants.kParking_amount,response.get("price").toString());
                        pref.commit();
                        booking_cnf_dialog(response.get("price").toString());
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
        Volley.newRequestQueue(Booking_Availability.this).add(jsObjRequest);
    }

// ******* CALL SLOT BLOCKING API *******

    public void slot_blocking_api() {

        pDialog = new ProgressDialog(Booking_Availability.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        Map <String, String> postParam = new HashMap <String, String>();

        postParam.put("customerId",     pref.get(Constants.kcust_id));
        postParam.put("fromTime",       pref.get(Constants.kStartDateTime1));
        postParam.put("levelId",        pref.get(Constants.kLevel_Id));
        postParam.put("slotTypeId",     pref.get(Constants.kSlotTypeId));
        postParam.put("timeZone",       pref.get(Constants.kTimeZone));
        postParam.put("toTime",         pref.get(Constants.kEndDateTime1));
        postParam.put("vehicleTypeId",  pref.get(Constants.kVehicleTypeId));

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, getResources().getString(R.string.parking_slot_blocking_api), new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                pDialog.cancel();

                try {
                    JSONObject header_Obj = response.getJSONObject("appHeader");

                    resCode = header_Obj.get("statusCode").toString();
                    resMsg  = header_Obj.get("statusMessage").toString();

                    if (resCode.equals("200")) {

                        String BookingId = response.get("bookingId").toString();
                        pref.set(Constants.kBooking_Id, BookingId);
                        pref.commit();
                        dialog.cancel();

                        startActivity(new Intent(Booking_Availability.this, Payment_Activity.class));
                        finish();

                    } else {

                        Common.alert(Booking_Availability.this, resMsg);
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
        Volley.newRequestQueue(Booking_Availability.this).add(jsObjRequest);
    }
}
