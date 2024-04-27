package com.sgodi.bitirmeprojesi.ui.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
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

        binding.buttonGitYolTarifi.setOnClickListener(view -> {
            yolTarifi(view);
        });





        return binding.getRoot();
    }

    private void yolTarifi(View view) {
        try {
            // Firestore sorgusuyla ilgili hayvanın konumunu al
            // Hayvanın konumunu al
            double hayvanLatitude = Double.parseDouble(enlem);
            double hayvanLongitude = Double.parseDouble(boylam);
            // Kullanıcının mevcut konumunu almak için yer servislerini kullanın
            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // İzin yoksa işlem yapma
                return;
            }
            Location location = locationManager.getLastKnownLocation(provider);
            // Mevcut konum alındıysa
            if (location != null) {
                double userLatitude = location.getLatitude();
                double userLongitude = location.getLongitude();
                // Google Haritalar uygulamasını açmak için bir URL oluşturun
                String uri = "http://maps.google.com/maps?saddr=" + userLatitude + "," + userLongitude +
                                                "&daddr=" + hayvanLatitude + "," + hayvanLongitude;

                // Intent'i oluşturun ve URL'yi ekleyin
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                // Intent'i başlatın
                startActivity(intent);
            } else {
                // Konum alınamadıysa kullanıcıya bilgi verin
                Toast.makeText(getContext(), "Konum bilgisine erişilemiyor.", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Log.i("Mesaj",e.getMessage());
        }
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