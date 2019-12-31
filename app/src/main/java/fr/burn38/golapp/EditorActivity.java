package fr.burn38.golapp;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.Date;

import fr.burn38.golapp.utils.FileUtils;
import fr.burn38.golapp.utils.NetworkUtils;
import fr.burn38.golapp.views.PaintView;

//TODO: possibility to choose image name
public class EditorActivity extends AppCompatActivity {

    public static int IMAGE_WIDTH = 0, IMAGE_HEIGHT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int[] IMAGE_SIZE = (getIntent().getExtras().getIntArray(MainActivity.IMAGE_SIZE_KEY));
        IMAGE_SIZE = IMAGE_SIZE == null ? new int[]{IMAGE_WIDTH, IMAGE_HEIGHT} : IMAGE_SIZE;

        IMAGE_WIDTH = IMAGE_SIZE[0];
        IMAGE_HEIGHT = IMAGE_SIZE[1];

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
    }

    @Override
    protected void onStart() {
        TextView title = findViewById(R.id.editor_title);
        title.setText(getResources().getString(R.string.editor_title, IMAGE_WIDTH, IMAGE_HEIGHT));

        super.onStart();
    }

    public void clearImage(View v) {
        PaintView im = findViewById(R.id.editor_image_view);
        im.clear();
    }

    public void switchColor(View v) {
        PaintView im = findViewById(R.id.editor_image_view);
        ImageButton ib = findViewById(R.id.editor_switch_color_button);
        if (im.getColor() == Color.BLACK) {
            im.changeColor(Color.WHITE);
            ib.setImageResource(R.drawable.ic_switch_color_white);
        } else {
            im.changeColor(Color.BLACK);
            ib.setImageResource(R.drawable.ic_switch_color_black);
        }
    }

    public void saveImage(View v) {
        PaintView im = findViewById(R.id.editor_image_view);
        String filename = new Date().toString().replaceAll(" ","_")+".bmp";
        FileUtils.saveImage(im.getImage(), new File(new File(getFilesDir(), "imgs"), filename)); // to internal storage
    }

    public void sendImage(View v) {
        PaintView im = findViewById(R.id.editor_image_view);
        Bitmap bm = im.getImage();

        File bitmapFile = new File(new File(MainActivity.getCacheDirectory(), "imgs"), new Date().toString().replaceAll(" ","_")+".bmp");

        NetworkUtils.postImage(bm, bitmapFile);
    }
}
