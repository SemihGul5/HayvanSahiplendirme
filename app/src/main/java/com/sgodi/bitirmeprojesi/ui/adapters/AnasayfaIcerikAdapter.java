package com.sgodi.bitirmeprojesi.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sgodi.bitirmeprojesi.data.models.AnasayfaIcerik;
import com.sgodi.bitirmeprojesi.data.models.Hayvan;
import com.sgodi.bitirmeprojesi.databinding.AnasayfaCardTasarimBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AnasayfaIcerikAdapter extends RecyclerView.Adapter<AnasayfaIcerikAdapter.AnasayfaCardTutucu> {
    private Context mContext;
    private List<AnasayfaIcerik> anasayfaIcerikList;

    public AnasayfaIcerikAdapter(Context mContext, List<AnasayfaIcerik> anasayfaIcerikList) {
        this.mContext = mContext;
        this.anasayfaIcerikList = anasayfaIcerikList;
    }

    @NonNull
    @Override
    public AnasayfaCardTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AnasayfaCardTasarimBinding binding=AnasayfaCardTasarimBinding.inflate(LayoutInflater.from(mContext),parent,false);

        return new AnasayfaCardTutucu(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AnasayfaCardTutucu holder, int position) {
        AnasayfaIcerik icerik= new AnasayfaIcerik();
        icerik=anasayfaIcerikList.get(position);
        Picasso.get().load(icerik.getResim()).resize(150,100).into(holder.binding.imageViewAnasayfaIcerikResim);
        holder.binding.textViewAnasayfaAciklama.setText(icerik.getIcerik());
        holder.binding.textViewAnasayfaYaziBaslik.setText(icerik.getBaslik());

    }

    @Override
    public int getItemCount() {
        return anasayfaIcerikList.size();
    }


    public class AnasayfaCardTutucu extends RecyclerView.ViewHolder{
        private AnasayfaCardTasarimBinding binding;

        public AnasayfaCardTutucu(AnasayfaCardTasarimBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
