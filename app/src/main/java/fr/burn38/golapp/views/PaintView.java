package fr.burn38.golapp.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import fr.burn38.golapp.EditorActivity;

public class PaintView extends AppCompatImageView {

    boolean[][] pixels; // to keep track of pixels
    boolean isBlack = true;
    Bitmap bitmap;
    Paint brush;
    Matrix scaleMatrix = new Matrix();

    public PaintView(Context context) {
        super(context);

        setup();
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setup();
    }

    public PaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setup();
    }

    public void setup() {

        pixels = new boolean[EditorActivity.IMAGE_HEIGHT][EditorActivity.IMAGE_WIDTH];

        brush = new Paint();
        brush.setColor(Color.BLACK);

        bitmap = Bitmap.createBitmap(EditorActivity.IMAGE_WIDTH, EditorActivity.IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
            c.drawColor(Color.WHITE);
    }

    public void clear() {
        pixels = new boolean[EditorActivity.IMAGE_HEIGHT][EditorActivity.IMAGE_WIDTH];

        setup();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX()/getWidth()* EditorActivity.IMAGE_WIDTH;
        float touchY = event.getY()/getHeight() * EditorActivity.IMAGE_HEIGHT;
        int x = (int)Math.ceil(touchX-0.5)-1, y = (int)Math.ceil(touchY-0.5)-1;
        if (x > -1 && y > -1 && y < EditorActivity.IMAGE_HEIGHT && x < EditorActivity.IMAGE_WIDTH) {
            pixels[y][x] = isBlack;
            bitmap.setPixel(x, y, brush.getColor());
        }

        postInvalidate();
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {

        scaleMatrix.setScale((float)getWidth() / bitmap.getWidth(), (float)getHeight() / bitmap.getHeight());
        canvas.drawBitmap(this.bitmap, scaleMatrix, null);
    }

    public Bitmap getImage() {

        Bitmap.Config cfg = Bitmap.Config.RGB_565;
        Bitmap bm = Bitmap.createBitmap(EditorActivity.IMAGE_WIDTH, EditorActivity.IMAGE_HEIGHT, cfg);
            Canvas c = new Canvas(bm);
            c.drawColor(Color.WHITE);

        bm.setHasAlpha(false);

        for (int y = 0; y < bm.getHeight(); ++y)
            for (int x = 0; x < bm.getWidth(); ++x) {
                if(pixels[y][x])
                    bm.setPixel(x, y, Color.BLACK);
            }
        return bm;
    }

    public int getColor() { return brush.getColor(); }
    public void changeColor(int c) {
        brush.setColor(c);
        isBlack = c == Color.BLACK;
    }

}
