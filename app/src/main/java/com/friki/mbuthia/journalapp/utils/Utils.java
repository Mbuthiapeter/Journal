/**
 * Created by Mbuthia Peter on 25/06/2018.
 * As part of completion of 7 days of code challenge
 * In pursuit of the Google ALC nano degree program
 */

package com.friki.mbuthia.journalapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;



public class Utils {
    private Context mContext = null;

    /**
     * Public constructor that takes mContext for later use
     */
    public Utils(Context con) {
        mContext = con;
    }

    /**
     * Encode user email to use it as a Firebase key
     * Encoded email is also used as "userEmail", list and item "owner" value
     */
    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    //A method to Check if the device  is connected to the internet
    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager

                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }
}
