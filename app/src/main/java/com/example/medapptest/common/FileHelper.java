package com.example.medapptest.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FileHelper {

    public static String CreateTempFile(Context context, Bitmap bitmap, String name) {
        try {
            String outputDir = "MedAppTest";
            File f = new File(Environment.getExternalStorageDirectory(), outputDir);
            if (!f.exists()) {
                f.mkdirs();
            }
            File imageFile = new File(f, name + ".jpg");
            OutputStream os;
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.IMAGE_QUALITY, os);
            os.flush();
            os.close();
            return imageFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return Constants.IMAGE_NAME; //invalid file name
        }

    }

}
