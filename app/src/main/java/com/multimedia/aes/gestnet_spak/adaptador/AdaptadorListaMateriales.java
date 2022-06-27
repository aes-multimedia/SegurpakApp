package com.multimedia.aes.gestnet_spak.adaptador;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.multimedia.aes.gestnet_spak.R;
import com.multimedia.aes.gestnet_spak.dao.ArticuloParteDAO;
import com.multimedia.aes.gestnet_spak.entidades.Articulo;
import com.multimedia.aes.gestnet_spak.fragments.TabFragment6_materiales;

import java.sql.SQLException;
import java.util.ArrayList;


public class AdaptadorListaMateriales extends ArrayAdapter implements View.OnLongClickListener {
    //VARIABLES GLOBALES
    private Context context;
    private int view;
    private ArrayList<Articulo> arrayList;
    private Activity activity;

    private int fk_parte;
    //CONSTRUCTOR
    public AdaptadorListaMateriales(Context context, int view, ArrayList<Articulo> arrayList, Activity activity,int fk_parte) {
        super(context, view, arrayList);
        this.context = context;
        this.view = view;
        this.arrayList=arrayList;
        this.activity=activity;
        this.fk_parte=fk_parte;
    }
    //OVERRIDE METHODS
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;
        if (item == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            item = inflater.inflate(view, null);
        }
        LinearLayout linearLayout =  item.findViewById(R.id.llRow);
        linearLayout.setTag(arrayList.get(position).getId_articulo());
        linearLayout.setOnLongClickListener(this);
        TextView txtTituloArticulo =  item.findViewById(R.id.txtTituloArticulo);
        TextView txtUsadas =  item.findViewById(R.id.txtUsadas);
        TextView txtPrecio =  item.findViewById(R.id.txtPrecio);
        ImageView imagen =  item.findViewById(R.id.imagenMaterialEntregadoSiNo);

        txtTituloArticulo.setText(arrayList.get(position).getNombre_articulo()+"-"+arrayList.get(position).getReferencia());
        double usados = 1;
        try {
            if(ArticuloParteDAO.buscarArticuloPartePorFkParteFkArticulo(context,arrayList.get(position).getId_articulo(),fk_parte) != null) {
                usados = ArticuloParteDAO.buscarArticuloPartePorFkParteFkArticulo(context, arrayList.get(position).getId_articulo(), fk_parte).getUsados();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        txtUsadas.setText(usados+"");
        if(arrayList.get(position).isEntregado()==0){
            imagen.setImageResource(R.drawable.ic_done_black_24dp);

        }
        txtPrecio.setText(   (String.format("%.2f", arrayList.get(position).getTarifa()*usados)+"€"));

        return item;
    }
    @Override
    public boolean onLongClick(View v) {
        TabFragment6_materiales.borrarArticulo(Integer.parseInt(v.getTag().toString()));
        return false;
    }
}
