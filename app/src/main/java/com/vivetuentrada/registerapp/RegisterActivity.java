package com.vivetuentrada.registerapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;
import com.vivetuentrada.registerapp.ServerResponse.Error;

import okhttp3.Response;

import static com.vivetuentrada.registerapp.ContinuousCaptureActivity.*;


/**
 * Created by usuario on 23/02/2018.
 */

public class RegisterActivity  extends AppCompatActivity  implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private String CODE_FORMAT = "CODE_128";
    private ValidateInputTicketTask tValidateTask = null;
    private Button scanBtn;
    private TextView formatTxt, contentTxt;
    private SessionStorageService _session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _session =  SessionStorageService.getInstance(getBaseContext());
        setContentView(R.layout.register_activity);
        scanBtn = (Button) findViewById(R.id.scan_button);
        formatTxt = (TextView) findViewById(R.id.scan_format);
        contentTxt = (TextView) findViewById(R.id.scan_content);
        this.requestPermissions();
        scanBtn.setOnClickListener(this);
        Log.d("access_token", _session.getAtribute(_session.ACCESS_TOKEN));
    }

    /**
     * onlcikc listener
     **/
    public void onClick(View v) {
        //respond to clicks
        if (v.getId() == R.id.scan_button) {
            if (tValidateTask != null){
                return ;
            }

            Intent intent = new Intent(this, ContinuousCaptureActivity.class);
            intent.putExtra("SCAN_FORMATS", CODE_FORMAT);
            startActivity(intent);

        }
    }





    public void requestPermissions() {
        Log.i("MyApp", "permissions");
        System.out.println(PackageManager.PERMISSION_GRANTED);
        //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET}, 1);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {// Marshmallow+

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No se necesita dar una explicación al usuario, sólo pedimos el permiso.
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                    // MY_PERMISSIONS_REQUEST_CAMARA es una constante definida en la app. El método callback obtiene el resultado de la petición.
                }
            }


        } else { // Pre-Marshmallow

        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class ValidateInputTicketTask extends AsyncTask<Void, Void, Integer>  {
        private String codeBar;
        private RegisterService rService = new RegisterService();
        ServerResponse<RegisterStatus> serverResp;

        ValidateInputTicketTask(String codeBar) {
            this.codeBar = codeBar;
        }

        @Override
        protected Integer doInBackground(Void... strings) {
            try {
                Response resp = this.rService.validateTicket(codeBar);
                Log.d("response",resp.toString());
                int code = resp.code();
                if (code  == 200){
                    serverResp = new ServerResponse<RegisterStatus>(resp.body().charStream());
                }else{
                    if (code >=400  && code <=403){
                        _session.logout();
                        Intent loginActivity = new Intent(RegisterActivity.this,LoginActivity.class);
                        startActivity(loginActivity);
                    }
                }
                return resp.code();
            }
            catch (ConnectException e){
                return 512;
            }
            catch (Exception e) {
                return 500;
            }
        }


        protected void onPostExecute(final Integer resp) {
            tValidateTask = null;
            formatTxt.setText(codeBar);
            if (resp == 200) {

                if (serverResp.hasError()) {
                    ServerResponse.Error firstError = (ServerResponse.Error) serverResp.getErrorsList().get(0);
                    contentTxt.setText(MessagesHelper.RegisterErrors(firstError.getCode(), getBaseContext()));
                } else {
                    contentTxt.setTextColor(Color.BLUE);

                    Gson convert = new Gson();
                    String strResponse= convert.toJson(serverResp.getData());
                    RegisterStatus status = convert.fromJson(strResponse,  new TypeToken<RegisterStatus>(){}.getType());
                    String str_status = status.getStatus();
                    if (str_status.equals("accept")){
                        contentTxt.setTextColor(Color.GREEN);
                    }
                    if (str_status.equals("reject") ){
                        contentTxt.setTextColor(Color.RED);
                    }
                    contentTxt.setText(MessagesHelper.RegisterMessages(status.getStatus(),getBaseContext()));
                }
            } else {
                contentTxt.setText(MessagesHelper.HttpErrorsMessage(resp, getBaseContext()));
            }
        }
        @Override
        protected void onCancelled() {
            tValidateTask = null;
            //showProgress(false);
        }
    }
}