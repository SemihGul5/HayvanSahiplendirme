package com.sgodi.bitirmeprojesi.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.Bakici;
import com.sgodi.bitirmeprojesi.data.models.Kullanici;
import com.sgodi.bitirmeprojesi.databinding.FragmentMesajListemBinding;
import com.sgodi.bitirmeprojesi.ui.adapters.GonderilenMesajListAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class MesajListemFragment extends Fragment {
    private FragmentMesajListemBinding binding;
    GonderilenMesajListAdapter adapter;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    ArrayList<Kullanici> bakiciList;
    String gonderenEmail,aliciEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentMesajListemBinding.inflate(inflater, container, false);
        binding.materialToolbarMesajListem.setTitle("Mesajlar");
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        bakiciList=new ArrayList<>();
        adapter=new GonderilenMesajListAdapter(getContext(),bakiciList,firestore);
        binding.recyclerViewGelenMesajListesi.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewGelenMesajListesi.setAdapter(adapter);

        getData();










        return binding.getRoot();
    }

    private void getData() {
        String currentUserEmail = auth.getCurrentUser().getEmail();

        firestore.collection("mesaj")
                .orderBy("tarih", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Hata işleme
                            Toast.makeText(getContext(), "Bir şeyler ters gitti.", Toast.LENGTH_SHORT).show();
                            Log.e("Mesaj", error.getMessage());
                            return;
                        }

                        if (value != null) {
                            // Veritabanından gelen mesajları işle
                            Set<String> uniqueEmails = new HashSet<>();
                            for (DocumentSnapshot document : value.getDocuments()) {
                                String gonderenEmail = document.getString("gonderen_email");
                                String aliciEmail = document.getString("alici_email");

                                if (gonderenEmail.equals(currentUserEmail) || aliciEmail.equals(currentUserEmail)) {
                                    uniqueEmails.add(gonderenEmail.equals(currentUserEmail) ? aliciEmail : gonderenEmail);
                                }
                            }

                            // Her bir benzersiz e-posta adresi için bakıcıları al
                            for (String email : uniqueEmails) {
                                firestore.collection("kullanicilar")
                                        .whereEqualTo("email", email)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                // Firestore'dan dönen belgeleri işle
                                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                    Map<String, Object> data = documentSnapshot.getData();
                                                    String aciklama = (String) data.get("aciklama");
                                                    String ad = (String) data.get("ad");
                                                    String cinsiyet = (String) data.get("cinsiyet");
                                                    String email = (String) data.get("email");
                                                    String foto = (String) data.get("foto");
                                                    String kisilik = (String) data.get("kisilik");
                                                    String konum = (String) data.get("konum");
                                                    String soyad = (String) data.get("soyad");
                                                    String tel = (String) data.get("tel");
                                                    Boolean oneri_durum = (Boolean) data.get("oneri_durum");
                                                    Kullanici bakici=new Kullanici(ad,soyad,email,kisilik,konum,tel,aciklama,foto,cinsiyet,
                                                            oneri_durum);
                                                    bakiciList.add(bakici);
                                                }
                                                adapter.notifyDataSetChanged();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Hata işleme
                                                Toast.makeText(getContext(), "Bir şeyler ters gitti.", Toast.LENGTH_SHORT).show();
                                                Log.e("Mesaj", e.getMessage());
                                            }
                                        });
                            }
                        }
                    }
                });
    }





}