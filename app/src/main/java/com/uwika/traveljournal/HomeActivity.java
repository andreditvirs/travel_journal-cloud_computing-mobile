package com.uwika.traveljournal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
;import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.Plugin;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private MapView mapView;
    private static final String[] COUNTRIES = new String[]{"Sidoarjo", "Surabaya", "Jombang", "Blitar"};

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
}