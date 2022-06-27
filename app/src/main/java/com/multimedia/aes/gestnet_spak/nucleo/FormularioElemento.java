package com.multimedia.aes.gestnet_spak.nucleo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.multimedia.aes.gestnet_spak.R;
import com.multimedia.aes.gestnet_spak.Utils.DatePickerFragment;
import com.multimedia.aes.gestnet_spak.dao.ElementoInstDAO;
import com.multimedia.aes.gestnet_spak.dao.TipoElementosDAO;
import com.multimedia.aes.gestnet_spak.entidades.ElementoInstalacion;
import com.multimedia.aes.gestnet_spak.dialogo.Dialogo;
import com.multimedia.aes.gestnet_spak.entidades.OperacionesTiposElementos;

import com.multimedia.aes.gestnet_spak.entidades.TipoElementos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.multimedia.aes.gestnet_spak.dao.OperacionTipoElementoDAO.buscarOperacionesTiposElementoPorFkTipo;

public class FormularioElemento extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher {

    private JSONArray registro;
    private int height;
    private int id;
    private LinearLayout llFormElemento;
    private Button btnActElement;
    private TextView titTipo;
    private ElementoInstalacion elemento;
    private JSONArray valoresElemento;
    private JSONObject valoresGestnetElemento;
    private JSONArray valoresOperacionesCampos;
    private  List<EditText> allET = new ArrayList<EditText>();
    private  List<Spinner> listaSpinner = new ArrayList<Spinner>();
    private  List<Switch> allSW = new ArrayList<Switch>();
    private  String nombreTipo = "";
    private boolean userIsInteracting;
    private boolean textChange;
    private  ArrayList<String> listaOperaciones = new ArrayList<String>();
    private  ArrayList<String> listaValoresOperaciones = new ArrayList<String>();
    private Context context;
    private int fk_tipo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.ly_formulario_elemento);
        textChange = false;
        llFormElemento = (LinearLayout) findViewById(R.id.llFormElemento);
        titTipo = (TextView) findViewById(R.id.tw_titTipo);
        btnActElement= (Button) findViewById(R.id.btnActualizarDatosElemento);
        btnActElement.setOnClickListener(this);
        Display display = getWindowManager().getDefaultDisplay();
        height = display.getHeight();
        height=height/16;
        id=getIntent().getIntExtra("id",-1);
        nombreTipo=getIntent().getStringExtra("nombreTipo");
        titTipo.setText("Tipo: "+nombreTipo);
        try {
            elemento = ElementoInstDAO.buscarElementoInstalacionPorIdElemento(this,id);
            valoresElemento = new JSONArray(elemento.getValores());
            valoresGestnetElemento = new JSONObject(elemento.getValoresgestnet());
            fk_tipo = elemento.getFk_tipo();
            OperacionesTiposElementos OperacionesTipo = buscarOperacionesTiposElementoPorFkTipo(this,fk_tipo);

            String OperacionesTipoCampos = OperacionesTipo.getCampos();
            valoresOperacionesCampos = new JSONArray(OperacionesTipoCampos);
            if(elemento.getbfinalizado()){
                montarElemento();
            }else{
                montarFormulario();
            }

        } catch (SQLException | JSONException | ParseException e) {
            e.printStackTrace();
        }

    }

public void changeElementosTxt(){
    for(int i =0 ;i<valoresElemento.length();i++){
        String tipo = null;
        String campo = null;
        String campo_mostrar = null;
        try {
            tipo = valoresElemento.getJSONObject(i).getString("tipo");
            campo = valoresElemento.getJSONObject(i).getString("campo");
            campo_mostrar = valoresElemento.getJSONObject(i).getString("campo_mostrar");

            String bOpciones = "";
                switch (tipo) {
                    case "estatico":

                        for(int z=0; z < allET.size(); z++) {
                            EditText et = allET.get(z);
                            String tag = et.getTag().toString();
                            if(tag.equals("presion_extintor") && campo.equals("presion_extintor")){
                                String valor = et.getText().toString();
                                Editable valor2 = et.getText();
                                valor.toLowerCase();
                                valor2.length();
                            }
                            if (tag.equals(campo)) {
                                valoresElemento.getJSONObject(i).put("valor", et.getText().toString());
                            }
                        }
                        for(int t=0; t < listaSpinner.size(); t++){
                            Spinner sp = listaSpinner.get(t);
                            String tag = sp.getTag().toString();
                            if(tag.equals(campo)){
                                valoresElemento.getJSONObject(i).put("valor",sp.getSelectedItem().toString());
                            }
                        }

                        break;
                    case "estatico_fecha":
                        for(int z=0; z < allET.size(); z++){
                            EditText et = allET.get(z);
                            String tag = et.getTag().toString();


                            if(tag.equals(campo)){
                                valoresElemento.getJSONObject(i).put("valor",et.getText().toString());
                            }
                        }
                        break;
                    case "texto":
                        for(int z=0; z < allET.size(); z++){
                            EditText et = allET.get(z);
                            String tag = et.getTag().toString();
                            if(tag.equals("medida_cristal_extinto") && campo.equals("medida_cristal_extinto")){
                                String valor = et.getText().toString();
                                Editable valor2 = et.getText();
                                valor.toLowerCase();
                                valor2.length();
                            }
                            if(tag.equals(campo)){
                                valoresElemento.getJSONObject(i).put("valor",et.getText().toString());
                            }
                        }
                        break;
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    try {
        saveElemento();
        Dialogo.dialogoError("El elemento del tipo "+nombreTipo+" ha sido actualizado correctamente.\n\rGracias.",this);
    } catch (SQLException | JSONException throwables) {
        throwables.printStackTrace();
    }
}
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnActualizarDatosElemento) {
            changeElementosTxt();
        }
    }

    private void saveOperacion(int posicion) throws SQLException {
        String fk_operacion = (listaValoresOperaciones.get(posicion)) ;
        ElementoInstDAO.actualizaOperacion(this,id,fk_operacion);
    }

    private void saveElemento() throws SQLException, JSONException {
        String valoresElString = valoresElemento.toString();
        for(int i =0 ;i<valoresElemento.length();i++){
            String campo_nombre = valoresElemento.getJSONObject(i).getString("campo");
            String tipo = valoresElemento.getJSONObject(i).getString("tipo");
            String valor = valoresElemento.getJSONObject(i).getString("valor");
            valoresGestnetElemento.put(campo_nombre,valor);
        }
        ElementoInstDAO.actualizaValores(this,id,valoresElemento.toString());
        ElementoInstDAO.actualizaValoresGestnet(this,id,valoresGestnetElemento.toString());
        textChange= false;
    }

    private  void reload(){
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    private void montarFormulario() throws JSONException, ParseException, SQLException {
       /* JSONArray UnOrderedArray = valoresElemento;
        Log.e("montarFormulario: ",valoresElemento.toString() );
        SORTJSONArray OrderArray = new SORTJSONArray(this,valoresElemento,"orden");
        JSONArray OrderedArray = OrderArray.SorteredArray;
        Log.e("montarFormulario: ",OrderedArray.toString() );*/
        //_________________SELECTOR OPERACION__________________________
        Spinner spinnerTipoOperacion = (Spinner) findViewById(R.id.spinnerTipoOperaciones);
        listaOperaciones.clear();
        listaValoresOperaciones.clear();
        for (int i = 0; i < valoresOperacionesCampos.length(); i++) {
            JSONObject operaciones = valoresOperacionesCampos.getJSONObject(i);
            listaOperaciones.add(operaciones.getString("nombre"));
            listaValoresOperaciones.add(operaciones.getString("fk_operacion"));
        }
        spinnerTipoOperacion.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaOperaciones));
        elemento.getFk_operacion();
        int fk_operacion = elemento.getFk_operacion();
        for (int i=0;i<listaValoresOperaciones.size();i++){
            int valor =  Integer.parseInt(listaValoresOperaciones.get(i));
            if(fk_operacion == valor){
                spinnerTipoOperacion.setSelection(i);
            }
        }
        spinnerTipoOperacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int selectedPosition, long arg3) {
                try {
                    saveOperacion(selectedPosition);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        //_________________CHECK FINALIZADO__________________________
        CheckBox bFinaCb = (CheckBox) findViewById(R.id.bFinCb);
        bFinaCb.setChecked(elemento.getbfinalizado());
        bFinaCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        elemento.setbfinalizado(true);
                        //Dialogo.dialogoError("Recuerde que al marcar como finalizado si sale de la edición de este Elemento ya no podrá volver a editarlo\n\n",context);
                        reload();
                    } else {
                        elemento.setbfinalizado(false);
                        reload();
                    }

                    ElementoInstDAO.actualizaBfinalizado(context,elemento.getId_elemento(),elemento.getbfinalizado());
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
    });
        //_________________FORMULARIO__________________________

        for(int i =0 ;i<valoresElemento.length();i++){
            Drawable dw = this.getDrawable(R.drawable.borde_inferior);
            JSONObject ELEMENTO =  valoresElemento.getJSONObject(i);

            String campo_nombre =ELEMENTO.getString("campo");
            String campo_mostrar = ELEMENTO.getString("campo_mostrar");
            //ByteBuffer buffer = StandardCharsets.UTF_8.encode(campo_mostrar);
            //campo_mostrar = StandardCharsets.UTF_8.encode(campo_mostrar).toString();
            byte[] campo_mostrarBytes = campo_mostrar.getBytes();
             campo_mostrar = new String(campo_mostrarBytes, StandardCharsets.UTF_8);
            String valor = ELEMENTO.getString("valor");
            if(valor.equals("null")){
                valor="";
            }

            String es = "dd/MM/yyyy";
            String en = "yyyy-MM-dd";
            SimpleDateFormat sdf1 = new SimpleDateFormat(en);
            SimpleDateFormat sdf2 = new SimpleDateFormat(es);

            String tipo = ELEMENTO.getString("tipo");


            switch (tipo){
                    case "estatico":
                        //BUSCO SI ES TIPO SPINNER O TEXTO
                        TipoElementos tipoElemento;
                        String campos;
                        int posicion = 0;
                        JSONArray jsonArray = null;
                        String bOpciones ="";
                        try {
                            tipoElemento = TipoElementosDAO.buscarTiposElementoPorFkTipo(context,fk_tipo);
                            campos = tipoElemento.getCamposElemento();
                            jsonArray = new JSONArray(campos);

                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                        boolean encontrado = false;
                        int x=0;
                        do{
                            JSONObject jsonobj = jsonArray.getJSONObject(x);
                            if (jsonobj.getString("campo").equals(ELEMENTO.getString("campo"))){
                                encontrado=true;
                                bOpciones = jsonobj.getString("bOpciones");
                                posicion = x;
                            }
                            x++;
                        }while(encontrado==false);

                        if (bOpciones.equals("0")){//SI NO ES SPINNER

                            LinearLayout.LayoutParams layoutParamsEstatico = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);

                            TextView txtCampoEstatico = new TextView(this);

                            txtCampoEstatico.setText(campo_mostrar);
                            txtCampoEstatico.setPadding(10, 5, 10, 5);
                            EditText txtEditarEstatico = new EditText(this);
                            txtEditarEstatico.setBackgroundResource(R.drawable.edit_texts_naranja);
                            txtEditarEstatico.setPadding(10, 5, 10, 5);
                            txtEditarEstatico.addTextChangedListener(this);
                            layoutParamsEstatico.setMargins(0, 0, 0, 10);
                            txtEditarEstatico.setLayoutParams(layoutParamsEstatico);
                            if(valor.equals("null")){
                                valor="";
                            }
                            txtEditarEstatico.setTextSize(15);
                            txtEditarEstatico.setText(valor);
                            txtEditarEstatico.setTag(campo_nombre);
                            llFormElemento.addView(txtCampoEstatico);
                            llFormElemento.addView(txtEditarEstatico);
                            allET.add(txtEditarEstatico);
                        }
                        else if (bOpciones.equals("1")){//SI ES SPINNER
                            LinearLayout campoCrearElemento = new LinearLayout(this);
                            LinearLayout.LayoutParams layoutCrearElemento = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            campoCrearElemento.setPadding(0,15,0,15);
                            campoCrearElemento.setLayoutParams(layoutCrearElemento);
                            TextView txtCampoCreacion = new TextView(this);
                            txtCampoCreacion.setWidth(400);
                            txtCampoCreacion.setPadding(10, 5, 10, 5);
                            txtCampoCreacion.setText(campo_mostrar);
                            campoCrearElemento.addView(txtCampoCreacion);

                            Spinner SpinnerCampoCreacion = new Spinner(this);
                            SpinnerCampoCreacion.setPadding(10, 5, 10, 5);
                            SpinnerCampoCreacion.setId(i);
                            List<String> myArraySpinner = new ArrayList<String>();
                            TipoElementos tipoElem = TipoElementosDAO.buscarTiposElementoPorFkTipo(context, fk_tipo);
                            String campo = tipoElem.getCamposElemento();
                            JSONArray arrayOpciones_0 = new JSONArray(campo);
                            JSONObject jsonObj = arrayOpciones_0.getJSONObject(posicion);
                            JSONArray arrayOpciones = jsonObj.getJSONArray("opciones");
                            for (int z=0;z<arrayOpciones.length();z++){
                                JSONObject opcion =  arrayOpciones.getJSONObject(z);
                                myArraySpinner.add(opcion.getString("valor"));
                            }
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, myArraySpinner);
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            SpinnerCampoCreacion.setTag(campo_mostrar);
                            SpinnerCampoCreacion.setBackgroundResource(R.drawable.elemen_form_spiners);
                            listaSpinner.add(SpinnerCampoCreacion);
                            SpinnerCampoCreacion.setBackgroundResource(R.drawable.elemen_form_spiners);
                            SpinnerCampoCreacion.setAdapter(spinnerArrayAdapter);
                            SpinnerCampoCreacion.setSelected(false);
                                for (int q=0; q<myArraySpinner.size();q++){
                                    if (valor.equals(myArraySpinner.get(q))){
                                        SpinnerCampoCreacion.setSelection(q,false);
                                    }
                                }
                            SpinnerCampoCreacion.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            SpinnerCampoCreacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    String valor = myArraySpinner.get(position);
                                    try {

                                        for(int i =0 ;i<valoresElemento.length();i++){
                                            String campo_mostrar = valoresElemento.getJSONObject(i).getString("campo_mostrar");
                                            String tag = SpinnerCampoCreacion.getTag().toString();
                                            if(tag.equals(campo_mostrar)){
                                                valoresElemento.getJSONObject(i).put("valor",valor);
                                            }
                                        }
                                        saveElemento();
                                    } catch (JSONException | SQLException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                            campoCrearElemento.addView(SpinnerCampoCreacion);
                            campoCrearElemento.setBackground(dw);
                            llFormElemento.addView(campoCrearElemento);

                        }
                        break;
                    case "estatico_fecha":
                        LinearLayout campoCrearElemento = new LinearLayout(this);
                        LinearLayout.LayoutParams layoutCrearElemento = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        campoCrearElemento.setPadding(0,10,0,10);
                        campoCrearElemento.setLayoutParams(layoutCrearElemento);
                        TextView txtCampoCreacion = new TextView(this);
                        txtCampoCreacion.setWidth(500);
                        txtCampoCreacion.setPadding(10, 5, 10, 5);
                        txtCampoCreacion.setText(campo_mostrar);
                        campoCrearElemento.addView(txtCampoCreacion);

                        EditText EtxtCampoCreacion = new EditText(this);
                        EtxtCampoCreacion.setPadding(10, 5, 10, 15);
                        EtxtCampoCreacion.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                        EtxtCampoCreacion.setId(i);
                        EtxtCampoCreacion.setText(valor);
                        EtxtCampoCreacion.setTextSize(15);
                        EtxtCampoCreacion.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        //////////////////////////
                        EtxtCampoCreacion.setFocusable(false);
                        EtxtCampoCreacion.setClickable(true);
                        EtxtCampoCreacion.setTag(campo_nombre);
                        EtxtCampoCreacion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showDatePickerDialog(EtxtCampoCreacion);
                            }
                        });
                        //////////////////////////
                        campoCrearElemento.setBackground(dw);
                        campoCrearElemento.addView(EtxtCampoCreacion);
                        allET.add(EtxtCampoCreacion);
                        llFormElemento.addView(campoCrearElemento);

                        break;
                    case "texto":

                        LinearLayout.LayoutParams layoutParamsTexto = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);

                        TextView txtCampo = new TextView(this);

                        txtCampo.setText(campo_mostrar);
                        txtCampo.setPadding(10, 5, 10, 5);
                        EditText txtEditar = new EditText(this);
                        txtEditar.setBackgroundResource(R.drawable.edit_texts_naranja);
                        txtEditar.setPadding(10, 5, 10, 5);
                        txtEditar.addTextChangedListener(this);
                        layoutParamsTexto.setMargins(0, 0, 0, 10);
                        txtEditar.setLayoutParams(layoutParamsTexto);
                        if(valor.equals("null")){
                            valor="";
                        }
                        txtEditar.setText(valor);
                        txtEditar.setTag(campo_nombre);
                        llFormElemento.addView(txtCampo);
                        llFormElemento.addView(txtEditar);
                        allET.add(txtEditar);
                        break;

                    case "dual":
                        LinearLayout parentLayoutDual = new LinearLayout(this);
                        LinearLayout.LayoutParams layoutParamsDual = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        LinearLayout.LayoutParams layoutParamsValorDual = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        LinearLayout.LayoutParams layoutParamsTxtDual = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        layoutParamsValorDual.weight = 1;
                        layoutParamsTxtDual.weight =2;

                        layoutParamsDual.setMargins(0, 0, 0, 10);
                        parentLayoutDual.setLayoutParams(layoutParamsDual);

                        TextView txtDual = new TextView(this);
                        txtDual.setLayoutParams(layoutParamsTxtDual);
                        txtDual.setText(campo_mostrar+": ");
                        txtDual.setPadding(10, 5, 10, 5);
                        txtDual.setTextSize(14);

                        Switch switchBtDual =new Switch(this);
                        switchBtDual.setLayoutParams(layoutParamsValorDual);
                        switchBtDual.setTrackResource(R.drawable.switchtrack);
                        switchBtDual.setThumbResource(R.drawable.switchthumb);
                       // switchBtDual.setSwitchMinWidth(20);
                        switchBtDual.setTextOn("SÍ");
                        switchBtDual.setTextOff("NO");
                        if(valor.equals("1")){
                            switchBtDual.setChecked(true);
                        }else if(valor.equals("2")){
                            switchBtDual.setChecked(false);
                        }
                        switchBtDual.setTag(campo_nombre);

                        switchBtDual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                try {
                                    boolean che = isChecked;
                                    if (isChecked) {
                                        ELEMENTO.put("valor", 1);
                                    } else {
                                        ELEMENTO.put("valor", 2);
                                    }
                                }catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    saveElemento();
                                } catch (SQLException throwables) {
                                    throwables.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        parentLayoutDual.addView(txtDual);
                        parentLayoutDual.addView(switchBtDual);
                        llFormElemento.addView(parentLayoutDual);
                        break;
                case "cuadra":
                    LinearLayout parentLayoutCuadra = new LinearLayout(this);
                    LinearLayout.LayoutParams layoutParamsCuadra = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams layoutParamsValorCuadra = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout.LayoutParams layoutParamsTxtCuadra = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParamsValorCuadra.weight = 1;
                    layoutParamsTxtCuadra.weight =2;

                    layoutParamsCuadra.setMargins(0, 0, 0, 15);

                    parentLayoutCuadra.setLayoutParams(layoutParamsCuadra);

                    TextView txtCuadra = new TextView(this);
                    txtCuadra.setLayoutParams(layoutParamsTxtCuadra);
                    txtCuadra.setText(campo_mostrar+": ");
                    txtCuadra.setPadding(10, 5, 10, 5);
                    txtCuadra.setTextSize(14);
                    List<String> myArraySpinner = new ArrayList<String>();

                    myArraySpinner.add("CORRECTO");
                    myArraySpinner.add("INCORRECTO");
                    myArraySpinner.add("N/A");
                    myArraySpinner.add("CAD");

                    Spinner spBtCuadra =new Spinner(this);

                    spBtCuadra.setPadding(0, 5, 0, 5);
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, myArraySpinner);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spBtCuadra.setTag(campo_nombre);
                    spBtCuadra.setBackgroundResource(R.drawable.elemen_form_spiners);
                    spBtCuadra.setAdapter(spinnerArrayAdapter);
                    spBtCuadra.setSelected(false);
                    switch (valor){
                        case "1":
                            if(spBtCuadra.getSelectedItemPosition() != 0){
                                spBtCuadra.setSelection(0,false);
                            }

                            break;
                        case "2":
                            if(spBtCuadra.getSelectedItemPosition() != 1){
                                spBtCuadra.setSelection(1,false);
                            }
                            break;
                        case "3":
                            if(spBtCuadra.getSelectedItemPosition() != 2){
                                spBtCuadra.setSelection(2,false);
                            }
                            break;
                        case "4":
                            if(spBtCuadra.getSelectedItemPosition() != 3){
                                spBtCuadra.setSelection(3,false);
                            }

                            break;
                    }
                    spBtCuadra.setOnItemSelectedListener(this);

                    parentLayoutCuadra.addView(txtCuadra);
                    parentLayoutCuadra.addView(spBtCuadra);
                    llFormElemento.addView(parentLayoutCuadra);
                    break;
            }
        }
    }

    private void montarElemento() throws JSONException, ParseException, SQLException {
       /* JSONArray UnOrderedArray = valoresElemento;
        Log.e("montarFormulario: ",valoresElemento.toString() );
        SORTJSONArray OrderArray = new SORTJSONArray(this,valoresElemento,"orden");
        JSONArray OrderedArray = OrderArray.SorteredArray;
        Log.e("montarFormulario: ",OrderedArray.toString() );*/

        listaValoresOperaciones.clear();
        listaOperaciones.clear();
        for(int i =0 ;i<valoresOperacionesCampos.length();i++){
            JSONObject operaciones =  valoresOperacionesCampos.getJSONObject(i);
            listaOperaciones.add(operaciones.getString("nombre")) ;
            listaValoresOperaciones.add(operaciones.getString("fk_operacion")) ;
        }

        LinearLayout lyOpe = (LinearLayout) findViewById(R.id.lyOperacion);
        Spinner spinnerTipoOperacion = (Spinner) findViewById(R.id.spinnerTipoOperaciones);
        TextView titTipoOperacion = (TextView) findViewById(R.id.titSpOperacion);
        spinnerTipoOperacion.setVisibility(View.GONE);
        titTipoOperacion.setVisibility(View.GONE);
        /*Spinner spinnerTipoOperacion = (Spinner) findViewById(R.id.spinnerTipoOperaciones);
        spinnerTipoOperacion.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaOperaciones ));*/
        /*
        String fk_operacionStr = "";
        int fk_operacion = elemento.getFk_operacion();
        int posicion = listaOperaciones.indexOf(fk_operacion+"");
        fk_operacionStr = listaOperaciones.get(posicion);
        */
        /*
         */
        int fk_operacion = elemento.getFk_operacion();
        String fk_operacionStr = "";

        for (int i=0;i<listaValoresOperaciones.size();i++){
            int valor =  Integer.parseInt(listaValoresOperaciones.get(i));
            if(fk_operacion == valor){
                fk_operacionStr = listaOperaciones.get(i);
            }
        }
       /* spinnerTipoOperacion.setSelection(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaOperaciones).getPosition("Baja"));

        spinnerTipoOperacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int selectedPosition, long arg3) {
                //saveOperacion(elemento.getId_elemento(),selectedPosition);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });*/
        LinearLayout operacionValorLy = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParamsOpe = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        operacionValorLy.setLayoutParams(layoutParamsOpe);
        TextView txtFkOperacion= new TextView(this);

        txtFkOperacion.setPadding(10, 5, 10, 5);
        //txtFkOperacion.setTypeface(txtFkOperacion.getTypeface(), Typeface.BOLD);
        txtFkOperacion.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        txtFkOperacion.setTextSize(18);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtFkOperacion.setText(Html.fromHtml("Tipo de Operación: <b>"+fk_operacionStr+"</b>", Html.FROM_HTML_MODE_COMPACT));
        }else{
            txtFkOperacion.setText(Html.fromHtml("Tipo de Operación: <b>"+fk_operacionStr+"</b>"));
        }
        operacionValorLy.addView(txtFkOperacion);

        lyOpe.addView(operacionValorLy);

        CheckBox bFinaCb = (CheckBox) findViewById(R.id.bFinCb);
        bFinaCb.setChecked(elemento.getbfinalizado());
        bFinaCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if (isChecked) {
                        elemento.setbfinalizado(true);
                        //Dialogo.dialogoError("Recuerde que al marcar como finalizado si sale de la edición de este Elemento ya no podrá volver a editarlo\n\n",context);
                        reload();
                    } else {
                        elemento.setbfinalizado(false);
                        reload();
                    }

                    ElementoInstDAO.actualizaBfinalizado(context,elemento.getId_elemento(),elemento.getbfinalizado());

                }catch (SQLException e){
                    e.printStackTrace();
                }
            }
        });

        for(int i =0 ;i<valoresElemento.length();i++){
            JSONObject ELEMENTO =  valoresElemento.getJSONObject(i);
            String campo_nombre =ELEMENTO.getString("campo");
            String campo_mostrar = ELEMENTO.getString("campo_mostrar");
            //ByteBuffer buffer = StandardCharsets.UTF_8.encode(campo_mostrar);
            //campo_mostrar = StandardCharsets.UTF_8.encode(campo_mostrar).toString();

            byte[] campo_mostrarBytes = campo_mostrar.getBytes();
            campo_mostrar = new String(campo_mostrarBytes, StandardCharsets.UTF_8);

            String valor =ELEMENTO.getString("valor");
            if(valor.equals("null")){
                valor="";
            }
            String tipo = ELEMENTO.getString("tipo");

            String es = "dd/MM/yyyy";
            String en = "yyyy-MM-dd";
            SimpleDateFormat sdf1 = new SimpleDateFormat(en);
            SimpleDateFormat sdf2 = new SimpleDateFormat(es);
            LinearLayout parentLayout = new LinearLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams layoutParamsValor = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams layoutParamsTxt = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsValor.weight =1;
            layoutParamsTxt.weight =2;

            layoutParams.setMargins(0, 0, 0, 10);
            parentLayout.setLayoutParams(layoutParams);

            Drawable dw = this.getDrawable(R.drawable.borde_inferior);

            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                parentLayout.setBackgroundDrawable(dw);
            } else {
                parentLayout.setBackground(dw);
            }

            TextView txt = new TextView(this);
            txt.setLayoutParams(layoutParamsTxt);
            txt.setText(campo_mostrar+": ");
            txt.setPadding(10, 5, 10, 5);
            txt.setTextSize(14);

            TextView txtValor = new TextView(this);
            txtValor.setLayoutParams(layoutParamsValor);
            txtValor.setPadding(10, 5, 10, 5);
            txtValor.setTypeface(txtValor.getTypeface(), Typeface.BOLD);
            txtValor.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            txtValor.setTextSize(16);
            switch (tipo){
                case "dual":
                    switch (valor){
                        case "1":
                            valor= "OK";
                            break;
                        case "2":
                            valor= "NO OK";
                            break;
                    }
                    break;
                case "cuadra":
                    switch (valor){
                        case "1":
                            valor= "CORRECTO";
                            break;
                        case "2":
                            valor= "INCORRECTO";
                            break;
                        case "3":
                            valor= "N/A";
                            break;
                        case "4":
                            valor= "CAD";
                            break;
                    }
                    break;
                default:
                    valor = valor;
                    break;
            }
            txtValor.setText(valor);

            parentLayout.addView(txt);
            parentLayout.addView(txtValor);
            llFormElemento.addView(parentLayout);


        }
    }

    public boolean isNumber(String num){
        try{
            Double.parseDouble(num);
        }catch (Exception e){
            return  false;
        }
        return true;
    }

    public void btBackPressed(){
        if(textChange){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Ha realizado cambios en el Elemento.\r\nPor favor asegurese de haber guardado esos cambios haciendo click en el botón\r\nActualizar Elemento.\r\n\r\nGracias.");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "Salir SIN ACTUALIZAR",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            textChange = false;
                            finish();
                        }
                    });
            builder1.setNegativeButton(
                    "ACTUALIZAR",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            changeElementosTxt();
                            dialog.cancel();
                            finish();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.setCanceledOnTouchOutside(false);
            alert11.show();
        }else{
            super.onBackPressed();
        }

    }

    @Override
    public void onBackPressed() {

      btBackPressed();

    }
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String campo_nombre = parent.getTag().toString();
        String itemSelected = parent.getItemAtPosition(position).toString();
        if (userIsInteracting) {
            for (int i = 0; i < valoresElemento.length(); i++) {
                JSONObject ELEMENTO = null;
                try {
                    ELEMENTO = valoresElemento.getJSONObject(i);
                    if (ELEMENTO.getString("campo").equals(campo_nombre)) {
                        switch (itemSelected) {
                            case "CORRECTO":
                                ELEMENTO.put("valor", 1);
                                break;
                            case "INCORRECTO":
                                ELEMENTO.put("valor", 2);
                                break;
                            case "N/A":
                                ELEMENTO.put("valor", 3);
                                break;
                            case "CAD":
                                ELEMENTO.put("valor", 4);
                                break;
                        }
                        saveElemento();
                    }
                } catch (JSONException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(userIsInteracting){
            textChange = true;
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

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
}
