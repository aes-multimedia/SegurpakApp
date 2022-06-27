package com.multimedia.aes.gestnet_spak.nucleo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.multimedia.aes.gestnet_spak.BBDD.GuardarParte;
import com.multimedia.aes.gestnet_spak.R;
import com.multimedia.aes.gestnet_spak.SharedPreferences.GestorSharedPreferences;
import com.multimedia.aes.gestnet_spak.Utils.Utils;
import com.multimedia.aes.gestnet_spak.adaptador.AdaptadorPartes;
import com.multimedia.aes.gestnet_spak.constantes.BBDDConstantes;
import com.multimedia.aes.gestnet_spak.dao.ArticuloDAO;
import com.multimedia.aes.gestnet_spak.dao.ClienteDAO;
import com.multimedia.aes.gestnet_spak.dao.ConfiguracionDAO;
import com.multimedia.aes.gestnet_spak.dao.DatosAdicionalesDAO;
import com.multimedia.aes.gestnet_spak.dao.EnvioDAO;
import com.multimedia.aes.gestnet_spak.dao.ParteDAO;
import com.multimedia.aes.gestnet_spak.dao.UsuarioDAO;
import com.multimedia.aes.gestnet_spak.dialogo.DialogoBuscarArticulo;
import com.multimedia.aes.gestnet_spak.dialogo.DialogoKilometros;
import com.multimedia.aes.gestnet_spak.entidades.Cliente;
import com.multimedia.aes.gestnet_spak.entidades.Configuracion;
import com.multimedia.aes.gestnet_spak.entidades.DatosAdicionales;
import com.multimedia.aes.gestnet_spak.entidades.Envio;
import com.multimedia.aes.gestnet_spak.entidades.Parte;
import com.multimedia.aes.gestnet_spak.entidades.Usuario;
import com.multimedia.aes.gestnet_spak.fragments.FragmentImpresion;
import com.multimedia.aes.gestnet_spak.fragments.FragmentPartes;
import com.multimedia.aes.gestnet_spak.hilos.HiloActPartePendiente;
import com.multimedia.aes.gestnet_spak.hilos.HiloIniciarParte;
import com.multimedia.aes.gestnet_spak.hilos.HiloNoEnviados;
import com.multimedia.aes.gestnet_spak.hilos.HiloPartes;
import com.multimedia.aes.gestnet_spak.hilos.HiloPorFecha;
import com.multimedia.aes.gestnet_spak.servicios.ServicioArticulos;
import com.multimedia.aes.gestnet_spak.servicios.ServicioLocalizacion;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
public class Index extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private ListView lvIndex;
    private SwipeRefreshLayout srl;
    private ImageView ivIncidencias;
    private LinearLayout cuerpo;
    private AdaptadorPartes adaptadorPartes;
    private ArrayList<Parte> arrayListParte = new ArrayList<>();
    private String fecha;
    private Usuario u;
    private Cliente c;
    private int tipoAviso;
    private static Utils ut;

    //METODO
    private void inicializarVariables() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        srl = findViewById(R.id.lllistview);
        srl.setOnRefreshListener(this);
        lvIndex = findViewById(R.id.lvIndex);
        lvIndex.setOnItemClickListener(this);
        lvIndex.setOnItemLongClickListener(this);
        ivIncidencias = navigationView.getHeaderView(0).findViewById(R.id.ivIncidencias);
        ivIncidencias.setOnClickListener(this);
        cuerpo = findViewById(R.id.cuerpo);

    }

    public void sacarMensaje(String msg) {
        srl.setRefreshing(false);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(Html.fromHtml(msg,Html.FROM_HTML_MODE_LEGACY));
        builder1.setCancelable(true);
        builder1.setPositiveButton(
            "Aceptar",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    finish();
                }
            });
        AlertDialog alert11 = builder1.create();
        alert11.setCanceledOnTouchOutside(false);
        alert11.setCanceledOnTouchOutside(false);
        alert11.show();

    }

    public void guardarPartes(String msg) {
        try {
            JSONObject jsonObject = new JSONObject(msg);
            if (jsonObject.getInt("estado") == 1 || jsonObject.getInt("estado") == 272) {
                new GuardarParte(this, msg,tipoAviso).execute();
            } else {
                sacarMensaje(jsonObject.getString("mensaje"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void cambiaListado(int tipo) {
        Intent intent = new Intent(this, Index.class);
        intent.putExtra("tipoAviso",tipo);
        getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getIntent().removeExtra("metodo");
        getIntent().removeExtra("notId");
        getIntent().removeExtra("tipoAviso");
        startActivity(intent);
        finish();
        //recreate();
    }
    public void datosActualizados() {
        Intent intent = new Intent(this, Index.class);
        intent.putExtra("tipoAviso",tipoAviso);
        getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getIntent().removeExtra("metodo");
        getIntent().removeExtra("notId");
        getIntent().removeExtra("tipoAviso");
        startActivity(intent);
        finish();
        //recreate();
    }

    public void impresion() {
        Class fragmentClass = FragmentImpresion.class;
        Fragment fragment;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.cuerpo, fragment).commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private void actualizarFecha(String fecha) {


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.index);
        inicializarVariables();
        fecha = ut.getDateTimeEsp();
        JSONObject jo;
       // String dia=ut.getDateTimeEsp();
        String dia="";
        try {
            jo = GestorSharedPreferences.getJsonDia(GestorSharedPreferences.getSharedPreferencesDia(this));
            if(jo.has("dia")){
                dia = jo.getString("dia");
                if(dia.contains("/")){
                    dia = dia.replace("/","-");
                }
                //dia = ut.formatDateTimeSharedToEsp(dia);
                boolean isValid = ut.isSpfT(dia);
                if(dia != "" && isValid){
                    fecha = dia;
                }
            }
        } catch (JSONException  e) {
            e.printStackTrace();
        }
        setTitle("Avisos" + " " + fecha);
        Intent i = this.getIntent();
        tipoAviso = i.getIntExtra("tipoAviso",2);
        try {
            u = UsuarioDAO.buscarUsuario(this);
            c = ClienteDAO.buscarCliente(this);
            if (ArticuloDAO.buscarTodosLosArticulos(this) == null) {
                startService(new Intent(this, ServicioArticulos.class));
            } else {
                startService(new Intent(this, ServicioLocalizacion.class));
            }
            arrayListParte.clear();

            if(tipoAviso==1){
                List<Parte> partes = ParteDAO.buscarTodosLosPartes(this);
                if (partes != null) {
                    arrayListParte.addAll(partes);
                }
                setTitle("Bolsa de Avisos");
            }else{
                String fechaHilo = "";
                boolean isFtSp = ut.isSpfT(fecha);
               if(isFtSp){
                   fechaHilo = ut.formatDateTimeEn(fecha);
               }else{
                   if(ut.isEnfT(fecha)){
                       fechaHilo = fecha;
                   }else{

                   }
               }
                List<Parte> partes = ParteDAO.buscarTodosLosPartesPorFecha(this,fechaHilo);
                if ( partes != null) {
                    arrayListParte.addAll(partes);
                }
                //setTitle("Avisos" + " " + fecha);
            }



        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }


            adaptadorPartes = new AdaptadorPartes(this, R.layout.camp_adapter_list_view_parte, arrayListParte);
            lvIndex.setAdapter(adaptadorPartes);

      /*  Intent intent = getIntent();
        if (intent != null) {

            int metodo = intent.getIntExtra("metodo", 0);
            int notId = intent.getIntExtra("notiId", 0);
            int id = intent.getIntExtra("id", 0);
            switch (metodo) {
                case 1:
                    break;
                case 2:
                    try {
                        if (ParteDAO.buscarPartePorId(this, id) != null) {
                            arrayListParte.clear();
                            int direccion = ParteDAO.buscarPartePorId(this, id).getFk_direccion();
                            ProtocoloAccionDAO.borrarProtocoloPorFkParte(this, id);
                            DatosAdicionalesDAO.borrarDatosAdicionalesPorFkParte(this, id);
                            MaquinaDAO.borrarMaquinaPorFkDireccion(this, direccion);
                            ParteDAO.borrarPartePorID(this, id);
                            if (ParteDAO.buscarTodosLosPartes(this) != null) {
                                arrayListParte.addAll(ParteDAO.buscarTodosLosPartes(this));
                            }
                            adaptadorPartes = new AdaptadorPartes(this, R.layout.camp_adapter_list_view_parte, arrayListParte);
                            lvIndex.setAdapter(adaptadorPartes);
                            Dialogo.dialogoError("Un Parte de Intervención ha sido eliminado de su ruta diaria", this);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    new HiloPartesId(this, u.getFk_entidad(), id, c.getIp_cliente(), u.getApi_key()).execute();
                    break;
                case 4:
                    try {
                        arrayListParte.clear();
                        ProtocoloAccionDAO.borrarTodosLosProtocolo(this);
                        DatosAdicionalesDAO.borrarTodosLosDatosAdicionales(this);
                        MaquinaDAO.borrarTodasLasMaquinas(this);
                        ParteDAO.borrarTodosLosPartes(this);
                        adaptadorPartes = new AdaptadorPartes(this, R.layout.camp_adapter_list_view_parte, arrayListParte);
                        lvIndex.setAdapter(adaptadorPartes);
                        Dialogo.dialogoError("ruta borrada", this);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;


            }
            if (notId != 0) {
                GcmIntentService.cerrarNotificacion(notId);
            }
        }*/

        try {
            Configuracion co = ConfiguracionDAO.buscarTodasLasConfiguraciones(this).get(0);
            if (ConfiguracionDAO.buscarTodasLasConfiguraciones(this) != null && ConfiguracionDAO.buscarTodasLasConfiguraciones(this).get(0).isKms_finalizacion()) {
                JSONObject kil = new JSONObject();
                try {
                    kil = GestorSharedPreferences.getJsonKilometros(GestorSharedPreferences.getSharedPreferencesKilometros(this));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (kil == null || kil.toString().equals("{}")) {
                    DialogoKilometros dialog = new DialogoKilometros().newInstance(u.getFk_entidad());
                    android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dialog.setCancelable(false);
                    dialog.show(ft, "DialogoKilometros");
                } else {
                    try {
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String strDate = sdf.format(c.getTime());
                        if (!kil.getString("fecha").equals(strDate)) {
                            DialogoKilometros dialog = new DialogoKilometros().newInstance(0);
                            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                            dialog.setCancelable(false);
                            dialog.show(ft, "DialogoKilometros");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //recreate();
            finish();
            startActivity(getIntent());
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.averias) {
            //recreate();
            /*finish();
            Intent i = getIntent();
            i.putExtra("tipoAviso",1);
            startActivity(getIntent());*/
            Intent i = new Intent(this, Index.class);
            i.putExtra("tipoAviso",1);
            startActivity(i);
            finish();
        }else if (id == R.id.averiasFecha) {
            Intent i = new Intent(this, Index.class);
            i.putExtra("tipoAviso",2);
            startActivity(i);
            finish();
        }else if (id == R.id.mi_firma) {
            Intent i = new Intent(this, MiFirma.class);
            startActivity(i);
        } else if (id == R.id.cierre_dia) {
            Intent i = new Intent(this, CierreDia.class);
            startActivity(i);
        } else if (id == R.id.aviso_guardia) {
            String ip = c.getIp_cliente();
            String fk_tecnico = u.getFk_entidad() + "";
            String url = "http://" + ip + "/webservices/webview/avisoGuardia.php?fk_tecnico=" + fk_tecnico;
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (id == R.id.cambiar_fecha) {
            try {
                List<Parte> partes = ParteDAO.buscarTodosLosPartes(this);

                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        String day = selectedday + "";
                        int monthReal = selectedmonth + 1;
                        String month = monthReal + "";
                        if (selectedday < 10) {
                            day = "0" + selectedday;
                        }
                        if (monthReal < 10) {
                            //Los meses en android van de 0 a 11
                            month = "0" + monthReal;
                        }
                        String year = selectedyear + "";
                        JSONObject js = new JSONObject();
                        try {
                            js.put("dia", day + "-" + month + "-" + year);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        GestorSharedPreferences.setJsonDia(GestorSharedPreferences.getSharedPreferencesDia(Index.this), js);
                        try {
                            BBDDConstantes.borrarDatosTablasPorDia(Index.this);
                            GestorSharedPreferences.clearSharedPreferencesParte(Index.this);
                            String fecha = year + "-" + month + "-" + day;
                            new HiloPorFecha(Index.this, u.getFk_entidad(), fecha, c.getIp_cliente()).execute();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select Date");
                mDatePicker.show();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }else if (id == R.id.cierres_pendientes) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("¿Se enviaran todos los cambios pendientes, desea continuar?");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                "Enviar cambios.",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            if (EnvioDAO.buscarTodosLosEnvios(Index.this) != null) {
                                List<Envio> envios = EnvioDAO.buscarTodosLosEnvios(Index.this);
                                for (Envio envio : envios) {
                                    new HiloNoEnviados(Index.this, envio.getId_envio()).execute();
                                }
                            } else {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(Index.this);
                                builder1.setMessage("No hay Cambios pendientes.");
                                builder1.setCancelable(true);
                                builder1.setPositiveButton(
                                    "Aceptar",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                                AlertDialog alert11 = builder1.create();
                                alert11.setCanceledOnTouchOutside(false);
                                alert11.show();
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        dialog.cancel();
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
        else if(id == R.id.actualizarPartes){
            Calendar calendar = Calendar.getInstance();
            int d = calendar.get(Calendar.DAY_OF_MONTH);
            int m = calendar.get(Calendar.MONTH) + 1;
            int y = calendar.get(Calendar.YEAR);
            String diaFecha = "";
            if(d < 10){
                diaFecha = "0"+d;
            }
            String mesFecha = "";
            if(m < 10){
                mesFecha = "0"+m;
            }
            String fechar = String.valueOf(diaFecha + "-" + mesFecha + "-" + y);

            List<Envio> envios = null;

        try {
            envios = EnvioDAO.buscarTodosLosEnvios(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }

            if (envios != null && envios.size() > 0) {

            new AlertDialog.Builder(this).setMessage("Por favor, antes de continuar envie los cierres pendientes.")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface hi, int dd) {

                            srl.setRefreshing(false);
                        }
                    }
                ).show();

            } else {


            JSONObject js = new JSONObject();
            try {
                js.put("dia", fechar);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            GestorSharedPreferences.setJsonDia(GestorSharedPreferences.getSharedPreferencesDia(Index.this), js);
            try {
                BBDDConstantes.borrarDatosTablasPorDia(this);
                srl.setRefreshing(true);
                new HiloPartes(this, u.getFk_entidad(), c.getIp_cliente(), u.getApi_key()).execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            }
        }
        /*else if (id == R.id.actualizar_stock) {
            int fkEntidad = u.getFk_entidad();
            try {
                new HiloActualizarStock(this, fkEntidad).execute();
            } catch (Exception e) {

                ProgressDialog dialog;
                dialog = new ProgressDialog(this);
                dialog.setTitle("Error al actualizar almacén");
                dialog.setMessage("Conectando con el servidor, porfavor espere..." + "\n" + "Esto puede tardar unos minutos si la cobertura es baja.");
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setIndeterminate(true);
                dialog.show();
            }

        }*/ else if (id == R.id.buscar_articulos) {
            DialogoBuscarArticulo dialog = new DialogoBuscarArticulo();
            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
            dialog.setCancelable(false);
            dialog.show(ft, "DialogoBuscarArticulo");
        } else if (id == R.id.cerrar_sesion) {
            try {
                stopService(new Intent(this, ServicioLocalizacion.class));
                BBDDConstantes.borrarDatosTablas(this);
                Intent i = new Intent(this, Login.class);
                startActivity(i);
                finish();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, @NonNull View view , int i, long l) {
        if(tipoAviso == 2){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", view.getTag());
                GestorSharedPreferences.clearSharedPreferencesParte(this);
                GestorSharedPreferences.setJsonParte(GestorSharedPreferences.getSharedPreferencesParte(this), jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            srl.setVisibility(View.GONE);
            Parte parte = null;
            try {
                parte = ParteDAO.buscarPartePorId(this, Integer.parseInt(view.getTag().toString()));
                if (parte.getFk_tipo() == 4 && ClienteDAO.buscarCliente(this).getId_cliente() == 30) {
                    iniciarParte(parte);
                    Cliente cliente = null;
                    try {
                        cliente = ClienteDAO.buscarCliente(this);
                        String url = "http://" + cliente.getIp_cliente() + "/webservices/webview/trabajos_obra.php?fk_parte=" + parte.getId_parte();
                        Uri uri = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } else {
                    Class fragmentClass = FragmentPartes.class;
                    Fragment fragment;
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.cuerpo, fragment).commit();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            Context context = this;
            Parte parte = null;
            try {

                parte = ParteDAO.buscarPartePorId(context, Integer.parseInt(view.getTag().toString()));

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            final int id_parte = parte.getId_parte();
            String dir = "";
            if (!parte.getTipo_via().trim().equals("")&&!parte.getTipo_via().trim().equals("false")&&!parte.getTipo_via().trim().equals("null")){
                dir+=parte.getTipo_via()+" ";
            }
            if (!parte.getVia().trim().equals("")&&!parte.getVia().trim().equals("null")){
                dir+=parte.getVia()+" ";
            }
            if (!parte.getNumero_direccion().trim().equals("")&&!parte.getNumero_direccion().trim().equals("null")){
                dir+="Nº "+parte.getNumero_direccion()+" ";
            }
            if (!parte.getEscalera_direccion().trim().equals("")&&!parte.getEscalera_direccion().trim().equals("null")){
                dir+="Esc. "+parte.getEscalera_direccion()+" ";
            }
            if (!parte.getPiso_direccion().trim().equals("")&&!parte.getPiso_direccion().trim().equals("null")){
                dir+="Piso "+parte.getPiso_direccion()+" ";
            }
            if (!parte.getPuerta_direccion().trim().equals("")&&!parte.getPuerta_direccion().trim().equals("null")){
                dir+=parte.getPuerta_direccion()+" ";
            }
            if (!parte.getCp_direccion().trim().equals("")&&!parte.getCp_direccion().trim().equals("null")){
                dir+= parte.getCp_direccion()+"-";
            }
            if (!parte.getMunicipio_direccion().trim().equals("")&&!parte.getMunicipio_direccion().trim().equals("null")){
                dir+=parte.getMunicipio_direccion()+"-";
            }
            if (!parte.getProvincia_direccion().trim().equals("")&&!parte.getProvincia_direccion().trim().equals("null")){
                dir+=parte.getProvincia_direccion();
            }
            final String direccion = dir;
            final String cliente = parte.getNombre_cliente();


            AlertDialog.Builder pasarPendiente = new AlertDialog.Builder(this);
            pasarPendiente.setMessage(Html.fromHtml("Aviso:<br><b>"+cliente+"<br>"+direccion+"</b><br><br>Va a pasar el Aviso al listado de pendientes para el día de hoy.<br><br><big>¿Esta seguro?</big>",Html.FROM_HTML_MODE_LEGACY));
            pasarPendiente.setCancelable(true);
            pasarPendiente.setPositiveButton(
                    "Aceptar Cambio",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                         try {


                                new HiloActPartePendiente(context,u.getFk_entidad(),fecha,c.getIp_cliente(),id_parte,1).execute();

                            }catch (Exception e){

                            }
                            dialog.cancel();
                        }
                    });
            pasarPendiente.setNegativeButton(
                    "Cancelar",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertPasarPendiente = pasarPendiente.create();
            alertPasarPendiente.setCanceledOnTouchOutside(false);
            alertPasarPendiente.show();

        }



    }
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Context context = this;
        Parte parte = null;
        try {
            parte = ParteDAO.buscarPartePorId(context, Integer.parseInt(view.getTag().toString()));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        final int id_parte = parte.getId_parte();
        String dir = "";
        if (!parte.getTipo_via().trim().equals("")&&!parte.getTipo_via().trim().equals("false")&&!parte.getTipo_via().trim().equals("null")){
            dir+=parte.getTipo_via()+" ";
        }
        if (!parte.getVia().trim().equals("")&&!parte.getVia().trim().equals("null")){
            dir+=parte.getVia()+" ";
        }
        if (!parte.getNumero_direccion().trim().equals("")&&!parte.getNumero_direccion().trim().equals("null")){
            dir+="Nº "+parte.getNumero_direccion()+" ";
        }
        if (!parte.getEscalera_direccion().trim().equals("")&&!parte.getEscalera_direccion().trim().equals("null")){
            dir+="Esc. "+parte.getEscalera_direccion()+" ";
        }
        if (!parte.getPiso_direccion().trim().equals("")&&!parte.getPiso_direccion().trim().equals("null")){
            dir+="Piso "+parte.getPiso_direccion()+" ";
        }
        if (!parte.getPuerta_direccion().trim().equals("")&&!parte.getPuerta_direccion().trim().equals("null")){
            dir+=parte.getPuerta_direccion()+" ";
        }
        if (!parte.getCp_direccion().trim().equals("")&&!parte.getCp_direccion().trim().equals("null")){
            dir+= parte.getCp_direccion()+"-";
        }
        if (!parte.getMunicipio_direccion().trim().equals("")&&!parte.getMunicipio_direccion().trim().equals("null")){
            dir+=parte.getMunicipio_direccion()+"-";
        }
        if (!parte.getProvincia_direccion().trim().equals("")&&!parte.getProvincia_direccion().trim().equals("null")){
            dir+=parte.getProvincia_direccion();
        }
        final String direccion = dir;
        final String cliente = parte.getNombre_cliente();


        AlertDialog.Builder pasarPendiente = new AlertDialog.Builder(this);
        pasarPendiente.setMessage(Html.fromHtml("Aviso:<br><b>"+cliente+"<br>"+direccion+"</b><br><br>Va a pasar el Aviso a la bolsa de avisos sin fecha de realización.<br><br><big>¿Esta seguro?</big>",Html.FROM_HTML_MODE_LEGACY));
        pasarPendiente.setCancelable(true);
        pasarPendiente.setPositiveButton(
                "Aceptar Cambio",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {


                            new HiloActPartePendiente(context,u.getFk_entidad(),fecha,c.getIp_cliente(),id_parte,2).execute();

                        }catch (Exception e){

                        }
                        dialog.cancel();
                    }
                });
        pasarPendiente.setNegativeButton(
                "Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertPasarPendiente = pasarPendiente.create();
        alertPasarPendiente.setCanceledOnTouchOutside(false);
        alertPasarPendiente.show();
        return true;
    }
    private void iniciarParte(@NonNull Parte parte) throws SQLException {
        if (parte.getEstado_android() == 0) {
            DatosAdicionales datos = DatosAdicionalesDAO.buscarDatosAdicionalesPorFkParte(this, parte.getId_parte());
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            String formattedDate = df.format(c.getTime());
            datos.setMatem_hora_entrada(formattedDate);
            try {
                DatosAdicionalesDAO.actualizarHoraEntrada(this, datos.getId_rel(), formattedDate);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            new HiloIniciarParte(this, parte, 1, 2,null).execute();
        }
    }

    @Override
    public void onRefresh() {
        Calendar calendar = Calendar.getInstance();
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        int m = calendar.get(Calendar.MONTH) + 1;
        int y = calendar.get(Calendar.YEAR);
        String diaFecha = "";
        if(d < 10){
            diaFecha = "0"+d;
        }
        String mesFecha = "";
        if(m < 10){
            mesFecha = "0"+m;
        }
        String fechar = String.valueOf(diaFecha + "-" + mesFecha + "-" + y);


        List<Envio> envios = null;

        srl.setRefreshing(false);
       /*
        try {
            envios = EnvioDAO.buscarTodosLosEnvios(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
*/
        if (envios != null && envios.size() > 0) {

            new AlertDialog.Builder(this).setMessage("Por favor, antes de continuar envie los cierres pendientes.")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface hi, int dd) {

                            srl.setRefreshing(false);
                        }
                    }
                ).show();

        } else {


            JSONObject js = new JSONObject();
            try {
                js.put("dia", fechar);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            GestorSharedPreferences.setJsonDia(GestorSharedPreferences.getSharedPreferencesDia(Index.this), js);
            try {
                //BBDDConstantes.borrarDatosTablasPorDia(this);
                srl.setRefreshing(true);
                if(tipoAviso == 1){
                    new HiloPartes(this, u.getFk_entidad(), c.getIp_cliente(), u.getApi_key()).execute();
                }else{
                    new HiloPorFecha(this, u.getFk_entidad(),fecha, c.getIp_cliente()).execute();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    @Override
    public void onClick(View v) {


    }


}