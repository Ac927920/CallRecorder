package com.example.callrecorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 100;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = findViewById(R.id.button1);

        button1.setOnClickListener(v -> {
            if (!isRecording) {
                startRecording();
            } else {
                stopRecording();
            }
        });

        // Request permissions at runtime if not granted
        if (!hasPermissions()) {
            requestPermissions();
        }
    }

    private boolean hasPermissions() {
        int recordAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int writeStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return recordAudioPermission == PackageManager.PERMISSION_GRANTED && writeStoragePermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
        }, REQUEST_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                Toast.makeText(this, "Permissions granted. You can start recording.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions denied. App cannot record audio or write to storage.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startRecording() {
        if (hasPermissions()) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Call_Recorder";
            File directory = new File(filePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = "Recording" + System.currentTimeMillis() + ".mp3";
            String completeFilePath = filePath + "/" + fileName;

            mediaRecorder.setOutputFile(completeFilePath);

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                isRecording = true;
                button1.setText("Stop Recording");
                Toast.makeText(getApplicationContext(), "Recording Started.", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                releaseMediaRecorder();
                isRecording = false;
            }
        } else {
            requestPermissions();
        }
    }

    private void stopRecording() {
        if (isRecording) {
            mediaRecorder.stop();
            releaseMediaRecorder();
            isRecording = false;
            button1.setText("Start Recording");
            Toast.makeText(getApplicationContext(), "Recording Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }
}
