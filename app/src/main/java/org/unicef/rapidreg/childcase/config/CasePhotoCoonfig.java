package org.unicef.rapidreg.childcase.config;

import android.os.Environment;

public class CasePhotoCoonfig {
    public static final int MAX_WIDTH = 1080;
    public static final int MAX_HEIGHT = 1920;
    public static final int MAX_SIZE_KB = 1000;
    public static final String MEDIA_PATH_FOR_CAMERA = Environment.getExternalStorageDirectory() +
            "/_media_path_for_camera_image.jpg";
}