package com.example.arclient;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Bundle;
import android.widget.TextView;

import com.example.arclient.databinding.ActivityMainBinding;

import org.opencv.core.Mat;

import org.opencv.android.CameraBridgeViewBase;

public class MainActivity extends AppCompatActivity {


    // Used to load the 'arclient' library on application startup.
    static {
        System.loadLibrary("arclient");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());
        Mat mat = new Mat();

        CameraBridgeViewBase cb = new CameraBridgeViewBase();
        

    
    }

    // TODO : Foreground Service : https://developer.android.com/guide/components/services#java
    
    void foregroundMethod() {

        Intent notifiactionIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
            PendingIntent.getActivity(this, 0, notifiactionIntent, 0);

        Notification notification =
            new Notification.Builder(this, CHANNEL_DEFAULT_IMPORTANCE)
            .setContentTitle(getText(R.string.notification_title))
            .setContentText(getText(R.string.notification_message))
            .setSmallIcon(R.drawable.icon)
            .setContentIntent(pendingIntent)
            .setTicker(getText(R.string.ticker_text))
            .build();
  
    }


    /**
     * A native method that is implemented by the 'arclient' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();


}