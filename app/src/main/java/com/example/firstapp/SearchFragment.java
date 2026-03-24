package com.example.firstapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText etSearch;
    private RecyclerView rvResults;
    private View layoutNoResults;
    private List<MapPoint> allPoints = new ArrayList<>();
    private List<MapPoint> filteredPoints = new ArrayList<>();
    private SearchAdapter adapter;

    public interface OnSearchClickListener {
        void onPointSelected(MapPoint point);
    }

    private OnSearchClickListener callback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchClickListener) {
            callback = (OnSearchClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnSearchClickListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);


        rvResults = view.findViewById(R.id.rvResults);
        etSearch = view.findViewById(R.id.etSearchField);
        layoutNoResults = view.findViewById(R.id.layoutNoResults);


        view.findViewById(R.id.btnBack).setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });


        view.findViewById(R.id.btnAddMissingPoint).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ActivityAddPoint.class);
            startActivity(intent);
        });


        rvResults.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SearchAdapter(filteredPoints);
        rvResults.setAdapter(adapter);


        FirebaseFirestore.getInstance().collection("Points")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allPoints.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        MapPoint p = doc.toObject(MapPoint.class);
                        allPoints.add(p);
                    }
                    filterResults("");
                });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterResults(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void filterResults(String query) {
        filteredPoints.clear();
        if (query.isEmpty()) {
            filteredPoints.addAll(allPoints);
        } else {
            for (MapPoint p : allPoints) {
                if (p.title != null && p.title.toLowerCase().contains(query.toLowerCase())) {
                    filteredPoints.add(p);
                }
            }
        }


        if (filteredPoints.isEmpty()) {
            layoutNoResults.setVisibility(View.VISIBLE);
            rvResults.setVisibility(View.GONE);
        } else {
            layoutNoResults.setVisibility(View.GONE);
            rvResults.setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
        private List<MapPoint> list;
        public SearchAdapter(List<MapPoint> list) { this.list = list; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            MapPoint p = list.get(position);

            holder.text1.setText(p.title);
            holder.text1.setTextColor(0xFFFFFFFF);


            holder.text2.setText(p.locationName);
            holder.text2.setTextColor(0xFFBCCFC2);

            holder.itemView.setOnClickListener(v -> {
                if (callback != null) {
                    callback.onPointSelected(p);
                    getParentFragmentManager().popBackStack();
                }
            });
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView text1, text2;
            ViewHolder(View v) {
                super(v);
                text1 = v.findViewById(android.R.id.text1);
                text2 = v.findViewById(android.R.id.text2);
            }
        }
    }
}