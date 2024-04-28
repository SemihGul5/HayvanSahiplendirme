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





        return binding.getRoot();
    }
}
