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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Login extends AppCompatActivity {
    //Widgets for Login
    private Button signup, login;
    private EditText log_email, log_pwd;
    //Entry point for firebase authentication
    private FirebaseAuth mAuth;
    //Represents a user's information in the firebase project
    private FirebaseUser loguser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //hide the actionbar
        getSupportActionBar().hide();
        //bellow are the impostors
        mAuth = FirebaseAuth.getInstance();
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.login);

        log_email = findViewById(R.id.log_email);
        log_pwd = findViewById(R.id.log_pwd);
        /*
        Description:Verifying if the user has already logged in order to take the user directly to the
        dashboard
        Getting the current logged user and searching for him in the database in order to save his
        information for easy usage in the application  and also if his UID is not in the database it
        will be added here
        * */
        if (mAuth.getCurrentUser() != null) {
            ;
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("_user_");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    loguser = mAuth.getCurrentUser();//getting current user
                    for (DataSnapshot dss : snapshot.getChildren()) {
                        User u = dss.getValue(User.class);//saving the user
                        if (u.getEmail().equals(loguser.getEmail())) {
                            if (u.getUid() == null)
                                ref.child(dss.getKey()).child("uid").setValue(loguser.getUid());//adding UID
                            Intent i = new Intent(getBaseContext(), Dashboard.class);
                            ((logged) getApplication()).setLogged(u);//calling the class that extends the app to hold the type of user
                            startActivity(i);
                            finish();
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        //Starting the register activity
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), Register.class);
                startActivity(i);
            }
        });
        //Validating the login credentials and  signing in using the firebase method
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = log_email.getText().toString().trim();
                String pwd = log_pwd.getText().toString().trim();
                if (TextUtils.isEmpty(mail)) {//testing if the email field is empty
                    log_email.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {//testing if the  password field is empty
                    log_pwd.setError("Password is required");
                    return;
                }
                mAuth.signInWithEmailAndPassword(mail, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {//Signing in
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Login.class));
                        } else
                            Toast.makeText(Login.this, "Error !", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });


    }
}