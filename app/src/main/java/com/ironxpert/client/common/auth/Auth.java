package com.ironxpert.client.common.auth;

import android.content.Context;

import com.android.volley.Request;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ironxpert.client.common.db.Database;
import com.ironxpert.client.common.security.AES128;
import com.ironxpert.client.common.settings.ApiKey;
import com.google.firebase.auth.FirebaseAuth;
import com.ironxpert.client.models.User;
import com.ironxpert.client.utils.Promise;
import com.ironxpert.client.utils.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Auth {
    public static String AUTH_TOKEN = "";
    public static String ENCRYPTION_KEY = "";
    public static String PAYMENT_KEY = "";

    public static boolean isUserAuthenticated(Context context) {
        AuthPreferences preferences = new AuthPreferences(context);
        AUTH_TOKEN = preferences.getAuthToken();
        ENCRYPTION_KEY = preferences.getEncryptionKey();
        PAYMENT_KEY = preferences.getPaymentKey();
        return AUTH_TOKEN != null && FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static String getAuthUserUid() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return null;
    }

    public static String getAuthUserEmail() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }
        return null;
    }

    public static String getAuthUserName() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        }
        return null;
    }

    public static FirebaseAuth getInstance() {
        return FirebaseAuth.getInstance();
    }

    public static FirebaseMessaging getMessaging() {
        return FirebaseMessaging.getInstance();
    }

    public static class Signup {
        public static void signup(FirebaseUser user, Promise<Object> promise) {
            promise.resolving(0, null);
            Database.getInstance().collection("user").document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    promise.resolved(null);
                } else {
                    User newUser = new User(
                            user.getEmail(),
                            null,
                            user.getDisplayName(),
                            user.getPhoneNumber(),
                            null,
                            user.getUid()
                    );
                    Database.getInstance().collection("user").document(user.getUid()).set(newUser).addOnSuccessListener(unused -> promise.resolved(null)).addOnFailureListener(e -> promise.reject(null));
                }
            }).addOnFailureListener(e -> promise.reject(null));
        }
    }

    public static class Login {
        public static void login(Context context, FirebaseUser user, Promise<Object> promise) {
            promise.resolving(0, null);
            Database.getInstance().collection("user").document(user.getUid()).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Database.getInstance().collection("app").document("account").get().addOnSuccessListener(snap -> {
                        String encKey = AES128.decrypt(AES128.NATIVE_ENCRYPTION_KEY, snap.get("sharedEncKey", String.class));
                        String authToken = AES128.decrypt(AES128.NATIVE_ENCRYPTION_KEY, snap.get("serverToken", String.class));
                        String payKey = AES128.decrypt(AES128.NATIVE_ENCRYPTION_KEY, snap.get("paymentKey", String.class));

                        AuthPreferences preferences = new AuthPreferences(context);
                        preferences.setAuthToken(authToken);
                        preferences.setEncryptionKey(encKey);
                        preferences.setPaymentKey(payKey);

                        promise.resolved(null);
                    }).addOnFailureListener(e -> promise.reject(null));
                } else {
                    promise.reject(null);
                }
            }).addOnFailureListener(e -> promise.reject(null));
        }

        public static void updateMessageToken(String uid, String token) {
            Map<String, Object> map = new HashMap<>();
            map.put("msgToken", token);
            Database.getInstance().collection("user").document(uid).update(map);
        }
    }

    public static class Notify {
        public static void pushNotification(Context context, String to, String title, String body, Promise<String> promise) {
            Map<String, String> headers = new HashMap<>();
            headers.put("RAK", ApiKey.REQUEST_API_KEY);
            headers.put("AT", Auth.AUTH_TOKEN);
            headers.put("UID", Auth.getAuthUserUid());

            JSONObject notification = new JSONObject();
            try {
                notification.put("uid", to);
                notification.put("title", title);
                notification.put("body", body);
            } catch (JSONException e) {
                promise.reject("unable to Login.");
                e.printStackTrace();
                return;
            }

            Server.request(context, Request.Method.POST, ApiKey.REQUEST_API_URL + "account/push-notification/", headers, notification, new Promise<JSONObject>() {
                        @Override
                        public void resolving(int progress, String msg) {
                            promise.resolving(progress, msg);
                        }

                        @Override
                        public void resolved(JSONObject data) {
                            try {
                                promise.resolved(data.getString("message"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                promise.reject("Something went wrong.");
                            }
                        }

                        @Override
                        public void reject(String err) {
                            promise.reject(err);
                        }
                    }
            );
        }
    }
}
