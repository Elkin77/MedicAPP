package com.medicapp.medicappprojectcomp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.medicapp.medicappprojectcomp.R;
import com.medicapp.medicappprojectcomp.models.News;
import com.medicapp.medicappprojectcomp.models.PositionMap;

import java.util.List;

public class AdapterNews  extends  RecyclerView.Adapter<AdapterNews.ViewHolder> {

    private List<News> dataSet;
    Context mContext;

    int resourcesId;

    public AdapterNews(List<News> data) {
        this.dataSet = data;
    }

    public void setList(List<News> data) {
        this.dataSet = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_adapter_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textName.setText(dataSet.get(position).getName());
        holder.textDescription.setText(dataSet.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textNameNews);
            textDescription = itemView.findViewById(R.id.textDescriptionNews);
        }
    }

}