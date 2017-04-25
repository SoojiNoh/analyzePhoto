package com.example.soojinoh.analyzeimage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.google.zxing.Result;

import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarcodeActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private AlertDialog mDialog = null;
    public String barcodeString;
    TextToSpeech t1;

    @Override
    public void onCreate(Bundle state) {

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });

        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }
    public static final String TAG = "suzi";

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v(TAG, rawResult.getText()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        barcodeString=rawResult.getText();
        mDialog = createDialog();
        mDialog.show();
        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this);
    }

    private AlertDialog createDialog() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("Barcode");
        ab.setMessage(barcodeString);
        ab.setCancelable(false);
        ab.setIcon(null);
        t1.speak("Barcode Detected, Do you want to search", TextToSpeech.QUEUE_FLUSH, null);


        ab.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("barcodeUri", barcodeString);
                startActivity(intent);
            }
        });

        return ab.create();
    }



}
