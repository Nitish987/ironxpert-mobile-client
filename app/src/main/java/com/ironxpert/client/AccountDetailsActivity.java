package com.ironxpert.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.ironxpert.client.common.auth.Auth;
import com.ironxpert.client.common.db.Database;
import com.ironxpert.client.models.User;
import com.ironxpert.client.utils.Validator;

import java.util.HashMap;
import java.util.Map;

public class AccountDetailsActivity extends AppCompatActivity {
    private LinearLayout sectionName, sectionEmail, sectionPhone;
    private EditText name_eTxt, email_eTxt, phone_eTxt;
    private AppCompatButton saveBtn;
    private CircularProgressIndicator saveProgress;

    private String name, email, phone;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        user = (User) getIntent().getSerializableExtra("USER");

        sectionName = findViewById(R.id.section_name);
        sectionEmail = findViewById(R.id.section_email);
        sectionPhone = findViewById(R.id.section_phone);
        name_eTxt = findViewById(R.id.name_e_txt);
        email_eTxt = findViewById(R.id.email_e_txt);
        phone_eTxt = findViewById(R.id.phone_e_txt);
        saveBtn = findViewById(R.id.save_btn);
        saveProgress = findViewById(R.id.save_progress);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user.getName() != null) {
            sectionName.setVisibility(View.GONE);
            name_eTxt.setText(user.getName());
        }
        if (user.getEmail() != null) {
            sectionEmail.setVisibility(View.GONE);
            email_eTxt.setText(user.getEmail());
        }
        if (user.getPhone() != null) {
            sectionPhone.setVisibility(View.GONE);
            phone_eTxt.setText(user.getPhone().substring(3));
        }

        saveBtn.setOnClickListener(view -> {
            name = name_eTxt.getText().toString();
            email = email_eTxt.getText().toString();
            phone = phone_eTxt.getText().toString();

            if (Validator.isEmpty(name)) {
                name_eTxt.setError("Name required.");
                return;
            }

            if (Validator.isEmpty(email)) {
                email_eTxt.setError("Email required.");
                return;
            }

            if (!Validator.isEmail(email)) {
                email_eTxt.setError("Invalid Email.");
                return;
            }

            if (Validator.isEmpty(phone)) {
                phone_eTxt.setError("Phone required.");
                return;
            }

            if (phone.startsWith("+91") || phone.length() != 10) {
                phone_eTxt.setError("Invalid phone.");
                return;
            }

            saveBtn.setVisibility(View.INVISIBLE);
            saveProgress.setVisibility(View.VISIBLE);

            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("email", email);
            map.put("phone", "+91" + phone);

            Database.getInstance().collection("user").document(user.getUid()).update(map).addOnSuccessListener(documentSnapshot -> {
                FirebaseUser user = Auth.getInstance().getCurrentUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();

                user.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveBtn.setVisibility(View.VISIBLE);
                        saveProgress.setVisibility(View.INVISIBLE);

                        Toast.makeText(this, "Contact Details Saved.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        failure();
                    }
                }).addOnFailureListener(e -> failure());
            }).addOnFailureListener(e -> failure());
        });
    }

    public void failure() {
        saveBtn.setVisibility(View.VISIBLE);
        saveProgress.setVisibility(View.INVISIBLE);

        Toast.makeText(this, "Unable to save Contact Details.", Toast.LENGTH_SHORT).show();
    }
}