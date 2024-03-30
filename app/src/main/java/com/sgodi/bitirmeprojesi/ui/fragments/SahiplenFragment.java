package com.sgodi.bitirmeprojesi.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.Hayvan;
import com.sgodi.bitirmeprojesi.databinding.FragmentSahiplenBinding;
import com.sgodi.bitirmeprojesi.ui.adapters.HayvanimAdapter;
import com.sgodi.bitirmeprojesi.ui.adapters.SahiplendirAdapter;

import java.util.ArrayList;
import java.util.Map;

public class SahiplenFragment extends Fragment {
    private FragmentSahiplenBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private ArrayList<Hayvan> hayvanListesi;
    SahiplendirAdapter adapter;
    String kisilik="";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentSahiplenBinding.inflate(inflater, container, false);
        binding.materialToolbarSahiplen.setTitle("Sahiplen");
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        hayvanListesi=new ArrayList<>();
        OnBackPressedCallback backButtonCallback = new OnBackPressedCallback(true) {
            private long backPressedTime = 0;
            @Override
            public void handleOnBackPressed() {
                long currentTime = System.currentTimeMillis();
                if (backPressedTime + 2000 > currentTime) {
                    requireActivity().finishAffinity();
                } else {
                    Toast.makeText(getContext(), "Çıkmak için tekrar basın", Toast.LENGTH_SHORT).show();
                }
                backPressedTime = currentTime;
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), backButtonCallback);

        getData();
        binding.rvSahiplendirHayvanlar.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter= new SahiplendirAdapter(getContext(),hayvanListesi);
        binding.rvSahiplendirHayvanlar.setAdapter(adapter);










        return binding.getRoot();
    }

    private void getData() {
        getKullaniciKisilik(firestore, auth, new KisilikCallback() {
            @Override
            public void onKisilikReceived(String kisilik) {

                getIlandami(firestore, auth, new ilanCallback() {
                    @Override
                    public void onIlanReceived(String ilanValue) {
                        firestore.collection("kullanici_hayvanlari")
                                .whereEqualTo("kisilik", kisilik)
                                .whereEqualTo("ilanda_mi","true")
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if (error != null) {
                                            Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        hayvanListesi.clear();

                                        if (value != null) {
                                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                                Map<String, Object> data = documentSnapshot.getData();

                                                String email = (String) data.get("email");
                                                String foto = (String) data.get("foto");
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
                                                String docid = documentSnapshot.getId();

                                                Hayvan hayvan = new Hayvan(email, foto, ad, tur, irk, cinsiyet, yas, saglik, aciklama,
                                                        kisilik, docid, sahipliMi, ilandaMi,enlem,boylam,sehir,ilce);
                                                hayvanListesi.add(hayvan);
                                            }
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }

    private void getKullaniciKisilik(FirebaseFirestore firestore, FirebaseAuth auth, KisilikCallback callback) {
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
    private void getIlandami(FirebaseFirestore firestore, FirebaseAuth auth,ilanCallback callback) {
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
                                String ilan = (String) data.get("ilanda_mi");
                                callback.onIlanReceived(ilan);
                            }
                        }
                    }
                });
    }
    public interface KisilikCallback {
        void onKisilikReceived(String kisilikValue);
    }

    public interface ilanCallback {
        void onIlanReceived(String ilanValue);
    }
}