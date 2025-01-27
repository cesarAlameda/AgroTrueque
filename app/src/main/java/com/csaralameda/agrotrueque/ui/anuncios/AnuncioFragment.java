package com.csaralameda.agrotrueque.ui.anuncios;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csaralameda.agrotrueque.R;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class AnuncioFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private RecyclerView rc;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AnuncioFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AnuncioFragment newInstance(int columnCount) {
        AnuncioFragment fragment = new AnuncioFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listadeanuncios, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            rc = (RecyclerView) view;


            if (mColumnCount <= 1) {
                rc.setLayoutManager(new LinearLayoutManager(context));
                
            } else {

                rc.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            rc.setAdapter(new MyanuncioRecyclerViewAdapter(Anuncios.listanuncios));
        }
        return view;
    }
    public void actualizarAnunciosConFiltro(List<Anuncio> anunciosFiltrados) {

        if (rc.getAdapter() != null) {
            rc.setAdapter(new MyanuncioRecyclerViewAdapter(anunciosFiltrados));
        }
    }
    public void actualizarAnuncios(){
        rc.setAdapter(new MyanuncioRecyclerViewAdapter(Anuncios.listanuncios));
        rc.getAdapter().notifyDataSetChanged();
    }
}