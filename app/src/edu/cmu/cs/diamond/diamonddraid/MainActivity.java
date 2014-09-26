package edu.cmu.cs.diamond.diamonddraid;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
    @SuppressWarnings("unused")
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        char[] me = loadImageFromRes(R.raw.me);
        char[] notFace = loadImageFromRes(R.raw.not_face);
        
        isFace(me);
        //isFace(notFace);
    }
    
    private void isFace(char[] img) {
        Context context = this.getApplicationContext();
        Filter.loadFilters(context);
        Filter rgbFilter = new Filter(FilterEnum.RGBIMG, context, "RGB", null, null);
        String output = rgbFilter.readTag();
        while (!output.equals("init-success")) output = rgbFilter.readTag();
        Log.d(TAG, "Filter ready to start.");

        do {
            Log.d(TAG, "output: " + output);
            output = rgbFilter.readTag();
            if (output.equals("log")) {
                rgbFilter.readInt();
                rgbFilter.readString();
            }
        } while (!output.equals("get-attribute"));
        Log.d(TAG, "Getting first attribute.");
        output = rgbFilter.readString();
        Log.d(TAG, "Sending binary.");
        rgbFilter.sendBinary(img);
        rgbFilter.dumpStderrLine();
        rgbFilter.dumpStderrLine();
        
        do {
            Log.d(TAG, "output: " + output);
            output = rgbFilter.readTag();
        } while (!output.equals("set-attribute"));
        Log.d(TAG, "Set attribute.");
        output = rgbFilter.readString();
        Log.d(TAG, "out: " + output);
        if (output.isEmpty()) {
            Log.d(TAG, "Empty - Sending blank.");
            rgbFilter.sendBlank();
        } else {
            throw new RuntimeException("Unimplemented");
        }
    }

    private char[] loadImageFromRes(int id) {
        InputStream ins = this.getApplicationContext().getResources().openRawResource(id);
        try {
            byte[] buffer = new byte[ins.available()];
            ins.read(buffer);
            ins.close();
            Log.d(TAG, "Byte: " + Byte.toString(buffer[0]));
            Log.d(TAG, "Byte: " + Byte.toString(buffer[1]));
            return new String(buffer).toCharArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}