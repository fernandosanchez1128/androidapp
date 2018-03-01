package com.vivetuentrada.registerapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import com.vivetuentrada.registerapp.ServerResponse.Error;

import okhttp3.Response;


/**
 * Created by usuario on 23/02/2018.
 */

public class RegisterActivity  extends AppCompatActivity  implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private String CODE_FORMAT = "CODE_128";
    private ValidateTicketTask tValidateTask = null;
    private Button scanBtn;
    public TextView formatTxt, contentTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MyApp", "I am here");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        scanBtn = (Button) findViewById(R.id.scan_button);
        formatTxt = (TextView) findViewById(R.id.scan_format);
        contentTxt = (TextView) findViewById(R.id.scan_content);
        this.requestPermissions();
        scanBtn.setOnClickListener(this);
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
            //startActivity(intent);
            //IntentIntegrator scanIntegrator = new IntentIntegrator(this);

            //scanIntegrator.initiateScan(Arrays.asList(new String[]{CODE_FORMAT}));
            //scanIntegrator.

            //tValidateTask = new ValidateTicketTask("1243");
            //tValidateTask.execute((Void) null);

        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanFormat = scanningResult.getFormatName();
            String scanContent = scanningResult.getContents();
            formatTxt.setText("FORMAT: " + scanFormat);
            contentTxt.setText("CONTENT: " + scanContent);
            formatTxt.setText(scanContent);
            tValidateTask = new ValidateTicketTask(scanContent);
            tValidateTask.execute((Void) null);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
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
    public class ValidateTicketTask extends AsyncTask<Void, Void, ServerResponse> {
        private String codeBar;
        private RegisterService rService = new RegisterService();

        ValidateTicketTask(String codeBar) {
            this.codeBar = codeBar;
        }

        @Override
        protected ServerResponse doInBackground(Void... strings) {
            try {
                Response resp = this.rService.validateTicket(codeBar);
                ServerResponse <List <UserAuth>> response = null;
                Log.d("response",resp.toString());
                if (resp.code() == 200){
                     ServerResponse<Object> serverResp = new ServerResponse<Object>(resp.body().charStream());
                     return serverResp;
                }else{
                    return null;
                }


            } catch (IOException e) {
                e.printStackTrace();
                Log.d("error",e.getMessage());
                return null;
            }
        }


        protected void onPostExecute(final ServerResponse resp) {
            tValidateTask = null;
            if (resp.hasError()){
                Error firstError = (Error) resp.getErrorsList().get(0);
                //formatTxt.setText(firstError.getTitle());
                contentTxt.setText(firstError.getMessage());
            }else{
                contentTxt.setText("success");

            }


            //System.out.println(response.getData().get(1).getTicketSale());



        }



        @Override
        protected void onCancelled() {
            tValidateTask = null;
            //showProgress(false);
        }
    }
}