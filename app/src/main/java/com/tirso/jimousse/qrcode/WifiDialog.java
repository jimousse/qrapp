package com.tirso.jimousse.qrcode;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WifiDialog extends Dialog implements
        android.view.View.OnClickListener {

    String ssid;
    TextView wifiNameTextview;
    DatabaseHandler db;
    Button savePasswordButton;
    private boolean updatePassword; // check if the password is being created or updated
    EditText passwordEditText;


    public WifiDialog(Activity activity, String ssid, DatabaseHandler db, boolean updatePassword){
        super(activity);
        this.db = db;
        this.ssid = ssid;
        this.updatePassword = updatePassword;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.wifi_dialog);
        wifiNameTextview = (TextView) findViewById(R.id.wifi_ssid);
        wifiNameTextview.setText(ssid);
        passwordEditText = (EditText) findViewById(R.id.network_password);
        savePasswordButton = (Button) findViewById(R.id.save_password_button);
        savePasswordButton.setOnClickListener(this);
        if(!updatePassword){
            savePasswordButton.setText("Save Password");
        } else {
            savePasswordButton.setText("Update Password");
            passwordEditText.setText(db.getWifi(ssid).getPassword());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_password_button:
                if(passwordEditText.getText().toString().length()>0){
                    if(!updatePassword){
                        savePasswordInDb(passwordEditText.getText().toString());
                        Toast.makeText(getContext(), "Password saved!", Toast.LENGTH_LONG).show();
                    } else {
                        updatePasswordInDb(passwordEditText.getText().toString());
                        Toast.makeText(getContext(), "Password updated!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "No password!", Toast.LENGTH_LONG).show();
                }
            default:
                break;
        }
        dismiss();
    }


    public void savePasswordInDb(String wifiPassword){
        db.addWifiPassword(ssid, wifiPassword);
    }

    public void updatePasswordInDb(String wifiPassword){
        db.modifyWifiPassword(ssid, wifiPassword);
    }
}