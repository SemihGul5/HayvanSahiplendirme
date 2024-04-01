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
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.Bakici;
import com.sgodi.bitirmeprojesi.data.models.Mesaj;
import com.sgodi.bitirmeprojesi.databinding.FragmentMesajBinding;
import com.sgodi.bitirmeprojesi.ui.adapters.MesajAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MesajFragment extends Fragment {
    private FragmentMesajBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    String gonderen_ad;
    ArrayList<Mesaj> mesajArrayList;
    MesajAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentMesajBinding.inflate(inflater, container, false);
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        mesajArrayList=new ArrayList<>();
        binding.recyclerViewKisiselMesaj.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new MesajAdapter(getContext(),mesajArrayList,auth.getCurrentUser().getEmail());
        binding.recyclerViewKisiselMesaj.setAdapter(adapter);

        MesajFragmentArgs bundle=MesajFragmentArgs.fromBundle(getArguments());
        Bakici bakici= bundle.getBakici();

        String alici_ad= bakici.getAd()+" "+bakici.getSoyad();
        String alici_email=bakici.getEmail();

        getKullaniciAd(firestore, auth, new AdCallback() {
            @Override
            public void onAdReceived(String adValue) {
                gonderen_ad=adValue;
            }
        });



        String gonderen_email=auth.getCurrentUser().getEmail();

        String okunduMu="false";

        binding.buttonMesajGonder.setOnClickListener(view -> {

            HashMap<String,Object> gonder=new HashMap<>();
            String mesaj=binding.mesajText.getText().toString();
            gonder.put("alici_ad",alici_ad);
            gonder.put("alici_email",alici_email);
            gonder.put("gonderen_ad",gonderen_ad);
            gonder.put("gonderen_email",gonderen_email);
            gonder.put("mesaj",mesaj);
            gonder.put("okunduMu",okunduMu);
            gonder.put("tarih", FieldValue.serverTimestamp());

            firestore.collection("mesaj").add(gonder).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    binding.mesajText.setText("");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.mesajText.setText("");
                }
            });



        });



        getData(bakici);




        return binding.getRoot();
    }

    private void getData(Bakici bakici) {
        firestore.collection("mesaj").whereEqualTo("alici_email",bakici.getEmail())
                .orderBy("tarih", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            //Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.i("Mesaj",error.getMessage());
                        }else {
                            if (value != null) {
                                if (value.isEmpty()) {
                                    Toast.makeText(getContext(), "Mesaj Yok", Toast.LENGTH_SHORT).show();
                                } else {
                                    mesajArrayList.clear();
                                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                        Map<String, Object> data = documentSnapshot.getData();

                                        String alici_ad = (String) data.get("alici_ad");
                                        String alici_email = (String) data.get("alici_email");
                                        String gonderen_ad = (String) data.get("gonderen_ad");
                                        String gonderen_email = (String) data.get("gonderen_email");
                                        String mesaj = (String) data.get("mesaj");
                                        String okunduMu = (String) data.get("okunduMu");

                                        Mesaj mesaj1=new Mesaj(alici_ad,alici_email,gonderen_ad,gonderen_email,mesaj,okunduMu);
                                        mesajArrayList.add(mesaj1);


                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });
    }

    private void getKullaniciAd(FirebaseFirestore firestore, FirebaseAuth auth, MesajFragment.AdCallback callback) {
        firestore.collection("kullanicilar")
                .whereEqualTo("email", auth.getCurrentUser().getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Hata işleme
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (value != null) {
                            for (DocumentSnapshot document : value.getDocuments()) {
                                String ad = document.getString("ad");
                                String soyad=document.getString("soyad");
                                String adim=ad+" "+soyad;
                                callback.onAdReceived(adim);
                            }
                        }
                    }
                });
    }

    // Geri arama (callback) arayüzü
    interface AdCallback {
        void onAdReceived(String adValue);
    }
}