package com.uwika.traveljournal;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

public class DetailPhotoActivity extends AppCompatActivity {

    ImageView photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_photo);

        String photo_url = getIntent().getExtras().getString("photo_url");
        photo = findViewById(R.id.imgV_photo);

        File imgFile = new File(photo_url);
        Glide.with(DetailPhotoActivity.this).load(Uri.fromFile(imgFile)).into(photo);
    }
}