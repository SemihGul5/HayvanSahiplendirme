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
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.ImageUtil;
import com.sgodi.bitirmeprojesi.databinding.FragmentEkleEvcilBinding;
import com.sgodi.bitirmeprojesi.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class EkleEvcilFragment extends Fragment{
    private FragmentEkleEvcilBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Uri imageData=null;
    FirebaseAuth auth;
    FirebaseStorage firebaseStorage;
    FirebaseFirestore firebaseFirestore;
    StorageReference storageReference;
    Bitmap img;
    String latitude,longitude,sehir,ilce;
    boolean secildiMi=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEkleEvcilBinding.inflate(inflater, container, false);
        binding.toolbarHayvanEkle.setTitle("Hayvan Ekle");
        // Tür, kisilik ve yaş bölümü başlatılması
        turBaslat();
        kisilikBaslat();
        yasBaslat();
        //
        registerLauncher();
        binding.imageView.setOnClickListener(view -> {
            fotografTiklandi(view);
        });
        // başlatılmalar
        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();// görseli depoda nereye kaydediceğimizi gösteren bir değişken


        Bundle bundle = getArguments();
        if (bundle != null) {
            latitude = bundle.getString("la");
            longitude = bundle.getString("lo");
            sehir = bundle.getString("sehir");
            ilce = bundle.getString("ilce");
            //binding.textView4.setText(latitude+"   "+longitude);
            if (latitude==null){
                //Toast.makeText(getContext(), "boş", Toast.LENGTH_SHORT).show();
                binding.imageViewCheckPass.setImageResource(R.drawable.close);
                secildiMi=false;
            }else{
                //Toast.makeText(getContext(), "dolu", Toast.LENGTH_SHORT).show();
                binding.imageViewCheckPass.setImageResource(R.drawable.check);
                secildiMi=true;
            }

            // Veri alındı, burada işlemleri yapabilirsiniz
        }

        if (secildiMi){
            binding.imageViewKonum.setEnabled(false);
        }


        //kaydet butonu tıklanması
        binding.buttonHayvanEkle.setOnClickListener(view -> {
            hayvan_kaydet(view);
        });



        binding.imageViewKonum.setOnClickListener(view -> {

            Navigation.findNavController(view).navigate(R.id.action_ekleEvcilFragment_to_mapsFragment);
        });


        return binding.getRoot();
    }



    private void turBaslat() {
        ArrayList<String> turler= new ArrayList<>();
        turler.add("Kedi");
        turler.add("Köpek");
        turler.add("Kuş");
        turler.add("Balık");
        turler.add("Hamster");
        turler.add("Tavşan");
        turler.add("Kaplumbağa");
        turler.add("Diğer");
        ArrayAdapter arrayAdapter= new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,turler);
        binding.autoCompleteTextView.setAdapter(arrayAdapter);
    }
    private void kisilikBaslat(){
        ArrayList<String> kisilik= new ArrayList<>();
        kisilik.add("Açıklık");
        kisilik.add("Sorumluluk");
        kisilik.add("Dışa Dönüklük");
        kisilik.add("Uyum");
        kisilik.add("Duyarlılık");

        ArrayAdapter arrayAdapter= new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,kisilik);
        binding.autoCompleteTextViewHayvanKisilik.setAdapter(arrayAdapter);
    }
    private void yasBaslat(){
        ArrayList<String> yas= new ArrayList<>();
        yas.add("Yavru");
        yas.add("Genç");
        yas.add("Orta");
        yas.add("Yaşlı");

        ArrayAdapter arrayAdapter= new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,yas);
        binding.autoCompleteTextViewHayvanYas.setAdapter(arrayAdapter);
    }


    private void hayvan_kaydet(View view) {
        if (imageData == null || binding.editTextHayvanAd.getText().toString().isEmpty()
                || binding.autoCompleteTextView.getText().toString().isEmpty()
                || binding.editTextHayvanIrk.getText().toString().isEmpty()
                || binding.radioGroupCinsiyet.getCheckedRadioButtonId() == -1
                || binding.autoCompleteTextViewHayvanYas.getText().toString().isEmpty()
                || binding.editTextHayvanSaglik.getText().toString().isEmpty()
                || binding.autoCompleteTextViewHayvanKisilik.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Açıklama hariç tüm alanları doldurmak zorunludur.", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.progressBarHayvanEkle.setVisibility(View.VISIBLE);
        binding.buttonHayvanEkle.setEnabled(false);
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
                    String ad = binding.editTextHayvanAd.getText().toString();
                    String tur = binding.autoCompleteTextView.getText().toString();
                    String irk = binding.editTextHayvanIrk.getText().toString();
                    String cinsiyet = "";
                    int cinsiyetID = binding.radioGroupCinsiyet.getCheckedRadioButtonId();
                    if (cinsiyetID == R.id.radioButtonErkek) {
                        cinsiyet = "Erkek";
                    } else {
                        cinsiyet = "Dişi";
                    }
                    String yas= binding.autoCompleteTextViewHayvanYas.getText().toString();
                    String saglik = binding.editTextHayvanSaglik.getText().toString();
                    String aciklama = binding.editTextHayvanAciklama.getText().toString().isEmpty() ? "yok" : binding.editTextHayvanAciklama.getText().toString();
                    String kisilik = binding.autoCompleteTextViewHayvanKisilik.getText().toString();
                    HashMap<String, Object> postData = new HashMap<>();
                    postData.put("email", email);
                    postData.put("aciklama", aciklama);
                    postData.put("ad", ad);
                    postData.put("cinsiyet", cinsiyet);
                    postData.put("foto", foto);
                    postData.put("kisilik", kisilik);
                    postData.put("saglik", saglik);
                    postData.put("tur", tur);
                    postData.put("yas", yas);
                    postData.put("ırk", irk);
                    postData.put("sahipli_mi","false");
                    postData.put("ilanda_mi","false");
                    postData.put("enlem",latitude);
                    postData.put("boylam",longitude);
                    postData.put("sehir",sehir);
                    postData.put("ilce",ilce);
                    //firebase koleksiyonuna yükleme işlemi ve sonucunun ne olduğunu değerlendirme
                    firebaseFirestore.collection("kullanici_hayvanlari").add(postData).addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                        temizle();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }).addOnCompleteListener(task -> {
                        binding.progressBarHayvanEkle.setVisibility(View.INVISIBLE);
                        binding.buttonHayvanEkle.setEnabled(true);
                    });
                    firebaseFirestore.collection("kullanici_hayvanlari_sahiplendir").add(postData).addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Kayıt Başarılı", Toast.LENGTH_SHORT).show();
                        temizle();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }).addOnCompleteListener(task -> {
                        binding.progressBarHayvanEkle.setVisibility(View.INVISIBLE);
                        binding.buttonHayvanEkle.setEnabled(true);
                    });
                });

            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                binding.progressBarHayvanEkle.setVisibility(View.INVISIBLE);
                binding.buttonHayvanEkle.setEnabled(true);

            });
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            binding.progressBarHayvanEkle.setVisibility(View.INVISIBLE);
            binding.buttonHayvanEkle.setEnabled(true);

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
                                for (int i = 0; i < outputFeature0.getFloatArray().length; i++) {
                                    float resultValue = outputFeature0.getFloatArray()[i];
                                    resultBuilder.append("Result ").append(i).append(": ").append(resultValue).append("\n");

                                    // Belirli bir koşulu kontrol et ve uygun durumda Toast mesajı göster
                                    if (i == 5 && resultValue == 255.0f||i==2 && resultValue == 255.0f||i==3 && resultValue == 255.0f||
                                    i==4 && resultValue == 255.0f) {
                                        Snackbar.make(getView(),"Yüklediğiniz fotoğraf kedi veya köpek fotoğrafı değil! Lütfen başka bir fotoğraf yükleyin.",Snackbar.LENGTH_LONG).show();
                                        binding.imageView.setImageResource(R.drawable.baseline_add_a_photo_24);
                                        imageData=null;
                                    }
                                    if(i==0 && resultValue==255.f){
                                        binding.autoCompleteTextView.setText("Kedi");
                                        binding.autoCompleteTextView.setEnabled(false);
                                    }
                                    if(i==1 && resultValue==255.f){
                                        binding.autoCompleteTextView.setText("Köpek");
                                        binding.autoCompleteTextView.setEnabled(false);
                                    }
                                    else{
                                        imageData = intentFromResult.getData();
                                        binding.imageView.setImageBitmap(img);
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

    private void temizle(){
        binding.autoCompleteTextView.clearListSelection();
        binding.imageView.setImageResource(R.drawable.baseline_add_a_photo_24);
        binding.radioButtonErkek.setChecked(false);
        binding.radioButtonDisi.setChecked(false);
        binding.autoCompleteTextViewHayvanKisilik.clearListSelection();
        binding.editTextHayvanAd.setText("");
        binding.editTextHayvanIrk.setText("");
        binding.autoCompleteTextViewHayvanYas.clearListSelection();
        binding.editTextHayvanSaglik.setText("");
        binding.editTextHayvanAciklama.setText("");
    }


}
