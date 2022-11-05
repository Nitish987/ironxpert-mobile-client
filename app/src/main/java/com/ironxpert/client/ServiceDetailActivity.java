package com.ironxpert.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.ironxpert.client.models.Service;

public class ServiceDetailActivity extends AppCompatActivity {
    private TextView serviceName;
    private RecyclerView serviceItemRV;

    private Service service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        service = (Service) getIntent().getSerializableExtra("SERVICE");

        serviceName = findViewById(R.id.service_name);
        serviceItemRV = findViewById(R.id.service_item_rv);
        serviceItemRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        serviceItemRV.setHasFixedSize(true);
    }
}