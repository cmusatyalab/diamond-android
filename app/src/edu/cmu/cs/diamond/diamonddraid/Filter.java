package edu.cmu.cs.diamond.diamonddraid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.util.Log;

public class Filter {
    @SuppressWarnings("unused")
    private final String TAG = this.getClass().getSimpleName();

    public Process proc;
    private BufferedReader br;
    private OutputStreamWriter os;

    public Filter(FilterEnum type, Context context, String name, String[] args, char[] blob) {
        File f = context.getFileStreamPath(getResourceName(type));
        try {
            Runtime RT = Runtime.getRuntime();
            proc = RT.exec(f.getAbsolutePath());
            br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            os = new OutputStreamWriter(proc.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        sendInt(1);
        sendString(name);
        sendStringArray(args);
        sendBinary(blob);
    }

    public static void loadFilters(Context context) {
        for (FilterEnum f : FilterEnum.values()) {
            InputStream ins = context.getResources().openRawResource(getResourceId(f));
            byte[] buffer;
            try {
                buffer = new byte[ins.available()];
                ins.read(buffer);
                ins.close();
                FileOutputStream fos = context.openFileOutput(
                    getResourceName(f), Context.MODE_PRIVATE
                );
                fos.write(buffer);
                fos.close();
                context.getFileStreamPath(getResourceName(f)).setExecutable(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void sendBlank() {
        try{
            os.write("\n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendString(String s) {
        try {
            os.write(Integer.toString(s.length()));
            sendBlank();
            os.write(s);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void sendStringArray(String[] a) {
        if (a != null) {
            for (String s : a) sendString(s);
        }
        sendBlank();
    }
    
    public void sendInt(int i) { sendString(Integer.toString(i)); }
    public void sendDouble(double d) { sendString(Double.toString(d)); }
    
    public void sendBinary(char[] b) {
        try {
            if (b == null) os.write('0');
            else os.write(Integer.toString(b.length));
            sendBlank();
            if (b != null) os.write(b);
            sendBlank();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String readTag() {
        try {
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String readString() {
        try {
            @SuppressWarnings("unused")
            int len = Integer.parseInt(br.readLine());
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public int readInt() {
        return Integer.parseInt(readString());
    }
    
    public void dumpStderrLine() {
        try {
            Log.d(TAG, "Err: " + new BufferedReader(
                new InputStreamReader(proc.getErrorStream())).readLine());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private static String getResourceName(FilterEnum f) {
        switch (f) {
            case DOG_TEXTURE: return "dog_texture";
            case GABOR_TEXTURE: return "gabor_texture";
            case IMG_DIFF: return "img_diff";
            case NULL_FIL: return "null_fil";
            case NUM_ATTR: return "num_attr";
            case OCV_FACE: return "ocv_face";
            case RGB_HISTOGRAM: return "rgb_histogram";
            case RGBIMG: return "rgbimg";
            case SHINGLING: return "shingling";
            case TEXT_ATTR: return "text_attr";
            case THUMBNAILER: return "thumbnailer";
            default: throw new RuntimeException("Could not find filter resource.");
        }
    }

    private static int getResourceId(FilterEnum f) {
        switch (f) {
            case DOG_TEXTURE: return R.raw.dog_texture;
            case GABOR_TEXTURE: return R.raw.gabor_texture;
            case IMG_DIFF: return R.raw.img_diff;
            case NULL_FIL: return R.raw.null_fil;
            case NUM_ATTR: return R.raw.num_attr;
            case OCV_FACE: return R.raw.ocv_face;
            case RGB_HISTOGRAM: return R.raw.rgb_histogram;
            case RGBIMG: return R.raw.rgbimg;
            case SHINGLING: return R.raw.shingling;
            case TEXT_ATTR: return R.raw.text_attr;
            case THUMBNAILER: return R.raw.thumbnailer;
            default: throw new RuntimeException("Could not find filter resource.");
        }
    }
}