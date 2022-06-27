package com.multimedia.aes.gestnet_spak.BBDD;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.SQLException;
import android.os.AsyncTask;

import com.multimedia.aes.gestnet_spak.entidades.TiposOs;
import com.multimedia.aes.gestnet_spak.nucleo.Index;
import com.multimedia.aes.gestnet_spak.nucleo.Login;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GuardarAuxElementos extends AsyncTask<Void,Void,Void> {


    private static String json;
    private static Context context;
    private static boolean bien = false;
    private static ArrayList<TiposOs> tiposOs = new ArrayList<>();
    private ProgressDialog dialog;

    public GuardarAuxElementos(Context context, String json) throws java.sql.SQLException, JSONException {
        this.context = context;
        this.json = json;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setTitle("Guardando Datos Auxiliares.");
        dialog.setMessage("Conectando con el servidor, porfavor espere..." + "\n" + "Esto puede tardar unos minutos si la cobertura es baja.");
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        dialog.show();
        super.onPreExecute();
    }
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            guardarJsonParte();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dialog.dismiss();
        if (bien) {
            if (context.getClass()==Login.class){
                ((Login) context).irIndex();
            }else if (context.getClass()==Index.class){
                ((Index) context).datosActualizados();
            }

        } else {
            if (context.getClass()==Login.class){
                ((Login) context).sacarMensaje("error al guardar las tipos de Os");
            }else if (context.getClass()==Index.class){
                ((Index) context).sacarMensaje("error al guardar las tipos de Os");
            }

        }
    }
    private void guardarJsonParte()  throws JSONException, SQLException, java.sql.SQLException {
        int id_tipo_os;
        String nombre_tipo_os;
        int fk_tipo_parte;
        JSONObject jsonObject = new JSONObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("AuxTiposElementos");
        for (int i = 0; i < jsonArray.length(); i++) {
             //TODO RECOGER DATOS TABLAS AUXILIARES
             //TODO COMPROBAR EXISTENCIA DE RGISTROS TABLAS AUXILIARES
             //TODO GUARDAR DATOS DE TABLAS AUXILIARES


            Boolean bExiste = false;

            //TODO COMPROBAR EXISTENCIA DE RGISTROS TABLAS AUXILIARES
            /*TiposOs tipoOsExiste = null;
            tipoOsExiste =TiposOsDAO.buscarTipoOsPorId_tipoOS(context,id_tipo_os);*/

            if(bExiste){
                //TiposOsDAO.actualizarTiposOs(context, id_tipo_os, nombre_tipo_os,fk_tipo_parte);
            }else{
                //TODO GUARDAR DATOS DE TABLAS AUXILIARES
                /*if (TiposOsDAO.newTiposOs(context, id_tipo_os, nombre_tipo_os,fk_tipo_parte)) {
                    bien = true;
                } else {
                    bien = false;
                }*/
            }

        }

    }
}
