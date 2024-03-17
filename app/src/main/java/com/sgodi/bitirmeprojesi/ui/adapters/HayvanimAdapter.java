package com.sgodi.bitirmeprojesi.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

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
        Picasso.get().load(hayvan.getFoto()).resize(150,150)
                .into(holder.binding.imageViewHayvanimFoto);
        holder.binding.textViewHayvanimAd.setText(hayvan.getAd());
        holder.binding.textViewHayvanimCinsiyet.setText(hayvan.getCinsiyet());

        // hayvan kart tasarımına tıklandığında
        holder.binding.hayvanimCard.setOnClickListener(view -> {
            gitAyrinti(view,hayvan,holder);
        });


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
