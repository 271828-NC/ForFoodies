package com.example.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailedReview extends AppCompatActivity {
    TextView login, rDesc, likes, dislikes;
    ImageView profilePic, delete, like, dislike;
    RatingBar rating;
    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("_user_");//Firebase reference to users
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("_reviews_");//Firebase reference to reviews
    DatabaseReference eref = FirebaseDatabase.getInstance().getReference("Eatery");//Firebase reference to Eateries
    String uPath, ePath;
    Button profile;
    boolean like_clicked = true;//checks if  the  like clicked button has been clicked
    boolean dislike_clicked = true;//checks if  the dislike clicked button has been clicked
    Eatery reviewedE;//the Eatery reviewed
    User reviewer;//the user that reviewed the eatery
    ArrayList<String> Like = new ArrayList<>();//The list that holds the UID of all users that liked the review
    ArrayList<String> Dislike = new ArrayList<>();//The list that holds the UID of all users that disliked the review

    public void onBackPressed() {// overload of the back button to send the user back to dashboard
        startActivity(new Intent(getBaseContext(), Dashboard.class));
        if (reviewedE.getType().equals("Restaurant")) {
            Intent i = new Intent(getBaseContext(), RecycleView.class);
            i.putExtra("Path", "Eatery");
            i.putExtra("Code", 1);
            i.putExtra("Type", "Restaurant");
            i.putExtra("Header", 1);
            startActivity(i);

        } else {
            Intent i = new Intent(getBaseContext(), RecycleView.class);
            i.putExtra("Path", "Eatery");
            i.putExtra("Code", 1);
            i.putExtra("Type", "Street Food");
            i.putExtra("Header", 2);
            startActivity(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_review);
        //        hide the actionbar
        getSupportActionBar().hide();
        final Review r = getIntent().getParcelableExtra("Review");//the review that was passed when the review was clicked
        final User log = ((logged) getApplication()).getLogged();//The logged user
        profile = findViewById(R.id.btn_profile);
        login = findViewById(R.id.tv_detailedreview_login);
        rDesc = findViewById(R.id.review_desc);
        likes = findViewById(R.id.tv_review_likes2);
        dislikes = findViewById(R.id.tv_dislikes2);
        profilePic = findViewById(R.id.iv_detailedreview_pic);
        delete = findViewById(R.id.iv_delete);
        like = findViewById(R.id.iv_like);
        dislike = findViewById(R.id.iv_dislike);
        rating = findViewById(R.id.detailed_reviewRB);
        rating.setRating(r.getRating());
        rDesc.setText(r.getReview());
        likes.setText(String.valueOf(r.getLikes()));
        dislikes.setText(String.valueOf(r.getDislikes()));
        eref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {//finding the eatery reviewed
                for (DataSnapshot dss : snapshot.getChildren()) {
                    Eatery e = dss.getValue(Eatery.class);
                    if (e.getName().equals(r.getEateryName())) {
                        reviewedE = e;//saving the reviewed eatery
                        ePath = dss.getKey();//the path to the eatery
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dss : snapshot.getChildren()) {//finding the reviewer in firebase
                    {
                        User u = dss.getValue(User.class);
                        if (u.getUid().equals(r.getReviewerID())) {
                            reviewer=u;//saving the reviewer
                            login.setText(u.getLogin());//displaying its login
                            Picasso.get().load(u.getUrl()).into(profilePic);//loading its profile picture
                        }
                        if (u.getUid().equals(log.getUid()))
                            uPath = u.getUid();//geting the path of the logged user

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        int ok=0;
        if (r.getReviewerID().equals(log.getUid()))//if the reviwer is the logged user he can see the  remove review option
            ok=1;
        else if(log.getType() != 3|| ok==0)
            delete.setVisibility(View.GONE);//Removing the the remove option if the the user is not an admin and he is not the reviewer
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dss : snapshot.getChildren()) {
                    Review rev = dss.getValue(Review.class);
                    if (rev.getPath().equals(r.getPath())) {//gathering the UIDs of users who liked or disliked the review
                        for (DataSnapshot ds : dss.child("Like").getChildren()) {
                            Like.add(ds.getValue(String.class));//saving the uid
                            Like.add(ds.getKey());//saving the path of  the  saved uid


                        }
                        for (DataSnapshot ds : dss.child("Dislike").getChildren()) {
                            Dislike.add(ds.getValue(String.class));
                            Dislike.add(ds.getKey());


                        }
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        profile.setOnClickListener(new View.OnClickListener() {//takes the user to the reviewer profile
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), UserProfile.class);
                i.putExtra("User", reviewer);
                startActivity(i);

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//the delete option which removes a restaurant from firebase
                eref.child(ePath).child("rating").setValue(reviewedE.getRating() - r.getRating());//Update the eatery rating once the review is removed
                eref.child(ePath).child("ratingNr").setValue(reviewedE.getRatingNr() - 1);
                ref.child(r.getPath()).removeValue();
                Toast.makeText(DetailedReview.this, "Review removed !", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getBaseContext(), Dashboard.class));//return to dashboard
            }
        });
        like.setOnClickListener(new View.OnClickListener() {

            boolean exists;
            boolean movedDislike;
            String npath;

            @Override
            public void onClick(View v) {
                like_clicked = true;//we record that the like button is clicked
                movedDislike = false;//we record that the dislike button is not pressed and there is no need to move values from dislike
                exists = false;//we record that the current user didnt like this review before
                /*
                the for loop is for the moment the current user disliked the review and then pressed the like button
                the  dislike counter is lowered by one and the uid is removed from Dislike field
                the like counter is increased by one and a uid is added to Like field
                 */
                for (int i = 0; i < Dislike.size() && dislike_clicked == true; i++)
                    if (Dislike.get(i).equals(log.getUid())) {//checking if the  uid from Dislike is the same as the logged user
                        ref.child(r.getPath()).child("dislikes").setValue(r.getDislikes() - 1);//remove 1 dislike from review in firebase
                        r.setDislikes(r.getDislikes() - 1);//remove 1 dislike in current review
                        ref.child(r.getPath()).child("Dislike").child(Dislike.get(i + 1)).removeValue();//remove the field inside the Dislike field in firbase
                        Dislike.remove(i);//remove  the uid  from the local list
                        Dislike.remove(i);//remove the path from the local list
                        dislikes.setText(String.valueOf(r.getDislikes()));//update textview
                        ref.child(r.getPath()).child("likes").setValue(r.getLikes() + 1);//update like with 1 in firebase
                        r.setLikes(r.getLikes() + 1);//add 1 like
                        npath = ref.push().getKey();//create path for new user that liked the review
                        ref.child(r.getPath()).child("Like").child(npath).setValue(uPath);//add the uid to Like
                        likes.setText(String.valueOf(r.getLikes()));//update textview
                        Like.add(uPath);//update the local list
                        Like.add(npath);
                        movedDislike = true;//that dislike has been moved
                        exists = true;//the user liked this review
                        dislike_clicked = false;//the dislike is no longer clicked
                        like_clicked = true;//the like is clicked
                    }

                if (Like.size() == 0) {//if the list is empty we can  add  the like and update the firebase
                    ref.child(r.getPath()).child("likes").setValue(r.getLikes() + 1);
                    r.setLikes(r.getLikes() + 1);
                    npath = ref.push().getKey();
                    ref.child(r.getPath()).child("Like").child(npath).setValue(uPath);
                    likes.setText(String.valueOf(r.getLikes()));
                    Like.add(uPath);
                    Like.add(npath);
                    exists = true;
                    //if the list is not empty then we check the local list if the user clicked like before
                    //in this situation the user didnt click dislike instead he pressed like again
                } else for (int i = 0; i < Like.size() && (movedDislike == false); i += 2) {
                    if (Like.get(i).equals(log.getUid())) {//checking if the stored uid is the same as the one of the logged user
                        ref.child(r.getPath()).child("likes").setValue(r.getLikes() - 1);//remove 1 like from firebase
                        r.setLikes(r.getLikes() - 1);//remove  the uid  from the local list
                        ref.child(r.getPath()).child("Like").child(Like.get(i + 1)).removeValue();//remove the field inside the Like field in firbase
                        Like.remove(i);
                        Like.remove(i);
                        likes.setText(String.valueOf(r.getLikes()));
                        exists = true;
                        like_clicked = false;

                    }
                }
                if (exists == false) {//in this scenario the list is not empty and the user didnt click like before nor dislike so we can add him to FB
                    ref.child(r.getPath()).child("likes").setValue(r.getLikes() + 1);
                    r.setLikes(r.getLikes() + 1);
                    npath = ref.push().getKey();
                    ref.child(r.getPath()).child("Like").child(npath).setValue(uPath);
                    Like.add(uPath);
                    Like.add(npath);
                    likes.setText(String.valueOf(r.getLikes()));

                }
            }

        });

        dislike.setOnClickListener(new View.OnClickListener() {
            boolean exists, movedLike;
            String npath;
            //In mirror with like listener
            @Override
            public void onClick(View v) {
                dislike_clicked = true;//we record that the like button is clicked
                movedLike = false;//we record that the dislike button is not pressed and there is no need to move values from dislike
                exists = false;//we record that the current user didnt like this review before
                 /*
                the for loop is for the moment the current user liked the review and then pressed the dislike button
                the  like counter is lowered by one and the uid is removed from Like field in firebase
                the dislike counter is increased by one and a uid is added to Dislike field
                 */
                for (int i = 0; i < Like.size() && like_clicked == true; i++)
                    if (Like.get(i).equals(log.getUid())) {//checking if the  uid from Dislike is the same as the logged user
                        ref.child(r.getPath()).child("likes").setValue(r.getLikes() - 1);//remove 1 like from review in firebase
                        r.setLikes(r.getLikes() - 1);//remove 1 like in current review
                        ref.child(r.getPath()).child("Like").child(Like.get(i + 1)).removeValue();//remove the field inside the Dislike field in firbase
                        Like.remove(i);//remove  the uid  from the local list
                        Like.remove(i);//remove the path from local list
                        likes.setText(String.valueOf(r.getLikes()));//update text view
                        ref.child(r.getPath()).child("dislikes").setValue(r.getDislikes() + 1);//
                        r.setDislikes(r.getDislikes() + 1);
                        npath = ref.push().getKey();
                        ref.child(r.getPath()).child("Dislike").child(npath).setValue(uPath);
                        dislikes.setText(String.valueOf(r.getDislikes()));
                        Dislike.add(uPath);
                        Dislike.add(npath);
                        movedLike = true;
                        exists = true;
                        dislike_clicked = true;
                        like_clicked = false;
                    }

                if (Dislike.size() == 0) {//if the list is empty we can  add  the like and update the firebase
                    ref.child(r.getPath()).child("dislikes").setValue(r.getDislikes() + 1);
                    r.setDislikes(r.getDislikes() + 1);
                    npath = ref.push().getKey();
                    ref.child(r.getPath()).child("Dislike").child(npath).setValue(uPath);
                    dislikes.setText(String.valueOf(r.getDislikes()));
                    Dislike.add(uPath);
                    Dislike.add(npath);
                    exists = true;
                    //if the list is not empty then we check the local list if the user clicked disliked before
                    //in this situation the user didnt click like instead he pressed dislike again
                } else for (int i = 0; i < Dislike.size() && movedLike == false; i += 2) {
                    if (Dislike.get(i).equals(log.getUid())) {
                        ref.child(r.getPath()).child("dislikes").setValue(r.getDislikes() - 1);
                        r.setDislikes(r.getDislikes() - 1);
                        ref.child(r.getPath()).child("Dislike").child(Dislike.get(i + 1)).removeValue();
                        Dislike.remove(i);
                        Dislike.remove(i);
                        dislikes.setText(String.valueOf(r.getDislikes()));
                        exists = true;
                        dislike_clicked = false;
                    }
                }
                if (exists == false) {//in this scenario the list is not empty and the user didnt click like before nor dislike so we can add him to FB
                    ref.child(r.getPath()).child("dislikes").setValue(r.getDislikes() + 1);
                    r.setDislikes(r.getDislikes() + 1);
                    npath = ref.push().getKey();
                    ref.child(r.getPath()).child("Dislike").child(npath).setValue(uPath);
                    Dislike.add(uPath);
                    Dislike.add(npath);
                    dislikes.setText(String.valueOf(r.getDislikes()));

                }

            }
//

        });


    }
}
