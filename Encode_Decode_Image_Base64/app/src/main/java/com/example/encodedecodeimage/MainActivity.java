package com.example.encodedecodeimage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button btn_encode,btn_decode;
    ImageView imageView;
    TextView textView;
    String sImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_encode=findViewById(R.id.btn_encode);
        btn_decode=findViewById(R.id.btn_decode);
        textView=findViewById(R.id.text_view);
        imageView=findViewById(R.id.image_view);

        btn_encode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                    ,100);
                }else {
                    selectImage();
                }
            }
        });


        btn_decode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte [] bytes =Base64.decode(sImage,Base64.DEFAULT);

                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                imageView.setImageBitmap(bitmap);
            }
        });

    }

    private void selectImage() {
        textView.setText("");
        imageView.setImageBitmap(null);
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Select Image"),
                100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==100 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            selectImage();
        else {
            Toast.makeText(getApplicationContext(), "Permission Denied...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100 && resultCode ==RESULT_OK && data!=null){
            Uri uri=data.getData();
            try {

                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uri);

                ByteArrayOutputStream stream=new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                byte [] bytes=stream.toByteArray();

                sImage= Base64.encodeToString(bytes,Base64.DEFAULT);

                textView.setText(sImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}