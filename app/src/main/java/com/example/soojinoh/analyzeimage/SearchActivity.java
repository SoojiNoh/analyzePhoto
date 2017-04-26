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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.soojinoh.analyzeimage.R.id.textView;


public class SearchActivity extends AppCompatActivity {

    private String htmlPageUrl, htmlPageUrl2;
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
        htmlPageUrl2 = "http://www.koreannet.or.kr/home/hpisSrchGtin.gs1?gtin=" + barcodeUri;
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

        Elements name, price, content, detail_info;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Log.i("logcat", htmlPageUrl);
                Document doc = Jsoup.connect(htmlPageUrl).get();
                detail_info = doc.select("font[color=#0080c0]");
                price = doc.select("tr[bgcolor=#F9F9F9");

                Document doc2 = Jsoup.connect(htmlPageUrl2).get();
                //*[@id="contents"]/div[3]/dl/div/div[1]
                name = doc2.select("div[class=productTit]");
                content = doc2.select("dd[class=description]");

                Log.i("logcat", "price :"+price.text());
                Log.i("logcat", "name :"+name.text());
                Log.i("logcat", "content :"+content.text());


                //adapt search_result with string pattern
                String price_string, name_string;
                Matcher matcher;
                Pattern r;
                Log.i("logcat", htmlPageUrl);

                String pattern_price = "(.*)(\\s)(.*)(\\s)(\\d+)(\\s)(.*)";
                String pattern_name = "(\\d+)(\\s*)(.*)";
                // Create a Pattern object
                r = Pattern.compile(pattern_price);
                // Now create matcher object.
                matcher = r.matcher(price.text());
                if (matcher.find( )) {
                    price_string = matcher.group(7);
                } else {
                    price_string = "none";
                }
                r = Pattern.compile(pattern_name);
                matcher = r.matcher(name.text());
                if (matcher.find( )) {
                    name_string = matcher.group(3);
                } else {
                    name_string = "none";
                }
                htmlContentInStringFormat = "";
                Log.i("logcat","zz"+detail_info.text().trim()+"zz");
                if (name_string == "none"){
                    htmlContentInStringFormat ="본 상품의 정보를 찾을 수 없습니다. 죄송합니다.";
                } else {
                    htmlContentInStringFormat += "본 상품의 이름은 " + (name_string.trim()) + "입니다.";
                        if(!detail_info.text().trim().equals("●")){
                            htmlContentInStringFormat+="더 자세하게 말씀드리자면 " + (price_string.trim()) + "입니다. 간략하게 말씀드리자면 " + (content.text().trim()) + "입니다.";
                        }
                }

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