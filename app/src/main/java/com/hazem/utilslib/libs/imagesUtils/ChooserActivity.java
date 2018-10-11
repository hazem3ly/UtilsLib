package com.hazem.utilslib.libs.imagesUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;

public class ChooserActivity extends Activity {

    public static final String TYPE = "type";

    public static final int IMAGE = 0;
    public static final int CAPTURE = 1;
    public static final int FILE = 2;

    private static final int REQUEST_CAMERA = 3;
    private static final int SELECT_FILE = 4;

    public static ImagesUtils.OnChooserFileResult chooserImageResultCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra(TYPE)) {
            switch (getIntent().getIntExtra(TYPE, -1)) {
                case IMAGE:
                    imageIntent();
                    break;
                case CAPTURE:
                    captureIntent();
                    break;
                case FILE:
                    selectFile();
                default:
                    finish();
            }
        } else {
            finish();
        }
    }

    private void selectFile() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @SuppressLint("NewApi")
    private void captureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Uri photoURI = null;
            try {
                File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                photoURI = FileProvider.getUriForFile(this,
                        this.getApplicationContext().getPackageName() + ".provider",
                        file);

            } catch (Exception ex) {
                Log.e("TakePicture", ex.getMessage());
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                takePictureIntent.setClipData(ClipData.newRawUri("", photoURI));
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            startActivityForResult(takePictureIntent, REQUEST_CAMERA);
        }
    }

    private void imageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureMethodTwo();
        } else finish();
    }


    private void onCaptureMethodTwo() {
        File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
        if (chooserImageResultCallback != null) {
            chooserImageResultCallback.OnResult(file.getAbsolutePath());
            finish();
        } else finish();


    }

    private void onSelectFromGalleryResult(Intent data) {

        String realPath = ImagesUtils.getFilePathFromUri(this, data.getData());

        if (chooserImageResultCallback != null) {
            chooserImageResultCallback.OnResult(realPath);
            finish();
        } else finish();


    }


}



