package com.sgodi.bitirmeprojesi.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.Bakici;
import com.sgodi.bitirmeprojesi.databinding.FragmentBakiciAyrintiBinding;
import com.squareup.picasso.Picasso;

public class BakiciAyrintiFragment extends Fragment {
    private FragmentBakiciAyrintiBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentBakiciAyrintiBinding.inflate(inflater, container, false);
        binding.materialToolbarBakiciAyrinti.setTitle("Bakıcı Ayrıntı");

        BakiciAyrintiFragmentArgs bundle=BakiciAyrintiFragmentArgs.fromBundle(getArguments());
        Bakici bakici= bundle.getBakici();
        Picasso.get().load(bakici.getFoto()).into(binding.imageViewBakiciAyrintiFoto);
        binding.bakCAyrintiAD.setText(bakici.getAd()+" "+bakici.getSoyad());
        binding.bakiciAyrintiKonum.setText(bakici.getKonum());
        binding.bakiciAyrintiTelefon.setText(bakici.getTel());
        binding.bakiciAyrintiCINSIYET.setText(bakici.getCinsiyet());
        binding.kisilikAyrintiKisilik.setText(bakici.getKisilik());
        binding.hakkindaAyrintiHakkinda.setText(bakici.getAciklama());












        return binding.getRoot();
    }
}