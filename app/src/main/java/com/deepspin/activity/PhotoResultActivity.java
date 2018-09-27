package com.deepspin.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import com.android.volley.misc.AsyncTask;
import com.deepspin.DeepSpinApp;
import com.deepspin.DeepSpinStatics;
import com.deepspin.R;
import com.deepspin.utils.ExifUtil;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PhotoResultActivity extends AppCompatActivity {
    private ImageView mImageView;
    private String mImageUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mImageView = findViewById(R.id.imageview);
        mImageUri = DeepSpinStatics.SERVER_IP + getIntent().getStringExtra(DeepSpinStatics.EXTRA_RESULT_URI).substring(2);
        setResultImage();
    }

    private void setResultImage() {
        new MyAsyncTask().execute(mImageUri);
    }

    private class MyAsyncTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch(IOException e) {
                return null;
            }
        }

        protected void onPostExecute(Bitmap result) {
            mImageView.setImageBitmap(ExifUtil.rotateBitmap(mImageUri, result, DeepSpinApp.getCurrImageOrientation()));
        }
    }
}
