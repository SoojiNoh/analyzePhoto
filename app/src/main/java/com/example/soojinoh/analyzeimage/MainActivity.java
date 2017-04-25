package com.example.soojinoh.analyzeimage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public VisionServiceClient visionServiceClient = new VisionServiceRestClient("5a4866e40fa9456db03824a9143df092");
    public Uri imageUri;
    public Bitmap mBitmap;

    TextRecognizer ocrFrame;
    Frame frame;
    StringBuilder stringBuilder;
    TextToSpeech t1, t2, t3, t4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //tts init
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });
        t2=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t2.setLanguage(Locale.US);
                }
            }
        });
        t3=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t3.setLanguage(Locale.US);
                }
            }
        });
        t4=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t4.setLanguage(Locale.US);
                }
            }
        });


        Intent intent = getIntent();
        imageUri = intent.getParcelableExtra("imageUri");

        try {

            mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//      Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.baby);
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {

                TextView textView = (TextView)findViewById(R.id.txtDescription);
                Log.i("logcat",textView.getText().toString());
                t3.speak("I think the object as ", TextToSpeech.QUEUE_FLUSH, null);
                t1.speak(textView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

                TextView textView2 = (TextView)findViewById(R.id.txtDescription2);
                t4.speak("I think the text as ", TextToSpeech.QUEUE_FLUSH, null);
                t2.speak(textView2.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);


            }
        });
        Button btnProcess = (Button) findViewById(R.id.btnProcess);

        imageView.setImageBitmap(mBitmap);

        //Convert image to stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());


        //textRecognizer init
        ocrFrame = new TextRecognizer.Builder(getApplicationContext()).build();
        frame = new Frame.Builder().setBitmap(mBitmap).build();
        if (ocrFrame.isOperational()){
            Log.e("logcat", "Textrecognizer is operational");
        }



        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AsyncTask<InputStream,String,String> visionTask = new AsyncTask<InputStream, String, String>() {
                    ProgressDialog mDialog = new ProgressDialog(MainActivity.this);

                    @Override
                    protected String doInBackground(InputStream... params) {
                        try {
                            publishProgress("Recognizing....");
                            String[] features = {"Description"};
                            String[] details = {};

                            AnalysisResult result = visionServiceClient.analyzeImage(params[0],features,details);

                            String strResult = new Gson().toJson(result);
                            return strResult;
                        } catch (Exception e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        mDialog.show();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        mDialog.dismiss();

                        AnalysisResult result = new Gson().fromJson(s,AnalysisResult.class);
                        TextView textView = (TextView)findViewById(R.id.txtDescription);
                        stringBuilder = new StringBuilder();
//                        StringBuilder stringBuilder = new StringBuilder();
                        for(Caption caption:result.description.captions){
                            stringBuilder.append(caption.text);
                        }
                        textView.setText(stringBuilder);
//                        t1.speak(textView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);


                        //textRecognizer
                        SparseArray<TextBlock> textBlocks = ocrFrame.detect(frame);

                        if(textBlocks.size() != 0){

                            StringBuilder stringBuilder = new StringBuilder();
                            for(int i=0 ; i<textBlocks.size() ; ++i){
                                TextBlock item = textBlocks.valueAt(i);
                                stringBuilder.append(item.getValue());
                                stringBuilder.append("\n");
                            }
                            TextView textView2 = (TextView)findViewById(R.id.txtDescription2);
                            textView2.setText(stringBuilder.toString());
                        } else {
                            Log.i("logcat","no detection");
                        }


                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                        mDialog.setMessage(values[0]);
                    }
                };

                visionTask.execute(inputStream);
            }
        });
    }
}
