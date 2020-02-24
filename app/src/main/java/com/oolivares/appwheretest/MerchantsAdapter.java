package com.oolivares.appwheretest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

class MerchantsAdapter extends RecyclerView.Adapter<MerchantsAdapter.MyViewHolder>  {
    private final Context context;
    private final List<Merchants> merchants;

    public MerchantsAdapter(Context context, List<Merchants> merchants) {
        this.context = context;
        this.merchants = merchants;
    }

    @NonNull
    @Override
    public MerchantsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MerchantsAdapter.MyViewHolder holder, int position) {
        Merchants merchant = merchants.get(position);
        holder.descrip.setText(merchant.getMerchantAddress());
        holder.number.setText(merchant.getMerchantTelephone());
        holder.titulo.setText(merchant.getMerchantName());
        Picasso.get().load(R.mipmap.logo_appwhere_transparente_300).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return merchants.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView titulo;
        private TextView descrip;
        private TextView number;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_place);
            titulo = itemView.findViewById(R.id.titulo);
            descrip = itemView.findViewById(R.id.descrip);
            number = itemView.findViewById(R.id.number);
        }
    }
}
