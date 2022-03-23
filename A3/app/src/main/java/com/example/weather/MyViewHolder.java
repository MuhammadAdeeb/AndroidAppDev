package com.example.weather;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView rv_daytv;
    TextView rv_timetv;
    TextView rv_temptv;
    TextView rv_desctv;
    ImageView rv_image;


    MyViewHolder(View view) {
        super(view);
        rv_daytv = view.findViewById(R.id.rv_day_tv);
        rv_timetv = view.findViewById(R.id.rv_time_tv);
        rv_image = view.findViewById(R.id.rv_img);
        rv_temptv = view.findViewById(R.id.rv_temp_tv);
        rv_desctv = view.findViewById(R.id.rv_desc_tv);
    }

}
