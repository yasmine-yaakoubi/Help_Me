package com.example.scanner;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scanner.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class SecondActivity extends AppCompatActivity {
    TextView resulttv;
    TextView codebar;
    String rcodebar;
    TextToSpeech tts;
    Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Intent intent = getIntent();
        String message=intent.getStringExtra("TextView");
        resulttv=findViewById(R.id.result_tv);
        codebar=findViewById(R.id.textView);
        codebar.setText(message);
        rcodebar =message;
        bt=findViewById(R.id.button);

        try {
            getData();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }
    public void getData() throws MalformedURLException {
        Uri uri=Uri.parse("https://c1e9-196-200-133-171.ngrok.io/products")
                .buildUpon().build() ;
        URL url =new URL(uri.toString());
        new DoTask().execute(url);

    }
    class DoTask extends AsyncTask<URL,Void,String> {
        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];
            String data = null;
            try {
                data = NetworkUtils.makeHTTPRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                parseJson(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public void parseJson(String data) throws JSONException {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray cityArray = jsonObject.getJSONArray("products");
            for (int i = 0; i < cityArray.length(); i++) {
                JSONObject produit = cityArray.getJSONObject(i);
                String produit_code = produit.get("codebar").toString();
                if (produit_code.equals(rcodebar)) {
                    String description = produit.get("description").toString();
                    String type = produit.get("type").toString();
                    String price = produit.get("price").toString();
                    String name = produit.get("name").toString();
                    resulttv.setText("this is "+name+"\n"+"it is a   "+type+" \n"+"it cost   "+price+" dirhams  "+"\n"+"it is    "+description);
                    tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int i) {
                            if (i == TextToSpeech.SUCCESS) {
                                tts.setLanguage(Locale.UK);
                                tts.setSpeechRate(1.0f);
                                tts.speak(resulttv.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                                bt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        tts.speak(resulttv.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                                    }});
                            }
                        }})
                    ;break;
                }else {
                    resulttv.setText("we do not have any infomation about the product you have just scaned");
                            tts= new TextToSpeech(getApplicationContext(),new TextToSpeech.OnInitListener(){
                                @Override
                                public void onInit(int i) {
                                    if(i==TextToSpeech.SUCCESS){
                                        tts.setLanguage(Locale.UK);
                                        tts.setSpeechRate(1.0f);
                                        tts.speak(resulttv.getText().toString(),TextToSpeech.QUEUE_ADD,null);
                                    } }});
                    bt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tts.speak(resulttv.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                        }});
                }

        }


    }
}}
