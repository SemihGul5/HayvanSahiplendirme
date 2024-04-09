package com.sgodi.bitirmeprojesi.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sgodi.bitirmeprojesi.data.models.Bakici;
import com.sgodi.bitirmeprojesi.data.models.Kullanici;
import com.sgodi.bitirmeprojesi.databinding.MesajlarimListesiBinding;
import com.sgodi.bitirmeprojesi.ui.fragments.MesajListemFragment;
import com.sgodi.bitirmeprojesi.ui.fragments.MesajListemFragmentDirections;

import java.util.Date;
import java.util.List;

public class GonderilenMesajListAdapter extends RecyclerView.Adapter<GonderilenMesajListAdapter.GonderilenMesajListesiCardHolder> {

    private Context context;
    private List<Kullanici> bakiciList;
    private FirebaseFirestore firestore;

    public GonderilenMesajListAdapter(Context context, List<Kullanici> bakiciList,FirebaseFirestore firestore) {
        this.context = context;
        this.bakiciList = bakiciList;
        this.firestore=firestore;
    }
    public void add(Kullanici bakici){
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
        firestore=FirebaseFirestore.getInstance();
        Kullanici bakici= bakiciList.get(position);
        holder.binding.textViewAliciAdSoyad.setText(bakici.getAd()+" "+bakici.getSoyad());

        holder.binding.cardMesajlarLsitesi.setOnClickListener(view -> {
            //o konuşmayı aç
            MesajListemFragmentDirections.ActionMesajListemFragmentToMesajFragment gecis=
                    MesajListemFragmentDirections.actionMesajListemFragmentToMesajFragment(bakici);
            Navigation.findNavController(view).navigate(gecis);
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
