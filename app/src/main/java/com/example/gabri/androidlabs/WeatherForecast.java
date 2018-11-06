package com.example.gabri.androidlabs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class WeatherForecast extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        ProgressBar loaderView = findViewById(R.id.loading);
        loaderView.setVisibility(View.VISIBLE);

        ForecastQuery forecastQuery = new ForecastQuery();
        forecastQuery.execute();
    }



    class ForecastQuery extends AsyncTask<String, Integer, String>{

        String current, min, max, wind;
        String iconName;
        String iconURL;
        Bitmap image;

        ProgressBar loaderView = findViewById(R.id.loading);


        @Override
        protected String doInBackground(String... s) {

            String web = "http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=d99666875e0e51521f0040a3d97d0f6a&mode=xml&units=metric";

            try {
                URL url = new URL(web);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream input = conn.getInputStream();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(input, "UTF-8");

                while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {

                    if (xpp.getEventType() == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("temperature")) {
                            current = xpp.getAttributeValue(null, "value");

                            publishProgress(25);

                            min = xpp.getAttributeValue(null, "min");

                            publishProgress(50);

                            max = xpp.getAttributeValue(null, "max");

                            publishProgress(75);

                        } else if (xpp.getName().equals("speed")) {
                            wind = xpp.getAttributeValue(null, "value");
                        } else if (xpp.getName().equals("weather")){
                            iconName = xpp.getAttributeValue(null, "icon");
                            iconURL = "http://openweathermap.org/img/w/" + iconName + ".png";

                            if(fileExistance(iconName+".png")){

                                Log.i("downloaded", iconURL);
                                Log.i("downloaded", "Downloaded, getting from storage: " + iconName + ".png");

                                FileInputStream fis = null;

                                image = BitmapFactory.decodeStream(fis);

                            } else {
                                Log.i("downloading", iconURL);
                                Log.i("downloading", "Downloading from internet: " + iconName+".png");

                                image  = HttpUtils.getImage(iconURL);
                                FileOutputStream outputStream = openFileOutput( iconName + ".png", Context.MODE_PRIVATE);
                                image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                                outputStream.flush();
                                outputStream.close();
                            }

                            publishProgress(100);

                        }
                    }
                    xpp.next();
                }

            } catch (Exception e) {

            }
            return null;
        }


        boolean fileExistance(String fname){
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();   }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            loaderView.setVisibility(View.VISIBLE);
            loaderView.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            ImageView imageView = findViewById(R.id.image);
            imageView.setImageBitmap(image);

            TextView currentView = findViewById(R.id.currentTemp);
            currentView.setText(String.format(getResources().getString(R.string.current), current));

            TextView min = findViewById(R.id.minTemp);
            min.setText(String.format(getResources().getString(R.string.min), this.min));

            TextView max = findViewById(R.id.maxTemp);
            max.setText(String.format(getResources().getString(R.string.max), this.max));

            TextView wind = findViewById(R.id.windSpeed);
            wind.setText(String.format(getResources().getString(R.string.speed), this.wind));

            loaderView.setVisibility(View.INVISIBLE);



        }
    }
}


