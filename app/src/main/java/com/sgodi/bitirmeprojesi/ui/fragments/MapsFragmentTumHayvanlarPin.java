package com.sgodi.bitirmeprojesi.ui.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.Hayvan;
import com.sgodi.bitirmeprojesi.data.models.Kullanici;
import com.sgodi.bitirmeprojesi.databinding.FragmentMapsTumHayvanlarPinBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MapsFragmentTumHayvanlarPin extends Fragment {

    private GoogleMap mMap;
    private FirebaseFirestore firestore;
    private FusedLocationProviderClient fusedLocationClient;
    private FragmentMapsTumHayvanlarPinBinding binding;
    ArrayList<Hayvan> hayvans;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            getLastKnownLocation();
            // Kullanıcının mevcut konumunu haritada göster
            showCurrentLocation();
            // Firestore'dan tüm hayvanların konumlarını al ve haritada marker'lar oluştur
            getAllAnimalsLocation();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding=FragmentMapsTumHayvanlarPinBinding.inflate(inflater, container, false);
        hayvans=new ArrayList<>();


        return binding.getRoot();
    }
    private void getLastKnownLocation() {
        // Son bilinen konumu almak için işlem yap
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // İzin yoksa işlem yapma
            return;
        }

        // Son bilinen konumu al
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Son bilinen konumu al ve haritayı bu konumla başlat
                            LatLng lastKnownLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocation, 15)); // Örnek olarak 15
                        }
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        firestore = FirebaseFirestore.getInstance();
    }

    private void showCurrentLocation() {
        // Kullanıcının mevcut konumunu al
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // İzin yoksa işlem yapma
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void getAllAnimalsLocation() {
        firestore.collection("kullanici_hayvanlari")
                .whereEqualTo("ilanda_mi","true")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Firestore'dan tüm hayvanların konumlarını al
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot document : documents) {
                            // Hayvan belgesinden tüm özellikleri al
                            String ad = document.getString("ad");
                            String tur = document.getString("tur");
                            String irk = document.getString("ırk");
                            String cinsiyet = document.getString("cinsiyet");
                            String yas = document.getString("yas");
                            String saglik = document.getString("saglik");
                            String kisilik = document.getString("kisilik");
                            String aciklama = document.getString("aciklama");
                            String sehir = document.getString("sehir");
                            String ilce = document.getString("ilce");
                            String foto = document.getString("foto");
                            String email = document.getString("email");
                            String docid=document.getId();
                            String sahiplimi=document.getString("sahipli_mi");
                            String ilanda_mi=document.getString("ilanda_mi");
                            String enlem=document.getString("enlem");
                            String boylam=document.getString("boylam");
                            // Hayvan nesnesini oluştur
                            Hayvan hayvan = new Hayvan(email,foto,ad,tur,irk,cinsiyet,yas,saglik,aciklama,kisilik,docid,sahiplimi,ilanda_mi,enlem,
                                    boylam,sehir,ilce);
                            hayvans.add(hayvan);

                            // Hayvanın konumunu al
                            double latitude = Double.parseDouble(document.getString("enlem"));
                            double longitude = Double.parseDouble(document.getString("boylam"));

                            // Marker oluştur ve haritaya ekle
                            LatLng animalLocation = new LatLng(latitude, longitude);
                            Marker marker = mMap.addMarker(new MarkerOptions().position(animalLocation));

                            // Marker'a tıklanınca hangi hayvana tıklanıldığını belirlemek için dinleyici ekle
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    // Marker'a tıklandığında ilgili hayvanın bilgilerini göster
                                    Hayvan clickedHayvan = getHayvanFromMarker(marker);
                                    if (clickedHayvan != null) {
                                        // İlgili hayvanın bilgilerini gösteren bottom sheet dialog oluştur
                                        showAnimalDetailsDialog(clickedHayvan);
                                    }
                                    return false;
                                }
                            });
                        }
                    }
                });
    }

    // Marker'a tıklanınca ilgili hayvanı döndüren yardımcı bir metod
    private Hayvan getHayvanFromMarker(Marker marker) {
        LatLng markerPosition = marker.getPosition();
        for (Hayvan hayvan : hayvans) {
            double hayvanLatitude = Double.parseDouble(hayvan.getEnlem());
            double hayvanLongitude = Double.parseDouble(hayvan.getBoylam());
            LatLng hayvanLocation = new LatLng(hayvanLatitude, hayvanLongitude);
            if (hayvanLocation.equals(markerPosition)) {
                return hayvan;
            }
        }
        return null;
    }


    private void showAnimalDetailsDialog(Hayvan hayvan) {
        // Bottom sheet dialog oluştur
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(),R.style.BottomSheetDialogTheme);
        // Layout dosyasını yükle
        View view = getLayoutInflater().inflate(R.layout.hayvan_pin_bottom_sheet,null);
        bottomSheetDialog.setContentView(view);

        // Özellikleri göster;
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView imageView = view.findViewById(R.id.imageViewHayvanimAyrinti_bottom);
        Picasso.get().load(hayvan.getFoto()).resize(150,150)
                .into(imageView);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textViewAd = view.findViewById(R.id.hayvan_ayrinti_AD_bottom);
        textViewAd.setText(hayvan.getAd());
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textViewTur = view.findViewById(R.id.hayvan_ayrinti__TUR_bottom);
        textViewTur.setText(hayvan.getTur());
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textViewIrk = view.findViewById(R.id.hayvan_ayrinti_IRK_bottom);
        textViewIrk.setText(hayvan.getIrk());
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textViewCinsiyet = view.findViewById(R.id.hayvan_ayrinti_CINSIYET_bottom);
        textViewCinsiyet.setText(hayvan.getCinsiyet());
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textViewyas = view.findViewById(R.id.hayvan_ayrinti_YAS_bottom);
        textViewyas.setText(hayvan.getYas());
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textViewysaglik = view.findViewById(R.id.hayvan_ayrinti_SAGLIK_bottom);
        textViewysaglik.setText(hayvan.getSaglik());
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textViewkisilik = view.findViewById(R.id.hayvan_ayrinti_KISILIK_bottom);
        textViewkisilik.setText(hayvan.getKisilik());
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textViewsehir = view.findViewById(R.id.hayvan_ayrinti_Sehir_bottom);
        textViewsehir.setText(hayvan.getSehir());
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textViewhakkinda = view.findViewById(R.id.hayvan_ayrinti_HAKKINDA_bottom);
        textViewhakkinda.setText(hayvan.getAciklama());
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button buttonMesajGonder = view.findViewById(R.id.buttonMesaj_bottom);
        buttonMesajGonder.setOnClickListener(view1 -> {
            bottomSheetDialog.cancel();
            ilgiliKisiyeMesajListesiAc(hayvan.getEmail());
        });
        // Dialogu göster
        bottomSheetDialog.show();
    }

    private void ilgiliKisiyeMesajListesiAc(String email) {
        try {
            firestore.collection("kullanicilar")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            // E-postaya sahip kullanıcılar varsa
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    String aciklama = documentSnapshot.getString("aciklama");
                                    String ad = documentSnapshot.getString("ad");
                                    String bakici_durum = documentSnapshot.getString("bakici_durum");
                                    String kisilik_durum = documentSnapshot.getString("kisilik_durum");
                                    String kisilik = documentSnapshot.getString("kişilik");
                                    String konum = documentSnapshot.getString("konum");
                                    String soyad = documentSnapshot.getString("soyad");
                                    String tel = documentSnapshot.getString("tel");
                                    Boolean oneri_durum = documentSnapshot.getBoolean("oneri_durum");
                                    Kullanici kullanici= new Kullanici(ad,soyad,email,kisilik,konum,tel,aciklama,kisilik_durum,bakici_durum,oneri_durum);
                                    MapsFragmentTumHayvanlarPinDirections.ActionMapsFragmentTumHayvanlarPinToMesajFragment gecis=
                                            MapsFragmentTumHayvanlarPinDirections.actionMapsFragmentTumHayvanlarPinToMesajFragment(kullanici);

                                    Navigation.findNavController(getView()).navigate(gecis);
                                }
                            } else {
                                // Belirtilen e-posta adresine sahip kullanıcı bulunamadı
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Hata durumunda buraya düşer
                            Log.e("Mesaj", "Kullanıcı bulunurken hata oluştu: " + e.getMessage());
                        }
                    });
        }catch (Exception e){
            Log.i("Mesaj",e.getMessage());
        }



    }

}
