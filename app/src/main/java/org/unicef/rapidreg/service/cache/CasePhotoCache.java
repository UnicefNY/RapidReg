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

    public static final String MEDIA_PATH_FOR_CAMERA = Environment.getExternalStorageDirectory() +
            "/_media_path_for_camera_image.jpg";
}