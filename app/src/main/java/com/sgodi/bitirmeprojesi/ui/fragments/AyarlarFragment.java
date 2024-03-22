package com.sgodi.bitirmeprojesi.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.databinding.FragmentAnaSayfaBinding;
import com.sgodi.bitirmeprojesi.databinding.FragmentAyarlarBinding;

import java.util.ArrayList;

public class AyarlarFragment extends Fragment {
    private FragmentAyarlarBinding binding;
    ArrayList<String>ayarlarListesi;
    ArrayAdapter<String> arrayAdapter;
    FirebaseAuth auth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentAyarlarBinding.inflate(inflater, container, false);
        binding.materialToolbarAyarlar.setTitle("Ayarlar");
        ayarlarListesi=new ArrayList<>();
        ayarlarListesi.add("Profilim");
        ayarlarListesi.add("Kişilik Testi");
        ayarlarListesi.add("Bakıcı İlanımı kaldır");
        ayarlarListesi.add("Paylaş");
        ayarlarListesi.add("Bize Ulaşın");
        ayarlarListesi.add("Çıkış Yap");
        arrayAdapter=new ArrayAdapter<>(getContext(), android.R.layout.simple_expandable_list_item_1,ayarlarListesi);
        binding.listViewAyarlar.setAdapter(arrayAdapter);

        //liste Eleman Tıklanması
        binding.listViewAyarlar.setOnItemClickListener((adapterView, view, i, l) -> {
            listedenElemanTiklanmasi(adapterView,view,i,l);
        });
        auth=FirebaseAuth.getInstance();




        // geri tuşu
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




        return binding.getRoot();
    }

    private void listedenElemanTiklanmasi(AdapterView<?> adapterView, View view, int i, long l) {
        String secilen=arrayAdapter.getItem(i);
        if(secilen.equals("Profilim")) {
            Navigation.findNavController(view).navigate(R.id.action_ayarlarFragment_to_profilimFragment);
        } else if (secilen.equals("Anket")) {
            Navigation.findNavController(view).navigate(R.id.action_ayarlarFragment_to_kisilikTestFragment);
        } else if (secilen.equals("Paylaş")) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/developer?id=Abrebo+Studio");
            startActivity(Intent.createChooser(sharingIntent, "Paylaş"));

        } else if (secilen.equals("Bize Ulaşın")) {
            Navigation.findNavController(view).navigate(R.id.action_ayarlarFragment_to_bizeUlasFragment);
        } else if (secilen.equals("Çıkış Yap")) {
            cikisYap(view);
        } else if (secilen.equals("Bakıcı İlanımı kaldır")) {
            Toast.makeText(getContext(), "BAKICI İLANI KALDIRMA İŞLEMLERİ YAPILACAK", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(getContext(), "Hata", Toast.LENGTH_SHORT).show();
        }
        


    }

    private void cikisYap(View view) {

        AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
        alert.setTitle("Çıkıs Yap");
        alert.setMessage("Emin misiniz?");
        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(), "Çıkış Yapılıyor.", Toast.LENGTH_SHORT).show();
                auth.signOut();
                Navigation.findNavController(view).navigate(R.id.action_ayarlarFragment_to_girisYapFragment2);
            }
        });
        alert.show();
    }

}