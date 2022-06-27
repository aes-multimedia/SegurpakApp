package com.multimedia.aes.gestnet_spak.nucleo;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.multimedia.aes.gestnet_spak.R;
import com.multimedia.aes.gestnet_spak.Utils.CheckBoxMateriales;
import com.multimedia.aes.gestnet_spak.dao.ArticuloDAO;
import com.multimedia.aes.gestnet_spak.dao.ArticuloParteDAO;
import com.multimedia.aes.gestnet_spak.dao.ParteDAO;
import com.multimedia.aes.gestnet_spak.entidades.Articulo;
import com.multimedia.aes.gestnet_spak.entidades.ArticuloParte;
import com.multimedia.aes.gestnet_spak.entidades.Parte;

import java.sql.SQLException;
import java.util.ArrayList;

public class GestionMateriales extends AppCompatActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener {

    private Parte parte;
    private ArrayList<ArticuloParte> articuloParteArrayList = new ArrayList<>();
    private ArrayList<Articulo> articuloArrayList = new ArrayList<>();
    private Button btnTerminar;
    private LinearLayout llPadre;
    private Boolean createdView=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_materiales);
        Intent intent = getIntent();
        llPadre =  findViewById(R.id.llPadre);
        btnTerminar = findViewById(R.id.btnTerminar);
        btnTerminar.setOnClickListener(this);
        LinearLayout linearLayout = new LinearLayout(this);
        if (intent != null) {
            int fk_parte = intent.getIntExtra("fk_parte", 0);
            try {
                if (ArticuloParteDAO.buscarArticuloParteFkParte(this, fk_parte) != null) {
                    parte = ParteDAO.buscarPartePorId(this, fk_parte);
                    articuloParteArrayList.addAll(ArticuloParteDAO.buscarArticuloParteFkParte(this, fk_parte));
                    for (ArticuloParte articuloParte : articuloParteArrayList)
                        articuloArrayList.add(ArticuloDAO.buscarArticuloPorID(this, articuloParte.getFk_articulo()));
                    for (int i = 0; i < articuloArrayList.size(); i++) {
                        ArticuloParte art = ArticuloParteDAO.buscarArticuloPartePorFkParteFkArticulo(this,articuloArrayList.get(i).getId_articulo(),fk_parte);
                        boolean presupuestar = art.getPresupuestar();
                        boolean facturar = art.getFacturar();
                        LinearLayout linearLayoutHorizontal = new LinearLayout(this);
                        linearLayoutHorizontal.setWeightSum(10);


                        TextView tvNombreArticulo = new TextView(this);
                        tvNombreArticulo.setLayoutParams(layoutParams(true));
                        tvNombreArticulo.setText(articuloArrayList.get(i).getNombre_articulo());



                        CheckBoxMateriales checkBoxPresupuestar = new CheckBoxMateriales(this,articuloArrayList.get(i).getId_articulo(),"presupuestar");
                        checkBoxPresupuestar.setOnCheckedChangeListener(this);
                        checkBoxPresupuestar.setLayoutParams(layoutParams(false));

                        CheckBoxMateriales checkBoxFacturar = new CheckBoxMateriales(this,articuloArrayList.get(i).getId_articulo(),"facturar");
                        checkBoxFacturar.setOnCheckedChangeListener(this);
                        checkBoxFacturar.setLayoutParams(layoutParams(false));


                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                        linearLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
                        linearLayout.setPadding(5, 5, 5, 5);

                        checkBoxPresupuestar.setHintTextColor(Color.parseColor("#ff9002"));
                        checkBoxPresupuestar.setLinkTextColor(Color.parseColor("#ff9002"));
                        checkBoxPresupuestar.setChecked(presupuestar);
                        checkBoxFacturar.setHintTextColor(Color.parseColor("#ff9002"));
                        checkBoxFacturar.setLinkTextColor(Color.parseColor("#ff9002"));
                        checkBoxFacturar.setChecked(facturar);

                        linearLayoutHorizontal.addView(tvNombreArticulo);
                        linearLayoutHorizontal.addView(checkBoxPresupuestar);
                        linearLayoutHorizontal.addView(checkBoxFacturar);

                        linearLayout.addView(linearLayoutHorizontal);



                    }
                    llPadre.addView(linearLayout);
                }
        } catch(SQLException e){
            e.printStackTrace();
        }



    }

        createdView=true;

    }

    @Override
    public void onClick(View v) {

        finish();


    }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if(createdView) {

            CheckBoxMateriales chk = (CheckBoxMateriales) buttonView;

            try {
                int idArtParte = ArticuloParteDAO.buscarArticuloPartePorFkParteFkArticulo(this,chk.getIdArticulo(),parte.getId_parte()).getId();
                if (chk.getTipo().equals("presupuestar")) {
                    ArticuloParteDAO.actualizarPresupuestar(this, idArtParte, isChecked);
                } else {
                    ArticuloParteDAO.actualizarFacturar(this, idArtParte, isChecked);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        }



    private TableRow.LayoutParams layoutParams(boolean isTextView){
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        params.weight = isTextView ? (float) 5.5 :  (float) 2.25;
        params.gravity = Gravity.CENTER;
        params.setMargins(10,15,7,15);
        return params;
    }
}
