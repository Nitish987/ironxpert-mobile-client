package com.ironxpert.client.sheets;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.DocumentReference;
import com.ironxpert.client.CartActivity;
import com.ironxpert.client.R;
import com.ironxpert.client.common.auth.Auth;
import com.ironxpert.client.common.db.Database;
import com.ironxpert.client.models.CartItem;
import com.ironxpert.client.models.ServiceItem;

import java.util.Objects;

public class ServiceItemDetailBottomSheet extends BottomSheetDialogFragment {
    private View view;
    private TextView name, category, crossPrice, price, discount, available, quantityTxt, payableTxt;
    private CircularProgressIndicator addToCartProgress;
    private AppCompatImageButton incQuantityBtn, decQuantityBtn;
    private AppCompatButton addToCartBtn, openCartBtn;

    private ServiceItem item;
    private int one_piece_price = 0;
    private int one_piece_price_before_discount = 0;
    private int quantity = 1;

    public static ServiceItemDetailBottomSheet newInstance(ServiceItem item) {
        ServiceItemDetailBottomSheet fragment = new ServiceItemDetailBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable("ITEM", item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            item = (ServiceItem) getArguments().getSerializable("ITEM");

            one_piece_price_before_discount = item.getPrice();
            one_piece_price = item.getPrice();
            if (item.getDiscount() != 0) {
                one_piece_price = one_piece_price - Math.round((float) (item.getDiscount() * one_piece_price) / 100);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_service_item_detail, container, false);

        name = view.findViewById(R.id.item_name);
        category = view.findViewById(R.id.item_category);
        price = view.findViewById(R.id.price);
        crossPrice = view.findViewById(R.id.cross_price);
        discount = view.findViewById(R.id.item_discount);
        available = view.findViewById(R.id.item_available);
        quantityTxt = view.findViewById(R.id.quantity);
        payableTxt = view.findViewById(R.id.payable_price);
        incQuantityBtn = view.findViewById(R.id.inc_quantity);
        decQuantityBtn = view.findViewById(R.id.dec_quantity);
        addToCartBtn = view.findViewById(R.id.add_to_cart_btn);
        openCartBtn = view.findViewById(R.id.open_cart_btn);
        addToCartProgress = view.findViewById(R.id.add_to_cart_progress);

        crossPrice.setPaintFlags(crossPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        name.setText(item.getName());
        category.setText(item.getCategory());
        available.setText(item.isAvailable() ? "Service Available" : "No Service Available right now. Come back Later");

        if (!item.isAvailable()) {
            addToCartBtn.setVisibility(View.INVISIBLE);
            addToCartBtn.setEnabled(false);
        }

        if (item.getDiscount() == 0) {
            discount.setVisibility(View.GONE);
            crossPrice.setVisibility(View.GONE);
        } else {
            String dis_ = "-" + item.getDiscount() + "% OFF";
            discount.setText(dis_);

            setCrossPrice();
        }

        setPrice();
        setPayablePrice();

        incQuantityBtn.setOnClickListener(view1 -> {
            quantity += 1;
            setQuantity();
            setCrossPrice();
            setPayablePrice();
        });

        decQuantityBtn.setOnClickListener(view1 -> {
            quantity -= 1;
            if (quantity < 1) quantity = 1;
            setQuantity();
            setCrossPrice();
            setPayablePrice();
        });

        addToCartBtn.setOnClickListener(view1 -> {
            addToCartBtn.setVisibility(View.INVISIBLE);
            addToCartProgress.setVisibility(View.VISIBLE);

            DocumentReference reference = Database.getInstance().collection("user").document(Objects.requireNonNull(Auth.getAuthUserUid())).collection("cart").document();
            CartItem cartItem = new CartItem(
                    reference.getId(),
                    quantity,
                    item,
                    item.getId(),
                    (quantity * one_piece_price)
            );
            reference.set(cartItem).addOnSuccessListener(unused -> {
                addToCartBtn.setVisibility(View.VISIBLE);
                addToCartProgress.setVisibility(View.GONE);
                Toast.makeText(view.getContext(), "Item added.", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                addToCartBtn.setVisibility(View.VISIBLE);
                addToCartProgress.setVisibility(View.GONE);
                Toast.makeText(view.getContext(), "unable to add.", Toast.LENGTH_SHORT).show();
            });
        });

        openCartBtn.setOnClickListener(view1 -> {
            Intent intent = new Intent(view1.getContext(), CartActivity.class);
            startActivity(intent);
            dismiss();
        });
    }

    private void setQuantity() {
        String qua_ = Integer.toString(quantity);
        quantityTxt.setText(qua_);
    }

    private void setCrossPrice() {
        String cross_pri_ = "\u20B9 " + one_piece_price_before_discount;
        crossPrice.setText(cross_pri_);
    }

    private void setPrice() {
        String pri_ = "\u20B9 " + one_piece_price;
        price.setText(pri_);
    }

    private void setPayablePrice() {
        String pri_ = "\u20B9 " + (quantity * one_piece_price);
        payableTxt.setText(pri_);
    }
}