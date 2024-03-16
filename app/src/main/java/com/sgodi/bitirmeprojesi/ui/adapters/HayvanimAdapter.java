package com.sgodi.bitirmeprojesi.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sgodi.bitirmeprojesi.data.models.Hayvan;
import com.sgodi.bitirmeprojesi.databinding.HayvanlarimCardTasarimiBinding;
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
        Picasso.get().load(hayvanList.get(position).getFoto()).resize(150,150)
                .into(holder.binding.imageViewHayvanimFoto);
        holder.binding.textViewHayvanimAd.setText(hayvanList.get(position).getAd());
        holder.binding.textViewHayvanimCinsiyet.setText(hayvanList.get(position).getCinsiyet());



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
