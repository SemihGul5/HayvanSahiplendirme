package com.sgodi.bitirmeprojesi.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.databinding.FragmentKayitOlBinding;

import java.util.HashMap;


public class KayitOlFragment extends Fragment {
    private FragmentKayitOlBinding binding;
    private String ad, soyad, email, sifre, sifreTekrar;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentKayitOlBinding.inflate(inflater, container, false);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Giriş yap ekranına gidiş
        binding.kayitOlGirisYapButton.setOnClickListener(view -> {
            Navigation.findNavController(view).navigate(R.id.action_kayitOlFragment_to_girisYapFragment);
        });

        // Kayıt ol butonuna tıklandığında çalışacak metodu çağırıyoruz
        kayitOlButtonTiklandi();

        return binding.getRoot();
    }

    private void kayitOlButtonTiklandi() {
        binding.kayitOlKayitOlButton.setOnClickListener(view -> {
            binding.progressBarKayitOl.setVisibility(View.VISIBLE);
            ad = binding.kayitOlAdText.getText().toString();
            soyad = binding.kayitOlSoyAdText.getText().toString();
            email = binding.kayitOlEMailText.getText().toString();
            sifre = binding.kayitOlSifreText.getText().toString();
            sifreTekrar = binding.kayitOlSifreTekrarText.getText().toString();

            if (ad.isEmpty() || soyad.isEmpty() || email.isEmpty() || sifre.isEmpty() || sifreTekrar.isEmpty()) {
                Snackbar.make(getView(), "Tüm alanları doldurun", Snackbar.LENGTH_SHORT).show();
                binding.progressBarKayitOl.setVisibility(View.GONE);
            } else if (!sifre.equals(sifreTekrar)) {
                Snackbar.make(getView(), "Şifreler aynı olmalıdır", Snackbar.LENGTH_SHORT).show();
                binding.progressBarKayitOl.setVisibility(View.GONE);
            } else {
                kullaniciyiKaydet(ad, soyad, email, sifre, view);
            }
        });
    }

    private void kullaniciyiKaydet(String ad, String soyad, String email, String sifre, View view) {
        auth.createUserWithEmailAndPassword(email, sifre).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                binding.progressBarKayitOl.setVisibility(View.GONE); // İşlem tamamlandığında progress bar gizle
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser(); // Oluşturulan kullanıcıyı al
                    aktivasyonEmailiGonder(user, view);
                    kullaniciyiFirestoreKaydet(ad, soyad, email);
                    auth.signOut();
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getContext(), "E-posta zaten kayıtlı", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void kullaniciyiFirestoreKaydet(String ad, String soyad, String email) {
        HashMap<String, Object> data = new HashMap<>();
        StringBuffer ad2 = new StringBuffer(ad);
        ad2.setCharAt(0, Character.toUpperCase(ad2.charAt(0)));
        String adson = ad2.toString();

        StringBuffer soyad2 = new StringBuffer(soyad);
        soyad2.setCharAt(0, Character.toUpperCase(soyad2.charAt(0)));
        String soyad3 = soyad2.toString();

        data.put("ad", adson);
        data.put("soyad", soyad3);
        data.put("email", email);
        data.put("tel", "null");
        data.put("kişilik", "null");
        data.put("konum", "null");
        data.put("aciklama", "null");
        data.put("kisilik_durum", "true");
        data.put("bakici_durum", "false");
        data.put("oneri_durum", true);
        firestore.collection("kullanicilar").add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                // Kullanıcı başarıyla kaydedildiğinde yapılacak işlemler
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void aktivasyonEmailiGonder(FirebaseUser user, View view) {
        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(), "Email gönderildi, hesabınızı doğrulayın.", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}