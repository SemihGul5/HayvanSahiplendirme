package com.sgodi.bitirmeprojesi.ui.fragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.databinding.FragmentAnaSayfaBinding;

public class AnaSayfaFragment extends Fragment {
    private FragmentAnaSayfaBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentAnaSayfaBinding.inflate(inflater, container, false);
        binding.toolbar.setTitle("AnaSayfa");
        geriTusuIslemleri();

        //bakıcı sayfasına gidiş
        binding.imageViewBakici.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_anaSayfaFragment_to_bakiciFragment);
        });

        //hayvanlarım sayfasına gidiş
        binding.imageViewHayvanlarim.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_anaSayfaFragment_to_hayvanlarimFragment);
        });




        return binding.getRoot();
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