package fr.burn38.golapp.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;

import java.io.File;
import java.io.IOException;

import fr.burn38.golapp.utils.FileUtils;
import pl.droidsonroids.gif.GifDrawable;

public class ViewerImageView extends AppCompatImageView {

    private int[] padding = {2, 2, 2, 20};
    private Rect toDisplay = new Rect(), displayZone = new Rect(), borders = new Rect();
    private Paint paintBitmap, paintText;

    private Bitmap thumbnail;
    private GifDrawable animatedImage;

    private File thumbnailFile, animatedFile;


    public ViewerImageView(Context context) {
        super(context);

        setupView();
        setupPaint();
    }
    public ViewerImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        setupView();
        setupPaint();
    }
    public ViewerImageView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);

        setupView();
        setupPaint();
    }


    private void setupPaint() {

        paintBitmap = new Paint();
        paintBitmap.setColor(Color.BLACK);
        paintBitmap.setStyle(Paint.Style.FILL);
        paintBitmap.setStrokeCap(Paint.Cap.SQUARE);
        paintBitmap.setAntiAlias(false);
        paintBitmap.setFilterBitmap(false);
        paintBitmap.setDither(false);

        paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setAntiAlias(true);
        paintText.setTextSize(24f);
    }
    private void setupView() {

        TableRow.LayoutParams lp = new TableRow.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 50));
        setLayoutParams(lp);
    }


    @Override
    public void onMeasure(int measuredWidth, int measuredHeight) {
        super.onMeasure(measuredWidth, measuredHeight);

        measuredWidth = View.MeasureSpec.getSize(measuredWidth);
        measuredHeight= View.MeasureSpec.getSize(measuredHeight);

        this.displayZone.set(padding[0], padding[1], measuredWidth-padding[2], measuredHeight-padding[3]-(int)paintText.getTextSize());

        if(this.thumbnail != null)
            this.toDisplay.set(0,0, this.thumbnail.getWidth(), this.thumbnail.getHeight());

        this.borders.set(1,1, measuredWidth-1, measuredHeight-1);

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {

        if (animatedFile != null && animatedImage != null && animatedImage.getCurrentFrameIndex() > 1) {
            canvas.drawBitmap(animatedImage.getCurrentFrame(), this.toDisplay, this.displayZone, this.paintBitmap);
            System.out.println("current frame " + animatedImage.getCurrentFrameIndex());
        } else if(thumbnail != null && thumbnailFile != null && displayZone != null)
            canvas.drawBitmap(getThumbnail(), this.toDisplay, this.displayZone,  this.paintBitmap);

        if (borders != null) {
            paintBitmap.setStyle(Paint.Style.STROKE);
            paintBitmap.setStrokeWidth(2f);
            canvas.drawRect(this.borders, paintBitmap);
            canvas.drawLine(borders.left, this.displayZone.bottom, borders.right, this.displayZone.bottom, paintBitmap);
            paintBitmap.setStyle(Paint.Style.FILL_AND_STROKE);
        }

        postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewerImageView.this.postInvalidate();
            }
        }, 100);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        invalidate();
        return true;
    }

    public void setThumbnail(File file) {

        thumbnailFile = file;
        thumbnail = FileUtils.loadBitmap(thumbnailFile);
        animatedFile = FileUtils.getCorrespondingAnimatedImageFile(file);

        if (animatedFile != null && animatedFile.exists()) {
            try {
                animatedImage = new GifDrawable(animatedFile);
            } catch (IOException e) {
                animatedImage = null;
                e.printStackTrace();
            }
        }

        setImageBitmap(thumbnail);

        System.out.println("Thumbnail set: " + file.getName());
        if (animatedFile != null && animatedImage != null)
            System.out.println("Associated animated image is : " + animatedFile.getName());

        invalidate();
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public File getThumbnailFile() { return this.thumbnailFile; }
    public File getAnimatedImageFile() { return this.animatedFile; }
    public GifDrawable getAnimatedImage() { return this.animatedImage; }

    public void play() {
        animatedImage.start();
    }

    public void stop() {
        animatedImage.stop();
    }

    public void pause() {
        animatedImage.pause();
    }

    public void reset() {
        stop();
        animatedImage.seekToFrame(0);
    }
}