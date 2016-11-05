package com.tirso.jimousse.qrcode;

import android.util.Log;

/**
 * Created by jimousse on 30/09/16.
 */
public class WifiQr {

    private String ssid;
    private String authenticationType;
    private String password;
    private static final String TAG = "WifiQr";

    public WifiQr(String ssid, String password, String authenticationType){
        this.ssid = ssid;
        this.password = password;
        this.authenticationType = authenticationType;
    }


    public WifiQr(String ssid, String password){
        this.ssid = ssid;
        this.password = password;
    }

    public WifiQr(){}


    public static WifiQr parseWifiQrText(String wifiQrText){
        // example of wifiQrText: "WIFI:S:mynetwork;T:WPA;P:mypass;"
        WifiQr wifiQr = new WifiQr();

        if(!wifiQrText.startsWith("WIFI:")){
            Log.e(TAG, "Wifi Qr text doesn't start by WIFI:");
            return null;
        }
        String retrievedSSID = null;
        String retrievedAuthenticationType = null;
        String retrievedPassword = null;


        try {
            String[] wifiFields = wifiQrText.substring("WIFI:".length(), wifiQrText.length()).split(";");
            Log.i(TAG, "Wifi QR code text : " + wifiQrText);

            for(int i=0; i<wifiFields.length; i++){
                if(wifiFields[i].split(":")[0].equals("S")){
                    retrievedSSID = wifiFields[i].split(":")[1];
                    Log.i(TAG, "Wifi QR code SSID : " + retrievedSSID );
                } else if(wifiFields[i].split(":")[0].equals("T")){
                    retrievedAuthenticationType = wifiFields[i].split(":")[1];
                    Log.i(TAG, "Wifi QR code authentication type : " + retrievedAuthenticationType );
                } else if(wifiFields[i].split(":")[0].equals("P")){
                    retrievedPassword = wifiFields[i].split(":")[1];
                    Log.i(TAG, "Wifi QR code password : " + retrievedPassword );
                }
            }

            wifiQr.setAuthenticationType(retrievedAuthenticationType);
            wifiQr.setPassword(retrievedPassword);
            wifiQr.setSsid(retrievedSSID);

        } catch (Exception e){
            Log.e(TAG, "Error parsing Wifi Qr code content, maybe a field is missing...\n" + e.getMessage() );
            return null;
        }

        return wifiQr;
    }

    public String toString(){
        return "WiFi: " + ssid;
    }

    public static String buildWifiQrContent(String authenticationType, String ssid, String password){
        return "WIFI:T:" + authenticationType + ";S:" + ssid + ";P:" + password + ";;";
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }


}
