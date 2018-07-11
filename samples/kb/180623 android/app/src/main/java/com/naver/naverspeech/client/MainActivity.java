package com.naver.naverspeech.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;



public class MainActivity extends AppCompatActivity {
    Button favorites,start_meeting,search_meeting,participation_meeting,view_accounts,calender,setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("MainActivity");


        participation_meeting = (Button) findViewById(R.id.participation_meeting);
        view_accounts = (Button) findViewById(R.id.view_accounts);
        calender = (Button) findViewById(R.id.calender);
        start_meeting = (Button) findViewById(R.id.start_meeting);
        favorites = (Button) findViewById(R.id.favorites);
        setting = (Button) findViewById(R.id.setting);
        search_meeting = (Button) findViewById(R.id.search_meeting);

        start_meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Conference_pre.class);
//                intent.putExtra("text",String))
                startActivity(intent);
            }
        });

        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Join_Add.class);
                startActivity(intent);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Setting.class);
                startActivity(intent);
            }
        });

        search_meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,History.class);
                startActivity(intent);
            }
        });
        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Calendar.class);
                startActivity(intent);
            }
        });
    }

}
