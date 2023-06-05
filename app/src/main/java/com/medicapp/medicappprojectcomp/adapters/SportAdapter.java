package com.medicapp.medicappprojectcomp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.medicapp.medicappprojectcomp.R;

import java.util.ArrayList;
import java.util.List;

public class SportAdapter extends RecyclerView.Adapter<SportAdapter.ViewHolder>{


    private List<Sport> sportList;
    private final Context context;
    RecyclerView recyclerView;
    SportAdapter sportAdapter;

    public SportAdapter(List<Sport> sportList, Context context) {
        this.sportList = sportList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sport_view, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        String url = sportList.get(pos).getSportPng();
        String txt_name_sport = sportList.get(pos).getName().toString();
        String txt_Level = sportList.get(pos).getLevel().toString();
        String txt_routine = sportList.get(pos).getRoutine().toString();
        String txt_routine_days = sportList.get(pos).getDays().toString();

        holder.txt_name_sport.setText(txt_name_sport);
        holder.txt_Level.setText(txt_Level);
        holder.txt_routine.setText(txt_routine);
        holder.txt_routine_days.setText(txt_routine_days);

        Glide.with(context)
                .load(url)
                .centerCrop()
                .into(holder.imageSport);

    }
    @Override
    public int getItemCount() {
        return sportList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageSport  ;
        private TextView txt_name_sport;
        private TextView txt_Level;
        private TextView txt_routine;
        private TextView txt_routine_days;


        private FloatingActionButton btnCall;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageSport = itemView.findViewById(R.id.image_sport);
            txt_name_sport = itemView.findViewById(R.id.txt_name_sport);
            txt_Level = itemView.findViewById(R.id.txt_Level);
            txt_routine = itemView.findViewById(R.id.txt_routine);
            txt_routine_days = itemView.findViewById(R.id.txt_routine_days);


        }
    }
}
