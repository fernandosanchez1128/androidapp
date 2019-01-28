package com.vivetuentrada.registerapp.com.vivetuentrada.registerapp.services;

import android.util.Base64;

import com.vivetuentrada.registerapp.com.vivetuentrada.registerapp.services.HttpBase;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by desarrollo on 1/03/18.
 */

public class AuthService extends HttpBase {


    private String ROOT = "oauth/token";
    private String CLIENT_ID = "vive_register_app";
    private String CLIENT_SECRET = "^9QW%riJ7[";
    private String AUTH_HEADER = "Authorization";
    private SessionStorageService _session = SessionStorageService.getInstance(null);

    public AuthService (){

        super();
        //this.SERVICE_PREFIX = "register/";
        super.ROOT= this.ROOT;
    }


    public Response login (String username, String password) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url(this.getUrlFor())
                .header(AUTH_HEADER,getEncodeBasicCredentials())
                .post(formBody)
                .build();
        return http.newCall(request).execute();
    }

    public Response logout () throws IOException {
        Request request = new Request.Builder()
                //.url(this.getUrlFor( codeBar))
                .delete()
                .url(this.getUrlFor( ))
                .header(AUTH_HEADER,getEncodeBasicCredentials())
                .build();
        return http.newCall(request).execute();
    }

    public String getEncodeBasicCredentials (){
        return "Basic "+  Base64.encodeToString(
                (CLIENT_ID+":"+CLIENT_SECRET).getBytes(), Base64.NO_WRAP);
    }

}
