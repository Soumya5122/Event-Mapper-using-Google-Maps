package com.example.eventmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventmanager.Event;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CalendarActivity extends AppCompatActivity {

    private TextView locationTextView;
    private DatePicker datePicker;
    private EditText eventNameEditText;
    private Button addEventButton;

    private String location;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        locationTextView = findViewById(R.id.location_text_view);
        datePicker = findViewById(R.id.date_picker);
        eventNameEditText = findViewById(R.id.event_name_edit_text);
        addEventButton = findViewById(R.id.add_event_button);

        Intent intent = getIntent();

        if (intent != null) {
            location = intent.getStringExtra("location");
            latitude = intent.getDoubleExtra("latitude", 0);
            longitude = intent.getDoubleExtra("longitude", 0);
        }

        locationTextView.setText(location);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.events_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // eventSpinner.setAdapter(adapter); // Spinner is removed

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String event = String.valueOf("event"); // Spinner is removed
                String eventName = eventNameEditText.getText().toString();
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();

                if (location != null &&!location.equals("") &&!event.equals("") &&!eventName.equals("")) {
                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                    calendar.set(year, month, day);
                    Date date = calendar.getTime();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String dateString = sdf.format(date);

                    Event eventObj = new Event(location, event, eventName, dateString);

                    // Save the event data to Firebase
                    saveEventData(eventObj);

                    // Clear the form fields
                    eventNameEditText.setText("");
                    // eventSpinner.setSelection(0); // Spinner is removed
                    datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                    Toast.makeText(CalendarActivity.this, "Event added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CalendarActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add an OnClickListener to the locationTextView
        locationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the CalendarActivity and return to the previous activity (MainActivity)
                finish();
            }
        });
    }

    private void saveEventData(Event event) {
        // Generate a unique key for the event data
        String key = FirebaseDatabase.getInstance().getReference("events").push().getKey();
        if (key != null) {
            // Save the event data to Firebase
            FirebaseDatabase.getInstance().getReference("events").child(key).setValue(event);

            // Display theevent details in a toast message
            String message = "Event: " + event.getEvent() + "\n"
                    + "Event Name: " + event.getEventName() + "\n"
                    + "Date: " + event.getDate() + "\n"
                    + "Location: " + event.getLocation();
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            // Add a log statement to make sure that the method is being called
            Log.d("CalendarActivity", "Event saved to Firebase: " + message);
        }
    }
}