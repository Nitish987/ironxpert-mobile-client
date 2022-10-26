package com.ironxpert.client;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ironxpert.client.common.auth.Auth;
import com.ironxpert.client.tabs.HomeFragment;
import com.ironxpert.client.tabs.ProfileFragment;
import com.ironxpert.client.tabs.OrdersFragment;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView tabs;
    private ImageButton cartBtn;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Auth.isUserAuthenticated(this)) {
            toLoginActivity();
        }

        tabs = findViewById(R.id.tabs);
        cartBtn = findViewById(R.id.cart);
        title = findViewById(R.id.title);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onStart() {
        super.onStart();
        tabs.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    title.setText(getString(R.string.welcome_back));
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                    break;
                case R.id.orders:
                    title.setText(getString(R.string.orders));
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrdersFragment()).commit();
                    break;
                case R.id.profile:
                    title.setText(getString(R.string.profile));
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                    break;
            }
            return true;
        });

        cartBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
        });
    }

    private void toLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}