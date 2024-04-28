package com.sgodi.bitirmeprojesi.ui.adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.AnasayfaIcerik;
import com.sgodi.bitirmeprojesi.data.models.AyarlarIcerik;
import com.sgodi.bitirmeprojesi.databinding.AyarlarListesiBinding;

import java.util.ArrayList;
import java.util.List;

public class AyarlarAdapter extends RecyclerView.Adapter<AyarlarAdapter.AyarlarCardTutucu> {

    private Context mContext;
    private List<AyarlarIcerik> ayarlarList;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;


    public AyarlarAdapter(Context mContext, List<AyarlarIcerik> ayarlarList,FirebaseFirestore firestore,FirebaseAuth auth) {
        this.mContext = mContext;
        this.ayarlarList = ayarlarList;
        this.firestore=firestore;
        this.auth=auth;
    }

    @NonNull
    @Override
    public AyarlarCardTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AyarlarListesiBinding binding=AyarlarListesiBinding.inflate(LayoutInflater.from(mContext),parent,false);

        // Firestore'dan belirli bir koşulu karşılayan belgeleri getirme ve switch bileşenini buna göre ayarlama
        firestore.collection("kullanicilar")
                .whereEqualTo("email", auth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            Boolean oneriDurumu = document.getBoolean("oneri_durum");
                            if (oneriDurumu != null && oneriDurumu) {
                                // oneri_durumu true ise switch'i açık yap
                                binding.switchAyarlar.setChecked(true);
                            } else {
                                // oneri_durumu false veya null ise switch'i kapalı yap
                                binding.switchAyarlar.setChecked(false);
                            }
                        }
                    }
                });

        return new AyarlarCardTutucu(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AyarlarCardTutucu holder, int position) {
        AyarlarIcerik icerik=ayarlarList.get(position);
        holder.binding.textViewAyarIsmi.setText(icerik.getBaslik());
        boolean durum=icerik.getSwitchDurum();
        if (durum){
            holder.binding.switchAyarlar.setVisibility(View.VISIBLE);
        }else{
            holder.binding.switchAyarlar.setVisibility(View.INVISIBLE);
        }
        holder.binding.ayarlarCard.setOnClickListener(view -> {
            listedenElemanTiklanmasi(view,holder,icerik);
        });
        holder.binding.switchAyarlar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Firestore'daki oneri_durumu değerini güncelleme
            firestore.collection("kullanicilar")
                    .whereEqualTo("email", auth.getCurrentUser().getEmail())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                document.getReference().update("oneri_durum", isChecked)
                                        .addOnSuccessListener(aVoid -> {
                                            //Snackbar.make(getView(), "Başarılı", Snackbar.LENGTH_SHORT).show();
                                            // Başarılı bir şekilde güncellendiğinde bir işlem yapılabilir
                                        })
                                        .addOnFailureListener(e -> {
                                            //Snackbar.make(getView(), "Başarısız", Snackbar.LENGTH_SHORT).show();
                                            // Güncelleme başarısız olduğunda bir işlem yapılabilir
                                        });
                            }
                        }
                    });
        });

    }

    private void listedenElemanTiklanmasi(View view, AyarlarCardTutucu holder, AyarlarIcerik icerik) {
        String secilen=icerik.getBaslik();
        if(secilen.equals("Profilim")) {
            Navigation.findNavController(view).navigate(R.id.action_ayarlarFragment_to_profilimFragment);
        } else if (secilen.equals("Kişilik Testi")) {
            Navigation.findNavController(view).navigate(R.id.action_ayarlarFragment_to_kisilikTestFragment);
        } else if (secilen.equals("Uygulamayı Paylaş")) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/developer?id=Abrebo+Studio");
            startActivity(mContext,Intent.createChooser(sharingIntent, "Paylaş"),null);

        } else if (secilen.equals("Bize Ulaşın")) {
            Navigation.findNavController(view).navigate(R.id.action_ayarlarFragment_to_bizeUlasFragment);
        } else if (secilen.equals("Çıkış Yap")) {
            cikisYap(view);
        } else if (secilen.equals("Bakıcı İlanımı kaldır")) {
            AlertDialog.Builder alert=new AlertDialog.Builder(mContext);
            alert.setTitle("İlanı kaldır");
            alert.setMessage("İlanı kaldırmak istediğinizden emin misiniz?");
            alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    bakiciIlanKaldir(firestore);
                    bakici_druum_guncelle(firestore);
                }
            });
            alert.show();
        } else if (secilen.equals("Mesajlar")) {
            Navigation.findNavController(view).navigate(R.id.action_ayarlarFragment_to_mesajListemFragment);
        } else if (secilen.equals("Kişiselleştirilmiş Öneriler")) {

        }
        else{
            Toast.makeText(mContext, "Hata", Toast.LENGTH_SHORT).show();
        }
    }

    private void bakiciIlanKaldir(FirebaseFirestore firestore) {
        firestore.collection("bakici")
                .whereEqualTo("email", auth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Her belgeyi silebilirsiniz
                            firestore.collection("bakici").document(document.getId()).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Silme işlemi başarılıysa yapılacak işlemleri burada gerçekleştirin
                                        Toast.makeText(mContext,"İlanınız başarılı bir şekilde kaldırıldı",Toast.LENGTH_LONG).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Silme işlemi başarısız olursa yapılacak işlemleri burada gerçekleştirin
                                        Toast.makeText(mContext,"İlanın silme işlemi sırasında hata meydana geldi",Toast.LENGTH_LONG).show();
                                    });
                        }
                    } else {
                        // Belge alınırken hata oluşursa yapılacak işlemleri burada gerçekleştirin
                        Toast.makeText(mContext,"İlanın belgesine ulaşılamadı",Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void cikisYap(View view) {
        try {
            AlertDialog.Builder alert=new AlertDialog.Builder(mContext);
            alert.setTitle("Çıkıs Yap");
            alert.setMessage("Emin misiniz?");
            alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(mContext, "Çıkış Yapılıyor.", Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(view).navigate(R.id.action_ayarlarFragment_to_mainActivity);

                    auth.signOut();

                }
            });
            alert.show();
        }catch (Exception e){
            Log.i("Çıkış hatası: ",e.getMessage());
        }


    }
    private void bakici_druum_guncelle( FirebaseFirestore firebaseFirestore) {
        Query query=firebaseFirestore.collection("kullanicilar").whereEqualTo("email",auth.getCurrentUser().getEmail());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    // Belirli bir kritere uyan belgeyi güncelle
                    String userId = document.getId();
                    firebaseFirestore.collection("kullanicilar").document(userId).update("bakici_durum", "false");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext,"Bakıcı durum güncellenemedi! "+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ayarlarList.size();
    }

    public class AyarlarCardTutucu extends RecyclerView.ViewHolder{
        private AyarlarListesiBinding binding;

        public AyarlarCardTutucu(AyarlarListesiBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
