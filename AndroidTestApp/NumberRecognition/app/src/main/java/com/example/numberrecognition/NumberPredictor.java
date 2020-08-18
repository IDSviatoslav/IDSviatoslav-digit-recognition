package com.example.numberrecognition;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.util.Arrays;

public class NumberPredictor {
    Activity activity;
    String modelName;

    NumberPredictor(Activity activity, String modelType){
        this.activity = activity;
        if (modelType == "model1") modelName = "converted_model.tflite";
        if (modelType == "model2") modelName = "converted_model_aug.tflite";
    }

    char interpret(ByteBuffer byteBuffer){
        MappedByteBuffer tfliteModel;
        int predictionIndx = 0;
        float [][]output = new float [1][10];
        // Initialise the model
        try{
            Interpreter.Options options = new Interpreter.Options();
            tfliteModel = FileUtil.loadMappedFile(activity, modelName);
            Interpreter tflite = new Interpreter(tfliteModel, options);

            for (int j = 0; j < 10 ; j++) {
                Log.i ("bytebuffer1 " + j, Byte.toString(byteBuffer.get(j)));
            }
            int[] dims = {1, 28, 28, 1};
            tflite.resizeInput(0, dims);
            Log.i("Input","Input sample line");
            for (int j = 0; j < 10 ; j++) {
                Log.i ("bytebuffer2 " + j, Byte.toString(byteBuffer.get(j)));
            }
            tflite.run(byteBuffer, output);
            Log.i ("ModelPrediction", Arrays.toString(output[0]));
        }
        catch (IOException e){
            Log.e("tfliteSupport", "Error reading model", e);
        }
        for (int i = 0; i < output[0].length - 1; i++){
            if (output[0][predictionIndx] <= output[0][i + 1]) predictionIndx = i + 1;
        }
        char prediction = (char) ('0' + predictionIndx);
        return prediction;
    }

    public ByteBuffer toGrayscale(Bitmap bmpOriginal) {
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

        int size2 = bmpGrayscale.getRowBytes() * bmpGrayscale.getHeight();
        ByteBuffer byteBuffer = ByteBuffer.allocate(size2);
        bmpGrayscale.copyPixelsToBuffer(byteBuffer);

        return byteBuffer;
    }

    public ByteBuffer toGrayscale2(Bitmap bmpOriginal){
        Bitmap bmpGrayscale = Bitmap.createBitmap(bmpOriginal.getWidth(), bmpOriginal.getHeight(), Bitmap.Config.ARGB_8888);
        int size = bmpOriginal.getWidth()*bmpOriginal.getHeight()*4;
        ByteBuffer bmpBB = ByteBuffer.allocateDirect(size);
        //bmpOriginal.copyPixelsToBuffer(buf);

        int pixelColor, r, g, b, alpha;
        float graycolor;
        bmpBB.order(ByteOrder.nativeOrder());
        bmpBB.rewind();

        int count = 0;

        for (int i = 0; i< bmpOriginal.getHeight(); i++){
            for (int j = 0; j < bmpOriginal.getWidth(); j++){
                pixelColor = bmpOriginal.getPixel(j, i);
                alpha = (pixelColor >> 24) & 0xff;
                r = (pixelColor >> 16) & 0xff;
                g = (pixelColor >> 8) & 0xff;
                b = (pixelColor) & 0xff;
                Log.v ("initcolorR " + i + " " + j, Integer.toString(r));
                Log.v ("initcolorG " + i + " " + j, Integer.toString(g));
                Log.v ("initcolorB " + i + " " + j, Integer.toString(b));
                graycolor = (255 -  (r*0.2989f + g*0.587f + b*0.114f))/255;
                if (graycolor < 0.1) graycolor = 0;
                if (graycolor==1) graycolor = 0.85597f;
                Log.v("graycolor " + count, Float.toString(graycolor));
                count++;

                int red = (int) (255 - r*0.2989f);
                int green = (int) (255 - g*0.587f);
                int blue = (int) (255 - b*0.114f);
                Log.v ("colorR", Integer.toString(red));
                Log.v ("colorG", Integer.toString(green));
                Log.v ("colorB", Integer.toString(blue));
                int color = (alpha & 0xff) << 24 | (red & 0xff) << 16 | (green & 0xff) << 8 | (blue & 0xff);
                bmpBB.putFloat(graycolor);
                bmpGrayscale.setPixel(i, j, color);
            }
        }

        return bmpBB;
    }

    public float[][] toTrueGrayscale (Bitmap bmpOriginal){
        //int[] dim = {1, 28, 28};
        //TensorBufferFloat convTB = new TensorBufferFloat(dim);
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        int pixelColor, r, g, b;

        float[][] bmpGrayscale = new float[height][width];
        for (int i = 0; i< height; i++){
            for (int j = 0; j < width; j++){
                pixelColor = bmpOriginal.getPixel(i, j);
                r = (pixelColor >> 16) & 0xff;
                g = (pixelColor >> 8) & 0xff;
                b = (pixelColor) & 0xff;

                bmpGrayscale[i][j] = (float) (r*0.2989 + g*0.587 + b*0.114);
            }
        }
        return bmpGrayscale;
    }

    /*Bitmap centerBmp(Bitmap bmp){
        int sumX = 0, sumY = 0;
        Bitmap centeredBmp = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
        for (int i = 0; i < bmp.getHeight(); i++){
            for (int j = 0; j < bmp.getWidth(); j++){
                if (bmp.getPixel(j, i) != Color.WHITE)
                    sumX = sumX + j;
                    sumY = sumY + i;
            }
        }

        int xCentered = sumX / (bmp.getWidth()*bmp.getHeight());
        int yCentered = sumY / (bmp.getWidth()*bmp.getHeight());

        for (int i = 0; i < bmp.getHeight(); i++){
            for (int j = 0; j < bmp.getWidth(); j++){
                bmp.setPixel(j, i, Color.BLACK);
            }
        }

        return centeredBmp;
    }*/
}