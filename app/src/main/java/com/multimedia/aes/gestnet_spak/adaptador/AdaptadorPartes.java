package com.multimedia.aes.gestnet_spak.adaptador;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.multimedia.aes.gestnet_spak.R;
import com.multimedia.aes.gestnet_spak.Utils.Utils;
import com.multimedia.aes.gestnet_spak.dao.MaquinaDAO;
import com.multimedia.aes.gestnet_spak.entidades.Maquina;
import com.multimedia.aes.gestnet_spak.entidades.Parte;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

public class AdaptadorPartes extends ArrayAdapter {

    private Context context;
    private int view;
    private ArrayList<Parte> arrayList;

    public AdaptadorPartes(Context context, int view, ArrayList<Parte> arrayList) {
        super(context, view, arrayList);
        this.context = context;
        this.view = view;
        this.arrayList=arrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        if (item == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            item = inflater.inflate(view, null);
        }
        TextView direccion = item.findViewById(R.id.txtDireccion);
        TextView cp =  item.findViewById(R.id.txtCP);
        TextView hora =  item.findViewById(R.id.txtHora);
        TextView cliente =  item.findViewById(R.id.txtCliente);
        TextView compania =  item.findViewById(R.id.txtCompania);
        TextView txtTipoIntervencion =  item.findViewById(R.id.txtTipoIntervencion);
        LinearLayout global = item.findViewById(R.id.global);
        String dir = "";
        if (!arrayList.get(position).getTipo_via().trim().equals("")&&!arrayList.get(position).getTipo_via().trim().equals("false")&&!arrayList.get(position).getTipo_via().trim().equals("null")){
            dir+=arrayList.get(position).getTipo_via()+" ";
        }
        if (!arrayList.get(position).getVia().trim().equals("")&&!arrayList.get(position).getVia().trim().equals("null")){
            dir+=arrayList.get(position).getVia()+" ";
        }
        if (!arrayList.get(position).getNumero_direccion().trim().equals("")&&!arrayList.get(position).getNumero_direccion().trim().equals("null")){
            dir+="Nº "+arrayList.get(position).getNumero_direccion()+" ";
        }
        if (!arrayList.get(position).getEscalera_direccion().trim().equals("")&&!arrayList.get(position).getEscalera_direccion().trim().equals("null")){
            dir+="Esc. "+arrayList.get(position).getEscalera_direccion()+" ";
        }
        if (!arrayList.get(position).getPiso_direccion().trim().equals("")&&!arrayList.get(position).getPiso_direccion().trim().equals("null")){
            dir+="Piso "+arrayList.get(position).getPiso_direccion()+" ";
        }
        if (!arrayList.get(position).getPuerta_direccion().trim().equals("")&&!arrayList.get(position).getPuerta_direccion().trim().equals("null")){
            dir+=arrayList.get(position).getPuerta_direccion()+" ";
        }
        if (!arrayList.get(position).getMunicipio_direccion().trim().equals("")&&!arrayList.get(position).getMunicipio_direccion().trim().equals("null")){
            dir+="("+arrayList.get(position).getMunicipio_direccion()+"-";
        }
        if (!arrayList.get(position).getProvincia_direccion().trim().equals("")&&!arrayList.get(position).getProvincia_direccion().trim().equals("null")){
            dir+=arrayList.get(position).getProvincia_direccion()+")";
        }

        switch (arrayList.get(position).getEstado_android()){
            case 0:
                global.setBackgroundResource(R.drawable.fondo_rojo);
                break;
            case 1:
                global.setBackgroundResource(R.drawable.fondo_ambar);
                break;
            case 2:
                global.setBackgroundResource(R.drawable.fondo_azul);
                break;
            case 3:
                global.setBackgroundResource(R.drawable.fondo_verde);
                break;
            case 4:
                global.setBackgroundResource(R.drawable.fondo_tenue);
                break;
            case 436:
                global.setBackgroundResource(R.drawable.verde_oscuro);
                break;
        }

        switch (arrayList.get(position).getEstadoEjecucion()){
            case 2:
                global.setBackgroundResource(R.drawable.fondo_gris_no_rounded);
                cp.setTextColor(Color.parseColor("#777777"));
                direccion.setTextColor(Color.parseColor("#777777"));
                hora.setTextColor(Color.parseColor("#777777"));
                compania.setTextColor(Color.parseColor("#777777"));
                cliente.setTextColor(Color.parseColor("#777777"));
                txtTipoIntervencion.setTextColor(Color.parseColor("#777777"));
                break;
        }
String tipoFecha = arrayList.get(position).getTipo();

        if(!arrayList.get(position).getFecha_visita().equals("")  && !arrayList.get(position).getFecha_visita().equals("0000-00-00")){
            Utils ut = new Utils();
            try {
                tipoFecha += " - <b>"+ut.formatDateTimeEsp(arrayList.get(position).getFecha_visita())+"</b>";
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        direccion.setText(dir);
        cp.setText(String.valueOf(arrayList.get(position).getCp_direccion()));
        hora.setText(String.valueOf(arrayList.get(position).getHorario()));
        compania.setText(Html.fromHtml("<b>"+arrayList.get(position).getNombre_compania()+"</b>"));
        String nServicio = "";
        int fk_maquina_servicio = arrayList.get(position).getFk_maquina();
        try {
            Maquina maqServicio = MaquinaDAO.buscarMaquinaPorFkMaquina(context,fk_maquina_servicio);
             nServicio = maqServicio.getNombre_instalacion();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if(nServicio.equals("")){
            cliente.setText(arrayList.get(position).getNombre_cliente());
        }else{
            cliente.setText(nServicio);
        }

        txtTipoIntervencion.setText(Html.fromHtml(tipoFecha));
        global.setTag(String.valueOf(arrayList.get(position).getId_parte()));
        return item;
    }
}
