package com.eventhunt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.eventhunt.entity.Event;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddEventActivity extends AppCompatActivity {
    private static final String TAG = AddEventActivity.class.getSimpleName();
    public static final int RESULT_FAIL_CODE = 404;
    public static final int RESULT_SUCCESSFUL_CODE = 200;
    public static final String EVENT_KEY = "Event";

    private EditText title;
    private EditText description;
    private EditText editTextAddress;
    private Address address;
    private Date date;
    private Calendar calendar;
    private TimePicker timeEvent;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        Intent intent = getIntent();
        date = new Date();
        editTextAddress = findViewById(R.id.et_address);
        if(intent != null){
            Address address = intent.getParcelableExtra("Address");
            editTextAddress.setText(address.getAddressLine(0));
        }
        title = findViewById(R.id.et_name_event);
        description = findViewById(R.id.et_info_event);
        CalendarView calendarView = findViewById(R.id.calendarView_date);
        calendar = Calendar.getInstance();
        timeEvent = findViewById(R.id.tp_time);
        calendarView.setMinDate(calendar.getTime().getTime());
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
            }
        });
        timeEvent.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
            }
        });
        title.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return title.getText().length() < 255;
            }
        });

        editTextAddress.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(address != null){
                    address = null;
                }
                return true;
            }
        });
        description.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return description.getText().length() < 500;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_event_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item_add_event:
                Event event = new Event();
                event.setTitle(title.getText().toString());
                event.setDescription(description.getText().toString());
                if(address == null){
                    try {
                        Geocoder geocoder = new Geocoder(this);
                        List<Address> list = geocoder.getFromLocationName(editTextAddress.getText().toString(), 5);
                        address = list.get(0);
                    } catch (IOException e){
                        e.printStackTrace();
                        setResult(RESULT_FAIL_CODE);
                        finish();
                    }
                }
                event.setAddress(address);
                if(calendar.isSet(Calendar.MINUTE)){
                    event.setStartEvent(calendar);
                } else{
                    timeEvent.setFocusable(true);
                    Toast.makeText(this, "Установите время", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent();
                intent.putExtra(EVENT_KEY, event);
                setResult(RESULT_SUCCESSFUL_CODE, intent);
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
