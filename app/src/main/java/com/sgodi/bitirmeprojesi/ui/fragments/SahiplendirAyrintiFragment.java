package com.sgodi.bitirmeprojesi.ui.fragments;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.Hayvan;
import com.sgodi.bitirmeprojesi.data.models.Kullanici;
import com.sgodi.bitirmeprojesi.data.models.Mesaj;
import com.sgodi.bitirmeprojesi.databinding.FragmentSahiplendirAyrintiBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SahiplendirAyrintiFragment extends Fragment {
    private FragmentSahiplendirAyrintiBinding binding;
    FirebaseFirestore firestore;
    String docID,email;
    ArrayList<SlideModel> slideModels;
    private String foto1,foto2,foto3,foto4;
    Boolean favoriMi;
    private FirebaseAuth auth;

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSahiplendirAyrintiBinding.inflate(inflater, container, false);

        firestore = FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        email=auth.getCurrentUser().getEmail();
        SahiplendirAyrintiFragmentArgs bundle = SahiplendirAyrintiFragmentArgs.fromBundle(getArguments());
        Hayvan hayvan = bundle.getHayvan();
        foto1=hayvan.getFoto1();
        foto2=hayvan.getFoto2();
        foto3=hayvan.getFoto3();
        foto4=hayvan.getFoto4();
        slideModels=new ArrayList<>();
        SlideModel slideModel1=new SlideModel(foto1, ScaleTypes.FIT);
        slideModels.add(slideModel1);
        if (!"null".equals(foto2)) {
            SlideModel slideModel2 = new SlideModel(foto2, ScaleTypes.FIT);
            slideModels.add(slideModel2);
        }

        if (!"null".equals(foto3)) {
            SlideModel slideModel3 = new SlideModel(foto3, ScaleTypes.FIT);
            slideModels.add(slideModel3);
        }

        if (!"null".equals(foto4)) {
            SlideModel slideModel4 = new SlideModel(foto4, ScaleTypes.FIT);
            slideModels.add(slideModel4);
        }

        binding.imageSlider.setImageList(slideModels,ScaleTypes.FIT);
        //Picasso.get().load(hayvan.getFoto1()).into(binding.imageViewHayvanimAyrinti);
        binding.hayvanAyrintiAD.setText(hayvan.getAd());
        binding.hayvanAyrintiTUR.setText(hayvan.getTur());
        binding.hayvanAyrintiIRK.setText(hayvan.getIrk());
        binding.hayvanAyrintiCINSIYET.setText(hayvan.getCinsiyet());
        binding.hayvanAyrintiYAS.setText(hayvan.getYas());
        binding.hayvanAyrintiSAGLIK.setText(hayvan.getSaglik());
        binding.hayvanAyrintiKISILIK.setText(hayvan.getKisilik());
        binding.hayvanAyrintiHAKKINDA.setText(hayvan.getAciklama());
        binding.hayvanAyrintiSehir.setText(hayvan.getSehir() + " / " + hayvan.getIlce());
        getFavoriMi(auth.getCurrentUser().getEmail(),hayvan.getDocID());
        foto1=hayvan.getFoto1();


        binding.imageViewKaydet.setOnClickListener(view -> {
            if (favoriMi){
                hayvaniFavorilerdenSil(hayvan.getDocID(), firestore);
            }else{
                hayvaniFavorilereEkle(hayvan.getDocID(), auth.getCurrentUser().getEmail(), firestore);
            }
        });


        binding.hayvanAyrintiKonum.setOnClickListener(view -> {
            SahiplendirAyrintiFragmentDirections.ActionSahiplendirAyrintiFragmentToMapsFragmentSahiplenAyrinti gecis =
                    SahiplendirAyrintiFragmentDirections.actionSahiplendirAyrintiFragmentToMapsFragmentSahiplenAyrinti(hayvan.getEnlem(), hayvan.getBoylam());
            Navigation.findNavController(view).navigate(gecis);
        });


        binding.materialToolbarSahiplendirAyrinti.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.ilamBildir) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Bildir");
                    alert.setMessage("Bildirmek istediğinizden emin misiniz? Gereksiz bildirimler tekrarlanırsa cezalandırılacaktır.");
                    alert.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            firestoreBildir(hayvan.getEmail(), hayvan.getDocID());
                        }
                    });
                    alert.setNegativeButton("HAYIR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.show();


                }
                return false;
            }
        });


        binding.buttonMesaj.setOnClickListener(view -> {
            kullaniciBul(hayvan.getEmail());
            //mesaj sayfasına git
        });

        binding.buttonYolTarifi.setOnClickListener(view -> {
            yolTarifi(view);
        });


        return binding.getRoot();
    }
    private void hayvaniFavorilerdenSil(String docID, FirebaseFirestore firestore) {
        firestore.collection("favoriler")
                .whereEqualTo("docID", docID)
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Favoriler koleksiyonunda belirli bir etkinliğin kaydını bulduk
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        // Belirli bir kaydı sil
                        firestore.collection("favoriler")
                                .document(snapshot.getId()) // Silinecek belgenin kimliğini al
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Etkinlik favorilerden kaldırıldı", Toast.LENGTH_SHORT).show();
                                    binding.imageViewKaydet.setImageResource(R.drawable.baseline_bookmark_add_24_beyaz);
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


    private void hayvaniFavorilereEkle(String docID, String email, FirebaseFirestore firestore) {
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
                        binding.imageViewKaydet.setImageResource(R.drawable.baseline_bookmark_added_24_beyaz);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Favorilere eklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void getFavoriMi(String email, String docID) {
        // Kullanıcının emailine göre Firestore'da favoriler koleksiyonunda arama yap
        firestore.collection("favoriler")
                .whereEqualTo("email", email)
                .whereEqualTo("docID", docID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Favoriler koleksiyonunda kayıt bulundu
                        if (!task.getResult().isEmpty()) {
                            // Favoride var
                            favoriMi=true;
                            binding.imageViewKaydet.setImageResource(R.drawable.baseline_bookmark_added_24_beyaz);
                        } else {
                            // Favoride yok, ekle
                            favoriMi=false;;
                            binding.imageViewKaydet.setImageResource(R.drawable.baseline_bookmark_add_24_beyaz);
                        }
                    } else {
                        // Firestore sorgusu başarısız oldu
                        Toast.makeText(requireContext(), "Favorileri kontrol ederken bir hata oluştu.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void yolTarifi(View view) {
        try {
            // Firestore sorgusuyla ilgili hayvanın konumunu al
            firestore.collection("kullanici_hayvanlari").document(docID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Hayvanın konumunu al
                                    double hayvanLatitude = Double.parseDouble(document.getString("enlem"));
                                    double hayvanLongitude = Double.parseDouble(document.getString("boylam"));

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
                                } else {
                                    // Doküman yoksa kullanıcıya bilgi verin

                                    Log.d("mesaj", "No such document");
                                }
                            } else {
                                // Sorgu başarısız olursa kullanıcıya bilgi verin
                                Log.d("mesaj", "get failed with ",task.getException());
                            }
                        }
                    });
        }catch (Exception e){
            Log.i("Mesaj",e.getMessage());
        }
    }
    private void kullaniciBul(String email) {
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
                                SahiplendirAyrintiFragmentDirections.ActionSahiplendirAyrintiFragmentToMesajFragment gecis=
                                        SahiplendirAyrintiFragmentDirections.actionSahiplendirAyrintiFragmentToMesajFragment(kullanici);
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
    }


    private void firestoreBildir(String email, String docID) {
        // Firestore bağlantısını başlat
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Bildiriler koleksiyonuna referans oluştur
        CollectionReference bildirilerCollectionRef = db.collection("bildiriler");

        // Yeni bir belge referansı oluştur
        DocumentReference yeniBelgeRef = bildirilerCollectionRef.document();

        // Belgeye verileri ekle
        Map<String, Object> bildiriVerileri = new HashMap<>();
        bildiriVerileri.put("email", email);
        bildiriVerileri.put("docID", docID);

        // Belgeyi Firestore'a ekle
        yeniBelgeRef.set(bildiriVerileri)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Başarıyla eklendiğinde yapılacak işlemler
                        Toast.makeText(getContext(), "Bildiri başarıyla eklendi", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Ekleme işlemi başarısız olduğunda yapılacak işlemler
                        Toast.makeText(getContext(), "Bildiri eklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}