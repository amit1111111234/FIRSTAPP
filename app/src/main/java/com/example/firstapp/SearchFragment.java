package com.example.firstapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private EditText etSearch;
    private RecyclerView rvResults;
    private ArrayList<DataSnapshot> allPoints = new ArrayList<>();
    // You would create a simple Adapter for this RecyclerView

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
         rvResults = view.findViewById(R.id.rvResults);
        etSearch = view.findViewById(R.id.etSearchField);
//        RecyclerView rvRecent = view.findViewById(R.id.rvRecentSearches);
//        RecyclerView rvRecommended = view.findViewById(R.id.rvRecommended);
        rvResults.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch points once to search locally
        FirebaseDatabase.getInstance().getReference("Markers")
                .get().addOnSuccessListener(snapshot -> {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        allPoints.add(ds);
                    }
                });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterResults(s.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void filterResults(String query) {
        // Logic to update RecyclerView adapter based on 'allPoints'
    }
}