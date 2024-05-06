package com.sgodi.bitirmeprojesi.ui.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.Hayvan;
import com.sgodi.bitirmeprojesi.databinding.HayvanlarimCardTasarimiBinding;
import com.sgodi.bitirmeprojesi.ui.fragments.HayvanlarimFragmentDirections;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HayvanimAdapter extends RecyclerView.Adapter<HayvanimAdapter.HayvanimCardTutucu> {
    private Context mContext;
    private List<Hayvan> hayvanList;

    public HayvanimAdapter(Context mContext, List<Hayvan> hayvanList) {
        this.mContext = mContext;
        this.hayvanList = hayvanList;
    }

    @NonNull
    @Override
    public HayvanimCardTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HayvanlarimCardTasarimiBinding binding=HayvanlarimCardTasarimiBinding.inflate(LayoutInflater.from(mContext),parent,false);
        return new HayvanimCardTutucu(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HayvanimCardTutucu holder, int position) {
        //Karta foto, ad ve cinsiyet yazılması
        Hayvan hayvan= hayvanList.get(position);
        Picasso.get().load(hayvan.getFoto1()).resize(150,150)
                .into(holder.binding.imageViewHayvanimFoto);
        holder.binding.textViewHayvanimAd.setText(hayvan.getAd());
        holder.binding.textViewHayvanimKonum.setText(hayvan.getSehir()+" / "+hayvan.getIlce());

        // hayvan kart tasarımına tıklandığında
        holder.binding.hayvanimCard.setOnClickListener(view -> {
            gitAyrinti(view,hayvan,holder);
        });
        holder.binding.imageViewCardHayvanSil.setOnClickListener(view -> {
            if (hayvan.getSahipliMi().equals("false")){
                gitSil(view,hayvan,holder);
            }else{
                Toast.makeText(mContext,"Sahipli hayvan artık silinemez!",Toast.LENGTH_LONG).show();
            }

        });




    }


    private void gitSil(View view, Hayvan hayvan, HayvanimCardTutucu holder) {
        AlertDialog.Builder alert= new AlertDialog.Builder(mContext);
        alert.setTitle("Hayvanı sil");
        alert.setMessage("Hayvanı silmek istediğinizden emin misiniz ?");
        alert.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int pos=holder.getAdapterPosition();
                if(pos!=RecyclerView.NO_POSITION){
                    Hayvan hayvam=hayvanList.get(pos);
                    String docID=hayvan.getDocID();
                    deleteDocFromFirestore(docID);
                }
            }
        });
        alert.show();
    }

    private void deleteDocFromFirestore(String docID) {
        FirebaseFirestore firestore=FirebaseFirestore.getInstance();
        firestore.collection("kullanici_hayvanlari").document(docID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(mContext, "Hayvan başarıyla silindi.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        hayvanList.clear();
        notifyDataSetChanged();
    }

    private void gitAyrinti(View view, Hayvan hayvan, HayvanimCardTutucu holder) {
        HayvanlarimFragmentDirections.ActionHayvanlarimFragmentToHayvanimAyrintiFragment gecis=
                HayvanlarimFragmentDirections.actionHayvanlarimFragmentToHayvanimAyrintiFragment(hayvan);
        Navigation.findNavController(view).navigate(gecis);
    }


    @Override
    public int getItemCount() {
        return hayvanList.size();
    }


    public class HayvanimCardTutucu extends RecyclerView.ViewHolder{
        private HayvanlarimCardTasarimiBinding binding;

        public HayvanimCardTutucu(HayvanlarimCardTasarimiBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
