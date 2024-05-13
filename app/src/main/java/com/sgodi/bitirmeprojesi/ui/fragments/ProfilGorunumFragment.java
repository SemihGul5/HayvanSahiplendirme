package com.sgodi.bitirmeprojesi.ui.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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
import com.sgodi.bitirmeprojesi.databinding.FragmentProfilGorunumBinding;

import java.util.Map;

public class ProfilGorunumFragment extends Fragment {
    private FragmentProfilGorunumBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private String email;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentProfilGorunumBinding.inflate(inflater, container, false);
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        email=auth.getCurrentUser().getEmail();
        getData(email,auth);

        binding.linearLayoutAyarlar.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_profilGorunumFragment_to_ayarlarFragment);
        });
        binding.linearLayoutMesajlar.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_profilGorunumFragment_to_mesajListemFragment);
        });
        binding.linearLayoutKisilikTesti.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_profilGorunumFragment_to_kisilikTestFragment);
        });
        binding.textViewGitProfilimFragment.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_profilGorunumFragment_to_profilimFragment);
        });
        binding.textViewGitSifreDegistir.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_profilGorunumFragment_to_kaydedilenlerFragment);
        });



        return binding.getRoot();
    }
    private void getData(String email, FirebaseAuth auth) {
        if (auth.getCurrentUser()!=null){
            firestore.collection("kullanicilar")
                    .whereEqualTo("email",email)
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

                                    String ad = (String) data.get("ad");
                                    String soyad = (String) data.get("soyad");
                                    String tel = (String) data.get("tel");
                                    String kisilik = (String) data.get("kişilik");

                                    String isim=ad+" "+soyad;
                                    binding.textViewKullaniciIsmi.setText(isim);
                                    binding.textViewKullaniciEmail.setText(email);
                                    binding.textViewTelBilgi.setText(tel);
                                    binding.textViewKisilikBilgi.setText(kisilik);
                                }
                            }
                        }
                    });
        }else{
            Toast.makeText(getContext(), "Oturum açılmamış", Toast.LENGTH_SHORT).show();
        }



    }
}