package com.vivetuentrada.registerapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DialogTitle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;

/**
 * Created by desarrollo on 21/06/18.
 */

public class DialogAlert extends DialogFragment {


    private String MESSAGE = "Error Desconocido";
    private String TITLE = "BOLETO RECHAZADO";
    private String BUTTON_TITLE = "ACEPTAR";

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState){

       AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        TextView title = new TextView(getActivity());
        title.setTextColor(Color.YELLOW);
        title.setTextSize(22);
        title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        title.setPadding(70,40,0,0);
        title.setText(this.TITLE);

       builder
               //.setTitle(this.TITLE)
               .setCustomTitle(title)
               .setMessage(this.MESSAGE)
               .setPositiveButton(this.BUTTON_TITLE, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {

                   }
               })
               .setCancelable(false);



       final Dialog dialog = builder.create();
       dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_red_dark);
        //dialog.setCanceledOnTouchOutside(false);

       dialog.setOnShowListener(new DialogInterface.OnShowListener() {
           @Override
           public void onShow(final DialogInterface dialogInterface) {
               final Button button = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
               button.setBackgroundColor(Color.WHITE);
               button.setTextColor(Color.BLACK);
               button.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                   @Override
                   public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                   return true;
                   }

               });
               TextView textView = ((TextView) dialog.findViewById(android.R.id.message));
               textView.setTextColor(Color.WHITE);
               textView.setTextSize(22);
           }
       });



       return dialog;
   }




    public String getMESSAGE() {
        return MESSAGE;
    }

    public void setMESSAGE(String MESSAGE) {
        this.MESSAGE = MESSAGE;
    }

    public String getTITLE() {
        return TITLE;
    }

    public void setTITLE(String TITLE) {
        this.TITLE = TITLE;
    }

    public String getBUTTON_TITLE() {
        return BUTTON_TITLE;
    }

    public void setBUTTON_TITLE(String BUTTON_TITLE) {
        this.BUTTON_TITLE = BUTTON_TITLE;
    }
}
