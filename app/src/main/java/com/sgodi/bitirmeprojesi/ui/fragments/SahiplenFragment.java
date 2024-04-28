package com.sgodi.bitirmeprojesi.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.Hayvan;
import com.sgodi.bitirmeprojesi.databinding.FragmentSahiplenBinding;
import com.sgodi.bitirmeprojesi.ui.adapters.HayvanimAdapter;
import com.sgodi.bitirmeprojesi.ui.adapters.SahiplendirAdapter;

import java.util.ArrayList;
import java.util.Map;

public class SahiplenFragment extends Fragment {
    private FragmentSahiplenBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private ArrayList<Hayvan> hayvanListesi;
    SahiplendirAdapter adapter;
    String secilenSehir="",secilenCinsiyet="";
    Boolean oneri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentSahiplenBinding.inflate(inflater, container, false);
        binding.materialToolbarSahiplen.setTitle("Sahiplen");
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        hayvanListesi=new ArrayList<>();

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
        getKullaniciOneriDurum();

        binding.rvSahiplendirHayvanlar.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter= new SahiplendirAdapter(getContext(),hayvanListesi);
        binding.rvSahiplendirHayvanlar.setAdapter(adapter);

        binding.imageViewsahiplenfiltre.setOnClickListener(view -> {
            bottomDialogShow();
        });

        binding.imageViewTumHayvanlarPin.setOnClickListener(view -> {
            gitTumHayvanlar(view);
        });









        return binding.getRoot();
    }

    private void getKullaniciOneriDurum() {
        firestore.collection("kullanicilar")
                .whereEqualTo("email", auth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Boolean oneriDurumu = document.getBoolean("oneri_durum");
                            if (oneriDurumu != null && oneriDurumu) {
                                oneri = true;
                            } else {
                                oneri = false;
                            }
                            // Öneri durumu alındıktan sonra gerekli işlemleri yapmak için burada çağırabilirsiniz
                            if (oneri) {
                                getData("","");
                            } else {
                                getDataKisilikYok("","");
                            }
                        }
                    }
                });
    }


    private void gitTumHayvanlar(View view) {
        Navigation.findNavController(view).navigate(R.id.action_sahiplenFragment_to_mapsFragmentTumHayvanlarPin);
    }

    private void bottomDialogShow() {
        Dialog dialog=new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_dialog);

        TextView sehir=dialog.findViewById(R.id.sehirText);
        TextView cinsiyet=dialog.findViewById(R.id.cinsiyetText);

        sehir.setOnClickListener(view -> {
            dialog.dismiss();
            Dialog dialog2=new Dialog(getContext());
            dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog2.setContentView(R.layout.bottom_sheet_dialog_sehir);

            ListView listView = dialog2.findViewById(R.id.bottomSheetSehirlerList);
            sehirBaslat(listView); // Liste görünümünü ayarlamak için metodu çağırın
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedCity = (String) adapterView.getItemAtPosition(i);
                    secilenSehir=selectedCity;

                    dialog2.dismiss();
                    dialog.show();
                    ((TextView) dialog.findViewById(R.id.secilenSehirText)).setText(secilenSehir);
                }
            });

            dialog2.show();
            dialog2.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog2.getWindow().setGravity(Gravity.BOTTOM);
        });

        cinsiyet.setOnClickListener(view -> {
            dialog.dismiss();
            Dialog dialog3=new Dialog(getContext());
            dialog3.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog3.setContentView(R.layout.bottom_sheet_dialog_cinsiyet);
            ((TextView)dialog3.findViewById(R.id.kadinText)).setText("  Dişi");

            TextView erkek=dialog3.findViewById(R.id.erkekText);
            TextView kadin=dialog3.findViewById(R.id.kadinText);

            erkek.setOnClickListener(view1 -> {
                secilenCinsiyet="Erkek";
                dialog3.dismiss();
                dialog.show();
                ((TextView) dialog.findViewById(R.id.secilenCinsiyet)).setText("Erkek");
            });
            kadin.setOnClickListener(view1 -> {
                secilenCinsiyet="Dişi";
                dialog3.dismiss();
                dialog.show();
                ((TextView) dialog.findViewById(R.id.secilenCinsiyet)).setText("Dişi");
            });

            dialog3.show();
            dialog3.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog3.getWindow().setGravity(Gravity.BOTTOM);
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        dialog.findViewById(R.id.buttonFiltre).setOnClickListener(view -> {
            if (oneri){
                getData(secilenSehir,secilenCinsiyet);
            }
            else{
                getDataKisilikYok(secilenSehir,secilenCinsiyet);
            }
            secilenCinsiyet="";
            secilenSehir="";
            dialog.dismiss();
        });
    }
    private void getData(@Nullable String sehir,@Nullable String cinsiyet) {

        getKullaniciKisilik(firestore, auth, new KisilikCallback() {
            @Override
            public void onKisilikReceived(String kisilik) {

                getIlandami(firestore, auth, new ilanCallback() {
                    @Override
                    public void onIlanReceived(String ilanValue) {
                        if (sehir.equals("")&&cinsiyet.equals("")){
                            firestore.collection("kullanici_hayvanlari")
                                    .whereEqualTo("kisilik", kisilik)
                                    .whereEqualTo("ilanda_mi","true")
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @SuppressLint("NotifyDataSetChanged")
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (error != null) {
                                                Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            hayvanListesi.clear();

                                            if (value != null) {
                                                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                                    Map<String, Object> data = documentSnapshot.getData();

                                                    String email = (String) data.get("email");
                                                    String foto = (String) data.get("foto");
                                                    String ad = (String) data.get("ad");
                                                    String tur = (String) data.get("tur");
                                                    String irk = (String) data.get("ırk");
                                                    String cinsiyet = String.valueOf(data.get("cinsiyet"));
                                                    String yas = String.valueOf(data.get("yas"));
                                                    String saglik = (String) data.get("saglik");
                                                    String aciklama = (String) data.get("aciklama");
                                                    String kisilik = (String) data.get("kisilik");
                                                    String sahipliMi = (String) data.get("sahipli_mi");
                                                    String ilandaMi = (String) data.get("ilanda_mi");
                                                    String enlem = (String) data.get("enlem");
                                                    String boylam = (String) data.get("boylam");
                                                    String sehir = (String) data.get("sehir");
                                                    String ilce = (String) data.get("ilce");
                                                    String docid = documentSnapshot.getId();

                                                    Hayvan hayvan = new Hayvan(email, foto, ad, tur, irk, cinsiyet, yas, saglik, aciklama,
                                                            kisilik, docid, sahipliMi, ilandaMi,enlem,boylam,sehir,ilce);
                                                    hayvanListesi.add(hayvan);
                                                }
                                                adapter.notifyDataSetChanged();
                                                binding.rvSahiplendirHayvanlar.setVisibility(View.VISIBLE);
                                                binding.imageViewCerikBulunamadi.setVisibility(View.GONE);
                                                binding.textViewCerikBulunamadiYazisi.setVisibility(View.GONE);
                                            }
                                            else{
                                                binding.rvSahiplendirHayvanlar.setVisibility(View.INVISIBLE);
                                                binding.imageViewCerikBulunamadi.setImageResource(R.drawable.not_found);
                                                binding.imageViewCerikBulunamadi.setVisibility(View.VISIBLE);
                                                binding.textViewCerikBulunamadiYazisi.setText("Sahiplenecek hiç hayvan yok mu? Belkide kişilik testini yapmalısınız.");
                                                binding.textViewCerikBulunamadiYazisi.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });

                        } else if (sehir.equals("")) {
                            firestore.collection("kullanici_hayvanlari")
                                    .whereEqualTo("kisilik", kisilik)
                                    .whereEqualTo("ilanda_mi","true")
                                    .whereEqualTo("cinsiyet",cinsiyet)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @SuppressLint("NotifyDataSetChanged")
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (error != null) {
                                                Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            hayvanListesi.clear();

                                            if (value != null) {
                                                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                                    Map<String, Object> data = documentSnapshot.getData();

                                                    String email = (String) data.get("email");
                                                    String foto = (String) data.get("foto");
                                                    String ad = (String) data.get("ad");
                                                    String tur = (String) data.get("tur");
                                                    String irk = (String) data.get("ırk");
                                                    String cinsiyet = String.valueOf(data.get("cinsiyet"));
                                                    String yas = String.valueOf(data.get("yas"));
                                                    String saglik = (String) data.get("saglik");
                                                    String aciklama = (String) data.get("aciklama");
                                                    String kisilik = (String) data.get("kisilik");
                                                    String sahipliMi = (String) data.get("sahipli_mi");
                                                    String ilandaMi = (String) data.get("ilanda_mi");
                                                    String enlem = (String) data.get("enlem");
                                                    String boylam = (String) data.get("boylam");
                                                    String sehir = (String) data.get("sehir");
                                                    String ilce = (String) data.get("ilce");
                                                    String docid = documentSnapshot.getId();

                                                    Hayvan hayvan = new Hayvan(email, foto, ad, tur, irk, cinsiyet, yas, saglik, aciklama,
                                                            kisilik, docid, sahipliMi, ilandaMi,enlem,boylam,sehir,ilce);
                                                    hayvanListesi.add(hayvan);
                                                }
                                                adapter.notifyDataSetChanged();

                                            }
                                            else{
                                            }
                                        }
                                    });

                        } else if (cinsiyet.equals("")) {
                            firestore.collection("kullanici_hayvanlari")
                                    .whereEqualTo("kisilik", kisilik)
                                    .whereEqualTo("ilanda_mi","true")
                                    .whereEqualTo("sehir",sehir)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @SuppressLint("NotifyDataSetChanged")
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (error != null) {
                                                Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            hayvanListesi.clear();

                                            if (value != null) {
                                                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                                    Map<String, Object> data = documentSnapshot.getData();

                                                    String email = (String) data.get("email");
                                                    String foto = (String) data.get("foto");
                                                    String ad = (String) data.get("ad");
                                                    String tur = (String) data.get("tur");
                                                    String irk = (String) data.get("ırk");
                                                    String cinsiyet = String.valueOf(data.get("cinsiyet"));
                                                    String yas = String.valueOf(data.get("yas"));
                                                    String saglik = (String) data.get("saglik");
                                                    String aciklama = (String) data.get("aciklama");
                                                    String kisilik = (String) data.get("kisilik");
                                                    String sahipliMi = (String) data.get("sahipli_mi");
                                                    String ilandaMi = (String) data.get("ilanda_mi");
                                                    String enlem = (String) data.get("enlem");
                                                    String boylam = (String) data.get("boylam");
                                                    String sehir = (String) data.get("sehir");
                                                    String ilce = (String) data.get("ilce");
                                                    String docid = documentSnapshot.getId();

                                                    Hayvan hayvan = new Hayvan(email, foto, ad, tur, irk, cinsiyet, yas, saglik, aciklama,
                                                            kisilik, docid, sahipliMi, ilandaMi,enlem,boylam,sehir,ilce);
                                                    hayvanListesi.add(hayvan);
                                                }
                                                adapter.notifyDataSetChanged();

                                            }
                                            else{

                                            }
                                        }
                                    });

                        }else{
                            firestore.collection("kullanici_hayvanlari")
                                    .whereEqualTo("kisilik", kisilik)
                                    .whereEqualTo("ilanda_mi","true")
                                    .whereEqualTo("sehir",sehir)
                                    .whereEqualTo("cinsiyet",cinsiyet)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @SuppressLint("NotifyDataSetChanged")
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (error != null) {
                                                Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            hayvanListesi.clear();

                                            if (value != null) {
                                                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                                    Map<String, Object> data = documentSnapshot.getData();

                                                    String email = (String) data.get("email");
                                                    String foto = (String) data.get("foto");
                                                    String ad = (String) data.get("ad");
                                                    String tur = (String) data.get("tur");
                                                    String irk = (String) data.get("ırk");
                                                    String cinsiyet = String.valueOf(data.get("cinsiyet"));
                                                    String yas = String.valueOf(data.get("yas"));
                                                    String saglik = (String) data.get("saglik");
                                                    String aciklama = (String) data.get("aciklama");
                                                    String kisilik = (String) data.get("kisilik");
                                                    String sahipliMi = (String) data.get("sahipli_mi");
                                                    String ilandaMi = (String) data.get("ilanda_mi");
                                                    String enlem = (String) data.get("enlem");
                                                    String boylam = (String) data.get("boylam");
                                                    String sehir = (String) data.get("sehir");
                                                    String ilce = (String) data.get("ilce");
                                                    String docid = documentSnapshot.getId();

                                                    Hayvan hayvan = new Hayvan(email, foto, ad, tur, irk, cinsiyet, yas, saglik, aciklama,
                                                            kisilik, docid, sahipliMi, ilandaMi,enlem,boylam,sehir,ilce);
                                                    hayvanListesi.add(hayvan);
                                                }
                                                adapter.notifyDataSetChanged();
                                            }
                                            else{
                                            }
                                        }
                                    });
                        }
                    }//
                });
            }
        });
    }

    private void getKullaniciKisilik(FirebaseFirestore firestore, FirebaseAuth auth, KisilikCallback callback) {
        String currentUserEmail = auth.getCurrentUser().getEmail();
        firestore.collection("kullanicilar").whereEqualTo("email", currentUserEmail)
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
                                String kisilik = (String) data.get("kişilik");
                                callback.onKisilikReceived(kisilik);
                            }
                        }
                    }
                });
    }
    private void getIlandami(FirebaseFirestore firestore, FirebaseAuth auth,ilanCallback callback) {
        String currentUserEmail = auth.getCurrentUser().getEmail();
        firestore.collection("kullanicilar").whereEqualTo("email", currentUserEmail)
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
                                String ilan = (String) data.get("ilanda_mi");
                                callback.onIlanReceived(ilan);
                            }
                        }
                    }
                });
    }
    public interface KisilikCallback {
        void onKisilikReceived(String kisilikValue);
    }

    public interface ilanCallback {
        void onIlanReceived(String ilanValue);
    }
    private void sehirBaslat(ListView listView){
        ArrayList<String> sehirler=new ArrayList<>();
        sehirler.add("Adana");
        sehirler.add("Adıyaman");
        sehirler.add("Afyonkarahisar");
        sehirler.add("Ağrı");
        sehirler.add("Amasya");
        sehirler.add("Ankara");
        sehirler.add("Antalya");
        sehirler.add("Artvin");
        sehirler.add("Aydın");
        sehirler.add("Balıkesir");
        sehirler.add("Bilecik");
        sehirler.add("Bingöl");
        sehirler.add("Bitlis");
        sehirler.add("Bolu");
        sehirler.add("Burdur");
        sehirler.add("Bursa");
        sehirler.add("Çanakkale");
        sehirler.add("Çankırı");
        sehirler.add("Çorum");
        sehirler.add("Denizli");
        sehirler.add("Diyarbakır");
        sehirler.add("Edirne");
        sehirler.add("Elazığ");
        sehirler.add("Erzincan");
        sehirler.add("Erzurum");
        sehirler.add("Eskişehir");
        sehirler.add("Gaziantep");
        sehirler.add("Giresun");
        sehirler.add("Gümüşhane");
        sehirler.add("Hakkari");
        sehirler.add("Hatay");
        sehirler.add("Isparta");
        sehirler.add("Mersin");
        sehirler.add("İstanbul");
        sehirler.add("İzmir");
        sehirler.add("Kars");
        sehirler.add("Kastamonu");
        sehirler.add("Kayseri");
        sehirler.add("Kırklareli");
        sehirler.add("Kırşehir");
        sehirler.add("Kocaeli");
        sehirler.add("Konya");
        sehirler.add("Kütahya");
        sehirler.add("Malatya");
        sehirler.add("Manisa");
        sehirler.add("Kahramanmaraş");
        sehirler.add("Mardin");
        sehirler.add("Muğla");
        sehirler.add("Muş");
        sehirler.add("Nevşehir");
        sehirler.add("Niğde");
        sehirler.add("Ordu");
        sehirler.add("Rize");
        sehirler.add("Sakarya");
        sehirler.add("Samsun");
        sehirler.add("Siirt");
        sehirler.add("Sinop");
        sehirler.add("Sivas");
        sehirler.add("Tekirdağ");
        sehirler.add("Tokat");
        sehirler.add("Trabzon");
        sehirler.add("Tunceli");
        sehirler.add("Şanlıurfa");
        sehirler.add("Uşak");
        sehirler.add("Van");
        sehirler.add("Yozgat");
        sehirler.add("Zonguldak");
        sehirler.add("Aksaray");
        sehirler.add("Bayburt");
        sehirler.add("Karaman");
        sehirler.add("Kırıkkale");
        sehirler.add("Batman");
        sehirler.add("Şırnak");
        sehirler.add("Bartın");
        sehirler.add("Ardahan");
        sehirler.add("Iğdır");
        sehirler.add("Yalova");
        sehirler.add("Karabük");
        sehirler.add("Kilis");
        sehirler.add("Osmaniye");
        sehirler.add("Düzce");


        ArrayAdapter arrayAdapter= new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,sehirler);
        listView.setAdapter(arrayAdapter);
    }

    private void getDataKisilikYok(@Nullable String sehir,@Nullable String cinsiyet) {
                getIlandami(firestore, auth, new ilanCallback() {
                    @Override
                    public void onIlanReceived(String ilanValue) {
                        if (sehir.equals("")&&cinsiyet.equals("")){
                            firestore.collection("kullanici_hayvanlari")
                                    .whereEqualTo("ilanda_mi","true")
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @SuppressLint("NotifyDataSetChanged")
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (error != null) {
                                                Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            hayvanListesi.clear();

                                            if (value != null) {
                                                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                                    Map<String, Object> data = documentSnapshot.getData();

                                                    String email = (String) data.get("email");
                                                    String foto = (String) data.get("foto");
                                                    String ad = (String) data.get("ad");
                                                    String tur = (String) data.get("tur");
                                                    String irk = (String) data.get("ırk");
                                                    String cinsiyet = String.valueOf(data.get("cinsiyet"));
                                                    String yas = String.valueOf(data.get("yas"));
                                                    String saglik = (String) data.get("saglik");
                                                    String aciklama = (String) data.get("aciklama");
                                                    String kisilik = (String) data.get("kisilik");
                                                    String sahipliMi = (String) data.get("sahipli_mi");
                                                    String ilandaMi = (String) data.get("ilanda_mi");
                                                    String enlem = (String) data.get("enlem");
                                                    String boylam = (String) data.get("boylam");
                                                    String sehir = (String) data.get("sehir");
                                                    String ilce = (String) data.get("ilce");
                                                    String docid = documentSnapshot.getId();

                                                    Hayvan hayvan = new Hayvan(email, foto, ad, tur, irk, cinsiyet, yas, saglik, aciklama,
                                                            kisilik, docid, sahipliMi, ilandaMi,enlem,boylam,sehir,ilce);
                                                    hayvanListesi.add(hayvan);
                                                }
                                                adapter.notifyDataSetChanged();
                                                binding.rvSahiplendirHayvanlar.setVisibility(View.VISIBLE);
                                                binding.imageViewCerikBulunamadi.setVisibility(View.GONE);
                                                binding.textViewCerikBulunamadiYazisi.setVisibility(View.GONE);
                                            }
                                            else{
                                                binding.rvSahiplendirHayvanlar.setVisibility(View.INVISIBLE);
                                                binding.imageViewCerikBulunamadi.setImageResource(R.drawable.not_found);
                                                binding.imageViewCerikBulunamadi.setVisibility(View.VISIBLE);
                                                binding.textViewCerikBulunamadiYazisi.setText("Sahiplenecek hiç hayvan yok mu? Belkide kişilik testini yapmalısınız.");
                                                binding.textViewCerikBulunamadiYazisi.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    });

                        } else if (sehir.equals("")) {
                            firestore.collection("kullanici_hayvanlari")
                                    .whereEqualTo("ilanda_mi","true")
                                    .whereEqualTo("cinsiyet",cinsiyet)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @SuppressLint("NotifyDataSetChanged")
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (error != null) {
                                                Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            hayvanListesi.clear();

                                            if (value != null) {
                                                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                                    Map<String, Object> data = documentSnapshot.getData();

                                                    String email = (String) data.get("email");
                                                    String foto = (String) data.get("foto");
                                                    String ad = (String) data.get("ad");
                                                    String tur = (String) data.get("tur");
                                                    String irk = (String) data.get("ırk");
                                                    String cinsiyet = String.valueOf(data.get("cinsiyet"));
                                                    String yas = String.valueOf(data.get("yas"));
                                                    String saglik = (String) data.get("saglik");
                                                    String aciklama = (String) data.get("aciklama");
                                                    String kisilik = (String) data.get("kisilik");
                                                    String sahipliMi = (String) data.get("sahipli_mi");
                                                    String ilandaMi = (String) data.get("ilanda_mi");
                                                    String enlem = (String) data.get("enlem");
                                                    String boylam = (String) data.get("boylam");
                                                    String sehir = (String) data.get("sehir");
                                                    String ilce = (String) data.get("ilce");
                                                    String docid = documentSnapshot.getId();

                                                    Hayvan hayvan = new Hayvan(email, foto, ad, tur, irk, cinsiyet, yas, saglik, aciklama,
                                                            kisilik, docid, sahipliMi, ilandaMi,enlem,boylam,sehir,ilce);
                                                    hayvanListesi.add(hayvan);
                                                }
                                                adapter.notifyDataSetChanged();

                                            }
                                            else{
                                            }
                                        }
                                    });

                        } else if (cinsiyet.equals("")) {
                            firestore.collection("kullanici_hayvanlari")
                                    .whereEqualTo("ilanda_mi","true")
                                    .whereEqualTo("sehir",sehir)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @SuppressLint("NotifyDataSetChanged")
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (error != null) {
                                                Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            hayvanListesi.clear();

                                            if (value != null) {
                                                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                                    Map<String, Object> data = documentSnapshot.getData();

                                                    String email = (String) data.get("email");
                                                    String foto = (String) data.get("foto");
                                                    String ad = (String) data.get("ad");
                                                    String tur = (String) data.get("tur");
                                                    String irk = (String) data.get("ırk");
                                                    String cinsiyet = String.valueOf(data.get("cinsiyet"));
                                                    String yas = String.valueOf(data.get("yas"));
                                                    String saglik = (String) data.get("saglik");
                                                    String aciklama = (String) data.get("aciklama");
                                                    String kisilik = (String) data.get("kisilik");
                                                    String sahipliMi = (String) data.get("sahipli_mi");
                                                    String ilandaMi = (String) data.get("ilanda_mi");
                                                    String enlem = (String) data.get("enlem");
                                                    String boylam = (String) data.get("boylam");
                                                    String sehir = (String) data.get("sehir");
                                                    String ilce = (String) data.get("ilce");
                                                    String docid = documentSnapshot.getId();

                                                    Hayvan hayvan = new Hayvan(email, foto, ad, tur, irk, cinsiyet, yas, saglik, aciklama,
                                                            kisilik, docid, sahipliMi, ilandaMi,enlem,boylam,sehir,ilce);
                                                    hayvanListesi.add(hayvan);
                                                }
                                                adapter.notifyDataSetChanged();

                                            }
                                            else{

                                            }
                                        }
                                    });

                        }else{
                            firestore.collection("kullanici_hayvanlari")
                                    .whereEqualTo("ilanda_mi","true")
                                    .whereEqualTo("sehir",sehir)
                                    .whereEqualTo("cinsiyet",cinsiyet)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @SuppressLint("NotifyDataSetChanged")
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (error != null) {
                                                Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            hayvanListesi.clear();

                                            if (value != null) {
                                                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                                    Map<String, Object> data = documentSnapshot.getData();

                                                    String email = (String) data.get("email");
                                                    String foto = (String) data.get("foto");
                                                    String ad = (String) data.get("ad");
                                                    String tur = (String) data.get("tur");
                                                    String irk = (String) data.get("ırk");
                                                    String cinsiyet = String.valueOf(data.get("cinsiyet"));
                                                    String yas = String.valueOf(data.get("yas"));
                                                    String saglik = (String) data.get("saglik");
                                                    String aciklama = (String) data.get("aciklama");
                                                    String kisilik = (String) data.get("kisilik");
                                                    String sahipliMi = (String) data.get("sahipli_mi");
                                                    String ilandaMi = (String) data.get("ilanda_mi");
                                                    String enlem = (String) data.get("enlem");
                                                    String boylam = (String) data.get("boylam");
                                                    String sehir = (String) data.get("sehir");
                                                    String ilce = (String) data.get("ilce");
                                                    String docid = documentSnapshot.getId();

                                                    Hayvan hayvan = new Hayvan(email, foto, ad, tur, irk, cinsiyet, yas, saglik, aciklama,
                                                            kisilik, docid, sahipliMi, ilandaMi,enlem,boylam,sehir,ilce);
                                                    hayvanListesi.add(hayvan);
                                                }
                                                adapter.notifyDataSetChanged();
                                            }
                                            else{
                                            }
                                        }
                                    });
                        }
                    }
                });


    }
}