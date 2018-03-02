package com.vivetuentrada.registerapp;

import android.content.Context;
import android.util.Log;

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

    public static String RegisterErrors (String code,Context context) {

        try {
            String error = null;
            switch (code) {
                case "shortcode":
                    error = context.getString(R.string.server_register_error_shortcode);
                    break;
                case "longcode":
                    error = context.getString(R.string.server_register_error_longcode);
                    break;
                case "emptyornull":
                    error = context.getString(R.string.server_auth_error_not_found);
                    break;
                case "badformat":
                    error = context.getString(R.string.server_register_error_badformat);
                    break;
                case "ticketbadformat":
                    error = context.getString(R.string.server_register_error_ticketbadformat);
                    break;

                case "format.dctransaction": {
                    error = context.getString(R.string.server_register_error_format_dctransaction);
                    break;
                }
                case "format.dccounter": {
                    error = context.getString(R.string.server_register_error_format_dccounter);
                    break;
                }
                case "format.dccheckdigit": {
                    error = context.getString(R.string.server_register_error_format_dccheckdigit);
                    break;
                }
                case "format.dcshow": {
                    error = context.getString(R.string.server_register_error_format_dcshow);
                    break;
                }
                default:
                    error = context.getString(R.string.server_register_error_unknown);
                    break;

            }
            return error;
        }catch (Exception exc){
            return context.getString(R.string.server_register_error_unknown);
        }
    }


    public static String RegisterMessages (String code, Context context ){
        String message = null;
        switch (code.toLowerCase()) {
            case "reject":{
                message = context.getString(R.string.server_register_messages_badinfo);
                break;
            }
            case "accept":{
                message = context.getString(R.string.server_register_messages_accept);
                break;
            }
            case "defeated":{
                message = context.getString(R.string.server_register_messages_defeated);
                break;
            }
            case "consumed":{
                message = context.getString(R.string.server_register_messages_consumed);
                break;
            }
            case "at":{
                message = context.getString(R.string.server_register_messages_at);
                break;
            }
        }

        return message;

    }


}
