package com.sparsh.smartparkingsystem.booking;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.sparsh.smartparkingsystem.R;
import com.sparsh.smartparkingsystem.common.Constants;
import com.sparsh.smartparkingsystem.common.Preferences;

public class Booking_Availability extends AppCompatActivity {

// ******* DECLARING LIST VIEWS *******

    ListView lv_availability_list;

// ******* Declaring Class Objects *******

    Preferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_booking_availability);

        pref = new Preferences(Booking_Availability.this);

        lv_availability_list = (ListView)findViewById(R.id.lv_availability_list);
        lv_availability_list.setAdapter(new Booking_Availability_Adapter(Booking_Availability.this, Parking_Selection_Activity.arr_levels_map_List) {
            @Override
            protected void onBookBtnClick(View v, String position) {

                pref.set(Constants.kParking_amount, Parking_Selection_Activity.arr_levels_map_List.get(Integer.parseInt(position)).get("price"));
                pref.commit();

                booking_cnf_dialog();
            }
        });
    }

// ******* CONFIRMATION DIALOG *******

    public void booking_cnf_dialog(){

        final Dialog dialog = new Dialog(Booking_Availability.this);
        dialog.setContentView(R.layout.confirmation_dialog);

        TextView tv_zone_name    = (TextView)dialog.findViewById(R.id.tv_zone_name);
        TextView tv_duration     = (TextView)dialog.findViewById(R.id.tv_duration);
        TextView tv_vehicle_type = (TextView)dialog.findViewById(R.id.tv_vehicle_type);
        TextView tv_slot_type    = (TextView)dialog.findViewById(R.id.tv_slot_type);
        TextView tv_amount       = (TextView)dialog.findViewById(R.id.tv_amount);

        tv_zone_name.setText(pref.get(Constants.kZone_Name));
        tv_duration.setText(pref.get(Constants.kDuration));
        tv_vehicle_type.setText(pref.get(Constants.kVehicleTypeName));
        tv_slot_type.setText(pref.get(Constants.kSlotTypeName));
        tv_amount.setText(pref.get(Constants.kParking_amount));

        Button btn_pay = (Button)dialog.findViewById(R.id.btn_pay);
        btn_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*startActivity(new Intent(Booking_Availability.this, MainActivity.class));
                finish();*/
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
}
