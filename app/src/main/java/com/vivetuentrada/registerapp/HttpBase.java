package com.vivetuentrada.registerapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import okhttp3.OkHttpClient;

/**
 * Created by usuario on 23/02/2018.
 */

public class HttpBase  extends Service{


    private  String GATEWAY_URL = "http://192.168.0.8:8080/";
    protected String SERVICE_PREFIX = "auth/";
    protected String ROOT = null;

    OkHttpClient http = new OkHttpClient();



    protected String getUrlFor (String endpoint) {
        if (SERVICE_PREFIX == null){
            return  GATEWAY_URL + ROOT +"/"+ endpoint;
        }else{
            return GATEWAY_URL + SERVICE_PREFIX+ ROOT + "/" + endpoint;
        }

    }

    protected String getUrlFor () {
        if (SERVICE_PREFIX == null){
            return  GATEWAY_URL + ROOT;
        }else{
            return GATEWAY_URL + SERVICE_PREFIX+ ROOT;
        }

    }



    protected void updateMicroservicePrefix (String prefix){
        this.SERVICE_PREFIX = prefix;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
