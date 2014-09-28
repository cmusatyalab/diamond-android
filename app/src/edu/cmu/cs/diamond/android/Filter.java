package edu.cmu.cs.diamond.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import edu.cmu.cs.diamond.android.token.*;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class Filter {
    private final String TAG = this.getClass().getSimpleName();

    public Process proc;
    private InputStream is;
    private OutputStream os;
    private File tempDir;

    public Filter(FilterEnum type, Context context, String name, String[] args, byte[] blob) throws IOException {
        File f = context.getFileStreamPath(context.getResources().getResourceEntryName(type.id));
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
        os = proc.getOutputStream();

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
        sendBlank();
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

    public static String getHash(byte[] b) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(b);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public void sendBinary(byte[] b) throws IOException {
        if (b == null) {
            IOUtils.write("0", os);
        }
        else {
            Log.d(TAG, "SendingBinary: " + getHash(b));
            IOUtils.write(Integer.toString(b.length), os);
        }
        sendBlank();
        if (b != null) {
            os.write(b);
        }
        sendBlank();
    }


    private String readLine() throws IOException {
        StringBuilder buf = new StringBuilder();
        char c = 0;
        do {
            c = (char) is.read();
            if (c != '\n' && c != '\0') buf.append(c);
        } while (c != '\n' && c != '\0');
        return buf.toString();
    }

    public String readTagStr() throws IOException {
        return readLine();
    }
    
    public String readString() throws NumberFormatException, IOException {
        int len = Integer.parseInt(readLine());
        byte[] buf = new byte[len];
        is.read(buf, 0, len);
        is.read();
        return new String(buf);
    }

    public double readDouble() throws NumberFormatException, IOException {
        return Double.parseDouble(readString());
    }
    public int readInt() throws NumberFormatException, IOException {
        return Integer.parseInt(readString());
    }
    
    public byte[] readByteArray() throws IOException {
        int len = Integer.parseInt(readLine());
        byte[] buf = new byte[len];
        is.read(buf, 0, len);
        is.read();
        return buf;
    }
    
    public Token getNextToken() throws IOException {
        String tagString = readTagStr();
        Log.d(TAG, "=== getNextToken");
        Log.d(TAG, "tag: " + tagString);
        TagEnum tag = TagEnum.findByStr(tagString);
        switch (tag) {
            case LOG:
                int logLevel = readInt();
                String msg = readString();
                Log.d(TAG, "  msg: " + msg);
                return new LogToken(logLevel, msg);
            case GET:
                String getVar = readString();
                Log.d(TAG, "  var: " + getVar);
                return new GetToken(getVar);
            case SET:
                String setVar = readString();
                Log.d(TAG, "  set: " + setVar);
                byte[] buf = readByteArray();
                return new SetToken(setVar, buf);
            case RESULT:
                double resVar = readDouble();
                return new ResultToken(resVar);
            default:
                return new Token(tag);
        }
    }

    public void dumpStdoutAndStderr() throws IOException {
        new Thread(new Runnable() {
            public void run() {
                try {
                    while (true) {
                        Log.d(TAG, "stdout: "+readLine());
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