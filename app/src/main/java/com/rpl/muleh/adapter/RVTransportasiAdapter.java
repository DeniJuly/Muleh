package com.rpl.muleh.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rpl.muleh.activity.DetailBusActivity;
import com.rpl.muleh.method.Transportasi;

import java.util.List;
import com.rpl.muleh.R;
import com.squareup.picasso.Picasso;

public class RVTransportasiAdapter extends RecyclerView.Adapter<RVTransportasiAdapter.MyViewHolder> {

    private Context mCtx;
    private List<Transportasi> mData;

    public RVTransportasiAdapter(Context mCtx, List<Transportasi> mData) {
        this.mCtx = mCtx;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        view = inflater.inflate(R.layout.item_transportasi,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.tv_nama.setText(mData.get(position).getNama());
        holder.tv_asal.setText("posisi: "+mData.get(position).getAsal());
        holder.tv_tujuan.setText("tujuan: "+mData.get(position).getTujuan());
        if(mData.get(position).getStatus() == "aktif"){
            holder.tv_status.setBackgroundResource(R.drawable.bg_status_transportsi_blue);
        }else if(mData.get(position).getStatus() == "tidak aktif"){
            holder.tv_status.setBackgroundResource(R.drawable.bg_status_transportsi_red);
        }
        holder.tv_status.setText(mData.get(position).getStatus());
        Picasso.with(mCtx)
                .load(mData.get(position).getFoto())
                .centerCrop()
                .resize(200,204)
                .into(holder.iv_Transportasi);
        holder.lLayoutTransportasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mCtx, DetailBusActivity.class);
                i.putExtra("id_user",mData.get(position).getIdUser());
                i.putExtra("id",mData.get(position).getId());
                i.putExtra("nama",mData.get(position).getNama());
                i.putExtra("foto",mData.get(position).getFoto());
                i.putExtra("nomor",mData.get(position).getNomor());
                mCtx.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_nama, tv_asal, tv_tujuan, tv_status;
        ImageView iv_Transportasi;
        LinearLayout lLayoutTransportasi;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_nama = itemView.findViewById(R.id.tv_NamaPengemudi);
            tv_asal = itemView.findViewById(R.id.tv_posisi);
            tv_tujuan = itemView.findViewById(R.id.tv_tujuan);
            tv_status = itemView.findViewById(R.id.tv_satus);
            iv_Transportasi = itemView.findViewById(R.id.ivTransportasi);
            lLayoutTransportasi = itemView.findViewById(R.id.lLayoutTransportasi);

        }
    }
}
