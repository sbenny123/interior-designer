package com.project.horizoninteriordesigner.utils;

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

        /*return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + "HorizonInterior/" + dateStr + "_screenshot.jpg";*/

        //File sdCard = Environment.getExternalStorageDirectory();

        //return sdCard.getAbsolutePath();

        return dateStr + "_screenshot.jpg";
    }


    /**
     * Writes out bitmap to the file - describes the type of image.
     * @param bitmap
     * @param filename Name of the file
     * @throws IOException
     */
    public static void saveBitmapToDisk(Bitmap bitmap, String filename) throws IOException {

        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/HorizonInterior/");

        // Create directory if it does not already exist
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }

        File out = new File(dir, filename);

        try {
            FileOutputStream outputStream = new FileOutputStream(out);
            ByteArrayOutputStream outputData = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            throw new IOException("Failed to save bitmap to disk", e);
        }
    }
}
