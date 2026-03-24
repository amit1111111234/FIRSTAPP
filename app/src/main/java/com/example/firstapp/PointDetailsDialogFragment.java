package com.example.firstapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class PointDetailsDialogFragment extends BottomSheetDialogFragment {

    private MapPoint point;

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
        View v = inflater.inflate(R.layout.layout_point_details, container, false);

        if (getArguments() != null) {
            point = (MapPoint) getArguments().getSerializable("point_data");
        }

        TextView tvTitle = v.findViewById(R.id.tvPointTitle);
        TextView tvLocation = v.findViewById(R.id.tvLocationName);
        TextView tvDescription = v.findViewById(R.id.tvDescription);
        Button btnNavigate = v.findViewById(R.id.btnNavigate);

        if (point != null) {
            tvTitle.setText(point.title);
            tvLocation.setText(point.locationName);
            tvDescription.setText(point.description);

            btnNavigate.setOnClickListener(view -> {
                String uri = "google.navigation:q=" + point.latitude + "," + point.longitude;
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {

                    Uri geoUri = Uri.parse("geo:" + point.latitude + "," + point.longitude);
                    startActivity(new Intent(Intent.ACTION_VIEW, geoUri));
                }
            });
        }

        return v;
    }
}