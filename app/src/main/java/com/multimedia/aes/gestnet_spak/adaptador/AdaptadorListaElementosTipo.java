package com.multimedia.aes.gestnet_spak.adaptador;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.multimedia.aes.gestnet_spak.R;
import com.multimedia.aes.gestnet_spak.clases.dataTiposElementos;

import com.multimedia.aes.gestnet_spak.dao.ElementoInstDAO;
import com.multimedia.aes.gestnet_spak.dialogo.Dialogo;
import com.multimedia.aes.gestnet_spak.entidades.ElementoInstalacion;

import com.multimedia.aes.gestnet_spak.nucleo.FormularioElemento;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;


public class AdaptadorListaElementosTipo extends ArrayAdapter implements View.OnClickListener {
    //VARIABLES GLOBALES
    private Context context;
    private int view;
    private ArrayList<ElementoInstalacion> arrayList;
    private Activity activity;
    private String nombreTipo;


    //CONSTRUCTOR
    public AdaptadorListaElementosTipo(Context context, int view, ArrayList<ElementoInstalacion> arrayList,String nombreTipo, Activity activity) {
        super(context, view, arrayList);
        this.context = context;
        this.view = view;
        this.arrayList=arrayList;
        this.activity=activity;
        this.nombreTipo=nombreTipo;
    }


    //OVERRIDE METHODS
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;

        if (item == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            item = inflater.inflate(view, null);


        }
        Button deactBt = item.findViewById(R.id.deactElm);
        ElementoInstalacion elemento = null;

        try {
             elemento = ElementoInstDAO.buscarElementoInstalacionPorIdElemento(context,arrayList.get(position).getId_elemento());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        TextView txtElemento =item.findViewById(R.id.txtElemento);

        ArrayList tag =  new ArrayList();
        tag.add(0,elemento);
        tag.add(1,item);
        deactBt.setTag(tag);
        deactBt.setOnClickListener(this);
        LinearLayout llElemento = item.findViewById(R.id.llElementosTipo);

        llElemento.setOnClickListener(this);

        llElemento.setTag(tag);

        JSONArray registro = null;
        try {
            registro = new JSONArray(elemento.getValores());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dataTiposElementos data = new dataTiposElementos(registro);
        String html = "";
        html = data.getDataElemento();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtElemento.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT));
        } else {
            txtElemento.setText( Html.fromHtml(html));
        }

        for (int i = 0; i< arrayList.size();i++ ){
            if ( elemento.getbfinalizado()==true){
                txtElemento.setTextColor(Color.rgb(0,160,0));
            }else{
                txtElemento.setTextColor(Color.rgb(20,20,20));
            }
        }

            if ( !elemento.getActivo()){
                deactBt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ajustes, 0,  0, 0);
                deactBt.setText("  Activar Eelemento");
                txtElemento.setTextColor(Color.rgb(150,150,150));
                txtElemento.setBackgroundColor(Color.LTGRAY);
                llElemento.setBackgroundColor(Color.LTGRAY);

            }else{
                deactBt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_white, 0,  0, 0);
                deactBt.setText("  Desactivar Eelemento");
                txtElemento.setTextColor(Color.rgb(20,20,20));
                txtElemento.setBackgroundColor(Color.WHITE);
                llElemento.setBackgroundColor(Color.WHITE);
            }

        return item;
    }
    private void saveElemento(JSONArray valoresElemento, int id) throws SQLException, JSONException {
        String valoresElString = valoresElemento.toString();
        for(int i =0 ;i<valoresElemento.length();i++){
            String campo_nombre = valoresElemento.getJSONObject(i).getString("campo");
            String tipo = valoresElemento.getJSONObject(i).getString("tipo");
            String valor = valoresElemento.getJSONObject(i).getString("valor");
        }
        ElementoInstDAO.actualizaValores(context,id,valoresElemento.toString());

    }
    @Override
    public void onClick(View v) {
        ArrayList a = (ArrayList) v.getTag();
        ElementoInstalacion e = (ElementoInstalacion) a.get(0);
        View item = (View) a.get(1);
        TextView txtElemento = (TextView) item.findViewById(R.id.txtElemento);
        LinearLayout llElemento = (LinearLayout) item.findViewById(R.id.llElementosTipo);

       if(v.getId()==R.id.llElementosTipo){
           if(e.getActivo()){
               Intent i = new Intent(getContext(), FormularioElemento.class);
               i.putExtra("id",e.getId_elemento());
               i.putExtra("nombreTipo",nombreTipo);
               context.startActivity(i);
           }else{
               Dialogo.dialogoErrorHtml("El elemento seleccionado esta desactivado y no puede ser editado.<br>Gracias.",context);
           }

        }else if(v.getId()==R.id.deactElm){



           Button myBt= (Button) v;
           int id_elemento = e.getFk_elemento_gestnet();
           int id_tipo = e.getFk_tipo();
           boolean activo;
           if(e.getActivo()){
               String textDialog = "El elemento seleccionado va ser desactivado y no podrá ser editado.<br>¿Esta seguro?<br>Gracias.";
               AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
               builder1.setMessage(Html.fromHtml(textDialog, Html.FROM_HTML_MODE_LEGACY));
               builder1.setCancelable(true);
               builder1.setPositiveButton(
                       "OK",
                       new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                               myBt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ajustes, 0,  0, 0);
                               myBt.setText("  Activar Eelemento");
                               txtElemento.setTextColor(Color.rgb(150,150,150));
                               llElemento.setBackgroundColor(Color.LTGRAY);
                               txtElemento.setBackgroundColor(Color.LTGRAY);



                               try {
                                   JSONObject registro;
                                   registro = new JSONObject( e.getRegistro());
                                   registro.put("activo","0");
                                   e.setActivo(false);
                                   e.setRegitro(registro.toString());
                                   ElementoInstDAO.actualizaActivo(context,e.getId_elemento(),false);
                                   ElementoInstDAO.actualizaRegistro(context,e.getId_elemento(),registro.toString());
                               } catch (SQLException | JSONException throwables) {
                                   throwables.printStackTrace();
                               }
                           }
                       });
               builder1.setNegativeButton(
                       "Cancelar",
                       new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                               dialog.cancel();
                           }
                       });
               AlertDialog alert11 = builder1.create();
               alert11.setCanceledOnTouchOutside(false);
               alert11.show();
           }else{
               String textDialog = "El elemento seleccionado va ser activado para poder editar sus valores.<br>¿Esta seguro?<br>Gracias.";
               AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
               builder1.setMessage(Html.fromHtml(textDialog, Html.FROM_HTML_MODE_LEGACY));
               builder1.setCancelable(true);
               builder1.setPositiveButton(
                       "OK",
                       new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                               myBt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_white, 0,  0, 0);
                               myBt.setText("  Desactivar Eelemento");
                               txtElemento.setTextColor(Color.rgb(20,20,20));
                               llElemento.setBackgroundColor(Color.WHITE);
                               txtElemento.setBackgroundColor(Color.WHITE);

                               try {
                                   JSONObject registro;
                                   registro = new JSONObject( e.getRegistro());
                                   registro.put("activo","1");
                                   e.setActivo(true);
                                   e.setRegitro(registro.toString());
                                   ElementoInstDAO.actualizaActivo(context,e.getId_elemento(),true);
                                   ElementoInstDAO.actualizaRegistro(context,e.getId_elemento(),registro.toString());
                               } catch (SQLException | JSONException throwables) {
                                   throwables.printStackTrace();
                               }
                           }
                       });
               builder1.setNegativeButton(
                       "Cancelar",
                       new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int id) {
                               dialog.cancel();
                           }
                       });
               AlertDialog alert11 = builder1.create();
               alert11.setCanceledOnTouchOutside(false);
               alert11.show();

           }


           //new HiloDeactElem(getContext(), id_elemento,id_tipo).execute();

       }
    }
}
