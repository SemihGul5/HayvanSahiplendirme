package com.sgodi.bitirmeprojesi.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.Hayvan;
import com.sgodi.bitirmeprojesi.databinding.FragmentArtikSahibiVarBinding;
import com.sgodi.bitirmeprojesi.ui.adapters.SahiplendirAdapter;

import java.util.ArrayList;
import java.util.Map;

public class ArtikSahibiVarFragment extends Fragment {
    private FragmentArtikSahibiVarBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private ArrayList<Hayvan> hayvanListesi;
    SahiplendirAdapter adapter;
    private ArrayList<String> docIDListesi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentArtikSahibiVarBinding.inflate(inflater, container, false);
        binding.materialToolbarArtikSahibiVar.setTitle("Sahiplendirilen Hayvanlar");
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        hayvanListesi=new ArrayList<>();
        docIDListesi = new ArrayList<>();
        getData(firestore);

        binding.rvArtikSahibiVar.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter= new SahiplendirAdapter(getContext(),hayvanListesi, SahiplendirAdapter.SayfaTuru.ARTIK_SAHIBI_VAR);
        binding.rvArtikSahibiVar.setAdapter(adapter);










        return binding.getRoot();
    }

    private void getData(FirebaseFirestore firestore) {
        // Favoriler koleksiyonundan belirli bir kullanıcının favori etkinliklerini getir
        firestore.collection("sahibi_olan_hayvanlar")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Etkinlik DocID'lerini al ve ArrayList'e ekle
                            String etkinlikDocID = document.getString("docID");
                            docIDListesi.add(etkinlikDocID);
                        }

                        // Elde edilen DocID listesiyle başka bir işlem yapmak için bir fonksiyonu çağırın
                        aramaYap(docIDListesi);
                    } else {
                        // Veri alınamadığında yapılacak işlemler
                        Toast.makeText(getContext(), "Favori etkinlikler alınamadı", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void aramaYap(ArrayList<String> docIDListesi) {
        for (String docID : docIDListesi) {
            firestore.collection("kullanici_hayvanlari")
                    .document(docID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> data = document.getData();
                                if (data != null) {

                                    String email = (String) data.get("email");
                                    String foto1 = (String) data.get("foto1");
                                    String foto2 = (String) data.get("foto2");
                                    String foto3 = (String) data.get("foto3");
                                    String foto4 = (String) data.get("foto4");
                                    String ad = (String) data.get("ad");
                                    String tur = (String) data.get("tur");
                                    String irk = (String) data.get("ırk");
                                    String cinsiyet = String.valueOf(data.get("cinsiyet"));
                                    String yas = String.valueOf(data.get("yas"));
                                    String saglik = (String) data.get("saglik");
                                    String aciklama = (String) data.get("aciklama");
                                    String kisilik = (String) data.get("kisilik");
                                    String sahipliMi = (String) data.get("sahipli_mi");
                                    String ilandaMi = (String) data.get("ilanda_mi");
                                    String enlem = (String) data.get("enlem");
                                    String boylam = (String) data.get("boylam");
                                    String sehir = (String) data.get("sehir");
                                    String ilce = (String) data.get("ilce");
                                    String docid = document.getId();

                                    Hayvan hayvan = new Hayvan(email, foto1,foto2,foto3,foto4, ad, tur, irk, cinsiyet, yas, saglik, aciklama,
                                            kisilik, docid, sahipliMi, ilandaMi,enlem,boylam,sehir,ilce);
                                    hayvanListesi.add(hayvan);

                                    // Tüm veriler alındığında adapter'ı güncelle
                                    if (hayvanListesi.size() == docIDListesi.size()) {
                                        adapter.notifyDataSetChanged();
                                        int sayac = adapter.getItemCount();
                                        String sayacS = "Sahiplendirilen Hayvan Sayısı: " + sayac;
                                        binding.textViewSahipliSayac.setText(sayacS);
                                    }

                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                // Belge bulunamadığında yapılacak işlemler
                            }
                        } else {
                            // Arama yapılırken hata oluştuğunda yapılacak işlemler
                            Toast.makeText(getContext(), "Etkinlik aranırken hata oluştu", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}