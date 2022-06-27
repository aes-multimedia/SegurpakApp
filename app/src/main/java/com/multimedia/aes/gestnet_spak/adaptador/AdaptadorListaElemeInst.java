package com.multimedia.aes.gestnet_spak.adaptador;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.multimedia.aes.gestnet_spak.R;
import com.multimedia.aes.gestnet_spak.nucleo.ListaElemenInstTipo;

import java.util.ArrayList;


public class AdaptadorListaElemeInst extends ArrayAdapter implements View.OnClickListener {
    //VARIABLES GLOBALES
    private Context context;
    private int view;
    private ArrayList<String[]> arrayList;
    private Activity activity;

    //CONSTRUCTOR
    public AdaptadorListaElemeInst(Context context, int view, ArrayList<String[]> arrayList, Activity activity) {
        super(context, view, arrayList);
        this.context = context;
        this.view = view;
        this.arrayList=arrayList;
        this.activity=activity;
    }
    //OVERRIDE METHODS
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        if (item == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            item = inflater.inflate(view, null);
        }

        LinearLayout llElemInst = item.findViewById(R.id.llistaTiposElem);
        TextView txtTipo =item.findViewById(R.id.txtTipoElemInst);
        TextView txtNumElem = item.findViewById(R.id.txtNumElemInst);

        llElemInst.setOnClickListener(this);
        int fk_tipo = Integer.parseInt(arrayList.get(position)[0]);
        String tipo =arrayList.get(position)[1];
        String numElementos = arrayList.get(position)[2];
        ArrayList tag =  new ArrayList();
        tag.add(0,fk_tipo);
        tag.add(1,tipo);
       // tagElemento tag = new tagElemento(fk_tipo,tipo);

        llElemInst.setTag(tag);
        txtTipo.setText( tipo);
        txtNumElem.setText(numElementos);

        return item;
    }
    @Override
    public void onClick(View v) {

       if(v.getId()==R.id.llistaTiposElem){
           Intent i = new Intent(getContext(), ListaElemenInstTipo.class);

               ArrayList a = (ArrayList) v.getTag();
               int id_tipo = Integer.parseInt(a.get(0).toString());
               String nombreTipo = a.get(1).toString();
               i.putExtra("id_tipo",id_tipo);
               i.putExtra("nombreTipo",nombreTipo);
               context.startActivity(i);
        }
    }
}
