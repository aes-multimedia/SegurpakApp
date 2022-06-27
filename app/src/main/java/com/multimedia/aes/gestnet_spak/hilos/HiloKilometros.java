package com.multimedia.aes.gestnet_spak.hilos;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.multimedia.aes.gestnet_spak.constantes.Constantes;
import com.multimedia.aes.gestnet_spak.dao.ClienteDAO;
import com.multimedia.aes.gestnet_spak.dao.EnvioDAO;
import com.multimedia.aes.gestnet_spak.dialogo.DialogoKilometros;
import com.multimedia.aes.gestnet_spak.entidades.Cliente;

import org.json.JSONArray;
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

public class HiloKilometros extends AsyncTask<Void,Void,Void> {

    private int fk_tecnico;
    private double litros_reposta,kilometros;
    private String mensaje;
    private Context context;
    private ProgressDialog dialog;
    private Cliente cliente;
    private DialogoKilometros dialogoKilometros;



    public HiloKilometros(Context context, DialogoKilometros dialogoKilometros, int fk_tecnico, double litros_reposta, double kilometros) {
        this.fk_tecnico = fk_tecnico;
        this.litros_reposta = litros_reposta;
        this.kilometros = kilometros;
        this.context=context;
        this.dialogoKilometros=dialogoKilometros;
        try {
            cliente= ClienteDAO.buscarCliente(context);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            mensaje = iniciar();
        } catch (JSONException e) {
            mensaje = "JSONException";
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setTitle("Guardando kilometros.");
        dialog.setMessage("Conectando con el servidor, porfavor espere..."+"\n"+"Esto puede tardar unos minutos si la cobertura es baja.");
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        dialog.show();
        super.onPreExecute();
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dialog.dismiss();
        if (mensaje.indexOf('}')!=-1){
            try {
                JSONObject jsonObject = new JSONObject(mensaje);
                if (jsonObject.getInt("estado")==1){
                    dialogoKilometros.hiloBien();
                }else{
                    dialogoKilometros.errorHilo(mensaje);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                try {
                    dialogoKilometros.errorHilo("{\"estado\":188,\"mensaje\":\"Error al conectar con el servidor\"}");
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }else{
            try {
                dialogoKilometros.errorHilo("{\"estado\":188,\"mensaje\":\"Error al conectar con el servidor.\"}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private String iniciar() throws JSONException {
        JSONObject msg = new JSONObject();
        msg.put("fk_tecnico", fk_tecnico);
        msg.put("litros_reposta", litros_reposta);
        msg.put("kilometros", kilometros);
        URL urlws = null;
        HttpURLConnection uc = null;
        try {
            urlws = new URL("http://"+cliente.getIp_cliente()+Constantes.URL_KILOMETROS);
            uc = (HttpURLConnection) urlws.openConnection();
            uc.setDoOutput(true);
            uc.setDoInput(true);
            uc.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            uc.setRequestMethod("POST");
            uc.connect();
        } catch (MalformedURLException e) {
            JSONArray jsonArray = new JSONArray();
            EnvioDAO.newEnvio(context,msg.toString(),"http://"+cliente.getIp_cliente()+Constantes.URL_CIERRE_DIA,jsonArray.toString());
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("estado", 5);
            error.put("mensaje", "Error de conexion, URL malformada");
            return error.toString();
        } catch (ProtocolException e) {
            JSONArray jsonArray = new JSONArray();
            EnvioDAO.newEnvio(context,msg.toString(),"http://"+cliente.getIp_cliente()+Constantes.URL_CIERRE_DIA,jsonArray.toString());
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("estado", 5);
            error.put("mensaje", "Error de conexion, error de protocolo");
            return error.toString();
        } catch (IOException e) {
            JSONArray jsonArray = new JSONArray();
            EnvioDAO.newEnvio(context,msg.toString(),"http://"+cliente.getIp_cliente()+Constantes.URL_CIERRE_DIA,jsonArray.toString());
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("estado", 5);
            error.put("mensaje", "Error de conexion, IOException");
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
            JSONArray jsonArray = new JSONArray();
            EnvioDAO.newEnvio(context,msg.toString(),"http://"+cliente.getIp_cliente()+Constantes.URL_CIERRE_DIA,jsonArray.toString());
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("estado", 5);
            error.put("mensaje", "Error de conexion, IOException");
            return error.toString();
        }
        return contenido;
    }
}
