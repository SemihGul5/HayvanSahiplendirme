package com.sgodi.bitirmeprojesi.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sgodi.bitirmeprojesi.MainActivity2;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.AyarlarIcerik;
import com.sgodi.bitirmeprojesi.databinding.FragmentAyarlarBinding;
import com.sgodi.bitirmeprojesi.ui.adapters.AnasayfaIcerikAdapter;
import com.sgodi.bitirmeprojesi.ui.adapters.AyarlarAdapter;

import java.util.ArrayList;

public class AyarlarFragment extends Fragment {
    private FragmentAyarlarBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private ArrayList<AyarlarIcerik> ayarlarList;
    private AyarlarAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentAyarlarBinding.inflate(inflater, container, false);

        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        ayarlarList=new ArrayList<>();



        AyarlarIcerik profilim=new AyarlarIcerik("Profilim",false);
        AyarlarIcerik kisilikTesti=new AyarlarIcerik("Kişilik Testi",false);
        AyarlarIcerik mesajlar=new AyarlarIcerik("Mesajlar",false);
        AyarlarIcerik bakiciIlan=new AyarlarIcerik("Bakıcı İlanımı Kaldır",false);
        AyarlarIcerik oneri=new AyarlarIcerik("Kişiselleştirilmiş Öneriler",true);
        AyarlarIcerik kaydedilenler=new AyarlarIcerik("Kaydedilen Hayvanlar",false);
        AyarlarIcerik hayvanKisilikleri=new AyarlarIcerik("Hayvan Kişilikleri",false);
        AyarlarIcerik paylas=new AyarlarIcerik("Uygulamayı Paylaş",false);
        AyarlarIcerik ulas=new AyarlarIcerik("Bize Ulaşın",false);
        AyarlarIcerik cikis=new AyarlarIcerik("Çıkış Yap",false);

        ayarlarList.add(profilim);
        ayarlarList.add(kisilikTesti);
        ayarlarList.add(mesajlar);
        ayarlarList.add(bakiciIlan);
        ayarlarList.add(oneri);
        ayarlarList.add(kaydedilenler);
        ayarlarList.add(hayvanKisilikleri);
        ayarlarList.add(paylas);
        ayarlarList.add(ulas);
        ayarlarList.add(cikis);

        adapter = new AyarlarAdapter(getContext(), ayarlarList,firestore,auth);
        binding.rvAyarlar.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAyarlar.setAdapter(adapter);








        return binding.getRoot();
    }




}