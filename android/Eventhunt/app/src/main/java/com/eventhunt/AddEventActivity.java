package com.eventhunt;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.eventhunt.entity.Event;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private EditText dateEditText;
    private ImageButton eventDateButton;
    private Address address;
    private Date date;
    private Calendar dateTime;
    private TimePicker timeEvent;
    private SimpleDateFormat sdf;

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
        dateEditText = findViewById(R.id.editText2);
        dateTime = Calendar.getInstance();
        sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        title = findViewById(R.id.et_name_event);
        eventDateButton = findViewById(R.id.ib_date_event);
        eventDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();

                DatePickerDialog date = new DatePickerDialog(AddEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateTime.set(Calendar.YEAR, year);
                        dateTime.set(Calendar.MONTH, month);
                        dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                date.getDatePicker().setMinDate(calendar.getTime().getTime());
                TimePickerDialog time = new TimePickerDialog(AddEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        dateTime.set(Calendar.MINUTE, minute);

                        String df = sdf.format(dateTime.getTime());
                        dateEditText.setText(df);
                    }
                }, 12, 0, true);
                time.show();

                date.show();

            }
        });
        //description = findViewById(R.id.et_info_event);
        //CalendarView calendarView = findViewById(R.id.calendarView_date);
        /*timeEvent = findViewById(R.id.tp_time);
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
        });*/
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
        /*description.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return description.getText().length() < 500;
            }
        });*/


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
                //event.setDescription(description.getText().toString());
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
                if(!dateEditText.getText().toString().isEmpty()) {
                    String strDate = dateEditText.getText().toString();
                    Date date;
                    try {
                         date = sdf.parse(strDate);
                    } catch (ParseException e) {
                        Toast.makeText(AddEventActivity.this, "Неправильно введен формат даты", Toast.LENGTH_SHORT).show();
                        dateEditText.setFocusable(true);
                        return false;
                    }
                    if(date.getTime() != dateTime.getTime().getTime())
                        dateTime.setTime(date);
                    event.setStartEvent(dateTime);
                }
                Intent intent = new Intent();
                intent.putExtra(EVENT_KEY, event);
                setResult(RESULT_SUCCESSFUL_CODE, intent);
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_FAIL_CODE);
        finish();
    }
}
