package com.tirso.jimousse.qrcode;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;

/**
 * Created by jimousse on 18/09/16.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "qrcodeDB";
    private static final String TABLE_QRCODE = "qrcodes";
    private static final String TABLE_WIFI = "wifis";


    // QR codes Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_CONTENT = "content";

    // Wifi Table Columns names
    private static final String KEY_SSID = "ssid";
    private static final String KEY_PASSWORD = "password";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // QR code history table creation query
        String CREATE_QR_TABLE = "CREATE TABLE " + TABLE_QRCODE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TYPE + " TEXT,"
                + KEY_CONTENT + " TEXT);";
        db.execSQL(CREATE_QR_TABLE);
        Log.i(TAG, "Creating QR code database...");

        String CREATE_WIFI_TABLE = "CREATE TABLE " + TABLE_WIFI + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SSID + " TEXT,"
                + KEY_PASSWORD + " TEXT);";
        db.execSQL(CREATE_WIFI_TABLE);

        Log.i(TAG, "Creating Wifi database...");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QRCODE);
        // Create tables again
        onCreate(db);
    }

    // QRCODE
    // Adding new qrcode
    public void addQrcode(QrCodeItem qrcode){
        Log.i(TAG, "Adding new QR code in database: " + qrcode.toString());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TYPE, qrcode.getStringType()); // qrcode type
        values.put(KEY_CONTENT, qrcode.getRawContent()); // qrcode content

        // Inserting Row
        db.insert(TABLE_QRCODE, null, values);
        db.close(); // Closing database connection
        Log.i(TAG, "QR code successfully added in database: " + qrcode.toString());
    }

    // WIFI
    // Adding new wifi
    public void addWifiPassword(String ssid, String password){
        Log.i(TAG, "Adding new Wifi in database: " + ssid);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SSID, ssid);
        values.put(KEY_PASSWORD, password);

        // Inserting Row
        db.insert(TABLE_WIFI, null, values);
        db.close(); // Closing database connection
        Log.i(TAG, "Wifi successfully added in database: " + ssid);
    }

    // WIFI
    // Modify wifi password WHERE ssid =
    public void modifyWifiPassword(String ssid, String password){
        Log.i(TAG, "Updating Wifi in database: " + ssid);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_WIFI + " SET " + KEY_PASSWORD + "='" + password  + "' WHERE " + KEY_SSID + "='" + ssid +  "'");
        db.close(); // Closing database connection
        Log.i(TAG, "Wifi successfully updated in database: " + ssid);
    }




    // QRCODE
    // Getting qrcode by id
    public QrCodeItem getQrcode(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_QRCODE, new String[] { KEY_ID,
                        KEY_TYPE, KEY_CONTENT }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        QrCodeItem qrcode = new QrCodeItem(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        return qrcode;
    }

    // WIFI
    // Getting wifi by ssid
    public WifiQr getWifi(String ssid){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_WIFI, new String[] { KEY_ID,
                        KEY_SSID, KEY_PASSWORD }, KEY_SSID + "=?",
                new String[] { String.valueOf(ssid) }, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        WifiQr wifi = new WifiQr(cursor.getString(1), cursor.getString(2));
        return wifi;
    }


    // QRCODE
    // Getting all qrcodes
    public List<QrCodeItem> getAllQrcodes(){
        List<QrCodeItem> qrcodeList = new ArrayList<QrCodeItem>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_QRCODE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                QrCodeItem qrCode = new QrCodeItem(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
                // Adding contact to list
                qrcodeList.add(qrCode);
            } while (cursor.moveToNext());
        }

        // return contact list
        return qrcodeList;
    }

    // WIFI
    // Getting all wifis
    public List<WifiQr> getAllWifis(){
        List<WifiQr> wifiList = new ArrayList<WifiQr>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_WIFI;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                WifiQr wifi = new WifiQr();
                wifi.setSsid(cursor.getString(1));
                wifi.setPassword(cursor.getString(2));
                // Adding contact to list
                wifiList.add(wifi);
            } while (cursor.moveToNext());
        }

        // return contact list
        return wifiList;
    }

    // QRCODE
    // Getting number of qrcodes
    public int getQrcodesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_QRCODE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }


    // QRCODE
    // Deleting single qrcode
    public void deleteQrcode(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_QRCODE, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }
}
