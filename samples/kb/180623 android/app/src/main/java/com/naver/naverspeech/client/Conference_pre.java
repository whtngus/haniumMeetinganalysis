package com.naver.naverspeech.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Conference_pre extends AppCompatActivity {

    EditText subject,metting_room;
    Button add_invite,start_meeting;
    String save_subject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conference_pre);
        setTitle("Conference_pre");

        subject = (EditText) findViewById(R.id.subject);
        start_meeting = (Button) findViewById(R.id.start_meeting);
        start_meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_subject = subject.getText().toString();

                Intent intent = new Intent(Conference_pre.this,Conference.class);
                intent.putExtra("subject",save_subject);
                startActivity(intent);


            }
        });
    }
}
