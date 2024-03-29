package com.ironxpert.client.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ironxpert.client.R;
import com.ironxpert.client.common.auth.Auth;
import com.ironxpert.client.models.Address;
import com.ironxpert.client.utils.Pass;

import java.util.Objects;

public class AddressRecyclerAdapter extends FirestoreRecyclerAdapter<Address, AddressRecyclerAdapter.AddressHolder> {
    private final TextView savedAddressI;
    private final Pass pass;

    public AddressRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Address> options, TextView savedAddressI, Pass pass) {
        super(options);
        this.savedAddressI = savedAddressI;
        this.pass = pass;
    }

    @Override
    protected void onBindViewHolder(@NonNull AddressHolder holder, int position, @NonNull Address model) {
        savedAddressI.setVisibility(View.GONE);
        holder.setAddressDetails(model);

        holder.itemView.setOnClickListener(view -> {
            FirebaseFirestore.getInstance().collection("user").document(Objects.requireNonNull(Auth.getAuthUserUid())).collection("current").document("address").set(model).addOnSuccessListener(pass::on);
        });
    }

    @NonNull
    @Override
    public AddressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddressHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false));
    }

    public static class AddressHolder extends RecyclerView.ViewHolder {
        private final TextView address;

        public AddressHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
        }

        public void setAddressDetails(Address model) {
            address.setText(model.getAddress());
        }
    }
}
