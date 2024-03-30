package com.sgodi.bitirmeprojesi.ui.fragments;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.databinding.FragmentBakiciBinding;
import com.sgodi.bitirmeprojesi.databinding.FragmentMapsBinding;
import com.sgodi.bitirmeprojesi.ui.interfaces.LocationDataTransferInterface;

public class MapsFragment extends Fragment implements LocationDataTransferInterface {
    ActivityResultLauncher<String> permissionLauncher;
    LocationManager locationManager;
    LocationListener locationListener;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private @NonNull FragmentMapsBinding binding;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                // Konumu al
                getLocation(googleMap);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        registerLauncher();

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        binding.buttonKonumKaydet.setOnClickListener(view1 -> {
            // Butona tıklandığında o anki konumu al
            if (locationManager != null && locationListener != null) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation != null) {
                    double latitude = lastLocation.getLatitude();
                    double longitude = lastLocation.getLongitude();
                    String latitudeStr = String.valueOf(latitude);
                    String longitudeStr = String.valueOf(longitude);

                    onLocationDataReceived(latitudeStr,longitudeStr);
                    binding.textView5.setText(latitudeStr+"   "+longitudeStr);
                    Bundle bundle = new Bundle();
                    bundle.putString("la", latitudeStr); // Key, alıcı fragment'ta bu veriyi almak için kullanılacak
                    bundle.putString("lo", longitudeStr);
                    EkleEvcilFragment ekleEvcilFragment = new EkleEvcilFragment();
                    ekleEvcilFragment.setArguments(bundle);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.constMAP, ekleEvcilFragment);
                    fragmentTransaction.commit();

                    //MapsFragmentDirections.ActionMapsFragmentToEkleEvcilFragment gecis=MapsFragmentDirections
                    //       .actionMapsFragmentToEkleEvcilFragment(latitudeStr,longitudeStr);
                    //Navigation.findNavController(view).navigate(R.id.action_mapsFragment_to_ekleEvcilFragment);




                    // Toast ile kullanıcıya bilgi ver
                    Toast.makeText(getContext(), "Konum başarıyla kaydedildi", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Konum bulunamadı", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Konum izni verilmedi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerLauncher() {
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean o) {
                if (o) {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {
                        // Konumu al
                        getLocation(null);
                    }
                } else {
                    Toast.makeText(getContext(), "Konum izni verilmedi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getLocation(GoogleMap googleMap) {
        if (locationManager != null) {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String latitudeStr = String.valueOf(latitude);
                    String longitudeStr = String.valueOf(longitude);

                    // Firestore'da konumu güncelle
                    //updateLocationInFirestore(latitudeStr, longitudeStr);
                    onLocationDataReceived(latitudeStr, longitudeStr);

                    // Haritada konumu göster
                    LatLng currentLocation = new LatLng(latitude, longitude);
                    googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                }
            };
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation!=null){
                LatLng currentLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            }
        }
    }

    private void updateLocationInFirestore(String latitude, String longitude) {
        String currentUserEmail = auth.getCurrentUser().getEmail();
        DocumentReference userDocRef = firestore.collection("users").document(currentUserEmail);

        // Belirli bir belgedeki alanları güncelle
        userDocRef
                .update("latitude", latitude,
                        "longitude", longitude)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Konum başarıyla güncellendi", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Konum güncellenirken hata oluştu", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
        binding = null;
    }

    @Override
    public void onLocationDataReceived(String latitude, String longitude) {

    }

}
