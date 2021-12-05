package com.example.arclient._castable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.arclient.Castable;

import org.opencv.android.Utils;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.nio.ByteBuffer;

public class DisplayBridge implements Castable {

    private static int width;
    private static int height;
    private static int dpi;
    private static int flags;
    private static Context _context;

    @SuppressLint("StaticFieldLeak")

    private static MediaProjectionManager mediaProjectionManager;
    private static MediaProjection mediaProjection;
    private static VirtualDisplay virtualDisplay;
    private static ImageReader imageReader;
    private static ActivityResultLauncher<Intent> projection;
    private static DisplayMetrics displayMetrics;

    @Override
    public void cast(Object... obj) {

        System.loadLibrary("opencv_java4");

        _context = (Context) obj[0];
        ActivityResultCallback<ImageReader> _callback = (ActivityResultCallback<ImageReader>) obj[1];

        mediaProjectionManager = (MediaProjectionManager) _context
                .getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        projection = ((AppCompatActivity) _context)
                .registerForActivityResult(new MediaProjectionResultContract<ImageReader>(), _callback);

    }

    @Override
    public void launch(Object... obj) {

        projection.launch(mediaProjectionManager.createScreenCaptureIntent());

    }

    @SuppressLint("WrongConstant")
    public static void test(int resultCode, @Nullable Intent intent) {


        displayMetrics = new DisplayMetrics();
        ((WindowManager) _context
                .getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        dpi = displayMetrics.densityDpi;

        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2);
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, intent);
        virtualDisplay = mediaProjection.createVirtualDisplay(

                "Q",
                width,
                height,
                dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(),
                null,
                null

        );

        imageReader.setOnImageAvailableListener((reader)
        -> {

            Image image = reader.acquireLatestImage();
            ByteBuffer bb = image.getPlanes()[0].getBuffer();
            byte[] data = new byte[bb.remaining()];
            bb.get(data);

            Mat mat = new Mat();
            //Utils.bitmapToMat(new Bitmap(),mat);
            image.close();

        } , null);
    }

    public static class MediaProjectionResultContract<O> extends ActivityResultContract<Intent, O> {

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Intent input) {

            return input;

        }

        @Override
        public O parseResult(int resultCode, @Nullable Intent intent) {

            test(resultCode, intent);
            return (resultCode == Activity.RESULT_OK && intent != null)
                    ? (O) imageReader
                    : null;


        }
    }
}
