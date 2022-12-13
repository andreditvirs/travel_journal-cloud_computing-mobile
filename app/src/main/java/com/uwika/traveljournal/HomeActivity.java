package com.uwika.traveljournal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
;import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.Plugin;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private static final String[] COUNTRIES = new String[]{"Sidoarjo", "Surabaya", "Jombang", "Blitar"};
    RecyclerView rV_last_journals;
    LastJournalAdapter last_journal_adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<LastJournalModel> last_journal_item = new ArrayList<>();
    FloatingActionButton btn_create;
    HashMap<String, String> my_journals = new HashMap<>();

    // create
    String currentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PERMISSION_REQUEST_CODE = 200;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = findViewById(R.id.home_appbar);
        setSupportActionBar(toolbar);

        mapView = findViewById(R.id.mapbox_home);
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);

        AutoCompleteTextView autoCTxtV_journals = findViewById(R.id.autoCTxtV_journals);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_list_item, R.id.txtV_list_item, COUNTRIES);
        autoCTxtV_journals.setAdapter(adapter);

        rV_last_journals = findViewById(R.id.rV_last_journals);
        rV_last_journals.setHasFixedSize(true);

        layout_manager = new GridLayoutManager(this, 2);
        rV_last_journals.setLayoutManager(layout_manager);

        // Floating button create
        btn_create = findViewById(R.id.btn_create);
        btn_create.setOnClickListener(this);

        // Floating button create
        btn_create = findViewById(R.id.btn_create);
        btn_create.setOnClickListener(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        UID = user.getUid();


        final TextView greetingTextView = (TextView) findViewById(R.id.greeting);

        reference.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    String name = userProfile.name;
                    String email = userProfile.email;
                    String password = userProfile.password;
                    String birthdate = userProfile.birthdate;

                    for(DataSnapshot ds : snapshot.child("journals").getChildren()) {
                        String key = ds.getKey();
                        my_journals.put(ds.getKey(), snapshot.child("journals").child(key).getValue(String.class));
                    }

                    for(Map.Entry<String, String> data : my_journals.entrySet()) {
                        String txt_my_journal = data.getValue();

                        Type mapType = new TypeToken<HashMap<String, Object>>(){}.getType();
                        HashMap<String, Object> gson = new Gson().fromJson(txt_my_journal, mapType);

                        ArrayList<String> photos = new ArrayList<>();
                        ArrayList<String> shared_friends = new ArrayList<>();
                        String journal_name = "", journal_note = "", journal_date = "";
                        try {
                            photos = (ArrayList<String>) gson.get("photos");
                            journal_name = (String) gson.get("name");
                            journal_date = (String) gson.get("date");
                            journal_note = (String) gson.get("note");
                            shared_friends = (ArrayList<String>) gson.get("shared_friends");
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                        if (journal_name == null || journal_name.length() <= 0) {
                            journal_name = "_";
                        } else if (journal_name.length() <= 12) {
                        } else {
                            journal_name = journal_name.substring(0, 12)+"...";
                        }
                        last_journal_item.add(new LastJournalModel(journal_name
                                , journal_date.split(" ")[0]
                                , journal_date.split(" ")[1] + " " +journal_date.split(" ")[2]
                                , R.drawable.example_journal_1
                                , photos.get(0)
                                , data.getKey()
                                , "click"));
                    }

                    last_journal_adapter = new LastJournalAdapter(HomeActivity.this, last_journal_item);
                    rV_last_journals.setAdapter(last_journal_adapter);

                    SharedPreferences sharedPreferences = getSharedPreferences("uwika-travel-journal",MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("profile_name", name);
                    myEdit.putString("profile_email", email);
                    myEdit.putString("profile_birthdate", birthdate);
                    myEdit.commit();

                    greetingTextView.setText("Hi, "+ name +"!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
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
        new AlertDialog.Builder(HomeActivity.this)
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
                startActivity(new Intent(this, InsertDescActivity.class));
                SharedPreferences sharedPreferences = getSharedPreferences("uwika-travel-journal",MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                Set<String> set = new HashSet<String>();
                set.add(currentPhotoPath);
                myEdit.putStringSet("photos", set);
                myEdit.commit();
            }
        }
    }
}