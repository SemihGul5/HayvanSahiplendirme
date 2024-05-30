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
import com.sgodi.bitirmeprojesi.MainActivity;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.AyarlarIcerik;
import com.sgodi.bitirmeprojesi.databinding.AyarlarListesiBinding;

import java.util.List;
import java.util.Objects;

public class AyarlarAdapter extends RecyclerView.Adapter<AyarlarAdapter.AyarlarCardTutucu> {

    private Context mContext;
    private List<AyarlarIcerik> ayarlarList;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private Boolean oneri;


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

        try {
            // Firestore'dan belirli bir koşulu karşılayan belgeleri getirme ve switch bileşenini buna göre ayarlama
            firestore.collection("kullanicilar")
                    .whereEqualTo("email", Objects.requireNonNull(auth.getCurrentUser()).getEmail())
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
        }catch (Exception e){
            Log.i("Mesaj",e.getMessage());
        }


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

        ayarlarIconAyarlama(icerik,holder);
        try {
            holder.binding.switchAyarlar.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Firestore'daki oneri_durumu değerini güncelleme
                firestore.collection("kullanicilar")
                        .whereEqualTo("email", Objects.requireNonNull(auth.getCurrentUser()).getEmail())
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
        }catch (Exception e){
            Log.i("Mesaj",e.getMessage());
        }


    }

    private void ayarlarIconAyarlama(AyarlarIcerik icerik, AyarlarCardTutucu holder) {
        if (icerik.getBaslik().equals("Profilim")){
            holder.binding.imageViewAyarlarIcon.setImageResource(R.drawable.baseline_person_24);
        } else if (icerik.getBaslik().equals("Kişilik Testi")) {
            holder.binding.imageViewAyarlarIcon.setImageResource(R.drawable.testing);
        }
        else if (icerik.getBaslik().equals("Mesajlar")) {
            holder.binding.imageViewAyarlarIcon.setImageResource(R.drawable.baseline_message_24);
        }
        else if (icerik.getBaslik().equals("Bakıcı İlanımı Kaldır")) {
            holder.binding.imageViewAyarlarIcon.setImageResource(R.drawable.baseline_bookmark_remove_24);
        }
        else if (icerik.getBaslik().equals("Kişiselleştirilmiş Öneriler")) {
            holder.binding.imageViewAyarlarIcon.setImageResource(R.drawable.oneriler);
        }
        else if (icerik.getBaslik().equals("Kaydedilen Hayvanlar")) {
            holder.binding.imageViewAyarlarIcon.setImageResource(R.drawable.baseline_bookmarks_24);
        }
        else if (icerik.getBaslik().equals("Hayvan Kişilikleri")) {
            holder.binding.imageViewAyarlarIcon.setImageResource(R.drawable.black_cat);
        }
        else if (icerik.getBaslik().equals("Uygulamayı Paylaş")) {
            holder.binding.imageViewAyarlarIcon.setImageResource(R.drawable.baseline_share_24);
        }
        else if (icerik.getBaslik().equals("Bize Ulaşın")) {
            holder.binding.imageViewAyarlarIcon.setImageResource(R.drawable.baseline_email_24);
        }
        else if (icerik.getBaslik().equals("Çıkış Yap")) {
            holder.binding.imageViewAyarlarIcon.setImageResource(R.drawable.baseline_logout_24);
        }
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
            cikisYap(view,auth);
        } else if (secilen.equals("Bakıcı İlanımı Kaldır")) {
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
        else if (secilen.equals("Kaydedilen Hayvanlar")) {
            Navigation.findNavController(view).navigate(R.id.action_ayarlarFragment_to_kaydedilenlerFragment);
        }
        else if (secilen.equals("Hayvan Kişilikleri")) {
            Navigation.findNavController(view).navigate(R.id.action_ayarlarFragment_to_hayvanimKisilikYonergeleriFragment);
        }
        else{
            Toast.makeText(mContext, "Hata", Toast.LENGTH_SHORT).show();
        }
    }

    private void bakiciIlanKaldir(FirebaseFirestore firestore) {
        firestore.collection("bakici")
                .whereEqualTo("email", Objects.requireNonNull(auth.getCurrentUser()).getEmail())
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

    private void cikisYap(View view, FirebaseAuth auth) {
        try {
            AlertDialog.Builder alert=new AlertDialog.Builder(mContext);
            alert.setTitle("Çıkıs Yap");
            alert.setMessage("Emin misiniz?");
            alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (auth.getCurrentUser()!=null){
                        Toast.makeText(mContext, "Çıkış Yapılıyor.", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(mContext, MainActivity.class);
                        startActivity(mContext,intent,null);
                        //Navigation.findNavController(view).navigate(R.id.action_ayarlarFragment_to_mainActivity);

                        auth.signOut();
                    }else{
                        Toast.makeText(mContext, "hata.", Toast.LENGTH_SHORT).show();
                    }


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