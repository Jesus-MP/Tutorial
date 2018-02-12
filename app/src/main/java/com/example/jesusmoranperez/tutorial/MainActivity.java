package com.example.jesusmoranperez.tutorial;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {


    private Button mRecord;
    private Button mStop;
    private static final int RECORDER_SAMPLERATE=44100;
    private static final int RECORDER_CHANNELS= AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING=AudioFormat.ENCODING_PCM_16BIT;
    private Boolean isRecording=null;
    private Thread recordingThread=null;
    int BufferElements2Rec = 1024;
    int BytesPerElement = 2;

    private AudioRecord record;
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(MainActivity.this);
        initializeButtons();
        setListeners();


    }


    public void initializeButtons()
    {
        mRecord= (Button)findViewById(R.id.playButton);
        mStop= (Button)findViewById(R.id.stopButton);

    }

    public void setListeners()
    {
        mRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorderRaw();
            }
        });


        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 stopRecording();
            }
        });




    }



    void recorderRaw()
    {

        int bufferSize=AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING);




        short[] audioBuffer = new short[bufferSize/2];
       record = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING,BufferElements2Rec*BytesPerElement

        );
        record.startRecording();
        Toast toast =Toast.makeText(getApplicationContext(),"Started Recording",Toast.LENGTH_LONG);
        toast.show();

        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();








    }

    void stopRecording()
    {
        if (null != record)
        {
            record.stop();
            record.release();
            isRecording=false;
            record=null;
            recordingThread=null;
            Toast toast=Toast.makeText(getApplicationContext(),"Stopped Recording",Toast.LENGTH_LONG);
            toast.show();
        }

    }

    private void writeAudioDataToFile() {
        // Write the output audio in byte

        String filePath = "/sdcard/Test.wav";
        short sData[] = new short[BufferElements2Rec];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isRecording) {
            // gets the voice output from microphone to byte format

            record.read(sData, 0, BufferElements2Rec);
            System.out.println("Short writing to file" + sData.toString());
            try {
                // // writes the data to file from buffer
                // // stores the voice buffer
                byte bData[] = short2byte(sData);
                os.write(bData, 0, BufferElements2Rec * BytesPerElement);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }


    //convert short to byte
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }




    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE

            );
        }
    }




















}
