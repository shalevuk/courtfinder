package com.example.basketballcourts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CourtAdapter extends RecyclerView.Adapter<CourtAdapter.MyViewHolder> {
    ArrayList<Courts> courts;
    Context context;

    public CourtAdapter(Context context, ArrayList<Courts> allCourts) {
        this.context = context;
        this.courts = allCourts;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.court_sample, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Courts court = courts.get(position);
        holder.courtName.setText(court.getName());
        holder.courtCity.setText(court.getCity());
        holder.mail.setText(court.getEmail());

        holder.mapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address = court.getCity()+" "+court.getName()+" "+court.getStreet()+" "+court.getHousenum();
                Intent intent = new Intent(context, MapsActivity.class);
                intent.putExtra("address", address);
                context.startActivity(intent);
            }
        });

        holder.phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = court.getPhone(); // assuming there is a getPhone() method in Courts class
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView courtName;
        public TextView courtCity;
        public TextView mail;
        ImageButton mapsBtn;
        ImageButton phoneBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            courtName = itemView.findViewById(R.id.court_name_tv);
            courtCity = itemView.findViewById(R.id.court_city_tv);
            mail = itemView.findViewById(R.id.court_mail);
            mapsBtn = itemView.findViewById(R.id.court_maps_btn);
            phoneBtn = itemView.findViewById(R.id.court_phone_btn);
        }
    }
}
