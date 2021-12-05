package com.example.arclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.arclient.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Streamizer _stream;


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

        _init();

    }
    public void _init() {

        _stream = Streamizer._build(this);
        _stream._init();

        ((Button) findViewById(R.id.startbutton))

                .setOnClickListener((View v) -> { _stream._launch(); });

        ((Button) findViewById(R.id.stopbutton))

                .setOnClickListener((View v) -> { _stream._stop(); });

    }
    /**
     * A native method that is implemented by the 'arclient' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();


}