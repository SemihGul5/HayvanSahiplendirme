package com.sgodi.bitirmeprojesi.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sgodi.bitirmeprojesi.MainActivity2;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.databinding.FragmentAyarlarBinding;

import java.util.ArrayList;

public class AyarlarFragment extends Fragment {
    private FragmentAyarlarBinding binding;
    ArrayList<String>ayarlarListesi;
    ArrayAdapter<String> arrayAdapter;
    FirebaseAuth auth;
    FirebaseFirestore firestore;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentAyarlarBinding.inflate(inflater, container, false);
        binding.materialToolbarAyarlar.setTitle("Ayarlar");
        firestore=FirebaseFirestore.getInstance();
        ayarlarListesi=new ArrayList<>();
        ayarlarListesi.add("Profilim");
        ayarlarListesi.add("Kişilik Testi");
        ayarlarListesi.add("Mesajlar");
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
        } else if (secilen.equals("Kişilik Testi")) {
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
            AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
            alert.setTitle("İlanı kaldır");
            alert.setMessage("İlanı kaldırmak istediğinizden emin misiniz?");
            alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    bakiciIlanKaldir(firestore);
                    bakici_druum_guncelle(firestore);
                }
            });
            alert.show();

        } else if (secilen.equals("Mesajlar")) {
            Navigation.findNavController(view).navigate(R.id.action_ayarlarFragment_to_mesajListemFragment);


        } else{
            Toast.makeText(getContext(), "Hata", Toast.LENGTH_SHORT).show();
        }
        


    }

    private void bakiciIlanKaldir(FirebaseFirestore firestore) {
        firestore.collection("bakici")
                .whereEqualTo("email", auth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Her belgeyi silebilirsiniz
                            firestore.collection("bakici").document(document.getId()).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Silme işlemi başarılıysa yapılacak işlemleri burada gerçekleştirin
                                        Toast.makeText(getContext(),"İlanınız başarılı bir şekilde kaldırıldı",Toast.LENGTH_LONG).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Silme işlemi başarısız olursa yapılacak işlemleri burada gerçekleştirin
                                        Toast.makeText(getContext(),"İlanın silme işlemi sırasında hata meydana geldi",Toast.LENGTH_LONG).show();
                                    });
                        }
                    } else {
                        // Belge alınırken hata oluşursa yapılacak işlemleri burada gerçekleştirin
                        Toast.makeText(getContext(),"İlanın belgesine ulaşılamadı",Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void cikisYap(View view) {
        try {
            AlertDialog.Builder alert=new AlertDialog.Builder(getContext());
            alert.setTitle("Çıkıs Yap");
            alert.setMessage("Emin misiniz?");
            alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(getContext(), "Çıkış Yapılıyor.", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigate(R.id.action_ayarlarFragment_to_mainActivity);

                    auth.signOut();

                }
            });
            alert.show();
        }catch (Exception e){
            Log.i("Çıkış hatası: ",e.getMessage());
        }


    }
    private void bakici_druum_guncelle( FirebaseFirestore firebaseFirestore) {
        Query query=firebaseFirestore.collection("kullanicilar").whereEqualTo("email",auth.getCurrentUser().getEmail());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    // Belirli bir kritere uyan belgeyi güncelle
                    String userId = document.getId();
                    firebaseFirestore.collection("kullanicilar").document(userId).update("bakici_durum", "false");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Bakıcı durum güncellenemedi! "+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}