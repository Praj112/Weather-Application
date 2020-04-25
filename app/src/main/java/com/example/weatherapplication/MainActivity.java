package com.example.weatherapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.security.auth.login.LoginException;

public class MainActivity extends AppCompatActivity {

    EditText cityname;
    TextView resulttextview;

    public void findweather(View view)
    {
        Log.i("city name",cityname.getText().toString() );
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //to hide the keyboard after typing
        mgr.hideSoftInputFromWindow(cityname.getWindowToken(),0);
        try {
            String enCodedCityname = URLEncoder.encode(cityname.getText().toString(),"UTF-8");

            downloadtask task = new downloadtask();
            task.execute("https://api.openweathermap.org/data/2.5/weather?q="+ enCodedCityname +"&appid=ead44115f8a8d1cfe57c5d86aacf4b98");
        }
        catch (UnsupportedEncodingException e) {
            Toast.makeText(getApplicationContext(),"Could not find the Weather",Toast.LENGTH_LONG);
        }
//        https://api.openweathermap.org/data/2.5/weather?q=Bangalore&appid=ead44115f8a8d1cfe57c5d86aacf4b98
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resulttextview = findViewById(R.id.resulttextview);
        cityname = findViewById(R.id.cityname);
    }

    public class downloadtask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }
            catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Could not find the Weather",Toast.LENGTH_LONG);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                String message = "";
                JSONObject jsonObject = new JSONObject(result);
                String weatherinfo = jsonObject.getString("weather");
                JSONArray jsonArray = new JSONArray(weatherinfo);
                for (int i =0; i<jsonArray.length(); i++)
                {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String main = "";
                    String description = "";
                    main = jsonObject1.getString("main");
                    description = jsonObject1.getString("description");

                    if(main !="" && description !="") {
                        message += main + ":" + description + "\r\n";
                    }
                }
                if(message !="")
                {
                    resulttextview.setText(message);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Could not find the Weather",Toast.LENGTH_LONG);
                }
            }
            catch (JSONException e) {
                Toast.makeText(getApplicationContext(),"Could not find the Weather",Toast.LENGTH_LONG);
            }
        }
    }
}