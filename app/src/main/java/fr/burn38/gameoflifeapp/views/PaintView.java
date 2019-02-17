package fr.burn38.gameoflifeapp.views;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.PaintFlagsDrawFilter;

import androidx.appcompat.widget.AppCompatImageView;
import fr.burn38.gameoflifeapp.EditorActivity;

import android.util.AttributeSet;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

//TODO: add onDrag event
//TODO: fix antialiasing problem
public class PaintView extends AppCompatImageView {

    protected int pColor = Color.BLACK;
    protected Paint drawPaint;

    //TODO: only store value per pixel (no duplicate pixels allowed)
    protected List<Pixel> pixels = new ArrayList<>();
    //protected short pSize = 1;
    protected Canvas canvas;
    protected Bitmap bitmap;

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
        setupCanvas();
        setLayerType(View.LAYER_TYPE_SOFTWARE, drawPaint);
    }

    private void setupCanvas() {
        bitmap = Bitmap.createBitmap(EditorActivity.IMAGE_WIDTH, EditorActivity.IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);
        bitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        bitmap.setHasAlpha(false);
        setImageBitmap(bitmap);
        canvas = new Canvas(bitmap);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.ANTI_ALIAS_FLAG, 0));
        canvas.drawColor(Color.WHITE);
    }

    private void setupPaint() {
        drawPaint = new Paint();
        drawPaint.setColor(pColor);
        drawPaint.setStyle(Paint.Style.FILL);
        drawPaint.setStrokeCap(Paint.Cap.SQUARE);
        drawPaint.setAntiAlias(false);
        drawPaint.setFilterBitmap(false);
        drawPaint.setDither(false);
    }

    public void changeColor(int c) {
        pColor = c;
        drawPaint.setColor(pColor);
    }

    public int getColor() {
        return pColor;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX()/getWidth()*EditorActivity.IMAGE_WIDTH;
        float touchY = event.getY()/getHeight()*EditorActivity.IMAGE_HEIGHT;
        int x = (int)Math.ceil(touchX-0.5)-1, y = (int)Math.ceil(touchY-0.5)-1;
        if (x > -1 && y > -1 && y < EditorActivity.IMAGE_HEIGHT && x < EditorActivity.IMAGE_WIDTH)
            pixels.add(new Pixel(x, y, pColor));

        display(bitmap);

        postInvalidate();
        return true;
    }

    private void display(Bitmap bm) {
        for (Pixel p : pixels) {
            bm.setPixel(p.x(), p.y(), p.color());
        }
        invalidate();
    }

    public void clear() {
        pixels = new ArrayList<>();
        setupCanvas();
        invalidate();
    }

    //TODO: convert image to 1 bit color depth
    public Bitmap getImage() {
        Bitmap.Config cfg = Bitmap.Config.RGB_565;
        Bitmap bm = Bitmap.createBitmap(EditorActivity.IMAGE_WIDTH, EditorActivity.IMAGE_HEIGHT, cfg);

        bm.setHasAlpha(false);

        Canvas c = new Canvas(bm);
        c.drawColor(Color.WHITE);

        for (Pixel p : pixels) {
            if(p.color() == Color.BLACK)
                bm.setPixel(p.x(), p.y(), p.color());
        }
        return bm;
    }
}
