package com.example.riccardo.lomux;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements ThreadCompleteListener{
    String JSON_STRING, JSON_ITINERARY;
    ProgressDialog progressDialog;
    int n_thread=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!isOnline()){
            Toast.makeText(getApplicationContext(),"No connection detected, check connectivity status and retry", Toast.LENGTH_LONG).show();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        else {
            progressDialog = ProgressDialog.show(MainActivity.this, "Please wait ...",  "Update information from database ...", true);
            progressDialog.setCancelable(false);
            NotifyingThread info= new ThreadInfo();
            NotifyingThread itinerary= new ThreadItinerary();
            info.addListener(this);
            itinerary.addListener(this);
            info.start();
            itinerary.start();
             //  new BackgroundTaskInfo().execute();
           // new BackgroundTaskItinerary().execute();
        }
    }

    @Override
    public void notifyOfThreadComplete(Thread thread) {
        n_thread--;
        if(n_thread==0)
        {
            progressDialog.dismiss();
        }
    }

    class ThreadInfo extends NotifyingThread{


     @Override
     public void doRun() {
         String json_url="https://test-db-lomux.000webhostapp.com/json_get_info.php";
         try {
             URL url = new URL(json_url);
             HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
             InputStream inputStream = httpURLConnection.getInputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             StringBuilder stringBuilder = new StringBuilder();
             while ((JSON_STRING = bufferedReader.readLine()) != null) {
                 stringBuilder.append(JSON_STRING + "\n");
             }

             bufferedReader.close();
             inputStream.close();
             httpURLConnection.disconnect();
             JSON_STRING= stringBuilder.toString().trim();

         } catch (MalformedURLException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }
     }
 }

    class ThreadItinerary extends NotifyingThread{


        @Override
        public void doRun() {
            String json_url = "https://test-db-lomux.000webhostapp.com/json_get_itinerary.php";

            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_ITINERARY = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_ITINERARY + "\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                JSON_ITINERARY= stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class BackgroundTaskInfo extends AsyncTask<Void, Void, String> {
        String json_url;

        @Override
        protected void onPreExecute() {
            json_url = "https://test-db-lomux.000webhostapp.com/json_get_info.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_STRING = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_STRING + "\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            JSON_STRING= result;
        }
    }

    class BackgroundTaskItinerary extends AsyncTask<Void, Void, String> {
        String json_url;

        @Override
        protected void onPreExecute() {
            json_url = "https://test-db-lomux.000webhostapp.com/json_get_itinerary.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_ITINERARY = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_ITINERARY + "\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            JSON_ITINERARY= result;
        }
    }

    public void openLomuxMap(View view) {
        if(JSON_STRING==null|| JSON_ITINERARY==null)
        {
            Toast.makeText(getApplicationContext(),"Wait Database connection", Toast.LENGTH_LONG).show();
        }
        else {
            Intent intent = new Intent(this, LomuxMapActivity.class);
            intent.putExtra("json_data", JSON_STRING);
            intent.putExtra("json_itinerary", JSON_ITINERARY);
            startActivity(intent);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
