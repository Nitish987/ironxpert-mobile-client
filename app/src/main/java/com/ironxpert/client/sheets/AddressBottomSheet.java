package com.ironxpert.client.sheets;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ironxpert.client.R;
import com.ironxpert.client.adapters.AddressRecyclerAdapter;
import com.ironxpert.client.common.auth.Auth;
import com.ironxpert.client.models.Address;
import com.ironxpert.client.utils.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddressBottomSheet extends BottomSheetDialogFragment {
    private View view;
    private EditText address;
    private AppCompatButton saveAddressBtn;
    private RecyclerView addressRV;
    private TextView savedAddressI;

    private String myAddress = null;

    public static AddressBottomSheet newInstance(String myAddress) {
        AddressBottomSheet addressBottomSheet = new AddressBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putString("ADDRESS", myAddress);
        addressBottomSheet.setArguments(bundle);
        return addressBottomSheet;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            myAddress = getArguments().getString("ADDRESS");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_address, container, false);

        address = view.findViewById(R.id.address);
        saveAddressBtn = view.findViewById(R.id.save_address);
        savedAddressI = view.findViewById(R.id.saved_address_i);

        addressRV = view.findViewById(R.id.address_rv);
        addressRV.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, true));
        addressRV.setHasFixedSize(true);

        if (!myAddress.equals(getString(R.string.no_address))) address.setText(myAddress);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = FirebaseFirestore.getInstance().collection("user").document(Objects.requireNonNull(Auth.getAuthUserUid())).collection("addresses");
        FirestoreRecyclerOptions<Address> options = new FirestoreRecyclerOptions.Builder<Address>().setQuery(query, Address.class).build();
        AddressRecyclerAdapter adapter = new AddressRecyclerAdapter(options, savedAddressI, o -> dismiss());
        addressRV.setAdapter(adapter);
        adapter.startListening();


        saveAddressBtn.setOnClickListener(view1 -> {
            String address_ = address.getText().toString();
            if (Validator.isEmpty(address_)) {
                address.setError("Address required.");
                return;
            }

            Map<String, String> map = new HashMap<>();
            map.put("address", address_);
            FirebaseFirestore.getInstance().collection("user").document(Objects.requireNonNull(Auth.getAuthUserUid())).collection("current").document("address").set(map).addOnFailureListener(e -> {
                Toast.makeText(view.getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
            }).addOnSuccessListener(unused -> {
                FirebaseFirestore.getInstance().collection("user").document(Auth.getAuthUserUid()).collection("addresses").document().set(map).addOnFailureListener(e1 -> {
                    Toast.makeText(view.getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }).addOnSuccessListener(unused1 -> dismiss());
            });
        });
    }
}