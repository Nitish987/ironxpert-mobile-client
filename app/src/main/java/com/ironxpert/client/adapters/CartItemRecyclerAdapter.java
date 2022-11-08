package com.ironxpert.client.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.ironxpert.client.R;
import com.ironxpert.client.models.CartItem;
import com.ironxpert.client.models.ServiceItem;
import com.ironxpert.client.utils.Pass;

import java.util.HashSet;
import java.util.List;

public class CartItemRecyclerAdapter extends RecyclerView.Adapter<CartItemRecyclerAdapter.CartItemHolder> {
    private final List<CartItem> cartItems;
    private int totalCartPrice;
    private final Pass pass;
    public static HashSet<String> REMOVED_CART_ITEM;

    public CartItemRecyclerAdapter(List<CartItem> cartItems, int totalCartPrice, Pass pass) {
        this.cartItems = cartItems;
        this.totalCartPrice = totalCartPrice;
        this.pass = pass;
        REMOVED_CART_ITEM = new HashSet<>();
    }

    @NonNull
    @Override
    public CartItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemHolder holder, int position) {
        CartItem item = cartItems.get(position);
        String itemId = item.getId();

        ServiceItem serviceItem = item.getServiceItem();
        holder.setName(serviceItem.getName());
        holder.setCategory(serviceItem.getCategory());
        holder.setDiscount(serviceItem.getDiscount());
        holder.setQuantity(item.getQuantity());

        int price = item.getTotalPrice();
        holder.setPrice(price);

        if (REMOVED_CART_ITEM.contains(itemId)) {
            holder.cartItemLayout.setBackgroundColor(holder.itemView.getContext().getColor(R.color.red_transparent));
            holder.removeCartItemBtn.setImageResource(R.drawable.ic_baseline_check_24);
        } else {
            holder.cartItemLayout.setBackgroundColor(holder.itemView.getContext().getColor(R.color.transparent));
            holder.removeCartItemBtn.setImageResource(R.drawable.ic_baseline_close_24);
        }

        holder.removeCartItemBtn.setOnClickListener(view -> {
            if (REMOVED_CART_ITEM.contains(itemId)) {
                holder.removeCartItemBtn.setImageResource(R.drawable.ic_baseline_check_24);
                REMOVED_CART_ITEM.remove(itemId);
                totalCartPrice += price;
            } else {
                holder.removeCartItemBtn.setImageResource(R.drawable.ic_baseline_close_24);
                REMOVED_CART_ITEM.add(itemId);
                totalCartPrice -= price;
            }
            pass.on(totalCartPrice);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartItemHolder extends RecyclerView.ViewHolder {
        private final TextView name, category, price , discount, quantity;
        private final AppCompatImageButton removeCartItemBtn;
        private final LinearLayout cartItemLayout;

        public CartItemHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            category = itemView.findViewById(R.id.item_category);
            price = itemView.findViewById(R.id.price);
            discount = itemView.findViewById(R.id.discount);
            quantity = itemView.findViewById(R.id.quantity);
            removeCartItemBtn = itemView.findViewById(R.id.remove_cart_item);
            cartItemLayout = itemView.findViewById(R.id.cart_item_layout);
        }

        public void setName(String name) {
            this.name.setText(name);
        }

        public void setCategory(String category) {
            this.category.setText(category);
        }

        public void setDiscount(int discount) {
            if (discount == 0) {
                this.discount.setVisibility(View.GONE);
            } else {
                this.discount.setVisibility(View.VISIBLE);
                String discount_ = discount + "% OFF";
                this.discount.setText(discount_);
            }
        }

        public void setQuantity(int quantity) {
            String qua_ = "Quantity : " + quantity;
            this.quantity.setText(qua_);
        }

        private void setPrice(int totalPrice) {
            String pri_ = "\u20B9 " + totalPrice;
            this.price.setText(pri_);
        }
    }
}
