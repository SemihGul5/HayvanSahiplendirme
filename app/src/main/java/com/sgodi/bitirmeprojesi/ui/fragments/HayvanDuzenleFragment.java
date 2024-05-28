package com.sgodi.bitirmeprojesi.ui.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sgodi.bitirmeprojesi.MainActivity2;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.Hayvan;
import com.sgodi.bitirmeprojesi.data.models.ImageUtil;
import com.sgodi.bitirmeprojesi.databinding.FragmentHayvanDuzenleBinding;
import com.sgodi.bitirmeprojesi.ml.Model;
import com.squareup.picasso.Picasso;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class HayvanDuzenleFragment extends Fragment {
    private FragmentHayvanDuzenleBinding binding;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private Hayvan hayvan;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    Uri imageData=null,imageData2=null,imageData3=null,imageData4=null;
    Bitmap img;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    ActivityResultLauncher<Intent> activityResultLauncher2;
    ActivityResultLauncher<String> permissionLauncher2;
    ActivityResultLauncher<Intent> activityResultLauncher3;
    ActivityResultLauncher<String> permissionLauncher3;
    ActivityResultLauncher<Intent> activityResultLauncher4;
    ActivityResultLauncher<String> permissionLauncher4;
    private ArrayList<Uri> imageDatalist;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentHayvanDuzenleBinding.inflate(inflater, container, false);
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        imageDatalist=new ArrayList<>();
        kisilikBaslat();
        yasBaslat();
        HayvanDuzenleFragmentArgs bundle=HayvanDuzenleFragmentArgs.fromBundle(getArguments());
        hayvan=bundle.getHayvan();
        binding.editTextHayvanAdDuzenle.setText(hayvan.getAd());
        binding.autoCompleteTextViewDuzenle.setText(hayvan.getTur());
        binding.editTextHayvanIrkDuzenle.setText(hayvan.getIrk());
        String cinsiyet=hayvan.getCinsiyet();
        if (cinsiyet.equals("Erkek")){
            binding.radioButtonErkekDuzenle.setChecked(true);
        }else{
            binding.radioButtonDisiDuzenle.setChecked(true);
        }
        binding.autoCompleteTextViewHayvanYasDuzenle.setText(hayvan.getYas());
        binding.editTextHayvanSaglikDuzenle.setText(hayvan.getSaglik());
        binding.autoCompleteTextViewHayvanKisilikDuzenle.setText(hayvan.getKisilik());
        binding.editTextHayvanAciklamaDuzenle.setText(hayvan.getAciklama());
        //izin işlemleri
        registerLauncher();
        registerLauncher2();
        registerLauncher3();
        registerLauncher4();
        Picasso.get().load(hayvan.getFoto1()).into(binding.imageViewDuzenle);
        if (!hayvan.getFoto2().equals("null")){
            Picasso.get().load(hayvan.getFoto2()).into(binding.addImage2Duzenle);
        }
        if (!hayvan.getFoto3().equals("null")){
            Picasso.get().load(hayvan.getFoto3()).into(binding.addImage3Duzenle);
        }
        if (!hayvan.getFoto4().equals("null")){
            Picasso.get().load(hayvan.getFoto4()).into(binding.addImage4Duzenle);
        }
        binding.imageViewDuzenle.setOnClickListener(view -> {
            fotografTiklandi1(view);
        });
        binding.addImage2Duzenle.setOnClickListener(view -> {
            if (!binding.addImage2Duzenle.getResources().equals(R.drawable.background_photo)){
                fotografTiklandi2(view);
            }
        });
        binding.addImage3Duzenle.setOnClickListener(view -> {
            if (!binding.addImage3Duzenle.getResources().equals(R.drawable.background_photo)){
                fotografTiklandi3(view);
            }
        });
        binding.addImage4Duzenle.setOnClickListener(view -> {
            if (!binding.addImage4Duzenle.getResources().equals(R.drawable.background_photo)){
                fotografTiklandi4(view);
            }
        });

        binding.buttonHayvanEkleDuzenle.setOnClickListener(view -> {
            hayvanGuncelle(view);
        });





        return binding.getRoot();
    }

    private void hayvanGuncelle(View view) {
        if (imageData == null || binding.editTextHayvanAdDuzenle.getText().toString().isEmpty()
                || binding.autoCompleteTextViewDuzenle.getText().toString().isEmpty()
                || binding.editTextHayvanIrkDuzenle.getText().toString().isEmpty()
                || binding.radioGroupCinsiyetDuzenle.getCheckedRadioButtonId() == -1
                || binding.autoCompleteTextViewHayvanYasDuzenle.getText().toString().isEmpty()
                || binding.editTextHayvanSaglikDuzenle.getText().toString().isEmpty()
                || binding.autoCompleteTextViewHayvanKisilikDuzenle.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Açıklama hariç tüm alanları doldurmak zorunludur.", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.progressBarHayvanEkleDuzenle.setVisibility(View.VISIBLE);
        binding.buttonHayvanEkleDuzenle.setEnabled(false);

        imageDatalist.add(imageData);
        imageDatalist.add(imageData2);
        imageDatalist.add(imageData3);
        imageDatalist.add(imageData4);

        String[] fotoURLs = new String[4];
        for (int i = 0; i < 4; i++) {
            final int index = i;

            if (imageDatalist.get(i) != null) {
                UUID uuid = UUID.randomUUID();
                String path = "images/" + uuid + "_" + (i + 1) + ".jpg";

                // Resim yükleme işleminin başarı dinleyicisi
                storageReference.child(path).putFile(imageDatalist.get(i)).addOnSuccessListener(taskSnapshot -> {
                    // Resim yükleme işlemi başarılı olduğunda URL alma işlemini gerçekleştir
                    storageReference.child(path).getDownloadUrl().addOnSuccessListener(uri -> {
                        fotoURLs[index] = uri.toString();

                        // Tüm resimlerin yüklenmesini beklerip, URL'leri aldıktan sonra veritabanına kaydet
                        if (allURLsReceived(fotoURLs)) {
                            savedF(view, fotoURLs[0], fotoURLs[1], fotoURLs[2], fotoURLs[3]);
                        }
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "URL alınamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Resim yüklenemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                // Resim verisi yoksa, URL'yi "null" olarak ayarla
                fotoURLs[i] = "null";

                // Tüm resimlerin yüklenmesini beklerip, URL'leri aldıktan sonra veritabanına kaydet
                if (allURLsReceived(fotoURLs)) {
                    savedF(view, fotoURLs[0], fotoURLs[1], fotoURLs[2], fotoURLs[3]);
                }
            }
        }

    }
    private void savedF(View view, String foto, String foto2, String foto3, String foto4) {
        try {
            // Güncellenecek etkinliğin belirteci
            String etkinlikBelirteci = hayvan.getDocID(); // Etkinliği belirleyen bir ID kullanılmalıdır.

            // Yeni verileri bir haritada saklayalım
            HashMap<String, Object> updatedData = new HashMap<>();
            updatedData.put("ad", binding.editTextHayvanAdDuzenle.getText().toString());
            updatedData.put("tur", binding.autoCompleteTextViewDuzenle.getText().toString());
            updatedData.put("irk", binding.editTextHayvanIrkDuzenle.getText().toString());
            String cinsiyet = "";
            int cinsiyetID = binding.radioGroupCinsiyetDuzenle.getCheckedRadioButtonId();
            if (cinsiyetID == R.id.radioButtonErkek) {
                cinsiyet = "Erkek";
            } else {
                cinsiyet = "Dişi";
            }
            updatedData.put("cinsiyet", cinsiyet);
            updatedData.put("yas", binding.autoCompleteTextViewHayvanYasDuzenle.getText().toString());
            updatedData.put("saglik", binding.editTextHayvanSaglikDuzenle.getText().toString());
            updatedData.put("kisilik", binding.autoCompleteTextViewHayvanKisilikDuzenle.getText().toString());
            updatedData.put("aciklama", binding.editTextHayvanAciklamaDuzenle.getText().toString());
            if (!foto.equals("null")) {
                updatedData.put("foto1", foto);
            }
            if (!foto2.equals("null")) {
                updatedData.put("foto2", foto2);
            }
            if (!foto3.equals("null")) {
                updatedData.put("foto3", foto3);
            }
            if (!foto4.equals("null")) {
                updatedData.put("foto4", foto4);
            }

            // Firestore koleksiyonunda güncelleme işlemi
            firestore.collection("kullanici_hayvanlari").document(etkinlikBelirteci)
                    .update(updatedData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Güncellendi", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), MainActivity2.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Güncellenirken Bir Hata Oluştu: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnCompleteListener(task -> {
                        binding.progressBarHayvanEkleDuzenle.setVisibility(View.INVISIBLE);
                        binding.buttonHayvanEkleDuzenle.setEnabled(true);
                    });
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            binding.progressBarHayvanEkleDuzenle.setVisibility(View.INVISIBLE);
            binding.buttonHayvanEkleDuzenle.setEnabled(true);
        }
    }


    private boolean allURLsReceived(String[] urls) {
        for (String url : urls) {
            if (url == null) {
                return false;
            }
        }
        return true;
    }

    private void yasBaslat(){
        ArrayList<String> yas= new ArrayList<>();
        yas.add("Yavru");
        yas.add("Genç");
        yas.add("Orta");
        yas.add("Yaşlı");

        ArrayAdapter arrayAdapter= new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,yas);
        binding.autoCompleteTextViewHayvanYasDuzenle.setAdapter(arrayAdapter);
    }
    private void kisilikBaslat(){
        ArrayList<String> kisilik= new ArrayList<>();
        kisilik.add("Açıklık");
        kisilik.add("Sorumluluk");
        kisilik.add("Dışa Dönüklük");
        kisilik.add("Uyum");
        kisilik.add("Duyarlılık");

        ArrayAdapter arrayAdapter= new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,kisilik);
        binding.autoCompleteTextViewHayvanKisilikDuzenle.setAdapter(arrayAdapter);
    }
    public void fotografTiklandi1(View view) {
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
    public void fotografTiklandi2(View view) {
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
                            permissionLauncher2.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                }
                else{
                    //izin işlemleri
                    permissionLauncher2.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                }

            }
            else{
                //izin daha önceden verilmiş, galeriye git
                Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher2.launch(intentToGallery);
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
                    permissionLauncher2.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                }

            }
            else{
                //izin daha önceden verilmiş, galeriye git
                Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher2.launch(intentToGallery);
            }
        }
    }

    public void fotografTiklandi4(View view) {
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
                            permissionLauncher4.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                }
                else{
                    //izin işlemleri
                    permissionLauncher4.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                }

            }
            else{
                //izin daha önceden verilmiş, galeriye git
                Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher4.launch(intentToGallery);
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
                    permissionLauncher4.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                }

            }
            else{
                //izin daha önceden verilmiş, galeriye git
                Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher4.launch(intentToGallery);
            }
        }
    }
    public void fotografTiklandi3(View view) {
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
                            permissionLauncher3.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                }
                else{
                    //izin işlemleri
                    permissionLauncher3.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
                }

            }
            else{
                //izin daha önceden verilmiş, galeriye git
                Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher3.launch(intentToGallery);
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
                    permissionLauncher3.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                }

            }
            else{
                //izin daha önceden verilmiş, galeriye git
                Intent intentToGallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher3.launch(intentToGallery);
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
                        //binding.imageView.setImageURI(imageData);
                        try {
                            img  = ImageUtil.uriToBitmap(getContext(), imageData);
                            imageData=null;
                            // Bitmap'i kullanabilirsiniz

                            img = Bitmap.createScaledBitmap(img, 224, 224, true);
                            try {
                                Model model = Model.newInstance(getContext());

                                // Creates inputs for reference.
                                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
                                TensorImage tensorImage=new TensorImage(DataType.UINT8);
                                tensorImage.load(img);
                                ByteBuffer byteBuffer=tensorImage.getBuffer();
                                inputFeature0.loadBuffer(byteBuffer);

                                // Runs model inference and gets result.
                                Model.Outputs outputs = model.process(inputFeature0);
                                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                                StringBuilder resultBuilder = new StringBuilder();
                                float[] results = outputFeature0.getFloatArray(); // Sonuç dizisini al
                                for (int i = 0; i < results.length; i++) {
                                    float resultValue = results[i];
                                    resultBuilder.append("Result ").append(i).append(": ").append(resultValue).append("\n");

                                    // Belirli bir koşulu kontrol et ve uygun durumda işlem yap
                                    if ((i == 5 || i == 2 || i == 3 || i == 4) && resultValue == 255.0f) {
                                        Snackbar.make(getView(),"Yüklediğiniz fotoğraf kedi veya köpek fotoğrafı değil! Lütfen başka bir fotoğraf yükleyin.",Snackbar.LENGTH_LONG).show();
                                        binding.imageViewDuzenle.setImageResource(R.drawable.add_photo);
                                        imageData=null;
                                    } else if (i == 0 && resultValue == 255.f) {
                                        binding.autoCompleteTextViewDuzenle.setText("Kedi");
                                        binding.autoCompleteTextViewDuzenle.setEnabled(false);
                                        imageData = intentFromResult.getData();
                                        binding.imageViewDuzenle.setImageBitmap(img);
                                        binding.addImage2Duzenle.setImageResource(R.drawable.add_photo);
                                        binding.addImage2Duzenle.setEnabled(true);
                                    } else if (i == 1 && resultValue == 255.f) {
                                        binding.autoCompleteTextViewDuzenle.setText("Köpek");
                                        binding.autoCompleteTextViewDuzenle.setEnabled(false);
                                        imageData = intentFromResult.getData();
                                        binding.imageViewDuzenle.setImageBitmap(img);
                                        binding.addImage2Duzenle.setImageResource(R.drawable.add_photo);
                                        binding.addImage2Duzenle.setEnabled(true);
                                    }

                                }
                                // Releases model resources if no longer used.
                                model.close();
                            } catch (IOException e) {
                                // TODO Handle the exception
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


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

    private void registerLauncher2() {
        activityResultLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        imageData2 = intentFromResult.getData();
                        //binding.imageView.setImageURI(imageData);
                        try {
                            img  = ImageUtil.uriToBitmap(getContext(), imageData2);
                            imageData2=null;
                            // Bitmap'i kullanabilirsiniz

                            img = Bitmap.createScaledBitmap(img, 224, 224, true);
                            try {
                                Model model = Model.newInstance(getContext());

                                // Creates inputs for reference.
                                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
                                TensorImage tensorImage=new TensorImage(DataType.UINT8);
                                tensorImage.load(img);
                                ByteBuffer byteBuffer=tensorImage.getBuffer();
                                inputFeature0.loadBuffer(byteBuffer);
                                // Runs model inference and gets result.
                                Model.Outputs outputs = model.process(inputFeature0);
                                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                                StringBuilder resultBuilder = new StringBuilder();
                                float[] results = outputFeature0.getFloatArray();
                                String sonuc = "";
                                for (int i = 0; i < results.length; i++) {
                                    float resultValue = results[i];
                                    resultBuilder.append("Result ").append(i).append(": ").append(resultValue).append("\n");
                                    // Belirli bir koşulu kontrol et ve uygun durumda işlem yap
                                    if ((i == 5 || i == 2 || i == 3 || i == 4) && resultValue == 255.0f) {
                                        Snackbar.make(getView(),"Yüklediğiniz fotoğraf kedi veya köpek fotoğrafı değil! Lütfen başka bir fotoğraf yükleyin.",Snackbar.LENGTH_LONG).show();
                                        binding.addImage2Duzenle.setImageResource(R.drawable.add_photo);
                                        imageData2=null;
                                    }
                                    else if (i == 0 && resultValue == 255.f) {
                                        sonuc="Kedi";
                                        imageData2 = intentFromResult.getData();
                                        binding.addImage2Duzenle.setImageBitmap(img);
                                        binding.addImage3Duzenle.setImageResource(R.drawable.add_photo);
                                        binding.addImage3Duzenle.setEnabled(true);
                                    } else if (i == 1 && resultValue == 255.f) {
                                        sonuc="Köpek";
                                        imageData2 = intentFromResult.getData();
                                        binding.addImage2Duzenle.setImageBitmap(img);
                                        binding.addImage3Duzenle.setImageResource(R.drawable.add_photo);
                                        binding.addImage3Duzenle.setEnabled(true);
                                    }
                                    if (!sonuc.equals(binding.autoCompleteTextViewDuzenle.getText().toString())){
                                        Snackbar.make(getView(),"Yüklediğiniz fotoğrafta başka hayvan bulunuyor !",Snackbar.LENGTH_SHORT).show();
                                        binding.addImage2Duzenle.setImageResource(R.drawable.add_photo);
                                        imageData2=null;
                                        binding.addImage3Duzenle.setImageResource(R.drawable.backgorund_ekle_evcil);
                                        binding.addImage4Duzenle.setImageResource(R.drawable.backgorund_ekle_evcil);
                                    }

                                }
                                // Releases model resources if no longer used.
                                model.close();
                            } catch (IOException e) {
                                // TODO Handle the exception
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        permissionLauncher2 = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
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

    private void registerLauncher3() {
        activityResultLauncher3 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        imageData3 = intentFromResult.getData();
                        //binding.imageView.setImageURI(imageData);
                        try {
                            img  = ImageUtil.uriToBitmap(getContext(), imageData3);
                            imageData3=null;
                            // Bitmap'i kullanabilirsiniz

                            img = Bitmap.createScaledBitmap(img, 224, 224, true);
                            try {
                                Model model = Model.newInstance(getContext());

                                // Creates inputs for reference.
                                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
                                TensorImage tensorImage=new TensorImage(DataType.UINT8);
                                tensorImage.load(img);
                                ByteBuffer byteBuffer=tensorImage.getBuffer();
                                inputFeature0.loadBuffer(byteBuffer);

                                // Runs model inference and gets result.
                                Model.Outputs outputs = model.process(inputFeature0);
                                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                                StringBuilder resultBuilder = new StringBuilder();
                                float[] results = outputFeature0.getFloatArray();
                                String sonuc="";
                                for (int i = 0; i < results.length; i++) {
                                    float resultValue = results[i];
                                    resultBuilder.append("Result ").append(i).append(": ").append(resultValue).append("\n");
                                    // Belirli bir koşulu kontrol et ve uygun durumda işlem yap
                                    if ((i == 5 || i == 2 || i == 3 || i == 4) && resultValue == 255.0f) {
                                        Snackbar.make(getView(),"Yüklediğiniz fotoğraf kedi veya köpek fotoğrafı değil! Lütfen başka bir fotoğraf yükleyin.",Snackbar.LENGTH_LONG).show();
                                        binding.addImage3Duzenle.setImageResource(R.drawable.add_photo);
                                        imageData3=null;
                                    } else if (i == 0 && resultValue == 255.f) {
                                        sonuc="Kedi";
                                        imageData3 = intentFromResult.getData();
                                        binding.addImage3Duzenle.setImageBitmap(img);
                                        binding.addImage4Duzenle.setImageResource(R.drawable.add_photo);
                                        binding.addImage4Duzenle.setEnabled(true);
                                    } else if (i == 1 && resultValue == 255.f) {
                                        sonuc="Köpek";
                                        imageData3 = intentFromResult.getData();
                                        binding.addImage3Duzenle.setImageBitmap(img);
                                        binding.addImage4Duzenle.setImageResource(R.drawable.add_photo);
                                        binding.addImage4Duzenle.setEnabled(true);
                                    }
                                    if (!sonuc.equals(binding.autoCompleteTextViewDuzenle.getText().toString())){
                                        Snackbar.make(getView(),"Yüklediğiniz fotoğrafta başka hayvan bulunuyor !",Snackbar.LENGTH_SHORT).show();
                                        binding.addImage3Duzenle.setImageResource(R.drawable.add_photo);
                                        imageData3=null;
                                        binding.addImage4Duzenle.setImageResource(R.drawable.backgorund_ekle_evcil);
                                    }
                                }
                                // Releases model resources if no longer used.
                                model.close();
                            } catch (IOException e) {
                                // TODO Handle the exception
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        permissionLauncher3 = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
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

    private void registerLauncher4() {
        activityResultLauncher4 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent intentFromResult = result.getData();
                    if (intentFromResult != null) {
                        imageData4 = intentFromResult.getData();
                        //binding.imageView.setImageURI(imageData);
                        try {
                            img  = ImageUtil.uriToBitmap(getContext(), imageData4);
                            imageData4=null;
                            // Bitmap'i kullanabilirsiniz

                            img = Bitmap.createScaledBitmap(img, 224, 224, true);
                            try {
                                Model model = Model.newInstance(getContext());

                                // Creates inputs for reference.
                                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
                                TensorImage tensorImage=new TensorImage(DataType.UINT8);
                                tensorImage.load(img);
                                ByteBuffer byteBuffer=tensorImage.getBuffer();
                                inputFeature0.loadBuffer(byteBuffer);

                                // Runs model inference and gets result.
                                Model.Outputs outputs = model.process(inputFeature0);
                                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                                StringBuilder resultBuilder = new StringBuilder();
                                float[] results = outputFeature0.getFloatArray();
                                String sonuc="";
                                for (int i = 0; i < results.length; i++) {
                                    float resultValue = results[i];
                                    resultBuilder.append("Result ").append(i).append(": ").append(resultValue).append("\n");
                                    // Belirli bir koşulu kontrol et ve uygun durumda işlem yap
                                    if ((i == 5 || i == 2 || i == 3 || i == 4) && resultValue == 255.0f) {
                                        Snackbar.make(getView(),"Yüklediğiniz fotoğraf kedi veya köpek fotoğrafı değil! Lütfen başka bir fotoğraf yükleyin.",Snackbar.LENGTH_LONG).show();
                                        binding.addImage4Duzenle.setImageResource(R.drawable.add_photo);
                                        imageData4=null;
                                    } else if (i == 0 && resultValue == 255.f) {
                                        sonuc="Kedi";
                                        imageData4 = intentFromResult.getData();
                                        binding.addImage4Duzenle.setImageBitmap(img);
                                    } else if (i == 1 && resultValue == 255.f) {
                                        sonuc="Köpek";
                                        imageData4 = intentFromResult.getData();
                                        binding.addImage4Duzenle.setImageBitmap(img);
                                    }
                                    if (!sonuc.equals(binding.autoCompleteTextViewDuzenle.getText().toString())){
                                        Snackbar.make(getView(),"Yüklediğiniz fotoğrafta başka hayvan bulunuyor !",Snackbar.LENGTH_SHORT).show();
                                        binding.addImage4Duzenle.setImageResource(R.drawable.add_photo);
                                        imageData4=null;
                                    }
                                }
                                // Releases model resources if no longer used.
                                model.close();
                            } catch (IOException e) {
                                // TODO Handle the exception
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        permissionLauncher4 = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
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
}