package com.sgodi.bitirmeprojesi.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sgodi.bitirmeprojesi.databinding.FragmentKisisellestirilmisSahiplenmeAyarBinding;

public class KisisellestirilmisSahiplenmeAyarFragment extends Fragment {
    private FragmentKisisellestirilmisSahiplenmeAyarBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentKisisellestirilmisSahiplenmeAyarBinding.inflate(inflater, container, false);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Firestore'dan belirli bir koşulu karşılayan belgeleri getirme ve switch bileşenini buna göre ayarlama
        firestore.collection("kullanicilar")
                .whereEqualTo("email", auth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Boolean oneriDurumu = document.getBoolean("oneri_durum");
                            if (oneriDurumu != null && oneriDurumu) {
                                // oneri_durumu true ise switch'i açık yap
                                binding.switchKisAyar.setChecked(true);
                            } else {
                                // oneri_durumu false veya null ise switch'i kapalı yap
                                binding.switchKisAyar.setChecked(false);
                            }
                        }
                    }
                });

        // Switch değişikliklerini dinleme ve Firestore'da güncelleme
        binding.switchKisAyar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Firestore'daki oneri_durumu değerini güncelleme
            firestore.collection("kullanicilar")
                    .whereEqualTo("email", auth.getCurrentUser().getEmail())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                document.getReference().update("oneri_durum", isChecked)
                                        .addOnSuccessListener(aVoid -> {
                                            //Snackbar.make(getView(), "Başarılı", Snackbar.LENGTH_SHORT).show();
                                            // Başarılı bir şekilde güncellendiğinde bir işlem yapılabilir
                                        })
                                        .addOnFailureListener(e -> {
                                            //Snackbar.make(getView(), "Başarısız", Snackbar.LENGTH_SHORT).show();
                                            // Güncelleme başarısız olduğunda bir işlem yapılabilir
                                        });
                            }
                        }
                    });
        });

        return binding.getRoot();
    }
}
