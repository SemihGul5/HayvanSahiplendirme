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
import com.sgodi.bitirmeprojesi.data.models.Hayvan;
import com.sgodi.bitirmeprojesi.databinding.FragmentProfilimBinding;

import java.util.HashMap;
import java.util.Map;


public class ProfilimFragment extends Fragment {
    private FragmentProfilimBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentProfilimBinding.inflate(inflater, container, false);
        binding.materialToolbarProfilim.setTitle("Profilim");
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        binding.textProfilEmail.setEnabled(false);
        getData();
        
        binding.buttonProfilGuncelle.setOnClickListener(view -> {
            guncelle(view);
        });
        
        binding.textViewSifreDegis.setOnClickListener(view -> {
            sifreDegis(view);
        });




        return binding.getRoot();
    }

    private void sifreDegis(View view) {
        Navigation.findNavController(view).navigate(R.id.action_profilimFragment_to_sifreDegisFragment);
    }

    private void guncelle(View view) {
        String yeniAd = binding.textProfilAd.getText().toString();
        String yeniSoyad = binding.textProfilSoyad.getText().toString();
        String yeniTel = binding.extProfilTel.getText().toString();
        String email = auth.getCurrentUser().getEmail();

        // Güncellenecek bilgileri içeren bir Map oluştur
        Map<String, Object> guncellenmisBilgiler = new HashMap<>();
        guncellenmisBilgiler.put("ad", yeniAd);
        guncellenmisBilgiler.put("soyad", yeniSoyad);
        guncellenmisBilgiler.put("tel", yeniTel);

        // Belgeyi bul ve güncelle
        firestore.collection("kullanicilar").whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Kullanıcının belgesini bulduğunuzda
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Belgeyi güncelle
                        firestore.collection("kullanicilar").document(documentSnapshot.getId())
                                .update(guncellenmisBilgiler)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Bilgiler güncellendi", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Bilgiler güncellenirken bir hata oluştu", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Belge bulunamadığında veya bir hata oluştuğunda
                    Toast.makeText(getContext(), "Belge bulunamadı veya bir hata oluştu", Toast.LENGTH_SHORT).show();
                });
    }



    private void getData() {
        String email=auth.getCurrentUser().getEmail();
        firestore.collection("kullanicilar").whereEqualTo("email",email)
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

                                binding.textProfilAd.setText(ad);
                                binding.textProfilSoyad.setText(soyad);
                                binding.textProfilEmail.setText(email);
                                binding.extProfilTel.setText(tel);


                            }
                        }
                    }
                });
    }
}