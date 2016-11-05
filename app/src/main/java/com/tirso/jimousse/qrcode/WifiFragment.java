package com.tirso.jimousse.qrcode;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class WifiFragment extends ListFragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "WifiFragment";
    WifiManager wifiManager;
    ArrayAdapter<String> adapter;
    private static WifiFragment instance;
    List<WifiConfiguration> configs;
    ArrayList<String> wifiNameList;
    DatabaseHandler db;



    public WifiFragment() {
    }

    public static WifiFragment getInstance(DatabaseHandler db) {
        if(instance == null) {
            WifiFragment fragment = new WifiFragment();
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
        View view = inflater.inflate(R.layout.fragment_wifi, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        configs = getWPAwifi(wifiManager.getConfiguredNetworks());
        wifiNameList = new ArrayList<>();
        for (WifiConfiguration config : configs) {
            if(getAuthAlgo(config)=="WPA"){
                wifiNameList.add(String.valueOf(config.SSID.replaceAll("\"", "")));
            }
        }
        adapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                wifiNameList);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String ssid = configs.get(position).SSID.replaceAll("\"", "");
                try{
                    db.getWifi(ssid); // test if wifi entry exists
                    WifiDialog wifiDialog = new WifiDialog(getActivity(), ssid, db, true);
                    wifiDialog.show();
                } catch (Exception e){
                    Log.i(TAG, "Password doesn't exist. Enter one!"); // do nothing
                }
                return true;
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WifiConfiguration config = configs.get(position);
        String ssid = configs.get(position).SSID.replaceAll("\"", "");
        String authAlgo = getAuthAlgo(config);
        try{
            String wifiPassword = db.getWifi(ssid).getPassword();
            Bundle data = new Bundle();
            data.putString("qrContent", WifiQr.buildWifiQrContent(authAlgo , ssid, wifiPassword));
            FragmentTransaction trans = getFragmentManager().beginTransaction();
            QrFragment qrFragment = QrFragment.newInstance();
            qrFragment.setArguments(data);
            trans.replace(R.id.root_frame, qrFragment);
            trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            trans.addToBackStack(null);
            trans.commit();
        } catch (Exception e){
            Log.i(TAG, "Password doesn't exist. Enter one!");
            WifiDialog wifiDialog = new WifiDialog(getActivity(), ssid, db, false);
            wifiDialog.show();
        }
    }

    public String getAuthAlgo(WifiConfiguration config){
        if(config.allowedKeyManagement.toString().contains("0")){
            return "WEP";
        } else {
            return "WPA";
        }
    }

    public List<WifiConfiguration> getWPAwifi(List<WifiConfiguration> configs) {
        List<WifiConfiguration> wpaWifis = new ArrayList<>();
        for (WifiConfiguration config : configs) {
            if (getAuthAlgo(config).equals("WPA")) {
                wpaWifis.add(config);
            }
        }
        return wpaWifis;
    }

    // in case the wifi was turned off when the app was launched,
    // the new wifi doesn't appear on the list
    public void refreshView() {
        configs = getWPAwifi(wifiManager.getConfiguredNetworks());
        wifiNameList = new ArrayList<>();
        for (WifiConfiguration config : configs) {
            if(getAuthAlgo(config)=="WPA"){
                wifiNameList.add(String.valueOf(config.SSID.replaceAll("\"", "")));
            }
        }
        if(adapter!=null){
            adapter.clear();
            adapter.addAll(wifiNameList);
            adapter.notifyDataSetChanged();
        }
    }




}
