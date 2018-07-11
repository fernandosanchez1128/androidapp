package com.vivetuentrada.registerapp;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;


import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.view.View.OnKeyListener;
import android.view.View;
import android.view.KeyEvent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.ConnectException;

import com.vivetuentrada.registerapp.com.vivetuentrada.registerapp.models.RegisterStatus;
import com.vivetuentrada.registerapp.com.vivetuentrada.registerapp.services.AuthService;
import com.vivetuentrada.registerapp.com.vivetuentrada.registerapp.services.RegisterService;
import com.vivetuentrada.registerapp.com.vivetuentrada.registerapp.services.ServerResponse;
import com.vivetuentrada.registerapp.com.vivetuentrada.registerapp.services.SessionStorageService;
import com.vivetuentrada.registerapp.com.vivetuentrada.registerapp.utils.MessagesHelper;

import okhttp3.Response;


/**
 * Created by usuario on 23/02/2018.
 */

public class RegisterActivity  extends AppCompatActivity  implements View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private Vibrator vibrator;
    private String CODE_FORMAT = "CODE_128";
    private ValidateInputTicketTask tValidateTask = null;
    private Button scanBtn, registerBtn, logoutBtn;
    private TextView responseTxt, welcomeTxt, codeInfo;
    private EditText codeTxt;
    private SessionStorageService _session;
    private View contentView,progressView ;
    private String lastCode ;
    private DialogAlert ALERT = new DialogAlert();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _session =  SessionStorageService.getInstance(getBaseContext());
        setContentView(R.layout.register_activity);
        scanBtn = (Button) findViewById(R.id.scan_button);
        logoutBtn = (Button) findViewById(R.id.logout);
        registerBtn = (Button) findViewById(R.id.register_button);
        responseTxt= (TextView) findViewById(R.id.response);
        codeInfo = (TextView) findViewById(R.id.code);
        welcomeTxt= (TextView) findViewById(R.id.welcome);
        codeTxt = (EditText) findViewById(R.id.codeText);
        contentView = findViewById(R.id.register_content);
        progressView = findViewById(R.id.scan_progress);
        this.requestPermissions();
        scanBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);
        // Obtiene instancia a Vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        codeTxt.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent keyevent) {
                //If the keyevent is a key-down event on the "enter" button
                if ((keyevent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String code_bar = codeTxt.getText().toString();
                    if (validateCode (code_bar)){
                        lastCode = code_bar;
                        showProgress(true);
                        tValidateTask = new RegisterActivity.ValidateInputTicketTask(code_bar);
                        tValidateTask.execute((Void) null);
                        codeTxt.setText(null);
                    }
                    return true;
                }
                return false;
            }
        });
        welcomeTxt.setText(welcomeTxt.getText() + " " + _session.getAtribute(_session.USER) );
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
        if (v.getId() == R.id.register_button) {
            String code_bar = codeTxt.getText().toString();
            if (validateCode (code_bar)){
                lastCode = code_bar;
                showProgress(true);
                tValidateTask = new RegisterActivity.ValidateInputTicketTask(code_bar);
                tValidateTask.execute((Void) null);
                codeTxt.setText(null);
            }
        }

        if (v.getId() == R.id.logout){
            LogoutTask logoutTask = new RegisterActivity.LogoutTask();
            logoutTask.execute((Void) null);

        }
    }

    public boolean validateCode (String code){
        if (TextUtils.isEmpty(code)) {
            codeTxt.setError(getString(R.string.error_field_required));
            return false;
        }
        if (code.length() <= 15){
            codeTxt.setError(getString(R.string.server_register_error_shortcode));
            return false;
        }
        //if (code.equals(lastCode)){
          //  codeTxt.setError(getString(R.string.error_code_input_repeated));
            //return false;
        //}
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            contentView.setVisibility(show ? View.GONE : View.VISIBLE);
            contentView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    contentView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            contentView.setVisibility(show ? View.GONE : View.VISIBLE);
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
                Log.e("response",resp.toString());
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
                Log.e("except","network");
                return 512;
            }
            catch (Exception e) {
                Log.e("except","exception");
                Log.e("except",e.getMessage());
                return 500;
            }
        }


        protected void onPostExecute(final Integer resp) {
            showProgress(false);
            tValidateTask = null;
            if (resp == 200) {
                FragmentManager fragment = getFragmentManager();
                responseTxt.setTextColor(Color.RED);
                codeInfo.setText(lastCode);
                if (serverResp.hasError()) {
                    ServerResponse.Error firstError = (ServerResponse.Error) serverResp.getErrorsList().get(0);
                    String error = MessagesHelper.registerErrors(firstError.getCode(), getBaseContext());
                    responseTxt.setText(error);
                    ALERT.setMESSAGE(error);
                    ALERT.show(fragment,"");

                } else {
                    Gson convert = new Gson();
                    String strResponse= convert.toJson(serverResp.getData());
                    RegisterStatus status = convert.fromJson(strResponse,  new TypeToken<RegisterStatus>(){}.getType());
                    String str_status = status.getStatus();
                    String msg = MessagesHelper.registerMessages(status.getMessage(),status.getArgs(),getBaseContext());
                    if (str_status.equals("accept")){
                        responseTxt.setTextColor(Color.BLUE);
                        responseTxt.setText(msg);
                    }else{
                        r
                        responseTxt.setText(msg);
                        ALERT.setMESSAGE(msg);
                        ALERT.show(fragment,"");
                    }

                }
            } else {
                responseTxt.setText(MessagesHelper.HttpErrorsMessage(resp, getBaseContext()));
            }
        }
        @Override
        protected void onCancelled() {
            tValidateTask = null;
            //showProgress(false);
        }
    }

    public class LogoutTask extends AsyncTask<Void, Void, Void>{
        private AuthService authService = new AuthService();
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Response resp = this.authService.logout();
                Log.d("logout",resp.toString());
                System.out.println(resp);
                _session.logout();
                Intent loginActivity = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(loginActivity);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}