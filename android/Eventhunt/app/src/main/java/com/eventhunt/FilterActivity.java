package com.eventhunt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;

public class FilterActivity extends AppCompatActivity {
    private static final String TAG = FilterActivity.class.getSimpleName();
    public static final int FILTER_RESULT_CODE = 99;
    public static final String TYPE_EVENT = "Event";
    public static final String TYPE_GENRE = "Genre";
    public static final String TIME = "Time";
    public static final String COST = "Cost";

    private Spinner mTypeEvent, mTypeGenre;
    private SeekBar mTimeBar, mCostBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        mTypeEvent = findViewById(R.id.type_event);
        mTypeGenre = findViewById(R.id.type_genre);
        Log.w(TAG, "fragment is created");
        ArrayAdapter<CharSequence> adapterEvent = ArrayAdapter.createFromResource(this, R.array.type_event_array, android.R.layout.simple_spinner_item);
        adapterEvent.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mTypeEvent.setAdapter(adapterEvent);
        mTypeEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = mTypeEvent.getSelectedItem().toString();
                Log.w(TAG, selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<CharSequence> adapterGenre = ArrayAdapter.createFromResource(this, R.array.type_genre_array, android.R.layout.simple_spinner_item);
        adapterGenre.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mTypeGenre.setAdapter(adapterGenre);
        mTypeEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = mTypeEvent.getSelectedItem().toString();
                Log.w(TAG, selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mTimeBar = findViewById(R.id.sb_time);
        mCostBar = findViewById(R.id.sb_cost);
        Button button = findViewById(R.id.btn_find);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(TYPE_EVENT, mTypeEvent.getSelectedItem().toString());
                intent.putExtra(TYPE_GENRE, mTypeGenre.getSelectedItem().toString());
                intent.putExtra(TIME, mTimeBar.getProgress());
                intent.putExtra(COST, mCostBar.getProgress());
                setResult(FILTER_RESULT_CODE, intent);
                FilterActivity.this.finish();
            }
        });
    }
}
