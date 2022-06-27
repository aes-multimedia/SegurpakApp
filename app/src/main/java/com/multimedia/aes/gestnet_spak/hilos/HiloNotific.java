package com.multimedia.aes.gestnet_spak.hilos;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.multimedia.aes.gestnet_spak.constantes.Constantes;
import com.multimedia.aes.gestnet_spak.dao.ClienteDAO;
import com.multimedia.aes.gestnet_spak.dao.UsuarioDAO;
import com.multimedia.aes.gestnet_spak.nucleo.Login;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.SQLException;


public class HiloNotific extends AsyncTask<Void,Void,Void> {
    private String mensaje="",tokken = "",imei = "",apikey;
    private int idEntidad;
    private Context context;
    private String ipCliente;
    private ProgressDialog dialog;

    public HiloNotific(Context context,String tokken,String imei) {
        this.context = context;
        this.tokken = tokken;
        this.imei=imei;
        try {
        this.idEntidad=UsuarioDAO.buscarUsuario(context).getFk_entidad();
        this.apikey=UsuarioDAO.buscarUsuario(context).getApi_key();
        this.ipCliente=ClienteDAO.buscarCliente(context).getIp_cliente();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setTitle("Preparando Notificaciones.");
        dialog.setMessage("Conectando con el servidor, porfavor espere..." + "\n" + "Esto puede tardar unos minutos si la cobertura es baja.");
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            mensaje = registrarNotificaciones();
        } catch (JSONException e) {
            mensaje = "JSONException";
            e.printStackTrace();
        }
        return null;
    }



    //TO DO
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dialog.dismiss();
        if (mensaje.indexOf('}')==-1){
            ((Login)context).sacarMensaje("No se ha devuelto correctamente de la api");
        }else{
            ((Login)context).hiloPartes();
        }

    }

    private String registrarNotificaciones() throws JSONException{
        JSONObject msg = new JSONObject();
        msg.put("fk_entidad",idEntidad);
        msg.put("tokken",tokken);
        msg.put("deviceImei",imei);
        URL urlws = null;
        HttpURLConnection uc = null;
        try {
            String url = "http://"+ipCliente+Constantes.URL_ALTA_NOTIFICACIONES;
            urlws = new URL(url);
            uc = (HttpURLConnection) urlws.openConnection();
            uc.setDoOutput(true);
            uc.setDoInput(true);
            uc.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            uc.setRequestProperty("id",String.valueOf(idEntidad));
            uc.setRequestProperty("apikey",apikey);
            uc.setRequestMethod("POST");
            uc.connect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("estado",5);
            error.put("mensaje","Error de conexión, URL malformada");
            return error.toString();
        } catch (ProtocolException e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("estado",5);
            error.put("mensaje","Error de conexión, error de protocolo");
            return error.toString();
        } catch (IOException e) {
            JSONObject error = new JSONObject();
            error.put("estado",5);
            error.put("mensaje","Error de conexión, IOException");
            return error.toString();
        }
        String contenido = "";
        OutputStream os = null;
        try {
            StringBuilder sb = new StringBuilder();
            os = uc.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(msg.toString());
            osw.flush();
            BufferedReader br = new BufferedReader(new InputStreamReader((uc.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output+"\n");
            }
            contenido = sb.toString();
            br.close();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("estado",5);
            error.put("mensaje","Error de conexión, error en lectura");
            contenido = error.toString();
        }


        return contenido;
    }


}
