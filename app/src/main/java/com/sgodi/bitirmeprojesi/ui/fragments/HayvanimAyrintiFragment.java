package com.sgodi.bitirmeprojesi.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sgodi.bitirmeprojesi.data.models.Hayvan;
import com.sgodi.bitirmeprojesi.databinding.FragmentHayvanimAyrintiBinding;
import com.squareup.picasso.Picasso;

public class HayvanimAyrintiFragment extends Fragment {
    private FragmentHayvanimAyrintiBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Fragmentin bir menüsü olduğunu belirt
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentHayvanimAyrintiBinding.inflate(inflater, container, false);
        binding.toolbarAyrinti.setTitle("Ayrıntılar");
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        // hayvan kartına tıklandığında gelen bilgileri alınması - hayvanim adapterdan
        HayvanimAyrintiFragmentArgs bundle=HayvanimAyrintiFragmentArgs.fromBundle(getArguments());
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

        String sahipliMi= hayvan.getSahipliMi();
        String ilandaMi=hayvan.getIlandaMi();
        if(sahipliMi.equals("true")){
            binding.buttonSahiplenmeIslemi.setText("ARTIK SAHİBİM VAR");
            binding.buttonSahiplenmeIslemi.setEnabled(false);
        }else{
            if(ilandaMi.equals("false")){
                binding.buttonSahiplenmeIslemi.setText("SAHİPLENDİR");
            }else{
                binding.buttonSahiplenmeIslemi.setText("SAHİPLENDİ");
            }
        }


        binding.buttonSahiplenmeIslemi.setOnClickListener(view -> {
            String buttonText = binding.buttonSahiplenmeIslemi.getText().toString();
            if (buttonText.equals("SAHİPLENDİR")) {
                //ilana koyulacak
                ilanGuncelle(firestore,auth,hayvan,"true");
                binding.buttonSahiplenmeIslemi.setText("SAHİPLENDİ");

            } else if (buttonText.equals("SAHİPLENDİ")) {
                //Sahiplendirme işlemi tamamlandı bildirimi
                //ilandan çıkartılacak, sahiplimi=true olacak
                sahipliMiGuncelle(firestore,auth,hayvan,"true");
                ilanGuncelle(firestore,auth,hayvan,"false");
                binding.buttonSahiplenmeIslemi.setText("ARTIK SAHİBİM VAR");
                binding.buttonSahiplenmeIslemi.setEnabled(false);
            }
        });






        return binding.getRoot();
    }

    private void ilanGuncelle(FirebaseFirestore firestore, FirebaseAuth auth, Hayvan hayvan, String aTrue) {
        String email=auth.getCurrentUser().getEmail();
        firestore.collection("kullanici_hayvanlari").document(hayvan.getDocID()).update("ilanda_mi",aTrue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Başarılı", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void sahipliMiGuncelle(FirebaseFirestore firestore, FirebaseAuth auth, Hayvan hayvan, String aTrue) {
        String email=auth.getCurrentUser().getEmail();
        firestore.collection("kullanici_hayvanlari").document(hayvan.getDocID()).update("sahipli_mi",aTrue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "Başarılı", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void getKullaniciHayvan(FirebaseFirestore firestore, FirebaseAuth auth, String hayvanDocID, KullaniciHayvanCallback callback) {
        firestore.collection("kullanici_hayvanlari")
                .whereEqualTo("email", auth.getCurrentUser().getEmail())
                .whereEqualTo("docID", hayvanDocID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Belirli bir hayvanın bilgilerini al
                                String hayvanAdi = document.getString("hayvanAdi");
                                // Diğer hayvan özelliklerini de alabilirsiniz
                                // Örneğin: yaş, tür, cinsiyet vb.

                                callback.onKullaniciHayvanReceived(hayvanAdi);
                            }
                        } else {
                            Toast.makeText(getContext(), "Belirli hayvanı alma işlemi başarısız oldu: "+task.getException(), Toast.LENGTH_SHORT).show();
                            callback.onKullaniciHayvanError("Belirli hayvanı alma işlemi başarısız oldu");
                        }
                    }
                });
    }

    // Geri çağrı arayüzü
    interface KullaniciHayvanCallback {
        void onKullaniciHayvanReceived(String hayvanAdi);
        void onKullaniciHayvanError(String errorMessage);
    }


}