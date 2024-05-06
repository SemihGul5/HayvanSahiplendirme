package com.sgodi.bitirmeprojesi.ui.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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
import com.sgodi.bitirmeprojesi.data.models.AnasayfaIcerik;
import com.sgodi.bitirmeprojesi.data.models.Hayvan;
import com.sgodi.bitirmeprojesi.databinding.FragmentAnaSayfaBinding;
import com.sgodi.bitirmeprojesi.ui.adapters.AnasayfaIcerikAdapter;
import com.sgodi.bitirmeprojesi.ui.adapters.HayvanimAdapter;

import java.util.ArrayList;
import java.util.Map;

public class AnaSayfaFragment extends Fragment {
    private FragmentAnaSayfaBinding binding;
    private FirebaseFirestore firestore; // Firestore nesnesini sınıf alanı olarak tanımla
    private ArrayList<Hayvan> hayvanListesi;
    private AnasayfaIcerikAdapter adapter;
    private ArrayList<AnasayfaIcerik> icerikArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAnaSayfaBinding.inflate(inflater, container, false);
        binding.toolbar.setTitle("AnaSayfa");
        geriTusuIslemleri();

        // Firestore nesnesini başlat
        firestore = FirebaseFirestore.getInstance();

        //bakıcı sayfasına gidiş
        binding.imageViewBakici.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_anaSayfaFragment_to_bakiciFragment);
        });

        //hayvanlarım sayfasına gidiş
        binding.imageViewHayvanlarim.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_anaSayfaFragment_to_hayvanlarimFragment);
        });
        binding.imageViewSahiplendirilenler.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_anaSayfaFragment_to_artikSahibiVarFragment);
        });

        getIcerik();
        icerikArrayList = new ArrayList<>();

        binding.recyclerAnasayfa.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new AnasayfaIcerikAdapter(getContext(), icerikArrayList);
        binding.recyclerAnasayfa.setAdapter(adapter);

        return binding.getRoot();
    }

    private void getIcerik() {
        firestore.collection("anasayfa_icerik")
                .orderBy("siralama")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        icerikArrayList.clear();

                        if (value != null) {
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                Map<String, Object> data = documentSnapshot.getData();

                                String foto = (String) data.get("foto");
                                String baslik = (String) data.get("baslik");
                                String icerik = (String) data.get("icerik");
                                long siralamaLong = (long) data.get("siralama");
                                int siralama = (int) siralamaLong;

                                AnasayfaIcerik icerik1= new AnasayfaIcerik(foto,baslik,icerik,siralama);
                                icerikArrayList.add(icerik1);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void geriTusuIslemleri() {
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
    }
}
