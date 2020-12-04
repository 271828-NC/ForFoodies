package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Details extends AppCompatActivity {
    //Details will  hold the rating of the restaurant and options to add, read review and booking
    ImageView iv, info;
    TextView name, specifc, location, desc;
    Button read, add, reservation;
    RatingBar rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //        hide the actionbar
        getSupportActionBar().hide();
        info = findViewById(R.id.info);
        iv = findViewById(R.id.iv_details);
        name = findViewById(R.id.tv_details_name);
        specifc = findViewById(R.id.tv_details_specific);
        location = findViewById(R.id.tv_details_location);
        desc = findViewById(R.id.tv_details);
        rating = findViewById(R.id.ratingBar2);
        read = findViewById(R.id.btn_r);
        add = findViewById(R.id.btn_ar);
        reservation = findViewById(R.id.btn_res);
        final Eatery e = getIntent().getParcelableExtra("Eatery");//we get the eatery that was selected
        Picasso.get().load(e.getUrl()).fit().into(iv);
        name.setText(e.getName());//load its details
        specifc.setText(e.getServing());
        location.setText(e.getLocation());
        desc.setText(e.getDescription());
        rating.setRating(e.getRating() / e.getRatingNr());
        if (e.getType().equals("Street Food"))//Street foods dont have reservations
            reservation.setVisibility(View.GONE);
//        get info page
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), Info.class));
            }
        });
        if (e.getType().equals("Restaurant"))//Only a critic can review a restaurant
            if (((logged) getApplication()).getLogged().getType() == 1 || ((logged) getApplication()).getLogged().getType() == 3)
                add.setVisibility(View.GONE);

//        add reservation intent
        reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Details.this, Reservation.class);
                intent.putExtra("Eatery", e);
                startActivity(intent);
            }
        });

//        add review
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), AddReviews.class);
                i.putExtra("Eatery", e);
                startActivity(i);

            }
        });
        //read review
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), RecycleView.class);
                i.putExtra("Path", "_reviews_");
                i.putExtra("Code", 3);
                i.putExtra("Eatery", e);
                startActivity(i);
            }
        });
    }
}