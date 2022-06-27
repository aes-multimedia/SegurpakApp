package com.multimedia.aes.gestnet_spak.nucleo;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.multimedia.aes.gestnet_spak.R;
import com.multimedia.aes.gestnet_spak.clases.TouchView;
import com.multimedia.aes.gestnet_spak.dialogo.Dialogo;

import java.io.File;
import java.io.FileOutputStream;

public class FirmaTecnico extends Activity implements View.OnClickListener, View.OnTouchListener {
    private Button btnGuardar, btnBorrar;
    private FrameLayout frFirma;
    private TouchView cvFirma;
    private boolean tocado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firma_tecnico);
        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        btnBorrar = (Button) findViewById(R.id.btnBorrar);
        frFirma = (FrameLayout) findViewById(R.id.frFirma);
        cvFirma = (TouchView) findViewById(R.id.cvFirma);
        cvFirma.setOnTouchListener(this);
        tocado = false;
        btnGuardar.setOnClickListener(this);
        btnBorrar.setOnClickListener(this);
        int id = 0;

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnBorrar) {
            //recreate();
            finish();
            startActivity(getIntent());
        } else if (view.getId() == R.id.btnGuardar) {
            if (tocado) {
                Bitmap bitmap = Bitmap.createBitmap(frFirma.getWidth(), frFirma.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                frFirma.draw(canvas);
                bitmap = redimensionarImagenMaximo(bitmap, 320, 320);
                saveToInternalSorage(bitmap);
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            } else {
                Dialogo.dialogoError("Es necesario firma_cliente.", this);
            }

        }
    }

    public Bitmap redimensionarImagenMaximo(Bitmap mBitmap, float newWidth, float newHeigth) {
        //Redimensionamos
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeigth) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        return Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, false);
    }

    private String saveToInternalSorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath;
        mypath = new File(directory, "firmaTecnico.png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mypath.getAbsolutePath();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        tocado = true;
        return false;
    }
}
