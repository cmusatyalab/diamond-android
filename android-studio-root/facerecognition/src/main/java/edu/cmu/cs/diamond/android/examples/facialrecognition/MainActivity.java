/*
 *  Diamond Android - Android library for running Diamond filters
 *
 *  Copyright (c) 2013-2014 Carnegie Mellon University
 *  All Rights Reserved.
 *
 *  This software is distributed under the terms of the Eclipse Public
 *  License, Version 1.0 which can be found in the file named LICENSE.
 *  ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS SOFTWARE CONSTITUTES
 *  RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT
 */

package edu.cmu.cs.diamond.android.examples.facialrecognition;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import edu.cmu.cs.diamond.android.Filter;
import edu.cmu.cs.diamond.android.FilterException;
import edu.cmu.cs.diamond.android.examples.facialrecognition.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

public class MainActivity extends Activity {
    private final String TAG = this.getClass().getSimpleName();

    private Context context;
    
    private CameraPreview mCamera;
    private TextView mClassificationText;

    private Object frameLock = new Object();
    private byte[] frameBuffer;
    public boolean isRunning = true;
    
    private PreviewCallback previewCallback = new PreviewCallback() {
        public void onPreviewFrame(byte[] frame, Camera cam) {
            synchronized (frameLock) {
                frameBuffer = frame;
                frameLock.notify();
            }
        }
    };
    
    private Thread processingThread = new Thread() {
        public void run() {
            String[] faceFilterArgs = {"1.2", "24", "24", "1", "2"};
            InputStream ocvXmlIS = context.getResources().openRawResource(R.raw.haarcascade_frontalface);
            Filter rgbFilter, faceFilter;
            try {
                rgbFilter = new Filter(R.raw.rgbimg, context, "RGB", null, null);
                byte[] ocvXml = IOUtils.toByteArray(ocvXmlIS);
                faceFilter = new Filter(R.raw.ocv_face, context, "OCVFace",
                    faceFilterArgs, ocvXml);
            } catch (IOException e1) {
                Log.e(TAG, "Unable to create filter subprocess.");
                e1.printStackTrace();
                return;
            }
            
            while (isRunning) {
                byte[] data = null;
                synchronized(frameLock) {
                    while (frameBuffer == null){
                        try {
                            frameLock.wait();
                        } catch (InterruptedException e) {}
                    }
                    data = frameBuffer;
                    frameBuffer = null;
                }
                Log.d(TAG, "Got frame, trying to detect face.");
                Size cameraImageSize = mCamera.getCamera().getParameters().getPreviewSize();
                YuvImage image = new YuvImage(data, ImageFormat.NV21,
                    cameraImageSize.width, cameraImageSize.height, null);
                ByteArrayOutputStream tmpBuffer = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 90, tmpBuffer);
                byte[] jpgFrame = tmpBuffer.toByteArray();
                boolean faceInFrame = false;
                try {
                    faceInFrame = isFace(jpgFrame, rgbFilter, faceFilter);
                } catch (Exception e) {
                    mClassificationText.setText("Detection exception.");
                    e.printStackTrace();
                }

                final String classification;
                if (faceInFrame) {
                    Log.i(TAG, "Face detected.");
                    classification = getString(R.string.classificationTrue);
                } else {
                    Log.i(TAG, "No face detected.");
                    classification = getString(R.string.classificationFalse);
                }
                runOnUiThread(new Runnable() {
                   public void run() {
                       mClassificationText.setText(classification);
                       mClassificationText.invalidate();
                   }
                });
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();

        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED+
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON+
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mCamera = (CameraPreview) findViewById(R.id.cameraPreview);
//        mCamera.changeConfiguration(null, null, ImageFormat.NV21);
        mClassificationText = (TextView) findViewById(R.id.classificationText);

        mCamera.setPreviewCallback(previewCallback);
        processingThread.start();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
        try {
            processingThread.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private boolean isFace(byte[] jpegImage, Filter rgbFilter, Filter faceFilter) throws IOException, FilterException {
        final Map<String,byte[]> m = new HashMap<String,byte[]>();
        Log.d(TAG, "Sending JPEG image to RGB filter.");
        Log.d(TAG, "JPEG image size: " + String.valueOf(jpegImage.length) + " bytes.");

        m.put("", jpegImage);
        rgbFilter.process(m);
        byte[] rgbImage = m.get("_rgb_image.rgbimage");

        Log.d(TAG, "Obtained RGB image from RGB filter.");
        Log.d(TAG, "RGB image size: " + String.valueOf(rgbImage.length) + " bytes.");

        Log.d(TAG, "Sending RGB image to OCV face filter.");
        double faceRecognized = faceFilter.process(m);
        return Math.abs(faceRecognized-1.0d) < 1E-6;
    }
}
