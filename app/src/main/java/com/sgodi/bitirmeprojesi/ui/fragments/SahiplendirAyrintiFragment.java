package com.sgodi.bitirmeprojesi.ui.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.sgodi.bitirmeprojesi.data.models.Hayvan;
import com.sgodi.bitirmeprojesi.databinding.FragmentSahiplendirAyrintiBinding;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class SahiplendirAyrintiFragment extends Fragment {
    private FragmentSahiplendirAyrintiBinding binding;

    @SuppressLint("ResourceType")
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
        binding.hayvanAyrintiSehir.setText(hayvan.getSehir()+" / "+hayvan.getIlce());

        binding.hayvanAyrintiKonum.setOnClickListener(view -> {
            SahiplendirAyrintiFragmentDirections.ActionSahiplendirAyrintiFragmentToMapsFragmentSahiplenAyrinti gecis=
                    SahiplendirAyrintiFragmentDirections.actionSahiplendirAyrintiFragmentToMapsFragmentSahiplenAyrinti(hayvan.getEnlem(), hayvan.getBoylam());
            Navigation.findNavController(view).navigate(gecis);
        });



        binding.materialToolbarSahiplendirAyrinti.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==R.id.ilamBildir){

                    AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
                    alert.setTitle("Bildir");
                    alert.setMessage("Bildirmek istediğinizden emin misiniz? Gereksiz bildirimler tekrarlanırsa cezalandırılacaktır.");
                    alert.setPositiveButton("EVET", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            firestoreBildir(hayvan.getEmail(),hayvan.getDocID());
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

    private void firestoreBildir(String email, String docID) {
        // Firestore bağlantısını başlat
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Bildiriler koleksiyonuna referans oluştur
        CollectionReference bildirilerCollectionRef = db.collection("bildiriler");

        // Yeni bir belge referansı oluştur
        DocumentReference yeniBelgeRef = bildirilerCollectionRef.document();

        // Belgeye verileri ekle
        Map<String, Object> bildiriVerileri = new HashMap<>();
        bildiriVerileri.put("email", email);
        bildiriVerileri.put("docID", docID);

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