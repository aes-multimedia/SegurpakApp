package com.multimedia.aes.gestnet_spak.hilos;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.multimedia.aes.gestnet_spak.R;
import com.multimedia.aes.gestnet_spak.constantes.Constantes;
import com.multimedia.aes.gestnet_spak.dao.ClienteDAO;
import com.multimedia.aes.gestnet_spak.dao.HorasParteDAO;
import com.multimedia.aes.gestnet_spak.dao.ParteDAO;
import com.multimedia.aes.gestnet_spak.entidades.Cliente;
import com.multimedia.aes.gestnet_spak.entidades.HorasParte;
import com.multimedia.aes.gestnet_spak.fragments.FragmentPartes;
import com.multimedia.aes.gestnet_spak.nucleo.Index;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HiloHorasParte extends AsyncTask<Void,Void,Void> {
    private int fk_tecnico,fk_parte,tipo_cierre,fk_reinicio;
    /*
    inicio 1
    pausa 2
    resume 3
    finalizar 4
    */
    private String mensaje,fecha_visita,hora_inicio,hora_fin,fecha;
    private Context context;
    private Fragment frg;
    private ProgressDialog dialog;
    private Cliente cliente;

    public HiloHorasParte(Context context, int fk_tecnico,int fk_parte, String hora_inicio,String hora_fin,String fechaVisita,int tipo_cierre,Fragment frg) {
        this.context=context;
        this.fk_tecnico = fk_tecnico;
        this.fk_parte = fk_parte;
        this.tipo_cierre = tipo_cierre;
        this.hora_inicio = hora_inicio;
        this.hora_fin = hora_fin;
        this.fk_reinicio=0;
        if(this.tipo_cierre == 2){
            try {
                HorasParte HorasReinicio = HorasParteDAO.getLastHoraReinicio(this.context,this.fk_parte);
                if(HorasReinicio!=null){
                    this.fk_reinicio= HorasReinicio.getId_hora();
                }
            }catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        this.fecha_visita = fechaVisita;
        this.frg=frg;
        DateFormat dtf = new SimpleDateFormat("Y-M-d");
        fecha = dtf.format(Calendar.getInstance().getTime());
        try {
            this.cliente= ClienteDAO.buscarCliente(context);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPreExecute() {

        dialog = new ProgressDialog(context);
        dialog.setTitle("Actualizando estado y horas del aviso");
        dialog.setMessage("Conectando con el servidor, porfavor espere..." + "\n" + "Esto puede tardar unos minutos si la cobertura es baja.");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setIndeterminate(true);
        dialog.show();
        super.onPreExecute();
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
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mensaje.indexOf('}')!=-1){
            try {
                JSONObject msg = new JSONObject(mensaje);
                if(msg.getString("estado").equals("1")){
                    JSONObject result = new JSONObject(msg.getString("mensaje"));
                    grabarHoras(result);
                }else{
                    JSONObject msgError = new JSONObject(msg.getString("mensaje"));
                    String ErrorStr = msgError.getString("mensaje");
                    ((Index) context).sacarMensaje(ErrorStr);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        }else{
            ((Index) context).sacarMensaje(mensaje);
        }
    }
    private String iniciar() throws JSONException {
        JSONObject msg = new JSONObject();
        msg.put("fk_tecnico", fk_tecnico);
        msg.put("fk_parte",fk_parte );
        msg.put("fecha_visita",fecha_visita );
        msg.put("fecha",fecha );
        msg.put("hora_inicio",hora_inicio );
            msg.put("hora_fin",hora_fin );
        msg.put("tipo_cierre",tipo_cierre );
        URL urlws = null;
        HttpURLConnection uc = null;
        try {
            urlws = new URL("http://"+cliente.getIp_cliente()+ Constantes.URL_HORAS_PARTES);
            uc = (HttpURLConnection) urlws.openConnection();
            uc.setDoOutput(true);
            uc.setDoInput(true);
            uc.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            uc.setRequestMethod("POST");
            uc.connect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("estado", 5);
            error.put("mensaje", "Error de conexion, URL malformada");
            return error.toString();
        } catch (ProtocolException e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("estado", 5);
            error.put("mensaje", "Error de conexion, error de protocolo");
            return error.toString();
        } catch (IOException e) {
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
            e.printStackTrace();
        }
        return contenido;
    }

    /* private  void actualizarStock(String mensaje) {

        try {
            JSONArray jsonElements = new JSONArray(mensaje);
            for (int i = 0; i < jsonElements.length(); i++) {


                int id;
                if (jsonElements.getJSONObject(i).getString("id_articulo").equals("") || jsonElements.getJSONObject(i).getString("id_articulo").equals("null"))
                    id = 0;
                else
                    id = jsonElements.getJSONObject(i).getInt("id_articulo");


                double stock;
                if (jsonElements.getJSONObject(i).getString("cantidad").equals("") || jsonElements.getJSONObject(i).getString("cantidad").equals("null"))
                    stock = 0;
                else
                    stock = jsonElements.getJSONObject(i).getDouble("cantidad");
                ArticuloDAO.actualizarStockPorfK(context,id,stock);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (SQLException sql) {
            sql.printStackTrace();
        }
    }*/
     private  void grabarHoras(JSONObject mensaje) {

        try {
                int id_gestnet;
                if (mensaje.getString("id_horas").equals("") ||mensaje.getString("id_horas").equals("null"))
                    id_gestnet = 0;
                else
                    id_gestnet =mensaje.getInt("id_horas");

                HorasParteDAO.newHorasParte(context,id_gestnet,fk_parte,fk_tecnico,fk_reinicio,hora_inicio,hora_fin,fecha,fecha_visita,tipo_cierre);
               if(tipo_cierre!=4){
                   ParteDAO.actualizarEstadoEjecucion(context,fk_parte,tipo_cierre );
               }


            if(frg!=null){
                Class fragmentClass = FragmentPartes.class;
                Fragment fragment;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                    FragmentManager fragmentManager = frg.getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.cuerpo, fragment).commit();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
     }

}
