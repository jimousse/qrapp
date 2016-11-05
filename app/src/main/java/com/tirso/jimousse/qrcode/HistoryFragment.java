package com.tirso.jimousse.qrcode;
import org.json.JSONArray;


import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends ListFragment implements AdapterView.OnItemClickListener {

    DatabaseHandler db;
    ArrayAdapter<QrCodeItem> adapter;
    ArrayList<QrCodeItem> qrCodeList;
    private static HistoryFragment instance;


    public HistoryFragment() {
    }

    public static HistoryFragment getInstance(DatabaseHandler db) {
        if(instance == null) {
            HistoryFragment fragment = new HistoryFragment();
            instance = fragment;
            fragment.db = db;
            return fragment;
        } else {
            return instance;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        qrCodeList = getScannedQrCodes();
        adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                qrCodeList);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        QrCodeItem qrCodeItem = qrCodeList.get(position);
        HistoryItemDialog historyItemDialog = new HistoryItemDialog(getActivity(), qrCodeItem, db);
        historyItemDialog.show();
    }



    public ArrayList<QrCodeItem> getScannedQrCodes(){
        ArrayList<QrCodeItem> qrCodeList = new ArrayList<>();
        List<QrCodeItem> qrList = db.getAllQrcodes();
        for(QrCodeItem qrCodeItem: qrList){
            qrCodeList.add(qrCodeItem);
        }
        return qrCodeList;
    }

    public void refreshView() {
        qrCodeList = getScannedQrCodes();
        if(adapter!=null){
            adapter.clear();
            adapter.addAll(qrCodeList);
            adapter.notifyDataSetChanged();
        }
    }


}
