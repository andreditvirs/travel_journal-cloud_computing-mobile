package com.uwika.traveljournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
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
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class DetailJournalActivity extends AppCompatActivity {

    String uuid;
    private FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    RecyclerView rV_last_journals;
    LastJournalAdapter last_journal_adapter;
    RecyclerView.LayoutManager layout_manager;
    ArrayList<LastJournalModel> last_journal_item = new ArrayList<>();
    TextView txtV_journal_name, txtV_journal_note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_journal);
        uuid = getIntent().getExtras().getString("uuid");

        txtV_journal_name = findViewById(R.id.txtV_journal_name);
        txtV_journal_note = findViewById(R.id.txtV_journal_note);

        rV_last_journals = findViewById(R.id.rV_last_journals);
        rV_last_journals.setHasFixedSize(true);

        layout_manager = new GridLayoutManager(this, 2);
        rV_last_journals.setLayoutManager(layout_manager);

        // get data journal by uuid
        user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        getDataJournal(uuid);
    }

    private void getDataJournal(String uuid) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) { // data users
                    for(DataSnapshot subDs : ds.child("journals").getChildren()){
                        if(subDs.getKey().equals(uuid)){
                            String txt_my_journal = subDs.getValue(String.class);

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

                            txtV_journal_name.setText(journal_name);
                            txtV_journal_note.setText(journal_note);

                            for(int i = 0; i < photos.size(); i++){
                                last_journal_item.add(new LastJournalModel(""
                                        , journal_date.split(" ")[0]
                                        , journal_date.split(" ")[1] + " " +journal_date.split(" ")[2]
                                        , R.drawable.example_journal_1
                                        , photos.get(i)
                                        , uuid
                                        , "show"));
                            }

                            last_journal_adapter = new LastJournalAdapter(DetailJournalActivity.this, last_journal_item);
                            rV_last_journals.setAdapter(last_journal_adapter);

                            break;
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailJournalActivity.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}