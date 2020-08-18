package com.example.numberrecognition;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "TESTFILEPATH";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__test);
        Bitmap testBmp = null;
        String filename = getIntent().getStringExtra("Image");
        try {
            FileInputStream is = getApplicationContext().openFileInput(filename);
            testBmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        testBmp = toGrayscale(testBmp);

        try{
            //String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            String filePath = Environment.getExternalStorageDirectory().getPath();
            Log.v(TAG, Environment.getExternalStorageDirectory().getAbsolutePath());
            File new_file = new File (filePath, "Test_sample.jpg");
            OutputStream out = new FileOutputStream(new_file);
            testBmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();

            //scanMedia( filePath);
            MediaScannerConnection.scanFile(this, new String[]{new_file.getPath()},
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            // now visible in gallery
                            Log.v("ExternalStorage", "Scanned " + path + ":");
                            Log.v("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        }
        catch (Exception e){
            e.printStackTrace();
            Log.e ("EXCEPT", Log.getStackTraceString(e));
        }

        ImageView imageView = (ImageView) findViewById(R.id.testimageView);
        imageView.setImageBitmap(testBmp);
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
}
