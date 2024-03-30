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
    private String sehir,ilce;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                // Konumu al
                getLocation(googleMap);
            }
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
            // Butona tıklandığında o anki konumu al
            if (locationManager != null && locationListener != null) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation != null) {
                    double latitude = lastLocation.getLatitude();
                    double longitude = lastLocation.getLongitude();
                    String latitudeStr = String.valueOf(latitude);
                    String longitudeStr = String.valueOf(longitude);

                    getCityNameWithLocation(latitude,longitude);

                    Bundle bundle = new Bundle();
                    bundle.putString("la", latitudeStr); // Key, alıcı fragment'ta bu veriyi almak için kullanılacak
                    bundle.putString("lo", longitudeStr);
                    bundle.putString("sehir", sehir);
                    bundle.putString("ilce", ilce);
                    EkleEvcilFragment ekleEvcilFragment = new EkleEvcilFragment();
                    ekleEvcilFragment.setArguments(bundle);

                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.constMAP, ekleEvcilFragment)
                            .addToBackStack(null)
                            .commit();

                    // Toast ile kullanıcıya bilgi ver
                    Toast.makeText(requireContext(), "Konum başarıyla kaydedildi", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Konum bulunamadı", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Konum izni verilmedi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerLauncher() {
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Konumu al
                    getLocation(null);
                }
            } else {
                Toast.makeText(requireContext(), "Konum izni verilmedi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLocation(GoogleMap googleMap) {
        if (locationManager != null) {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                   /* double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String latitudeStr = String.valueOf(latitude);
                    String longitudeStr = String.valueOf(longitude);

                    // Haritada konumu göster
                    LatLng currentLocation = new LatLng(latitude, longitude);
                    if (googleMap != null) {
                        googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                    }*/
                }
            };
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // İzin verilmemiş, işlem yapma
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null && googleMap != null) {
                LatLng currentLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            }
        }
    }
    private void getCityNameWithLocation(double lat, double lng) {
        try {

            Geocoder geo = new Geocoder(getContext(), Locale.getDefault());
            List < Address > addresses = geo.getFromLocation(lat, lng, 1);
            if (addresses.isEmpty()) {

            } else {
                if (addresses.size() > 0) {
                    Log.i("cityname",addresses.get(0).getAdminArea());
                    sehir=addresses.get(0).getAdminArea();
                    ilce=addresses.get(0).getSubLocality();
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
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
