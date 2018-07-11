package com.vivetuentrada.registerapp.com.vivetuentrada.registerapp.models;
import java.util.Map;
/**
 * Created by desarrollo on 2/03/18.
 */

public class RegisterStatus {

    public Map args;
    public String code;
    public String message;
    public String status;


    public RegisterStatus() {
    }

    public RegisterStatus(String code, String message, String status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map getArgs() {
        return args;
    }

    public void setArgs(Map args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return "RegisterStatus{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
