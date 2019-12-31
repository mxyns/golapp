package fr.burn38.golapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import fr.burn38.golapp.utils.FileUtils;
import fr.burn38.golapp.utils.NetworkUtils;
import fr.burn38.golapp.views.ViewerImageView;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ViewerActivity extends AppCompatActivity {


    private FileUtils.LOCATION location = FileUtils.LOCATION.INTERNAL;
    private String filename;
    private File bitmapFile;
    private ViewerImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        createFromBundle(b);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    public void createFromBundle(Bundle b) {
        String intentType = b.getString(MainActivity.VIEWER_LOCATION_KEY);
        if (intentType != null) this.location = FileUtils.LOCATION.valueOf(intentType);

        this.filename = b.getString(MainActivity.VIEWER_FILE_NAME_KEY); //must not contain extension

        File dir = location.getDirectory();
        if(!dir.exists()) finish();

        this.bitmapFile = new File(new File(dir, "imgs"), filename+".bmp");
        if (!this.bitmapFile.exists()) finish();

    }
    public void init() {

        imageView = findViewById(R.id.viewer_displayview);
            imageView.setThumbnail(bitmapFile);

        File animatedFile = imageView.getAnimatedImageFile();
            if (animatedFile != null)
                imageView.reset();

        if(location == FileUtils.LOCATION.INTERNAL) {
            findViewById(R.id.viewer_save_image_button).setVisibility(GONE); //no need to move to internal storage if already in it
            findViewById(R.id.viewer_delete_image_button).setVisibility(VISIBLE); //no need to move to internal storage if already in it
            findViewById(R.id.viewer_send_image_button).setVisibility(VISIBLE);
        } else {
            findViewById(R.id.viewer_send_image_button).setVisibility(GONE); //can only send if not in cache (in cache means already sent to server)
            findViewById(R.id.viewer_delete_image_button).setVisibility(GONE); //no need to move to internal storage if already in it
            findViewById(R.id.viewer_save_image_button).setVisibility(VISIBLE);
        }

        findViewById(R.id.viewer_send_image_button).setVisibility(imageView.getAnimatedImageFile() == null ? VISIBLE : GONE);


        findViewById(R.id.viewer_play_animated_image_button).setVisibility(animatedFile != null && animatedFile.exists() && imageView.getAnimatedImage() != null ? VISIBLE : GONE); //can't play animated image if isn't generated

        ((TextView)findViewById(R.id.viewer_title)).setText(getString(R.string.viewer_title, this.filename, this.location));


        findViewById(R.id.viewer_play_animated_image_button).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                rewindGif();
                pauseGif();
                return true;
            }
        });
    }

    public void moveFilesToInternalStorage(View v) {
        final Object[] a = FileUtils.moveFileToInternalStorage(this.bitmapFile);
        final Object[] b = FileUtils.moveFileToInternalStorage(imageView.getAnimatedImageFile());

        if ((Boolean)a[0] && (Boolean)b[0]) { // if both files deleted correctly, reload viewer as INTERNAL stored files
            Objects.requireNonNull(getIntent().getExtras()).clear();
            Bundle bundle = new Bundle();
            bundle.putString(MainActivity.VIEWER_LOCATION_KEY, "INTERNAL");
            bundle.putString(MainActivity.VIEWER_FILE_NAME_KEY, FileUtils.getFilename((File) a[1]));

            getIntent().putExtras(bundle);

            createFromBundle(bundle);
            init();
        } //TODO: else pop an error dialog
    }
    public void sendImage(View v) {
        NetworkUtils.postImage(FileUtils.loadBitmap(this.bitmapFile), this.bitmapFile, new File(this.bitmapFile.getParent(), this.filename+".gif"));
    }
    public void moveFilesToCache(View v) {
        //TODO: use alert to confirm
        final Object[] a = FileUtils.moveFileToCache(this.bitmapFile);
        final Object[] b = FileUtils.moveFileToCache(imageView.getAnimatedImageFile());

        if ((Boolean)a[0] && (Boolean)b[0]) { // if both files deleted correctly, reload viewer as CACHE stored files
            Objects.requireNonNull(getIntent().getExtras()).clear();
            Bundle bundle = new Bundle();
            bundle.putString(MainActivity.VIEWER_LOCATION_KEY, "CACHE");
            bundle.putString(MainActivity.VIEWER_FILE_NAME_KEY, FileUtils.getFilename((File) a[1]));

            getIntent().putExtras(bundle);

            createFromBundle(bundle);
            init();
        }
    }

    public void onPlayButton(View v) {
        ImageButton imb = (ImageButton)v;
        if(imb.getContentDescription().equals(getResources().getString(R.string.viewer_play_animated_image_contentDescription))) {
            playGif();
        } else {
            pauseGif();
        }
    }

    public void pauseGif() {
        ImageButton imb = findViewById(R.id.viewer_play_animated_image_button);

        imageView.pause();

        imb.setContentDescription(getString(R.string.viewer_play_animated_image_contentDescription));
        Drawable ic = getResources().getDrawable(android.R.drawable.ic_media_play);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ic.setTint(getResources().getColor(R.color.colorPrimary));
        }
        imb.setImageDrawable(ic);
    }
    public void playGif() {
        ImageButton imb = findViewById(R.id.viewer_play_animated_image_button);

        imageView.play();

        imb.setContentDescription(getString(R.string.viewer_pause_animated_image_contentDescription));
        Drawable ic = getResources().getDrawable(android.R.drawable.ic_media_pause);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ic.setTint(getResources().getColor(R.color.colorPrimary));
        }
        imb.setImageDrawable(ic);
    }
    public void rewindGif() {
        pauseGif();
        imageView.reset();
    }

}

/**import fr.burn38.golapp.utils.FileUtils;
import fr.burn38.golapp.utils.NetworkUtils;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import fr.burn38.golapp.utils.FileUtils.LOCATION;

import java.io.File;
import java.io.IOException;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ViewerActivity extends AppCompatActivity {

    private LOCATION location = LOCATION.INTERNAL;
    private String filename;
    private File bitmapFile, animatedFile;
    private GifDrawable animatedImage;
    private Bitmap staticBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        createFromBundle(b);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_viewer);
    }

    @Override
    protected void onStart() {
        super.onStart();
        start();
    }

    public void createFromBundle(Bundle b) {
        String intentType = b.getString(MainActivity.VIEWER_LOCATION_KEY);
        if (intentType != null) this.location = LOCATION.valueOf(intentType);

        this.filename = b.getString(MainActivity.VIEWER_FILE_NAME_KEY); //must not contain extension

        File dir;
        if(location == LOCATION.CACHE) dir = MainActivity.getCacheDirectory();
        else dir = MainActivity.getInternalStorageDirectory();
        if(!dir.exists()) finish();

        this.bitmapFile = new File(new File(dir, "imgs"), filename+".bmp");
        if (!this.bitmapFile.exists()) finish();
        this.staticBitmap = FileUtils.loadBitmap(bitmapFile);

        this.animatedFile = FileUtils.getCorrespondingAnimatedImageFile(bitmapFile); //can be null or exist
        if (this.animatedFile != null && this.animatedFile.exists()) {
            try {
                this.animatedImage = new GifDrawable(this.animatedFile);
            } catch(IOException ex) {
                this.animatedImage = null;
                ex.printStackTrace();
            }
        }
    }
    public void start() {
        if(location == LOCATION.INTERNAL) {
            findViewById(R.id.viewer_save_image_button).setVisibility(GONE); //no need to move to internal storage if already in it
            findViewById(R.id.viewer_delete_image_button).setVisibility(VISIBLE); //no need to move to internal storage if already in it
        } else {
            findViewById(R.id.viewer_send_image_button).setVisibility(GONE); //can only send if not in cache (in cache means already sent to server)
            findViewById(R.id.viewer_delete_image_button).setVisibility(GONE); //no need to move to internal storage if already in it
        }

        if(this.animatedFile != null && !this.animatedFile.exists() && this.animatedImage != null) {
            findViewById(R.id.viewer_play_animated_image_button); //can't play animated image if isn't generated
        }

        ((TextView)findViewById(R.id.viewer_title)).setText(getString(R.string.viewer_title, this.filename, this.location));

        GifImageView displayView = findViewById(R.id.viewer_displayview);
        if (this.animatedImage != null) {
            displayView.setImageDrawable(this.animatedImage);
            this.animatedImage.stop();
        } else {
            displayView.setImageBitmap(this.staticBitmap);
            findViewById(R.id.viewer_send_image_button).setVisibility(VISIBLE);
        }

        findViewById(R.id.viewer_play_animated_image_button).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                rewindGif();
                pauseGif();
                return true;
            }
        });

    }

    public void moveFilesToInternalStorage(View v) {
        final Object[] a = FileUtils.moveFileToInternalStorage(this.bitmapFile);
        final Object[] b = FileUtils.moveFileToInternalStorage(this.animatedFile);

        if ((Boolean)a[0] && (Boolean)b[0]) {
            getIntent().getExtras().clear();
            Bundle bundle = new Bundle();
            bundle.putString(MainActivity.VIEWER_LOCATION_KEY, "INTERNAL");
            bundle.putString(MainActivity.VIEWER_FILE_NAME_KEY, FileUtils.getFilename((File) a[1]));

            getIntent().putExtras(bundle);

            createFromBundle(bundle);
            start();
        }
    }
    public void sendImage(View v) {
        NetworkUtils.postImage(FileUtils.loadBitmap(this.bitmapFile), this.bitmapFile, new File(this.bitmapFile.getParent(), this.filename+".gif"));
    }
    public void deleteImages(View v) {
        //TODO: use alert to confirm
        FileUtils.deleteFile(this.animatedFile);
        FileUtils.deleteFile(this.bitmapFile);
    }

    public void onPlayButton(View v) {
        ImageButton imb = (ImageButton)v;
        if(imb.getContentDescription().equals(getResources().getString(R.string.viewer_play_animated_image_contentDescription))) {
            playGif();
        } else {
            pauseGif();
        }
    }

    public void pauseGif() {
        ImageButton imb = findViewById(R.id.viewer_play_animated_image_button);

        this.animatedImage.pause();

        imb.setContentDescription(getString(R.string.viewer_play_animated_image_contentDescription));
        Drawable ic = getResources().getDrawable(android.R.drawable.ic_media_play);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ic.setTint(getResources().getColor(R.color.colorPrimary));
        }
        imb.setImageDrawable(ic);
    }
    public void playGif() {
        ImageButton imb = findViewById(R.id.viewer_play_animated_image_button);

        this.animatedImage.start();

        imb.setContentDescription(getString(R.string.viewer_pause_animated_image_contentDescription));
        Drawable ic = getResources().getDrawable(android.R.drawable.ic_media_pause);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ic.setTint(getResources().getColor(R.color.colorPrimary));
        }
        imb.setImageDrawable(ic);
    }
    public void rewindGif() {
        this.animatedImage.stop();
        setGifFrame(0);
    }
    public void setGifFrame(int i) {
        this.animatedImage.seekToFrame(i);
    }

}
**/