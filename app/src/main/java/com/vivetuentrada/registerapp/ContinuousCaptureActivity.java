package com.vivetuentrada.registerapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.vivetuentrada.registerapp.com.vivetuentrada.registerapp.models.RegisterStatus;
import com.vivetuentrada.registerapp.com.vivetuentrada.registerapp.services.RegisterService;
import com.vivetuentrada.registerapp.com.vivetuentrada.registerapp.services.ServerResponse;
import com.vivetuentrada.registerapp.com.vivetuentrada.registerapp.services.SessionStorageService;
import com.vivetuentrada.registerapp.com.vivetuentrada.registerapp.utils.MessagesHelper;

import java.net.ConnectException;
import java.util.List;

import okhttp3.Response;

//import com.journeyapps.barcodescanner.DecoratedBarcodeView;

/**
 * Created by desarrollo on 26/02/18.
 */

public class ContinuousCaptureActivity extends Activity {
    private static final String TAG = ContinuousCaptureActivity.class.getSimpleName();
    private final String CODE_FORMAT = "CODE_128";
    private DecoratedBarcodeView barcodeView;
    private CaptureManager capture;
    private BeepManager beepManager;
    private String lastText;
    private ContinuousCaptureActivity.ValidateTicketTask tValidateTask = null;
    private TextView codeTxt, responseTxt;
    private View contentView,progressView ;
    private SessionStorageService _session;


    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            onPause();

            if(result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                Toast errorToast = Toast.makeText(ContinuousCaptureActivity.this,
                        getString(R.string.error_code_repeated),Toast.LENGTH_LONG);
                errorToast.setGravity(Gravity.TOP|Gravity.LEFT, 0, 0);
                errorToast.show();
                onResume();
                return;
            }
            if (tValidateTask != null){
                onResume();
                return ;

            }

            lastText = result.getText();
            //barcodeView.setStatusText(result.getText());
            beepManager.playBeepSoundAndVibrate();

            //Added preview of scanned barcode
            ImageView imageView = (ImageView) findViewById(R.id.barcodePreview);
            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
            showProgress(true);
            tValidateTask = new ValidateTicketTask(lastText);
            tValidateTask.execute((Void) null);
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.continuous_scan);
        _session =  SessionStorageService.getInstance(getBaseContext());
        codeTxt = (TextView) findViewById(R.id.code_bar);
        responseTxt = (TextView) findViewById(R.id.response);
        contentView = findViewById(R.id.scan_content);
        progressView = findViewById(R.id.register_progress);
        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);

        beepManager = new BeepManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class ValidateTicketTask extends AsyncTask<Void, Void, Integer> {
        private String codeBar;
        private RegisterService rService = new RegisterService();
        ServerResponse<RegisterStatus> serverResp;

        ValidateTicketTask(String codeBar) {
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
                        Intent loginActivity = new Intent(ContinuousCaptureActivity.this,LoginActivity.class);
                        startActivity(loginActivity);
                    }
                }
                return resp.code();
            }
            catch (ConnectException e){
                lastText = null;
                Log.e("excep","network");
                return 512;
            }
            catch (Exception e) {
                lastText = null;
                Log.e("excep","exception");
                Log.e("excep",e.getMessage());
                return 500;
            }
        }


        protected void onPostExecute(final Integer resp) {
            tValidateTask = null;
            showProgress(false);
            onResume();
            codeTxt.setText(codeBar);
            if (resp == 200) {

                if (serverResp.hasError()) {
                    ServerResponse.Error firstError = (ServerResponse.Error) serverResp.getErrorsList().get(0);
                    responseTxt.setText(MessagesHelper.registerErrors(firstError.getCode(), getBaseContext()));
                } else {
                    responseTxt.setTextColor(Color.RED);

                    Gson convert = new Gson();
                    String strResponse= convert.toJson(serverResp.getData());
                    RegisterStatus status = convert.fromJson(strResponse,  new TypeToken<RegisterStatus>(){}.getType());
                    String str_status = status.getStatus();
                    String msg = MessagesHelper.registerMessages(status.getMessage(),status.getArgs(),getBaseContext());
                    if (str_status.equals("accept")){
                        responseTxt.setTextColor(Color.BLUE);
                        responseTxt.setText(msg);
                    }else{
                        responseTxt.setText(msg);
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
}
