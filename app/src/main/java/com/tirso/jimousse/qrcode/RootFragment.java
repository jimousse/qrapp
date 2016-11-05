package com.tirso.jimousse.qrcode;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jimousse on 5/09/16.
 */
public class RootFragment extends Fragment {
    private static RootFragment instance;
    DatabaseHandler db;



    public static RootFragment getInstance(DatabaseHandler db) {
        if(instance == null) {
            RootFragment fragment = new RootFragment();
            instance = fragment;
            fragment.db = db;
            return fragment;
        } else {
            return instance;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.root_fragment, container, false);
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.root_frame, WifiFragment.getInstance(db));
        transaction.commit();
        return view;
    }

}
