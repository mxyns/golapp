package fr.burn38.gameoflifeapp;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.text.InputFilter;


import android.os.Bundle;



public class MainActivity extends AppCompatActivity {

    static final String IMAGE_SIZE_KEY = "fr.burn38.gameoflifeapp.IMAGE_SIZE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ((EditText)findViewById(R.id.main_editor_settings_height)).setFilters(new InputFilter[] {new MinMaxFilter(1,1080)});
        ((EditText)findViewById(R.id.main_editor_settings_width)).setFilters(new InputFilter[] {new MinMaxFilter(1,1920)});
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

    public void toggleDrawSettings(View v) {
        boolean state = findViewById(R.id.main_editor_settings_layout).getVisibility() == View.GONE;
        int newState = state ? View.VISIBLE : View.GONE;

        System.out.println("-> "+(state ? "VISIBLE" : "GONE"));
        findViewById(R.id.main_editor_settings_layout).setVisibility(newState);
        findViewById(R.id.main_editor_settings_height).setVisibility(newState);
        findViewById(R.id.main_editor_settings_width).setVisibility(newState);
    }
}
