package com.tirso.jimousse.qrcode;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abhi.barcode.frag.libv2.BarcodeFragment;
import com.abhi.barcode.frag.libv2.IScanResultHandler;
import com.abhi.barcode.frag.libv2.ScanResult;


public class ScanFragment extends Fragment implements IScanResultHandler {

    private static final String TAG = "ScanActivity";
    BarcodeFragment barcodeFragment;
    DatabaseHandler db;
    private static ScanFragment instance;


    public static ScanFragment getInstance(DatabaseHandler db) {
        if(instance == null) {
            ScanFragment fragment = new ScanFragment();
            instance = fragment;
            fragment.db = db;
            return fragment;
        } else {
            return instance;
        }
    }



    public ScanFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void scanResult(ScanResult result) {
        String rawContent = result.getRawResult().getText();
        QrCodeItem.QrType qrType = QrCodeItem.getTypeFromRaw(rawContent);
        QrCodeItem qrCodeItem = new QrCodeItem(qrType, rawContent);
        db.addQrcode(new QrCodeItem(qrType, rawContent));
        Log.i(TAG, "QR Code content: " + rawContent);
        Log.i(TAG, "QR Code type: " + qrType);
        ScanResultDialog scanResultDialog = new ScanResultDialog(getActivity(), qrCodeItem, db);
        scanResultDialog.show();
        HistoryFragment.getInstance(db).refreshView();
        barcodeFragment.restart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demo, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        barcodeFragment = (BarcodeFragment)getChildFragmentManager().findFragmentById(R.id.sample);
        barcodeFragment.setScanResultHandler(this);
        barcodeFragment.restartPreviewAfterDelay(100000);
    }

    @Override
    public void onResume(){
        super.onResume();
    }


}
