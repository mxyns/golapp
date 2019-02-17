package fr.burn38.gameoflifeapp.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import fr.burn38.gameoflifeapp.EditorActivity;
import fr.burn38.gameoflifeapp.MainActivity;
import fr.burn38.gameoflifeapp.ViewerActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtils {

    final static String API_URL = "https://limelion.herokuapp.com/api/gol?output=gif&gen=10&delay=500&rule=conway";
    //TODO: make settings activity to change parameters
    //TODO: store api_url to @strings

    public static void postImage(Bitmap bm, File bmFile) {
        postImage(bm, bmFile, null);
    }
    public static void postImage(Bitmap bm, File bmFile, File outputFile) {
            Thread thread = new Thread(new PostThread(bm, bmFile, outputFile));
            thread.start();
    }
    static Request createPostRequest(String url, Bitmap bm, File bmFile) {
        File tempImage = FileUtils.saveImage(bm, bmFile);

        if(tempImage == null) {
            Log.e("[createPostRqst][write]", "Coudln't write image to "+bmFile.getPath());
            return null;
        }

        RequestBody body = RequestBody.create(MediaType.parse("x-markdown; charset=utf-8"), tempImage);

        return new Request.Builder()
                    .url(url) //set server url
                    .post(body) //add post body to request
                    .build(); //generate request
    }
}

class PostThread implements Runnable {

    private Bitmap bm;
    private File bmFile;
    private File outputFile;
    private String url;
    private String logTag = "[PostThread][%step]";
    private String step = "start";

    PostThread(Bitmap bm, File bmFile) {
        this(NetworkUtils.API_URL, bm, bmFile, null);
    }
    PostThread(Bitmap bm, File bmFile, File outputFile) {
        this(NetworkUtils.API_URL, bm, bmFile, outputFile);
    }
    PostThread(String url, Bitmap bm, File bmFile, File outputFile) {
        this.bm = bm;
        this.url = url;
        this.bmFile = bmFile;
        this.outputFile = outputFile;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient();

        Request rq = NetworkUtils.createPostRequest(url, bm, bmFile);
        if(rq==null) {
            Log.e("[PostThread][genRqst]","Error creating post request, aborting.");
            return;
        }

        Response r = null;

        step = "execute";
        try {
            r = client.newCall(rq).execute();
            Log.i(logTag.replaceAll("%step",step), "Request sent");
            Log.i(logTag.replaceAll("%step",step), r.message());
        } catch (IOException ex) {
            Log.e(logTag.replaceAll("%step",step), ex.toString());
            ex.printStackTrace();
        }

        step = "request";
        if (r != null && !r.isSuccessful()) {
            Log.e(logTag.replaceAll("%step",step),"Request not successful: "+r.message());
        } else {
            step = "response";
            MediaType type = r.body().contentType();
            if(type!= null && (type.toString().toLowerCase().contains("string") || type.toString().toLowerCase().contains("text"))) {
                try {
                    Log.i(logTag.replaceAll("%step", step), type.toString() + " => " + r.body().string());
                } catch (IOException ex) {Log.i(logTag.replaceAll("%step", step), ex.toString()+ ": Couldn't retrieve text gotten");}
            } else {
                //Let's assume it's a gif and friction is negligible
                //TODO: consider other formats.

                InputStream input = r.body().byteStream();
                String originalName = bmFile.getName().substring(0, bmFile.getName().lastIndexOf("."));

                File output = new File(new File(MainActivity.getCacheDirectory(), "animated"), originalName+".gif");
                if (this.outputFile != null) output = this.outputFile;

                File gif = FileUtils.saveGif(input, output);
                if (gif.exists()) {
                    MainActivity.startViewer(gif);
                }
            }
        }
    }
}
