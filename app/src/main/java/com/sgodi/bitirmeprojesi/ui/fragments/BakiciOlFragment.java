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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.databinding.FragmentBakiciOlBinding;

import java.util.HashMap;
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

        binding.buttonBakiciOl.setOnClickListener(view -> {
            bakici_kaydet(view);

        });


        return binding.getRoot();
    }
    private void bakici_kaydet(View view) {
        if (imageData == null || binding.editTextBakiciAd.getText().toString().isEmpty()
                || binding.editTextBakiciSoyad.getText().toString().isEmpty()
                || binding.editTextBakiciKisilik.getText().toString().isEmpty()
                || binding.editTextBakiciKonum.getText().toString().isEmpty()
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
                    String konum= binding.editTextBakiciKonum.getText().toString();
                    String tel = binding.editTextBakiciTel.getText().toString();
                    String aciklama = binding.editTextBakiciAciklama.getText().toString();
                    String cinsiyet="";
                    int cinsiyetID = binding.radioGroupBakiciCinsiyet.getCheckedRadioButtonId();
                    if (cinsiyetID == R.id.radioButtonErkek) {
                        cinsiyet = "Erkek";
                    } else {
                        cinsiyet = "Kadın";
                    }


                    HashMap<String, Object> postData = new HashMap<>();
                    postData.put("email", email);
                    postData.put("foto", foto);
                    postData.put("ad", ad);
                    postData.put("soyad", soyad);
                    postData.put("kişilik", kisilik);
                    postData.put("konum", konum);
                    postData.put("tel", tel);
                    postData.put("aciklama", aciklama);
                    postData.put("cinsiyet", cinsiyet);


                    //firebase koleksiyonuna yükleme işlemi ve sonucunun ne olduğunu değerlendirme
                    firebaseFirestore.collection("bakici").add(postData).addOnSuccessListener(documentReference -> {
                        bakici_druum_guncelle(email,firebaseFirestore);
                        Toast.makeText(getContext(), "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                        temizle();
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
        firebaseFirestore.collection("kullanicilar").document(email)
                .update("bakici_durum", "true")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Başarıyla güncellendiğinde yapılacak işlemler
                        Toast.makeText(getContext(), "Bakıcı durumu güncellendi", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Güncelleme başarısız olduğunda yapılacak işlemler
                        Toast.makeText(getContext(), "Bakıcı durumu güncellenirken bir hata oluştu", Toast.LENGTH_SHORT).show();
                    }
                });

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
        binding.editTextBakiciKonum.setText("");
        binding.editTextBakiciTel.setText("");
        binding.editTextBakiciAciklama.setText("");
    }
}