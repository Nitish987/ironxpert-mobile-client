package com.ironxpert.client.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ironxpert.client.R;
import com.ironxpert.client.ServiceItemActivity;
import com.ironxpert.client.models.Service;

import java.util.List;

public class ServiceRecyclerAdapter extends RecyclerView.Adapter<ServiceRecyclerAdapter.ServiceHolder> {
    private final List<Service> serviceList;

    public ServiceRecyclerAdapter(List<Service> serviceList) {
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ServiceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ServiceHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.setPhoto(service.getDrawableId());
        holder.setName(service.getName());
        holder.setDescription(service.getDesc());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ServiceItemActivity.class);
            intent.putExtra("SERVICE_INDEX", service.getIndex());
            intent.putExtra("SERVICE_NAME", service.getName());
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ServiceHolder extends RecyclerView.ViewHolder {
        private final ImageView photo;
        private final TextView name, description;

        public ServiceHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.service_photo);
            name = itemView.findViewById(R.id.service_name);
            description = itemView.findViewById(R.id.service_description);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public void setPhoto(int drawableId) {
            Glide.with(itemView.getContext()).load(itemView.getContext().getDrawable(drawableId)).into(photo);
        }

        public void setName(String name) {
            this.name.setText(name);
        }

        public void setDescription(String description) {
            this.description.setText(description);
        }
    }
}
