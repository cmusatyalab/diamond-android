package edu.cmu.cs.diamond.android.examples.facialrecognition;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import edu.cmu.cs.diamond.android.Filter;
import edu.cmu.cs.diamond.android.FilterEnum;
import edu.cmu.cs.diamond.android.TagEnum;
import edu.cmu.cs.diamond.android.examples.facialrecognition.R;
import edu.cmu.cs.diamond.android.token.*;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
    private final String TAG = this.getClass().getSimpleName();

    private Context context;
    private Filter rgbFilter;
    private Filter faceFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getApplicationContext();

        try {
            Filter.loadFilters(context);

            Log.d(TAG, "Creating RGB filter.");
            rgbFilter = new Filter(FilterEnum.RGBIMG, context, "RGB", null, null);
            while (rgbFilter.getNextToken().tag != TagEnum.INIT);
            Log.d(TAG, "RGB filter initialized.");
            while (rgbFilter.getNextToken().tag != TagEnum.GET);
            Log.d(TAG, "RGB filter ready to receive input.");

            Log.d(TAG, "Creating OCV face filter.");
            String[] faceFilterArgs = {"1.2", "24", "24", "1", "2"};
            InputStream ocvXmlIS = context.getResources().openRawResource(R.raw.haarcascade_frontalface);
            byte[] ocvXml = IOUtils.toByteArray(ocvXmlIS);
            faceFilter = new Filter(FilterEnum.OCV_FACE, context, "OCVFace",
                faceFilterArgs, ocvXml);
            while (faceFilter.getNextToken().tag != TagEnum.INIT);
            Log.d(TAG, "OCV face filter initialized.");

            Log.d(TAG, "Checking image with a face.");
            byte[] me = loadImageFromRes(R.raw.me);
            isFace(me);

            Log.d(TAG, "Checking image without a face.");
            byte[] notFace = loadImageFromRes(R.raw.not_face);
            isFace(notFace);

            rgbFilter.destroy();
            faceFilter.destroy();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
        
    
    private void isFace(byte[] jpegImage) throws IOException {
        Log.d(TAG, "Sending JPEG image to RGB filter.");
        Log.d(TAG, "JPEG image size: " + String.valueOf(jpegImage.length) + " bytes.");
        rgbFilter.sendBinary(jpegImage);

        Log.d(TAG, "Receiving RGB output buffer.");
        byte[] rgbImage = null;
        while (rgbImage == null) {
            Token t = rgbFilter.getNextToken();
            if (t.tag == TagEnum.SET) {
                SetToken st = (SetToken) t;
                if (st.var.equals("_rgb_image.rgbimage")) {
                    rgbImage = st.buf;
                }
            }
        }
        Log.d(TAG, "Obtained RGB image from RGB filter.");
        Log.d(TAG, "RGB image size: " + String.valueOf(rgbImage.length) + " bytes.");
        Log.d(TAG, "Preparing RGB filter for next input.");
        boolean rgbReady = false;
        while (!rgbReady) {
            Token t = rgbFilter.getNextToken();
            if (t.tag == TagEnum.OMIT) {
                rgbFilter.sendString("false");
            } else if (t.tag == TagEnum.RESULT) {
                rgbReady = true;
            }
        }

        Log.d(TAG, "Sending RGB image to OCV face filter.");
        faceFilter.sendBinary(rgbImage);
        boolean foundResult = false;
        while (!foundResult) {
            Token t = faceFilter.getNextToken();
            if (t.tag == TagEnum.RESULT) {
                ResultToken rt = (ResultToken) t;
                Log.d(TAG, "Result: " + String.valueOf(rt.var));
                foundResult = true;
            }
        }
    }

    private byte[] loadImageFromRes(int id) throws IOException {
        InputStream ins = this.getApplicationContext().getResources().openRawResource(id);
        return IOUtils.toByteArray(ins);
    }
}