package org.unicef.rapidreg.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ImageCompressUtil {

    public static byte[] convertImageToBytes(Bitmap imageSource) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageSource.compress(Bitmap.CompressFormat.JPEG, 10, stream);
        return stream.toByteArray();
    }

    public static Bitmap convertByteArrayToImage(byte[] byteSource) {
        return BitmapFactory.decodeByteArray(byteSource, 0, byteSource.length);
    }

    public static Bitmap getThumbnail(String imagePath, int width, int height) {
        return getThumbnail(BitmapFactory.decodeFile(imagePath), width, height);
    }

    public static Bitmap getThumbnail(String imagePath, int size) {
        return getThumbnail(BitmapFactory.decodeFile(imagePath), size, size);
    }

    public static Bitmap getThumbnail(Bitmap bitmap, int width, int height) {
        return ThumbnailUtils.extractThumbnail(bitmap, width, height);
    }


    public static  Bitmap compressImage(String filePath, int maxWidth, int maxHeight, int maxSize) throws IOException {
        Bitmap bitmap;
        int rotateDegree = readPictureRotateDegree(filePath);

        if (rotateDegree == 90 || rotateDegree == 270) {
            bitmap = compressBySize(filePath,
                    maxWidth, maxHeight);
        } else {
            bitmap = compressBySize(filePath,
                    maxHeight, maxWidth);
        }
        bitmap = compressByQuality(bitmap, maxSize);
        bitmap = rotateBitmapByExif(filePath, bitmap);
        return bitmap;
    }

    public static Bitmap getThumbnail(ContentResolver contentResolver, String path) {
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.MediaColumns._ID}, MediaStore.MediaColumns.DATA + "=?", new String[]{path}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return MediaStore.Images.Thumbnails.getThumbnail(contentResolver, id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        }
        cursor.close();
        return null;
    }

    public static Bitmap compressBySize(String pathName, int targetWidth,
                                        int targetHeight) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(pathName, opts);
        int imgWidth = opts.outWidth;
        int imgHeight = opts.outHeight;

        int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
        int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);

        if (widthRatio > 1 || heightRatio > 1) {
            opts.inSampleSize = widthRatio > heightRatio ? widthRatio : heightRatio;
        }

        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, opts);
    }

    public static Bitmap compressByQuality(Bitmap bitmap, int maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        boolean isCompressed = false;

        while (baos.toByteArray().length / 1024 > maxSize) {
            quality -= 5;
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            isCompressed = true;
        }
        if (isCompressed) {
            Bitmap compressedBitmap = BitmapFactory.decodeByteArray(
                    baos.toByteArray(), 0, baos.toByteArray().length);
            recycleBitmap(bitmap);
            return compressedBitmap;
        } else {
            return bitmap;
        }
    }

    public static int readPictureRotateDegree(String path) throws IOException {
        int degree = 0;
        ExifInterface exifInterface = new ExifInterface(path);
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
        }
        return degree;
    }

    public static Bitmap rotateBitmapByExif(String srcPath, Bitmap bitmap) {
        ExifInterface exif;
        Bitmap newBitmap;
        try {
            exif = new ExifInterface(srcPath);
            if (exif != null) {
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                int degree = 0;
                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
                if (degree != 0) {
                    Matrix m = new Matrix();
                    m.postRotate(degree);
                    newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                            bitmap.getWidth(), bitmap.getHeight(), m, true);
                    recycleBitmap(bitmap);
                    return newBitmap;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap compressBySize(Bitmap bitmap, int targetWidth,
                                        int targetHeight) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0,
                baos.toByteArray().length, opts);
        int imgWidth = opts.outWidth;
        int imgHeight = opts.outHeight;

        int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
        int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
        if (widthRatio > 1 || heightRatio > 1) {
            if (widthRatio > heightRatio) {
                opts.inSampleSize = widthRatio;
            } else {
                opts.inSampleSize = heightRatio;
            }
        }
        opts.inJustDecodeBounds = false;
        Bitmap compressedBitmap = BitmapFactory.decodeByteArray(
                baos.toByteArray(), 0, baos.toByteArray().length, opts);
        recycleBitmap(bitmap);
        return compressedBitmap;
    }

    public static Bitmap compressBySize(InputStream is, int targetWidth,
                                        int targetHeight) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int len;
        while ((len = is.read(buff)) != -1) {
            baos.write(buff, 0, len);
        }

        byte[] data = baos.toByteArray();
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bitmap;
        int imgWidth = opts.outWidth;
        int imgHeight = opts.outHeight;
        int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
        int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
        if (widthRatio > 1 || heightRatio > 1) {
            if (widthRatio > heightRatio) {
                opts.inSampleSize = widthRatio;
            } else {
                opts.inSampleSize = heightRatio;
            }
        }
        opts.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        return bitmap;
    }

    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            System.gc();
        }
    }

    public static void storeImage(Bitmap image, String saveFilePath) throws IOException {
        FileOutputStream fos = new FileOutputStream(saveFilePath);
        image.compress(Bitmap.CompressFormat.PNG, 70, fos);
        fos.close();
    }


    public static Bitmap rotateImage(Bitmap img, int rotateDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateDegree);
        return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
    }
}