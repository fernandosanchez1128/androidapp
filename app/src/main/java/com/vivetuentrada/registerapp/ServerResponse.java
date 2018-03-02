package com.vivetuentrada.registerapp;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by usuario on 23/02/2018.
 */

public class ServerResponse <T> {

    private int status ;
    private boolean success;
    private T data;
    private String message;
    private Map <String,Map> errors;
    private List<Error> listErrors = new ArrayList<Error>();

    class Error {
        private String title;
        private  String code;
        private  String message;

        public Error (String code, String title, String message ){
            this.code = code;
            this.title = title;
            this.message = message;
        }

        public String getTitle() {
            return title;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }




    public ServerResponse(Reader response) {
        this.convertResponse(response);

    }


    public ServerResponse() {

    }

    public int getStatus() {
        return status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public Map <String,Map> getErrors() {
        return errors;
    }
    public List <Error> getErrorsList() {
        return listErrors;
    }


    protected void convertResponse (Reader response){
        this.listErrors.clear();
        final Gson gson = new Gson();
        ServerResponse<T> clone = new ServerResponse<T>();
        clone = gson.fromJson(response, new TypeToken<ServerResponse<T>>(){}.getType());


        this.data = clone.data;


        this.errors = clone.getErrors();
        this.success = clone.isSuccess();
        this.status = clone.getStatus();
        this.message = clone.getMessage();
        if (clone.getErrors() != null){
            this.errorsToArray();
        }

    }


    public Boolean hasError (){
        return this.listErrors.size()>=1;
    }



    public List<Error> errorsToArray (){
        for (Map.Entry<String, Map> entry : this.errors.entrySet()){
            Map value = entry.getValue();
            this.listErrors.add(new Error
                    (entry.getKey(), (String)value.get("title"),(String)value.get("message")));
        }

        return this.listErrors;

    }


}

