package com.uwika.traveljournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
;import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.Plugin;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private MapView mapView;
    private static final String[] COUNTRIES = new String[]{"Sidoarjo", "Surabaya", "Jombang", "Blitar"};
    RecyclerView rV_last_journals;
    LastJournalAdapter last_journal_adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<LastJournalModel> last_journal_item;

    private Button logout;
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

        last_journal_item = new ArrayList<>();
        for (int i = 0; i < LastJournalItem.title.length; i++){
            last_journal_item.add(new LastJournalModel(LastJournalItem.title[i]
                    , LastJournalItem.date[i].split("_")[0]
                    , LastJournalItem.date[i].split("_")[1]
                    , LastJournalItem.cover[i]));
        }

        last_journal_adapter = new LastJournalAdapter(last_journal_item);
        rV_last_journals.setAdapter(last_journal_adapter);

        // Logout Auth
        //logout = (Button) findViewById(R.id.btn_logout);
        //logout.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        FirebaseAuth.getInstance().signOut();
        //        startActivity(new Intent(HomeActivity.this,LoginActivity.class));
        //    }
        //});

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