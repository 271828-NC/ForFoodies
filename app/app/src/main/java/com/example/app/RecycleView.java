package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class RecycleView extends AppCompatActivity implements EateryAdapter.holder.OnCardClickedListener, ReviewAdapter.Reviewholder.OnCardClickedListener {
    ImageView header;
    RecyclerView rv;
    ArrayList<Eatery> list = new ArrayList<>();
    ArrayList<Booking> listB = new ArrayList<>();
    ArrayList<Review> listR = new ArrayList<>();
    DatabaseReference dbref;
    EateryAdapter adapter;
    BookingAdapter adapterB;
    ReviewAdapter adapterR;
    String type;
    String path;
    Eatery e;
    int code;
    //class that will hold the lists of bookings, reviews, eateries
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_view);
        //        hide the actionbar
        getSupportActionBar().hide();
        header = findViewById(R.id.imageView15);

        rv = findViewById(R.id.rv_l);

        rv.setLayoutManager(new LinearLayoutManager(RecycleView.this));//LinearLayoutManager.HORIZONTAL,false));
        path = getIntent().getStringExtra("Path");//what type of items are displayed
        code = getIntent().getIntExtra("Code", 0);//what variation of a type of item are dysplayed
        int head = getIntent().getIntExtra("Header", 0);//the header for each list
        if (head == 1)
            header.setImageResource(R.drawable.restaurants);
        else if (head == 2)
            header.setImageResource(R.drawable.streetfoodheader);
        dbref = FirebaseDatabase.getInstance().getReference(path);
        if (code == 1) {//eateries
            type = getIntent().getStringExtra("Type");
            dbref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dss : snapshot.getChildren()) {
                        Eatery e = dss.getValue(Eatery.class);
                        if (e.getType().equals(type))
                            list.add(e);//adding the selected types of eateries

                    }
                    Collections.sort(list);//sorting the list
                    adapter = new EateryAdapter(list, RecycleView.this);
                    rv.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if (code == 2) {//bookings
            header.setImageResource(R.drawable.bookingheader);
            dbref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dss : snapshot.getChildren()) {
                        Booking b = dss.getValue(Booking.class);
                        if (b.getAddress().equals(((logged) getApplication()).getLogged().getUid()))
                            listB.add(b);
                    }
                    adapterB = new BookingAdapter(listB);
                    rv.setAdapter(adapterB);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if (code == 3) {//reviews
            e = getIntent().getParcelableExtra("Eatery");//the eatery reviewed
            Picasso.get().load(e.getUrl()).fit().into(header);
            dbref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dss : snapshot.getChildren()) {
                        Review r = dss.getValue(Review.class);
                        if (r.getEateryName().equals(e.getName()))
                            listR.add(r);//adding the review for the eatery selected
                    }
                    adapterR = new ReviewAdapter(listR, ((logged) getApplication()).getLogged(), RecycleView.this);
                    rv.setAdapter(adapterR);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    @Override
    public void OnCardClickedListener(int i) {
        if (code == 1) {
            Intent intent = new Intent(RecycleView.this, Details.class);
            intent.putExtra("Eatery", list.get(i));//when clicked it will bring the details activity
            startActivity(intent);

        } else if (code == 3) {
            Intent intent = new Intent(RecycleView.this, DetailedReview.class);
            intent.putExtra("Review", listR.get(i));//when clicked it will bring the detailed review activity
            startActivity(intent);
        }
    }

}