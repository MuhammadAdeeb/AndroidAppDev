package com.example.weather;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

@SuppressLint("ParcelCreator")
public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener, Parcelable {

    private static final String TAG = "MainActivity";
    private boolean running;
    private boolean fahrenheit;
    private RecyclerView recyclerView; // Layout's recyclerview
    private HourlyAdapter hourly_adap; // Data to recyclerview adapter
    private Double lat;
    private Double lng;
    private String cty_st;
    private Weather globw;
    private SharedPreferences pref;
    private SharedPreferences.Editor prefsEditor;
    private Menu mu;
    private SwipeRefreshLayout swiper;

    //android:configChanges="orientation|keyboardHidden|screenSize"
    // private ActivityResultLauncher<Intent> arl; //New Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("OpenWeather App");
        pref = getSharedPreferences("PREF", Context.MODE_PRIVATE);
        prefsEditor = pref.edit();

        if (pref.contains("lat") && pref.contains(("lng")) && pref.contains("fahrenheit")){
            lat = new Double(pref.getString("lat", "41.8675766"));
            lng = new Double(pref.getString("lng", "-87.616232"));
            fahrenheit = pref.getBoolean("fahrenheit", true); //default to true; can assign it using the saved setting!
            cty_st = pref.getString("cty_st", "Chicago, IL");
        } else {
            lat = 41.8675766;
            lng = -87.616232;
            fahrenheit = true; //default to true; can assign it using the saved setting!
            cty_st = "Chicago, IL";
        }
        globw = null;

        if (doNetCheck() == false){
            TextView tv = findViewById(R.id.DateLoc_tv);
            tv.setText("No Internet Connection");
        }else{
            Log.d(TAG, "onCreate: b4 calling download");
            download();
        }
        swiper = findViewById(R.id.swiper);

        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // textView.setText(String.format(Locale.getDefault(), "Swipe Refresh Count: %d", ctr++));
                if (doNetCheck() == false){
                    // TextView tv = findViewById(R.id.DateLoc_tv);
                    // tv.setText("No Internet Connection");
                    Toast.makeText(MainActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d(TAG, "onCreate: b4 calling download");
                    download();
                }
                swiper.setRefreshing(false); // This stops the busy-circle
            }
        });
        // double [] d = getLatLon(cty_st);
        //lat = d[0];
        //lng = d[1]; // default vals of Chicago; can assign the saved setting!
        // arl = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::backFromAct);// this::receiveNote
    }

    private boolean doNetCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;  // No connection
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null)
            return false; // No connection

        // Check for active live connection
        boolean isConnected = activeNetwork.isConnectedOrConnecting();

        // Check if the connection is metered (possibly paid data)
        boolean isMetered = cm.isActiveNetworkMetered();
        return isConnected;
    }


    //public void backFromAct(ActivityResult result){

    // Next two methods are for Options Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ma_menu, menu);
        mu = menu;
        if (fahrenheit == true){
            int i_id = this.getResources().getIdentifier("units_f", "drawable", this.getPackageName());
            mu.getItem(0).setIcon(i_id);
        }else{
            int i_id = this.getResources().getIdentifier("units_c", "drawable", this.getPackageName());
            mu.getItem(0).setIcon(i_id);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.metric) {
            // Retrieve weather info using new metric -> update the whole screen
            if (doNetCheck() == false){
                Toast.makeText(MainActivity.this, "Internet required to update metric data! ", Toast.LENGTH_LONG).show();
            }else {
                // i = item;
                if (fahrenheit == true) {
                    //change to Celsius
                    fahrenheit = false;
                    //Log.d(TAG, "receiveData: " + weather.getIcon());
                    //icon.setImageResource(icon_id);
                    download();
                    // int i_id = this.getResources().getIdentifier("units_c", "drawable", this.getPackageName());
                    // item.setIcon(i_id);
                } else {
                    //change to Fahrenheit
                    fahrenheit = true;
                    download();
                    // int i_id = this.getResources().getIdentifier("units_f", "drawable", this.getPackageName());
                    // item.setIcon(i_id);
                }
                prefsEditor.putBoolean("fahrenheit", fahrenheit);
                prefsEditor.apply();
                // return true;
            }
            return true;
        } else if (item.getItemId() == R.id.calendar) {
            //Do Calendar icon stuff
            if (doNetCheck() == false && globw == null){
                Toast.makeText(MainActivity.this, "Internet required for weekly forecast! ", Toast.LENGTH_LONG).show();
                return true;
            }else {
                Intent intent = new Intent(this, WeeklyActivity.class);

                intent.putExtra("weekly", globw.getDaily());
                intent.putExtra("cty", cty_st);
                Log.d(TAG, "onOptionsItemSelected: b4 MA Intent");
                // intent.putExtra("act", (Parcelable) MainActivity.this);
                Log.d(TAG, "onOptionsItemSelected: After MA Intent/b4 new Activity Launch");
                startActivity(intent);
                Log.d(TAG, "onOptionsItemSelected: After new Act Launch");
                return true;
            }
        } else if (item.getItemId() == R.id.location) {
            // Create Dialog -> check if valid location -> New thread -> Update the whole screen
            // Single input value dialog

            if (doNetCheck() == false){
                Toast.makeText(MainActivity.this, "Internet required to change location! ", Toast.LENGTH_LONG).show();
            }else {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                // Create an edittext and set it to be the builder's view
                final EditText et = new EditText(this);
                et.setInputType(InputType.TYPE_CLASS_TEXT);
                et.setGravity(Gravity.CENTER_HORIZONTAL);
                builder.setView(et);

                // lambda can be used here (as is below)
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // tv1.setText(et.getText());
                        String entered_loc = et.getText().toString();
                        Log.d(TAG, "onClick: " + entered_loc);
                        String form = getLocationName(entered_loc);

                        if (form != null) {
                            //US city or International city
                            double[] ret_gll = getLatLon(form);
                            if (ret_gll != null) {  //Shouldn't be..?
                                //New thread
                                cty_st = getLocationName(form);
                                lat = ret_gll[0];
                                lng = ret_gll[1];
                                Log.d(TAG, "onClick: B4 Download w/ new location");
                                prefsEditor.putString("lat", String.valueOf(lat));
                                prefsEditor.putString("lng", String.valueOf(lng));
                                prefsEditor.putString("cty_st", cty_st);
                                // prefsEditor.pu
                                prefsEditor.apply();
                                download();
                            }
                        } else {
                            makeT();
                        }
                    }
                });
                // lambda can be used here (as is below)
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                builder.setMessage("For US: Enter 'City' or 'City, State'\nFor International Cities: Enter 'City, Country'");
                builder.setTitle("Enter a Location");

                AlertDialog dialog = builder.create();
                dialog.show();

            }
            return true;  // Not sure if this would make dialog exit b4 data entered
        }else {
            return super.onOptionsItemSelected(item);
        }
    }
    //OPTION MENU METHODS FINISHED

    public void makeT(){
        Toast.makeText(this, "Invalid Address! Please try again", Toast.LENGTH_LONG).show();
    }
    public void download(){
        if (running){ //makes sure no new thread is ran if user clicks on weather update request again
            Toast.makeText(this, "Wait for Task to complete", Toast.LENGTH_SHORT).show();
            return;
        }
        running = true;
        Log.d(TAG, "download: b4 starting new thread");
        MyRunnable myRunnable = new MyRunnable(this, lat, lng, fahrenheit);
        new Thread(myRunnable).start();
        Log.d(TAG, "download: Started the thread");
        
    }

    public void receiveData(Weather weather) { //Weather weather: correct parameter
        Log.d(TAG, "receiveData: RECEIVED DATA");
        if (weather == null) {
            Toast.makeText(this, "Please Enter a Valid City Name", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO: assign the weather attrs to TVs
        //   -Update cty_st using geocoder!
        //
        globw = weather;
        recyclerView = findViewById(R.id.recycler);
        hourly_adap = new HourlyAdapter(weather.getHourly(), this);// Hourly list from The weather object returned from the thread
        recyclerView.setAdapter(hourly_adap);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        TextView tv_date_loc = findViewById(R.id.DateLoc_tv);
        TextView tv_curr_temp = findViewById(R.id.curr_temp_tv);
        TextView tv_flike = findViewById(R.id.flike_tv);
        TextView tv_desc_clouds =findViewById(R.id.desc_clouds_tv);
        TextView tv_winds = findViewById(R.id.winds_tv);
        TextView tv_humidity = findViewById(R.id.humidity_tv);
        TextView tv_uv = findViewById(R.id.uv_tv);
        TextView tv_mtemp = findViewById(R.id.mtemp_tv);
        TextView tv_atemp = findViewById(R.id.atemp_tv);
        TextView tv_etemp = findViewById(R.id.etemp_tv);
        TextView tv_ntemp = findViewById(R.id.ntemp_tv);
        TextView tv_mtime = findViewById(R.id.mtime_tv);
        TextView tv_atime = findViewById(R.id.mtime_tv2);
        TextView tv_etime = findViewById(R.id.mtime_tv3);
        TextView tv_ntime = findViewById(R.id.mtime_tv5);

        TextView tv_pressure = findViewById(R.id.pressure_tv);
        TextView tv_visibility = findViewById(R.id.visibility_tv);
        TextView tv_sunrise = findViewById(R.id.sunrise_tv);
        TextView tv_sunset = findViewById(R.id.sunset_tv);
        ImageView icon = findViewById(R.id.icon_iv);
        // String icon_pic
        int icon_id = this.getResources().getIdentifier("_" + weather.getIcon(), "drawable", this.getPackageName());
        Log.d(TAG, "receiveData: " + weather.getIcon());
        icon.setImageResource(icon_id);

        //
        String tmp_desc = weather.getDescription();
        String[] desc_split = tmp_desc.split(" ");
        String final_desc = "";
        boolean cl_ch = false;
        for(String word: desc_split){
            if (word.equals("cloud") || word.equals("clouds")){
                cl_ch = true;
            }
            String sbst = word.substring(0,1);
            final_desc = final_desc + sbst.toUpperCase() + word.substring(1) + " ";

        }

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            tv_date_loc.setText(cty_st + "   " + weather.getDt());
        } else {
            // In portrait
            tv_date_loc.setText(cty_st + "\n" + weather.getDt());
        }
        // Double spd = Double.parseDouble(weather.getWind_speed());

        // String kspd = String.valueOf((int)(spd*1.60934));
        // String mspd = String.valueOf(Math.round(spd));
        Log.d(TAG, "receiveData: SPD: " + weather.getWind_speed());


        tv_curr_temp.setText(weather.getTemp());
        tv_flike.setText("Feels like: " + weather.getFlike());
        if (cl_ch == true){
            tv_desc_clouds.setText(final_desc + "(" + String.valueOf(weather.getCloud()) + "% clouds)"); // TODO
        }else{
            tv_desc_clouds.setText(final_desc);
        }
        double sp = new Double(weather.getWind_speed());
        int msp = (int) sp;
        double ksp = sp * 3.6;
        int iksp = (int)ksp;
        Log.d(TAG, "receiveData: Fahrenheit " + fahrenheit + ":    " + sp);

        if(fahrenheit == true){
            tv_winds.setText("Winds: " + weather.getWind_dir() + " at " + msp + " mph");
            tv_visibility.setText(String.format("Visibility: %.2f mi", weather.getVisibility()*.00062137119));
        }else{
            tv_winds.setText("Winds: " + weather.getWind_dir() + " at " + iksp + " kph");
            tv_visibility.setText(String.format("Visibility: %.2f km", weather.getVisibility()*.001));
        }

        tv_humidity.setText("Humidity: " + weather.getHumidity());
        tv_uv.setText("UV Index: " + weather.getUv());
        tv_mtemp.setText(weather.getMtemp());
        tv_atemp.setText(weather.getDtemp());
        tv_etemp.setText(weather.getEtemp());
        tv_ntemp.setText(weather.getNtemp());
        tv_mtime.setText("8 am");
        tv_atime.setText("1 pm");
        tv_etime.setText("5 pm");
        tv_ntime.setText("11 pm");
        //tv_pressure.setText("Pressure: " + weather.getPressure());

        tv_sunrise.setText("Sunrise: "+ weather.getSunrise());
        tv_sunset.setText("Sunset: "+ weather.getSunset());

        // MenuItem i = findViewById(R.id.metric);
        Log.d(TAG, "receiveData: B4 Icon assignment");

        if (fahrenheit == false){
            int i_id = this.getResources().getIdentifier("units_c", "drawable", this.getPackageName());
            mu.getItem(0).setIcon(i_id);
        }else{
            int i_id = this.getResources().getIdentifier("units_f", "drawable", this.getPackageName());
            mu.getItem(0).setIcon(i_id);
        }

        Log.d(TAG, "receiveData: AFter Icon assignment");
        running = false;
    }

    private String getLocationName(String userProvidedLocation) {
        Geocoder geocoder = new Geocoder(this); // Here, “this” is an Activity
        try {
            List<Address> address = geocoder.getFromLocationName(userProvidedLocation, 1);
            if (address == null || address.isEmpty()) {
                // Nothing returned!
                return null; }
            String country = address.get(0).getCountryCode();
            String p1 = ""; String p2 = "";

            if (country.equals("US")) {
                p1 = address.get(0).getLocality();
                p2 = address.get(0).getAdminArea();
            } else {
                p1 = address.get(0).getLocality();
                if (p1 == null)
                    p1 = address.get(0).getSubAdminArea();
                p2 = address.get(0).getCountryName();
            }
            String locale = p1 + ", " + p2;
            return locale;
        } catch (IOException e) { // Failure to get an Address object
            return null;
        }
    }


    private double[] getLatLon(String userProvidedLocation) {
        Geocoder geocoder = new Geocoder(this); // Here, “this” is an Activity
        try {
            List<Address> address = geocoder.getFromLocationName(userProvidedLocation, 1);
            if (address == null || address.isEmpty())
            { // Nothing returned!
                return null;
            }
            Log.d(TAG, "getLatLon: LAT TYPE: " + address.get(0).getLatitude());
            Double lt = address.get(0).getLatitude();
            Double lg = address.get(0).getLongitude();
            return new double[] {lt, lg};
        }
        catch (IOException e) { // Failure to get an Address object
            return null;
        }
    }

    //TODO: Dialog, when posButton clicked -> update lat, lng using Geocoder and call download
    @Override
    public void onClick(View v){
        //In
        int pos = recyclerView.getChildLayoutPosition(v);
        Log.d(TAG, "onClick: pos: " + pos);

        String[] ar = globw.getHourly()[pos];
        String day = ar[0];
        String time = ar[1];


    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (running ? 1 : 0));
        parcel.writeByte((byte) (fahrenheit ? 1 : 0));
        if (lat == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(lat);
        }
        if (lng == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(lng);
        }
        parcel.writeString(cty_st);
    }
}