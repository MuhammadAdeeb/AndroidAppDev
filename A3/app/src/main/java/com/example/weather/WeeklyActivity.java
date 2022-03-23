package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

public class WeeklyActivity extends AppCompatActivity {


    private static final String TAG = "WeeklyActivity";
    private RecyclerView recyclerView; // Layout's recyclerview
    private WeeklyAdapter weekly_adap; // Data to recyclerview adapter
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly);

        Log.d(TAG, "WA, onCreate: Just Started");
        Intent intent = getIntent();
        // p = intent.getPackage();
        Log.d(TAG, "WA, onCreate: AFter getIntent");
        String[][] sa = (String[][]) intent.getSerializableExtra("weekly");
        String cty = intent.getStringExtra("cty");
        setTitle(cty);
        Log.d(TAG, "onCreate: " + sa[0][0] + " " + sa[1][0] + " " + sa[2][0] + " " + sa[3][0] + " " + sa[4][0] + " " + sa[5][0] + " " + sa[6][0] + " " );

        Log.d(TAG, "WA, onCreate: After String Array Intent");
        // MainActivity ma = (MainActivity) intent.getParcelableExtra("act");
        recyclerView = findViewById(R.id.wk_recycler);

        Log.d(TAG, "WA, onCreate: after 1");
        // Using intent, retrieve the weekly array (daily) array
        weekly_adap = new WeeklyAdapter(sa, this);  // Hourly list from The weather object returned from the thread

        Log.d(TAG, "WA, onCreate: after 2");
        recyclerView.setAdapter(weekly_adap);

        Log.d(TAG, "WA, onCreate: after 3");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Log.d(TAG, "WA, onCreate: after 4");

    }
}