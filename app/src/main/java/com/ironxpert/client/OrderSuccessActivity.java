package com.ironxpert.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ironxpert.client.common.auth.Auth;
import com.ironxpert.client.common.db.Database;
import com.ironxpert.client.common.db.LaunderingService;
import com.ironxpert.client.utils.Promise;

import java.util.List;
import java.util.Objects;

public class OrderSuccessActivity extends AppCompatActivity {
    private AppCompatButton viewOrderDetailsBtn;
    private ImageButton closeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        viewOrderDetailsBtn = findViewById(R.id.view_order_details);
        closeBtn = findViewById(R.id.close);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Database.getInstance().collection("shop").document(LaunderingService.SHOP).get().addOnSuccessListener(documentSnapshot -> {
            List<String> adminUserList = (List<String>) documentSnapshot.get("admin");
            Auth.Notify.pushNotification(this, adminUserList.get(0), "New Order", "You have a new order. Just have a look and fulfil it", "admin", new Promise<String>() {
                @Override
                public void resolving(int progress, String msg) {}

                @Override
                public void resolved(String o) {}

                @Override
                public void reject(String err) {}
            });
        });

        Thread thread = new Thread(() -> FirebaseFirestore.getInstance().collection("user").document(Objects.requireNonNull(Auth.getAuthUserUid())).collection("cart").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot snapshot: queryDocumentSnapshots) {
                FirebaseFirestore.getInstance().collection("user").document(Auth.getAuthUserUid()).collection("cart").document(snapshot.getId()).delete();
            }
        }));
        thread.start();

        viewOrderDetailsBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra("ORDER", getIntent().getStringExtra("ORDER"));
            startActivity(intent);
            finish();
        });

        closeBtn.setOnClickListener(view -> finish());
    }
}