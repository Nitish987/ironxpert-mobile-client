package com.ironxpert.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ironxpert.client.common.auth.Auth;
import com.ironxpert.client.common.db.Database;
import com.ironxpert.client.common.db.LaunderingService;
import com.ironxpert.client.utils.Promise;
import com.ironxpert.client.utils.Validator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpSupportActivity extends AppCompatActivity {
    private EditText email, message;
    private Spinner type;
    private AppCompatButton send;
    private ImageButton close;
    private TextView termsPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);

        email = findViewById(R.id.email);
        type = findViewById(R.id.help_type);
        message = findViewById(R.id.message);
        send = findViewById(R.id.send);
        close = findViewById(R.id.close);
        termsPrivacy = findViewById(R.id.terms_privacy);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Auth.isUserAuthenticated(this)) {
            email.setText(Auth.getAuthUserEmail());
            email.setEnabled(false);
        }

        send.setOnClickListener(view -> {
            String email = this.email.getText().toString();
            String message = this.message.getText().toString();

            if (!Validator.isEmail(email)) {
                String e_ = "Invalid Email";
                this.email.setError(e_);
                return;
            }

            if (Validator.isEmpty(message)) {
                String e_ = "Field Required.";
                this.message.setError(e_);
                return;
            }

            DocumentReference ref = FirebaseFirestore.getInstance().collection("help").document();

            Map<String, Object> map = new HashMap<>();
            map.put("email", email);
            map.put("type", type.getSelectedItem().toString());
            map.put("message", message);
            map.put("time", System.currentTimeMillis());
            map.put("by", Auth.isUserAuthenticated(this)? Auth.getAuthUserUid() : "");
            map.put("help_id", ref.getId());
            map.put("read", false);

            ref.set(map).addOnSuccessListener(unused -> {
                Toast.makeText(this, "Message sent Successfully.", Toast.LENGTH_SHORT).show();
                Database.getInstance().collection("shop").document(LaunderingService.SHOP).get().addOnSuccessListener(documentSnapshot -> {
                    List<String> adminUserList = (List<String>) documentSnapshot.get("admin");
                    Auth.Notify.pushNotification(this, adminUserList.get(0), "Customer Query", "You Customer need some help. Just have a look and fulfil it.", "admin", new Promise<String>() {
                        @Override
                        public void resolving(int progress, String msg) {}

                        @Override
                        public void resolved(String o) {
                            finish();
                        }

                        @Override
                        public void reject(String err) {
                            finish();
                        }
                    });
                }).addOnFailureListener(e -> finish());
            }).addOnFailureListener(e -> Toast.makeText(this, "Unable to send Message.", Toast.LENGTH_SHORT).show());
        });

        termsPrivacy.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://rotiking.co.in/privacy/"));
            startActivity(i);
        });

        close.setOnClickListener(view -> finish());
    }
}