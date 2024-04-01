package com.sgodi.bitirmeprojesi.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.Bakici;
import com.sgodi.bitirmeprojesi.databinding.FragmentBakiciAyrintiBinding;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

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



        binding.buttonAra.setOnClickListener(view -> {
            // Telefon uygulamasını başlatmak için Intent oluşturma
            Intent intent = new Intent(Intent.ACTION_DIAL);
            // Telefon numarasını veri olarak ekleyin
            intent.setData(Uri.parse("tel:" + bakici.getTel()));
            // Telefon uygulamasını başlatın
            startActivity(intent);
        });

        binding.buttonMesaj.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_bakiciAyrintiFragment_to_mesajFragment);

        });

        binding.materialToolbarBakiciAyrinti.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==R.id.ilamBildir){

                    AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
                    alert.setTitle("Bildir");
                    alert.setMessage("Bildirmek istediğinizden emin misiniz? Gereksiz bildirimler tekrarlanırsa cezalandırılacaktır.");
                    alert.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            firestoreBildir(bakici.getEmail());
                        }
                    });
                    alert.setNegativeButton("HAYIR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    alert.show();



                }
                return false;
            }
        });











        return binding.getRoot();
    }

    private void firestoreBildir(String email) {
        // Firestore bağlantısını başlat
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Bildiriler koleksiyonuna referans oluştur
        CollectionReference bildirilerCollectionRef = db.collection("bildiriler");

        // Yeni bir belge referansı oluştur
        DocumentReference yeniBelgeRef = bildirilerCollectionRef.document();

        // Belgeye verileri ekle
        Map<String, Object> bildiriVerileri = new HashMap<>();
        bildiriVerileri.put("email", email);
        bildiriVerileri.put("docID", "");

        // Belgeyi Firestore'a ekle
        yeniBelgeRef.set(bildiriVerileri)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Başarıyla eklendiğinde yapılacak işlemler
                        Toast.makeText(getContext(), "Bildiri başarıyla eklendi", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Ekleme işlemi başarısız olduğunda yapılacak işlemler
                        Toast.makeText(getContext(), "Bildiri eklenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}