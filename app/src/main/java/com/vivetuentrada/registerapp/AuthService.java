package com.vivetuentrada.registerapp;

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
    public AuthService (){

        super();
        //this.SERVICE_PREFIX = "register/";
        super.ROOT= this.ROOT;
    }


    public Response login (String username, String password) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("client_id", CLIENT_ID)
                .add("client_secret", CLIENT_SECRET)
                .build();
        Request request = new Request.Builder()
                .url(this.getUrlFor())
                .post(formBody)
                .build();
        return http.newCall(request).execute();
    }

}
