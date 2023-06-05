package com.medicapp.medicappprojectcomp.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;

public class DatabaseRoutes {
    public final static String USERS_PATH = "users";
    public final static String MESSAGES_PATH = "messages";

    public static String getUser (String uuid){
        return String.format("%s/%s", USERS_PATH, uuid);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getMessage (String uuid, String uid){
        return String.format("%s/%s", MESSAGES_PATH+"/"+uid+"/"+ LocalDate.now(), uuid);
    }
}