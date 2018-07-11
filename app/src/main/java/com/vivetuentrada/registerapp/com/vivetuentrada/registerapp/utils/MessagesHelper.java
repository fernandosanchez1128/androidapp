package com.vivetuentrada.registerapp.com.vivetuentrada.registerapp.utils;

import android.content.Context;
import android.util.Log;
import java.util.Map;

import com.vivetuentrada.registerapp.R;

/**
 * Created by desarrollo on 2/03/18.
 */

public class MessagesHelper {

    public static String  HttpErrorsMessage (int code, Context context){
        String error = null;
        switch (code) {
            case 400 :
                error = context.getString(R.string.server_auth_error_bad_password);
                break;
            case 401 :
                error = context.getString(R.string.server_auth_error_bad_user);
                break;
            case 404 :
                error = context.getString(R.string.server_auth_error_not_found);
                break;
            case 500 :
                error = context.getString(R.string.server_error);
                break;
            case 512 :
                error = context.getString(R.string.server_auth_error_connect);

        }
        return error;
    }

    public static String registerErrors(String code, Context context){
        String error = context.getString(R.string.server_register_error_unknown);
        switch (code) {
            case "badformat":
                error = context.getString(R.string.server_register_error_badformat);
                break;
            case "ticketbadformat":
                error = context.getString(R.string.server_register_error_ticketbadformat);
                break;
        }
        return error;
    }

    public static String registerMessages (String msg, Map params, Context context){
        Log.e("message",msg);
        String start,end,expl,lot;
        String msg_trans = context.getString(R.string.server_register_error_unknown);;
        switch (msg){
            case "modules.entranceregister.messages.entrance.badinfo" :
                msg_trans = context.getString(R.string.server_register_messages_badinfo);
                break;

            case "modules.entranceregister.messages.entrance.accept" :
                msg_trans = context.getString(R.string.server_register_messages_accept);
                break;

            case "modules.entranceregister.messages.entrance.defeated" :
                start = (String) params.get("start");
                end   = (String) params.get("end");
                msg_trans = context.getString(R.string.server_register_messages_defeated, start,end);
                break;

            case "modules.entranceregister.messages.entrance.consumed" :
                expl = (String) params.get("expl");
                msg_trans = context.getString(R.string.server_register_messages_consumed,expl);
                break;

            case "modules.entranceregister.messages.entrance.at" :
                start = (String) params.get("start");
                end   = (String) params.get("end");
                msg_trans = context.getString(R.string.server_register_messages_at,start,end);
                break;

            case "modules.entranceregister.messages.entrance.nopayed" :
                lot = (String) params.get("lot");
                msg_trans = context.getString(R.string.server_register_messages_nopayed,lot);
                break;
        }
        return msg_trans;


    }

    public static String msg_translate (String msg, Context context ){
        String msg_trans = null;
        int value = context.getResources().getIdentifier("server_auth_error_connect", "string",context.getPackageName());
        int value2 = R.string.server_auth_error_connect;
        Log.e("v1",Integer.toString(value));
        Log.e("v2",Integer.toString(value2));
        Log.e("package", context.getPackageName());
        return "hola";

    }


}
