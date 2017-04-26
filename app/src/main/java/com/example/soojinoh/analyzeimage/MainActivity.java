package com.example.soojinoh.analyzeimage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private Button shape_btn;
    private Button barcode_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startActivity(new Intent(this, SplashActivity.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        shape_btn = (Button)findViewById(R.id.shapebutton);
        barcode_btn = (Button)findViewById(R.id.barcodebutton);

        shape_btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
            }
        });
        barcode_btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), BarcodeActivity.class);
                startActivity(intent);
            }
        });

    }

    private void shape_btn_event(){

    }

}

