package com.ironxpert.client.tabs;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ironxpert.client.R;
import com.ironxpert.client.SearchResultActivity;
import com.ironxpert.client.adapters.FoodCardRecyclerAdapter;
import com.ironxpert.client.adapters.FoodItemRecyclerAdapter;
import com.ironxpert.client.models.Food;
import com.ironxpert.client.models.Topping;
import com.ironxpert.client.sheets.FoodDetailBottomSheet;
import com.ironxpert.client.utils.Validator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {
    private View view;
    private RecyclerView serviceRv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);


        return view;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onStart() {
        super.onStart();
    }
}