package com.example.horizoninteriordesigner.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class CameraUtils {

    /**
     * Generates a unique file name for the photo taken.
     * The file will be stored in the standard pictures directory under 'HorizonInterior'
     * @return
     */
    public static String generatePhotoFileName() {

        // Get current date and time - to ensure each file name is unique
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMddhhmmss");
        String dateStr = formatter.format(date);

        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + "HorizonInterior/" + dateStr + "_screenshot.jpg";
    }


    /**
     * Writes out bitmap to the file - describes the type of image.
     * @param bitmap
     * @param filename Name of the file
     * @throws IOException
     */
    public static void saveBitmapToDisk(Bitmap bitmap, String filename) throws IOException {

        File out = new File(filename);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(filename);
             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            throw new IOException("Failed to save bitmap to disk", ex);
        }
    }
}
