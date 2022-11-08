package com.ironxpert.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.ironxpert.client.adapters.ServiceItemRecyclerAdapter;
import com.ironxpert.client.common.db.Database;
import com.ironxpert.client.common.db.LaunderingService;
import com.ironxpert.client.models.ServiceItem;

public class ServiceItemActivity extends AppCompatActivity {
    private ImageButton ibClose;
    private RecyclerView rvItems;
    private LinearLayout noItemI;

    private int serviceIndex;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_item);

        serviceIndex = getIntent().getIntExtra("SERVICE_INDEX", 0);
        String serviceName = getIntent().getStringExtra("SERVICE_NAME");

        ibClose = findViewById(R.id.close);
        noItemI = findViewById(R.id.no_item_i);
        TextView tServiceName = findViewById(R.id.service_name);
        tServiceName.setText(serviceName);

        rvItems = findViewById(R.id.items_rv);
        rvItems.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvItems.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = Database.getInstance().collection("shop").document(LaunderingService.SHOP).collection("items").whereEqualTo("service", serviceIndex);
        FirestoreRecyclerOptions<ServiceItem> options = new FirestoreRecyclerOptions.Builder<ServiceItem>().setQuery(query, ServiceItem.class).build();
        ServiceItemRecyclerAdapter adapter = new ServiceItemRecyclerAdapter(options, getSupportFragmentManager(), noItemI);
        rvItems.setAdapter(adapter);
        adapter.startListening();

        ibClose.setOnClickListener(view -> finish());
    }
}