package com.tirso.jimousse.qrcode;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by jimousse on 17/09/16.
 */
public class ScanResultDialog extends QrActionDialog {

    private static final String TAG = "ScanResultDialog";

    public ScanResultDialog(Activity activity, QrCodeItem qrCodeItem, DatabaseHandler db){
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
        textDisplayed.setText(qrCodeItem.getRawContent());
        leftButton.setOnClickListener(this);
        rightButton.setOnClickListener(this);
        configureLeftButton();
        configureRightButton();
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.left_button && qrCodeItem.getType() == QrCodeItem.QrType.WifiQr){
            WifiQr wifiQr = WifiQr.parseWifiQrText(qrCodeItem.getRawContent());
            connectToWifi(wifiQr);
            db.addWifiPassword(wifiQr.getSsid(), wifiQr.getPassword());
            WifiFragment.getInstance(db).refreshView();
        }
        if(v.getId() == R.id.right_button){
            dismiss();
        }
        super.onClick(v);
    }

    @Override
    public void configureLeftButton(){
        super.configureLeftButton();
        if(qrCodeItem.getType() == QrCodeItem.QrType.TextQr.WifiQr){
            leftButton.setText("Connect");
        }
    }

    @Override
    public void configureRightButton(){
        rightButton.setText("Close");
    }


    public void connectToWifi(WifiQr wifiQr){
        WifiManager wifiManager = (WifiManager)getContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        Log.i(TAG,"Trying to connect to wifi " + wifiQr.toString());
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = String.format("\"%s\"", wifiQr.getSsid());
        if(wifiQr.getAuthenticationType().equals("WEP")){
            config.wepKeys[0] = "\"" + wifiQr.getPassword() + "\"";
            config.wepTxKeyIndex = 0;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        } else if(wifiQr.getAuthenticationType().equals("WPA")){
            config.preSharedKey = String.format("\"%s\"", wifiQr.getPassword());
            config.status = WifiConfiguration.Status.ENABLED;
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        }
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals(config.SSID)) {
                Log.i(TAG,"Wifi configuration already exists:  " + i.SSID);
                wifiManager.removeNetwork(i.networkId);
                break;
            }
        }
        int networkId = wifiManager.addNetwork(config);
        wifiManager.saveConfiguration();
        wifiManager.disconnect();
        wifiManager.enableNetwork(networkId, true);
        wifiManager.reconnect();
    }



}

