package com.miaxis.sm93m;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {
    public static boolean writeFeatureToFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file;
        boolean save=false;
        try {
            File dir = new File(filePath);
            if(!dir.exists() || !dir.isDirectory()){
                dir.mkdirs();
            }
            file = new File(filePath, fileName);
            if (file.exists()) file.delete();
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
            save=true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return save;
    }

    public static byte[] loadFileFeature(String pathname) {
        File file = new File(pathname);
        try {
            FileInputStream out = new FileInputStream(file);
            int available = out.available();
            byte[] buffer = new byte[available];
            int ret = out.read(buffer);
            out.close();
            if (ret != available) {
                return null;
            }
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String saveFile(String filePath, byte[] buffer, int len) {
        try {
            File file = new File(filePath);
            if (file.exists()) file.delete();
            FileOutputStream out = new FileOutputStream(file);
            out.write(buffer, 0, len);
            out.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
