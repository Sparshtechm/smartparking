package com.sparsh.smartparkingsystem.dashboard;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sparsh.smartparkingsystem.R;
import com.sparsh.smartparkingsystem.booking.Booking_Availability;
import com.sparsh.smartparkingsystem.common.Constants;
import com.sparsh.smartparkingsystem.registration.LoginActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Test_demo_Activity extends AppCompatActivity {

    TextView tv_time_demo;
    Button btn_time_demo;
    private int CalendarHour, CalendarMinute;
    String format;
    Calendar calendar;
    TimePickerDialog timepickerdialog;

    DialogFragment dialogfragment;

    TimePickerDialog timepickerdialog1;

    int Chour,Cminute;

    // ******* Declaring Alert Dialog *******

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_demo);

        tv_time_demo = (TextView)findViewById(R.id.tv_time_demo);

        // ******* GETTING CURRENT DATE *******

       tv_time_demo.setText(new SimpleDateFormat("dd-MMM-yyyy").format(new Date()));

       /* calendar = Calendar.getInstance();
        Chour = calendar.get(Calendar.HOUR_OF_DAY);
        Cminute = calendar.get(Calendar.MINUTE);*/


        btn_time_demo = (Button)findViewById(R.id.btn_time_demo);
        btn_time_demo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //datePickerDialog(tv_time_demo.getText().toString(), 1);
               // timePicker();
                time_picker_dialog();
                //Creating object of TimePickerTheme4class using DialogFragment.
             /*   dialogfragment = new TimePickerTheme4class();

                dialogfragment.show(getFragmentManager(),"Time Picker with Theme 4");
*/

            }
        });
    }


// ******* DATE PICKER *******

    public void datePickerDialog(final String selected_date, final int button_id ){

        final DatePicker datePicker = new DatePicker(getApplicationContext());

        String title = "Start Date";

    /*    if(button_id == 1){
            title = getString(R.string.Pop_dtpick_Title_FromDate);
        }
        else{
            title = getString(R.string.Pop_dtpick_Title_ToDate);
        }*/
        new AlertDialog.Builder(Test_demo_Activity.this).setView(datePicker).setTitle(title).setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {{

            int fYear = 0,fMonth = 0,fDay = 0;

            // ******* GET DATE FROM DATEPICKER *******
            try {
                java.util.Date date3 = new SimpleDateFormat("dd-MMM-yyyy").parse(selected_date);
                fYear = date3.getYear() + 1900;
                fMonth = date3.getMonth();
                fDay = date3.getDate();
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

                DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                String date_selected = dateFormat.format(date);

                tv_time_demo.setText(date_selected);
               /* if(button_id == 1){
                    btn_From_Date.setText(date_selected);
                }
                else{
                    btn_To_Date.setText(date_selected);
                }*/
            }
        }).setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        }).show();
    }

// ******* Time PICKER *******

    public void timePicker(){

        calendar       = Calendar.getInstance();
        CalendarHour   = calendar.get(Calendar.HOUR_OF_DAY);
        CalendarMinute = calendar.get(Calendar.MINUTE);

        timepickerdialog = new TimePickerDialog(Test_demo_Activity.this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

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
                tv_time_demo.setText(hourOfDay + ":" + minute + format);
            }
        }, CalendarHour, CalendarMinute, false);
        timepickerdialog.show();

    }

// ******* CONFIRMATION DIALOG *******

    public void time_picker_dialog(){

        dialog = new Dialog(Test_demo_Activity.this);
        dialog.setContentView(R.layout.time_picker_layout);

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
                tv_time_demo.setText(hourOfDay + ":" + minute + ":00" + format);
            }
        });
       /* Button btn_pay = (Button)dialog.findViewById(R.id.btn_pay);
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
*/
        dialog.show();
    }



}
