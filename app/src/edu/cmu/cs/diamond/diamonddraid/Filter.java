package edu.cmu.cs.diamond.diamonddraid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.util.Log;

public class Filter {
    private final String TAG = this.getClass().getSimpleName();

    public Process proc;
    private BufferedReader br;
    private OutputStream os;

    public Filter(FilterEnum type, Context context, String name, String[] args, byte[] blob) throws IOException {
        File f = context.getFileStreamPath(getResourceName(type));
        try {
            Runtime RT = Runtime.getRuntime();
            proc = RT.exec(f.getAbsolutePath());
            br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            os = proc.getOutputStream();
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
    
    public void sendBlank() throws IOException {
        IOUtils.write("\n", os);
        os.flush();
    }

    public void sendString(String s) throws IOException {
        IOUtils.write(Integer.toString(s.length()), os);
        sendBlank();
        IOUtils.write(s, os);
        os.flush();
    }
    
    public void sendStringArray(String[] a) throws IOException {
        if (a != null) {
            for (String s : a) sendString(s);
        }
        sendBlank();
    }
    
    public void sendInt(int i) throws IOException { sendString(Integer.toString(i)); }
    public void sendDouble(double d) throws IOException { sendString(Double.toString(d)); }
    
    public void sendBinary(byte[] b) throws IOException {
        if (b == null) IOUtils.write("0", os);
        else IOUtils.write(Integer.toString(b.length), os);
        sendBlank();
        if (b != null) IOUtils.write(b, os);
        sendBlank();
    }
    
    public String readTag() throws IOException {
            return br.readLine();
    }
    
    public String readString() throws NumberFormatException, IOException {
            @SuppressWarnings("unused")
            int len = Integer.parseInt(br.readLine());
            return br.readLine();
    }
    
    public int readInt() throws NumberFormatException, IOException {
        return Integer.parseInt(readString());
    }
    
    public void dumpStdoutAndStderr() throws IOException {
        Log.d(TAG, "stdout: " + IOUtils.toString(proc.getInputStream()));
        Log.d(TAG, "stderr: " + IOUtils.toString(proc.getErrorStream()));
    }
    
    private static String getResourceName(FilterEnum f) {
        switch (f) {
            case DOG_TEXTURE: return "dog_texture";
            case GABOR_TEXTURE: return "gabor_texture";
            case IMG_DIFF: return "img_diff";
            case NULL_FILTER: return "null_filter";
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
            case NULL_FILTER: return R.raw.null_filter;
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