package com.tirso.jimousse.qrcode;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by jimousse on 5/11/16.
 */
public class QrActionDialog extends Dialog implements
        android.view.View.OnClickListener {

    Button leftButton;
    Button rightButton;
    TextView textDisplayed;
    TextView dialogTitle;
    Activity activity;
    QrCodeItem qrCodeItem;
    DatabaseHandler db;


    public QrActionDialog(Context context, QrCodeItem qrCodeItem, DatabaseHandler db) {
        super(context);
        this.qrCodeItem = qrCodeItem;
        this.db = db;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_button:
                if(qrCodeItem.getType() ==  QrCodeItem.QrType.UrlQr){
                    openURL(qrCodeItem.getRawContent());
                } else if(qrCodeItem.getType() == QrCodeItem.QrType.TextQr) {
                    copyText(qrCodeItem.getRawContent());
                    dismiss();
                }
                break;
            default:
                break;
        }
        dismiss();
    }


    public void configureLeftButton(){
        if(qrCodeItem.getType() == QrCodeItem.QrType.UrlQr){
            dialogTitle.setText("URL");
            leftButton.setText("Open URL");
        } else if(qrCodeItem.getType() == QrCodeItem.QrType.TextQr){
            dialogTitle.setText("Text");
            leftButton.setText("Copy Text");
        } else if(qrCodeItem.getType() == QrCodeItem.QrType.TextQr.WifiQr){
            dialogTitle.setText("WiFi");
            // text defined in child class
        }
    }

    public void configureRightButton(){
    }

    public void openURL(String url){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(browserIntent);
    }

    public void copyText(String textToCopy){
        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("qrText", textToCopy);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(activity, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
    }


}
