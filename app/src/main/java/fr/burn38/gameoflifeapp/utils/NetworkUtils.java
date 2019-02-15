package fr.burn38.gameoflifeapp.utils;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import fr.burn38.gameoflifeapp.MainActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtils {

    final static String API_URL = "https://limelion.herokuapp.com/api/gol?output=gif&gen=10&delay=500&rule=conway";

    public static void postImage(Bitmap bm) {
        Thread thread = new Thread(new PostThread(bm));
        thread.start();
    }
    static Request createPostRequest(String url, Bitmap bm) {
        File tempImage = FileUtils.saveImage(bm, MainActivity.getCacheDirectory(), "upld");

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("data-binary", tempImage.getName(),
                        RequestBody.create(MediaType.parse("image/bmp"), tempImage))
                .build();


        return new Request.Builder()
                    .url(url) //set server url
                    .post(body)
                    .build(); //generate request
    }
}

class PostThread implements Runnable {

    private Bitmap bm;
    private String url;

    PostThread(Bitmap bm) {
        this(NetworkUtils.API_URL, bm);
    }
    PostThread(String url, Bitmap bm) {
        this.bm = bm;
        this.url = url;
    }

    @Override
    public void run() {
        OkHttpClient client = new OkHttpClient();

        Request rq = NetworkUtils.createPostRequest(url, bm);

        Response r = null;
        try {
            r = client.newCall(rq).execute();
        } catch (IOException ex) {
            Log.e("[PostThread][execute]", ex.toString());
            ex.printStackTrace();
        }

        if (!r.isSuccessful()) {
            Log.e("[PostThread][request]","Request not successful: "+r.message());
        } else {

            try{
                Log.i("IMG INPUT", "got: "+r.body().contentType().toString()+" from server");
                Log.i("IMG INPUT", "  => "+r.body().string()+" from server");
            } catch(IOException ex) {
                ex.printStackTrace();
            } catch(NullPointerException ex) {ex.printStackTrace();}


            String type = r.body().contentType().toString();

        }
    }
}
