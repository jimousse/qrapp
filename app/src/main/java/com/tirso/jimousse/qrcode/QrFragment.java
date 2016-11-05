package com.tirso.jimousse.qrcode;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.result.VCardResultParser;

public class QrFragment extends Fragment {

    ImageView qrCodeView;
    String qrContent;
    TextView ssidTextView;
    TextView passwordTextView;
    TextView authAlgoTextView;
    WifiQr wifiQr;
    private static QrFragment instance;




    public QrFragment() {
    }

    public static QrFragment newInstance() {
        if(instance == null) {
            QrFragment fragment = new QrFragment();
            instance = fragment;
            return fragment;
        } else {
            return instance;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = this.getArguments();
        qrContent = extras.getString("qrContent");
        wifiQr = WifiQr.parseWifiQrText(qrContent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        qrCodeView = (ImageView) view.findViewById(R.id.qrcode);
        ssidTextView = (TextView) view.findViewById(R.id.ssid);
        ssidTextView.setText(wifiQr.getSsid());
        passwordTextView = (TextView) view.findViewById(R.id.password);
        passwordTextView.setText(wifiQr.getPassword());
        authAlgoTextView = (TextView) view.findViewById(R.id.authalgo);
        authAlgoTextView.setText(wifiQr.getAuthenticationType());
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                try {
                    Bitmap bitmap = null;
                    bitmap = encodeAsBitmap(qrContent, 1200);
                    qrCodeView.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }


    Bitmap encodeAsBitmap(String str, int width) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, width, width, null);
        } catch (IllegalArgumentException iae) {
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 1200, 0, 0, w, h);
        return bitmap;
    }


}
