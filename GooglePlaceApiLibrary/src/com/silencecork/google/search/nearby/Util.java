package com.silencecork.google.search.nearby;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class Util {
    private static final String TAG = "Util";
    private static final int DEFAULT_TIMEOUT = 20000;
    
    public static String streamToString(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len;
            int size = 0;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
                size += len;
            }
            if (size <= 0) {
                return null;
            }
            return out.toString();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    public static boolean streamToFile(InputStream in, String name) throws IOException {
        FileOutputStream out = new FileOutputStream(name);
        try {
            byte[] buffer = new byte[1024];
            int len;
            int size = 0;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
                size += len;
            }
            if (size <= 0) {
                return false;
            }
            
        } finally {
            if (out != null) {
                out.close();
            }
        }
        Log.d(TAG, "output to file complete");
        return true;
    }
    
    public static String get(String strUrl) throws IOException {
        URL url = new URL(strUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(DEFAULT_TIMEOUT);
        
        int code = conn.getResponseCode();
        Log.i(TAG, "response code " + code); 
        
        if (code != 200) {
            String msg = conn.getResponseMessage();
            InputStream errStream = conn.getErrorStream();
            String errMsg = Util.streamToString(errStream);
            Log.e(TAG, "transmission error, response code " + code + ", message " + msg + ", errMsg " + errMsg);
            return null;
        } 
        
        InputStream in = conn.getInputStream();
        String content = Util.streamToString(in);
//        Util.streamToFile(in, "/sdcard/place_matrix.txt");
        
        
//        Log.d(TAG, "content " + content);
        return content;
//        return null;
    }
}
