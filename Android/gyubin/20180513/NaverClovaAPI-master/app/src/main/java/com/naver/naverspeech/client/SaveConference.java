package com.naver.naverspeech.client;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SaveConference extends AppCompatActivity {

    Button camera_upload;
    Button file_upload;
    EditText note;
    EditText search_tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_conference);
        setTitle("Save_Conference");

        camera_upload =(Button) findViewById(R.id.camera_upload);

    }

    public void onClick (View v){
        switch (v.getId()){
            case R.id.camera_upload:
               startActivity(new Intent(SaveConference.this,Camera_popup.class));
                break;
            case R.id.file_upload:
               Intent i= new Intent(Intent.ACTION_GET_CONTENT);
               i.setType("applcation/*");
               i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(i.createChooser(i,"OPEN"));
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
                i.setDataAndType(uri,"application/*");
                startActivity(i.createChooser(i,"OPEN"));
                Toast.makeText(getApplicationContext(), "파일업로드", Toast.LENGTH_SHORT).show();
                break;
        }


    }

}
