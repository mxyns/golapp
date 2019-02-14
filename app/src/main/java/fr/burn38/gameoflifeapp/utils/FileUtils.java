package fr.burn38.gameoflifeapp.utils;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

public class FileUtils {

    public static void saveImage(Bitmap bm, File root) {
        File imgs = new File(root.getAbsolutePath(), "imgs");
        if (!imgs.exists()) {
            imgs.mkdir();
        }

        File img = new File(imgs.getAbsolutePath(), (new Date().toString().replaceAll(" ","_"))+".bmp");
        if(!img.exists()) {
            FileOutputStream fos = null;
            try {
                //Create file
                fos = new FileOutputStream(img);

                img.createNewFile();

                //Set FoS
                fos = new FileOutputStream(img);

                //Convert Bitmap to byte array
                bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
                System.out.println("File "+img.getAbsolutePath()+" ("+(bm.getHeight()*bm.getWidth())+" bytes) saved.");
            } catch(IOException ex) {
                ex.printStackTrace();
            } finally {
                try{
                    fos.flush();
                    fos.close();
                } catch(Exception ex) {
                    ex.printStackTrace();
                }

            }

        }
    }

    static File[] ls(File root) {
        return root.listFiles();
    }
}
