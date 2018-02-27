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
import android.widget.ImageView;

import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.io.IOException;
import java.util.Arrays;
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
    private RegisterActivity.ValidateTicketTask tValidateTask = null;


    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }

            lastText = result.getText();
            //barcodeView.setStatusText(result.getText());
            beepManager.playBeepSoundAndVibrate();

            //Added preview of scanned barcode
            ImageView imageView = (ImageView) findViewById(R.id.barcodePreview);
            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.continuous_scan);

        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        barcodeView.initializeFromIntent(getIntent());
        barcodeView.decodeContinuous(callback);
//u        capture = new CaptureManager(this, barcodeView);

        //capture.initializeFromIntent(getIntent(), savedInstanceState);
        //capture.decode();
        //barcodeView.get


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
                ServerResponse <List <Ts>> response = null;
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
                ServerResponse.Error firstError = (ServerResponse.Error) resp.getErrorsList().get(0);
                //formatTxt.setText(firstError.getTitle());
                //contentTxt.setText(firstError.getMessage());
            }else{
                //contentTxt.setText("success");

            }
        }



        @Override
        protected void onCancelled() {
            tValidateTask = null;
            //showProgress(false);
        }
    }
}
