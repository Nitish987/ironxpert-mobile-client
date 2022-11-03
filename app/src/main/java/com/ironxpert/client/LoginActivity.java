package com.ironxpert.client;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.ironxpert.client.common.auth.Auth;
import com.ironxpert.client.utils.Promise;
import com.ironxpert.client.utils.Validator;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private AppCompatButton loginBtn;
    private TextView termsPrivacy;
    private EditText phone_eTxt;
    private CircularProgressIndicator loginProgress;
    private SignInButton googleSignBtn;

    private FirebaseAuth auth;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleResultIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Auth.isUserAuthenticated(this)) {
            toMainActivity();
        }

        phone_eTxt = findViewById(R.id.phone_e_txt);
        loginBtn = findViewById(R.id.login_btn);
        loginProgress = findViewById(R.id.login_progress);
        googleSignBtn = findViewById(R.id.google_sign_in_btn);
        termsPrivacy = findViewById(R.id.terms_privacy);

        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        phone_eTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (phone_eTxt.getText().toString().equals("")) {
                    googleSignBtn.setVisibility(View.VISIBLE);
                } else {
                    googleSignBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        loginBtn.setOnClickListener(view -> {
            String phone = phone_eTxt.getText().toString();

            if (Validator.isEmpty(phone)) {
                phone_eTxt.setError("Phone required.");
                return;
            }

            if ((phone.startsWith("+91") || phone.startsWith("91")) && phone.length() != 10) {
                phone_eTxt.setError("Invalid phone.");
                return;
            }

            Intent intent = new Intent(this, LoginVerificationActivity.class);
            intent.putExtra("PHONE", "+91" + phone);
            startActivity(intent);

        });

        googleResultIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent intent = result.getData();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Signup failed!", Toast.LENGTH_LONG).show();
            }
        });

        googleSignBtn.setOnClickListener(v -> googleSignIn());

        termsPrivacy.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://rotiking.co.in/privacy/"));
            startActivity(i);
        });
    }

    private void googleSignIn() {
        loginBtn.setVisibility(View.INVISIBLE);
        googleSignBtn.setVisibility(View.INVISIBLE);
        loginProgress.setVisibility(View.VISIBLE);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleResultIntent.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = task.getResult().getUser();
                Auth.Signup.signup(Objects.requireNonNull(user), new Promise<Object>() {
                    @Override
                    public void resolving(int progress, String msg) {
                        loginProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void resolved(Object o) {
                        Auth.Login.login(getApplicationContext(), user, new Promise<Object>() {
                            @Override
                            public void resolving(int progress, String msg) {
                                loginProgress.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void resolved(Object o) {
                                toMainActivity();
                            }

                            @Override
                            public void reject(String err) {
                                Toast.makeText(LoginActivity.this, "sign in failed!", Toast.LENGTH_SHORT).show();
                                auth.signOut();

                                loginBtn.setVisibility(View.VISIBLE);
                                googleSignBtn.setVisibility(View.VISIBLE);
                                loginProgress.setVisibility(View.INVISIBLE);
                            }
                        });
                    }

                    @Override
                    public void reject(String err) {
                        Toast.makeText(LoginActivity.this, "sign in failed!", Toast.LENGTH_SHORT).show();
                        auth.signOut();
                    }
                });
            } else {
                Toast.makeText(this, "Sign in failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Auth.isUserAuthenticated(this)) {
            Auth.getMessaging().getToken().addOnSuccessListener(s -> Auth.Login.updateMessageToken(Auth.getAuthUserUid(), s));
        }
    }

    private void toMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}