package com.example.weather;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HourlyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static final String TAG = "EmployeesAdapter";
    private final String[][] weatherList;
    private final MainActivity mainAct;

    HourlyAdapter(String[][] wList, MainActivity ma) {
        this.weatherList = wList;
        mainAct = ma;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW MyViewHolder");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: FILLING VIEW HOLDER Hourly Forecast " + position);
        //Log.d(TAG, "onBindViewHolder: WeatherList.length()" + weatherList.length);
        // Log.d(TAG, "onBindViewHolder: TIME: " + weatherList[position][1]);
        // Log.d(TAG, "onBindViewHolder: WEATHER: " + weatherList[position][2]);
        // Log.d(TAG, "onBindViewHolder: DESCRIPTION: " + weatherList[position][4]);
        Log.d(TAG, "onBindViewHolder: ICON: " + weatherList[position][3]);


        // Weather employee = weatherList.get(position);
        String i = "@drawable" + weatherList[position][3];

        //Resources res = getResources();
        //Resources
        //get
        // Resources.getSystem().getResourcePackageName();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            int resourceId = Resources.getSystem().getIdentifier(i, "drawable", mainAct.getOpPackageName() );
            holder.rv_image.setImageResource(resourceId);
        }

        // Drawable d = Resources.getSystem().getDrawable(resourceId);
        holder.rv_daytv.setText(weatherList[position][0]);
        holder.rv_timetv.setText(weatherList[position][1]);
        holder.rv_temptv.setText(weatherList[position][2]);
        int icon_id = holder.rv_image.getResources().getIdentifier("_" + weatherList[position][3], "drawable", mainAct.getPackageName());
        holder.rv_image.setImageResource(icon_id);
        //holder.rv_image.setImageDrawable(d);
        // holder.rv_image.setImageResource(getResources().getIdentifier(i, drawable, getPackageName()));
                // setImageDrawable(resourceId);  // TODO: Set the appropriate pic

        String td = weatherList[position][4];
        String[] desc_split = td.split(" ");
        String final_desc = "";
        for(String word: desc_split){
            String sbst = word.substring(0,1);
            final_desc = final_desc + sbst.toUpperCase() + word.substring(1) + " ";
        }
        holder.rv_desctv.setText(final_desc);
    }

    @Override
    public int getItemCount() {return weatherList.length -1;}

}
