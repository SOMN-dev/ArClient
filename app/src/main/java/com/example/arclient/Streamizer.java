package com.example.arclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.ImageReader;

import androidx.activity.result.ActivityResultCallback;

import com.example.arclient._castable.DisplayBridge;
import com.example.arclient._castable.ForegroundService;

public class Streamizer extends Castable.Streamable {

    private static final String TAG = "Streamizer";
    @SuppressLint("StaticFieldLeak")
    private static final Streamizer _instance = new Streamizer();
    private Context _context;
    private Castable _service;
    private Castable _projection;

    private Streamizer () {}

    public static Streamizer _build(Object... obj) {

        _instance._context = (Context) obj[0];
        _instance._service = new ForegroundService();
        _instance._projection = new DisplayBridge();

        return _instance;

    }

    @Override
    void _init() {

        _service.cast();
        _projection.cast(_context, (ActivityResultCallback<ImageReader>) (result)
        -> {




        });

    }

    @Override
    void _launch(Object... obj) {

        _service.launch(_context);
        _projection.launch();

    }

    @Override
    void _stop(Object... obj) {

        ((ForegroundService) _service).stop(_context);

    }

}
