package com.example.tai.weatherforecast;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.tai.weatherforecast.R.drawable.extreme;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;
    ChangeTheWeather changeTheWeather;
    double latitude, longitude;
    TextView userclimate, usertemperature, usercountrycode, usercity;
    RelativeLayout relativeLayout;
    Button button;
    ImageView imageView;
    DownloadTask task;
    JSONApiManipulation jsonApiManipulation;
    String temp,weathermain, weatherdescription, city, country, firstpart, apiid,id,weatherid = null;
    int count,testbackground = 0;

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        task = new DownloadTask();
        userclimate = (TextView) findViewById(R.id.userclimate);
        usertemperature = (TextView) findViewById(R.id.usertemperature);
        usercity = (TextView) findViewById(R.id.usercity);
        usercountrycode = (TextView) findViewById(R.id.usercountrycode);
        //button = (Button) findViewById(R.id.changeBackGround);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativelayout);
        imageView = (ImageView) findViewById(R.id.imageView);
    }
    protected void onStart(){
        super.onStart();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.i("Test Location", "Latitude: " + latitude + "" + "Longitude: " + longitude);
                firstpart = "http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude;
                apiid = "&appid=8a9cbc8db325a1ff4287792759faf76c";
                Log.i("test coordinates", "" + latitude + " " + longitude);
                if (count == 0) {
                    task.execute(firstpart + apiid);
                    count++;
                }
                Log.i("test", "task executed");
                Log.i("test", "finished successfully");
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }
            @Override
            public void onProviderEnabled(String s) {
            }
            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Log.i("WTF DUDE","YOU HERE YET?");
        if(location == null){
            Log.i("ERROR","NETWORK_PROVIDER NOT AVAILABLE");
            userclimate.setText("OPEN YOUR INTERNET");
        }else{
            locationListener.onLocationChanged(location);
        }
    }
    public class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (true) {
                    if (data == -1) {
                        break;
                    }
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                jsonApiManipulation = new JSONApiManipulation(result);
                temp = jsonApiManipulation.getJSONObjectFromJsonObject("main", "temp");
                weathermain = jsonApiManipulation.getJSONObjectFromJsonArray("weather", "main");
                weatherdescription = jsonApiManipulation.getJSONObjectFromJsonArray("weather", "description");
                city = jsonApiManipulation.getJSONObject("name");
                id = jsonApiManipulation.getJSONObjectFromJsonArray("weather","id");
                country = jsonApiManipulation.getJSONObjectFromJsonObject("sys", "country");
                changeTheWeather = new ChangeTheWeather(id);
                weatherid = changeTheWeather.idToWeather();
                String celcius = JSONApiManipulation.kelvinToCelcius(temp) + " \u2103";
                userclimate.setText(weatherdescription);
                usertemperature.setText(celcius);
                usercity.setText(city);
                usercountrycode.setText(country);
                changeBackground(weatherid);
                Log.i("test", "test id " + id + "test method " + weatherid);
                Log.i("test", "JSON finished successfully");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /*public void onclick(View view){
        switch (testbackground){
            case 0 : imageView.setImageResource(R.drawable.extreme); testbackground++; break;
            case 1: imageView.setImageResource(R.drawable.clear);testbackground++; break;
            case 2: imageView.setImageResource(R.drawable.misty);testbackground++; break;
            case 3: imageView.setImageResource(R.drawable.rain); testbackground++; break;
            case 4: imageView.setImageResource(R.drawable.snow); testbackground++; break;
            case 5: imageView.setImageResource(R.drawable.thunderstorm); testbackground++; break;
            default: imageView.setImageResource(R.drawable.cloudy); break;
        }
    }*/
    public void changeBackground(String weatherid){
        switch (weatherid){
            case "thunderstorm": imageView.setImageResource(R.drawable.thunderstorm); break;
            case "rain": imageView.setImageResource(R.drawable.rain); break;
            case "snow": imageView.setImageResource(R.drawable.snow); break;
            case "misty": imageView.setImageResource(R.drawable.misty); break;
            case "clear": imageView.setImageResource(R.drawable.clear);break;
            case "cloudy": imageView.setImageResource(R.drawable.cloudy); break;
            case "extreme": imageView.setImageResource(R.drawable.extreme); break;
            default: imageView.setImageResource(R.drawable.cloudy); break;
        }
    }
}
