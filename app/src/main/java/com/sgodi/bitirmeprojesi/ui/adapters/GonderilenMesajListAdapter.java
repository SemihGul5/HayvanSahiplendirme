package com.sgodi.bitirmeprojesi.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sgodi.bitirmeprojesi.data.models.Bakici;
import com.sgodi.bitirmeprojesi.data.models.Kullanici;
import com.sgodi.bitirmeprojesi.databinding.MesajlarimListesiBinding;

import java.util.List;

public class GonderilenMesajListAdapter extends RecyclerView.Adapter<GonderilenMesajListAdapter.GonderilenMesajListesiCardHolder> {

    private Context context;
    private List<Bakici> bakiciList;

    public GonderilenMesajListAdapter(Context context, List<Bakici> bakiciList) {
        this.context = context;
        this.bakiciList = bakiciList;
    }
    public void add(Bakici bakici){
        bakiciList.add(bakici);
    }
    public void clear(){
        bakiciList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GonderilenMesajListesiCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MesajlarimListesiBinding binding=MesajlarimListesiBinding.inflate(LayoutInflater.from(context),parent,false);
        return new GonderilenMesajListesiCardHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GonderilenMesajListesiCardHolder holder, int position) {
        Bakici bakici= bakiciList.get(position);
        holder.binding.textViewAliciAdSoyad.setText(bakici.getAd()+" "+bakici.getSoyad());
        holder.binding.textViewSonGonderilenMesaj.setText(bakici.getKisilik());

        holder.binding.cardMesajlarLsitesi.setOnClickListener(view -> {
            //o konuşmayı aç
        });
    }

    @Override
    public int getItemCount() {
        return bakiciList.size();
    }

    public class GonderilenMesajListesiCardHolder extends RecyclerView.ViewHolder{
        private MesajlarimListesiBinding binding;


        public GonderilenMesajListesiCardHolder(MesajlarimListesiBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
