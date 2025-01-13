package com.csaralameda.agrotrueque.ui.anuncios;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.csaralameda.agrotrueque.R;
import com.csaralameda.agrotrueque.databinding.FragmentAnuncioBinding;

import java.util.List;


public class    MyanuncioRecyclerViewAdapter extends RecyclerView.Adapter<MyanuncioRecyclerViewAdapter.ViewHolder> {

    private final List<Anuncio> mValues;

    public MyanuncioRecyclerViewAdapter(List<Anuncio> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentAnuncioBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.tvHoraAnuncio.setText(mValues.get(position).getHora());
        holder.tvUbicacionAnuncio.setText(mValues.get(position).getLocalizacion());
        holder.tvTituloAnuncio.setText(mValues.get(position).getDescripcion());
        holder.imgAnuncio.setImageBitmap(mValues.get(position).getFotoAnuncio());

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvUbicacionAnuncio;
        public final TextView tvHoraAnuncio;
        public final TextView tvTituloAnuncio;
        public final ImageView imgAnuncio;
        public Anuncio mItem;

        public ViewHolder(FragmentAnuncioBinding binding) {
            super(binding.getRoot());
            tvUbicacionAnuncio = binding.tvUbicacionAnuncio;
            tvHoraAnuncio = binding.tvHoraAnuncio;
            tvTituloAnuncio = binding.tvTituloAnuncio;
            imgAnuncio = binding.imgAnuncio;
        }


    }
}