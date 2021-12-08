package com.example.arclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;
import android.util.Log;

import androidx.activity.result.ActivityResultCallback;

import com.example.arclient._castable.DisplayBridge;
import com.example.arclient._castable.ForegroundService;

import org.opencv.android.Utils;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;
import java.util.List;

public class Streamizer extends Castable.Streamable {

    private static final String TAG = "Streamizer";
    @SuppressLint("StaticFieldLeak")
    private static final Streamizer _instance = new Streamizer();
    private Context _context;
    private Castable _service;
    private Castable _projection;
    private MatOfKeyPoint kp;
    private ORB _ORB;

    private static final float targetThreshold = 35.0f;
    private static final int hessianThreshold = 400;

    private Streamizer () {}

    public static Streamizer _build(Object... obj) {

        System.loadLibrary("opencv_java4");

        _instance._context = (Context) obj[0];
        _instance._service = new ForegroundService();
        _instance._projection = new DisplayBridge();
        _instance._ORB = ORB.create(hessianThreshold);

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
        _projection.launch((ImageReader.OnImageAvailableListener) (reader)
        -> {

            Log.d(TAG, "_launch: image refreshed");

            Mat mat = getMat(reader);
            kp = (mat != null)
                    ? getKeyPoint(mat)
                    : kp;

        });

    }

    @Override
    void _stop(Object... obj) {

        ((ForegroundService) _service).stop(_context);
        DisplayBridge.release();

    }

    public MatOfKeyPoint getKeyPoint(Mat mat) {

        MatOfKeyPoint keyPoint = new MatOfKeyPoint();
        Mat descriptor = new Mat();
        Mat gray = new Mat();

        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_RGBA2GRAY);

        _ORB.detectAndCompute(gray, gray, keyPoint, descriptor);
        KeyPoint abc[] = keyPoint.toArray();
        List<KeyPoint> adb = keyPoint.toList();
        String acc[] = { "abcd", "adfgr", "cdfd" };

        return keyPoint;

    }
    public Mat getMat(ImageReader reader) {

        Image image = reader.acquireLatestImage();

        if (image != null) {

            int width = reader.getWidth();
            int height = reader.getHeight();

            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;

            Bitmap bitmap = Bitmap
                    .createBitmap(
                            width + rowPadding / pixelStride,
                            height,
                            Bitmap.Config.ARGB_8888);

            bitmap.copyPixelsFromBuffer(buffer);

            Mat mat = new Mat();

            Utils.bitmapToMat(bitmap, mat);
            image.close();

            return mat;
        }
        else return null;
    }

}
