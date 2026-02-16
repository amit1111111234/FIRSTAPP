package com.example.firstapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PointDetailsDialogFragment extends BottomSheetDialogFragment {

    private MapPoint point;

    // Static method to initialize the fragment with data
    public static PointDetailsDialogFragment newInstance(MapPoint point) {
        PointDetailsDialogFragment fragment = new PointDetailsDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("point_data", point);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate your Ceramic and Sage layout
        View v = inflater.inflate(R.layout.layout_point_details, container, false);

        if (getArguments() != null) {
            point = (MapPoint) getArguments().getSerializable("point_data");
        }

        TextView tvTitle = v.findViewById(R.id.tvPointTitle);
        TextView tvLocation = v.findViewById(R.id.tvLocationName);
        TextView tvDescription = v.findViewById(R.id.tvDescription);

        if (point != null) {
            tvTitle.setText(point.title);
            tvLocation.setText(point.locationName != null ? point.locationName : "Location details");
            tvDescription.setText(point.description);
        }

        return v;
    }
}