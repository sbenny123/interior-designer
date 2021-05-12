package com.project.horizoninteriordesigner;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.google.common.truth.Truth.assertThat;
import static com.project.horizoninteriordesigner.utils.CameraUtils.generatePhotoFileName;


public class CameraUtilsUnitTest {

    /**
     * Tests that date is used - for uniqueness
     */
    @Test
    public void photoFileName_isValid() {
        String dateStr = (new SimpleDateFormat("yyMMddhhmmss").format(new Date()));

        assertThat(generatePhotoFileName()).contains(dateStr);
    }

    /**
     * Tests that the file ends with .jpg
     */
    @Test
    public void photoFileName_isJPG() {
        assertThat(generatePhotoFileName()).endsWith(".jpg");
    }
}