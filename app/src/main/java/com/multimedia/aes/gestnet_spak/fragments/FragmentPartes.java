package com.multimedia.aes.gestnet_spak.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.multimedia.aes.gestnet_spak.R;
import com.multimedia.aes.gestnet_spak.SharedPreferences.GestorSharedPreferences;
import com.multimedia.aes.gestnet_spak.adaptador.PageAdapter;
import com.multimedia.aes.gestnet_spak.dao.ParteDAO;
import com.multimedia.aes.gestnet_spak.entidades.Parte;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

public class FragmentPartes extends Fragment implements View.OnClickListener {
    private View vista;
    private TabFragment1_cliente tab1;
    private TabFragment2_equipo tab2;
    private TabFragment3_operaciones tab3;
    private TabFragment4_finalizacion tab4;
    private TabFragment5_documentacion tab5;
    private TabFragment6_materiales tab6;

    private Parte parte;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.settings_main, container, false);
        TabLayout tabLayout = (TabLayout) vista.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Cliente"));
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
       if (parte.getEstado_android()!=0 && parte.getEstado_android()!=3 && parte.getEstadoEjecucion() != 2){
               //0: asignado (rojo) // 1: iniciado (ambar) // 2: falta material (azul) // 3: finalizado (verde)
               tabLayout.addTab(tabLayout.newTab().setText("Elementos Inst."));
               //tabLayout.addTab(tabLayout.newTab().setText("Operaciones"));
               tabLayout.addTab(tabLayout.newTab().setText("Materiales"));
               tabLayout.addTab(tabLayout.newTab().setText("Finalización"));

        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        final ViewPager viewPager = (ViewPager) vista.findViewById(R.id.pager);
        final PageAdapter adapter = new PageAdapter
                (getFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener (new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tab1 = adapter.getTab1();
                tab2 = adapter.getTab2();
                //tab3 = adapter.getTab3();
                tab4 = adapter.getTab4();
                tab6 = adapter.getTab6();
                if (tab.getPosition()==0){

                }else if (tab.getPosition()==1){

                }else if (tab.getPosition()==2){

                }/*else if (tab.getPosition()==3){

                }*/else if (tab.getPosition()==4){
                    tab4.recalcular();
                }else if (tab.getPosition()==5){


                }
            }

            @Override
            public void onTabUnselected(final TabLayout.Tab tab) {
                if (tab.getPosition()==0){

                }else if (tab.getPosition()==1){

                }else if (tab.getPosition()==2){
                    /*try {
                        tab3.guardarProtocolo();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }*/
                }/*else if (tab.getPosition()==3){

                }*/else if (tab.getPosition()==4){

                }else if (tab.getPosition()==6){

                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        return vista;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onClick(View view) {
    }

}
