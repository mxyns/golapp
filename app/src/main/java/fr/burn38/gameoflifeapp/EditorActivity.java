package fr.burn38.gameoflifeapp;

import android.graphics.Bitmap;
import android.graphics.Color;

import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import fr.burn38.gameoflifeapp.utils.FileUtils;
import fr.burn38.gameoflifeapp.utils.NetworkUtils;

public class EditorActivity extends AppCompatActivity {

    static int IMAGE_WIDTH = 0, IMAGE_HEIGHT = 0;

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
        FileUtils.saveImage(im.getImage(), getFilesDir(), "imgs");
    }

    public void sendImage(View v) {
        PaintView im = findViewById(R.id.editor_image_view);
        Bitmap bm = im.getImage();

        NetworkUtils.postImage(bm);
    }
}
