package edu.cmu.cs.diamond.diamond.android;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import edu.cmu.cs.diamond.diamonddraid.R;
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

        try {
            byte[] me = loadImageFromRes(R.raw.me);
            isFace(me);
//            char[] notFace = loadImageFromRes(R.raw.not_face);
            //isFace(notFace);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
        
    
    private void isFace(byte[] img) throws IOException {
        Context context = this.getApplicationContext();
        Filter.loadFilters(context);
        Filter rgbFilter = new Filter(FilterEnum.RGBIMG, context, "RGB", null, null);
        while (rgbFilter.getNextOutputTag() != TagEnum.INIT);
        while (rgbFilter.getNextOutputTag() != TagEnum.GET);
        rgbFilter.sendBinary(img);
        byte[] rgbImage = rgbFilter.readByteArray();
    }

    private byte[] loadImageFromRes(int id) throws IOException {
        InputStream ins = this.getApplicationContext().getResources().openRawResource(id);
        return IOUtils.toByteArray(ins);
    }
}