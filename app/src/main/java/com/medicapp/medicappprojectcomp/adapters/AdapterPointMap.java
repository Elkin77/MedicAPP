package com.medicapp.medicappprojectcomp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.medicapp.medicappprojectcomp.R;
import com.medicapp.medicappprojectcomp.models.PositionMap;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdapterPointMap  extends  RecyclerView.Adapter<AdapterPointMap.ViewHolder> {
    private OnItemClickListener listener;
    private List<PositionMap> dataSet;
    Context mContext;

    int resourcesId;

    public AdapterPointMap(List<PositionMap> data) {
        this.dataSet = data;
    }

    public void setList(List<PositionMap> data) {
        this.dataSet = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_point_map, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.textTitle.setText(dataSet.get(position).getTitle());
        holder.textAddress.setText(dataSet.get(position).getAddress());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(position);
            }
        });
    }
    public PositionMap getItem(int position){
        return dataSet.get(position);
    }
    @Override
    public int getItemCount() {
        if(dataSet!=null) {
            return dataSet.size();
        }else{
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
         TextView textTitle;
         TextView textAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textNamePoint);
            textAddress = itemView.findViewById(R.id.textAddressPoint);
        }



    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener mListener) {
        listener = mListener;
    }


}
