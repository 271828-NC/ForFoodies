package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Reservation extends AppCompatActivity {
    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("_bookings_");
    CalendarView calendar;
    TextView name;
    EditText hour;
    Button reserve;
    Booking booking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reservation);
        //        hide the actionbar
        getSupportActionBar().hide();
        calendar = findViewById(R.id.calendarView);
        name = findViewById(R.id.tv_res_name);
        reserve = findViewById(R.id.btn_reserve);
        hour = findViewById(R.id.et_time);
        final Eatery e = getIntent().getParcelableExtra("Eatery");
        name.setText(e.getName());
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                booking = new Booking(year, month, dayOfMonth, 0, e.getName(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                Toast.makeText(Reservation.this, "Date Selected !", Toast.LENGTH_SHORT).show();
                //When clicked on the calendar widget a booking object will be created with the date selected

            }
        });
        reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double time = 0;
                try {
                    time = Double.parseDouble(hour.getText().toString());
                    if (time < 11 || time > 18)//select anf validate the time
                        hour.setError("We are not open at that time");
                    else {
                        booking.setHour(time);
                        dbref.child(dbref.push().getKey()).setValue(booking);
                        startActivity(new Intent(getBaseContext(), Dashboard.class));
                    }
                } catch (Exception exception) {
                    hour.setError("Please enter a valid time");
                }

            }
        });

    }
}
