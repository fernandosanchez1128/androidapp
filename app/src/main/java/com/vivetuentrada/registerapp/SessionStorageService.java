package com.vivetuentrada.registerapp;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

/**
 * Created by desarrollo on 2/03/18.
 */

public class SessionStorageService extends Service{

    private static volatile SessionStorageService sSessionStorageService;
    public final String IDENTIFICATION= "identification ";
    public final String ID = "id";
    public final String USER = "user";
    public final String EMAIL = "email";
    public final String ACCESS_TOKEN = "access_token";
    private Context context;

    private SessionStorageService(Context context) {
        if (sSessionStorageService != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        this.context = context;
    }

    public static SessionStorageService getInstance(Context context) {
        if (sSessionStorageService == null) { //if there is no instance available... create new one
            synchronized (SessionStorageService.class) {
                if (sSessionStorageService == null) sSessionStorageService = new SessionStorageService(context);
            }
        }

        return sSessionStorageService;
    }

    protected SessionStorageService readResolve(Context context) {
        return getInstance(context);
    }

    public String getAtribute (String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);


    }

    public void storeAuthData (UserAuth user){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ACCESS_TOKEN, user.getAccess_token());
        editor.commit();

    }

    public boolean isAuth (){
        return this.getAtribute(ACCESS_TOKEN) != null;
    }
    public void logout (){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
