package com.sparsh.smartparkingsystem.registration;

/**
 * Created by SS0C67653 on 26-12-2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sparsh.smartparkingsystem.R;

import java.util.ArrayList;
import java.util.HashMap;


public abstract class CountriesListAdapter extends BaseAdapter {

// ******* DECLARING TEXT VIEW *******

    TextView tv_gv_country_name, tv_gv_country_isd_code;

// ******* DECLARING IMAGE VIEW *******

    ImageView iv_gv_country_flag_icon;

// ******* DECLARING CONTEXT *******

    Context context;

// ******* DECLARING ARRAY LIST *******

    ArrayList<HashMap<String, String>> data;

// ******* DECLARING CLICK LISTENER ******

    public View.OnClickListener onListItemClickListener;



    public CountriesListAdapter(Context context, ArrayList<HashMap<String, String>> placeData) {

        this.context = context;
        this.data    = placeData;

        onListItemClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onListItemClick(v, String.valueOf(v.getTag()));
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = convertView;

        if (rootView == null) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rootView = inflater.inflate(R.layout.gv_country_list_adapter, null);
        }

        iv_gv_country_flag_icon = (ImageView)rootView.findViewById(R.id.iv_gv_country_flag_icon);
        iv_gv_country_flag_icon.setImageResource(RegistrationActivity.getFlagResID(data.get(position).get("countryCode")));
        tv_gv_country_name     = (TextView) rootView.findViewById(R.id.tv_gv_country_name);
        tv_gv_country_isd_code = (TextView) rootView.findViewById(R.id.tv_gv_country_isd_code);

        tv_gv_country_name.setText(data.get(position).get("countryName")+" ("+ data.get(position).get("countryCode").toUpperCase()+")");
        tv_gv_country_isd_code.setText(data.get(position).get("countryISDCode"));

        rootView.setOnClickListener(onListItemClickListener);
        rootView.setTag(position);

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

    protected abstract void onListItemClick(View v, String position);
}