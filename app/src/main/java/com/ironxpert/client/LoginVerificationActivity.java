package com.ironxpert.client;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.ironxpert.client.common.auth.Auth;
import com.ironxpert.client.common.db.Database;
import com.ironxpert.client.models.User;
import com.ironxpert.client.utils.Promise;
import com.ironxpert.client.utils.Validator;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginVerificationActivity extends AppCompatActivity {
    private EditText otp_eTxt;
    private TextView resendOtp, otpSubheading;
    private AppCompatButton verifyBtn;
    private CircularProgressIndicator verifyProgress;

    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private String phone, mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_verification);

        if (Auth.isUserAuthenticated(this)) {
            toMainActivity();
        }

        phone = getIntent().getStringExtra("PHONE");
        auth = Auth.getInstance();

        otp_eTxt = findViewById(R.id.otp_e_txt);
        resendOtp = findViewById(R.id.resent_otp);
        verifyBtn = findViewById(R.id.verify_btn);
        verifyProgress = findViewById(R.id.verify_progress);
        otpSubheading = findViewById(R.id.otp_subheading);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(LoginVerificationActivity.this, "Verification Failed.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };

        startPhoneNumberVerification(phone);
    }

    @Override
    protected void onStart() {
        super.onStart();
        String subheading = "Enter the otp sent to your number " + phone;
        otpSubheading.setText(subheading);

        otp_eTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 6 || charSequence.length() < 6) {
                    otp_eTxt.setError("Invalid OTP");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        resendOtp.setOnClickListener(view -> resendVerificationCode(phone, mResendToken));

        verifyBtn.setOnClickListener(view -> {
            String otp = otp_eTxt.getText().toString();

            if (Validator.isEmpty(otp)) {
                otp_eTxt.setError("Required Field");
                return;
            }

            if (!Validator.isEqualLength(otp, 6)) {
                otp_eTxt.setError("Invalid OTP.");
                return;
            }

            verifyProgress.setVisibility(View.VISIBLE);
            verifyBtn.setVisibility(View.GONE);

            verifyPhoneNumberWithCode(mVerificationId, otp);
        });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(Auth.getInstance())
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .setForceResendingToken(token)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = task.getResult().getUser();
                Auth.Signup.signup(Objects.requireNonNull(user), new Promise<Object>() {
                    @Override
                    public void resolving(int progress, String msg) {
                        verifyProgress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void resolved(Object o) {
                        Auth.Login.login(getApplicationContext(), user, new Promise<Object>() {
                            @Override
                            public void resolving(int progress, String msg) {
                                verifyProgress.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void resolved(Object o) {
                                Database.getInstance().collection("user").document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {
                                    User u = documentSnapshot.toObject(User.class);
                                    if (u.getName() == null || u.getEmail() == null || u.getPhone() == null) {
                                        Intent intent = new Intent(getApplicationContext(), AccountDetailsActivity.class);
                                        intent.putExtra("USER", u);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        toMainActivity();
                                    }
                                });
                            }

                            @Override
                            public void reject(String err) {
                                Toast.makeText(LoginVerificationActivity.this, "sign in failed!", Toast.LENGTH_SHORT).show();
                                auth.signOut();

                                verifyBtn.setVisibility(View.VISIBLE);
                                verifyProgress.setVisibility(View.INVISIBLE);
                            }
                        });
                    }

                    @Override
                    public void reject(String err) {
                        Toast.makeText(LoginVerificationActivity.this, "sign in failed!", Toast.LENGTH_SHORT).show();
                        auth.signOut();
                    }
                });
            } else {
                Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
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