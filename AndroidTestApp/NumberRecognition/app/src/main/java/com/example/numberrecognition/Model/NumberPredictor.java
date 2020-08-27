package com.example.numberrecognition.Model;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;

public class NumberPredictor {
    Activity activity;
    String modelName;

    public NumberPredictor(Activity activity, String modelType){
        this.activity = activity;
        if (modelType == "model1") modelName = "converted_model.tflite";
        if (modelType == "model2") modelName = "converted_model_aug.tflite";
    }

    public char interpret(Bitmap inputBitmap){
        ByteBuffer inputGrayByteBuffer = NumberPredictor.toGrayscale(inputBitmap);

        MappedByteBuffer tfliteModel;
        int predictionIndx = 0;
        float [][]output = new float [1][10];
        // Initialise the model
        try{
            Interpreter.Options options = new Interpreter.Options();
            tfliteModel = FileUtil.loadMappedFile(activity, modelName);
            Interpreter tflite = new Interpreter(tfliteModel, options);

            int[] dims = {1, 28, 28, 1};
            tflite.resizeInput(0, dims);
            tflite.run(inputGrayByteBuffer, output);
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

    private static ByteBuffer toGrayscale(Bitmap bmpOriginal){
       // Bitmap bmpGrayscale = Bitmap.createBitmap(bmpOriginal.getWidth(), bmpOriginal.getHeight(), Bitmap.Config.ARGB_8888);
        int size = bmpOriginal.getWidth()*bmpOriginal.getHeight()*4;
        ByteBuffer bmpBB = ByteBuffer.allocateDirect(size);

        int pixelColor, r, g, b, alpha;
        float graycolor;
        bmpBB.order(ByteOrder.nativeOrder());
        bmpBB.rewind();


        for (int i = 0; i< bmpOriginal.getHeight(); i++){
            for (int j = 0; j < bmpOriginal.getWidth(); j++){
                pixelColor = bmpOriginal.getPixel(j, i);
                alpha = (pixelColor >> 24) & 0xff;
                r = (pixelColor >> 16) & 0xff;
                g = (pixelColor >> 8) & 0xff;
                b = (pixelColor) & 0xff;

                graycolor = (255 -  (r*0.2989f + g*0.587f + b*0.114f))/255;
                if (graycolor < 0.1) graycolor = 0;
                if (graycolor==1) graycolor = 0.85597f;

                int red = (int) (255 - r*0.2989f);
                int green = (int) (255 - g*0.587f);
                int blue = (int) (255 - b*0.114f);

                int color = (alpha & 0xff) << 24 | (red & 0xff) << 16 | (green & 0xff) << 8 | (blue & 0xff);
                bmpBB.putFloat(graycolor);
                //bmpGrayscale.setPixel(i, j, color);
            }
        }

        return bmpBB;
    }
}