package com.sparsh.smartparkingsystem.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.sparsh.smartparkingsystem.R;

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

        alert = new AlertDialog.Builder(c)/*setIcon(R.drawable.app_icon)*/.setTitle(titleRes).setMessage(message).create();// .setIcon(R.drawable.error)
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

// ******* COMMON ALERT DIALOG (2 PARAMETERS) *******

    public static void alert(Context c, String message) {

        alert(c, R.string.app_name, message);
    }

}