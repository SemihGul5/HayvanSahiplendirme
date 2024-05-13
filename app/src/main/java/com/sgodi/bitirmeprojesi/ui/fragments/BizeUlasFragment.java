package com.sgodi.bitirmeprojesi.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.databinding.FragmentBizeUlasBinding;

public class BizeUlasFragment extends Fragment {
    private FragmentBizeUlasBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentBizeUlasBinding.inflate(inflater, container, false);

        binding.imageView4.setImageResource(R.drawable.ml);
        binding.imageView5.setImageResource(R.drawable.ig);
        binding.imageView6.setImageResource(R.drawable.tw);

        binding.imageView4.setOnClickListener(view -> {
            Toast.makeText(getContext(), "Gmail", Toast.LENGTH_SHORT).show();
        });
        binding.imageView5.setOnClickListener(view -> {
            Toast.makeText(getContext(), "İnstagram", Toast.LENGTH_SHORT).show();
        });
        binding.imageView6.setOnClickListener(view -> {
            Toast.makeText(getContext(), "Twitter", Toast.LENGTH_SHORT).show();
        });










        return binding.getRoot();
    }
}