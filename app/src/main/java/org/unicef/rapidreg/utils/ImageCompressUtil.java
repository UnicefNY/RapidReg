package org.unicef.rapidreg.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.raizlabs.android.dbflow.data.Blob;
import org.unicef.rapidreg.base.record.recordphoto.PhotoConfig;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.unicef.rapidreg.base.record.recordphoto.PhotoConfig.MAX_QUALITY_COMPRESS_SIZE;


public class ImageCompressUtil {

    public static byte[] convertImageToBytes(Bitmap imageSource) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageSource.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
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

    public static Bitmap compressImage(String filePath, float maxWidth, float maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        float imgRatio = imageWidth / (float) imageHeight;
        float maxRatio = maxWidth / maxHeight;

        if (imageHeight > maxHeight || imageWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / imageHeight;
                imageWidth = (int) (imgRatio * imageWidth);
                imageHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / imageWidth;
                imageHeight = (int) (imgRatio * imageHeight);
                imageWidth = (int) maxWidth;
            } else {
                imageHeight = (int) maxHeight;
                imageWidth = (int) maxWidth;
            }
        }
        options.inSampleSize = calculateInSampleSize(options, imageWidth, imageHeight);
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[16 * 1024];

        Bitmap resizedImage = BitmapFactory.decodeFile(filePath, options);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(resizedImage, imageWidth, imageHeight, true);

        Bitmap qualityBitmap = compressByQuality(scaledBitmap, MAX_QUALITY_COMPRESS_SIZE);
        return rotateBitmapByExif(filePath, qualityBitmap);
    }

    public static Bitmap compressByQuality(Bitmap bitmap, int maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        boolean isCompressed = false;

        while (baos.toByteArray().length > maxSize) {
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

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        float totalPixels = width * height;
        float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    public static Blob readImageFile(String filePath) throws IOException {
        if (new File(filePath).length() <= PhotoConfig.MAX_SIZE_KB) {
            return new Blob(StreamUtil.readFile(filePath));
        }
        return new Blob(ImageCompressUtil.convertImageToBytes(
                ImageCompressUtil.compressImage(filePath, PhotoConfig.MAX_COMPRESS_WIDTH, PhotoConfig.MAX_COMPRESS_HEIGHT)));
    }
}