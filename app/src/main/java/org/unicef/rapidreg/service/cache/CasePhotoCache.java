package org.unicef.rapidreg.service.cache;

import android.graphics.Bitmap;
import android.os.Environment;

import org.unicef.rapidreg.model.CasePhoto;
import org.unicef.rapidreg.utils.ImageCompressUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CasePhotoCache {
    public static final int MAX_WIDTH = 1836;
    public static final int MAX_HEIGHT = 3264;
    public static final int MAX_SIZE_KB = 1000;
    public static final int PHOTO_LIMIT = 4;

    public static final String MEDIA_PATH_FOR_CAMERA = Environment.getExternalStorageDirectory() +
            "/_media_path_for_camera_image.jpg";
    public static final Map<Integer, String> CASE_PHOTO_FILE_PATH_FROM_DB = new HashMap<>();

    private static Map<Bitmap, String> photoBitPaths = new LinkedHashMap<>();

    static {
        for (int i = 0; i < PHOTO_LIMIT; i++) {
            CASE_PHOTO_FILE_PATH_FROM_DB.put(i, Environment.getExternalStorageDirectory()
                    + File.separator + "case_photo_" + i + ".jpg");
        }
    }

    public static void clear() {
        photoBitPaths.clear();
    }
}