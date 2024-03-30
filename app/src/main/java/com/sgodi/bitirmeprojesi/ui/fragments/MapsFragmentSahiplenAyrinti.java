package com.sgodi.bitirmeprojesi.ui.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.databinding.FragmentMapsSahiplenAyrintiBinding;

public class MapsFragmentSahiplenAyrinti extends Fragment {
    private FragmentMapsSahiplenAyrintiBinding binding;
    private String enlem, boylam;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            LatLng location = new LatLng(Double.parseDouble(enlem), Double.parseDouble(boylam));
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(location)
                    .title("Hayvanının Konumu");
            googleMap.addMarker(markerOptions);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsSahiplenAyrintiBinding.inflate(inflater, container, false);



        // Gelen verileri al
        if (getArguments() != null) {
            enlem = MapsFragmentHayvanimAyrintiArgs.fromBundle(getArguments()).getEnlem();
            boylam = MapsFragmentHayvanimAyrintiArgs.fromBundle(getArguments()).getBoylam();
        }

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapSahiplenAyrinti);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}