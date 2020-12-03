package com.example.teller2;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class SecondActivity extends Activity {
    ArrayList<HashMap<String, String>> data;
    String data_parsed="";
    String single_parsed="";
    private ProgressDialog pDialog;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layout);
        data = new ArrayList<>();
        lv = findViewById(R.id.fetch);
        new fetchData().execute();
//        fetchData process = new fetchData();
//        process.execute();

    }

    public class fetchData extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SecondActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            try
            {
                URL url = new URL("http://stimakonnekt.herokuapp.com/api/Transactionhistory/");
//                URL url = new URL("http://anothertrial.herokuapp.com/appAPI/transactions/");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
                String line = "";
                while(line != null)
                {
                    line = bufferedReader.readLine();
                    data_parsed = data_parsed +line ;
                }
                JSONArray JA = new JSONArray(data_parsed);
                for(int i =0;i<JA.length();i++)
                {
                    JSONObject JO = (JSONObject) JA.getJSONObject(i);
//                single_parsed = "\n"+"Units: "+ JO.get("units") + "KSH: " + JO.get("ksh")+
//                                 "Day : " + JO.get("day") + "Time : "+ JO.get("time");
                Log.e("something",single_parsed);
//                single_parsed = "\n"+"Meter_no: "+ JO.get("meter_no") + "\nKSH: " + JO.get("amount");
//                data_parsed = data_parsed + single_parsed;
//                    String ksh=JO.getString("amount");
                    String ksh=JO.getString("ksh");
                    String units= JO.getString("units");
//                    String day = JO.getString("date");
                    String day = JO.getString("day");
//                    String time = JO.getString("time_r");
                    String time = JO.getString("time");
                    String ID = JO.getString("id");
                    HashMap<String, String> history = new HashMap<>();
                    history.put("ksh",ksh);
                    history.put("id",ID);
                    history.put("units",units);
                    history.put("day",day);
                    history.put("time",time);
                    data.add(history);
                }
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pDialog.isShowing())
                pDialog.dismiss();
            ListView lv = (ListView) findViewById(R.id.fetch);
            ListAdapter adapter = new SimpleAdapter(SecondActivity.this, data, R.layout.lister,new String[]{"id","ksh","units","day","time"}, new int[]{R.id.ID,R.id.ksh, R.id.units, R.id.day, R.id.time});
            lv.setAdapter(adapter);
        }
    }
//            ArrayList<HashMap<String, String>> userList = new ArrayList<>();

}
