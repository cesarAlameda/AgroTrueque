package com.csaralameda.agrotrueque;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.pdfview.PDFView;
import java.io.File;

public class PDFvista extends AppCompatActivity {

    PDFView vistaPdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pdfvista);
        vistaPdf = findViewById(R.id.vistaPdf);

        try {
            File file = new File(getCacheDir(), "Privacidad.pdf");
            vistaPdf.fromFile(file);
            vistaPdf.isZoomEnabled();
            vistaPdf.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
