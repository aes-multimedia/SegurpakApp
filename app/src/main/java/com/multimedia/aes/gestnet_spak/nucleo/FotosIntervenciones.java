package com.multimedia.aes.gestnet_spak.nucleo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.multimedia.aes.gestnet_spak.R;
import com.multimedia.aes.gestnet_spak.adaptador.AdaptadorImagenesIntervenciones;
import com.multimedia.aes.gestnet_spak.hilos.HiloListarImagenesIntervencion;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class FotosIntervenciones extends AppCompatActivity {

    private ListView lvIndexFotosIntervenciones;

    private ArrayList<String> listaRutas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotos_intervenciones);
        Toolbar toolbar = findViewById(R.id.toolbarFotosIntervenciones);
        setSupportActionBar(toolbar);

        int id = getIntent().getIntExtra("id_parte",-1);
        boolean porParte = true;
        setTitle("Imagenes de la intervención");
        if (id ==-1){
            setTitle("Imagenes del usuario");
            porParte = false;
            id = getIntent().getIntExtra("id_usuario",-1);
        }
        new HiloListarImagenesIntervencion(this,id,porParte).execute();


    }

    public void sacarMensaje(String mensaje) {


        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(mensaje);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.setCanceledOnTouchOutside(false);
        alert11.setCanceledOnTouchOutside(false);
        alert11.show();


    }
    @Override
    public void onBackPressed() {
       finish();
    }

    public void almacenarRutas(String mensaje) {
        listaRutas=new ArrayList<>();
        try {
            JSONArray jsonElements = new JSONArray(mensaje);
        for (int i = 0; i <= jsonElements.length() ; i++) {


            String ruta;
            if(jsonElements.getJSONObject(i).getString("imagen").equals("") || jsonElements.getJSONObject(i).getString("imagen").equals("null"))
                ruta="";
            else
                ruta=jsonElements.getJSONObject(i).getString("imagen");



            if(ruta.contains("../../../"))
                ruta= ruta.substring(8,ruta.length());

            listaRutas.add(ruta);

        }

        } catch (JSONException e) {
            e.printStackTrace();
        }





        mostrarImagenes();


    }


    private void mostrarImagenes() {

        AdaptadorImagenesIntervenciones adaptatorImagenesIntervenciones;
        lvIndexFotosIntervenciones=findViewById(R.id.lvIndexFotosIntervenciones);
        adaptatorImagenesIntervenciones = new AdaptadorImagenesIntervenciones(this, R.layout.camp_adapter_list_view_imagenes_intervencion, listaRutas);
        lvIndexFotosIntervenciones.setAdapter(adaptatorImagenesIntervenciones);

    }


}
