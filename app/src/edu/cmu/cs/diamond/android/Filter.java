package edu.cmu.cs.diamond.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class Filter {
    private final String TAG = this.getClass().getSimpleName();

    public Process proc;
    private InputStream is;
    private BufferedReader br;
    private OutputStream os;
    private File tempDir;

    public Filter(FilterEnum type, Context context, String name, String[] args, byte[] blob) throws IOException {
        File f = context.getFileStreamPath(context.getResources().getResourceEntryName(type.id));
        try {
            ProcessBuilder pb = new ProcessBuilder(f.getAbsolutePath());
            Map<String,String> env = pb.environment();
            tempDir = File.createTempFile("filter", null, context.getCacheDir());
            tempDir.delete(); // Delete file and create directory.
            if (!tempDir.mkdir()) {
                throw new IOException("Unable to create temporary directory.");
            }
            env.put("TEMP", tempDir.getAbsolutePath());
            env.put("TMPDIR", tempDir.getAbsolutePath());
            proc = pb.start();
            is = proc.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            os = proc.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        sendInt(1);
        sendString(name);
        sendStringArray(args);
        sendBinary(blob);
    }
    
    public void destroy() {
        proc.destroy();
        try {
            FileUtils.deleteDirectory(tempDir);
        } catch (IOException e) {
            Log.i(TAG, "Failed to destroy temporary directory '"
                + tempDir.getAbsolutePath() + "'.");
        }
    }

    public static void loadFilters(Context context) throws IOException {
        Resources r = context.getResources();
        for (FilterEnum f : FilterEnum.values()) {
            InputStream ins = r.openRawResource(f.id);
            String name = r.getResourceEntryName(f.id);
            byte[] buf = IOUtils.toByteArray(ins);
            FileOutputStream fos = context.openFileOutput(name, Context.MODE_PRIVATE);
            IOUtils.write(buf, fos);
            context.getFileStreamPath(name).setExecutable(true);
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

    public String readTagStr() throws IOException {
        return br.readLine();
    }

    public String readString() throws NumberFormatException, IOException {
        int len = Integer.parseInt(br.readLine());
        char[] buf = new char[len];
        IOUtils.read(br, buf, 0, len);
        br.read();
        Log.d(TAG, Integer.toString(len));
        Log.d(TAG, new String(buf));
        return new String(buf);
    }

    public int readInt() throws NumberFormatException, IOException {
        return Integer.parseInt(readString());
    }
    
    public byte[] readByteArray() throws IOException {
        int len = Integer.parseInt(br.readLine());
        byte[] buf = new byte[len];
        IOUtils.read(is, buf, 0, len);
        br.read();
        Log.d(TAG, Integer.toString(len));
        Log.d(TAG, new String(buf));
        return buf;
    }
    
    public TagEnum getNextOutputTag() throws IOException {
        TagEnum tag = TagEnum.findByStr(readTagStr());
        switch (tag) {
            case LOG:
                readInt();
                readString();
                break;
            default:
                break;
        }
        return tag;
    }

    public void dumpStdoutAndStderr() throws IOException {
        new Thread(new Runnable() {
            public void run() {
                try {
                    while (true) {
                        Log.d(TAG, "stdout: "+br.readLine());
                    }
                } catch (IOException e) { e.printStackTrace(); }
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {
                try {
                    BufferedReader err_br = new BufferedReader(new InputStreamReader(
                        proc.getErrorStream()));
                    while (true) {
                        Log.d(TAG, "stderr: "+err_br.readLine());
                    }
                } catch (IOException e) { e.printStackTrace(); }
            }
        }).start();
    }
    
}