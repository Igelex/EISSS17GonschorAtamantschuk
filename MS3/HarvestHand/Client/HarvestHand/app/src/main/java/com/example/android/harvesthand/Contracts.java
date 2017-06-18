package com.example.android.harvesthand;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import static com.example.android.harvesthand.R.id.container;

/**
 * Created by Pastuh on 13.06.2017.
 */

public class Contracts {
    public static String URL_PROTOCOL = "http://";
    public static String URL_PORT = ":3001/";
    public static String URL_IP = "";
    public static String URL_IP_BASE = "192.168..";
    public static String URL_BASE_ENTRIES = "entries/";
    public static String BASE_URL ;

    public static String URL_BASE_USERS = "users/";
    public static String URL_BASE_SIGNIN = "signin";
    public static String URL_BASE_SIGNUP = "signup";
    public static String URL_BASE_TUTORIAL = "/tutorial/";

    public static String URL_PARAMS_OWNER_ID = "owner_id";
    public static String URL_PARAMS_COLLAB_ID = "collab_id";

    public static String USER_SHARED_PREFS = "user";
    public static String USER_SP_ID = "user_id";
    public static String USER_SP_TYPE = "user_type";

    public static String IP_ADDRESS_SHARED_PREFS = "ipaddress";
    public static String IP_SP_IP = "ip";

    public Contracts() {
    }

    public void showSnackbar(View view, String msg, Boolean error, Boolean success){
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        TextView snackBarText = (snackbarView.findViewById(android.support.design.R.id.snackbar_text));
        if(error) {
            snackBarText.setTextColor(Color.rgb(253, 86, 86));
        }
        if(success) {
            snackBarText.setTextColor(Color.rgb(71, 151, 75));
        }
        snackbar.show();
    }
}
