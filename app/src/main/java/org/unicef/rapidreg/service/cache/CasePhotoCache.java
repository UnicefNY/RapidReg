package org.unicef.rapidreg.service.cache;

import android.graphics.Bitmap;
import android.os.Environment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CasePhotoCache {
    public static final int PHOTO_LIMIT = 4;
    public static final String MEDIA_PATH_FOR_CAMERA = Environment.getExternalStorageDirectory() +
            "/_media_path_for_camera_image.jpg";

    private static Map<Bitmap, String> photoBitPaths = new LinkedHashMap<>();

    public static boolean isEmpty() {
        return photoBitPaths.isEmpty();
    }

    public static boolean isUnderLimit() {
        return photoBitPaths.size() < PHOTO_LIMIT;
    }

    public static boolean isFull() {
        return photoBitPaths.size() == PHOTO_LIMIT;
    }

    public static void clear() {
        photoBitPaths.clear();
    }

    public static void addPhoto(Bitmap bitmap, String photoPath) {
        photoBitPaths.put(bitmap, photoPath);
    }

    public static void removePhoto(Bitmap bitmap) {
        photoBitPaths.remove(bitmap);
    }

    public static Map<Bitmap, String> getPhotoBitPaths() {
        return photoBitPaths;
    }

    public static List<Bitmap> getPhotosBits() {
        List<Bitmap> result = new ArrayList<>();
        for (Bitmap photoBit : photoBitPaths.keySet()) {
            result.add(photoBit);
        }
        return result;
    }

    public static List<String> getPhotosPaths() {
        List<String> result = new ArrayList<>();
        for (String photoPath : photoBitPaths.values()) {
            result.add(photoPath);
        }
        return result;
    }
}