package com.sparsh.smartparkingsystem.dashboard;

/**
 * Created by SS0C67653 on 26-12-2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.sparsh.smartparkingsystem.R;
import com.sparsh.smartparkingsystem.common.Common;

import java.util.ArrayList;
import java.util.HashMap;


public abstract class Booking_History_Adapter extends BaseAdapter {

// ******* DECLARING TEXT VIEW *******

    TextView tv_zone_name, tv_start_duration, tv_end_duration, tv_parking_amount, tv_booking_status;

    Button btn_pass, btn_cancel;

// ******* DECLARING CONTEXT *******

    Context context;

// ******* DECLARING ARRAY LIST *******

    ArrayList<HashMap<String, String>> data;

// ******* DECLARING CLICK LISTENER ******

    public View.OnClickListener onShowPassBtnClickListener;
    public View.OnClickListener onCancelBtnClickListener;


    public Booking_History_Adapter(Context context, ArrayList <HashMap <String, String>> placeData) {
        this.context = context;
        this.data = placeData;

        onShowPassBtnClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onShowPassBtnClick(v, String.valueOf(v.getTag()));
            }
        };

        onCancelBtnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelBtnClick(v, String.valueOf(v.getTag()));
            }
        };
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent) {

        View rootView = convertView;

        if (rootView == null) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rootView = inflater.inflate(R.layout.gv_booking_history, null);
        }

        tv_zone_name       = (TextView) rootView.findViewById(R.id.tv_zone_name);
        tv_start_duration  = (TextView) rootView.findViewById(R.id.tv_start_duration);
        tv_end_duration    = (TextView) rootView.findViewById(R.id.tv_end_duration);
        tv_parking_amount  = (TextView) rootView.findViewById(R.id.tv_parking_amount);
        tv_booking_status  = (TextView) rootView.findViewById(R.id.tv_booking_status);

        tv_zone_name.setText(data.get(position).get("zoneName"));
        tv_start_duration.setText(Common.change_in_am_pm_format(data.get(position).get("fromTime")));
        tv_end_duration.setText(Common.change_in_am_pm_format(data.get(position).get("toTime")));
        tv_parking_amount.setText("$" + data.get(position).get("amount"));
        tv_booking_status.setText(data.get(position).get("status"));

        btn_pass = (Button)rootView.findViewById(R.id.btn_pass);
        btn_pass.setTag(position);
        btn_pass.setOnClickListener(onShowPassBtnClickListener);

        if(data.get(position).get("qrcodeGeneratable").equals("false")){
            btn_pass.setVisibility(View.GONE);
        }else{
            btn_pass.setVisibility(View.VISIBLE);
        }

        btn_cancel = (Button)rootView.findViewById(R.id.btn_cancel);
        btn_cancel.setTag(position);
        btn_cancel.setOnClickListener(onCancelBtnClickListener);

        if(data.get(position).get("cancellable").equals("false")){
            btn_cancel.setVisibility(View.GONE);
        }else{
            btn_cancel.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    @Override
    public int getCount() {

        return data.size();
    }

    @Override
    public Object getItem(int position) {

        return data.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    protected abstract void onShowPassBtnClick(View v, String position);
    protected abstract void onCancelBtnClick(View v, String position);
}