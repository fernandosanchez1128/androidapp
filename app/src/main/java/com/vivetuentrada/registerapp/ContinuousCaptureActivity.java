package com.vivetuentrada.registerapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.io.IOException;
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
    public TextView formatTxt, contentTxt;
    private SessionStorageService _session;


    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }
            if (tValidateTask != null){
                return ;
            }

            lastText = result.getText();
            //barcodeView.setStatusText(result.getText());
            beepManager.playBeepSoundAndVibrate();

            //Added preview of scanned barcode
            ImageView imageView = (ImageView) findViewById(R.id.barcodePreview);
            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
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
        formatTxt = (TextView) findViewById(R.id.scan_format);
        contentTxt = (TextView) findViewById(R.id.scan_content);

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
