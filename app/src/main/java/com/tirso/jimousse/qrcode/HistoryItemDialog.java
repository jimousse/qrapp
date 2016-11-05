package com.tirso.jimousse.qrcode;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by jimousse on 3/11/16.
 */
public class HistoryItemDialog extends QrActionDialog {

    private static final String TAG = "HistoryItemDialog";

    public HistoryItemDialog(Activity activity, QrCodeItem qrCodeItem, DatabaseHandler db) {
        super(activity, qrCodeItem, db);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.result_dialog);
        dialogTitle = (TextView) findViewById(R.id.dialog_title);
        leftButton = (Button) findViewById(R.id.left_button);
        rightButton = (Button) findViewById(R.id.right_button);
        textDisplayed = (TextView) findViewById(R.id.result_text);
        textDisplayed.setText(qrCodeItem.toString());
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        configureLeftButton();
        configureRightButton();
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.left_button && qrCodeItem.getType() == QrCodeItem.QrType.WifiQr){
            dismiss();
        }
        if(v.getId() == R.id.right_button){
            db.deleteQrcode(qrCodeItem.getId());
            HistoryFragment.getInstance(db).refreshView();
        }
        super.onClick(v);
    }

    @Override
    public void configureLeftButton(){
        super.configureLeftButton();
        if(qrCodeItem.getType() == QrCodeItem.QrType.TextQr.WifiQr){
            leftButton.setText("Ok");
        }
    }

    @Override
    public void configureRightButton(){
        rightButton.setText("Delete");
    }


}
