package fr.burn38.gameoflifeapp.utils;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkUtils {

    private final static String API_URL = "https://golapi.herokuapp.com/run?output=gif&gen=10&delay=500&rule=conway";

    public static boolean sendImage(Bitmap bm) {
        // curl -X POST --url "golapi.herokuapp.com/run?output=gif&gen=10&delay=500&rule=conway" --data-binary "@input.bmp" --output "output.gif"

        //create client & request
        Request rq = createPostRequest(bm);
        if (rq == null) Log.e("IMG UPLOAD", "Error while creating request");

        //Load server response
        Object[] response = executeRequest(rq);

        String type = (String)response[1];
        if(type.equals("IOException")) {
            StackTraceElement[] trace = (StackTraceElement[])response[2];
            for(StackTraceElement el : trace) {
                Log.e("[sendImage]["+type+"]", el.getMethodName()+" threw exception at line "+el.getLineNumber()+" in class "+el.getClassName()+"("+el.getFileName()+".class)");
            }
        } else if (type.equals("Exception")) {
            Log.e("[sendImage]["+type+"]", response[2].toString());

        } else{

            switch(type) {
                case "byte[]": {

                }
                case "String": {

                }
                default: {
                    Log.i("[sendImage]", "got "+type+" object => "+response[2]);
                }
            }

        }


        return (Boolean)response[0];
    }

    private static Request createPostRequest(Bitmap bm) {
        return new Request.Builder()
                    .url(API_URL) //set server url
                    .addHeader("data-binary", Utils.byteArrayToString(Utils.bitmapToByteArray(bm))) //add bitmap binary data
                    .build(); //generate request
    }

    // Object[] format: {boolean success, String contentType, Object content}
    private static Object[] executeRequest(Request rq) {
        final Object[] result = new Object[] {false, "Exception", "Undetermined error"};

        OkHttpClient client = new OkHttpClient();

        client.newCall(rq).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException ex) {
                Log.e("HTTP-ANSWER", "Error after get request");
                result[1] = "IOException";
                result[2] = ex.getStackTrace();

                ex.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    result[1] = "IOException";
                    result[2] = new StackTraceElement[] {new StackTraceElement("NetworkUtils", "onResponse","NetworkUtils",70)};

                    throw new IOException("Unexpected code " + response);
                } else {
                    Log.i("IMG INPUT", "got: "+response.body().contentType().toString()+" from server");
                    Log.i("IMG INPUT", "  => "+response.body().string()+" from server");

                    result[1] = response.body().contentType().toString();
                    if (result[1].toString().contains("String")) result[2] = response.body().string();
                    else if (result[1].toString().contains("byte")) result[2] = response.body().bytes();
                    else result[2] = null;
                }
            }
        });

        return result;
    }
}
