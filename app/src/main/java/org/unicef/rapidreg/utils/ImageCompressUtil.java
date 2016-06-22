package org.unicef.rapidreg.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageCompressUtil {
  
    public static Bitmap compressByQuality(Bitmap bitmap, int maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;  
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        System.out.println("图片压缩前大小：" + baos.toByteArray().length + "byte");  
        boolean isCompressed = false;  
        while (baos.toByteArray().length / 1024 > maxSize) {  
            quality -= 10;  
            baos.reset();  
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            System.out.println("质量压缩到原来的" + quality + "%时大小为："  
                    + baos.toByteArray().length + "byte");  
            isCompressed = true;  
        }  
        System.out.println("图片压缩后大小：" + baos.toByteArray().length + "byte");  
        if (isCompressed) {  
            Bitmap compressedBitmap = BitmapFactory.decodeByteArray(
                    baos.toByteArray(), 0, baos.toByteArray().length);  
            recycleBitmap(bitmap);  
            return compressedBitmap;  
        } else {  
            return bitmap;  
        }  
    }  
  
    public static Bitmap compressBySize(String pathName, int targetWidth,
            int targetHeight) {  
        BitmapFactory.Options opts = new BitmapFactory.Options();  
        opts.inJustDecodeBounds = true;
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
        return BitmapFactory.decodeFile(pathName, opts);
    }  
  
    public static Bitmap compressBySize(Bitmap bitmap, int targetWidth,
            int targetHeight) {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        BitmapFactory.Options opts = new BitmapFactory.Options();  
        opts.inJustDecodeBounds = true;  
        bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0,  
                baos.toByteArray().length, opts);  
        // 得到图片的宽度、高度；  
        int imgWidth = opts.outWidth;  
        int imgHeight = opts.outHeight;  
        // 分别计算图片宽度、高度与目标宽度、高度的比例；取大于该比例的最小整数；  
        int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);  
        int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);  
        if (widthRatio > 1 || heightRatio > 1) {  
            if (widthRatio > heightRatio) {  
                opts.inSampleSize = widthRatio;  
            } else {  
                opts.inSampleSize = heightRatio;  
            }  
        }  
        // 设置好缩放比例后，加载图片进内存；  
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
        int len = 0;  
        while ((len = is.read(buff)) != -1) {  
            baos.write(buff, 0, len);  
        }  
  
        byte[] data = baos.toByteArray();  
        BitmapFactory.Options opts = new BitmapFactory.Options();  
        opts.inJustDecodeBounds = true;  
        Bitmap bitmap;
        // 得到图片的宽度、高度；  
        int imgWidth = opts.outWidth;  
        int imgHeight = opts.outHeight;  
        // 分别计算图片宽度、高度与目标宽度、高度的比例；取大于该比例的最小整数；  
        int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);  
        int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);  
        if (widthRatio > 1 || heightRatio > 1) {  
            if (widthRatio > heightRatio) {  
                opts.inSampleSize = widthRatio;  
            } else {  
                opts.inSampleSize = heightRatio;  
            }  
        }  
        // 设置好缩放比例后，加载图片进内存；  
        opts.inJustDecodeBounds = false;  
        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);  
        return bitmap;  
    }  
  
    public static Bitmap rotateBitmapByExif(String srcPath, Bitmap bitmap) {
        ExifInterface exif;
        Bitmap newBitmap = null;  
        try {  
            exif = new ExifInterface(srcPath);  
            if (exif != null) { // 读取图片中相机方向信息  
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,  
                        ExifInterface.ORIENTATION_NORMAL);  
                int digree = 0;  
                switch (ori) {  
                case ExifInterface.ORIENTATION_ROTATE_90:  
                    digree = 90;  
                    break;  
                case ExifInterface.ORIENTATION_ROTATE_180:  
                    digree = 180;  
                    break;  
                case ExifInterface.ORIENTATION_ROTATE_270:  
                    digree = 270;  
                    break;  
                }  
                if (digree != 0) {  
                    Matrix m = new Matrix();
                    m.postRotate(digree);  
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

    public static Bitmap getThumbnail(ContentResolver cr, String path){
        Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.MediaColumns._ID}, MediaStore.MediaColumns.DATA + "=?", new String[]{path}, null);
        if (ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();
            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        }

        ca.close();
        return null;

    }

    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {  
            bitmap.recycle();  
            System.gc();  
            bitmap = null;  
        }  
    }  
}