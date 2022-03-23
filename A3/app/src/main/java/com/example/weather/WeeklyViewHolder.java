package com.example.weather;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class WeeklyViewHolder extends RecyclerView.ViewHolder{

    TextView rv_dttv;
    TextView rv_trangetv;
    TextView rv_desc_tv;
    TextView rv_prec_tv;
    TextView rv_uvtv;
    TextView rv_mtemptv;
    TextView rv_atemptv;
    TextView rv_etemptv;
    TextView rv_ntemptv;
    TextView rv_mtimetv;
    TextView rv_atimetv;
    TextView rv_etimetv;
    TextView rv_ntimetv;
    ImageView rv_waImage;


    WeeklyViewHolder(View view) {
        super(view);
        rv_dttv = view.findViewById(R.id.wa_dt_tv);
        rv_trangetv = view.findViewById(R.id.wa_trange_tv);
        rv_desc_tv = view.findViewById(R.id.wa_desc_tv);
        rv_prec_tv = view.findViewById(R.id.wa_prec_tv);
        rv_uvtv = view.findViewById(R.id.wa_uv_tv);
        rv_mtemptv = view.findViewById(R.id.wa_mtemp_tv);
        rv_atemptv = view.findViewById(R.id.wa_atemp_tv);
        rv_etemptv = view.findViewById(R.id.wa_etemp_tv);
        rv_ntemptv = view.findViewById(R.id.wa_ntemp_tv);
        rv_mtimetv = view.findViewById(R.id.wa_mtime_tv);
        rv_atimetv = view.findViewById(R.id.wa_atime_tv);
        rv_etimetv = view.findViewById(R.id.wa_etime_tv);
        rv_ntimetv = view.findViewById(R.id.wa_ntime_tv);
        rv_waImage = view.findViewById(R.id.wa_iv);

    }
}
