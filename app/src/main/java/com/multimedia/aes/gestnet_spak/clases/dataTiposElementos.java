package com.multimedia.aes.gestnet_spak.clases;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;

public class dataTiposElementos {
    private JSONArray registro;

    public dataTiposElementos(JSONArray registro) {

        this.registro = registro;
    }

    public String getDataElemento(){
        String data=null;
        try {
            data = "";
            String es = "dd/MM/yyyy";
            String en = "yyyy-MM-dd";
            SimpleDateFormat sdf1 = new SimpleDateFormat(en);
            SimpleDateFormat sdf2 = new SimpleDateFormat(es);
            for(int i =0 ;i<registro.length();i++){
                String mostrarApp = registro.getJSONObject(i).getString("bTitularApp");
                if(mostrarApp.equals("1")){
                    String valor = registro.getJSONObject(i).getString("valor");
                    String campo_mostrar = registro.getJSONObject(i).getString("campo_mostrar");
                    String tipo = registro.getJSONObject(i).getString("tipo");

                    /*if(tipo.equals("estatico_fecha")){
                        Date fechaDate =  sdf1.parse(registro.getJSONObject(i).getString("valor"));
                        String fechaStr =  sdf2.format(fechaDate);
                        valor = fechaStr;
                    }*/
                    if (i == registro.length()-1){
                        data  += campo_mostrar+": <b>"+valor+"</b>";
                    }else{
                        data  += campo_mostrar+": <b>"+valor+"</b><br>";
                    }
                }
            }

            /*switch (fkTipo){
                case 1:
                    Date fec_caducidadDate = sdf1.parse(registro.getString("fec_caducidad"));
                    Date fec_retimbradoDate = sdf1.parse(registro.getString("fec_retimbrado"));

                    String fec_caducidad =  sdf2.format(fec_caducidadDate);
                    String fec_retimbrado = sdf2.format(fec_retimbradoDate);

                    data  = "Equipo: <b>"+registro.getString("equipo")+"</b><br>";
                    data += "Marca: <b>"+registro.getString("marca")+"</b><br>";
                    data += "Fecha caducidad: <b>"+fec_caducidad+"</b><br>";
                    data +="Fecha retimbrado: <b>"+fec_retimbrado+"</b><br>";
                    data += "Localización: <b>"+registro.get("ubicacion")+"</b><br>";
                    break;
                case 2:
                    Date fec_fabricacionDate = sdf1.parse(registro.getString("fec_fabricacion"));
                    Date fec_retimbrado_biesDate = sdf1.parse(registro.getString("fec_retimbrado"));

                    String fec_fabricacion =  sdf2.format(fec_fabricacionDate);
                    String fec_retimbrado_bie = sdf2.format(fec_retimbrado_biesDate);

                    data  = "N&uacute;m. Identificaci&oacute;n: <b>"+registro.getString("num_identificacion")+"</b><br>";
                    data += "N&uacute;m. Placa: <b>"+registro.getString("num_placa")+"</b><br>";
                    data += "Fecha fabricacion: <b>"+fec_fabricacion+"</b><br>";
                    data +="Fecha retimbrado: <b>"+fec_retimbrado_bie+"</b><br>";
                    data += "Localización: <b>"+registro.get("ubicacion")+"</b><br>";
                    break;
            }*/


        }catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
        return data;
    }

}
