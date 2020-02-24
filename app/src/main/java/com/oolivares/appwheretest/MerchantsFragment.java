package com.oolivares.appwheretest;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MerchantsFragment extends Fragment {


    private List<Merchants> merchants;
    private MerchantsInterface listener;

    public static MerchantsFragment newInstance() {
        MerchantsFragment f = new MerchantsFragment();
        // Supply index input as an argument.
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.list_layout, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        if(savedInstanceState==null) {
            merchants = listener.getMerchants();
        }else{
            merchants = savedInstanceState.getParcelableArrayList("merchants");
        }
        MerchantsAdapter merchantsAdapter = new MerchantsAdapter(getContext(), merchants);
        recyclerView.setAdapter(merchantsAdapter);
        return rootView;
    }

    public interface MerchantsInterface{
        List<Merchants> getMerchants();
    }

    public void setinterface(MerchantsInterface listener){
        this.listener = listener;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("merchants",new ArrayList<>(merchants));
    }
}
