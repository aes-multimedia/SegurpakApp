package com.multimedia.aes.gestnet_spak.adaptador;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.multimedia.aes.gestnet_spak.R;
import com.multimedia.aes.gestnet_spak.clases.DataImagenes;
import com.multimedia.aes.gestnet_spak.nucleo.FotosProtocoloAccion;
import com.multimedia.aes.gestnet_spak.nucleo.GaleriaV2;

import java.util.ArrayList;

public class AdaptadorListaImagenes extends ArrayAdapter implements View.OnClickListener {
    //VARIABLES GLOBALES
    private Context context;
    private int view;
    private ArrayList<DataImagenes> arrayList;
    //CONSTRUCTOR
    public AdaptadorListaImagenes(Context context, int view, ArrayList<DataImagenes> arrayList) {
        super(context, view, arrayList);
        this.context = context;
        this.view = view;
        this.arrayList=arrayList;
    }
    //OVERRIDE METHODS
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        if (item == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            item = inflater.inflate(view, null);
        }
        TextView txtNombre =item.findViewById(R.id.txtNombre);
        ImageView ivFoto = item.findViewById(R.id.ivFoto);
        Button btnBorrar = item.findViewById(R.id.btnBorrar);
        LinearLayout llImagen = item.findViewById(R.id.llImagen);
        btnBorrar.setTag(position);
        btnBorrar.setOnClickListener(this);
        if (arrayList.get(position).enviado){
            llImagen.setBackgroundColor(Color.parseColor("#CBFFAD"));
        }else{
            llImagen.setBackgroundColor(Color.parseColor("#FFADAD"));
        }
        String nombre = arrayList.get(position).nombre;
        if (nombre.length()>20){
            nombre=nombre.substring(0,10)+"..."+nombre.substring(nombre.length()-5, nombre.length());
        }
        txtNombre.setText(nombre);
        ivFoto.setImageBitmap(arrayList.get(position).bitmap);
        return item;
    }
    @Override
    public void onClick(View v) {
        if (arrayList.get((int)v.getTag()).galeria){
            GaleriaV2.borrarArrayImagenes((int)v.getTag(),context);
        }else{
            FotosProtocoloAccion.borrarArrayImagenes((int)v.getTag(),context);
        }
    }
}