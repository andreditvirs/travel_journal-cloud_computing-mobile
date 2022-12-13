package com.uwika.traveljournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button registerUser;
    private FirebaseAuth mAuth;
    private EditText editTextName,editTextUsername,editTextEmail,editTextPassword, editTextBirthdate;
    private DatePickerDialog datePickerDialog;
    private Button dateButton;
    private TextView link_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        registerUser = (Button) findViewById(R.id.btn_register);
        registerUser.setOnClickListener(this);

        link_login = findViewById(R.id.link_login);
        link_login.setOnClickListener(this);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        initDatePicker();
        dateButton = findViewById(R.id.datePickerButton);
        dateButton.setText(getTodaysDate());
    }

    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    private String makeDateString(int day, int month, int year)
    {
        return day + "-" + getMonthFormat(month) + "-" + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_register:
                registerUser();
                break;
            case R.id.link_login:
                startActivity(new Intent(this,LoginActivity.class));
                break;
        }
    }

    private void registerUser(){
        String name = editTextName.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String birthdate = dateButton.getText().toString();

        //Validation
        if(name.isEmpty()){
            editTextName.setError("Full Name Required!");
            editTextName.requestFocus();
            return;
        }
        if(username.isEmpty()){
            editTextUsername.setError("UserName Required!");
            editTextUsername.requestFocus();
            return;
        }
        if(email.isEmpty()){
            editTextEmail.setError("Email Required!");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please Provide valid email");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("Password Required!");
            editTextPassword.requestFocus();
            return;
        }

        if(birthdate.isEmpty()){
            dateButton.setError("Birthdate Required!");
            dateButton.requestFocus();
            return;
        }

        if(password.length()<6){
            editTextPassword.setError("Password must be greater than 6 character!");
            editTextPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                User user = new User(name,username,email,password,birthdate, new HashMap<>());
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(RegisterActivity.this,"User Successfully Registered",Toast.LENGTH_LONG).show();
                                                    editTextName.setText("");
                                                    editTextUsername.setText("");
                                                    editTextEmail.setText("");
                                                    editTextPassword.setText("");
                                                    dateButton.setText("01-JAN-2020");
                                                }else{
                                                    Toast.makeText(RegisterActivity.this,"Failed to Register, Try Again!",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }else{
                                Toast.makeText(RegisterActivity.this,"Failed to Register, Try Again!",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
    }


}