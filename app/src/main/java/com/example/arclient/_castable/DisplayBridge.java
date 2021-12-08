package com.example.arclient._castable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.HardwareBuffer;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.grafika.gles.EglCore;
import com.android.grafika.gles.FullFrameRect;
import com.android.grafika.gles.OffscreenSurface;
import com.android.grafika.gles.Texture2dProgram;
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
import java.nio.ByteOrder;

public class DisplayBridge implements Castable {

    private static final String TAG = "DisplayBridge";

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
    private static ImageReader.OnImageAvailableListener imageAvailableListener;

    @Override
    public void cast(Object... obj) {

        _context = (Context) obj[0];
        ActivityResultCallback<ImageReader> _callback = (ActivityResultCallback<ImageReader>) obj[1];

        mediaProjectionManager = (MediaProjectionManager) _context
                .getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        projection = ((AppCompatActivity) _context)
                .registerForActivityResult(new MediaProjectionResultContract<ImageReader>(), _callback);

    }

    @Override
    public void launch(Object... obj) {

        imageAvailableListener = (ImageReader.OnImageAvailableListener) obj[0];
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

        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, intent);

        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2);

        // EGLSurface

        /*
        EglCore eglCore = new EglCore(null, EglCore.FLAG_TRY_GLES3);
        OffscreenSurface eglConsumerSide = new OffscreenSurface(eglCore, width, height);
        eglConsumerSide.makeCurrent();

        Texture2dProgram eglShader = new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT);
        FullFrameRect eglScreen = new FullFrameRect(eglShader);

        int eglTextureID = eglScreen.createTextureObject();
        @SuppressLint("Recycle")
        SurfaceTexture eglTexture = new SurfaceTexture(eglTextureID, false);
        eglTexture.setDefaultBufferSize(width,height);

        ByteBuffer eglBuffer = ByteBuffer.allocate(width * height * 4);
        eglBuffer.order(ByteOrder.nativeOrder());


        Bitmap eglCurrentBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Surface eglProducerSide = new Surface(eglTexture);
        eglTexture.setOnFrameAvailableListener((surfaceTexture)
        -> {

            Float[] eglMatrix;

            eglConsumerSide.makeCurrent();
            eglTexture.updateTexImage();
            eglTexture.get
            eglTexture.getTransformMatrix();

        });  */

        //

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

        imageReader.setOnImageAvailableListener(imageAvailableListener, null);


    }
    public static void release() {

        virtualDisplay.release();

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
            Log.d(TAG, "parseResult");
            return (resultCode == Activity.RESULT_OK && intent != null)
                    ? (O) imageReader
                    : null;


        }
    }
    public interface OnLaunchCallback extends ImageReader.OnImageAvailableListener {}
}
