package com.sparsh.smartparkingsystem.common;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;

import com.sparsh.smartparkingsystem.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Common {

    private static Dialog alert;

// ******* API COMPLETE URL *******

    public static String getCompleteApiUrl(Context ctx, int api) {

        return /*"http://" +*/ ctx.getString(R.string.server) + "/" + ctx.getString(api);
              /*  + ctx.getString(R.string.api_intermediary_path) + "/"*/

    }

// ******* CHECK EMAIL VALIDATION *******
	
	public static boolean isEmailValid(String email) {
	    
		boolean isValid = false;

	    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    CharSequence inputStr = email;

	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches()) {
	        isValid = true;
	    }
	    return isValid;
	}

// ******* GET MOBILE NUMBER *******

   /* public static String getMyPhoneNO(Context ctx) {

        TelephonyManager tMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

        String m1 = tMgr.getDeviceId();
        String m2 = tMgr.getNetworkOperator();
        String m3 = tMgr.getNetworkOperatorName();
        String mPhoneNumber = tMgr.getLine1Number();

        return mPhoneNumber;
    }*/

// ******* CHECK PASSWORD VALIDATION *******

    public static boolean validatePassword(String pswd) {
        //check that there are letters
        if(!pswd.matches("[a-zA-Z]+"))
            return false;  //nope no letters, stop checking and fail!
        //check if there are any numbers
        if(!pswd.matches("[0-9]+"))
            return false;  //nope no numbers, stop checking and fail!
        //check any valid special characters
        if(!pswd.matches("[.!#*()?,]+"))
            return false;  //nope no special chars, fail!

        //everything has passed so far, lets return valid
        return true;
    }

// ******* CHECK INTERNET CONNECTION *******

    public static boolean isConnectingToInternet(Context ctx){

        ConnectivityManager connectivity = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null){
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

// ******* COMMON ALERT DIALOG (3 PARAMETERS) *******

    public static void alert(Context ctx, int titleRes, String message) {

        alert = new Dialog(ctx);
        alert.setContentView(R.layout.common_alert_dialog_layout);

       //TextView tv_title = (TextView)alert.findViewById(R.id.tv_title);
       TextView tv_msg   = (TextView)alert.findViewById(R.id.tv_msg);
       tv_msg.setText(message);
       TextView tv_ok    = (TextView)alert.findViewById(R.id.tv_ok);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
            }
        });



        alert.show();

        /*alert = new AlertDialog.Builder(c).setIcon(R.drawable.icon_zone_logo).setTitle(titleRes).setMessage(message).setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                dialog.cancel();
            }
        }).create();// .setIcon(R.drawable.error)
        alert.setCanceledOnTouchOutside(true);
        alert.show();*/
    }

// ******* COMMON ALERT DIALOG (2 PARAMETERS) *******

    public static void alert(Context c, String message) {

        alert(c, R.string.app_name, message);
    }

// ******* CONVERT FORMAT in 12 hours  *******

    public static String change_in_am_pm_format(String sel_time){

        String f ="";

        // convert time in 24 hrs format
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
            SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = parseFormat.parse(sel_time);
            f = displayFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return f;
    }


    public static String convertToGMTTime(String time,String timezone){

        DateFormat sdfFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone timeZone = TimeZone.getTimeZone(timezone);
        sdfFormat.setTimeZone(timeZone);
        Date date=null;
        try {
            date = sdfFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat gmtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TimeZone gmtTime = TimeZone.getTimeZone("UTC");
        gmtFormat.setTimeZone(gmtTime);

        return gmtFormat.format(date);
    }


// ******* CONVERT STRING INTO DECIMAL UPTO 2 DIGITS DECIMAL  *******

    public static String convertToDecimal(String val){

        String convertedValue="";

        try {
            DecimalFormat df = new DecimalFormat("0.00");
            convertedValue = df.format(Double.parseDouble(val));
            df.setMaximumFractionDigits(2);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return convertedValue;
    }
}