package fr.burn38.golapp;

import android.content.Context;
import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.text.InputFilter;


import android.os.Bundle;

import java.io.File;
import java.util.Objects;

import fr.burn38.golapp.utils.FileUtils;


public class MainActivity extends AppCompatActivity {

    //TODO: store key to @strings
    public static final String IMAGE_SIZE_KEY = "fr.burn38.gameoflifeapp.IMAGE_SIZE";
    public static final String VIEWER_LOCATION_KEY = "fr.burn38.gameoflifeapp.VIEWER_LOCATION";
    public static final String VIEWER_FILE_NAME_KEY = "fr.burn38.gameoflifeapp.VIEWER_FILE_NAME";

    static MainActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        context = this;
    }

    @Override
    protected void onStart() {
        super.onStart();

        ((EditText)findViewById(R.id.main_editor_settings_height)).setFilters(new InputFilter[] {new MinMaxFilter(1,1080)});
        ((EditText)findViewById(R.id.main_editor_settings_width)).setFilters(new InputFilter[] {new MinMaxFilter(1,1920)});
    }

    @Override
    protected void onDestroy() {
        FileUtils.clearCache();

        super.onDestroy();
    }

    public void startEditor(View v) {
        String sWidth = ((EditText)findViewById(R.id.main_editor_settings_width)).getText().toString();
        String sHeight = ((EditText)findViewById(R.id.main_editor_settings_height)).getText().toString();
        int width=0,height=0;
        if(sWidth.equals("") || sHeight.equals("")) return;
        else {
            try {
                width = (int)Double.parseDouble(sWidth);
                height = (int)Double.parseDouble(sHeight);
            } catch(Exception e){ System.out.println("Can't parse image width/height");}
        }
        Intent editorIntent = new Intent(MainActivity.this, EditorActivity.class);

        Bundle b = new Bundle();
        b.putIntArray(IMAGE_SIZE_KEY, new int[] {width, height});

        editorIntent.putExtras(b);

        startActivity(editorIntent);
    }
    public static void startViewer(File f){
        System.out.println("Someone tried to start viewer for file " + f);
        startViewer(context, f);
    }
    public static void startViewer(Context context, File f) {
        Intent intent = new Intent(context, ViewerActivity.class);

        Bundle b = new Bundle();
        b.putString(MainActivity.VIEWER_LOCATION_KEY, FileUtils.LOCATION.fromFile(f) != null ? Objects.requireNonNull(FileUtils.LOCATION.fromFile(f)).name() : null);
        b.putString(MainActivity.VIEWER_FILE_NAME_KEY, FileUtils.getFilename(f));

        intent.putExtras(b);

        context.startActivity(intent);
    }
    public void openGallery(View v) {
        Intent intent = new Intent(MainActivity.this, GalleryActivity.class);

        startActivity(intent);
    }

    public void displayCanvasSizeFields(View v) {
        boolean state = findViewById(R.id.main_editor_settings_layout).getVisibility() == View.GONE;
        int newState = state ? View.VISIBLE : View.GONE;

        findViewById(R.id.main_editor_settings_layout).setVisibility(newState);
        findViewById(R.id.main_editor_settings_height).setVisibility(newState);
        findViewById(R.id.main_editor_settings_width).setVisibility(newState);
    }

    public static File getCacheDirectory(){return context.getCacheDir();}
    public static File getInternalStorageDirectory() {return context.getFilesDir();}
}
