package com.rpl.muleh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rpl.muleh.R;
import com.rpl.muleh.method.GaleriTransportasi;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RVGaleriTransportasiAdapter extends RecyclerView.Adapter<RVGaleriTransportasiAdapter.MyViewHolder>{
    private Context mCtx;
    private List<GaleriTransportasi> mData;

    public RVGaleriTransportasiAdapter(Context mCtx, List<GaleriTransportasi> mData) {
        this.mCtx = mCtx;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(mCtx);
        view = layoutInflater.inflate(R.layout.item_preview_transportasi, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.with(mCtx)
                .load(mData.get(position).getImg())
                .centerCrop()
                .resize(400,150)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivPreviewTransportasi);
        }
    }
}
