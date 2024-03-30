package com.sgodi.bitirmeprojesi.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.Hayvan;
import com.sgodi.bitirmeprojesi.databinding.SahiplendirHayvanlarCardBinding;
import com.sgodi.bitirmeprojesi.ui.fragments.HayvanlarimFragmentDirections;
import com.sgodi.bitirmeprojesi.ui.fragments.SahiplenFragmentDirections;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SahiplendirAdapter extends RecyclerView.Adapter<SahiplendirAdapter.SahiplendirCardTutucu> {
    private Context context;
    private List<Hayvan> hayvanList;

    public SahiplendirAdapter(Context context, List<Hayvan> hayvanList) {
        this.context = context;
        this.hayvanList = hayvanList;
    }

    @NonNull
    @Override
    public SahiplendirCardTutucu onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SahiplendirHayvanlarCardBinding binding=SahiplendirHayvanlarCardBinding.inflate(LayoutInflater.from(context),parent,false);
        return new SahiplendirCardTutucu(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SahiplendirCardTutucu holder, int position) {
        Hayvan hayvan= hayvanList.get(position);
        Picasso.get().load(hayvan.getFoto()).resize(150,150)
                .into(holder.binding.imageViewHayvanimFoto);
        holder.binding.textViewHayvanimAd.setText(hayvan.getAd());
        holder.binding.textViewHayvanimKonum.setText(hayvan.getSehir()+" / "+hayvan.getIlce());
        if (hayvan.getCinsiyet().equals("Erkek")){
            holder.binding.imageViewCinsiyet.setImageResource(R.drawable.mars);
        }else{
            holder.binding.imageViewCinsiyet.setImageResource(R.drawable.femenine);
        }


        // hayvan kart tasarımına tıklandığında
        holder.binding.sahiplendirCard.setOnClickListener(view -> {
            gitAyrinti(view,hayvan,holder);
        });

        String sahipliMi=hayvan.getSahipliMi();
        if(sahipliMi.equals("true")){
            holder.binding.textViewSahiplendiMi.setText("SAHİPLENDİRİLDİ");
        }
    }

    private void gitAyrinti(View view, Hayvan hayvan, SahiplendirCardTutucu holder) {
        SahiplenFragmentDirections.ActionSahiplenFragmentToSahiplendirAyrintiFragment gecis=
                SahiplenFragmentDirections.actionSahiplenFragmentToSahiplendirAyrintiFragment(hayvan);
        Navigation.findNavController(view).navigate(gecis);
    }

    @Override
    public int getItemCount() {
        return hayvanList.size();
    }

    public class SahiplendirCardTutucu extends RecyclerView.ViewHolder{
        private SahiplendirHayvanlarCardBinding binding;

        public SahiplendirCardTutucu(SahiplendirHayvanlarCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
