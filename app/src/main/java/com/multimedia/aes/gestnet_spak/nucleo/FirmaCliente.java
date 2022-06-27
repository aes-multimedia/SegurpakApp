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
import android.widget.EditText;
import android.widget.FrameLayout;

import com.multimedia.aes.gestnet_spak.R;
import com.multimedia.aes.gestnet_spak.SharedPreferences.GestorSharedPreferences;
import com.multimedia.aes.gestnet_spak.clases.TouchView;
import com.multimedia.aes.gestnet_spak.dao.ParteDAO;
import com.multimedia.aes.gestnet_spak.dialogo.Dialogo;
import com.multimedia.aes.gestnet_spak.entidades.Parte;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import id.zelory.compressor.Compressor;

public class FirmaCliente extends Activity implements View.OnClickListener, View.OnTouchListener {
    private Button btnGuardar,btnBorrar;
    private FrameLayout frFirma;
    private TouchView cvFirma;
    private EditText etNombreFirmante,etDniFirmante;
    private boolean tocado = false;
    private Parte parte;

    //METODOS
    public Bitmap redimensionarImagenMaximo(Bitmap mBitmap, float newWidth, float newHeigth){
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
    private String saveToInternalSorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath;
        mypath =new File(directory,"firmaCliente"+parte.getId_parte()+".png");
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

    //OVERRIDE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firma_cliente);
        etNombreFirmante = findViewById(R.id.etNombreFirmante);
        etDniFirmante = findViewById(R.id.etDniFirmante);
        btnGuardar =  findViewById(R.id.btnGuardar);
        btnBorrar =  findViewById(R.id.btnBorrar);
        frFirma =  findViewById(R.id.frFirma);
        cvFirma =  findViewById(R.id.cvFirma);
        cvFirma.setOnTouchListener(this);
        tocado = false;
        btnGuardar.setOnClickListener(this);
        btnBorrar.setOnClickListener(this);
        int id = 0;
        try {
            JSONObject jsonObject = GestorSharedPreferences.getJsonParte(GestorSharedPreferences.getSharedPreferencesParte(this));
            id = jsonObject.getInt("id");
            parte = ParteDAO.buscarPartePorId(this,id);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btnBorrar){
            //recreate();
            finish();
            startActivity(getIntent());
        }else if (view.getId()==R.id.btnGuardar){
            if (!etNombreFirmante.getText().toString().trim().equals("")){
                if (tocado){
                    Bitmap bitmap = Bitmap.createBitmap( frFirma.getWidth(), frFirma.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    frFirma.draw(canvas);
                    bitmap = redimensionarImagenMaximo(bitmap,320,320);
                    String path = saveToInternalSorage(bitmap);
                    File image = new File(path);
                    File compressedImageFile=null;
                    try {

                        compressedImageFile = new Compressor(this)
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(75)
                                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                .setDestinationDirectoryPath(path)
                                .compressToFile(image);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        ParteDAO.actualizarNombreFirma(this,parte.getId_parte(),etNombreFirmante.getText().toString());
                        if (!etDniFirmante.getText().toString().trim().equals("")){
                            ParteDAO.actualizarDniFirma(this,parte.getId_parte(),etDniFirmante.getText().toString());
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("path",path);
                    setResult(Activity.RESULT_OK,returnIntent);

                    finish();
                }else {
                    Dialogo.dialogoError("Es necesaria la firma del consumidor.",this);
                }
            }else{
                Dialogo.dialogoError("Es necesario el nombre del firmante.",this);
            }


        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        tocado = true;
        return false;
    }
}
