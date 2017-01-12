package com.sparsh.smartparkingsystem.dashboard;

/**
 * Created by SS0C67653 on 26-12-2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sparsh.smartparkingsystem.R;

import java.util.ArrayList;
import java.util.HashMap;



public abstract class AutoSearchAdapter extends BaseAdapter {

// ******* DECLARING TEXT VIEW *******

    TextView tv_auto_search;

// ******* DECLARING CONTEXT *******

    Context context;

// ******* DECLARING ARRAY LIST *******

    ArrayList<HashMap<String, String>> data;

// ******* DECLARING CLICK LISTENER ******

    public View.OnClickListener onRowClickListener;



    public AutoSearchAdapter(Context context, ArrayList <HashMap <String, String>> placeData) {
        this.context = context;
        this.data = placeData;

        onRowClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                onRowClick(v, String.valueOf(v.getTag()));
            }
        };
    }


    @Override
    public View getView( int position, View convertView, ViewGroup parent) {

        View rootView = convertView;

        if (rootView == null) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rootView = inflater.inflate(R.layout.row_autosearch, null);
        }

        tv_auto_search = (TextView) rootView.findViewById(R.id.tv_auto_search);
        tv_auto_search.setText(data.get(position).get("title"));


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

    protected abstract void onRowClick(View v, String position);

}