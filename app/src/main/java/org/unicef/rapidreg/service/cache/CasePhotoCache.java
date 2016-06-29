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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CasePhotoCache {
    public static final int PHOTO_LIMIT = 4;
    public static final String MEDIA_PATH_FOR_CAMERA = Environment.getExternalStorageDirectory() +
            "/_media_path_for_camera_image.jpg";
    public static final Map<Integer, String> CASE_PHOTO_FILE_PATH_FROM_DB = new HashMap<>();

    public static String applicationPackageName;

    private static Map<Bitmap, String> photoBitPaths = new LinkedHashMap<>();

    static {
        for (int i = 0; i < PHOTO_LIMIT; i++) {
            CASE_PHOTO_FILE_PATH_FROM_DB.put(i, Environment.getExternalStorageDirectory()
                    + File.separator + "case_photo_" + i + ".jpg");
        }
    }

    public static void clearLocalCachedPhotoFiles() {
        for (String filePath : CASE_PHOTO_FILE_PATH_FROM_DB.values()) {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static void cachePhotosFromDbToLocalFiles(List<CasePhoto> casePhotos) {
        clearLocalCachedPhotoFiles();
        int index = 0;
        for (CasePhoto casePhoto : casePhotos) {
            Bitmap thumbnail = ImageCompressUtil.convertByteArrayToImage(casePhoto.getThumbnail().getBlob());
            addPhoto(thumbnail, CASE_PHOTO_FILE_PATH_FROM_DB.get(index++));
        }
        asyncCachingPhotos(casePhotos);
    }

    private static void asyncCachingPhotos(final List<CasePhoto> casePhotos) {
        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(new Runnable() {
            @Override
            public void run() {
                int index = 0;
                for (CasePhoto casePhoto : casePhotos) {
                    try {
                        Bitmap bitmap = ImageCompressUtil.convertByteArrayToImage(casePhoto.getPhoto().getBlob());
                        ImageCompressUtil.storeImage(bitmap, CASE_PHOTO_FILE_PATH_FROM_DB.get(index++));
                        bitmap.recycle();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        service.shutdown();
    }

    public static boolean isEmpty() {
        return photoBitPaths.isEmpty();
    }

    public static boolean isUnderLimit() {
        return photoBitPaths.size() < PHOTO_LIMIT;
    }

    public static boolean isOneLessThanLimit() {
        return PHOTO_LIMIT - 1 == photoBitPaths.size();
    }

    public static boolean isFull() {
        return photoBitPaths.size() >= PHOTO_LIMIT;
    }

    public static void clear() {
        photoBitPaths.clear();
    }

    public static int size() {
        return photoBitPaths.size();
    }

    public static void initApplicationPackageName(String applicationPackageName) {
        if (null == applicationPackageName) {
            CasePhotoCache.applicationPackageName = applicationPackageName;
        }
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