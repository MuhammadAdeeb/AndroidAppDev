package com.example.weather;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class MyRunnable implements Runnable{

    private static final String TAG = "MyRunnable";
    private final MainActivity mainActivity;
    private final Double lat;
    private final Double lng;
    private final boolean fahrenheit;
    private String symbol;

    private static final String weatherURL = "https://api.openweathermap.org/data/2.5/onecall?";
    // https://api.openweathermap.org/data/2.5/onecall?lat=#########&lon=#########
    // &appid=your_api_key&units=units_selection&lang=en&exclude=minutely
    private static final String yourAPIKey = "598e329789d9666d33a43972c46e0ba8";

    MyRunnable(MainActivity mainActivity, Double lat, Double lng, boolean fahrenheit) {
        this.mainActivity = mainActivity;
        this.lat = lat;
        this.lng = lng;
        this.fahrenheit = fahrenheit;
        if (fahrenheit == true){
            symbol = "F";
        }else{
            symbol = "C";
        }

    }

    @Override
    public void run() {
        Log.d(TAG, "run: Starting Run");

        Uri.Builder buildURL = Uri.parse(weatherURL).buildUpon();
        buildURL.appendQueryParameter("lat", String.valueOf(lat));
        buildURL.appendQueryParameter("lon", String.valueOf(lng));
        buildURL.appendQueryParameter("appid", yourAPIKey);
        buildURL.appendQueryParameter("units", (fahrenheit ? "imperial" : "metric"));
        buildURL.appendQueryParameter("lang", "en");
        buildURL.appendQueryParameter("exclude", "minutely");

        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(urlToUse);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.connect();

            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                InputStream is = connection.getErrorStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                handleError(sb.toString());
                return;
            }

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            handleResults(null);
            return;
        }
        handleResults(sb.toString());
    }

    public void handleError(String s) {
        String msg = "Error: ";
        try {
            JSONObject jObjMain = new JSONObject(s);
            msg += jObjMain.getString("message");

        } catch (JSONException e) {
            msg += e.getMessage();
        }

        String finalMsg = String.format("%s ", msg);
        //mainActivity.runOnUiThread(() -> mainActivity.handleError(finalMsg));
        // TODO: Add a handleError method in MA
    }

    public void handleResults(final String jsonString) {

        Log.d(TAG, "handleResults: " + jsonString);

        Weather w = parseJSON(jsonString);
        //String w = ""; //TODO: remove str w and create a Weather obj
        mainActivity.runOnUiThread(() -> mainActivity.receiveData(w));
    }

    private Weather parseJSON(String s) {
        //TODO: return a weather obj!
        //Weather wr = new Weather("", "", "", "", "", "" ,"", "" , "", "", "", 0, 0 ,0, "","", "", "", "", "", "",);
        Weather wr = null;
        
        //Weather(String lat, String lng, String city, String dt, String sr, String ss, String tp, String flike, String pressure,
        //            String humidity, String uv, int cloud, int visibility, int wspd, String wdir, String icon, String description,
        //            String mtemp,String dtemp,String etemp,String ntemp, String[][] hourly, String[][] daily)
        //
        try {
            JSONObject jObjMain = new JSONObject(s);

            String jlat = String.valueOf(jObjMain.getDouble("lat"));
            String jlng = String.valueOf(jObjMain.getDouble("lon"));
            // TODO: skipped timezone, timezone_offset

            JSONObject current = jObjMain.getJSONObject("current");

            // Infor from Current JObj:
            long dt = current.getLong("dt");
            String jdate = new SimpleDateFormat("E MMM dd hh:mm a, yyyy", Locale.getDefault()).format(new Date(dt * 1000));
            String cur_hr = new SimpleDateFormat("hh a", Locale.getDefault()).format(new Date(dt * 1000)); // USed for the 48 hr forecast
            long sr = current.getLong("sunrise");
            String jsunrise = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date(sr * 1000));
            long ss = current.getLong("sunset");
            String jsunset = new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date(ss * 1000));

            Log.d(TAG, "parseJSON: DATE, SR, SS: " + jdate + ",  " + jsunrise + ",  " + jsunset);

            String jtemp = String.valueOf((int)current.getDouble("temp")) + "°" + symbol; // f°
            String jflike = String.valueOf((int)current.getDouble("feels_like")) + "°" + symbol;
            String jpressure = String.valueOf(current.getInt("pressure"));
            String jhumidity = String.valueOf(current.getInt("humidity")) + "%";
            String juv = String.valueOf(current.getDouble("uvi"));
            //String jpop = String.valueOf()
            int jclouds = current.getInt("clouds");
            int visibility = current.getInt("visibility");
            String jwind_sp = String.valueOf(current.getDouble("wind_speed"));  //(int)
            double jwind_dg_db = current.getDouble("wind_deg"); //They tell you direction
            String jdir = getDirection(jwind_dg_db);
            String jwind_dg = String.valueOf(jwind_dg_db);

            // TODO: Optional wind_gust

            // Weather obj: id, main, description, ICON!
            JSONArray jweather = current.getJSONArray("weather");
            JSONObject jw = jweather.getJSONObject(0);
            String main_d = jw.getString("main");
            Log.d(TAG, "parseJSON1: CURRENT MAIN: " + main_d);
            String jicon = jw.getString("icon");
            String jdesc = jw.getString("description"); // Tied to jclouds! Check instucs to see how to combine and display!

            // TODO: Optional rain, snow

            /*
            // Getting the 48 hr forecast and storing in a 2D [48][5] arr
            JSONArray jhourly = jObjMain.getJSONArray("hourly");
            String[][] hourly = new String[48][5];
            String[] desc_lst = {"Today", "Tomorrow", "Overmorrow"};
            int hr_mark = Integer.parseInt(new SimpleDateFormat("HH", Locale.getDefault()).format(new Date(dt * 1000)));
            int ctr = 0;
            int br1 = 24 - hr_mark;
            int br2 = br1 + 24;

            Log.d(TAG, "parseJSON: HOURLY LEN: " + jhourly.length());
            for(int i = 0; i < 48; i++){
                JSONObject temp_h = jhourly.getJSONObject(i);

                if (i < br1){
                    hourly[i][0] = "Today";
                }else if (i >= br1 && i < br2){
                    hourly[i][0] = "";
                }else{
                    hourly[i][0] = "Overmorrow";
                }
                long temp_t = temp_h.getLong("dt");
                String temp_time = new SimpleDateFormat("hh a", Locale.getDefault()).format(new Date(temp_t * 1000));
                hourly[i][1] = temp_time;
                hourly[i][2] = String.valueOf((int)temp_h.getDouble("temp")) + "°" + symbol;

                JSONArray temp_w = temp_h.getJSONArray("weather");
                JSONObject temp_wo = temp_w.getJSONObject(0);
                hourly[i][3] = temp_wo.getString("icon");
                hourly[i][4] = temp_wo.getString("description");
            }

            Log.d(TAG, "parseJSON: 48 HR Arr: " + hourly[30][0] + "  "  + hourly[30][1] + "  "  + hourly[30][2] + "  "  + hourly[29][3] + "  "  + hourly[29][4]);
            //Log.d(TAG, "parseJSON: last elem: " + hourly[46][0]);

             */

            String jmon = "";
            String jaft = "";
            String jeve = "";
            String jngt = "";
            String fd = null;
            String sd= null;
            // Loading wkly forecast
            String[][] daily = new String[7][10];
            JSONArray jdaily = jObjMain.getJSONArray("daily");
            for(int i = 0; i < 7; i++){
                JSONObject temp_daily = jdaily.getJSONObject(i); // DK if this shows todya or tmrw?
                long daily_dt = temp_daily.getLong("dt");
                String daily_date = new SimpleDateFormat("EEEE, MM/dd", Locale.getDefault()).format(new Date(daily_dt * 1000));
                if (i == 1){
                    fd = new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date(daily_dt * 1000));
                }
                if ( i == 2){
                    sd = new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date(daily_dt * 1000));
                }
                //Log.d(TAG, "parseJSON: " + daily_date);
                daily[i][0] = daily_date;
                JSONObject tmp_rng = temp_daily.getJSONObject("temp");

                String daily_trange = String.valueOf((int) tmp_rng.getDouble("max")) + "°" + symbol + "/" + String.valueOf((int)tmp_rng.getDouble("min")) + "°" + symbol;
                daily[i][1] = daily_trange;


                // 4: morn, day, eve, night
                String daily_mon = String.valueOf((int) tmp_rng.getDouble("morn"));
                String daily_day = String.valueOf((int) tmp_rng.getDouble("day"));
                String daily_eve = String.valueOf((int) tmp_rng.getDouble("eve"));
                String daily_night = String.valueOf((int) tmp_rng.getDouble("night"));

                JSONArray daily_weather = temp_daily.getJSONArray("weather");
                JSONObject dw0 = daily_weather.getJSONObject(0);
                String main_d2 = dw0.getString("main");

                String daily_desc = dw0.getString("description");
                Log.d(TAG, "parseJSON1: Desc + Hourly_main: " + daily_desc + "  " + main_d);
                String daily_icon = "_" + dw0.getString("icon");

                String daily_pop = String.valueOf(temp_daily.getDouble("pop"));
                String daily_uvi = String.valueOf(temp_daily.getDouble("uvi"));

                if (i == 0){
                    jmon = daily_mon + "°" + symbol;
                    jaft = daily_day + "°" + symbol;
                    jeve = daily_eve + "°" + symbol;
                    jngt = daily_night + "°" + symbol;

                    daily[i][2] = daily_desc;
                    daily[i][3] = "(" + daily_pop + "% precip.)";
                    daily[i][4] = "UV Idx: " + daily_uvi;
                    daily[i][5] = jmon;
                    daily[i][6] = jaft;
                    daily[i][7] = jeve;
                    daily[i][8] = jngt;
                    daily[i][9] = daily_icon;
                }else {
                    daily[i][2] = daily_desc;
                    daily[i][3] = "(" + daily_pop + "% precip.)";
                    daily[i][4] = "UV Idx: " + daily_uvi;
                    daily[i][5] = daily_mon + "°" + symbol;
                    daily[i][6] = daily_day + "°" + symbol;
                    daily[i][7] = daily_eve + "°" + symbol;
                    daily[i][8] = daily_night + "°" + symbol;
                    daily[i][9] = daily_icon;
                }
            }
            Log.d(TAG, "parseJSON: 1st Content: " + daily[0][0] + "  "  + daily[0][1] + "  "  + daily[0][2] + "  " + daily[0][3] + "  " + daily[0][4] + "  " + daily[0][5] + "  " + daily[0][6] + "  " + daily[0][7] + "  " + daily[0][8] + "  " + daily[0][9] + "  ");
            Log.d(TAG, "parseJSON: 2nd Content: " + daily[1][0] + "  "  + daily[1][1] + "  "  + daily[1][2] + "  " + daily[1][3] + "  " + daily[1][4] + "  " + daily[1][5] + "  " + daily[1][6] + "  " + daily[1][7] + "  " + daily[1][8] + "  " + daily[1][9] + "  ");

            // Getting the 48 hr forecast and storing in a 2D [48][5] arr
            JSONArray jhourly = jObjMain.getJSONArray("hourly");
            String[][] hourly = new String[48][5];
            String[] desc_lst = {"Today", "Tomorrow", "Overmorrow"};
            int hr_mark = Integer.parseInt(new SimpleDateFormat("HH", Locale.getDefault()).format(new Date(dt * 1000)));
            int ctr = 0;
            int br1 = 24 - hr_mark;
            int br2 = br1 + 24;

            Log.d(TAG, "parseJSON: HOURLY LEN: " + jhourly.length());
            for(int i = 0; i < 48; i++){
                JSONObject temp_h = jhourly.getJSONObject(i);

                if (i < br1){
                    hourly[i][0] = "Today";
                }else if (i >= br1 && i < br2){
                    hourly[i][0] = fd;
                }else{
                    hourly[i][0] = sd;
                }
                long temp_t = temp_h.getLong("dt");
                String temp_time = new SimpleDateFormat("hh a", Locale.getDefault()).format(new Date(temp_t * 1000));
                hourly[i][1] = temp_time;
                hourly[i][2] = String.valueOf((int)temp_h.getDouble("temp")) + "°" + symbol;

                JSONArray temp_w = temp_h.getJSONArray("weather");
                JSONObject temp_wo = temp_w.getJSONObject(0);
                hourly[i][3] = temp_wo.getString("icon");
                hourly[i][4] = temp_wo.getString("description");
            }

            Log.d(TAG, "parseJSON: 48 HR Arr: " + hourly[30][0] + "  "  + hourly[30][1] + "  "  + hourly[30][2] + "  "  + hourly[29][3] + "  "  + hourly[29][4]);
            //Log.d(TAG, "parseJSON: last elem: " + hourly[46][0]);

            Weather w = new Weather(jlat, jlng,"CITY", jdate, jsunrise, jsunset, jtemp, jflike, jpressure, jhumidity, juv, jclouds, visibility, jwind_sp, jdir, jicon, jdesc, jmon, jaft, jeve, jngt, hourly, daily);
            wr = w;
            //Weather(String lat, String lng, String city, String dt, String sr, String ss, String tp, String flike, String pressure,
            //            String humidity, String uv, int cloud, int visibility, int wspd, String wdir, String icon, String description,
            //            String mtemp,String dtemp,String etemp,String ntemp, String[][] hourly, String[][] daily)
            //

        } catch (Exception e) {
            e.printStackTrace();
        }
        return wr;
        //return new Weather(); TODO: rem ret false and return a weather obj
    }

    private String getDirection(double degrees) {
        if (degrees >= 337.5 || degrees < 22.5)
            return "N";
        if (degrees >= 22.5 && degrees < 67.5)
            return "NE";
        if (degrees >= 67.5 && degrees < 112.5)
            return "E";
        if (degrees >= 112.5 && degrees < 157.5)
            return "SE";
        if (degrees >= 157.5 && degrees < 202.5)
            return "S";
        if (degrees >= 202.5 && degrees < 247.5)
            return "SW";
        if (degrees >= 247.5 && degrees < 292.5)
            return "W";
        if (degrees >= 292.5 && degrees < 337.5)
            return "NW";
        return "X"; // We'll use 'X' as the default if we get a bad value
    }
}
