package com.multimedia.aes.gestnet_spak.nucleo;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.multimedia.aes.gestnet_spak.R;
import com.multimedia.aes.gestnet_spak.Utils.DatePickerFragment;
import com.multimedia.aes.gestnet_spak.dao.ElementoInstDAO;
import com.multimedia.aes.gestnet_spak.dao.TipoElementosDAO;
import com.multimedia.aes.gestnet_spak.entidades.TipoElementos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.List;


import static com.multimedia.aes.gestnet_spak.dao.TipoElementosDAO.buscarTodos;
import static com.multimedia.aes.gestnet_spak.dao.TipoElementosDAO.buscarTiposElementoPorFkTipo;

public class FormularioCrearElemento extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private  ArrayList<String> listaTipoElementos = new ArrayList<String>();
    private  ArrayList<Integer> FkTipoListaTipoElementos = new ArrayList<Integer>();
    private  List<TipoElementos> listaElementos = new ArrayList<TipoElementos>();
    private Context context;
    private JSONArray camposTipoElemento;
    private List<String> listaIdsEditText = new ArrayList<String>();
    private List<String> listacampos = new ArrayList<String>();
    private List<String> listaTipos = new ArrayList<String>();
    private List<String> listaCampoMostrar = new ArrayList<String>();
    private List<String> listaOrden = new ArrayList<String>();

    private List<Integer> listaTipo = new ArrayList<Integer>();
    private int fk_tipo = -1;
    private String nombre_tipo = "";
    private int fk_parte = 0;
    private int fk_maquina = 0;
    private int id = -1;

    private JSONArray valores = new JSONArray();
    private JSONObject valoresCampos;
    private JSONObject valoresGestnet = new JSONObject();
    private JSONObject registro = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_crear_elemento);
        BtnCrearElemento();
        fk_parte = getIntent().getIntExtra("fk_parte", 0);
        fk_maquina = getIntent().getIntExtra("fk_maquina", 0);
        Spinner spinnerTipoElemento = (Spinner) findViewById(R.id.spinnerTipoElemento);
        listaElementos.clear();
        FkTipoListaTipoElementos.clear();
        try {
            listaElementos = TipoElementosDAO.buscarTodos(this);

            List<TipoElementos> tipoelemento;
            tipoelemento = buscarTodos(context);
            listaTipoElementos.add("-- Seleccione tipo de elemento a crear --");
            FkTipoListaTipoElementos.add(-1);
            for(int i =0 ;i<tipoelemento.size();i++){
                listaTipoElementos.add(tipoelemento.get(i).getTipoElemento()) ;
                FkTipoListaTipoElementos.add(tipoelemento.get(i).getFkTipoElemento()) ;
            }
            spinnerTipoElemento.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaTipoElementos));
            spinnerTipoElemento.setOnItemSelectedListener(this);

        } catch (SQLException throwables) { throwables.printStackTrace(); }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        if(pos>0){
            try {
                montarFormularioCrearElemento(adapterView,view,pos);
                Spinner selector = findViewById(R.id.spinnerTipoElemento);
                selector.setVisibility(View.GONE);
                TextView titulo = findViewById(R.id.tituloCrearElemento);
                titulo.setText("Crear "+listaTipoElementos.get(pos));
            } catch (JSONException e) { e.printStackTrace(); } catch (ParseException e) { e.printStackTrace(); } catch (SQLException throwables) { throwables.printStackTrace(); }
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }

    private void montarFormularioCrearElemento(AdapterView<?> parent, View view, int pos) throws JSONException, ParseException, SQLException {

        fk_tipo = FkTipoListaTipoElementos.get(pos);
        nombre_tipo = listaTipoElementos.get(pos);
        TipoElementos TipoelementoElegido = buscarTiposElementoPorFkTipo(context,fk_tipo);
        String campos = TipoelementoElegido.getCamposElemento();
        camposTipoElemento = new JSONArray(campos);

        String tipo = "";
        int opciones = 0;
        String campo_bbdd ="";
        String campo_mostrar = "";
        String orden = "";
        Drawable dw = this.getDrawable(R.drawable.borde_inferior);

        for (int i=0;i<camposTipoElemento.length();i++){
            JSONObject elemento =  camposTipoElemento.getJSONObject(i);
            tipo = elemento.getString("tipo");
            campo_mostrar = elemento.getString("campo_mostrar");
            orden = elemento.getString("orden");
            if (campo_mostrar.equals("Fec. SeÃ±al")){
                campo_mostrar = "Fec. Señal";
            }
            campo_bbdd = elemento.getString("campo");
            opciones = Integer.parseInt(elemento.getString("bOpciones"));
            if(tipo.equals("estatico")||(tipo.equals("estatico_fecha"))){
                listacampos.add(campo_bbdd);
                listaTipos.add(tipo);
                listaCampoMostrar.add(campo_mostrar);
                listaOrden.add(orden);
            } else if(tipo.equals("dual")){
                listacampos.add(campo_bbdd);
                listaTipos.add(tipo);
                listaCampoMostrar.add(campo_mostrar);
                listaOrden.add(orden);

                valoresGestnet.put(campo_bbdd,1);

                valoresCampos = new JSONObject();
                valoresCampos.put("campo",campo_bbdd);
                valoresCampos.put("valor",1);
                valoresCampos.put("campo_mostrar",campo_mostrar);
                valoresCampos.put("tipo",tipo);
                valoresCampos.put("orden",orden);
                valoresCampos.put("bTitularApp",0);
            } else if(tipo.equals("cuadra")){
                listacampos.add(campo_bbdd);
                listaTipos.add(tipo);
                listaCampoMostrar.add(campo_mostrar);
                listaOrden.add(orden);

                valoresGestnet.put(campo_bbdd,1);

                valoresCampos = new JSONObject();
                valoresCampos.put("campo",campo_bbdd);
                valoresCampos.put("valor",1);
                valoresCampos.put("campo_mostrar",campo_mostrar);
                valoresCampos.put("tipo",tipo);
                valoresCampos.put("orden",orden);
                valoresCampos.put("bTitularApp",0);
            } else{
                listacampos.add(campo_bbdd);
                listaTipos.add(tipo);
                listaCampoMostrar.add(campo_mostrar);
                listaOrden.add(orden);
            }
            valores.put(valoresCampos);
            LinearLayout lyCampo = (LinearLayout) findViewById(R.id.layoutCreacion);
            switch (tipo){
                case "estatico"://texto
                    if (opciones == 0){
                        listaTipo.add(0);
                        LinearLayout campoCrearElemento = new LinearLayout(this);
                        LinearLayout.LayoutParams layoutCrearElemento = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        campoCrearElemento.setPadding(0,10,0,10);
                        campoCrearElemento.setLayoutParams(layoutCrearElemento);
                        TextView txtCampoCreacion = new TextView(this);
                        txtCampoCreacion.setPadding(10, 5, 10, 5);
                        txtCampoCreacion.setTextSize(18);
                        txtCampoCreacion.setWidth(500);
                        txtCampoCreacion.setText(campo_mostrar);
                        campoCrearElemento.addView(txtCampoCreacion);
                        LinearLayout campoCrearElemento2 = new LinearLayout(this);
                        EditText EtxtCampoCreacion = new EditText(this);
                        EtxtCampoCreacion.setPadding(10, 5, 10, 5);
                        EtxtCampoCreacion.setTextSize(18);
                        EtxtCampoCreacion.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                        EtxtCampoCreacion.setBackgroundResource(R.drawable.edit_texts_naranja);
                        EtxtCampoCreacion.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        EtxtCampoCreacion.setId(i);
                        listaIdsEditText.add(i+"");
                        campoCrearElemento2.addView(EtxtCampoCreacion);


                        lyCampo.addView(campoCrearElemento);
                        lyCampo.addView(campoCrearElemento2);
                    }else{//spinner
                        listaTipo.add(1);
                        LinearLayout campoCrearElemento = new LinearLayout(this);
                        LinearLayout.LayoutParams layoutCrearElemento = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        campoCrearElemento.setPadding(0,15,0,15);
                        campoCrearElemento.setLayoutParams(layoutCrearElemento);
                        TextView txtCampoCreacion = new TextView(this);
                        txtCampoCreacion.setWidth(400);
                        txtCampoCreacion.setPadding(10, 5, 10, 5);
                        txtCampoCreacion.setTextSize(18);
                        txtCampoCreacion.setText(campo_mostrar);
                        campoCrearElemento.addView(txtCampoCreacion);

                        Spinner SpinnerCampoCreacion = new Spinner(this);
                        SpinnerCampoCreacion.setPadding(10, 5, 10, 5);
                        SpinnerCampoCreacion.setId(i);
                        listaIdsEditText.add(i+"");
                        List<String> myArraySpinner = new ArrayList<String>();
                        JSONArray arrayOpciones = elemento.getJSONArray("opciones");
                        for (int z=0;z<arrayOpciones.length();z++){
                            JSONObject opcion =  arrayOpciones.getJSONObject(z);
                            myArraySpinner.add(opcion.getString("valor"));
                        }
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, myArraySpinner);
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        SpinnerCampoCreacion.setTag(campo_mostrar);
                        SpinnerCampoCreacion.setBackgroundResource(R.drawable.elemen_form_spiners);
                        SpinnerCampoCreacion.setAdapter(spinnerArrayAdapter);
                        SpinnerCampoCreacion.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        campoCrearElemento.setBackground(dw);
                        campoCrearElemento.addView(SpinnerCampoCreacion);

                        lyCampo.addView(campoCrearElemento);

                    }
                    break;
                case "estatico_fecha"://texto fecha
                    listaTipo.add(2);
                    LinearLayout campoCrearElemento = new LinearLayout(this);
                    LinearLayout.LayoutParams layoutCrearElemento = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    campoCrearElemento.setPadding(0,10,0,10);
                    campoCrearElemento.setLayoutParams(layoutCrearElemento);
                    TextView txtCampoCreacion = new TextView(this);
                    txtCampoCreacion.setWidth(500);
                    txtCampoCreacion.setPadding(10, 5, 10, 5);
                    txtCampoCreacion.setTextSize(18);
                    txtCampoCreacion.setText(campo_mostrar);
                    campoCrearElemento.addView(txtCampoCreacion);

                    EditText EtxtCampoCreacion = new EditText(this);
                    EtxtCampoCreacion.setPadding(10, 5, 10, 15);
                    EtxtCampoCreacion.setTextSize(18);
                    EtxtCampoCreacion.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                    EtxtCampoCreacion.setId(i);
                    listaIdsEditText.add(i+"");
                    EtxtCampoCreacion.setHint("DD/MM/AAAA");
                    EtxtCampoCreacion.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    campoCrearElemento.setBackground(dw);
                    //////////////////////////
                    EtxtCampoCreacion.setFocusable(false);
                    EtxtCampoCreacion.setClickable(true);
                    EtxtCampoCreacion.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showDatePickerDialog(EtxtCampoCreacion);
                        }
                    });
                    //////////////////////////
                    campoCrearElemento.addView(EtxtCampoCreacion);
                    lyCampo.addView(campoCrearElemento);
                    break;
                case "texto":
                    listaIdsEditText.add(i+"");
                    listaTipo.add(3);
                    break;
                case "dual":
                    listaIdsEditText.add(i+"");
                    listaTipo.add(4);
                    break;
                case "cuadra":
                    listaIdsEditText.add(i+"");
                    listaTipo.add(5);
                    break;
                default:

                    break;
            }
        }
        }

    private void showDatePickerDialog(final EditText editText) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                final String selectedDate = twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;
                editText.setText(selectedDate);
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    private void BtnCrearElemento(){
        Button btnCrear = (Button) findViewById(R.id.btnCrearElemento);
        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean error=false;
                String nombreCampo = "";
                String tabla = "";
                try {
                    tabla  = TipoElementosDAO.buscarTablaporFkTipo(context,fk_tipo);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                int fk_elemento_gestnet = -1;
                valores = new JSONArray();
                JSONObject jsonObj = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                for (int i=0; i< listaTipo.size();i++){
                    try {
                        TipoElementos tipoElemento = TipoElementosDAO.buscarTiposElementoPorFkTipo(context,fk_tipo);
                        String campos = tipoElemento.getCamposElemento();
                        jsonArray = new JSONArray(campos);
                        jsonObj = jsonArray.getJSONObject(i);
                    } catch (SQLException | JSONException throwables) {
                        throwables.printStackTrace();
                    }
                    if (listaTipo.get(i)==0){//para tipos estatico
                        EditText et = (EditText) findViewById(Integer.parseInt(listaIdsEditText.get(i)));
                        try {
                            registro.put(listacampos.get(i),et.getText());
                            valoresGestnet.put(listacampos.get(i),et.getText());

                            valoresCampos = new JSONObject();
                            valoresCampos.put("campo",listacampos.get(i));
                            valoresCampos.put("valor",et.getText());
                            valoresCampos.put("campo_mostrar",listaCampoMostrar.get(i));
                            valoresCampos.put("tipo",listaTipos.get(i));

                            for (int x=0;x<jsonArray.length();x++){
                            valoresCampos.put("orden",listaOrden.get(i));

                                nombreCampo = jsonObj.getString("campo");
                                if (nombreCampo.equals(listacampos.get(i))){
                                    nombreCampo = jsonObj.getString("campo");
                                    if (jsonObj.getString("bCampoOrden").equals("1")){
                                        valoresCampos.put("bCampoOrden",1);
                                    }else{
                                        valoresCampos.put("bCampoOrden",0);
                                    }
                                    if (jsonObj.getString("bCampoTit").equals("1")){
                                        valoresCampos.put("bTitularApp",1);
                                    }else{
                                        valoresCampos.put("bTitularApp",0);
                                    }
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(listaTipo.get(i)==1){//para tipos estatico con opciones
                        Spinner sp = (Spinner) findViewById(Integer.parseInt(listaIdsEditText.get(i)));
                        try {
                            registro.put(listacampos.get(i),sp.getSelectedItemPosition()+1);
                            valoresGestnet.put(listacampos.get(i),sp.getSelectedItemPosition()+1);

                            valoresCampos = new JSONObject();
                            valoresCampos.put("campo",listacampos.get(i));
                            String valor = String.valueOf(sp.getSelectedItemPosition()+1);

                            JSONArray arrayOpciones = jsonObj.getJSONArray("opciones");
                            JSONObject jsonOBJSpinner = arrayOpciones.getJSONObject(sp.getSelectedItemPosition());
                            String valorSpinner = jsonOBJSpinner.getString("valor");
                            valoresCampos.put("valor",valorSpinner);
                            valoresCampos.put("campo_mostrar",listaCampoMostrar.get(i));
                            valoresCampos.put("tipo",listaTipos.get(i));
                            for (int x=0;x<jsonArray.length();x++){
                                nombreCampo = jsonObj.getString("campo");
                                if (nombreCampo.equals(listacampos.get(i))){
                                    valoresCampos.put("orden",jsonObj.getString("orden"));
                                    if (jsonObj.getString("bCampoOrden").equals("1")){
                                        valoresCampos.put("bCampoOrden",1);
                                    }else{
                                        valoresCampos.put("bCampoOrden",0);
                                    }
                                    if (jsonObj.getString("bCampoTit").equals("1")){
                                        valoresCampos.put("bTitularApp",1);
                                    }else{
                                        valoresCampos.put("bTitularApp",0);
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(listaTipo.get(i)==2){//para tipos estatico fecha
                        EditText etFecha = (EditText) findViewById(Integer.parseInt(listaIdsEditText.get(i)));
                        try {
                            registro.put(listacampos.get(i),etFecha.getText());
                            valoresGestnet.put(listacampos.get(i),etFecha.getText());

                            valoresCampos = new JSONObject();
                            valoresCampos.put("campo",listacampos.get(i));
                            valoresCampos.put("valor",etFecha.getText());
                            valoresCampos.put("campo_mostrar",listaCampoMostrar.get(i));
                            valoresCampos.put("tipo",listaTipos.get(i));
                            for (int x=0;x<jsonArray.length();x++){
                                nombreCampo = jsonObj.getString("campo");
                                if (nombreCampo.equals(listacampos.get(i))){
                                    valoresCampos.put("orden",jsonObj.getString("orden"));
                                    if (jsonObj.getString("bCampoOrden").equals("1")){
                                        valoresCampos.put("bCampoOrden",1);
                                    }else{
                                        valoresCampos.put("bCampoOrden",0);
                                    }
                                    if (jsonObj.getString("bCampoTit").equals("1")){
                                        valoresCampos.put("bTitularApp",1);
                                    }else{
                                        valoresCampos.put("bTitularApp",0);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(listaTipo.get(i)==3) {//tipo texto
                        try {
                            //registro.put(listacampos.get(i),"");
                            valoresGestnet.put(listacampos.get(i),"");

                            valoresCampos = new JSONObject();
                            valoresCampos.put("campo",listacampos.get(i));
                            valoresCampos.put("valor","");
                            valoresCampos.put("campo_mostrar",listaCampoMostrar.get(i));
                            valoresCampos.put("tipo",listaTipos.get(i));
                            for (int x=0;x<jsonArray.length();x++){
                                nombreCampo = jsonObj.getString("campo");
                                if (nombreCampo.equals(listacampos.get(i))){
                                    valoresCampos.put("orden",jsonObj.getString("orden"));
                                    if (jsonObj.getString("bCampoOrden").equals("1")){
                                        valoresCampos.put("bCampoOrden",1);
                                    }else{
                                        valoresCampos.put("bCampoOrden",0);
                                    }
                                    if (jsonObj.getString("bCampoTit").equals("1")){
                                        valoresCampos.put("bTitularApp",1);
                                    }else{
                                        valoresCampos.put("bTitularApp",0);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(listaTipo.get(i)==4) {//tipo dual
                        try {
                            //registro.put(listacampos.get(i),"");
                            valoresGestnet.put(listacampos.get(i),"");

                            valoresCampos = new JSONObject();
                            valoresCampos.put("campo",listacampos.get(i));
                            valoresCampos.put("valor","1");
                            valoresCampos.put("campo_mostrar",listaCampoMostrar.get(i));
                            valoresCampos.put("tipo",listaTipos.get(i));
                            for (int x=0;x<jsonArray.length();x++){
                                nombreCampo = jsonObj.getString("campo");
                                if (nombreCampo.equals(listacampos.get(i))){
                                    valoresCampos.put("orden",jsonObj.getString("orden"));
                                    if (jsonObj.getString("bCampoOrden").equals("1")){
                                        valoresCampos.put("bCampoOrden",1);
                                    }else{
                                        valoresCampos.put("bCampoOrden",0);
                                    }
                                    if (jsonObj.getString("bCampoTit").equals("1")){
                                        valoresCampos.put("bTitularApp",1);
                                    }else{
                                        valoresCampos.put("bTitularApp",0);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if(listaTipo.get(i)==5) {//tipo cuadra
                        try {
                            //registro.put(listacampos.get(i),"");
                            valoresGestnet.put(listacampos.get(i),"");

                            valoresCampos = new JSONObject();
                            valoresCampos.put("campo",listacampos.get(i));
                            valoresCampos.put("valor","");
                            valoresCampos.put("campo_mostrar",listaCampoMostrar.get(i));
                            valoresCampos.put("tipo",listaTipos.get(i));
                            for (int x=0;x<jsonArray.length();x++){
                                nombreCampo = jsonObj.getString("campo");
                                if (nombreCampo.equals(listacampos.get(i))){
                                    valoresCampos.put("orden",jsonObj.getString("orden"));
                                    if (jsonObj.getString("bCampoOrden").equals("1")){
                                        valoresCampos.put("bCampoOrden",1);
                                    }else{
                                        valoresCampos.put("bCampoOrden",0);
                                    }
                                    if (jsonObj.getString("bCampoTit").equals("1")){
                                        valoresCampos.put("bTitularApp",1);
                                    }else{
                                        valoresCampos.put("bTitularApp",0);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    valores.put(valoresCampos);
                }
                    ElementoInstDAO.newElementoInstalacion(context, fk_maquina, fk_parte, fk_tipo, nombre_tipo, tabla,"", fk_elemento_gestnet,registro.toString(), "",0,false, true);
                    try {
                        id = ElementoInstDAO.buscarUltimoIdElemento(context);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    try {
                        String valor = valores.toString().replaceAll("null,","");
                        ElementoInstDAO.actualizarElementoInstalacion(context,id,fk_maquina,fk_parte,fk_tipo,nombre_tipo,tabla,valor,valoresGestnet.toString(),-1);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                        Toast toast =
                            Toast.makeText(getApplicationContext(),
                                    "Elemento creado correctamente", Toast.LENGTH_SHORT);
                    toast.show();

                    Intent intent = new Intent(view.getContext(), Index.class);
                    view.getContext().startActivity(intent);
                }
        });
    }
}
