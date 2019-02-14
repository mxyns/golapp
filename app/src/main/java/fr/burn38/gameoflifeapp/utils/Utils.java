package fr.burn38.gameoflifeapp.utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class Utils {

    public static byte[] bitmapToByteArray(Bitmap b) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] r = stream.toByteArray();
        b.recycle();

        return r;
    }
    public static String byteArrayToString(byte[] bA) {
        StringBuilder rB = new StringBuilder();
        for (Byte b : bA) {
            rB.append(Byte.toString(b));
        }

        return rB.toString();
    }
    public static byte[] byteArrayFromString(String s) throws UnsupportedEncodingException {
        return s.getBytes("UTF-8");
    }

}
