package com.naver.naverspeech.client;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.coremedia.iso.boxes.Container;
import com.naver.naverspeech.client.utils.AudioWriterPCM;
import com.naver.speech.clientapi.SpeechRecognitionResult;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Conference extends Activity {
    //퍼미션 위한 변수
    String[] permission = new String[]{Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};


    int f_count = 0; // 0 : 녹음이 처음시작임을 알림 // 1: 녹음 실행 중임을 알림

    int file_count = 0; // sttrecoding1,sttrecoding2....이런식으로 녹음 임시파일들이 서로 겹쳐지지않게하기위함
    String FilePath = null; // 파일저장 절대경로(내부저장소) + 파일명
    String resultFile; // 녹음 임시파일 병합(머지) 후 결과물
    ArrayList<String> list_stt; //STT 텍스트
    ArrayList<String> outputSttList; // 녹음파일 병합 할 리스트
    ProgressBar p_gradient;
    TextView start_time, end_time;
    P_Thread p_thread;
    String subject;
    private static final String TAG = Conference.class.getSimpleName();
    private static final String CLIENT_ID = "5pmGDZp9IdmVjruqMmbk";
    // 1. "내 애플리케이션"에서 Client ID를 확인해서 이곳에 적어주세요.
    // 2. build.gradle (Module:app)에서 패키지명을 실제 개발자센터 애플리케이션 설정의 '안드로이드 앱 패키지 이름'으로 바꿔 주세요

    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;

    private String mResult; // STT 텍스트
    private TextView txtResult; // STT 텍스트
    private Button btnRecord; // 녹음
    private Button btnPause; // 일시정지
    private Button btnSave; // 저장
    private Button btnReset; // 초기화
    private Button btnPlay;  // 재생


    private AudioWriterPCM writer; //open APi

    private Handler handler3; // rec_handler
    private Handler handler2; // pcm_handler

    // Handle speech recognition Messages.
    private void handleMessage(Message msg) {
        switch (msg.what) {
            case R.id.clientReady:
                String tv3 = "";
                if(list_stt.size()==0){
                    tv3 ="";
                }else{
                    for(int i=0; i<list_stt.size(); i++){
                        tv3 += list_stt.get(i)+ " ";
                    }
                }
                // Now an user can speak.
                txtResult.setText(tv3);
                writer = new AudioWriterPCM(
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;

            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;

            case R.id.partialResult:
                // Extract obj property typed with String.
                String tv ="";
                if(list_stt.size() == 0){
                    tv = "";
                }else{
                    for(int i=0; i<list_stt.size(); i++){
                        tv +=list_stt.get(i)+" ";
                    }
                }
                mResult = (String) (msg.obj);
                txtResult.setText(tv+mResult);
                break;

            case R.id.finalResult:
                // Extract obj property typed with String array.
                // The first element is recognition result for speech.

                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                String a = speechRecognitionResult.getResults().get(0);
                StringBuilder strBuf = new StringBuilder();
                for(String result : results) {
                    strBuf.append(result);
                    strBuf.append("\n");
                }
                String tv2 ="";
                mResult = a.toString();
                if(list_stt.size() ==0){
                    txtResult.setText(mResult);
                }else{
                    for(int i=0; i<list_stt.size(); i++){
                        tv2 +=list_stt.get(i)+" ";
                    }
                    txtResult.setText(tv2+mResult);
                }

                list_stt.add(mResult);
                btnRecord.setEnabled(true);
                btnPause.setEnabled(false);
                btnRecord.setVisibility(View.VISIBLE);
                btnPause.setVisibility(View.INVISIBLE);
                p_thread.work = false;
                break;

            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }

                mResult = "Error code : " + msg.obj.toString();
                txtResult.setText(mResult);
                break;

            case R.id.clientInactive:
                if (writer != null) {
                    writer.close();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conference);
        //권한체크
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        subject = new Intent(this.getIntent()).getStringExtra("subject");

        outputSttList = new ArrayList<String>();
        resultFile = Environment.getExternalStorageDirectory().getAbsolutePath()+"/output"+subject+".mp4";



        txtResult = (TextView) findViewById(R.id.txt_result);
        btnRecord = (Button) findViewById(R.id.btn_record);
        btnPause = (Button) findViewById(R.id.btn_pause);
        btnSave = (Button) findViewById(R.id.btn_finish) ;
        btnReset = (Button) findViewById(R.id.btn_reset);
        btnPlay = (Button) findViewById(R.id.btn_play);
        p_gradient = (ProgressBar) findViewById(R.id.progress);
        start_time = (TextView) findViewById(R.id.start_time);
        end_time = (TextView) findViewById(R.id.end_time);
        handler2 = new Handler();
        list_stt = new ArrayList<String>();

        btnRecord.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.INVISIBLE);
        btnRecord.setEnabled(true);
        btnPause.setEnabled(false);


        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(this, handler, CLIENT_ID);

        p_gradient.setProgress(0);



    }

    //권한 체크
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_record: {
                if (f_count == 0) {
                    handler3 = new Handler();
                    p_thread = new P_Thread();
                    p_thread.start();
                    p_thread.stop = false;
                    p_thread.work = true;
                    Log.e("btnRecord 처음시작"," "+p_thread.getState().toString()+"이씨"+p_thread.work);
                } else if (f_count == 1) {
                    p_thread.stop = false;
                    p_thread.work = true;
                    Log.e("btnRecord 재시작"," "+p_thread.getState().toString()+"이씨"+p_thread.work);
                }
                f_count = 1;
                Log.e("thread_status: " ,""+p_thread.getState()+ Integer.toString(f_count)+"이씨"+p_thread.work);

                if (!naverRecognizer.getSpeechRecognizer().isRunning()) {
                    // Start button is pushed when SpeechRecognizer's state is inactive.
                    // Run SpeechRecongizer by calling recognize().
                    mResult = "";
                    txtResult.setText("Connecting...");
                    naverRecognizer.recognize();
                } else {
                    Log.e(TAG, "stop and wait Final Result");
                    naverRecognizer.getSpeechRecognizer().stop();
                }
                btnRecord.setEnabled(false);
                btnPause.setEnabled(true);

                btnRecord.setVisibility(View.INVISIBLE);
                btnPause.setVisibility(View.VISIBLE);

                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.requestAudioFocus(focusChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);

                break;
            }

            case R.id.btn_pause: {
                Log.e("btnPause 일시정지"," "+p_thread.getState().toString()+"이씨"+p_thread.work);
                p_thread.work =false;
                naverRecognizer.getSpeechRecognizer().stop();
                try {
                    file_count += 1;
                    FilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sttrecording";
                    String nowFile = FilePath + file_count + ".mp4";
                    encodeSingleFile(nowFile);
                    outputSttList.add(nowFile);
                    Log.e("TAG", "" + Integer.toString(outputSttList.size()));

                } catch (Exception e) {
                    Log.e(TAG, "Exception while creating tmp file1", e);
                }

                btnRecord.setEnabled(true);
                btnPause.setEnabled(false);
                btnRecord.setVisibility(View.VISIBLE);
                btnPause.setVisibility(View.INVISIBLE);
                break;
            }
            case  R.id.btn_finish: {
                try{
                    startMerge(outputSttList);
                    Intent intent = new Intent(Conference.this,SaveConference.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("r_path", resultFile);
                    intent.putExtra("content", txtResult.getText());
                    intent.putExtra("subject",subject);
                    f_count =0;
                    p_thread.work = false;
                    p_thread.stop = true;
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "저장버튼", Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    Log.e(TAG, "Exception while creating tmp file", e);
                } finally {
                    onBtnReset();
                }
                break;
            }
            case R.id.btn_reset :{
                onBtnReset();
                p_thread.work = false;
                p_thread.stop = true;
                f_count =0;

                break;
            }

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        // NOTE : initialize() must be called on start time.
        naverRecognizer.getSpeechRecognizer().initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mResult = "";
        txtResult.setText("");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // NOTE : release() must be called on stop time.
        naverRecognizer.getSpeechRecognizer().release();
    }

    // Declare handler for handling SpeechRecognizer thread's Messages.
    static class RecognitionHandler extends Handler {
        private final WeakReference<Conference> mActivity;

        RecognitionHandler(Conference activity) {
            mActivity = new WeakReference<Conference>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Conference activity = mActivity.get();
            if (activity != null) {
                activity.handleMessage(msg);
            }
        }
    }


    private AudioManager.OnAudioFocusChangeListener focusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                            // Lower the volume while ducking.
                            break;
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):

                            break;
                        case (AudioManager.AUDIOFOCUS_GAIN):
                            break;
                        default:
                            break;
                    }
                }
            };

    private void encodeSingleFile(final String outputPath) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(encodeTask(1, outputPath));
    }

    private Runnable encodeTask(final int numFiles, final String outputPath) { // pcm 파일 인코딩
        return new Runnable() {
            @Override
            public void run() {
                try {
                    final PCMEncoder pcmEncoder = new PCMEncoder(48000, 16000, 1);
                    pcmEncoder.setOutputPath(outputPath);
                    pcmEncoder.prepare();
                    File directory = new File("storage/emulated/0/NaverSpeechTest/Test.pcm");
                    for (int i = 0; i < numFiles; i++) {
                        Log.d(TAG, "Encoding: " + i);
                        InputStream inputStream = new FileInputStream(directory);
                        inputStream.skip(44);
                        pcmEncoder.encode(inputStream, 16000);
                    }
                    pcmEncoder.stop();
                    handler2.post(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(getApplicationContext(), "Encoded file to: " + outputPath, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (IOException e) {
                    Log.e(TAG, "Cannot create FileInputStream", e);
                }
            }
        };
    }

    private class P_Thread extends Thread {
        private int progressStatus = 0;
        public boolean stop = false;
        public boolean work = true;

        public void run() {
            while (progressStatus < 60 && !Thread.currentThread().isInterrupted()) {
                if (work) {
                    try {
                        progressStatus++;
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Update the progress bar
                    handler3.post(new Runnable() {
                        public void run() {
                            p_gradient.setProgress(progressStatus);
                            start_time.setText("00:" + String.format("%02d", progressStatus));
                            end_time.setText("00:" + String.format("%02d", 60 - progressStatus));
                        }
                    });
                } else {
                    Log.e("End STT", " " + p_thread.getState().toString()+p_thread.work);
                    if (Thread.currentThread().getState().equals(State.RUNNABLE)) {
                        try {
                            Thread.sleep(800);
                        } catch (Exception e) {
                        }
                    }
                    if (stop) {
                        Log.e("hello", " " + Thread.currentThread().getState());
                        progressStatus = 0;
                        handler3.post(new Runnable() {
                            public void run() {
                                p_gradient.setProgress(progressStatus);
                                start_time.setText("00:" + String.format("%02d", progressStatus));
                                end_time.setText("00:" + String.format("%02d", 60 - progressStatus));
                            }
                        });
                        break;
                    }
                }
            }
            Log.e("끝", "끝");
            progressStatus = 0;
            stop = false;
            work = true;
        }
    }

    public void startMerge(ArrayList<String> outputFileList)throws IOException // mp4parser library
    {
        Movie[] inMovies = new Movie[outputFileList.size()];
        try
        {
            Log.e("file_size", ""+ Integer.toString(outputFileList.size()));
            for(int a = 0; a < outputFileList.size(); a++)
            {
                inMovies[a] = MovieCreator.build(outputFileList.get(a));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        List<Track> audioTracks = new LinkedList<Track>();
        for (Movie m : inMovies)
        {
            for (Track t : m.getTracks())
            {
                if (t.getHandler().equals("soun"))
                {
                    audioTracks.add(t);
                }
            }
        }

        Movie output = new Movie();
        if (audioTracks.size() > 0)
        {
            try
            {
                output.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        Container out = new DefaultMp4Builder().build(output);
        FileChannel fc = null;
        try
        {
            fc = new FileOutputStream(new File(resultFile)).getChannel();
            Log.e("output",resultFile);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        try
        {
            out.writeContainer(fc);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            fc.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public void onBtnReset(){
        outputSttList.clear();
        file_count =0;
        txtResult.setText("");
        list_stt.clear();
        p_gradient.setProgress(0);
    }


}
