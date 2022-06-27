package com.multimedia.aes.gestnet_spak.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.multimedia.aes.gestnet_spak.R;
import com.multimedia.aes.gestnet_spak.SharedPreferences.GestorSharedPreferences;
import com.multimedia.aes.gestnet_spak.adaptador.AdaptadorListaMaquinas;
import com.multimedia.aes.gestnet_spak.adaptador.AdaptadorListaElemeInst;
import com.multimedia.aes.gestnet_spak.dao.ParteDAO;
import com.multimedia.aes.gestnet_spak.dao.ElementoInstDAO;
import com.multimedia.aes.gestnet_spak.dialogo.Dialogo;
import com.multimedia.aes.gestnet_spak.entidades.Maquina;
import com.multimedia.aes.gestnet_spak.entidades.Parte;
import com.multimedia.aes.gestnet_spak.progressDialog.ManagerProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class TabFragment2_equipo extends Fragment {

    private View vista;
    private static ListView lvMaquinas;
    private static Parte parte = null;
    private static ArrayList<Maquina> arrayListMaquina = new ArrayList<>();
    private static ArrayList<String[]> arrayListElemInst = new ArrayList<>();
    private static AdaptadorListaElemeInst adaptadorListaElemInst ;
    private static AdaptadorListaMaquinas adat;
    private static Activity activity;
    public void sacarMensaje(String msg) {
        if (ManagerProgressDialog.getDialog()!=null){
            ManagerProgressDialog.cerrarDialog();
        }
        Dialogo.dialogoError(msg,getContext());
    }

    //METODOS
    public void inicializarVariables(){

        //LISTVIEW
        lvMaquinas = (ListView)vista.findViewById(R.id.lvMaquinas);
        activity = getActivity();
    }
    public static void añadirElemInst(){

            arrayListElemInst.clear();
            try {
                List <String[]> a = ElementoInstDAO.buscarTiposElementosParte(activity,parte.getId_parte());
                if (a!=null) {
                    arrayListElemInst.addAll(a);
                    adaptadorListaElemInst  = new AdaptadorListaElemeInst(activity, R.layout.camp_adapter_list_view_elementos_instalacion, arrayListElemInst, activity );
                    lvMaquinas.setAdapter(adaptadorListaElemInst);
                    lvMaquinas.setVisibility(View.VISIBLE);

                }else{
                    /*adaptadorListaMaquinas  = new AdaptadorListaMaquinas(activity, R.layout.camp_adapter_list_view_maquinas, arrayListElemInstarrayListElemInst, activity);
                    lvMaquinas.setAdapter(adaptadorListaMaquinas);
                    lvMaquinas.setVisibility(View.VISIBLE);*/
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

    }
    //OVERRIDE
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.tab_fragment2_equipo, container, false);
        JSONObject jsonObject = null;
        int idParte = 0;
        try {
            jsonObject = GestorSharedPreferences.getJsonParte(GestorSharedPreferences.getSharedPreferencesParte(getContext()));
            idParte = jsonObject.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            parte = ParteDAO.buscarPartePorId(getContext(), idParte);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        inicializarVariables();
        arrayListElemInst.clear();
        añadirElemInst();

        //añadirMaquina();
        return vista;
    }


}