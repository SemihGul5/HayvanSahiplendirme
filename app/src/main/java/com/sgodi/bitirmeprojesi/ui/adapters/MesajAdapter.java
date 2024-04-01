package com.sgodi.bitirmeprojesi.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.Mesaj;
import com.sgodi.bitirmeprojesi.databinding.KisiMesajBinding;

import java.util.List;

public class MesajAdapter extends RecyclerView.Adapter<MesajAdapter.MesajHolder> {
    private Context context;
    private List<Mesaj> mesajs;

    // Kullanıcının e-posta adresi
    private String userEmail;

    public MesajAdapter(Context context, List<Mesaj> mesajs, String userEmail) {
        this.context = context;
        this.mesajs = mesajs;
        this.userEmail = userEmail;
    }

    @NonNull
    @Override
    public MesajHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        KisiMesajBinding binding = KisiMesajBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MesajHolder(binding);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MesajHolder holder, int position) {
        Mesaj mesaj = mesajs.get(position);
        holder.binding.textViewCardMesaj.setText(mesaj.getMesaj());

        // Mesajın gönderildiği tarafı belirle
        if (mesaj.getGonderen_email().equals(userEmail)) {
            // Mesaj kullanıcı tarafından gönderildiyse, sağa yasla
            holder.binding.cardMesaj.setBackgroundTintList(ColorStateList.valueOf(R.color.turuncu));

        } else {
            // Mesaj kullanıcı tarafından gönderilmediyse, sola yasla
            holder.binding.cardMesaj.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }
    }

    @Override
    public int getItemCount() {
        return mesajs.size();
    }

    public class MesajHolder extends RecyclerView.ViewHolder {
        private KisiMesajBinding binding;

        public MesajHolder(KisiMesajBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
