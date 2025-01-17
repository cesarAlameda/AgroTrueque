package com.csaralameda.agrotrueque.ui.anuncios.misanuncios;

import static androidx.core.content.ContextCompat.startActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.csaralameda.agrotrueque.DataService.RetrofitClient;
import com.csaralameda.agrotrueque.Interfaces.ApiService;
import com.csaralameda.agrotrueque.databinding.ActivityCrearAnuncioBinding;
import com.csaralameda.agrotrueque.ui.anuncios.Anuncio;
import com.csaralameda.agrotrueque.databinding.FragmentMianuncioBinding;
import com.csaralameda.agrotrueque.ui.anuncios.CrearAnuncio;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MymiAnuncioRecyclerViewAdapter extends RecyclerView.Adapter<MymiAnuncioRecyclerViewAdapter.ViewHolder> {

    private final List<Anuncio> mValues;

    public MymiAnuncioRecyclerViewAdapter(List<Anuncio> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentMianuncioBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.imgmianuncio.setImageBitmap(mValues.get(position).getFotoAnuncio());
        holder.tvdesc.setText(mValues.get(position).getDescripcion());
        holder.tvubimianuncio.setText(mValues.get(position).getLocalizacion());
        holder.tvhoranuncio.setText(gethoraTiempo(mValues.get(position).getHora()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), CrearAnuncio.class);

                intent.putExtra("Editando", true);
                intent.putExtra("idAnuncio", mValues.get(position).getIdAnuncio());
                v.getContext().startActivity(intent);

            }
        });



        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Confirmación")
                        .setMessage("¿Deseas eliminar este anuncio?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                eliminarAnuncio(mValues.get(position).getIdAnuncio());


                            }

                            private void eliminarAnuncio(int idAnuncio) {

                                Retrofit retrofit = RetrofitClient.getClient("https://silver-goose-817541.hostingersite.com/");
                                ApiService apiService = retrofit.create(ApiService.class);

                                Call<JsonObject> call = apiService.deleteanuncio(idAnuncio);

                                call.enqueue(new Callback<JsonObject>() {
                                    @Override
                                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            Log.d("RESPUESTA_BODY", response.body().toString());
                                            String status = response.body().get("status").getAsString();
                                            String mensaje = response.body().get("message").getAsString();

                                            if ("success".equals(status)) {

                                                Toast.makeText(v.getContext(), "Anuncio borrado correctamente", Toast.LENGTH_SHORT).show();


                                            } else {
                                                Toast.makeText(v.getContext(), mensaje, Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(v.getContext(),
                                                    "Error en la respuesta del servidor",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<JsonObject> call, Throwable t) {
                                        Toast.makeText(v.getContext(),
                                                "Error en la conexión: " + t.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();



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
        public final TextView tvdesc;
        public final TextView tvubimianuncio;
        public final TextView tvhoranuncio;
        public final ImageView imgmianuncio;
        public final Button btnEliminar;
        public Anuncio mItem;

        public ViewHolder(FragmentMianuncioBinding binding) {
            super(binding.getRoot());
            tvdesc=binding.tvdesc;
            tvubimianuncio=binding.tvubimianuncio;
            tvhoranuncio=binding.tvhoranuncio;
            imgmianuncio=binding.imgmianuncio;
            btnEliminar=binding.btnAccion;
        }


    }
}