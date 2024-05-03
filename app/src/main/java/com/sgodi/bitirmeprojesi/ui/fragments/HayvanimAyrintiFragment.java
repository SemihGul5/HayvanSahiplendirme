package com.sgodi.bitirmeprojesi.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
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

import java.util.ArrayList;

public class HayvanimAyrintiFragment extends Fragment {
    private FragmentHayvanimAyrintiBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String enlem,boylam;
    ArrayList<SlideModel>slideModels;
    private String foto1,foto2,foto3,foto4;
    
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
        binding.buttonSahiplenmeIslemiGeriAl.setVisibility(View.INVISIBLE);
        // hayvan kartına tıklandığında gelen bilgileri alınması - hayvanim adapterdan
        HayvanimAyrintiFragmentArgs bundle=HayvanimAyrintiFragmentArgs.fromBundle(getArguments());
        Hayvan hayvan= bundle.getHayvan();
        foto1=hayvan.getFoto1();
        foto2=hayvan.getFoto2();
        foto3=hayvan.getFoto3();
        foto4=hayvan.getFoto4();
        slideModels=new ArrayList<>();
        SlideModel slideModel1=new SlideModel(foto1, ScaleTypes.FIT);
        slideModels.add(slideModel1);
        if (!"null".equals(foto2)) {
            SlideModel slideModel2 = new SlideModel(foto2, ScaleTypes.FIT);
            slideModels.add(slideModel2);
        }

        if (!"null".equals(foto3)) {
            SlideModel slideModel3 = new SlideModel(foto3, ScaleTypes.FIT);
            slideModels.add(slideModel3);
        }

        if (!"null".equals(foto4)) {
            SlideModel slideModel4 = new SlideModel(foto4, ScaleTypes.FIT);
            slideModels.add(slideModel4);
        }

        binding.imageSlider.setImageList(slideModels,ScaleTypes.FIT);

        //Picasso.get().load(hayvan.getFoto1()).into(binding.imageViewHayvanimAyrinti);
        binding.hayvanAyrintiAD.setText(hayvan.getAd());
        binding.hayvanAyrintiTUR.setText(hayvan.getTur());
        binding.hayvanAyrintiIRK.setText(hayvan.getIrk());
        binding.hayvanAyrintiCINSIYET.setText(hayvan.getCinsiyet());
        binding.hayvanAyrintiYAS.setText(hayvan.getYas());
        binding.hayvanAyrintiSAGLIK.setText(hayvan.getSaglik());
        binding.hayvanAyrintiKISILIK.setText(hayvan.getKisilik());
        binding.hayvanAyrintiHAKKINDA.setText(hayvan.getAciklama());
        enlem=hayvan.getEnlem();
        boylam=hayvan.getBoylam();
        binding.hayvanAyrintiSehir.setText(hayvan.getSehir()+" / "+hayvan.getIlce());

        String sahipliMi= hayvan.getSahipliMi();
        String ilandaMi=hayvan.getIlandaMi();
        if(sahipliMi.equals("true")){
            binding.buttonSahiplenmeIslemi.setText("ARTIK SAHİBİM VAR");
            binding.buttonSahiplenmeIslemiGeriAl.setVisibility(View.INVISIBLE);
            binding.buttonSahiplenmeIslemi.setEnabled(false);
        }else{
            if(ilandaMi.equals("false")){
                binding.buttonSahiplenmeIslemi.setText("SAHİPLENDİR");
            }else{
                binding.buttonSahiplenmeIslemi.setText("SAHİPLENDİ");
                binding.buttonSahiplenmeIslemiGeriAl.setVisibility(View.VISIBLE);
            }
        }


        binding.buttonSahiplenmeIslemi.setOnClickListener(view -> {
            String buttonText = binding.buttonSahiplenmeIslemi.getText().toString();
            if (buttonText.equals("SAHİPLENDİR")) {
                //ilana koyulacak
                ilanGuncelle("kullanici_hayvanlari",firestore,auth,hayvan,"true");
                //ilanGuncelle("kullanici_hayvanlari_sahiplendir",firestore,auth,hayvan,"true");
                binding.buttonSahiplenmeIslemi.setText("SAHİPLENDİ");
                binding.buttonSahiplenmeIslemiGeriAl.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Artık sahiplendir sayfasında bulabilirsiniz.", Toast.LENGTH_SHORT).show();

            } else if (buttonText.equals("SAHİPLENDİ")) {
                //Sahiplendirme işlemi tamamlandı bildirimi
                //ilandan çıkartılacak, sahiplimi=true olacak
                sahipliMiGuncelle("kullanici_hayvanlari",firestore,auth,hayvan,"true");
                //sahipliMiGuncelle("kullanici_hayvanlari_sahiplendir",firestore,auth,hayvan,"true");
                ilanGuncelle("kullanici_hayvanlari",firestore,auth,hayvan,"false");
                //ilanGuncelle("kullanici_hayvanlari_sahiplendir",firestore,auth,hayvan,"false");
                binding.buttonSahiplenmeIslemi.setText("ARTIK SAHİBİM VAR");
                binding.buttonSahiplenmeIslemiGeriAl.setVisibility(View.INVISIBLE);
                binding.buttonSahiplenmeIslemi.setEnabled(false);
            }
        });

        binding.buttonSahiplenmeIslemiGeriAl.setOnClickListener(view -> {
            ilanGuncelle("kullanici_hayvanlari",firestore,auth,hayvan,"false");
            Toast.makeText(getContext(), "Sahiplendir sayfasından kaldırıldı", Toast.LENGTH_SHORT).show();
            binding.buttonSahiplenmeIslemi.setText("SAHİPLENDİR");
            binding.buttonSahiplenmeIslemiGeriAl.setVisibility(View.INVISIBLE);
        });

        //hayvan konumunu aç
        binding.hayvanAyrintiKonum.setOnClickListener(view -> {
            HayvanimAyrintiFragmentDirections.ActionHayvanimAyrintiFragmentToMapsFragmentHayvanimAyrinti gecis=
                    HayvanimAyrintiFragmentDirections.actionHayvanimAyrintiFragmentToMapsFragmentHayvanimAyrinti(enlem,boylam);
            Navigation.findNavController(view).navigate(gecis);

        });




        return binding.getRoot();
    }

    private void ilanGuncelle(String collectionPath,FirebaseFirestore firestore, FirebaseAuth auth, Hayvan hayvan, String aTrue) {
        String email=auth.getCurrentUser().getEmail();
        firestore.collection(collectionPath).document(hayvan.getDocID()).update("ilanda_mi",aTrue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(), "İlan güncelleme", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(),e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void sahipliMiGuncelle(String collectionPath,FirebaseFirestore firestore, FirebaseAuth auth, Hayvan hayvan, String aTrue) {
        String email=auth.getCurrentUser().getEmail();
        firestore.collection(collectionPath).document(hayvan.getDocID()).update("sahipli_mi",aTrue)
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