package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Dashboard extends AppCompatActivity {
    Button settings, e_restaurant, e_streetfood, logout, addRestaurant, addStreetfood;
    TextView fullname, type;
    ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        //Creating a database reference towards the users node
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("_user_");
        getSupportActionBar().hide();
        //Saving  the logged in user
        final User User = ((logged) getApplication()).getLogged();
        settings = findViewById(R.id.settings);
        e_restaurant = findViewById(R.id.e_restaurant);
        e_streetfood = findViewById(R.id.e_streetfood);
        logout = findViewById(R.id.logout);
        addRestaurant = findViewById(R.id.addRestaurant);
        addStreetfood = findViewById(R.id.addStreetfood);
        fullname = findViewById(R.id.tv_fullname);
        type = findViewById(R.id.tv_type);
        avatar = findViewById(R.id.avatar);
        fullname.setText(User.getfName() + " " + User.getlName());//Setting the name inside the text view
        //The switch finds out the type of user is logged in order to display its type
        switch (User.getType()) {
            case 1: {
                type.setText("Standard User");
                break;
            }
            case 2: {
                type.setText("Food Critic");
                break;
            }
            case 3: {
                type.setText("Administrator");
                break;
            }
        }
        Picasso.get().load(User.getUrl()).fit().into(avatar);//Loading the user profile picture in image view
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//Clicking on the profile picture takes him to his profile page
                Intent i = new Intent(getBaseContext(), UserProfile.class);
                i.putExtra("User", User);//Adding the user that needs to be displayed
                startActivity(i);
            }
        });
        if (User.getType() == 1 || User.getType() == 2)//The add restaurant functionality is not available for anyone but the admin
            addRestaurant.setVisibility(View.INVISIBLE);

        //        go to settings page
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//Takes the user to the settings activity
                //In the if statement it is differentiated from the users and admins
                if (((logged) getApplication()).getLogged().getType() == 1 || ((logged) getApplication()).getLogged().getType() == 2) {
                    Intent i = new Intent(getBaseContext(), Settings.class);
                    startActivity(i);
                } else if (((logged) getApplication()).getLogged().getType() == 3) {
                    Intent i = new Intent(getBaseContext(), PersonalSettingsAdmin.class);
                    startActivity(i);
                }
            }


        });

//        explore restaurants
        e_restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), RecycleView.class);
                i.putExtra("Path", "Eatery");
                i.putExtra("Code", 1);
                i.putExtra("Type", "Restaurant");
                i.putExtra("Header", 1);
                startActivity(i);

            }
        });

//        explore street food
        e_streetfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), RecycleView.class);
                i.putExtra("Path", "Eatery");
                i.putExtra("Code", 1);
                i.putExtra("Type", "Street Food");
                i.putExtra("Header", 2);
                startActivity(i);
            }
        });

//      logout button
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finishAffinity();
                Toast.makeText(getBaseContext(), "Logout Successful!", Toast.LENGTH_SHORT).show();
            }
        });

//        add restaurant
        addRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), AddEatery.class);
                i.putExtra("Type", "Restaurant");
                startActivity(i);
            }
        });


//        add street food
        addStreetfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), AddEatery.class);
                i.putExtra("Type", "Street Food");
                startActivity(i);
            }
        });

    }
}
