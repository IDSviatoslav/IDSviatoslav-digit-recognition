package com.example.numberrecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private TextView mPredText;
    private PaintView paintView;
    private Button mClearButton, mPredictButton;
    private Switch mModSwitch;
    private String modelType;
    private TextView mModelTypeText;
    MediaPlayer Mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        paintView = (PaintView) findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);

        modelType = "model1";

        mModelTypeText = (TextView) findViewById(R.id.modelStatusText);
        mModSwitch = (Switch) findViewById(R.id.modelTypeSwitch);
        mModSwitch.setChecked(false);
        mModSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    modelType = "model2";
                    mModelTypeText.setText("Augmented");
                }
                else {
                    modelType = "model1";
                    mModelTypeText.setText("Normal");
                }
            }
        });

        mClearButton = (Button) findViewById(R.id.clearButton);
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.clear();
            }
        });

        mPredText = (TextView) findViewById(R.id.predictionOutputText);
        mPredictButton = (Button) findViewById(R.id.predictButton);
        mPredictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            NumberPredictor pred = new NumberPredictor(MainActivity.this, modelType);
            int rawid = 0;
                try {
                    Bitmap newBitmap = Bitmap.createScaledBitmap(paintView.mBitmap, 28, 28, false);

                    //Write file
                    String filename = "bitmap.png";
                    FileOutputStream stream = getApplicationContext().openFileOutput(filename, Context.MODE_PRIVATE);
                    newBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();
                    char prediction = pred.interpret(pred.toGrayscale2(newBitmap));
                    Log.i("ModelPrediction", Character.toString(prediction));
                    newBitmap.recycle();
                    mPredText.setText(String.valueOf(prediction));
                    switch (prediction) {
                        case '0': rawid = R.raw.s0; break;
                        case '1': rawid = R.raw.s1; break;
                        case '2': rawid = R.raw.s2; break;
                        case '3': rawid = R.raw.s3; break;
                        case '4': rawid = R.raw.s4; break;
                        case '5': rawid = R.raw.s5; break;
                        case '6': rawid = R.raw.s6; break;
                        case '7': rawid = R.raw.s7; break;
                        case '8': rawid = R.raw.s8; break;
                        case '9': rawid = R.raw.s9; break;
                    }
                    Mp = MediaPlayer.create(MainActivity.this, rawid);
                    Mp.start();
                    while (Mp.isPlaying()) {
                        // do nothing
                    }
                    Mp.release();
                } catch (Exception e) {
                    Log.e("My_Exception", Log.getStackTraceString(e));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.normal:
                paintView.normal();
                return true;
            case R.id.emboss:
                paintView.emboss();
                return true;
            case R.id.blur:
                paintView.blur();
                return true;
            case R.id.clear:
                paintView.clear();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}