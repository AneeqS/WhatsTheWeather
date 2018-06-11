package com.aneeqshah.whatstheweather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
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
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    Pattern p;
    Matcher m;
    String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = findViewById(R.id.editText);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {


        //String... strings. think of it as an array.
        @Override
        protected String doInBackground(String... strings) {

            String result = "";

            URL url;
            //Like a browser
            HttpURLConnection httpURLConnection = null;

            try {

                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();

                //Hold input data
                InputStream in = httpURLConnection.getInputStream();

                //Read input stream data. We do this one character at a time.
                InputStreamReader reader = new InputStreamReader(in);


                int data = reader.read();

                //When data is finished reading all the data from the reader, it will have a value on -1
                while (data != -1) {

                    //The current character being downloaded from the url
                    char curr = (char) data;

                    result += curr;

                    //Make date move to the next character.
                    data = reader.read();

                }

                return result;

            } catch (Exception e) {

                e.printStackTrace();
                return "Failed";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //After getting the data from the website, then convert the string into JSON data.

            try {

                JSONObject jsonObject = new JSONObject(result);

                String weatherInfo = jsonObject.getString("weather");
                String degreesInfo = jsonObject.getString("main");
                String humidity = "";
                String visibility = jsonObject.getString("visibility");

                float visib = Float.valueOf(visibility);
                visib = (Math.round(visib * 0.00062137));

                Log.i("degreesInfo",degreesInfo);
                Log.i("Weather", weatherInfo);

                p = Pattern.compile("temp\":(.*?),");
                m = p.matcher(degreesInfo);

                while (m.find()){
                    Log.i("degrees ", m.group(1));
                    float degrees = Float.valueOf(m.group(1));
                    degrees = (float) (Math.round((degrees * (9.0/5.0)) - 459.67));
                    Log.i("fahrenheit", String.valueOf(degrees));
                    message += "Degrees: " + String.valueOf(degrees) + "°F" + "\r\n";
                }

                p = Pattern.compile("humidity\":(.*?),");
                m = p.matcher(degreesInfo);

                while (m.find()){
                    Log.i("Humidity: ", m.group(1));
                    humidity = m.group(1);
                }

                JSONArray jsonArray = new JSONArray(weatherInfo);

                for(int i = 0; i<jsonArray.length(); i++) {
                    JSONObject jsonPart = jsonArray.optJSONObject(i);

                    String main = "";
                    String description = "";

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if(main != "" && description != "" && humidity != "" && visibility != ""){
                        message += "Main: " + main + "\r\n" + "Description: " + description + "\r\n"
                                + "Humidity: " + humidity + "%\r\n" + "Visibility: " + String.valueOf(visib) + "mi";
                    }
                }

                if(message != ""){
                    Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                    intent.putExtra("info", message);
                    startActivity(intent);

                }else{

                    Toast.makeText(getApplicationContext(), "Could Not Find Weather", Toast.LENGTH_LONG);

                }


            } catch (JSONException e) {

                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could Not Find Weather", Toast.LENGTH_LONG);
            }
        }
    }

    public void getData(View view){

        Log.i("City Name", cityName.getText().toString());

        //Closes the keyboard when the button is pressed
        View view1 = this.getCurrentFocus();
        if (view1 != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        try {

            String encodedCity = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCity + "&appid=4cf5266bbe4c941e70ff2dc1fa1f9336");

        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), "Could Not Find Weather", Toast.LENGTH_LONG);
            e.printStackTrace();

        }

        Intent intent = new Intent(this, SecondActivity.class);
        startActivity(intent);

    }
}
