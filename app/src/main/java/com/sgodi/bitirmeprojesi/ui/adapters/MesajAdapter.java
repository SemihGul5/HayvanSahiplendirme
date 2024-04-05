package com.sgodi.bitirmeprojesi.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.sgodi.bitirmeprojesi.R;
import com.sgodi.bitirmeprojesi.data.models.Mesaj;
import com.sgodi.bitirmeprojesi.databinding.KisiMesajBinding;

import java.util.List;

public class MesajAdapter extends RecyclerView.Adapter<MesajAdapter.MesajHolder> {
    public static final int VIEW_GONDEREN=1;
    public static final int VIEW_ALAN=2;
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
        holder.binding.textViewMesajSaati.setText(mesaj.getSaat());

        if (mesaj.getOkunduMu().equals("true")){
            holder.binding.imageViewOkunduMu.setImageResource(R.drawable.baseline_check_24);
        }else{
            holder.binding.imageViewOkunduMu.setImageResource(R.drawable.baseline_check_gri);
        }

        // Mesajın gönderildiği tarafı belirle
        if (mesaj.getGonderen_email().equals(userEmail)) {
            // Mesaj kullanıcı tarafından gönderildiyse, sağa yasla ve arkaplan rengini ayarla
            holder.binding.cardMesaj.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            holder.binding.cardMesaj.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.yesil)));
            // Mesajın içeriğini sağa yaslamak için linearLayoutMesaj'ın yerçekimini ayarla
            holder.binding.layoutMesaj.setGravity(Gravity.END);
            holder.binding.linearLayoutMesaj.setGravity(Gravity.END);
        } else {
            // Mesaj kullanıcı tarafından gönderilmediyse, sola yasla
            holder.binding.cardMesaj.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            // Diğer durumlar için gerekli işlemleri buraya ekleyebilirsiniz
            holder.binding.cardMesaj.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.kahverengi)));
            // Mesajın içeriğini sağa yaslamak için linearLayoutMesaj'ın yerçekimini ayarla
            holder.binding.layoutMesaj.setGravity(Gravity.START);
            holder.binding.linearLayoutMesaj.setGravity(Gravity.START);
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
