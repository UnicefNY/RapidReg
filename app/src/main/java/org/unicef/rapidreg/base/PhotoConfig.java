package org.unicef.rapidreg.base;

import android.os.Environment;

public class PhotoConfig {
    public static final float MAX_WIDTH = 1080;
    public static final float MAX_HEIGHT = 1920;
    public static final int MAX_SIZE_KB = 1000;
    public static final int THUMBNAIL_SIZE = 210;

    public static final String CONTENT_TYPE_IMAGE = "image/jpeg";
    public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";
    public static final String CONTENT_TYPE_AUDIO = "audio/amr";

    public static final String MEDIA_PATH_FOR_CAMERA = Environment.getExternalStorageDirectory() +
            "/_media_path_for_camera_image.jpg";
}
