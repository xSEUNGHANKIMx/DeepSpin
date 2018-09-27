package com.deepspin.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.deepspin.DeepSpinApp;
import com.deepspin.DeepSpinStatics;
import com.deepspin.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback {

    private ImageButton mTakePictureBtn;
    private File mImgFile = null;
    private Context mContext;
    private DeepSpinApp mApp;
    private static String[] PERMISSIONS_REQ = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private static final int REQUEST_CODE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        checkPermissions();

        setContentView(R.layout.activity_main);
        mTakePictureBtn = findViewById(R.id.button_image);
        mApp = DeepSpinApp.getInstance();

        mTakePictureBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                takePicture(v);
            }
        });
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_REQ,
                    REQUEST_CODE_PERMISSION
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                if(grantResults.length > 0) {
                    for(int i = 0; i < grantResults.length; ++i) {
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            this.finish();
                            break;
                        }
                    }
                }
                break;
        }
    }

    public void takePicture(View view) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            mImgFile = getOutputMediaFile();
        } catch (IOException ex) {
            Log.e(getClass().getName(), "Unable to create Image File", ex);
        }
        Uri uri = FileProvider.getUriForFile(this, "com.deepspin", mImgFile);
        mApp.setCurrTakenPicPath(mImgFile.getAbsolutePath());

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, DeepSpinStatics.RESULT_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DeepSpinStatics.RESULT_CAPTURE) {
            if (resultCode == RESULT_OK) {
                startActivityForResult(new Intent(mContext, PhotoOriginActivity.class), DeepSpinStatics.RESULT_CAPTURE_RESULT);
            }
        }
    }

    private File getOutputMediaFile() throws IOException {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "DeepSpin");
        File imageFile = null;
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }
}
