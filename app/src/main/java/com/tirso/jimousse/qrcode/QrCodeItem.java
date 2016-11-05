package com.tirso.jimousse.qrcode;

/**
 * Created by jimousse on 18/09/16.
 */
public class QrCodeItem {

    private int id;
    private QrType type;
    private String rawContent;

    public QrCodeItem(){
    }


    public QrCodeItem(int id, String stringType, String rawContent){
        this.id = id;
        this.type = getQrType(stringType);
        this.rawContent = rawContent;
    }

    public QrCodeItem(QrType type, String rawContent){
        this.type = type;
        this.rawContent = rawContent;
    }

    public String toString(){
        if(type == QrType.WifiQr){
            return WifiQr.parseWifiQrText(rawContent).getSsid();
        } else {
            return rawContent;
        }
    }


    public enum QrType {
        UrlQr("url"), WifiQr("wifi"), TextQr("text");
        private String type;
        QrType(String type) {
            this.type = type;
        }
        @Override
        public String toString(){
            return type;
        }
    }

    public static QrCodeItem.QrType getTypeFromRaw(String rawResult){
        if(rawResult.startsWith("http")){
            return QrCodeItem.QrType.UrlQr;
        } else if(rawResult.startsWith("WIFI")){
            return QrCodeItem.QrType.WifiQr;
        } else {
            return QrCodeItem.QrType.TextQr;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public QrType getType() {
        return type;
    }


    public String getStringType() {
        switch (type){
            case WifiQr:
                return "wifi";
            case TextQr:
                return "text";
            case UrlQr:
                return "url";
            default:
                return "text";
        }
    }

    public QrType getQrType(String type) {
        switch (type){
            case "wifi":
                return QrType.WifiQr;
            case "text":
                return QrType.TextQr;
            case "url":
                return QrType.UrlQr;
            default:
                return QrType.TextQr;
        }
    }

    public String getRawContent() {
        return rawContent;
    }

}
