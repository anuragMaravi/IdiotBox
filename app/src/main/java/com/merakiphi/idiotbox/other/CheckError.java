package com.merakiphi.idiotbox.other;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.VolleyError;

/**
 * Created by anuragmaravi on 05/02/17.
 */

public class CheckError {

    /**
     * Use this constructor to return the appropriate error to the user.
     * Converts the corresponding VolleyError to appropriate Toast.
     */
    public CheckError(Context context, VolleyError error, String name){
        switch(String.valueOf(error)){

            //Mention the name of the server
            case "com.android.volley.ServerError":
                Toast.makeText(context, "Unable to get " + name + " data.\nPlease Try Again Later.", Toast.LENGTH_LONG).show();
        }
    }
}
