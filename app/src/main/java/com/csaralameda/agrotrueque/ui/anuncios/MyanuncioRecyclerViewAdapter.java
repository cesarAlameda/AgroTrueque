package com.csaralameda.agrotrueque.ui.anuncios;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.csaralameda.agrotrueque.R;
import com.csaralameda.agrotrueque.databinding.FragmentAnuncioBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


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
        holder.tvHoraAnuncio.setText(gethoraTiempo(mValues.get(position).getHora()));
        holder.tvUbicacionAnuncio.setText(mValues.get(position).getLocalizacion());
        holder.tvTituloAnuncio.setText(mValues.get(position).getDescripcion());
        holder.imgAnuncio.setImageBitmap(mValues.get(position).getFotoAnuncio());
        holder.tvCategoria.setText(mValues.get(position).getCategoria());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(v.getContext(), AnuncioDetalles.class);
                Log.d("anuncio",mValues.get(position).getIdAnuncio()+" ");
                intent.putExtra("anuncio",mValues.get(position).getIdAnuncio());
                v.getContext().startActivity(intent);


            }
        });


    }
    public static String gethoraTiempo(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date past = sdf.parse(dateString);
            Date now = new Date();

            long timeInMillis = now.getTime() - past.getTime();

            long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis);
            long hours = TimeUnit.MILLISECONDS.toHours(timeInMillis);
            long days = TimeUnit.MILLISECONDS.toDays(timeInMillis);
            long months = days / 30;


            if (minutes < 1) {
                return "Hace un momento";
            } else if (minutes == 1) {
                return "Hace 1 minuto";
            } else if (hours < 1) {
                return "Hace " + minutes + " minutos";
            } else if (hours == 1) {
                return "Hace 1 hora";
            } else if (days < 1) {
                return "Hace " + hours + " horas";
            } else if (days == 1) {
                return "Hace 1 día";
            } else if (months < 1) {
                return "Hace " + days + " días";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString; // Retorna el string original si hay error

        }
        return "";
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
        public final TextView tvCategoria;
        public ViewHolder(FragmentAnuncioBinding binding) {
            super(binding.getRoot());
            tvUbicacionAnuncio = binding.tvUbicacionAnuncio;
            tvHoraAnuncio = binding.tvHoraAnuncio;
            tvTituloAnuncio = binding.tvTituloAnuncio;
            imgAnuncio = binding.imgAnuncio;
            tvCategoria = binding.tvCategoria;
        }


    }


}