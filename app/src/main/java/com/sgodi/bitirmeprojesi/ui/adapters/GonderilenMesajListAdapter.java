package com.sgodi.bitirmeprojesi.ui.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sgodi.bitirmeprojesi.databinding.MesajlarimListesiBinding;

public class GonderilenMesajListAdapter extends RecyclerView.Adapter<GonderilenMesajListAdapter.GonderilenMesajListesiCardHolder> {


    @NonNull
    @Override
    public GonderilenMesajListesiCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull GonderilenMesajListesiCardHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class GonderilenMesajListesiCardHolder extends RecyclerView.ViewHolder{
        private MesajlarimListesiBinding binding;


        public GonderilenMesajListesiCardHolder(MesajlarimListesiBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
