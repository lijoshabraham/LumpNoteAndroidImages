package com.example.imagecapture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button fromCamera, fromGallery, upload;
    private final int pick_image = 12345;
    private final int take_image = 6352;
    private EditText editText;
    ImageView imageView;
    private static final int REQUEST_CAMERA_ACCESS_PERMISSION =5674;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.textView);
        imageView = findViewById(R.id.imgView);
        //editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.calci, 0, 0, 0);
        //editText.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.calci,0);
        fromCamera = (Button) findViewById(R.id.fromCamera);
        fromGallery = (Button) findViewById(R.id.fromGallery);
        //upload.setOnClickListener(this);
        fromCamera.setOnClickListener(this);
        fromGallery.setOnClickListener(this);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            fromCamera.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.fromCamera:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA_ACCESS_PERMISSION);
                }else {
                    getImageFromCamera();
                }
                break;
            case R.id.fromGallery:
                getImageFromGallery();
                break;
//            case R.id.upload:
//                if (bitmap != null)
//                    uploadImageToServer();
//                break;
        }

    }

//    private File persistImage(Bitmap bitmap, String name) {
//        File filesDir = getApplicationContext().getFilesDir();
//        File imageFile = new File(filesDir, name + ".jpg");
//
//        OutputStream os;
//        try {
//            os = new FileOutputStream(imageFile);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
//            os.flush();
//            os.close();
//        } catch (Exception e) {
//            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
//        }
//
//        return imageFile;
//    }

    private void getImageFromCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicture.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePicture, take_image);
        }
    }

    private void getImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), pick_image);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        InputStream inputStream = null;
        if (requestCode == pick_image) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    inputStream = getContentResolver().openInputStream(data.getData());
                    bitmap = BitmapFactory.decodeStream(inputStream);

                    SpannableString ss = new SpannableString("    ");
                    Drawable d = new BitmapDrawable(getResources(), bitmap);
                    d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                    ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
                    int start = 0;
                    int end = 0;

                    if(editText.getText().toString().trim().length()>0) {
                        start = editText.getText().toString().length()-1;
                        end = editText.getText().toString().length();
                    }
                    ss.setSpan(span, start, end-2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                    editText.setText(ss);

                } catch (Exception e) {
                    e.printStackTrace();
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        } finally {
            try {
        } else if (requestCode == take_image) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");
            }
        }
    }

    public String getRealPathFromURI(Uri contentURI, Activity context) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            // cursor.close();
            return s;
        }
        // cursor.close();
        return null;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getImageFromCamera();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}
