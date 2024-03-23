package com.sgodi.bitirmeprojesi.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.Hayvan;
import com.sgodi.bitirmeprojesi.databinding.FragmentSahiplendirAyrintiBinding;
import com.squareup.picasso.Picasso;

public class SahiplendirAyrintiFragment extends Fragment {
    private FragmentSahiplendirAyrintiBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentSahiplendirAyrintiBinding.inflate(inflater, container, false);
        binding.materialToolbarSahiplendirAyrinti.setTitle("Ayrıntı");

        SahiplendirAyrintiFragmentArgs bundle=SahiplendirAyrintiFragmentArgs.fromBundle(getArguments());
        Hayvan hayvan= bundle.getHayvan();

        Picasso.get().load(hayvan.getFoto()).into(binding.imageViewHayvanimAyrinti);
        binding.hayvanAyrintiAD.setText(hayvan.getAd());
        binding.hayvanAyrintiTUR.setText(hayvan.getTur());
        binding.hayvanAyrintiIRK.setText(hayvan.getIrk());
        binding.hayvanAyrintiCINSIYET.setText(hayvan.getCinsiyet());
        binding.hayvanAyrintiYAS.setText(hayvan.getYas());
        binding.hayvanAyrintiSAGLIK.setText(hayvan.getSaglik());
        binding.hayvanAyrintiKISILIK.setText(hayvan.getKisilik());
        binding.hayvanAyrintiHAKKINDA.setText(hayvan.getAciklama());











        return binding.getRoot();
    }
}