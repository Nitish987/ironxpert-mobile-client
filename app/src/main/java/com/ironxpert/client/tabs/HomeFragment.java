package com.ironxpert.client.tabs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ironxpert.client.R;
import com.ironxpert.client.adapters.ServiceRecyclerAdapter;
import com.ironxpert.client.common.db.LaunderingService;
import com.ironxpert.client.models.Service;

import java.util.List;

public class HomeFragment extends Fragment {
    private View view;
    private RecyclerView serviceRv;

    private List<Service> serviceList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceList = LaunderingService.getServiceList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        serviceRv = view.findViewById(R.id.services_rv);
        serviceRv.setLayoutManager(new GridLayoutManager(getContext(), 2));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ServiceRecyclerAdapter serviceAdapter = new ServiceRecyclerAdapter(serviceList);
        serviceRv.setAdapter(serviceAdapter);
    }
}