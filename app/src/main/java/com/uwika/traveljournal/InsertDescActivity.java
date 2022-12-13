package com.uwika.traveljournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class InsertDescActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView rV_last_journals;
    LastJournalAdapter last_journal_adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<LastJournalModel> last_journal_item;
    FloatingActionButton btn_create;

    TextView journal_date;
    AppCompatButton btn_save;
    EditText journal_name, journal_note;

    // create
    String currentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_REQUEST_CODE = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_desc);

        journal_date = findViewById(R.id.txtV_journal_date);
        journal_name = findViewById(R.id.editText_JournalName);
        journal_note = findViewById(R.id.editText_JournalNote);

        // Floating button create
        btn_create = findViewById(R.id.btn_create);
        btn_create.setOnClickListener(this);

        // btn save
        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);

        // last journal "photos" one time called
        rV_last_journals = findViewById(R.id.rV_last_journals);
        rV_last_journals.setHasFixedSize(true);
        SharedPreferences sharedPreferences = getSharedPreferences("uwika-travel-journal",MODE_PRIVATE);
        Set<String> photos = sharedPreferences.getStringSet("photos", null);
        showPhotos(photos);
        layout_manager = new GridLayoutManager(this, 2);
        rV_last_journals.setLayoutManager(layout_manager);
    }

    // create
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_create:
                if (checkPermission()) {
                    openCamera();
                } else {
                    requestPermission();
                }
                break;
            case R.id.btn_save:
                SharedPreferences sharedPreferences = getSharedPreferences("uwika-travel-journal",MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("journal_name", journal_name.getText().toString());
                myEdit.putString("journal_note", journal_note.getText().toString());
                myEdit.putString("journal_date", journal_date.getText().toString());
                myEdit.commit();

                startActivity(new Intent(InsertDescActivity.this,InsertFriendActivity.class));
                break;
        }
    }

    // permission camera
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(InsertDescActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    // camera
    private void openCamera(){
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.uwika.traveljournal.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File imgFile = new File(currentPhotoPath);
            if(imgFile.exists()){
                SharedPreferences sharedPreferences = getSharedPreferences("uwika-travel-journal",MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                Set<String> set = sharedPreferences.getStringSet("photos", null);
                set.add(currentPhotoPath);
                myEdit.putStringSet("photos", set);
                myEdit.commit();

                showPhotos(set);
            }
        }
    }

    private void showPhotos(Set<String> photos){
        Date date = new Date(); // your date
        // Choose time zone in which you want to interpret your Date
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"));
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        journal_date.setText(day+ " "+ getMonthForInt(month) +" "+ year);
        last_journal_item = new ArrayList<>();
        Iterator<String> it = photos.iterator();
        int i = 1;
        while(it.hasNext()){
            String photo = it.next();
            last_journal_item.add(new LastJournalModel("Foto " + i
                    , Integer.toString(day)
                    , getMonthForInt(month)
                    , R.drawable.example_journal_1
                    , photo
                    , ""
                    , "click"));
            i++;
        }

        last_journal_adapter = new LastJournalAdapter(InsertDescActivity.this, last_journal_item);
        rV_last_journals.setAdapter(last_journal_adapter);
    }

    private String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11) {
            month = months[num];
        }
        return month.substring(0, 3);
    }
}