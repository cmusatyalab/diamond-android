/*
 *  Diamond Android - Diamond filters for the Android platform
 *
 *  Copyright (c) 2013-2014 Carnegie Mellon University
 *  All Rights Reserved.
 *
 *  This software is distributed under the terms of the Eclipse Public
 *  License, Version 1.0 which can be found in the file named LICENSE.
 *  ANY USE, REPRODUCTION OR DISTRIBUTION OF THIS SOFTWARE CONSTITUTES
 *  RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT
 */

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
    public String name;
    private InputStream is;
    private OutputStream os;
    private File tempDir;

    public Filter(int resourceId, Context context, String name, String[] args, byte[] blob) throws IOException {
        this.name = name;
        Resources r = context.getResources();
        String resourceName = r.getResourceEntryName(resourceId);
        File f = context.getFileStreamPath(resourceName);

        if (!f.exists()) {
            InputStream ins = r.openRawResource(resourceId);
            byte[] buf = IOUtils.toByteArray(ins);
            FileOutputStream fos = context.openFileOutput(resourceName, Context.MODE_PRIVATE);
            IOUtils.write(buf, fos);
            context.getFileStreamPath(resourceName).setExecutable(true);
            fos.close();
        }

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

        while (this.getNextToken().tag != TagEnum.INIT);
        Log.d(TAG, "Filter initialized.");
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

//    public static void loadFilters(Context context) throws IOException {
//        Resources r = context.getResources();
//        for (FilterEnum f : FilterEnum.values()) {
//            InputStream ins = r.openRawResource(f.id);
//            String name = r.getResourceEntryName(f.id);
//            byte[] buf = IOUtils.toByteArray(ins);
//            FileOutputStream fos = context.openFileOutput(name, Context.MODE_PRIVATE);
//            IOUtils.write(buf, fos);
//            context.getFileStreamPath(name).setExecutable(true);
//        }
//    }
    
    public double process(Map<String, byte[]> m) throws IOException, FilterException {
        while (true) {
            Token t = this.getNextToken();
            switch (t.tag) {
                case GET:
                    GetToken gt = (GetToken) t;
                    if (!m.containsKey(gt.var)) {
                        throw new FilterException("Value not found in map.");
                    }
                    this.sendBinary(m.get(gt.var));
                    break;
                case SET:
                    SetToken st = (SetToken) t;
                    m.put(st.var, st.buf);
                    break;
                case RESULT:
                    ResultToken rt = (ResultToken) t;
                    return rt.var;
                case OMIT:
                    OmitToken ot = (OmitToken) t;
                    this.sendBoolean(m.containsKey(ot.var));
                case LOG:
                    break;
                default:
                    throw new FilterException("Unimplemented Token found.");
            }
        }
    }
    
    private void sendBlank() throws IOException {
        IOUtils.write("\n", os);
        os.flush();
    }
    
    private void sendBoolean(boolean b) throws IOException {
        if (b) sendString("true");
        else sendString("false");
    }

    private void sendString(String s) throws IOException {
        IOUtils.write(Integer.toString(s.length()), os);
        sendBlank();
        IOUtils.write(s, os);
        sendBlank();
        os.flush();
    }
    
    private void sendStringArray(String[] a) throws IOException {
        if (a != null) {
            for (String s : a) sendString(s);
        }
        sendBlank();
    }
    
    private void sendInt(int i) throws IOException { sendString(Integer.toString(i)); }
    private void sendDouble(double d) throws IOException { sendString(Double.toString(d)); }

    private static String getHash(byte[] b) {
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

    private void sendBinary(byte[] b) throws IOException {
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

    private String readTagStr() throws IOException {
        return readLine();
    }
    
    private String readString() throws NumberFormatException, IOException {
        int len = Integer.parseInt(readLine());
        byte[] buf = new byte[len];
        int readLen = IOUtils.read(is, buf, 0, len);
        if (len != readLen) {
            throw new RuntimeException("Error reading all bytes from the InputStream.");
        }
        is.read();
        return new String(buf);
    }

    private double readDouble() throws NumberFormatException, IOException {
        return Double.parseDouble(readString());
    }
    private int readInt() throws NumberFormatException, IOException {
        return Integer.parseInt(readString());
    }
    
    private byte[] readByteArray() throws IOException {
        int len = Integer.parseInt(readLine());
        byte[] buf = new byte[len];
        int readLen = IOUtils.read(is, buf, 0, len);
        if (len != readLen) {
            throw new RuntimeException("Error reading all bytes from the InputStream.");
        }
        is.read();
        return buf;
    }
    
    private Token getNextToken() throws IOException {
        String tagString = readTagStr();
        Log.d(TAG, "=== getNextToken");
        Log.d(TAG, "tag: " + tagString);
        TagEnum tag = TagEnum.findByStr(tagString);
        switch (tag) {
            case INIT:
                return new Token(tag);
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
                Log.d(TAG, "  buf.len: " + String.valueOf(buf.length));
                return new SetToken(setVar, buf);
            case RESULT:
                double resVar = readDouble();
                return new ResultToken(resVar);
            case OMIT:
                String omitVar = readString();
                Log.d(TAG, "  var: " + omitVar);
                return new OmitToken(omitVar);
            default:
                Log.i(TAG, "getNextToken: Warning: " + tagString + " unimplemented.");
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
