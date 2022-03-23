package com.example.weather;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Weather implements Serializable {
    // 23 Vars
    private String city;
    private boolean US;  //Not used!
    //private final String st_cry;
    private String lat;
    private String lng;
    private String dt;  //"E MMM dd hh:mm a, yyyy"
    private String sunrise;
    private String sunset;
    private final String temp;
    private String flike;
    private String pressure; // added
    private final String humidity;
    private String uv;
    private int cloud; // added
    private int visibility; // type changed
    private String wind_speed;
    private String wind_dir;
    private String icon;
    private final String description;
    private String mtemp;
    private String dtemp;
    private String etemp;
    private String ntemp;
    private String[][] hourly = new String[48][5];
    private String[][] daily = new String[7][10];
    //private String visibility;
    //private final String conditions;
    // private final String wind;
    //private final String date;

    public Weather(String lat, String lng, String city, String dt, String sr, String ss, String tp, String flike, String pressure,
            String humidity, String uv, int cloud, int visibility, String wspd, String wdir, String icon, String description,
            String mtemp,String dtemp,String etemp,String ntemp, String[][] hourly, String[][] daily) {
        this.lat = lat;
        this.lng = lng;
        this.city = city;
        this.dt = dt; //TODO: Not sure about the datatype for dt
        this.sunrise = sr;
        this.sunset = ss;
        this.temp = tp;
        this.flike = flike;
        this.pressure = pressure;
        this.humidity = humidity;
        this.uv =uv;
        this.cloud = cloud;
        this.visibility = visibility;
        this.wind_speed = wspd;
        this.wind_dir = wdir;
        this.icon = icon;
        this.description = description;
        this.mtemp = mtemp;
        this.dtemp = dtemp;
        this.etemp = etemp;
        this.ntemp = ntemp;
        this.hourly = hourly;
        this.daily = daily;
        //arr to store 48 hr weather info????
        //this.sunrise = sunrise;
        //this.sunset = sunset;
        //this.visibility = visibility;
        //this.wind = wind;
        //Weather icon???
        //this.bitmap = bitmap;
    }

    //public Weather(String jlat, String jlng, String city, String jdate, String jsunrise, String jsunset, String jtemp, String jflike, String jpressure, String jhumidity, String juv, int jclouds, int visibility, String jwind_sp, String jdir, String jicon, String jdesc, String jmon, String jaft, String jeve, String jngt, String[][] hourly, String[][] daily) {
    //}

    public String getLat() {return lat;}
    public String getLng() {return lng;}
    public String getCity() {return city;}
    public String getDt() {return dt;}
    public String getSunrise() {return sunrise;}
    public String getSunset() {return sunset;}
    public String getTemp() {return temp;}
    public String getFlike() {return flike;}
    public String getPressure() {return pressure;}
    public String getHumidity() {return humidity;}
    public String getUv() {return uv;}
    public int getCloud() {return cloud;}
    public int getVisibility() {return visibility;}
    public String getWind_speed() {return wind_speed;}
    public String getWind_dir() {return wind_dir;}
    public String getIcon() {return icon;}
    public String getDescription() {return description;}
    public String getMtemp() {return mtemp;}
    public String getDtemp() {return dtemp;}
    public String getEtemp() {return etemp;}
    public String getNtemp() {return ntemp;}
    public String[][] getHourly() {return hourly;}
    public String[][] getDaily() {return daily;}
    //public String getVisibility() {return visibility;}
    //public String getWind() {return wind;}
}
