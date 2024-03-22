package com.sgodi.bitirmeprojesi.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.sgodi.bitirmeprojesi.data.models.Bakici;
import com.sgodi.bitirmeprojesi.databinding.BakiciCardBinding;
import com.sgodi.bitirmeprojesi.ui.fragments.BakiciFragmentDirections;
import com.sgodi.bitirmeprojesi.ui.fragments.HayvanlarimFragmentDirections;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BakiciAdapter extends RecyclerView.Adapter<BakiciAdapter.BakiciCardHolder> {

    private Context mContext;
    private List<Bakici> bakiciList;

    public BakiciAdapter(Context mContext, List<Bakici> bakiciList) {
        this.mContext = mContext;
        this.bakiciList = bakiciList;
    }

    @NonNull
    @Override
    public BakiciCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BakiciCardBinding binding=BakiciCardBinding.inflate(LayoutInflater.from(mContext),parent,false);

        return new BakiciCardHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BakiciCardHolder holder, int position) {
        //Karta foto, ad ve konum yazılması
        Bakici bakici= bakiciList.get(position);

        Picasso.get().load(bakici.getFoto()).into(holder.binding.imageViewCardBakiciFoto);
        holder.binding.textViewCardBakiciAd.setText(bakici.getAd());
        holder.binding.textViewBakiciKonum.setText(bakici.getKonum());

        // hayvan kart tasarımına tıklandığında
        holder.binding.bakiciCard.setOnClickListener(view -> {
            gitAyrinti(view,bakici,holder);
        });
    }
    private void gitAyrinti(View view, Bakici bakici, BakiciCardHolder holder) {
        BakiciFragmentDirections.ActionBakiciFragmentToBakiciAyrintiFragment gecis
                =BakiciFragmentDirections.actionBakiciFragmentToBakiciAyrintiFragment(bakici);
        Navigation.findNavController(view).navigate(gecis);
    }

    @Override
    public int getItemCount() {
        return bakiciList.size();
    }

    public class BakiciCardHolder extends RecyclerView.ViewHolder{
        private BakiciCardBinding binding;

        public BakiciCardHolder(BakiciCardBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
