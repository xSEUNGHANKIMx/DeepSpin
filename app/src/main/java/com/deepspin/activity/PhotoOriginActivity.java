package com.deepspin.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.deepspin.DeepSpinApp;
import com.deepspin.DeepSpinStatics;
import com.deepspin.R;
import com.deepspin.utils.ExifUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;


public class PhotoOriginActivity extends AppCompatActivity {
    private ImageView mImageView;
    private String BASE_URL = DeepSpinStatics.SERVER_IP + "fileupload.php";
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_photo_origin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageUpload();
                mDialog.setMessage("Uploading is in Process Please wait...");
                mDialog.show();
            }
        });

        mImageView = findViewById(R.id.imageview);
        mDialog = new ProgressDialog(this);

        setCapturedImage();
    }

    private void setCapturedImage() {
        String path = DeepSpinApp.getCurrTakenPicPath();
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        int orientation = -1;

        try {
            orientation = ExifUtil.getExifOrientation(path);

        } catch (IOException e) {
            e.printStackTrace();
        }

        mImageView.setImageBitmap(ExifUtil.rotateBitmap(path, bitmap, orientation));
        DeepSpinApp.setCurrImageOrientation(orientation);
    }

    private void imageUpload() {
        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", response);
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                mDialog.hide();

                Document doc = Jsoup.parse(response);
                String src = "";

                for (org.jsoup.nodes.Element img : doc.select("img")) {
                    if(img.attr("alt").equals("output image")) {
                        src = img.attr("src");
                    }
                }

                if(!src.isEmpty()) {
                    Intent intent = new Intent(getApplicationContext(), PhotoResultActivity.class);
                    intent.putExtra(DeepSpinStatics.EXTRA_RESULT_URI, src);
                    startActivity(intent);
                }
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                mDialog.hide();
            }
        };

        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, BASE_URL, response, errorListener);

        smr.addFile("image", DeepSpinApp.getCurrTakenPicPath());
        DeepSpinApp.getInstance().addToRequestQueue(smr);
    }
}
