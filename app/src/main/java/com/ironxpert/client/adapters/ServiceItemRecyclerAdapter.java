package com.ironxpert.client.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.ironxpert.client.R;
import com.ironxpert.client.models.ServiceItem;
import com.ironxpert.client.sheets.ServiceItemDetailBottomSheet;

public class ServiceItemRecyclerAdapter extends FirestoreRecyclerAdapter<ServiceItem, ServiceItemRecyclerAdapter.ServiceItemHolder> {
    private final FragmentManager manager;
    private final LinearLayout noItemI;

    public ServiceItemRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ServiceItem> options, FragmentManager manager, LinearLayout noItemI) {
        super(options);
        this.manager = manager;
        this.noItemI = noItemI;
    }

    @Override
    protected void onBindViewHolder(@NonNull ServiceItemHolder holder, int position, @NonNull ServiceItem model) {
        noItemI.setVisibility(View.GONE);

        holder.setItemName(model.getName());
        holder.setItemCategory(model.getCategory());
        holder.setItemPrice(model.getPrice());
        holder.setItemDiscount(model.getDiscount());

        holder.itemView.setOnClickListener(v -> ServiceItemDetailBottomSheet.newInstance(model).show(manager, "SERVICE_ITEM_DIALOG"));
    }

    @NonNull
    @Override
    public ServiceItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ServiceItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_item, parent, false));
    }

    public static class ServiceItemHolder extends RecyclerView.ViewHolder {
        private final TextView itemName, itemCategory, itemPrice, itemDiscount;

        public ServiceItemHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
            itemCategory = itemView.findViewById(R.id.item_category);
            itemPrice = itemView.findViewById(R.id.item_price);
            itemDiscount = itemView.findViewById(R.id.item_discount);
        }

        public void setItemName(String itemName) {
            this.itemName.setText(itemName);
        }

        public void setItemCategory(String itemCategory) {
            this.itemCategory.setText(itemCategory);
        }

        public void setItemPrice(int itemPrice) {
            String price = "\u20B9 " + itemPrice;
            this.itemPrice.setText(price);
        }

        public void setItemDiscount(int itemDiscount) {
            if (itemDiscount == 0) {
                this.itemDiscount.setVisibility(View.GONE);
            } else {
                this.itemDiscount.setVisibility(View.VISIBLE);
                String discount = itemDiscount + "% OFF";
                this.itemDiscount.setText(discount);
            }
        }
    }
}
