package com.csaralameda.agrotrueque.ui.chat;

import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.csaralameda.agrotrueque.R;
import com.csaralameda.agrotrueque.databinding.FragmentChatobjBinding;
import com.csaralameda.agrotrueque.ui.anuncios.AnuncioDetalles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MychatobjRecyclerViewAdapter extends RecyclerView.Adapter<MychatobjRecyclerViewAdapter.ViewHolder> {

    private final List<ChatObj> mValues;

    public MychatobjRecyclerViewAdapter(List<ChatObj> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentChatobjBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.nombreusuario.setText(mValues.get(position).getNombreUser());
        holder.ultimomensaje.setText(mValues.get(position).getUltimoMensaje());
        holder.horamensaje.setText(gethoraTiempo(mValues.get(position).getUltimoMensajeHora()));
        if(mValues.get(position).getFotoUser() == null) {
            holder.fotousuario.setImageResource(R.drawable.avatar);
        } else {
            holder.fotousuario.setImageBitmap(mValues.get(position).getFotoUser());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, Chat.class);
                intent.putExtra("OTROUSER", mValues.get(position).getIdUsuario());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView nombreusuario;
        public final TextView ultimomensaje;
        public final TextView horamensaje;
        public final ImageView fotousuario;
        public ChatObj mItem;

        public ViewHolder(FragmentChatobjBinding binding) {
            super(binding.getRoot());
            nombreusuario=binding.nombreUsuario;
            ultimomensaje=binding.ultimoMensaje;
            horamensaje=binding.horaMensaje;
            fotousuario=binding.fotoUsuario;
        }

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
}