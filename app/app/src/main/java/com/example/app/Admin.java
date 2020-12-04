package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Admin extends AppCompatActivity {

    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("_user_");
    String path;
    Button promotion;
    EditText criticmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        //        hide the actionbar
        getSupportActionBar().hide();
        promotion = findViewById(R.id.btn_promotion);
        criticmail = findViewById(R.id.et_criticmail);
//        go back to settings
        promotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the email address that is promoted and validate it
                final String mail = criticmail.getText().toString().trim();
                if (TextUtils.isEmpty(mail)) {
                    criticmail.setError("First Name is required");
                    return;
                }
                dbref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            User u = ds.getValue(User.class);//find the user with that address
                            if (u.getEmail().equals(mail)) {
                                path = ds.getKey();
                                dbref.child(path).child("type").setValue(2);//change him to food critic
                                Toast.makeText(Admin.this, "User promoted to critic !", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getBaseContext(), Dashboard.class));
                                finish();

                            }
                        }
                        if (path == null)
                            criticmail.setError("The email address selected is incorrect or doesn't exist");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
    }
}
