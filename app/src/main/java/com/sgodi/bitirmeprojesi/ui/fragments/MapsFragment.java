package com.sgodi.bitirmeprojesi.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.databinding.FragmentMapsBinding;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsFragment extends Fragment {

    private FragmentMapsBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private ActivityResultLauncher<String> permissionLauncher;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String sehir, ilce;

    private GoogleMap googleMap;
    private LatLng selectedLatLng; // Kullanıcının seçtiği konumu tutmak için

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap map) {
            googleMap = map;

            // Konum izni kontrolü
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                // Konum al
                getLocation();
            }

            // Haritada bir yere tıklandığında
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    // Kullanıcının tıkladığı konumu al
                    selectedLatLng = latLng;
                    // Önceki marker'ı temizle (sadece bir marker göstermek istiyoruz)
                    googleMap.clear();
                    // Yeni marker ekle
                    googleMap.addMarker(new MarkerOptions().position(selectedLatLng).title("Seçili Konum"));
                }
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapHayvanimAyrinti);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        registerLauncher();

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        binding.buttonKonumKaydet.setOnClickListener(view1 -> {
            // Seçili konumu kullanarak işlem yap
            if (selectedLatLng != null) {
                double latitude = selectedLatLng.latitude;
                double longitude = selectedLatLng.longitude;
                String latitudeStr = String.valueOf(latitude);
                String longitudeStr = String.valueOf(longitude);

                getCityNameWithLocation(latitude, longitude);

                MapsFragmentDirections.ActionMapsFragmentToEkleEvcilFragment gecis=
                        MapsFragmentDirections.actionMapsFragmentToEkleEvcilFragment(latitudeStr,longitudeStr,sehir,ilce);
                Navigation.findNavController(view).navigate(gecis);

                Toast.makeText(requireContext(), "Konum başarıyla kaydedildi", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Lütfen bir konum seçin.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerLauncher() {
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Konum izni verilmiş, işlem yap
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                }
            } else {
                Toast.makeText(requireContext(), "Konum izni verilmedi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLocation() {
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    // Konum değiştiğinde çalışacak kodlar
                }
            };
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                // Konumu al ve haritada göster
                LatLng currentLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                googleMap.setMyLocationEnabled(true);
            }
        }
    }

    private void getCityNameWithLocation(double lat, double lng) {
        try {
            Geocoder geo = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(lat, lng, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                sehir = address.getAdminArea();
                ilce = address.getSubLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
        binding = null;
    }
}

