package com.naver.naverspeech.client;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.CalendarView;
import android.widget.Toast;


public class Calendar extends Activity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarview);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                Toast.makeText(getApplicationContext(),""+year+"/"+(month+1)+"/"+dayOfMonth,Toast.LENGTH_LONG).show();


            }
        });

    }
}
