package com.sgodi.bitirmeprojesi.ui.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.Hayvan;
import com.sgodi.bitirmeprojesi.databinding.FragmentHayvanlarimBinding;
import com.sgodi.bitirmeprojesi.ui.adapters.HayvanimAdapter;

import java.util.ArrayList;
import java.util.Map;

public class HayvanlarimFragment extends Fragment {
    private FragmentHayvanlarimBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    ArrayList<Hayvan> hayvanListesi;
    HayvanimAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentHayvanlarimBinding.inflate(inflater, container, false);


        binding.floatingActionButtonHayvanlarim.setOnClickListener(view -> {
            //hayvan ekleye git
            if (hayvanListesi == null || hayvanListesi.isEmpty()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Uyarı");
                alert.setMessage("Yeni bir hayvan eklemeden önce hayvanın kişiliği ile ilgili bazı bilgiler bilmenizde fayda var. Buraya daha sonradan da ayarlardan gidebilirsiniz");
                alert.setPositiveButton("Beni oraya götür", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Navigation.findNavController(view).navigate(R.id.action_hayvanlarimFragment_to_hayvanimKisilikYonergeleriFragment);
                    }
                });
                alert.setNegativeButton("Belki sonra", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Navigation.findNavController(view).navigate(R.id.action_hayvanlarimFragment_to_mapsFragment);
                    }
                });
                Log.i("Mesaj", "hayvanListesi boş veya null");
                alert.show();
            } else {
                Navigation.findNavController(view).navigate(R.id.action_hayvanlarimFragment_to_mapsFragment);
            }


        });

        hayvanListesi=new ArrayList<>();
        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        getData();

        binding.hayvanlarimRV.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter= new HayvanimAdapter(getContext(),hayvanListesi);
        binding.hayvanlarimRV.setAdapter(adapter);


        return binding.getRoot();
    }

    private void getData() {
        String userEmail = auth.getCurrentUser().getEmail();

        firestore.collection("kullanici_hayvanlari")
                .whereEqualTo("email", userEmail)
                .orderBy("tarih", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            Log.i("Mesaj",error.getMessage());
                            return;
                        }
                        hayvanListesi.clear();

                        if (value != null) {
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                Map<String, Object> data = documentSnapshot.getData();

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
                                String docid=documentSnapshot.getId();

                                Hayvan hayvan = new Hayvan(email, foto1,foto2,foto3,foto4, ad, tur, irk, cinsiyet, yas, saglik, aciklama,
                                        kisilik,docid,sahipliMi,ilandaMi,enlem,boylam,sehir,ilce);
                                hayvanListesi.add(hayvan);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }


}