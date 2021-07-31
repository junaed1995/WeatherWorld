package com.something.weatherworld;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText ed;
    TextView res;
    private static  final int REQUESTCODE=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ed=findViewById(R.id.editTextTextPersonName);


        Button speakbutton=findViewById(R.id.button3);
        speakbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                it.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
              startActivityForResult(it,REQUESTCODE);
            }
        });




    }
        //for voice command
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

            if(REQUESTCODE==requestCode && resultCode==RESULT_OK)
            {
                List<String> li=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String s=li.get(0);
                //Do something with the spoken text
                Button btn=findViewById(R.id.button);

                ed.setText(s);

                btn.callOnClick();

            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getweather0(View view)
    {
        DownloadTask task=new DownloadTask();
        task.execute("https://api.openweathermap.org/data/2.5/weather?q="+ed.getText().toString()+"&appid="+ KeyFile.returnKey());

    }


    public class DownloadTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection connection=null;
            try {
                url=new URL(urls[0]);
                connection= (HttpURLConnection) url.openConnection();
                InputStream in= connection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while (data!=-1)
                {
                    char s=(char) data;
                    result+=s;
                    data=reader.read();
                }
                return result;
            }catch (Exception e)
            {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                    }
                });
                // return e.getMessage();
                return null;
            }




        }


        protected void onPostExecute(String x) {
            super.onPostExecute(x);
            //Log.i("JSON",x);
             res=findViewById(R.id.textView2);
            try{
                JSONObject jsonObject=new JSONObject(x);
                String weatherinfo=jsonObject.getString("weather");
                Log.i("weather info",weatherinfo);

                        JSONArray arr=new JSONArray(weatherinfo);
                        StringBuilder message= new StringBuilder();
                        for(int i=0;i<arr.length();i++)
                        {
                            JSONObject part=arr.getJSONObject(i);
                            String s1=part.getString("main");
                            String s2=part.getString("description");
                           /* Log.i("main",s1);
                            Log.i("description",s2);*/
                            if(!s1.equals("") && !s2.equals(""))
                            {
                              message.append(s1).append(": ").append(s2).append("\r\n");
                            }

                        }
                        if(!message.toString().equals(""))
                            res.setText(message.toString());

                        else
                        {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
            }catch (Exception e)
            {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }



    }

}