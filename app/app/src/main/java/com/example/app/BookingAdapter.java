package com.example.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//The booking adapter  puts the list of bookings inside the recycle view
public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.Bookingholder> {
    public BookingAdapter(ArrayList<Booking> list) {
        this.list = list;
    }

    ArrayList<Booking> list;
    EateryAdapter.holder.OnCardClickedListener listener;

    @NonNull
    @Override
    public Bookingholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        //we select the look of the card in which the booking is displayed
        Bookingholder h = new Bookingholder(v);
        return h;
    }

    public void onBindViewHolder(@NonNull Bookingholder hold, int position) {
        hold.tv.setText(list.get(position).getEatery());//we put the name of the eatery we booked
        String Hour = (int) list.get(position).getHour() + ":";
        if ((int) list.get(position).getHour() == list.get(position).getHour())
            Hour += "00";
        else {
            Hour += Math.round((((list.get(position).getHour() - (int) list.get(position).getHour())) * 100));//Format the date into the hour of booking
        }

        hold.tv2.setText(list.get(position).getEatery() +
                ":" + list.get(position).getDay() + "/" + list.get(position).getMonth() +
                "/" + list.get(position).getYear() + " set at " + Hour + " hours");
        hold.iv.setImageResource(R.drawable.menu);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class Bookingholder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tv, tv2;

        EateryAdapter.holder.OnCardClickedListener listener;

        public Bookingholder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.iv_card);
            tv = itemView.findViewById(R.id.tv_card);
            tv2 = itemView.findViewById(R.id.tv_card2);

        }

    }


}
