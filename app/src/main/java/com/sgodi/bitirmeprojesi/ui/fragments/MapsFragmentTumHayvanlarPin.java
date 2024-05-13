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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.Hayvan;
import com.sgodi.bitirmeprojesi.data.models.Kullanici;
import com.sgodi.bitirmeprojesi.databinding.FragmentMapsTumHayvanlarPinBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsFragmentTumHayvanlarPin extends Fragment {

    private GoogleMap mMap;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FusedLocationProviderClient fusedLocationClient;
    private FragmentMapsTumHayvanlarPinBinding binding;
    ArrayList<Hayvan> hayvans;
    private Boolean favoriMi,oneri;


    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            getLastKnownLocation();
            // Kullanıcının mevcut konumunu haritada göster
            showCurrentLocation();
            // Firestore'dan tüm hayvanların konumlarını al ve haritada marker'lar oluştur
            getKullaniciOneriDurum();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding=FragmentMapsTumHayvanlarPinBinding.inflate(inflater, container, false);
        hayvans=new ArrayList<>();
        auth=FirebaseAuth.getInstance();

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
    private void getKullaniciKisilik(FirebaseFirestore firestore, FirebaseAuth auth, MapsFragmentTumHayvanlarPin.KisilikCallback callback) {
        String currentUserEmail = auth.getCurrentUser().getEmail();
        firestore.collection("kullanicilar").whereEqualTo("email", currentUserEmail)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (value != null) {
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                Map<String, Object> data = documentSnapshot.getData();
                                String kisilik = (String) data.get("kişilik");
                                callback.onKisilikReceived(kisilik);
                            }
                        }
                    }
                });
    }
    public interface KisilikCallback {
        void onKisilikReceived(String kisilikValue);
    }
    private void getKullaniciOneriDurum() {
        firestore.collection("kullanicilar")
                .whereEqualTo("email", auth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Boolean oneriDurumu = document.getBoolean("oneri_durum");
                            if (oneriDurumu != null && oneriDurumu) {
                                oneri = true;
                            } else {
                                oneri = false;
                            }
                            // Öneri durumu alındıktan sonra gerekli işlemleri yapmak için burada çağırabilirsiniz
                            if (oneri) {
                                getData();

                            } else {
                                getAllAnimalsLocation();
                            }
                        }
                    }
                });
    }
    private void getData(){
        getKullaniciKisilik(firestore, auth, new KisilikCallback() {
            @Override
            public void onKisilikReceived(String kisilikValue) {
                firestore.collection("kullanici_hayvanlari")
                        .whereEqualTo("ilanda_mi","true")
                        .whereEqualTo("kisilik",kisilikValue)
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
                                    String foto1 = document.getString("foto1");
                                    String foto2 = document.getString("foto2");
                                    String foto3 = document.getString("foto3");
                                    String foto4 = document.getString("foto4");
                                    String email = document.getString("email");
                                    String docid=document.getId();
                                    String sahiplimi=document.getString("sahipli_mi");
                                    String ilanda_mi=document.getString("ilanda_mi");
                                    String enlem=document.getString("enlem");
                                    String boylam=document.getString("boylam");
                                    // Hayvan nesnesini oluştur
                                    Hayvan hayvan = new Hayvan(email,foto1,foto2,foto3,foto4,ad,tur,irk,cinsiyet,yas,saglik,aciklama,kisilik,docid,sahiplimi,ilanda_mi,enlem,
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
        });
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
                            String foto1 = document.getString("foto1");
                            String foto2 = document.getString("foto2");
                            String foto3 = document.getString("foto3");
                            String foto4 = document.getString("foto4");
                            String email = document.getString("email");
                            String docid=document.getId();
                            String sahiplimi=document.getString("sahipli_mi");
                            String ilanda_mi=document.getString("ilanda_mi");
                            String enlem=document.getString("enlem");
                            String boylam=document.getString("boylam");
                            // Hayvan nesnesini oluştur
                            Hayvan hayvan = new Hayvan(email,foto1,foto2,foto3,foto4,ad,tur,irk,cinsiyet,yas,saglik,aciklama,kisilik,docid,sahiplimi,ilanda_mi,enlem,
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
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView imageViewFav = view.findViewById(R.id.imageView_kaydet_bottom);
        firestore.collection("favoriler")
                .whereEqualTo("email", auth.getCurrentUser().getEmail())
                .whereEqualTo("docID", hayvan.getDocID())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            // Favoride var
                            favoriMi=true;
                            imageViewFav.setImageResource(R.drawable.baseline_bookmark_added_32);
                        } else {
                            favoriMi=false;
                            imageViewFav.setImageResource(R.drawable.baseline_bookmark_add_32);
                        }
                    } else {
                        // Firestore sorgusu başarısız oldu
                        Toast.makeText(requireContext(), "Favorileri kontrol ederken bir hata oluştu.", Toast.LENGTH_SHORT).show();
                    }
                });
        // Özellikleri göster;
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView imageView = view.findViewById(R.id.imageViewHayvanimAyrinti_bottom);
        Picasso.get().load(hayvan.getFoto1()).resize(150,150)
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
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView buttonFavEkle = view.findViewById(R.id.imageView_kaydet_bottom);
        buttonFavEkle.setOnClickListener(view1 -> {
            if (favoriMi){
                etkinligiFavorilerdenSil(hayvan.getDocID(), firestore,imageViewFav);
            }else{
                etkinligiFavorilereEkle(hayvan.getDocID(), auth.getCurrentUser().getEmail(), firestore,imageViewFav);
            }

        });
        // Dialogu göster
        bottomSheetDialog.show();
    }
    private void etkinligiFavorilerdenSil(String docID, FirebaseFirestore firestore, ImageView imageViewFav) {
        firestore.collection("favoriler")
                .whereEqualTo("docID", docID)
                .whereEqualTo("email", auth.getCurrentUser().getEmail())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        // Belirli bir kaydı sil
                        firestore.collection("favoriler")
                                .document(snapshot.getId()) // Silinecek belgenin kimliğini al
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Etkinlik favorilerden kaldırıldı", Toast.LENGTH_SHORT).show();
                                    imageViewFav.setImageResource(R.drawable.baseline_bookmark_add_32);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Etkinlik favorilerden kaldırılırken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Favorileri kontrol ederken bir hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void etkinligiFavorilereEkle(String docID, String email, FirebaseFirestore firestore, ImageView imageViewFav) {
        CollectionReference bildirilerCollectionRef = firestore.collection("favoriler");
        DocumentReference yeniBelgeRef = bildirilerCollectionRef.document();
        Map<String, Object> bildiriVerileri = new HashMap<>();
        bildiriVerileri.put("email", email);
        bildiriVerileri.put("docID", docID);
        yeniBelgeRef.set(bildiriVerileri)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Favorilere eklendi", Toast.LENGTH_SHORT).show();
                        imageViewFav.setImageResource(R.drawable.baseline_bookmark_added_32);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Favorilere eklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
