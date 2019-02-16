package fr.burn38.gameoflifeapp.utils;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import fr.burn38.gameoflifeapp.MainActivity;

public class FileUtils {

    private static File createFile(File file) {
        if(!file.exists() && !file.getParentFile().exists()) file.getParentFile().mkdirs();
        if(!file.exists()) {
            try{
                file.createNewFile();
            } catch (IOException ex) {
                Log.e("[FileUtils][createFile]", "Couldn't create file");
                ex.printStackTrace();
                return null;
            }
        }
        return file;
    }
    public static File saveImage(Bitmap bm, File outputFile) {
        outputFile = createFile(outputFile);

        if(outputFile != null) {
            FileOutputStream fos = null;
            try {
                //Set FoS
                fos = new FileOutputStream(outputFile);

                //Write bitmap to storage
                //TODO: set color-depth to 1 bit
                bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
                System.out.println("File "+outputFile.getAbsolutePath()+" ("+(bm.getHeight()*bm.getWidth())+" bytes) saved.");
            } catch(IOException ex) {
                ex.printStackTrace();
                return null;
            } finally {
                try{
                    fos.flush();
                    fos.close();
                } catch(Exception ex) {
                    ex.printStackTrace();
                }

            }

        }
        return outputFile;
    }
    public static File saveGif(InputStream input, File output) {
        Log.i("[saveGif]", "Saving gif "+output.getPath());

        BufferedInputStream buffStream = new BufferedInputStream(input);
        ByteArrayOutputStream arrayOutput = new ByteArrayOutputStream();

        int bit = 0;
        try {
            while ((bit = buffStream.read()) != -1) {
                arrayOutput.write(bit);
            }
        } catch (IOException ex) {
            Log.e("[saveGif]","Couldn't save gif "+output.getPath());
        }

        try {
            output = createFile(output);

            if (output != null) {
                //TODO: convert to monochromatic gif (1 bit per pixel)
                FileOutputStream outputStream = new FileOutputStream(output);
                outputStream.write(arrayOutput.toByteArray());
                outputStream.flush();

                outputStream.close();
                input.close();


                Log.i("[saveGif]", "Success");
                return output;
            } else {
                Log.e("[saveGif][createFile]", "Couldn't make file.");
            }
        }
        catch (FileNotFoundException notFound) {notFound.printStackTrace();}
        catch(IOException io) {io.printStackTrace();}


        Log.i("[saveGif]", "Failed");
        return null;
    }

    static File[] ls(File root) {
        return root.listFiles();
    }

    public static void clearCache() {
        clearFolder(new File(MainActivity.getCacheDirectory(), "imgs"));
        clearFolder(new File(MainActivity.getCacheDirectory(), "animated"));
    }
    public static void clearFolder(File folder) {
        File[] fLs = ls(folder);

        for (File f : fLs) {
            f.delete();
        }
    }
}
