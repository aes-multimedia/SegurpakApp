package com.multimedia.aes.gestnet_spak.hilos;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.multimedia.aes.gestnet_spak.constantes.Constantes;
import com.multimedia.aes.gestnet_spak.dao.ClienteDAO;
import com.multimedia.aes.gestnet_spak.dao.EnvioDAO;
import com.multimedia.aes.gestnet_spak.entidades.Cliente;
import com.multimedia.aes.gestnet_spak.nucleo.CierreDia;

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

public class HiloCierreDia extends AsyncTask<Void,Void,Void> {

    private int fk_tecnico,duracion,horas_guardia,horas_extra;
    private double dietas;
    private double parking;
    private double combustible;
    private double litros_reposta;
    private double entregado;
    private double material;
    private double km_inicio;
    private double km_final;
    private double km_totales;
    private String mensaje,horas_comida,hora_inicio,hora_fin,observaciones,fecha_cierre, matricula;
    private boolean festivo;
    private Context context;
    private ProgressDialog dialog;
    private Cliente cliente;



    public HiloCierreDia(Context context, int fk_tecnico, int duracion, int horas_guardia,
                         int horas_extra,double dietas, double parking, double combustible,
                         double litros_reposta, double entregado, double material,
                         String horas_comida, String hora_inicio, String hora_fin,
                         String observaciones, String fecha_cierre,boolean festivo,double km_inicio, double km_final,double km_totales, String matricula) {
        this.fk_tecnico = fk_tecnico;
        this.duracion = duracion;
        this.horas_guardia = horas_guardia;
        this.horas_extra = horas_extra;
        this.dietas = dietas;
        this.parking = parking;
        this.combustible = combustible;
        this.litros_reposta = litros_reposta;
        this.entregado = entregado;
        this.material = material;
        this.horas_comida = horas_comida;
        this.hora_inicio = hora_inicio;
        this.hora_fin = hora_fin;
        this.observaciones = observaciones;
        this.fecha_cierre = fecha_cierre;
        this.festivo = festivo;

        this.km_inicio = km_inicio;
        this.km_final = km_final;
        this.km_totales = km_totales;
        this.matricula = matricula;

        this.context=context;
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
        dialog.setTitle("Cerrando el dia.");
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
                    ((CierreDia)context).finalizar();
                }else{
                    ((CierreDia)context).error();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ((CierreDia)context).error();
            }
        }else{
            ((CierreDia)context).error();
        }
    }
    private String iniciar() throws JSONException {
        JSONObject msg = new JSONObject();
        msg.put("fk_tecnico", fk_tecnico);
        msg.put("duracion", duracion);
        msg.put("horas_guardia", horas_guardia);
        msg.put("horas_extra", horas_extra);
        msg.put("dietas", dietas);
        msg.put("parking", parking);
        msg.put("combustible", combustible);
        msg.put("litros_reposta", litros_reposta);
        msg.put("entregado", entregado);
        msg.put("material", material);
        msg.put("horas_comida", horas_comida);
        msg.put("hora_inicio", hora_inicio);
        msg.put("hora_fin", hora_fin);
        msg.put("observaciones", observaciones);
        msg.put("fecha_cierre", fecha_cierre);
        msg.put("festivo", festivo);

        msg.put("km_dia_inicio", km_inicio);
        msg.put("km_fin_dia", km_final);
        msg.put("total_km", km_totales);
        msg.put("matricula", matricula);


        URL urlws = null;
        HttpURLConnection uc = null;
        try {
            urlws = new URL("http://"+cliente.getIp_cliente()+Constantes.URL_CIERRE_DIA);
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
