package com.sgodi.bitirmeprojesi.ui.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.databinding.FragmentBakiciBinding;

import java.util.Map;

public class BakiciFragment extends Fragment {
    private FragmentBakiciBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBakiciBinding.inflate(inflater, container, false);
        binding.toolbar2.setTitle("Bakıcılar");
        auth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance(); // Firestore bağlantısını başlat

        //bakıcı ol fragment'a gidiş
        binding.extendedFabBakiciOl.setOnClickListener(view -> {
            // Belirli bir e-postaya sahip kullanıcıyı Firestore'dan al
            String email = auth.getCurrentUser().getEmail();
            db.collection("kullanicilar").whereEqualTo("email", email)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (value != null) {
                                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                    Map<String, Object> data = documentSnapshot.getData();
                                    durumTespit(data,view);

                                }
                            }
                        }
                    });
        });













        return binding.getRoot();
    }

    private void durumTespit(Map<String, Object> data, View view) {
        String kisilik = (String) data.get("kişilik");
        String kisilik_durum = (String) data.get("kisilik_durum");
        String bakici_durum=(String) data.get("bakici_durum");
        // kisilik_durum'un null olabileceğini kontrol et
        if (kisilik_durum != null) {
            if(bakici_durum!=null){
                if (bakici_durum.equals("true")){
                    Snackbar.make(view, "Zaten bakıcı ilanınız var, kaldırmak istiyorsanız ayarlara gidiniz.", Snackbar.LENGTH_LONG).show();
                }else{
                    if ("false".equals(kisilik_durum)) {
                        Snackbar.make(view, "Lütfen kişilik testini yapınız.", Snackbar.LENGTH_LONG).show();
                    } else {
                        if ("nevrotiklik".equals(kisilik)) {
                            Snackbar.make(view, "Bakıcı olmaya uygun değilsiniz.", Snackbar.LENGTH_LONG).show();
                        } else {
                            Navigation.findNavController(view).navigate(R.id.action_bakiciFragment_to_bakiciOlFragment);
                        }
                    }
                }
            }
            else{}
        } else {}
    }
}
