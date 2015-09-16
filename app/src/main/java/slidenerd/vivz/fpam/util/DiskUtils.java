package slidenerd.vivz.fpam.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by vivz on 30/07/15.
 */
public class DiskUtils {
    public static void writeToCache(Context context, String data) {

        File file;
        FileOutputStream outputStream;
        try {
            file = new File(context.getCacheDir(), "FpamCache");
            outputStream = new FileOutputStream(file);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFromCache(Context context) {
        BufferedReader input = null;
        File file = null;
        String data = null;
        try {
            file = new File(context.getCacheDir(), "FpamCache"); // Pass getFilesDir() and "MyFile" to read file

            input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = input.readLine()) != null) {
                buffer.append(line + "\n");
            }
            data = buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
