package org.unicef.rapidreg.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class StreamUtil {
    public static byte[] readFile(String filePath) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int length;
        while ((length = bis.read(buffer)) != -1) {
            bos.write(buffer, 0, length);
        }
        return bos.toByteArray();
    }

    public static void writeFile(byte[] byteFile, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        bos.write(byteFile);
    }
}

