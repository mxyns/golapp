package fr.burn38.golapp;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import fr.burn38.golapp.utils.FileUtils.Folder;
import fr.burn38.golapp.views.GalleryPictureView;

public class GalleryActivity extends AppCompatActivity {

    TableLayout mainLayout;
    int imgPerLine = 3;
    static Folder[] toScan = new Folder[] {
                        new Folder(new File(MainActivity.getInternalStorageDirectory(), "imgs"))
                        //,new Folder(new File(MainActivity.getCacheDirectory(), "imgs"))
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mainLayout = findViewById(R.id.gallery_table_layout);
        System.out.println("layout: " +mainLayout.toString());

        int imageCount = 0;

        System.out.println("toScan is " + (toScan == null ? "" : "not ") + "null");
        for (Folder f : toScan) {
            if(f.list() != null) {
                System.out.println("Folder " + f.getName() + " contains " + f.list().length + " files");
                imageCount += f.list().length;
            }
        }

        if (imageCount != 0) {
            int lineCount = (int) Math.ceil((double) imageCount / imgPerLine);
            ArrayList<TableRow> rows = generateRows(lineCount);
            populateTable(rows, imageCount);
        }

        System.out.println("row count: "+mainLayout.getChildCount());

        ((TextView)findViewById(R.id.gallery_title)).setText(getString(R.string.gallery_title, imageCount));
    }


    public ArrayList<TableRow> generateRows(int count) {
        ArrayList<TableRow> rows = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            rows.add(new TableRow(GalleryActivity.this));

            System.out.println("[generateRows] new row, now = "+mainLayout.getChildCount());
        }
        return rows;
    }
    public TableRow getRow(TableLayout tbl, int index) {
        //System.out.println("getRow n°"+index+"/"+(tbl.getChildCount()-1));
        int a = 0;
        for (int i = 0; i < tbl.getChildCount(); i++) {
            //System.out.println("["+i+"], a="+a);
            if (tbl.getChildAt(i) instanceof TableRow) {
                //System.out.println("    isRow and a="+a+", index="+index);
                if (index == a) {
                    //System.out.println("        result != null;");
                    return (TableRow) tbl.getChildAt(i);
                } else a++;
            }
        }

        //System.out.println("        result=null;");
        return null;
    }

    public void populateTable(ArrayList<TableRow> rows, int imageCount) {

        TableLayout table = findViewById(R.id.gallery_table_layout);
            table.removeAllViews();

        int c = 0; //count pictures
        Point screen = new Point();
        getWindowManager().getDefaultDisplay().getSize(screen);

        int marginSize = 25;
        int pictureSize = screen.x/imgPerLine - 2*marginSize;
        System.out.println("pictureSize: " + pictureSize);

        for (Folder folder : toScan) { // foreach Folder scanned
            final File[] ls = folder.getFileChildren();
            for (File l : ls) { // foreach Image in Folder
                final GalleryPictureView pic = createBox(l, pictureSize, pictureSize, ImageView.ScaleType.FIT_CENTER); // make a View

                int rowId = c / imgPerLine;
                TableRow row = rows.get(rowId);

                TableRow.LayoutParams imgParams = new TableRow.LayoutParams(pictureSize, pictureSize);
                    imgParams.leftMargin = marginSize;
                    imgParams.rightMargin = imgParams.leftMargin;
                row.addView(pic, imgParams);

                if (c % imgPerLine == imgPerLine - 1 || c == imageCount-1) {// if last of row or last
                    row.setGravity(Gravity.CENTER_HORIZONTAL);

                    TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                        rowParams.bottomMargin = marginSize;
                    mainLayout.addView(row, rowParams);
                }
                mainLayout.requestLayout();
                c++;
            }
        }
    }
    public GalleryPictureView createBox(File f, int w, int h, ImageView.ScaleType scaleType) {
        GalleryPictureView picture = new GalleryPictureView(GalleryActivity.this);
            picture.setThumbnail(f);
            picture.setSize(w, h, scaleType);

        return picture;
    }
}
