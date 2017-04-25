package com.example.soojinoh.analyzeimage;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import static com.example.soojinoh.analyzeimage.R.id.textView;


public class SearchActivity extends AppCompatActivity {

    private String htmlPageUrl ;
    private TextView textviewHtmlDocument;
    private String htmlContentInStringFormat;
    String barcodeUri;
    TextToSpeech t1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                }
            }
        });

        Intent intent = getIntent();
        barcodeUri = intent.getExtras().getString("barcodeUri");
        htmlPageUrl = "http://www.dllg.co.kr/product/product_view.asp?pcode=" + barcodeUri;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        textviewHtmlDocument = (TextView)findViewById(textView);
        textviewHtmlDocument.setMovementMethod(new ScrollingMovementMethod());

//        Button htmlTitleButton = (Button)findViewById(R.id.button);
//        htmlTitleButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();
//            }
//        });

        textviewHtmlDocument.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                t1.speak(textviewHtmlDocument.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        Elements name, price;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.i("logcat", htmlPageUrl);
                Document doc = Jsoup.connect(htmlPageUrl).get();
                name = doc.select("font[color=#0080c0]");
                price = doc.select("tr[bgcolor=#F9F9F9");

                    htmlContentInStringFormat += (name.text().trim()) + (price.text().trim()) + "\n";

//                for (Element link : links) {
//                    htmlContentInStringFormat += (link.attr("abs:href")
//                            + "("+link.text().trim() + ")\n");
//                }

            } catch (IOException e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            textviewHtmlDocument.setText(htmlContentInStringFormat);
        }
    }


}