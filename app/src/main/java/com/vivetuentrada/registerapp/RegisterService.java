package com.vivetuentrada.registerapp;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by usuario on 23/02/2018.
 */

public class RegisterService extends HttpBase {

    private String ROOT = "in";
    private SessionStorageService _session = SessionStorageService.getInstance(null);
    public RegisterService (){

        super();
        this.SERVICE_PREFIX = "register/";
        super.ROOT= this.ROOT;
    }


    public Response validateTicket (String codeBar) throws IOException {
        Request request = new Request.Builder()
                //.url(this.getUrlFor( codeBar))
                .url(this.getUrlFor( "00000001000002370019573"))
                .header("Authorization", "Bearer "+ _session.getAtribute(_session.ACCESS_TOKEN))
                .build();
        return http.newCall(request).execute();
    }




}
