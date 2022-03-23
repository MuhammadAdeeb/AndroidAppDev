package com.example.weather;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WeeklyAdapter extends RecyclerView.Adapter<WeeklyViewHolder> {

    private static final String TAG = "WeeklyAdapter";
    private String[][] weeklyList;
    private WeeklyActivity mainAct;
    //private final Activity act;

    WeeklyAdapter(String[][] wList, WeeklyActivity ma) { //,
        Log.d(TAG, "WeeklyAdapter: 1st line of Constructor");
        this.weeklyList = wList;
        this.mainAct = ma;
        Log.d(TAG, "WeeklyAdapter: Last line of Constructor");
        //act = a;
    }

    @NonNull
    @Override
    public WeeklyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "WeeklyAdapter: onCreateViewHolder: 1st Line");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weekly, parent, false);

        // itemView.setOnClickListener(mainAct);
        // itemView.setOnLongClickListener(mainAct);
        Log.d(TAG, "WeeklyAdapter: onCreateViewHolder: Last Line");
        return new WeeklyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WeeklyViewHolder holder, int position) {
        Log.d(TAG, "WeeklyAdapter: onBindViewHolder: 1st Line");

        String td = weeklyList[position][2];
        String[] desc_split = td.split(" ");
        String final_desc = "";
        for(String word: desc_split){
            String sbst = word.substring(0,1);
            final_desc = final_desc + sbst.toUpperCase() + word.substring(1) + " ";

        }
        holder.rv_dttv.setText(" " + weeklyList[position][0]);
        holder.rv_trangetv.setText(weeklyList[position][1]);
        holder.rv_desc_tv.setText(final_desc);
        holder.rv_prec_tv.setText(weeklyList[position][3]);
        holder.rv_uvtv.setText(weeklyList[position][4]);
        holder.rv_mtemptv.setText(weeklyList[position][5]);
        holder.rv_atemptv.setText(weeklyList[position][6]);
        holder.rv_etemptv.setText(weeklyList[position][7]);
        holder.rv_ntemptv.setText(weeklyList[position][8]);

        holder.rv_mtimetv.setText("8 am");
        holder.rv_atimetv.setText("1 pm");
        holder.rv_etimetv.setText("5 pm");
        holder.rv_ntimetv.setText("11 pm");

        Log.d(TAG, "weeklyAdapter: TRange: " + weeklyList[position][1]);
        int icon_id = holder.rv_waImage.getResources().getIdentifier(weeklyList[position][9], "drawable", mainAct.getPackageName()); //holder.rv_waImage
        Log.d(TAG, "onBindViewHolder: ICON: " + weeklyList[position][9]);
        holder.rv_waImage.setImageResource(icon_id);

        // int icon_id = this.getResources().getIdentifier("_" + weather.getIcon(), "drawable", this.getPackageName());
        //        Log.d(TAG, "receiveData: " + weather.getIcon());
        //        icon.setImageResource(icon_id);

        Log.d(TAG, "WeeklyAdapter: onBindViewHolder: Last Line");
    }

    @Override
    public int getItemCount() {return weeklyList.length;}
}
