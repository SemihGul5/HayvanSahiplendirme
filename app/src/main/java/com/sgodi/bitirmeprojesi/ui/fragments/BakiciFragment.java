package com.sgodi.bitirmeprojesi.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.Bakici;
import com.sgodi.bitirmeprojesi.databinding.FragmentBakiciBinding;
import com.sgodi.bitirmeprojesi.ui.adapters.BakiciAdapter;

import java.util.ArrayList;
import java.util.Map;

public class BakiciFragment extends Fragment {
    private FragmentBakiciBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    ArrayList<Bakici> bakiciList;
    BakiciAdapter adapter;
    String secilenSehir="",secilenCinsiyet="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBakiciBinding.inflate(inflater, container, false);
        binding.toolbar2.setTitle("Bakıcılar");
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        bakiciList=new ArrayList<>();

        getData("","");
        binding.recyclerViewBakicilar.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter= new BakiciAdapter(getContext(),bakiciList);
        binding.recyclerViewBakicilar.setAdapter(adapter);



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

        binding.imageViewBakiciFiltre.setOnClickListener(view -> {
            bottomDialogShow();
        });
















        return binding.getRoot();
    }



    public void durumTespit(Map<String, Object> data, View view) {

        try {
            String kisilik = (String) data.get("kişilik");
            String kisilik_durum = (String) data.get("kisilik_durum");
            String bakici_durum=(String) data.get("bakici_durum");
            String tel=(String) data.get("tel");
            // kisilik_durum'un null olabileceğini kontrol et
            if (kisilik_durum != null&&bakici_durum!=null) {
                    if (bakici_durum.equals("true")){
                        Snackbar.make(view, "Zaten bakıcı ilanınız var, kaldırmak istiyorsanız ayarlara gidiniz.", Snackbar.LENGTH_LONG).show();
                    }else{
                        if ("false".equals(kisilik_durum)) {
                            Snackbar.make(view, "Lütfen kişilik testini yapınız.", Snackbar.LENGTH_LONG).show();
                        } else {
                            if ("Duyarlılık".equals(kisilik)) {
                                Snackbar.make(view, "Bakıcı olmaya uygun değilsiniz.", Snackbar.LENGTH_LONG).show();
                            } else {
                                if (tel.equals("null")){
                                    Snackbar.make(view, "Telefon numaranızı sisteme kayıt etmelisiniz.", Snackbar.LENGTH_LONG).show();
                                }
                                else{
                                    Navigation.findNavController(view).navigate(R.id.action_bakiciFragment_to_bakiciOlFragment);
                                }
                            }
                        }
                    }
                }
            else {}
            }

        catch (Exception e){

        }



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

            TextView erkek=dialog3.findViewById(R.id.erkekText);
            TextView kadin=dialog3.findViewById(R.id.kadinText);

            erkek.setOnClickListener(view1 -> {
                secilenCinsiyet="Erkek";
                dialog3.dismiss();
                dialog.show();
                ((TextView) dialog.findViewById(R.id.secilenCinsiyet)).setText("Erkek");
            });
            kadin.setOnClickListener(view1 -> {
                secilenCinsiyet="Kadın";
                dialog3.dismiss();
                dialog.show();
                ((TextView) dialog.findViewById(R.id.secilenCinsiyet)).setText("Kadın");
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
            getData(secilenSehir,secilenCinsiyet);
            dialog.dismiss();
        });
    }


    private void getKullaniciKisilik(FirebaseFirestore firestore, FirebaseAuth auth, SahiplenFragment.KisilikCallback callback) {
        String currentUserEmail = auth.getCurrentUser().getEmail();
        firestore.collection("bakici").whereEqualTo("email", currentUserEmail)
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
                                String kisilik = (String) data.get("kisilik");
                                callback.onKisilikReceived(kisilik);
                            }
                        }
                    }
                });
    }
    public interface KisilikCallback {
        void onKisilikReceived(String kisilikValue);
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
    private void getData(@Nullable String sehir,@Nullable String cinsiyet) {
        getKullaniciKisilik(db, auth, new SahiplenFragment.KisilikCallback() {
            @Override
            public void onKisilikReceived(String kisilikValue) {
                if (sehir.equals("")&&cinsiyet.equals("")){
                    db.collection("bakici").whereEqualTo("kisilik",kisilikValue)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (error != null) {
                                        Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    bakiciList.clear();

                                    if (value != null) {
                                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                            Map<String, Object> data = documentSnapshot.getData();

                                            String email = (String) data.get("email");
                                            String aciklama = (String) data.get("aciklama");
                                            String ad = (String) data.get("ad");
                                            String cinsiyet = (String) data.get("cinsiyet");
                                            String foto=(String) data.get("foto");
                                            String kisilik = (String) data.get("kisilik");
                                            String konum = (String) data.get("konum");
                                            String soyad = (String) data.get("soyad");
                                            String tel = (String) data.get("tel");

                                            Bakici bakici= new Bakici(ad,soyad,email,kisilik,konum,tel,aciklama,foto,cinsiyet);
                                            bakiciList.add(bakici);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });

                } else if (sehir.equals("")) {
                    db.collection("bakici").whereEqualTo("cinsiyet",cinsiyet).whereEqualTo("kisilik",kisilikValue)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (error != null) {
                                        Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    bakiciList.clear();

                                    if (value != null) {
                                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                            Map<String, Object> data = documentSnapshot.getData();

                                            String email = (String) data.get("email");
                                            String aciklama = (String) data.get("aciklama");
                                            String ad = (String) data.get("ad");
                                            String cinsiyet = (String) data.get("cinsiyet");
                                            String foto=(String) data.get("foto");
                                            String kisilik = (String) data.get("kisilik");
                                            String konum = (String) data.get("konum");
                                            String soyad = (String) data.get("soyad");
                                            String tel = (String) data.get("tel");

                                            Bakici bakici= new Bakici(ad,soyad,email,kisilik,konum,tel,aciklama,foto,cinsiyet);
                                            bakiciList.add(bakici);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });

                } else if (cinsiyet.equals("")) {
                    db.collection("bakici").whereEqualTo("konum",sehir).whereEqualTo("kisilik",kisilikValue)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (error != null) {
                                        Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    bakiciList.clear();

                                    if (value != null) {
                                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                            Map<String, Object> data = documentSnapshot.getData();

                                            String email = (String) data.get("email");
                                            String aciklama = (String) data.get("aciklama");
                                            String ad = (String) data.get("ad");
                                            String cinsiyet = (String) data.get("cinsiyet");
                                            String foto=(String) data.get("foto");
                                            String kisilik = (String) data.get("kisilik");
                                            String konum = (String) data.get("konum");
                                            String soyad = (String) data.get("soyad");
                                            String tel = (String) data.get("tel");

                                            Bakici bakici= new Bakici(ad,soyad,email,kisilik,konum,tel,aciklama,foto,cinsiyet);
                                            bakiciList.add(bakici);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });

                }else {
                    db.collection("bakici").whereEqualTo("konum",sehir).whereEqualTo("cinsiyet",cinsiyet)
                            .whereEqualTo("kisilik",kisilikValue)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (error != null) {
                                        Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    bakiciList.clear();

                                    if (value != null) {
                                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                            Map<String, Object> data = documentSnapshot.getData();

                                            String email = (String) data.get("email");
                                            String aciklama = (String) data.get("aciklama");
                                            String ad = (String) data.get("ad");
                                            String cinsiyet = (String) data.get("cinsiyet");
                                            String foto=(String) data.get("foto");
                                            String kisilik = (String) data.get("kisilik");
                                            String konum = (String) data.get("konum");
                                            String soyad = (String) data.get("soyad");
                                            String tel = (String) data.get("tel");

                                            Bakici bakici= new Bakici(ad,soyad,email,kisilik,konum,tel,aciklama,foto,cinsiyet);
                                            bakiciList.add(bakici);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                }
            }
        });
    }
}
