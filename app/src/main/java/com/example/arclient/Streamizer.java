package com.example.arclient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.ImageReader;
import android.util.Log;
import android.widget.EditText;

import androidx.activity.result.ActivityResultCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.dto.DTO;
import com.dto.KeyPointDTO;
import com.example.arclient._castable.DisplayBridge;
import com.example.arclient._castable.ForegroundService;

import org.opencv.android.Utils;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.ORB;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Streamizer extends Castable.Streamable {

    private static final String TAG = "Streamizer";
    @SuppressLint("StaticFieldLeak")
    private static final Streamizer _instance = new Streamizer();
    private Context _context;
    private Castable _service;
    private Castable _projection;
    private SocketBridge _socketBridge;
    private MatOfKeyPoint kp;
    private ORB _ORB;
    private DTO dto = null;

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


        _socketBridge = new SocketBridge(getIP());
        _socketBridge.launch();
        _service.launch(_context);
        _projection.launch((ImageReader.OnImageAvailableListener) (reader)
        -> {

            Log.d(TAG, "_launch: image refreshed");
            Mat screen = getMat(reader);
            dto = (screen != null)
                    ? getSerializableFromImage(screen)
                    : dto;

            if (dto != null) {
                _socketBridge.stream(dto);
            }
        });
    }
    @Override
    void _stop(Object... obj) {

        ((ForegroundService) _service).stop(_context);
        DisplayBridge.release();
        _socketBridge.release();

    }
    public String getIP() {

        EditText editText = ((AppCompatActivity) _context)
                .findViewById(R.id.inputText);
        String ip = editText.getText().toString();

        return ip;
    }
    public DTO getSerializableFromImage(Mat screen) {

        MatOfKeyPoint keyPoint = new MatOfKeyPoint();
        Mat descriptor = new Mat();
        Mat gray = new Mat();

        Imgproc.cvtColor(screen, gray, Imgproc.COLOR_RGBA2GRAY);

        _ORB.detectAndCompute(gray, new Mat(), keyPoint, descriptor);
        KeyPoint[] kpArray = keyPoint.toArray();

        int length_d = (int) (descriptor.total() * descriptor.elemSize());
        int row_d = descriptor.rows();
        int col_d = descriptor.cols();
        int type_d = descriptor.type();

        byte[] descriptorBuffer = new byte[length_d];
        descriptor.get(0, 0, descriptorBuffer);

        DTO dto = new DTO();
        dto.dLength = length_d;
        dto.dBuffer = descriptorBuffer;
        dto.dCol = col_d;
        dto.dRow = row_d;
        dto.dType = type_d;
        dto.kp = new KeyPointDTO[0];
        ArrayList<KeyPointDTO> keyPointDTOArrayList = new ArrayList<KeyPointDTO>();

        for (KeyPoint point : kpArray) {

            KeyPointDTO kp = new KeyPointDTO();
            kp.setFromKeyPoint(point);
            keyPointDTOArrayList.add(kp);

        }
        dto.kp = keyPointDTOArrayList.toArray(dto.kp);

        return dto;

    }
    public MatOfKeyPoint getKeyPoint(Mat mat) throws IOException {

        MatOfKeyPoint keyPoint = new MatOfKeyPoint();
        Mat descriptor = new Mat();
        Mat gray = new Mat();

        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_RGBA2GRAY);

        _ORB.detectAndCompute(gray, new Mat(), keyPoint, descriptor);
        KeyPoint[] abc = keyPoint.toArray();
        MatOfKeyPoint testmat = new MatOfKeyPoint(abc);

        int length_d = (int) (descriptor.total() * descriptor.elemSize());
        int row_d = descriptor.rows();
        int col_d = descriptor.cols();
        int type_d = descriptor.type();

        byte[] desbuffer = new byte[length_d];
        descriptor.get(0, 0, desbuffer);

        // Make DTO

        DTO dto = new DTO();
        dto.dLength = length_d;
        dto.dBuffer = desbuffer;
        dto.dCol = col_d;
        dto.dRow = row_d;
        dto.dType = type_d;
        dto.kp = new KeyPointDTO[0];
        ArrayList<KeyPointDTO> kpArray = new ArrayList<KeyPointDTO>();

        for (KeyPoint point : abc) {

            KeyPointDTO kp = new KeyPointDTO();
            kp.setFromKeyPoint(point);
            kpArray.add(kp);

        }
        dto.kp = kpArray.toArray(dto.kp);

        // -> To Serialize

        byte[] samplebytes = toByteArray(dto);

        // Deserialize

        DTO sampleDeserialize = (DTO) toObject(samplebytes);



        /*
        // Serialize

        int length_d = (int) (descriptor.total() * descriptor.elemSize());
        int row_d = descriptor.rows();
        int col_d = descriptor.cols();
        int type_d = descriptor.type();

        byte[] desbuffer = new byte[length_d];
        descriptor.get(0, 0, desbuffer);

        // Deserialize

        Mat desdesMat = new Mat(row_d, col_d, type_d);
        desdesMat.put(0, 0, desbuffer);





        // Serialize

        int length = (int) (keyPoint.total() * keyPoint.elemSize());
        int row = keyPoint.rows();
        int col = keyPoint.cols();
        int type = keyPoint.type();

        byte[] kpbuffer = new byte[length];
        keyPoint.get(0,0, kpbuffer);

        // Deserialize

        Mat deserialMat = new Mat(row, col, type);
        deserialMat.put(0, 0, kpbuffer);

         */

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

    public byte[] toByteArray (Object obj)
    {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            oos.close();
            bos.close();
            bytes = bos.toByteArray ();
        }
        catch (IOException ex) {
            //TODO: Handle the exception
        }
        return bytes;
    }

    public Object toObject (byte[] bytes)
    {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream (bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
        }
        catch (IOException | ClassNotFoundException ex) {
            //TODO: Handle the exception
        }
        return obj;
    }

}
