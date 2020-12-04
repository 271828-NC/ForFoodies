package com.example.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Register extends AppCompatActivity {

    public static final int SELECT_PICTURE = 1;
    public static final String URL = "URL";
    private Uri url;
    int ok=0;
    EditText fname, lname, login, email, password;
    Button signup;
    ImageView chooseprofilepic;
    private FirebaseAuth mAuth;
    DatabaseReference dbref;
    //Creating a storage reference in order to store the image profile
    StorageReference sref = FirebaseStorage.getInstance().getReference("usersProfile");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //        hide the actionbar
        getSupportActionBar().hide();
        signup = findViewById(R.id.signup);
        chooseprofilepic = findViewById(R.id.chooseprofilepic);
        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        login = findViewById(R.id.login);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        //db reference
        dbref = FirebaseDatabase.getInstance().getReference("_user_");


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fn = fname.getText().toString().trim();
                final String ln = lname.getText().toString().trim();
                final String log = login.getText().toString().trim();
                final String mail = email.getText().toString().trim();
                final String pwd = password.getText().toString().trim();
                //Gathering the details in order to register the user and validate them
                if (TextUtils.isEmpty(fn)) {
                    fname.setError("First Name is required");
                    return;
                }
                if (TextUtils.isEmpty(ln)) {
                    lname.setError("Last Name is required");
                    return;
                }
                if (TextUtils.isEmpty(log)) {
                    login.setError("Username is required");
                    return;
                }
                if (TextUtils.isEmpty(mail)) {
                    email.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    password.setError("Password is required");
                    return;
                }
                if (pwd.length() < 6) {
                    password.setError("Password needs to be 6 characters or more");
                    return;
                }

                 /*
                The method  that creates a user with email and password and using try to make sure
                no error emerges while creating the user with its details
                * */
                mAuth.createUserWithEmailAndPassword(mail, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            try {
                                final StorageReference reference = sref.child(fn + "." + getExt(url));
                                reference.putFile(url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String downloadURL = uri.toString();//Creating image url
                                                User user = new User(fn, ln, mail, pwd, log, downloadURL, 1);//creating a new user
                                                String key = dbref.push().getKey();//creating a key
                                                dbref.child(key).setValue(user);//adding the user to the firebase
                                                Intent i=new Intent(getBaseContext(), Login.class);
                                                //We send the user back to the login screen in order to save his details in the global user
                                                startActivity(new Intent(getApplicationContext(), Login.class));

                                            }
                                        });
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                                Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {Toast.makeText(Register.this, "Please select a profile picture", Toast.LENGTH_SHORT).show();}
                        } else Toast.makeText(Register.this, "Error please try again !", Toast.LENGTH_SHORT).show();

                    }
                });
            }

        });


        chooseprofilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ok=1;
                //intent for choosing a picture
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select picture"), SELECT_PICTURE);


            }

        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //verify the image pick
        if (requestCode == SELECT_PICTURE) {
            url = data.getData();
            Picasso.get().load(url).into(chooseprofilepic);
        }
    }

    private String getExt(Uri uri) {
        //This method helps getting the extension of the image selected
        ContentResolver resolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(resolver.getType(uri));
    }
}
