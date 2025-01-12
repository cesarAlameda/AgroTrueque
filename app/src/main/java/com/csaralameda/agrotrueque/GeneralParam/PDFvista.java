package com.csaralameda.agrotrueque.GeneralParam;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.csaralameda.agrotrueque.R;
import com.pdfview.PDFView;

public class PDFvista extends AppCompatActivity {

    PDFView vistaPdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pdfvista);
        vistaPdf = findViewById(R.id.vistaPdf);
        //pdf hecho con termly (completar)1
        try {
            vistaPdf.fromAsset("Privacidad.pdf");
            vistaPdf.isZoomEnabled();
            vistaPdf.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
