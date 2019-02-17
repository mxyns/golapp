package fr.burn38.gameoflifeapp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import fr.burn38.gameoflifeapp.MainActivity;
import kotlin.text.Regex;

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
    public static boolean deleteFile(File file) {
        if(file.exists())
            return file.delete();
        else return false;
    }

    //Object[] format: {boolean result, File newFile}
    public static Object[] moveFileToInternalStorage(File oldFile){
        if(oldFile == null) {
            System.out.println("[moveFileToInternalStorage] oldFile=null");
            return new Object[] {false, null};
        }

        File internal = MainActivity.getInternalStorageDirectory();
        File cache = MainActivity.getCacheDirectory();

        File newFile = new File(internal.getPath(), getParentsAfterAncestor(cache, oldFile)+oldFile.getName());
        createFile(newFile);


        boolean written = false;
        try {
            FileInputStream inputStream = new FileInputStream(oldFile);
            FileOutputStream outputStream = new FileOutputStream(newFile);

            ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
            int i;
            while((i=inputStream.read()) != -1) {
                byteArrayStream.write(i);
            }
            outputStream.write(byteArrayStream.toByteArray());
            written = true;
        } catch (IOException ex) {
            System.out.println("[moveFileToInternalStorage] oldFile="+oldFile.getPath()+"("+oldFile.exists()+")");
            System.out.println("[moveFileToInternalStorage] newFile="+newFile.getPath()+"("+newFile.exists()+")");
            ex.printStackTrace();
        }

        if(written) {
            return new Object[] {oldFile.delete(), newFile};
        }
        return new Object[] {false, oldFile};
    }

    public static Bitmap loadBitmap(File f) {
        if (f == null || !f.exists()) return null;
        return BitmapFactory.decodeFile(f.getPath());
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
                fos.flush();
                fos.close();

                System.out.println("File "+outputFile.getAbsolutePath()+" ("+(bm.getHeight()*bm.getWidth())+" bytes) saved.");

            } catch(IOException ex) {
                ex.printStackTrace();
                return null;
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

    static File[] ls(File folder) {
        return folder.listFiles();
    }
    public static String getFileExtension(File file) {
        return file.getName().substring(file.getName().lastIndexOf("."));
    }
    public static String getFilename(File file) {
        return file.getName().substring(0, file.getName().lastIndexOf("."));
    }
    public static File getCorrespondingAnimatedImageFile(File file) {
        String filename = getFilename(file);
        File toList = new File(file.getParentFile().getParentFile(),"animated");
        File[] siblings = ls(toList);

        for (File f : siblings) {
            if(getFilename(f).equalsIgnoreCase(filename)) return f;
        }
        return null;
    }
    public static String getParentsAfterAncestor(File ancestor, File lastChild) {
        StringBuilder strBuilder = new StringBuilder();

        File parent=lastChild.getParentFile();
        while(parent != null && !parent.getPath().equalsIgnoreCase(ancestor.getPath())){
            strBuilder.insert(0, File.separatorChar).insert(0, parent.getName());
            parent = parent.getParentFile();
        }

        return strBuilder.toString();
    }

    public static void clearCache() {
        clearFolder(new File(MainActivity.getCacheDirectory(), "imgs"));
        clearFolder(new File(MainActivity.getCacheDirectory(), "animated"));
    }
    public static void clearFolder(File folder) {
        if(!folder.exists() || !folder.isDirectory()) return;

        File[] fLs = ls(folder);
        for (File f : fLs) {
            f.delete();
        }
    }

}

/*
/data/user/0/fr.burn38.gameoflifeapp/cache/animated/Sat_Feb_16_22:54:21_GMT_2019.gif
/data/user/0/fr.burn38.gameoflifeapp/cache/animated/Sat_Feb_16_22:54:42_GMT_2019.gif
/data/user/0/fr.burn38.gameoflifeapp/cache/animated/Sat_Feb_16_22:58:02_GMT_2019.gif
/data/user/0/fr.burn38.gameoflifeapp/cache/animated/Sat_Feb_16_22:58:38_GMT_2019.gif
/data/user/0/fr.burn38.gameoflifeapp/cache/animated/Sat_Feb_16_22:59:30_GMT_2019.gif
/data/user/0/fr.burn38.gameoflifeapp/cache/animated/Sat_Feb_16_23:01:05_GMT_2019.gif
/data/user/0/fr.burn38.gameoflifeapp/cache/animated/Sat_Feb_16_23:02:57_GMT_2019.gif
/data/user/0/fr.burn38.gameoflifeapp/cache/animated/Sat_Feb_16_23:04:48_GMT_2019.gif
/data/user/0/fr.burn38.gameoflifeapp/cache/animated/Sat_Feb_16_23:05:59_GMT_2019.gif
/data/user/0/fr.burn38.gameoflifeapp/cache/animated/Sat_Feb_16_23:07:58_GMT_2019.gif
/data/user/0/fr.burn38.gameoflifeapp/cache/animated/Sat_Feb_16_23:11:06_GMT_2019.gif
/data/user/0/fr.burn38.gameoflifeapp/cache/animated/Sat_Feb_16_23:13:15_GMT_2019.gif
/data/user/0/fr.burn38.gameoflifeapp/cache/animated/Sat_Feb_16_23:14:23_GMT_2019.gif
/data/user/0/fr.burn38.gameoflifeapp/cache/animated/Sat_Feb_16_23:15:33_GMT_2019.gif
/data/user/0/fr.burn38.gameoflifeapp/cache/animated/Sat_Feb_16_23:15:50_GMT_2019.gif
/data/user/0/fr.burn38.gameoflifeapp/cache/animated/Sat_Feb_16_23:17:02_GMT_2019.gif
/data/user/0/fr.burn38.gameoflifeapp/cache/animated/Sat_Feb_16_23:18:14_GMT_2019.gif
 */
