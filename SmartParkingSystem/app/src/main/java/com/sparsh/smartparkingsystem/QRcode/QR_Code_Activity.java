package com.sparsh.smartparkingsystem.QRcode;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.sparsh.smartparkingsystem.R;
import com.sparsh.smartparkingsystem.common.Common;
import com.sparsh.smartparkingsystem.dashboard.DashboardActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class QR_Code_Activity extends AppCompatActivity {

// ******* Declaring variables *******

    String resMsg, resCode;

// ******* Declaring Image Views *******

    ImageView iv_qr_code, iv_back;

// ******* Declaring Buttons *******

    Button btn_entry, btn_exit;

// ******* Declaring Progress Bar *******

    ProgressDialog pDialog;

// ******* Declaring Intent *******

    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qr_code);

        iv_qr_code = (ImageView) findViewById(R.id.iv_qr_code);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(QR_Code_Activity.this, DashboardActivity.class));
                finish();
            }
        });

        try {
            intent = getIntent();
            if (intent.hasExtra("b_id")) {

                generate_qr_code(intent.getStringExtra("b_id"), intent.getStringExtra("cst_id"), intent.getStringExtra("st"), intent.getStringExtra("et"), intent.getStringExtra("tz"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        btn_entry = (Button) findViewById(R.id.btn_entry);
        btn_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entry_api(intent.getStringExtra("b_id"), intent.getStringExtra("cst_id"), intent.getStringExtra("st"), intent.getStringExtra("et"), intent.getStringExtra("tz"));
            }
        });


        btn_exit = (Button) findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit_api(intent.getStringExtra("b_id"), intent.getStringExtra("cst_id"), intent.getStringExtra("st"), intent.getStringExtra("et"), intent.getStringExtra("tz"));
            }
        });
    }

// ******* Generating QR Code *******

    public void generate_qr_code(String bookingId, String cust_id, String startTime, String endTime, String timeZone) {

        String qrInputText = bookingId + "," + cust_id + "," + startTime + "," + endTime + "," + timeZone;
        // Log.v(LOG_TAG, qrInputText);

        //Find screen size
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;

        //Encode with a QR Code image
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrInputText,
                null,
                Contents.Type.TEXT,
                BarcodeFormat.QR_CODE.toString(),
                smallerDimension);
        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            iv_qr_code.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

// ******* Entry Api *******

    public void entry_api(String bookingId, String cust_id, String startTime, String endTime, String timeZone) {

        pDialog = new ProgressDialog(QR_Code_Activity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        Map<String, String> postParam = new HashMap<String, String>();

        postParam.put("bookingId", bookingId);
        postParam.put("customerId", cust_id);
        postParam.put("fromTime", startTime);
        postParam.put("toTime", endTime);
        postParam.put("timeZone", timeZone);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, Common.getCompleteApiUrl(QR_Code_Activity.this, R.string.entry_api), new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                pDialog.cancel();

                try {
                    JSONObject header_Obj = response.getJSONObject("appHeader");

                    resCode = header_Obj.get("statusCode").toString();
                    resMsg = header_Obj.get("statusMessage").toString();

                    if (resCode.equals("200")) {

                        pDialog.cancel();
                        Common.alert(QR_Code_Activity.this, resMsg);

                    } else {
                        Common.alert(QR_Code_Activity.this, resMsg);
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
        //Volley.newRequestQueue(Booking_Availability.this).add(jsObjRequest);
        Volley.newRequestQueue(QR_Code_Activity.this).add(jsObjRequest).setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

// ******* Exit Api *******

    public void exit_api(String bookingId, String cust_id, String startTime, String endTime, String timeZone) {

        pDialog = new ProgressDialog(QR_Code_Activity.this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setCancelable(false);
        pDialog.show();

        Map<String, String> postParam = new HashMap<String, String>();

        postParam.put("bookingId", bookingId);
        postParam.put("customerId", cust_id);
        postParam.put("fromTime", startTime);
        postParam.put("toTime", endTime);
        postParam.put("timeZone", timeZone);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, Common.getCompleteApiUrl(QR_Code_Activity.this, R.string.exit_api), new JSONObject(postParam), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                pDialog.cancel();

                try {
                    JSONObject header_Obj = response.getJSONObject("appHeader");

                    resCode = header_Obj.get("statusCode").toString();
                    resMsg = header_Obj.get("statusMessage").toString();

                    if (resCode.equals("200")) {

                        pDialog.cancel();
                        Common.alert(QR_Code_Activity.this, resMsg);

                    } else {
                        Common.alert(QR_Code_Activity.this, resMsg);
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
        // Volley.newRequestQueue(Booking_Availability.this).add(jsObjRequest);
        Volley.newRequestQueue(QR_Code_Activity.this).add(jsObjRequest).setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
