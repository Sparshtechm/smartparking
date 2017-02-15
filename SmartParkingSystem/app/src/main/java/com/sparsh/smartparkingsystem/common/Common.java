package com.sparsh.smartparkingsystem.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sparsh.smartparkingsystem.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Common {

    private static Dialog alert;

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

    public static void alert(Context c, int titleRes, String message) {

        alert = new AlertDialog.Builder(c)/*setIcon(R.drawable.app_icon)*/.setTitle(titleRes).setMessage(message).setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                dialog.cancel();
            }
        }).create();// .setIcon(R.drawable.error)
        alert.setCanceledOnTouchOutside(true);
        alert.show();
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

}