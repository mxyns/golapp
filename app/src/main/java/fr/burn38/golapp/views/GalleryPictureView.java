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

import fr.burn38.golapp.MainActivity;
import fr.burn38.golapp.utils.FileUtils;
import pl.droidsonroids.gif.GifDrawable;

public class GalleryPictureView extends AppCompatImageView {

    private int[] padding = {2, 2, 2, 20};
    private Rect toDisplay = new Rect(), displayZone = new Rect(), borders = new Rect();
    private Paint paintBitmap, paintText;

    private Bitmap thumbnail;
    private GifDrawable animatedImage;

    private File thumbnailFile, animatedFile;

    private long lastClick;


    public GalleryPictureView(Context context) {
        super(context);

        setupView();
        setupPaint();
    }
    public GalleryPictureView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        setupView();
        setupPaint();
    }
    public GalleryPictureView(Context context, AttributeSet attributeSet, int defStyle) {
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
    public boolean onTouchEvent(MotionEvent e) {

            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                lastClick = System.currentTimeMillis();

                if (animatedFile != null && animatedImage != null) {
                    System.out.println("Pressed image " + thumbnailFile);
                    setImageDrawable(animatedImage);
                    animatedImage.start();

                    invalidate();
                }
            } else if (e.getAction() == MotionEvent.ACTION_CANCEL || e.getAction() == MotionEvent.ACTION_UP) {
                long t = System.currentTimeMillis();

                if (animatedFile != null && animatedImage != null) {
                    setImageBitmap(thumbnail);
                    System.out.println("Released image " + thumbnailFile);
                    animatedImage.pause();
                    animatedImage.reset();
                    animatedImage.seekToFrame(0);
                }

                if (t - lastClick < 500)
                    MainActivity.startViewer(thumbnailFile);
                System.out.println("(-) Time is : " +(t-lastClick));

            }

        super.onTouchEvent(e);
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {

        if (animatedFile != null && animatedImage != null && animatedImage.isPlaying() && animatedImage.isRunning() && animatedImage.getCurrentFrameIndex() > 1) {

            canvas.drawBitmap(animatedImage.getCurrentFrame(), this.toDisplay, this.displayZone,  this.paintBitmap);
            canvas.drawText(this.thumbnailFile.getName(), this.displayZone.left+padding[0], this.displayZone.bottom+paintText.getTextSize()+paintBitmap.getStrokeWidth(), this.paintText);

        } else if(thumbnail != null && thumbnailFile != null && displayZone != null) {
            canvas.drawBitmap(getThumbnail(), this.toDisplay, this.displayZone,  this.paintBitmap);
            canvas.drawText(this.thumbnailFile.getName(), this.displayZone.left+padding[0], this.displayZone.bottom+paintText.getTextSize()+paintBitmap.getStrokeWidth(), this.paintText);
        }

        if (borders != null) {
            paintBitmap.setStyle(Paint.Style.STROKE);
            paintBitmap.setStrokeWidth(2f);
            canvas.drawRect(this.borders, paintBitmap);
            canvas.drawLine(borders.left, this.displayZone.bottom, borders.right, this.displayZone.bottom, paintBitmap);
            paintBitmap.setStyle(Paint.Style.FILL_AND_STROKE);
        }
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

    public void setSize(int w, int h, ScaleType scaleType) {
        ViewGroup.LayoutParams params = getLayoutParams();

        params.height = w;
        params.width = h;
        setScaleType(scaleType);
        requestLayout();

        System.out.println("Resized to ("+w+","+h+") -> " + "("+getWidth()+","+getHeight()+")");
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }
}