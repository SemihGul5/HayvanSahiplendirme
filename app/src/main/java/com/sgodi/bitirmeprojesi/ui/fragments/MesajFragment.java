package com.sgodi.bitirmeprojesi.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.Bakici;
import com.sgodi.bitirmeprojesi.data.models.Kullanici;
import com.sgodi.bitirmeprojesi.data.models.Mesaj;
import com.sgodi.bitirmeprojesi.databinding.FragmentMesajBinding;
import com.sgodi.bitirmeprojesi.ui.adapters.MesajAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MesajFragment extends Fragment {
    private FragmentMesajBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    String gonderen_ad;
    ArrayList<Mesaj> mesajArrayList;
    MesajAdapter mAdapter;
    Kullanici Kullanici;
    boolean isAtBottom=false;
    String mesajId,alici_ad,alici_email;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    // Tüm mesajları otomatik olarak okundu olarak işaretle
    private void markAllMessagesAsRead(String mesajId) {
        for (Mesaj mesaj : mesajArrayList) {
            if (mesaj.getOkunduMu().equals("false")) {
                firestore.collection("mesaj").document(mesajId)
                        .update("okunduMu", "true")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Mesaj başarıyla güncellendiğinde yapılacak işlemler
                                Log.d("Mesaj", "Mesaj okundu olarak işaretlendi.");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Hata durumunda yapılacak işlemler
                                Log.e("Mesaj", "Mesaj okundu olarak işaretlenirken hata oluştu: " + e.getMessage());
                            }
                        });
            }
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentMesajBinding.inflate(inflater, container, false);

        //binding.recyclerViewKisiselMesaj.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true));
        //adapter=new MesajAdapter(getContext(),mesajArrayList,auth.getCurrentUser().getEmail());
        //binding.recyclerViewKisiselMesaj.setAdapter(adapter);
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        mesajArrayList=new ArrayList<>();
        MesajFragmentArgs bundle=MesajFragmentArgs.fromBundle(getArguments());
        Kullanici= bundle.getKullanici();

        if (Kullanici!=null){
            binding.toolbarOzelMesaj.setTitle(Kullanici.getAd()+" "+Kullanici.getSoyad());

            getData(Kullanici);
            /*if (mesajId!=null){
                markAllMessagesAsRead(mesajId);
            }*/
            initRecyclerView();


            alici_ad= Kullanici.getAd()+" "+Kullanici.getSoyad();
            alici_email=Kullanici.getEmail();
        }



        getKullaniciAd(firestore, auth, new AdCallback() {
            @Override
            public void onAdReceived(String adValue) {
                gonderen_ad=adValue;
            }
        });



        String gonderen_email=auth.getCurrentUser().getEmail();

        String okunduMu="false";

        binding.buttonMesajGonder.setOnClickListener(view -> {

            HashMap<String,Object> gonder=new HashMap<>();
            String mesaj=binding.mesajText.getText().toString();
            gonder.put("alici_ad",alici_ad);
            gonder.put("alici_email",alici_email);
            gonder.put("gonderen_ad",gonderen_ad);
            gonder.put("gonderen_email",gonderen_email);
            gonder.put("mesaj",mesaj);
            gonder.put("okunduMu",okunduMu);
            gonder.put("tarih", FieldValue.serverTimestamp());

            Calendar calendar = Calendar.getInstance();
            int saat = calendar.get(Calendar.HOUR_OF_DAY);
            int dakika = calendar.get(Calendar.MINUTE);

            // Saati ve dakikayı string formatına dönüştür
            String zaman = String.format(Locale.getDefault(), "%02d:%02d", saat, dakika);

            // Saat ve dakikayı HashMap'e ekle
            gonder.put("saat", zaman);

            firestore.collection("mesaj").add(gonder).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    binding.mesajText.setText("");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.mesajText.setText("");
                }
            });



        });








        return binding.getRoot();
    }

    private void initRecyclerView() {
        mAdapter = new MesajAdapter(getContext(),mesajArrayList,auth.getCurrentUser().getEmail());
        binding.recyclerViewKisiselMesaj.setAdapter(mAdapter);

        //Creates layout manager and makes it feed new RecyclerView views from the bottom


        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);

        //Makes RecyclerView scroll to bottom when notifyItemInserted is called from adapter and RecyclerView is already at bottom

        RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);


                if (isAtBottom) {
                    binding.recyclerViewKisiselMesaj.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                }
            }
        };

        //Adds logic to see if RecyclerView is at bottom or not

        binding.recyclerViewKisiselMesaj.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    isAtBottom = true;
                } else {
                    isAtBottom = false;
                }
            }
        });

        //Assigns observer to adapter and LayoutManager to RecyclerView

        mAdapter.registerAdapterDataObserver(observer);
        binding.recyclerViewKisiselMesaj.setLayoutManager(linearLayoutManager);

    }
    private void getData(Kullanici Kullanici) {
        firestore.collection("mesaj")
                .orderBy("tarih", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            //Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.i("Mesaj",error.getMessage());
                        }else {
                            if (value != null) {
                                if (value.isEmpty()) {
                                    Toast.makeText(getContext(), "Mesaj Yok", Toast.LENGTH_SHORT).show();
                                } else {
                                    mesajArrayList.clear();


                                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                        Map<String, Object> data = documentSnapshot.getData();
                                        String alici_email = (String) data.get("alici_email");
                                        String gonderen_email = (String) data.get("gonderen_email");
                                        String email=auth.getCurrentUser().getEmail();

                                        if (gonderen_email.equals(Kullanici.getEmail()) && alici_email.equals(email)
                                                ||gonderen_email.equals(email)&&alici_email.equals(Kullanici.getEmail())){
                                            String alici_ad = (String) data.get("alici_ad");
                                            String gonderen_ad = (String) data.get("gonderen_ad");
                                            String mesaj = (String) data.get("mesaj");
                                            String okunduMu = (String) data.get("okunduMu");
                                            String saat = (String) data.get("saat");
                                            mesajId = documentSnapshot.getId();
                                            Mesaj mesaj1=new Mesaj(alici_ad,alici_email,gonderen_ad,gonderen_email,mesaj,okunduMu,saat,mesajId);
                                            mesajArrayList.add(mesaj1);
                                        }

                                    }
                                    mAdapter.notifyDataSetChanged();
                                    binding.recyclerViewKisiselMesaj.scrollToPosition(mesajArrayList.size() - 1);

                                }
                            }
                        }
                    }
                });
    }

    private void getKullaniciAd(FirebaseFirestore firestore, FirebaseAuth auth, MesajFragment.AdCallback callback) {
        firestore.collection("kullanicilar")
                .whereEqualTo("email", auth.getCurrentUser().getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Hata işleme
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (value != null) {
                            for (DocumentSnapshot document : value.getDocuments()) {
                                String ad = document.getString("ad");
                                String soyad=document.getString("soyad");
                                String adim=ad+" "+soyad;
                                callback.onAdReceived(adim);
                            }
                        }
                    }
                });
    }

    // Geri arama (callback) arayüzü
    interface AdCallback {
        void onAdReceived(String adValue);
    }
}