package com.sparsh.smartparkingsystem.booking;

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

import java.util.ArrayList;
import java.util.HashMap;


public abstract class Booking_Availability_Adapter extends BaseAdapter {

// ******* DECLARING TEXT VIEW *******

    TextView tv_level_lbl, tv_capacity, tv_availability, tv_price, tv_over_delayed, tv_long_hours;

// ******* DECLARING BUTTON *******

    Button btn_book_now;

// ******* DECLARING CONTEXT *******

    Context context;

// ******* DECLARING ARRAY LIST *******

    ArrayList<HashMap<String, String>> data;

// ******* DECLARING CLICK LISTENER ******

    public View.OnClickListener onBookBtnClickListener;



    public Booking_Availability_Adapter(Context context, ArrayList <HashMap <String, String>> placeData) {
        this.context = context;
        this.data = placeData;

        onBookBtnClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onBookBtnClick(v, String.valueOf(v.getTag()));
            }
        };
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent) {

        View rootView = convertView;

        if (rootView == null) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rootView = inflater.inflate(R.layout.gv_booking_availability, null);
        }

        btn_book_now = (Button) rootView.findViewById(R.id.btn_book_now);
        btn_book_now.setOnClickListener(onBookBtnClickListener);
        btn_book_now.setTag(position);

        tv_level_lbl        = (TextView) rootView.findViewById(R.id.tv_level_lbl);
        tv_capacity     = (TextView) rootView.findViewById(R.id.tv_capacity);
        tv_availability     = (TextView) rootView.findViewById(R.id.tv_availability);
        tv_price            = (TextView) rootView.findViewById(R.id.tv_price);
        tv_over_delayed     = (TextView) rootView.findViewById(R.id.tv_over_delayed);
        tv_long_hours       = (TextView) rootView.findViewById(R.id.tv_long_hours);

        tv_level_lbl.setText("Level " + data.get(position).get("floorId"));
        tv_capacity.setText(data.get(position).get("capacity"));
        tv_availability.setText(data.get(position).get("capacityRemaning"));

        if(!data.get(position).get("capacityRemaning").equals("0")){
            //tv_availability.setText(data.get(position).get("capacityRemaning"));
            //tv_availability.setBackgroundColor(context.getResources().getColor(R.color.green));
            btn_book_now.setVisibility(View.VISIBLE);
        }else{
           // tv_availability.setText("Not Available ");
            //tv_availability.setBackgroundColor(context.getResources().getColor(R.color.red));
            btn_book_now.setVisibility(View.GONE);
        }

        tv_price.setText(data.get(position).get("price"));
        tv_over_delayed.setText(data.get(position).get("overnightPrice"));
        tv_long_hours.setText(data.get(position).get("longhourPrice"));


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

    protected abstract void onBookBtnClick(View v, String position);
}