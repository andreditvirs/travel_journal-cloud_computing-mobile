package com.uwika.traveljournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.renderscript.Script;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

public class InsertFriendActivity extends AppCompatActivity implements View.OnClickListener{

    RecyclerView rV_last_journals;
    LastJournalAdapter last_journal_adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<LastJournalModel> last_journal_item;
    TextView journal_date;

    AutoCompleteTextView autoCTxtV_friends;
    AppCompatButton btn_send;
    TextView txtV_skip;

    // fetch data
    private FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<String> friends = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_friend);

        user = FirebaseAuth.getInstance().getCurrentUser();
        journal_date = findViewById(R.id.txtV_journal_date);
        btn_send = findViewById(R.id.btn_send);
        txtV_skip = findViewById(R.id.txtV_skip);

        btn_send.setOnClickListener(this);
        txtV_skip.setOnClickListener(this);

        // add friends
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        getDataUser();

        // last journal "photos" one time called
        rV_last_journals = findViewById(R.id.rV_last_journals);
        rV_last_journals.setHasFixedSize(true);
        SharedPreferences sharedPreferences = getSharedPreferences("uwika-travel-journal",MODE_PRIVATE);
        Set<String> photos = sharedPreferences.getStringSet("photos", null);
        showPhotos(photos);
        layout_manager = new GridLayoutManager(this, 2);
        rV_last_journals.setLayoutManager(layout_manager);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:{
                String friend = autoCTxtV_friends.getText().toString();
                Set<String> shared_friends = new HashSet<String>();;
                shared_friends.add(friend);
                if(!friends.contains(friend)){
                    Toast.makeText(InsertFriendActivity.this, "Pilih nama teman berdasarkan daftar yang ada", Toast.LENGTH_LONG).show();
                    return;
                }
                break;
            }
            case R.id.txtV_skip : {
                SharedPreferences sharedPreferences = getSharedPreferences("uwika-travel-journal",MODE_PRIVATE);
                Set<String> photos = sharedPreferences.getStringSet("photos", null);
                String journal_name = sharedPreferences.getString("journal_name", null);
                String journal_date = sharedPreferences.getString("journal_date", null);
                String journal_note = sharedPreferences.getString("journal_note", null);

                String friend = autoCTxtV_friends.getText().toString();
                Set<String> shared_friends = new HashSet<String>();

                HashMap<String, Object> values = new HashMap<>();
                values.put("photos", photos);
                values.put("name", journal_name);
                values.put("note", journal_note);
                values.put("date", journal_date);
                values.put("shared_friends", shared_friends);

                Gson gson = new Gson();
                String json = gson.toJson(values);

                String uniqueID = UUID.randomUUID().toString();
                HashMap<String, Object> data = new HashMap<>();
                data.put(uniqueID, json);

                databaseReference.child(user.getUid()).child("journals").updateChildren(data);

                SharedPreferences preferences = getSharedPreferences("uwika-travel-journal", MODE_PRIVATE);
                preferences.edit().remove("journal_name").commit();
                preferences.edit().remove("journal_note").commit();
                preferences.edit().remove("journal_date").commit();
                preferences.edit().remove("photos").commit();
                Toast.makeText(InsertFriendActivity.this, "Kenangan "+ journal_name+" berhasil ditambahkan!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(InsertFriendActivity.this, HomeActivity.class));
                this.finish();
                break;
            }
        }
    }

    private void getDataUser() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.child("name").getValue(String.class);

                    SharedPreferences sharedPreferences = getSharedPreferences("uwika-travel-journal",MODE_PRIVATE);
                    String profile_name = sharedPreferences.getString("profile_name", null);
                    if(!name.equals(profile_name)){
                        friends.add(name);
                    }
                }

                autoCTxtV_friends = findViewById(R.id.autoCTxtV_friends);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(InsertFriendActivity.this, R.layout.custom_list_item, R.id.txtV_list_item, friends);
                autoCTxtV_friends.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InsertFriendActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
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
                    , photo));
            i++;
        }

        last_journal_adapter = new LastJournalAdapter(last_journal_item);
        rV_last_journals.setAdapter(last_journal_adapter);
    }

    private String getMonthForInt(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11) {
            month = months[num];
        }
        return month;
    }
}