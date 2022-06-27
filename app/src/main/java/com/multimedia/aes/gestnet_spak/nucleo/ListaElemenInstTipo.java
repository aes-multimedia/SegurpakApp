package com.multimedia.aes.gestnet_spak.nucleo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.multimedia.aes.gestnet_spak.R;
import com.multimedia.aes.gestnet_spak.SharedPreferences.GestorSharedPreferences;
import com.multimedia.aes.gestnet_spak.Utils.SORTJSONArray;
import com.multimedia.aes.gestnet_spak.adaptador.AdaptadorListaElementosTipo;

import com.multimedia.aes.gestnet_spak.dao.ElementoInstDAO;
import com.multimedia.aes.gestnet_spak.dao.TipoElementosDAO;
import com.multimedia.aes.gestnet_spak.entidades.ElementoInstalacion;
import com.multimedia.aes.gestnet_spak.entidades.Parte;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.multimedia.aes.gestnet_spak.dao.ElementoInstDAO.buscarElementoInstalacionPorFkParteFkTipo;

public class ListaElemenInstTipo extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private static ListView LvElemTipo;
    private Button btnNuevoElem;
    private TextView tit;
    private static Parte parte = null;
    private static ArrayList<ElementoInstalacion> arrayListElemInstTipo = new ArrayList<>();
    private static AdaptadorListaElementosTipo adaptadorListaElementosTipo;
    private static int alto,height;
    private int idParte = 0;
    private int idTipo = -1;
    private String nombreTipo = "";
    private EditText etBusqueda;
    private Context context;
    private Spinner sp_bFinalizado;
    private String[] arrayTiposbFina;

    //OVERRIDE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_element_por_tipo);

        JSONObject jsonObject = null;
        tit = (TextView) findViewById(R.id.tw_tipoElemento);
        idTipo = getIntent().getIntExtra("id_tipo",-1);
        nombreTipo = getIntent().getStringExtra("nombreTipo");
        tit.setText("Tipo: "+nombreTipo);
        try {
            jsonObject = GestorSharedPreferences.getJsonParte(GestorSharedPreferences.getSharedPreferencesParte(this));
            idParte = jsonObject.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        inicializarVariables();

    }
    public void onResume() {
        inicializarVariables();
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       /* try {

        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }
    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btnNuevoElementoTipo){

        }
    }

    //METODOS
    public void inicializarVariables()  {
        //BUTTON
        btnNuevoElem = (Button) findViewById(R.id.btnNuevoElementoTipo);
        btnNuevoElem.setVisibility(View.GONE);
        btnNuevoElem.setOnClickListener(this);
        //LISTVIEW
        LvElemTipo = (ListView) findViewById(R.id.lvElemInstTipo);

        etBusqueda = (EditText) findViewById(R.id.editTextBusqueda);
        sp_bFinalizado = (Spinner) findViewById(R.id.sp_bFinalizado);
        sp_bFinalizado.setOnItemSelectedListener(this);
        arrayTiposbFina  = new String[4];
        arrayTiposbFina[0] = "--Seleciones un valor--";
        arrayTiposbFina[1] = "Todos";
        arrayTiposbFina[2] = "Si";
        arrayTiposbFina[3] = "No";

        ArrayAdapter tiposbFina =new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayTiposbFina) ;
        sp_bFinalizado.setAdapter(tiposbFina);
        sp_bFinalizado.setSelection(3);
        etBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence c, int start, int before, int count) {
                try {
                    //List <ElementoInstalacion> listaSinFiltrar = buscarElementoInstalacionPorFkParteFkTipo(context,idParte,idTipo);
                    if(etBusqueda.getText().toString().equals("")){
                        int position = sp_bFinalizado.getSelectedItemPosition();
                        switch (position){
                            case 1:
                                elementosPorFinalizado( -1);
                                break;
                            case 2:
                                elementosPorFinalizado( 1);
                                break;
                            case 3:
                                elementosPorFinalizado( 0);
                                break;
                            default:
                                //
                                break;
                        }
                    }else{
                        if(arrayListElemInstTipo.size() != 0){
                            List <ElementoInstalacion> listaSinFiltrar =arrayListElemInstTipo;
                            List <ElementoInstalacion> listaFiltrada = ordenacionYbusqueda(listaSinFiltrar,busqueda(c));
                            if (listaFiltrada.size()==0){
                                etBusqueda.setTextColor(Color.rgb(220,0,0));
                            }else{
                                etBusqueda.setTextColor(Color.rgb(0,0,0));
                            }
                            añadirElemInstBusqueda(listaFiltrada);
                        }else{
                            int position = sp_bFinalizado.getSelectedItemPosition();
                            switch (position){
                                case 1:
                                    elementosPorFinalizado( -1);
                                    break;
                                case 2:
                                    elementosPorFinalizado( 1);
                                    break;
                                case 3:
                                    elementosPorFinalizado( 0);
                                    break;
                                default:
                                    //
                                    break;
                            }
                        }

                    }

                } catch (SQLException | JSONException throwables) {
                    throwables.printStackTrace();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    public String busqueda(CharSequence c){
        String busqueda = c.toString();
        return busqueda;
    }

    public  void elementosPorFinalizado( int bFinalizado) throws JSONException, SQLException {
        List <ElementoInstalacion> listAll= buscarElementoInstalacionPorFkParteFkTipo(this,idParte,idTipo);
        List<ElementoInstalacion> listaFiltrada =new ArrayList<ElementoInstalacion>();
        switch (bFinalizado){
            case -1 :
                //TODOS
                listaFiltrada=listAll;
                break;
            case 0://SIN FINALIZAR
                for(int i=0;i<listAll.size();i++){
                    if(!listAll.get(i).getbfinalizado()){
                        listaFiltrada.add(listAll.get(i));
                    }
                }

                break;
            case 1://FINALIZADOS
                for(int i=0;i<listAll.size();i++){
                    if(listAll.get(i).getbfinalizado()){
                        listaFiltrada.add(listAll.get(i));
                    }
                }

                break;
        }


        arrayListElemInstTipo.clear();
        try {

            List <ElementoInstalacion> listaOrdenada =  ordenacionYbusqueda( listaFiltrada,"");;

            if (listaOrdenada!=null) {
                arrayListElemInstTipo.addAll(listaOrdenada);
                adaptadorListaElementosTipo  = new AdaptadorListaElementosTipo(this, R.layout.camp_adapter_list_view_elementos_tipo, arrayListElemInstTipo,nombreTipo,this );
                LvElemTipo.setAdapter(adaptadorListaElementosTipo);
                LvElemTipo.setVisibility(View.VISIBLE);
            }else{ }
        }catch (SQLException | JSONException e) {
            e.printStackTrace();
        }

    }


    public  List<ElementoInstalacion> ordenacionYbusqueda(List<ElementoInstalacion> listaDesordenada, String str) throws JSONException, SQLException {

        if ((str.equals(""))||(str.equals(null))){
            if (listaDesordenada.size()>1){
                ArrayList<ElementoInstalacion> listaOrdenada = new ArrayList<ElementoInstalacion>();
                JSONArray UnOrderedArray = new JSONArray();

                for(int i=0;i<listaDesordenada.size();i++){
                    try {
                        UnOrderedArray.put(new JSONObject(listaDesordenada.get(i).getValoresgestnet()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                SORTJSONArray OrderArray = null;

                try {
                    String nombreOrden = TipoElementosDAO.buscarCampoOrdenporFkTipo(this,idTipo);
                    if(nombreOrden.equals("")){
                        return listaDesordenada;
                    }
                    OrderArray = new SORTJSONArray(this,UnOrderedArray,nombreOrden);

                    JSONArray OrderedArray = OrderArray.SorteredArray;

                    for (int i=0;i<OrderedArray.length();i++){
                        String nombreListaOrdenada = OrderedArray.getJSONObject(i).get(nombreOrden).toString();
                        boolean resultado = false;
                        int y = 0;
                        do {
                            String nombreListaDesordenada = new JSONObject(listaDesordenada.get(y).getValoresgestnet()).getString(nombreOrden);
                            if (nombreListaDesordenada.equals(nombreListaOrdenada)) {
                                listaOrdenada.add(listaDesordenada.get(y));
                                resultado = true;
                            }
                            y++;
                        }while (resultado == false);
                    }
                    return listaOrdenada;
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }finally {
                    return listaOrdenada;
                }
            }else{
                return listaDesordenada;
            }
        } else{
            List <String> listaFiltros = ElementoInstDAO.busquedaFiltros(this,idParte,idTipo,str);
            if (listaFiltros != null){
               //List <ElementoInstalacion> listaElementos = buscarElementoInstalacionPorFkParteFkTipo(context, idParte, idTipo);
               List <ElementoInstalacion> listaElementos = arrayListElemInstTipo;
                ArrayList <ElementoInstalacion> listaFiltrada = new ArrayList<>();
                boolean encontrado = false;
                Pattern pattern = Pattern.compile(str);
                for (int x=0;x<listaElementos.size();x++){
                    encontrado = false;
                    String str_json = listaElementos.get(x).getValores();
                    JSONArray jsonArray = new JSONArray(str_json);
                    for (int z=0;z<jsonArray.length();z++){
                        JSONObject jsonObj = jsonArray.getJSONObject(z);
                        for(int y=0;y<listaFiltros.size();y++){
                            String campo = jsonObj.getString("campo");
                            String filtro_0 = listaFiltros.get(y);
                            if (campo.equals(filtro_0)){
                                String valorCampo = jsonObj.getString("valor");
                                int find = valorCampo.toUpperCase().indexOf(str.toUpperCase());
                                if ((find!=-1)&&(encontrado==false)) {
                                    listaFiltrada.add(listaElementos.get(x));
                                    encontrado = true;
                                }
                            }
                        }
                    }

                }

                List <ElementoInstalacion> listaFiltradaOrdenada = ordenacionYbusqueda(listaFiltrada,"");
                return listaFiltradaOrdenada;
            }else{
                return listaDesordenada;
            }
        }
    }

    public  void añadirElemInst(){
            arrayListElemInstTipo.clear();
            try {
                List <ElementoInstalacion> a = buscarElementoInstalacionPorFkParteFkTipo(this,idParte,idTipo);
                List <ElementoInstalacion> listaOrdenada = ordenacionYbusqueda(a,"");

                if (listaOrdenada!=null) {
                    arrayListElemInstTipo.addAll(listaOrdenada);
                    adaptadorListaElementosTipo  = new AdaptadorListaElementosTipo(this, R.layout.camp_adapter_list_view_elementos_tipo, arrayListElemInstTipo,nombreTipo,this );
                    LvElemTipo.setAdapter(adaptadorListaElementosTipo);
                    LvElemTipo.setVisibility(View.VISIBLE);
                }else{ }
            }catch (SQLException | JSONException e) {
                e.printStackTrace();
            }
    }

    public  void añadirElemInstBusqueda(List <ElementoInstalacion> lista){
            arrayListElemInstTipo.clear();
            arrayListElemInstTipo.addAll(lista);
            adaptadorListaElementosTipo  = new AdaptadorListaElementosTipo(this, R.layout.camp_adapter_list_view_elementos_tipo, arrayListElemInstTipo,nombreTipo,this );
            LvElemTipo.setAdapter(adaptadorListaElementosTipo);
            LvElemTipo.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == sp_bFinalizado.getId()) {
            try {
                switch (position){
                    case 1:
                        elementosPorFinalizado( -1);
                        break;
                    case 2:
                         elementosPorFinalizado( 1);
                        break;
                    case 3:
                        elementosPorFinalizado( 0);
                        break;
                    default:
                      //
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}