package com.sgodi.bitirmeprojesi.ui.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.databinding.FragmentBakiciOlBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class BakiciOlFragment extends Fragment {
    private FragmentBakiciOlBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Uri imageData=null;
    FirebaseAuth auth;
    FirebaseStorage firebaseStorage;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentBakiciOlBinding.inflate(inflater, container, false);
        binding.materialToolbarBakiciOl.setTitle("Bakıcı Ol");
        registerLauncher();
        binding.imageView.setOnClickListener(view -> {
            fotografTiklandi(view);
        });
        // başlatılmalar
        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();// görseli depoda nereye kaydediceğimizi gösteren bir değişken
        sehirBaslat();
        binding.editTextBakiciAd.setEnabled(false);
        binding.editTextBakiciSoyad.setEnabled(false);
        binding.editTextBakiciKisilik.setEnabled(false);
        binding.editTextBakiciTel.setEnabled(false);
        getBilgiler(firebaseFirestore);

        binding.buttonBakiciOl.setOnClickListener(view -> {
            bakici_kaydet(view);

        });


        return binding.getRoot();
    }

    private void getBilgiler(FirebaseFirestore firebaseFirestore) {
            // Belirli bir e-postaya sahip kullanıcıyı Firestore'dan al
            String email = auth.getCurrentUser().getEmail();
            firebaseFirestore.collection("kullanicilar").whereEqualTo("email", email)
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
                                    String ad = (String) data.get("ad");
                                    String soyad = (String) data.get("soyad");
                                    String kisilik=(String) data.get("kişilik");
                                    String tel=(String) data.get("tel");
                                    binding.editTextBakiciAd.setText(ad);
                                    binding.editTextBakiciSoyad.setText(soyad);
                                    binding.editTextBakiciKisilik.setText(kisilik);
                                    binding.editTextBakiciTel.setText(tel);
                                }
                            }
                        }
                    });

    }
    private void sehirBaslat(){
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
        binding.autoCompleteTextViewSehir.setAdapter(arrayAdapter);
    }
    private void bakici_kaydet(View view) {
        if (imageData == null || binding.editTextBakiciAd.getText().toString().isEmpty()
                || binding.editTextBakiciSoyad.getText().toString().isEmpty()
                || binding.editTextBakiciKisilik.getText().toString().isEmpty()
                || binding.autoCompleteTextViewSehir.getText().toString().isEmpty()
                || binding.radioGroupBakiciCinsiyet.getCheckedRadioButtonId() == -1
                || binding.editTextBakiciTel.getText().toString().isEmpty()
                || binding.editTextBakiciAciklama.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Tüm alanları doldurmak zorunludur.", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.progressBarBakiciOl.setVisibility(View.VISIBLE);
        binding.buttonBakiciOl.setEnabled(false);
        try {
            //unique bir id oluşturur
            UUID uuid = UUID.randomUUID();
            String path = "images/" + uuid + ".jpg";
            storageReference.child(path).putFile(imageData).addOnSuccessListener(taskSnapshot -> {
                //görseli storage'a kaydettik, onSuccess de veritabanına kaydetme işlemleri, ilgili kullanıcının
                //direkt o kaydedilen resmi eşleştiriyoruz
                StorageReference reference = firebaseStorage.getReference(path);
                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                    //veritabanına koyma işlemleri
                    FirebaseUser user = auth.getCurrentUser();
                    String email = user.getEmail();
                    String foto = uri.toString();
                    String ad = binding.editTextBakiciAd.getText().toString();
                    String soyad = binding.editTextBakiciSoyad.getText().toString();
                    String kisilik = binding.editTextBakiciKisilik.getText().toString();
                    String konum= binding.autoCompleteTextViewSehir.getText().toString();
                    String tel = binding.editTextBakiciTel.getText().toString();
                    String aciklama = binding.editTextBakiciAciklama.getText().toString();
                    String cinsiyet = "";
                    int selectedRadioButtonId = binding.radioGroupBakiciCinsiyet.getCheckedRadioButtonId();
                    if (selectedRadioButtonId ==R.id.radioButtonBakiciErkek) {
                        cinsiyet="Erkek";
                    }else{
                        cinsiyet="Kadın";
                    }

                    binding.textView4.setText(cinsiyet);
                    HashMap<String, Object> postData = new HashMap<>();
                    postData.put("email", email);
                    postData.put("foto", foto);
                    postData.put("ad", ad);
                    postData.put("soyad", soyad);
                    postData.put("kisilik", kisilik);
                    postData.put("konum", konum);
                    postData.put("tel", tel);
                    postData.put("aciklama", aciklama);
                    postData.put("cinsiyet", cinsiyet);


                    //firebase koleksiyonuna yükleme işlemi ve sonucunun ne olduğunu değerlendirme
                    firebaseFirestore.collection("bakici").add(postData).addOnSuccessListener(documentReference -> {
                        bakici_druum_guncelle(email,firebaseFirestore);
                        Toast.makeText(getContext(), "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                        temizle();
                        Navigation.findNavController(view).navigate(R.id.action_bakiciOlFragment_to_anaSayfaFragment);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }).addOnCompleteListener(task -> {
                        binding.progressBarBakiciOl.setVisibility(View.INVISIBLE);
                        binding.buttonBakiciOl.setEnabled(true);

                    });
                });

            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                binding.progressBarBakiciOl.setVisibility(View.INVISIBLE);
                binding.buttonBakiciOl.setEnabled(true);

            });
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            binding.progressBarBakiciOl.setVisibility(View.INVISIBLE);
            binding.buttonBakiciOl.setEnabled(true);

        }
    }

    private void bakici_druum_guncelle(String email, FirebaseFirestore firebaseFirestore) {
        try {
            Query query=firebaseFirestore.collection("kullanicilar").whereEqualTo("email",email);
            query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Belirli bir kritere uyan belgeyi güncelle
                        String userId = document.getId();
                        firebaseFirestore.collection("kullanicilar").document(userId).update("bakici_durum", "true");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),"Bakıcı durum güncellenemedi! "+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Log.i("Mesaj",e.getMessage());
        }

    }


    //izin işlemleri
    public void fotografTiklandi(View view) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_MEDIA_IMAGES)!= PackageManager.PERMISSION_GRANTED){
                //izin gerekli
                //kullanıcıya açıklama göstermek zorunda mıyız kontrolü
                if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_MEDIA_IMAGES)){
                    //açıklama gerekli
                    Snackbar.make(view,"Galeriye gitmek için izin gereklidir.",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //izin işlemleri
                            permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                }
                else{
                    //izin işlemleri
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                }

            }
            else{
                //izin daha önceden verilmiş, galeriye git
                Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        }
        else{
            if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                //izin gerekli
                //kullanıcıya açıklama göstermek zorunda mıyız kontrolü
                if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                    //açıklama gerekli
                    Snackbar.make(view,"Galeriye gitmek için izin gereklidir.",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //izin işlemleri
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                }
                else{
                    //izin işlemleri
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                }

            }
            else{
                //izin daha önceden verilmiş, galeriye git
                Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        }
    }

    private void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        imageData = intentFromResult.getData();
                        binding.imageView.setImageURI(imageData);
                    }
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intent);
                } else {
                    Toast.makeText(getContext(), "İzin verilmedi", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void temizle(){
        binding.imageView.setImageResource(R.drawable.baseline_add_a_photo_24);
        binding.editTextBakiciAd.setText("");
        binding.editTextBakiciSoyad.setText("");
        binding.editTextBakiciKisilik.setText("");
        binding.autoCompleteTextViewSehir.setText("");
        binding.editTextBakiciTel.setText("");
        binding.editTextBakiciAciklama.setText("");
    }
}