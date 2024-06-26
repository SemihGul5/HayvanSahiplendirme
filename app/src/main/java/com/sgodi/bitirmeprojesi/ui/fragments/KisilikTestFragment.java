package com.sgodi.bitirmeprojesi.ui.fragments;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.Hayvan;
import com.sgodi.bitirmeprojesi.databinding.FragmentKisilikTestBinding;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class KisilikTestFragment extends Fragment {
    private FragmentKisilikTestBinding binding;
    private OkHttpClient client;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    String aciklama="";

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentKisilikTestBinding.inflate(inflater, container, false);
        binding.materialToolbarAnket.setTitle("Kişilik Testi");
        client = new OkHttpClient();
        firestore=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();

        getKullaniciKisilik(firestore, auth, new KisilikCallback() {
            @Override
            public void onKisilikReceived(String kisilikValue) {
                if (!kisilikValue.equals("null")) {
                    binding.kisilikTestButton.setEnabled(false);
                    binding.anketDurumText.setTextColor(ContextCompat.getColor(requireContext(), R.color.yesil));
                    binding.anketDurumText.setText("Kişilik durum testi tamamlandı : " + kisilikValue+" : "+getKisilikAciklama(kisilikValue,kisilikValue));
                    kisilikGuncelle(firestore,auth,"true");
                } else {
                    binding.kisilikTestButton.setOnClickListener(view -> {
                        try {
                            if (!checkSeekBarValues()) {
                                Snackbar.make(view, "Tüm değerler 0'dan büyük olmalıdır", Snackbar.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), "İşleniyor...", Toast.LENGTH_SHORT).show();
                                tekrarDene();
                            }
                        }catch (Exception e){
                            Log.i("Mesaj",e.getMessage());
                        }

                    });
                }
            }
        });

        return binding.getRoot();
    }

    private void getKullaniciKisilik(FirebaseFirestore firestore, FirebaseAuth auth, KisilikCallback callback) {
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
                                String kisilik = document.getString("kişilik");
                                callback.onKisilikReceived(kisilik);
                            }
                        }
                    }
                });
    }

    // Geri arama (callback) arayüzü
    interface KisilikCallback {
        void onKisilikReceived(String kisilikValue);
    }
    private void kisilikGuncelle(FirebaseFirestore firestore, FirebaseAuth auth, String aTrue) {
        String email = auth.getCurrentUser().getEmail();

        // Kullanıcının e-posta adresine göre belgeyi sorgula
        firestore.collection("kullanicilar")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // İlgili belgeyi güncelle
                            String belgeId = document.getId();
                            firestore.collection("kullanicilar").document(belgeId)
                                    .update("kisilik_durum", aTrue)
                                    .addOnSuccessListener(aVoid -> {
                                        // Başarılı bir şekilde güncelleme yapıldığında yapılacak işlemler
                                        // Örneğin, kullanıcıya bilgi verme veya UI güncelleme
                                    })
                                    .addOnFailureListener(e -> {
                                        // Güncelleme sırasında hata oluştuğunda yapılacak işlemler
                                        // Örneğin, kullanıcıya hata mesajı gösterme
                                    });
                        }
                    } else {
                        // Belge bulunamadığında veya sorgulama başarısız olduğunda yapılacak işlemler
                        // Örneğin, kullanıcıya hata mesajı gösterme
                    }
                });
    }

    @SuppressLint("ResourceAsColor")
    private void sendTestResults() {
        // MediaType'ı ve seekbardan alınan değerleri kullanarak RequestBody oluşturuyoruz
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = createRequestBody(mediaType);

        // Request oluşturuyoruz
        Request request = new Request.Builder()
                .url("https://big-five-personality-test.p.rapidapi.com/submit")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("X-RapidAPI-Key", "")
                .addHeader("X-RapidAPI-Host", "")
                .build();

        // Request'i gönderiyoruz
        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    // Burada responseData'ı kullanarak istediğiniz işlemi yapabilirsiniz
                    // Örneğin, bu verileri bir TextView'da gösterebilirsiniz
                    getActivity().runOnUiThread(() -> {
                        try {
                            // JSON verisini JSONObject'e dönüştür
                            JSONObject jsonObject = new JSONObject(responseData);

                            // Olasılığı en yüksek olan özelliği bul
                            String highestProbabilityTrait = findHighestProbabilityTrait(jsonObject);

                            // TextView'da gösterme işlemi burada gerçekleştirilebilir
                            binding.anketDurumText.setText(highestProbabilityTrait+" : "+getKisilikAciklama(highestProbabilityTrait,highestProbabilityTrait));
                            binding.anketDurumText.setTextColor(ContextCompat.getColor(requireContext(), R.color.yesil));

                            kisilikDurumGuncelle(highestProbabilityTrait,firestore);
                            binding.kisilikTestButton.setEnabled(false);
                            Toast.makeText(getContext(), "Kişilik analizi tamamlandı", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    });
                } else {
                    // Request başarısız olduğunda yapılacak işlemler
                    //Snackbar.make(getView(),"Bu kez olmadı, tekrar deneyeceğiz. Lütfen bekleyiniz...",Snackbar.LENGTH_SHORT).show();
                    tekrarDene();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void tekrarDene() {
        binding.progressBarKisilik.setVisibility(View.VISIBLE);
        StringRequest stringRequest=new StringRequest(com.android.volley.Request.Method.POST, "https://bitirme-proje-309030c9a303.herokuapp.com/predict", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String data=jsonObject.getString("data");
                    String kisi="";
                    if (data.equals("0")){
                        kisi="Açıklık";
                    } else if (data.equals("1")) {
                        kisi="Sorumluluk";
                    } else if (data.equals("2")) {
                        kisi="Dışa Dönüklük";
                    } else if (data.equals("3")) {
                        kisi="Uyum";
                    } else if (data.equals("4")) {
                        kisi="Duyarlılık";
                    }  else if (data.equals("5")) {
                        kisi="Açıklık";
                    }
                    binding.anketDurumText.setText(kisi+" : "+getKisilikAciklama(kisi,kisi));
                    binding.anketDurumText.setTextColor(ContextCompat.getColor(requireContext(), R.color.yesil));

                    kisilikDurumGuncelle(kisi,firestore);
                    binding.kisilikTestButton.setEnabled(false);
                    Toast.makeText(getContext(), "Kişilik analizi tamamlandı", Toast.LENGTH_SHORT).show();
                    binding.progressBarKisilik.setVisibility(View.GONE);
                }catch (JSONException e){
                    e.printStackTrace();
                    binding.progressBarKisilik.setVisibility(View.GONE);
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params=new HashMap<String, String>();
                params.put("EXT1",String.valueOf(binding.ext1.getProgress()));
                params.put("EXT2",String.valueOf(binding.ext2.getProgress()));
                params.put("EXT3",String.valueOf(binding.ext3.getProgress()));
                params.put("EXT4",String.valueOf(binding.ext4.getProgress()));
                params.put("EXT5",String.valueOf(binding.ext5.getProgress()));
                params.put("EXT6",String.valueOf(binding.ext6.getProgress()));
                params.put("EXT7",String.valueOf(binding.ext7.getProgress()));
                params.put("EXT8",String.valueOf(binding.ext8.getProgress()));
                params.put("EXT9",String.valueOf(binding.ext9.getProgress()));
                params.put("EXT10",String.valueOf(binding.ext10.getProgress()));

                params.put("EST1",String.valueOf(binding.EST1.getProgress()));
                params.put("EST2",String.valueOf(binding.EST2.getProgress()));
                params.put("EST3",String.valueOf(binding.EST3.getProgress()));
                params.put("EST4",String.valueOf(binding.EST4.getProgress()));
                params.put("EST5",String.valueOf(binding.EST5.getProgress()));
                params.put("EST6",String.valueOf(binding.EST6.getProgress()));
                params.put("EST7",String.valueOf(binding.EST7.getProgress()));
                params.put("EST8",String.valueOf(binding.EST8.getProgress()));
                params.put("EST9",String.valueOf(binding.EST9.getProgress()));
                params.put("EST10",String.valueOf(binding.EST10.getProgress()));

                // AGR parametreleri
                params.put("AGR1", String.valueOf(binding.agr1.getProgress()));
                params.put("AGR2", String.valueOf(binding.agr2.getProgress()));
                params.put("AGR3", String.valueOf(binding.agr3.getProgress()));
                params.put("AGR4", String.valueOf(binding.agr4.getProgress()));
                params.put("AGR5", String.valueOf(binding.agr5.getProgress()));
                params.put("AGR6", String.valueOf(binding.agr6.getProgress()));
                params.put("AGR7", String.valueOf(binding.agr7.getProgress()));
                params.put("AGR8", String.valueOf(binding.agr8.getProgress()));
                params.put("AGR9", String.valueOf(binding.agr9.getProgress()));
                params.put("AGR10", String.valueOf(binding.agr10.getProgress()));
                // CSN parametreleri
                params.put("CSN1", String.valueOf(binding.csn1.getProgress()));
                params.put("CSN2", String.valueOf(binding.csn2.getProgress()));
                params.put("CSN3", String.valueOf(binding.csn3.getProgress()));
                params.put("CSN4", String.valueOf(binding.csn4.getProgress()));
                params.put("CSN5", String.valueOf(binding.csn5.getProgress()));
                params.put("CSN6", String.valueOf(binding.csn6.getProgress()));
                params.put("CSN7", String.valueOf(binding.csn7.getProgress()));
                params.put("CSN8", String.valueOf(binding.csn8.getProgress()));
                params.put("CSN9", String.valueOf(binding.csn9.getProgress()));
                params.put("CSN10", String.valueOf(binding.csn10.getProgress()));
                // OPN parametreleri
                params.put("OPN1", String.valueOf(binding.opn1.getProgress()));
                params.put("OPN2", String.valueOf(binding.opn2.getProgress()));
                params.put("OPN3", String.valueOf(binding.opn3.getProgress()));
                params.put("OPN4", String.valueOf(binding.opn4.getProgress()));
                params.put("OPN5", String.valueOf(binding.opn5.getProgress()));
                params.put("OPN6", String.valueOf(binding.opn6.getProgress()));
                params.put("OPN7", String.valueOf(binding.opn7.getProgress()));
                params.put("OPN8", String.valueOf(binding.opn8.getProgress()));
                params.put("OPN9", String.valueOf(binding.opn9.getProgress()));
                params.put("OPN10", String.valueOf(binding.opn10.getProgress()));

                return params;
            }
        };
        // İsteği Volley kuyruğuna ekleyin
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(stringRequest);
    }

    private void kisilikDurumGuncelle(String highestProbabilityTrait, FirebaseFirestore firestore) {
        Query query=firestore.collection("kullanicilar").whereEqualTo("email",auth.getCurrentUser().getEmail());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    // Belirli bir kritere uyan belgeyi güncelle
                    String userId = document.getId();
                    firestore.collection("kullanicilar").document(userId).update("kişilik", highestProbabilityTrait);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Bir şeyler ters gitti "+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getKisilikAciklama(String highestProbabilityTrait,String kisilik) {

        if(highestProbabilityTrait=="Açıklık"||kisilik=="Açıklık"){
            aciklama="Yeniliklere açık olma, hayal gücü, sanatsal hassasiyet, zeka ve maceraperestlik gibi özellikleri içerir. Açık kişiler yeni deneyimlerden hoşlanır, farklı düşüncelere ve duygulara açıktırlar.";
        } else if (highestProbabilityTrait=="Sorumluluk"||kisilik=="Sorumluluk") {
            aciklama="Disiplin, düzen, kararlılık, hedef odaklılık gibi özellikleri içerir. Sorumlu kişiler genellikle düzenli, organize ve güvenilir bireylerdir.";
        } else if (highestProbabilityTrait=="Dışa Dönüklük"||kisilik=="Dışa Dönüklük") {
            aciklama="Sosyallik, enerji, konuşkanlık, dışadönüklük ve risk alma gibi özellikleri içerir. Dışa dönük kişiler genellikle toplum içinde aktif, heyecanlı ve enerjik olarak tanımlanır.";
        } else if (highestProbabilityTrait=="Uyum"||kisilik=="Uyum") {
            aciklama=" İyi niyetlilik, yardımseverlik, işbirliğine açıklık, merhamet ve hoşgörü gibi özellikleri içerir. Uyumlu kişiler genellikle nazik, sempatik ve başkalarıyla uyum içinde olmaya eğilimlidirler.";
        } else if (highestProbabilityTrait=="Duyarlılık"||kisilik=="Duyarlılık") {
            aciklama="Kaygı, depresyon, duygusal dengesizlik gibi özellikleri içerir. Duyarlı bireyler stres ve olumsuz duygulara daha duyarlı olabilirler.";
        }
        return aciklama;
    }

    private String findHighestProbabilityTrait(JSONObject jsonObject) throws JSONException {
        // JSON verisinden trait'leri çıkar
        JSONObject openness = jsonObject.getJSONObject("Openness");
        JSONObject conscientiousness = jsonObject.getJSONObject("Conscientiousness");
        JSONObject extroversion = jsonObject.getJSONObject("Extroversion");
        JSONObject agreeableness = jsonObject.getJSONObject("Agreeableness");
        JSONObject neuroticism = jsonObject.getJSONObject("Neuroticism");

        // Her bir trait'in olasılığını al
        double opennessPercentage = openness.getDouble("percentage");
        double conscientiousnessPercentage = conscientiousness.getDouble("percentage");
        double extroversionPercentage = extroversion.getDouble("percentage");
        double agreeablenessPercentage = agreeableness.getDouble("percentage");
        double neuroticismPercentage = neuroticism.getDouble("percentage");

        // Olasılığı en yüksek olan trait'in adını bul
        double maxPercentage = Math.max(opennessPercentage, Math.max(conscientiousnessPercentage, Math.max(extroversionPercentage, Math.max(agreeablenessPercentage, neuroticismPercentage))));
        String highestProbabilityTrait = "";

        if (maxPercentage == opennessPercentage) {
            highestProbabilityTrait = "Açıklık";
        } else if (maxPercentage == conscientiousnessPercentage) {
            highestProbabilityTrait = "Sorumluluk";
        } else if (maxPercentage == extroversionPercentage) {
            highestProbabilityTrait = "Dışa Dönüklük";
        } else if (maxPercentage == agreeablenessPercentage) {
            highestProbabilityTrait = "Uyum";
        } else if (maxPercentage == neuroticismPercentage) {
            highestProbabilityTrait = "Duyarlılık";
        }

        return highestProbabilityTrait;
    }


    private RequestBody createRequestBody(MediaType mediaType) {
        // Değişkenlerinizi burada tanımlayabilirsiniz
        int[] seekBarValues = new int[]{
                binding.ext1.getProgress(),
                binding.ext2.getProgress(),
                binding.ext3.getProgress(),
                binding.ext4.getProgress(),
                binding.ext5.getProgress(),
                binding.ext6.getProgress(),
                binding.ext7.getProgress(),
                binding.ext8.getProgress(),
                binding.ext9.getProgress(),
                binding.ext10.getProgress(),

                binding.EST1.getProgress(),
                binding.EST2.getProgress(),
                binding.EST3.getProgress(),
                binding.EST4.getProgress(),
                binding.EST5.getProgress(),
                binding.EST6.getProgress(),
                binding.EST7.getProgress(),
                binding.EST8.getProgress(),
                binding.EST9.getProgress(),
                binding.EST10.getProgress(),

                binding.agr1.getProgress(),
                binding.agr2.getProgress(),
                binding.agr3.getProgress(),
                binding.agr4.getProgress(),
                binding.agr5.getProgress(),
                binding.agr6.getProgress(),
                binding.agr7.getProgress(),
                binding.agr8.getProgress(),
                binding.agr9.getProgress(),
                binding.agr10.getProgress(),

                binding.csn1.getProgress(),
                binding.csn2.getProgress(),
                binding.csn3.getProgress(),
                binding.csn4.getProgress(),
                binding.csn5.getProgress(),
                binding.csn6.getProgress(),
                binding.csn7.getProgress(),
                binding.csn8.getProgress(),
                binding.csn9.getProgress(),
                binding.csn10.getProgress(),

                binding.opn1.getProgress(),
                binding.opn2.getProgress(),
                binding.opn3.getProgress(),
                binding.opn4.getProgress(),
                binding.opn5.getProgress(),
                binding.opn6.getProgress(),
                binding.opn7.getProgress(),
                binding.opn8.getProgress(),
                binding.opn9.getProgress(),
                binding.opn10.getProgress(),


        };

        // JSON string oluşturmak için bir StringBuilder kullanabilirsiniz
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\r\n");
        jsonBuilder.append("    \"answers\": {\r\n");
        for (int i = 0; i < seekBarValues.length; i++) {
            jsonBuilder.append("        \"" + (i + 1) + "\": " + seekBarValues[i]);
            if (i < seekBarValues.length - 1) {
                jsonBuilder.append(",\r\n");
            } else {
                jsonBuilder.append("\r\n");
            }
        }
        jsonBuilder.append("    }\r\n");
        jsonBuilder.append("}");

        // JSON string oluşturulduktan sonra bu string'i RequestBody içine yerleştirebilirsiniz
        return RequestBody.create(mediaType, jsonBuilder.toString());
    }
    private boolean checkSeekBarValues() {
        // Tüm seekbarları al
        SeekBar ext1 = binding.ext1;
        SeekBar ext2 = binding.ext2;
        SeekBar ext3 = binding.ext3;
        SeekBar ext4 = binding.ext4;
        SeekBar ext5 = binding.ext5;
        SeekBar ext6 = binding.ext6;
        SeekBar ext7 = binding.ext7;
        SeekBar ext8 = binding.ext8;
        SeekBar ext9 = binding.ext9;
        SeekBar ext10 = binding.ext10;

        SeekBar est1 = binding.EST1;
        SeekBar est2 = binding.EST2;
        SeekBar est3 = binding.EST3;
        SeekBar est4 = binding.EST4;
        SeekBar est5 = binding.EST5;
        SeekBar est6 = binding.EST6;
        SeekBar est7 = binding.EST7;
        SeekBar est8 = binding.EST8;
        SeekBar est9 = binding.EST9;
        SeekBar est10 = binding.EST10;


        SeekBar agr1 = binding.agr1;
        SeekBar agr2 = binding.agr2;
        SeekBar agr3 = binding.agr3;
        SeekBar agr4 = binding.agr4;
        SeekBar agr5 = binding.agr5;
        SeekBar agr6 = binding.agr6;
        SeekBar agr7 = binding.agr7;
        SeekBar agr8 = binding.agr8;
        SeekBar agr9 = binding.agr9;
        SeekBar agr10 = binding.agr10;

        SeekBar csn1 = binding.csn1;
        SeekBar csn2 = binding.csn2;
        SeekBar csn3 = binding.csn3;
        SeekBar csn4 = binding.csn4;
        SeekBar csn5 = binding.csn5;
        SeekBar csn6 = binding.csn6;
        SeekBar csn7 = binding.csn7;
        SeekBar csn8 = binding.csn8;
        SeekBar csn9 = binding.csn9;
        SeekBar csn10 = binding.csn10;

        SeekBar opn1 = binding.opn1;
        SeekBar opn2 = binding.opn2;
        SeekBar opn3 = binding.opn3;
        SeekBar opn4 = binding.opn4;
        SeekBar opn5 = binding.opn5;
        SeekBar opn6 = binding.opn6;
        SeekBar opn7 = binding.opn7;
        SeekBar opn8 = binding.opn8;
        SeekBar opn9 = binding.opn9;
        SeekBar opn10 = binding.opn10;


        // Her bir seekbarın değerini kontrol et
        if (ext1.getProgress() <= 0 || ext2.getProgress() <= 0 || ext3.getProgress() <= 0|| ext4.getProgress() <= 0|| ext5.getProgress() <= 0
                || ext6.getProgress() <= 0|| ext7.getProgress() <= 0|| ext8.getProgress() <= 0|| ext9.getProgress() <= 0|| ext10.getProgress() <= 0
                || est1.getProgress() <= 0|| est2.getProgress() <= 0|| est3.getProgress() <= 0|| est4.getProgress() <= 0|| est5.getProgress() <= 0
                || est6.getProgress() <= 0|| est7.getProgress() <= 0|| est8.getProgress() <= 0|| est9.getProgress() <= 0|| est10.getProgress() <= 0
                || agr1.getProgress() <= 0|| agr2.getProgress() <= 0|| agr3.getProgress() <= 0|| agr4.getProgress() <= 0|| agr5.getProgress() <= 0
                || agr6.getProgress() <= 0|| agr7.getProgress() <= 0|| agr8.getProgress() <= 0|| agr9.getProgress() <= 0|| agr10.getProgress() <= 0
                || csn1.getProgress() <= 0|| csn2.getProgress() <= 0|| csn3.getProgress() <= 0|| csn4.getProgress() <= 0|| csn5.getProgress() <= 0
                || csn6.getProgress() <= 0|| csn7.getProgress() <= 0|| csn8.getProgress() <= 0|| csn9.getProgress() <= 0|| csn10.getProgress() <= 0
                || opn1.getProgress() <= 0|| opn2.getProgress() <= 0|| opn3.getProgress() <= 0|| opn4.getProgress() <= 0|| opn5.getProgress() <= 0
                || opn6.getProgress() <= 0|| opn7.getProgress() <= 0|| opn8.getProgress() <= 0|| opn9.getProgress() <= 0|| opn10.getProgress() <= 0) {
            return false; // En az bir seekbar değeri 0'dan küçükse false döndür
        }

        return true; // Tüm seekbar değerleri 0'dan büyükse true döndür
    }

}
