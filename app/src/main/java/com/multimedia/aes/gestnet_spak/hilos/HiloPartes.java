package com.multimedia.aes.gestnet_spak.hilos;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.multimedia.aes.gestnet_spak.constantes.Constantes;
import com.multimedia.aes.gestnet_spak.dao.ClienteDAO;
import com.multimedia.aes.gestnet_spak.entidades.Cliente;
import com.multimedia.aes.gestnet_spak.nucleo.Index;
import com.multimedia.aes.gestnet_spak.nucleo.Login;

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

public class HiloPartes extends AsyncTask<Void,Void,Void>{

    private String mensaje="",ipCliente;
    private int idUser;
    private String apiKey;
    private Context context;
    Cliente cliente;
    private ProgressDialog dialog;

    public HiloPartes(Context context,int idUser,String ipCliente,String apiKey) {
        this.idUser=idUser;
        this.context = context;
        this.apiKey=apiKey;
        this.ipCliente=ipCliente;
        try {
            this.cliente= ClienteDAO.buscarCliente(context);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setTitle("Descargando Partes.");
        dialog.setMessage("Conectando con el servidor, porfavor espere..." + "\n" + "Esto puede tardar unos minutos si la cobertura es baja.");
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            mensaje = partes();
        } catch (JSONException e) {
            mensaje = "JSONException";
            e.printStackTrace();
        }
         return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        dialog.dismiss();
        if (mensaje.indexOf('}')!=-1){
            if (context.getClass()==Login.class){
                ((Login)context).guardarPartes(mensaje);
            }else if (context.getClass()==Index.class){
                JSONObject resultado = null;
                try {
                    resultado = new JSONObject(mensaje);
                    JSONArray partes = resultado.getJSONArray("partes");
                    if(partes.length() > 0){
                        ((Index)context).guardarPartes(mensaje);
                    }else{
                        ((Index)context).datosActualizados();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }



        }else{
            if (context.getClass()==Login.class){
                ((Login)context).sacarMensaje("No se ha devuelto correctamente de la api");
            }else if (context.getClass()==Index.class){
                ((Index)context).sacarMensaje("No se ha devuelto correctamente de la api");
            }

        }
        super.onPostExecute(aVoid);
    }

    private String partes() throws JSONException{
        JSONObject msg = new JSONObject();
        msg.put("tecnico",idUser);
        URL urlws = null;
        HttpURLConnection uc = null;
        try {
            String url="http://"+cliente.getIp_cliente()+Constantes.URL_PARTES;
            urlws = new URL(url);
            uc = (HttpURLConnection) urlws.openConnection();
            uc.setDoOutput(true);
            uc.setDoInput(true);
            uc.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            uc.setRequestMethod("POST");
            uc.addRequestProperty("apikey",apiKey);
            uc.addRequestProperty("id",String.valueOf(idUser));
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
/*          Metodo de lectura antiguo
            os = uc.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(msg.toString());
            osw.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                contenido += inputLine + "\n";
            }
            in.close();
            osw.close();
*/
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
