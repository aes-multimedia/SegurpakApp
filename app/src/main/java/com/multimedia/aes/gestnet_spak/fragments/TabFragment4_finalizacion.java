package com.multimedia.aes.gestnet_spak.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import com.multimedia.aes.gestnet_spak.R;
import com.multimedia.aes.gestnet_spak.SharedPreferences.GestorSharedPreferences;
import com.multimedia.aes.gestnet_spak.Utils.Utils;
import com.multimedia.aes.gestnet_spak.constantes.Constantes;
import com.multimedia.aes.gestnet_spak.dao.ClienteDAO;
import com.multimedia.aes.gestnet_spak.dao.ArticuloDAO;
import com.multimedia.aes.gestnet_spak.dao.ArticuloParteDAO;
import com.multimedia.aes.gestnet_spak.dao.DatosAdicionalesDAO;
import com.multimedia.aes.gestnet_spak.dao.DisposicionesDAO;
import com.multimedia.aes.gestnet_spak.dao.FormasPagoDAO;
import com.multimedia.aes.gestnet_spak.dao.HorasParteDAO;
import com.multimedia.aes.gestnet_spak.dao.ManoObraDAO;
import com.multimedia.aes.gestnet_spak.dao.ParteDAO;
import com.multimedia.aes.gestnet_spak.dao.TiposOsDAO;
import com.multimedia.aes.gestnet_spak.dao.UsuarioDAO;
import com.multimedia.aes.gestnet_spak.dialogo.Dialogo;
import com.multimedia.aes.gestnet_spak.entidades.Articulo;
import com.multimedia.aes.gestnet_spak.entidades.ArticuloParte;
import com.multimedia.aes.gestnet_spak.entidades.Cliente;
import com.multimedia.aes.gestnet_spak.entidades.DatosAdicionales;
import com.multimedia.aes.gestnet_spak.entidades.Disposiciones;
import com.multimedia.aes.gestnet_spak.entidades.FormasPago;
import com.multimedia.aes.gestnet_spak.entidades.HorasParte;
import com.multimedia.aes.gestnet_spak.entidades.ManoObra;
import com.multimedia.aes.gestnet_spak.entidades.Parte;
import com.multimedia.aes.gestnet_spak.entidades.TiposOs;
import com.multimedia.aes.gestnet_spak.entidades.Usuario;
import com.multimedia.aes.gestnet_spak.hilos.HiloCerrarParte;
import com.multimedia.aes.gestnet_spak.hilos.HiloHorasParte;
import com.multimedia.aes.gestnet_spak.nucleo.FirmaCliente;
import com.multimedia.aes.gestnet_spak.nucleo.GestionMateriales;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static java.lang.Math.min;
import static java.lang.Math.round;


public class TabFragment4_finalizacion extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener, View.OnFocusChangeListener {
    private View vista;
    private double preeu_mano_de_obra_horas;
    private String tiempoDuracion;
    private ArrayList<FormasPago> formasPagos = new ArrayList<>();
    private ArrayList<ManoObra> manosObra = new ArrayList<>();
    private ArrayList<TiposOs> tiposOs = new ArrayList<>();
    private ArrayList<Disposiciones> disposicionesServicio = new ArrayList<>();
    private String[] arrayFormasPago, arrayManosObra, arrayDisposiciones, arrayTiposOs;
    private Parte parte = null;
    private Usuario usuario = null;
    private DatosAdicionales datos = null;

    private EditText etOperacionEfectuada, et_preeu_materiales, et_preeu_mano_de_obra_horas,
            et_preeu_puesta_marcha, et_preeu_servicio_urgencia, et_preeu_km, et_preeu_km_precio,
            et_preeu_analisis_combustion, et_preeu_otros_nombre,
            et_preeu_adicional, etSubTotal, et_preeu_iva_aplicado, et_total, et_total_ppto, et_preeu_total_mano_de_obra_horas, et_preeu_total_disposicion_servicio, et_enviar_por_correo;

    private boolean acepta_presupuesto = false, enviar_correo = false;
    private static DecimalFormat df2 = new DecimalFormat(",##");
    private Button btnFinalizar, btn_preeu_mano_de_obra, btnFirmar, btn_calcular_tiempo;
    private Spinner sp_preeu_disposicion_servicio, sp_preeu_mano_de_obra_precio, spFormaPago,spTiposOS;
    private CheckBox cb_acepta_presupuesto, cb_enviar_por_correo;
    private View llTiposOs;
    private double precioTotalArticulos, preeu_adicional, preeu_analisis_combustion,
            preeu_km_precio_total, preeu_materiales, preeu_total_mano_de_obra_horas, preeu_disposicion_servicio, preeu_puesta_marcha, preeu_servicio_urgencia, preeu_km, preeu_km_precio, preeu_mano_de_obra_precio;

    private String textoBoton;
    private int forma_pago,tipoOsId;


    private Button BtnVerMateriales;
    private Context context;

    //METODO
    private void inicializar() {
        //EDITTEXT
        et_preeu_materiales = vista.findViewById(R.id.et_preeu_materiales);
        et_preeu_materiales.setEnabled(false);
        et_preeu_materiales.setText(String.valueOf(0));
        et_preeu_total_mano_de_obra_horas = vista.findViewById(R.id.et_preeu_total_mano_de_obra_horas);
        et_preeu_total_mano_de_obra_horas.setEnabled(false);
        et_preeu_puesta_marcha = vista.findViewById(R.id.et_preeu_puesta_marcha);
        et_preeu_puesta_marcha.addTextChangedListener(watcher);
        et_preeu_servicio_urgencia = vista.findViewById(R.id.et_preeu_servicio_urgencia);
        et_preeu_servicio_urgencia.addTextChangedListener(watcher);
        et_preeu_km = vista.findViewById(R.id.et_preeu_km);
        et_preeu_km.addTextChangedListener(watcher);
        et_preeu_km_precio = vista.findViewById(R.id.et_preeu_km_precio);
        et_preeu_km_precio.addTextChangedListener(watcher);
        et_preeu_analisis_combustion = vista.findViewById(R.id.et_preeu_analisis_combustion);
        et_preeu_analisis_combustion.addTextChangedListener(watcher);
        et_preeu_otros_nombre = vista.findViewById(R.id.et_preeu_otros_nombre);
        et_preeu_otros_nombre.addTextChangedListener(watcher);
        et_preeu_total_disposicion_servicio = vista.findViewById(R.id.et_preeu_total_disposicion_servicio);
        et_preeu_total_disposicion_servicio.setEnabled(false);
        et_preeu_adicional = vista.findViewById(R.id.et_preeu_adicional);
        et_preeu_adicional.addTextChangedListener(watcher);
        etSubTotal = vista.findViewById(R.id.etSubTotal);
        et_preeu_iva_aplicado = vista.findViewById(R.id.et_preeu_iva_aplicado);
        et_total = vista.findViewById(R.id.et_total);
        cb_acepta_presupuesto = vista.findViewById(R.id.cb_acepta_presupuesto);
        cb_acepta_presupuesto.setOnCheckedChangeListener(this);
        cb_enviar_por_correo = vista.findViewById(R.id.cb_enviar_por_correo);
        cb_enviar_por_correo.setOnCheckedChangeListener(this);
        etOperacionEfectuada = vista.findViewById(R.id.etOperacionEfectuada);
        etOperacionEfectuada.setOnFocusChangeListener(this);
        et_enviar_por_correo = vista.findViewById(R.id.et_enviar_por_correo);

        //BUTTON
        btn_preeu_mano_de_obra = vista.findViewById(R.id.btn_preeu_mano_de_obra);
        btnFinalizar = vista.findViewById(R.id.btnFinalizar);
        btnFirmar = vista.findViewById(R.id.btnFirmar);
        btn_calcular_tiempo = vista.findViewById(R.id.btn_calcular_tiempo);


        //ONCLICK
        btn_preeu_mano_de_obra.setOnClickListener(this);
        btnFinalizar.setOnClickListener(this);
        btnFirmar.setOnClickListener(this);
        btn_calcular_tiempo.setOnClickListener(this);
        //SPINNER
        llTiposOs = vista.findViewById(R.id.ll_tiposOS);
        spTiposOS = vista.findViewById(R.id.sp_tipos_os);
        spTiposOS.setOnItemSelectedListener(this);
        spFormaPago = vista.findViewById(R.id.spFormaPago);
        spFormaPago.setOnItemSelectedListener(this);
        sp_preeu_disposicion_servicio = vista.findViewById(R.id.sp_preeu_disposicion_servicio);
        sp_preeu_disposicion_servicio.setOnItemSelectedListener(this);
        sp_preeu_mano_de_obra_precio = vista.findViewById(R.id.sp_preeu_mano_de_obra_precio);
        sp_preeu_mano_de_obra_precio.setOnItemSelectedListener(this);

        BtnVerMateriales = vista.findViewById(R.id.btnVerMateriales);
        BtnVerMateriales.setOnClickListener(this);
        try{
            Cliente cliente = ClienteDAO.buscarCliente(getContext());
            int cliId = cliente.getId_cliente();
            if(cliId !=21){
                llTiposOs.setVisibility(View.GONE);
            }
        }catch (SQLException E){

        }

        //ON TEXT CHANGE

        et_enviar_por_correo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!validarEmail(et_enviar_por_correo.getText().toString())) {
                    et_enviar_por_correo.setError("Email no válido");
                } else {
                    try {
                        ParteDAO.actualizarCorreoEnvioDeFactura(getContext(), parte.getId_parte(), et_enviar_por_correo.getText().toString());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        try{
            darValores();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        JSONObject jsonObject;
        int idParte = 0;
        try {
            jsonObject = GestorSharedPreferences.getJsonParte(GestorSharedPreferences.getSharedPreferencesParte(getContext()));
            idParte = jsonObject.getInt("id");
            parte = ParteDAO.buscarPartePorId(getContext(), idParte);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            ArrayList<ArticuloParte> articuloPartes = new ArrayList<>();
            if (ArticuloParteDAO.buscarArticuloParteFkParte(getContext(), parte.getId_parte()) != null) {
                articuloPartes.addAll(ArticuloParteDAO.buscarArticuloParteFkParte(getContext(), parte.getId_parte()));


                et_preeu_materiales.setText(String.format("%.2f", (getPrecioTotalArticulosPartePpto(articuloPartes))));
            } else {
                et_preeu_materiales.setText("0.00");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void darValores() throws SQLException {

            recalcular();
        //SPINNER TIPOS OS
        if (TiposOsDAO.buscarTipoOsPorFkTipoParte(getContext(),parte.getFk_tipo()) != null) {
            tiposOs.addAll(TiposOsDAO.buscarTipoOsPorFkTipoParte(getContext(),parte.getFk_tipo()));
            arrayTiposOs  = new String[tiposOs.size() + 1];
            arrayTiposOs[0] = "--Seleciones un valor--";
            for (int i = 1; i < tiposOs.size() + 1; i++) {
                arrayTiposOs[i] = tiposOs.get(i - 1).getNombre_tipoOs();
            }

            int fk_tipo_os_parte = parte.getFk_tipo_os0();

            ArrayAdapter tiposOsAdp =new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, arrayTiposOs) ;
            spTiposOS.setAdapter(tiposOsAdp);
            if(fk_tipo_os_parte != -1  && fk_tipo_os_parte != 0 ) {
                try{
                    String tipoOsParte = TiposOsDAO.buscarTipoOsPorId_tipoOS(getContext(),fk_tipo_os_parte).getNombre_tipoOs();
                    spTiposOS.setSelection(tiposOsAdp.getPosition(tipoOsParte));
                }catch (SQLException E){
                    E.printStackTrace();
                }
            }
        }
        //SPINNER FORMAS PAGO
        if (FormasPagoDAO.buscarTodasLasFormasPago(getContext()) != null) {
            formasPagos.addAll(FormasPagoDAO.buscarTodasLasFormasPago(getContext()));
            arrayFormasPago = new String[formasPagos.size() + 1];
            arrayFormasPago[0] = "--Seleciones un valor--";
            for (int i = 1; i < formasPagos.size() + 1; i++) {
                arrayFormasPago[i] = formasPagos.get(i - 1).getForma_pago();
            }

            if (datos.getFk_forma_pago() > 0) {
                try {
                    int idFormaPago = datos.getFk_forma_pago();
                    String nombreFormaPago = FormasPagoDAO.buscarFormasPagoPorId(getContext(), idFormaPago).getForma_pago();
                    arrayFormasPago[0] = nombreFormaPago;
                    arrayFormasPago[1] = nombreFormaPago;
                    spFormaPago.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, arrayFormasPago));
                    spFormaPago.setSelection(1);
                    /*spFormaPago.setEnabled(false);
                    spFormaPago.setClickable(false);*/
                } catch (Exception e) {
                    //si algo falla se utiliza la rutina habitual.
                    arrayFormasPago[0] = "--Seleciones un valor--";
                    spFormaPago.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, arrayFormasPago));
                }
            } else {

                spFormaPago.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, arrayFormasPago));
            }
        }


        //SPINNER MANOS DE OBRA
        if (ManoObraDAO.buscarTodasLasManoDeObra(getContext()) != null) {
            manosObra.addAll(ManoObraDAO.buscarTodasLasManoDeObra(getContext()));
            arrayManosObra = new String[manosObra.size() + 1];
            arrayManosObra[0] = "--Seleciones un valor--";
            for (int i = 1; i < manosObra.size() + 1; i++) {
                arrayManosObra[i] = manosObra.get(i - 1).getConcepto();
            }
            sp_preeu_mano_de_obra_precio.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, arrayManosObra));


        }


        //SPINNER DISPOSICIONES SERVICIO
        if (DisposicionesDAO.buscarTodasLasDisposiciones(getContext()) != null) {
            disposicionesServicio.addAll(DisposicionesDAO.buscarTodasLasDisposiciones(getContext()));
            arrayDisposiciones = new String[disposicionesServicio.size() + 1];
            arrayDisposiciones[0] = "--Seleciones un valor--";
            for (int i = 1; i < disposicionesServicio.size() + 1; i++) {
                arrayDisposiciones[i] = disposicionesServicio.get(i - 1).getNombre_disposicion();
            }
            sp_preeu_disposicion_servicio.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, arrayDisposiciones));
        }


        if (parte.getFirma64() != null && !parte.getFirma64().equals("")) {

            btnFirmar.setText("FIRMADO");
            btnFirmar.setEnabled(false);
        }

        if (parte.getTextoDuracion() != null && !parte.getTextoDuracion().equals("")) {
            btn_preeu_mano_de_obra.setText(parte.getTextoDuracion());

        }


        if (datos.getOperacion_efectuada() != null && !datos.getOperacion_efectuada().equals(""))
            etOperacionEfectuada.setText(datos.getOperacion_efectuada());


        et_preeu_total_disposicion_servicio.setText(String.valueOf(datos.getPreeu_disposicion_servicio()));
        et_preeu_total_mano_de_obra_horas
                .setText(String.valueOf(datos.getPreeu_mano_de_obra()
                        *
                        datos.getPreeu_mano_de_obra_precio()));

        et_enviar_por_correo.setText(parte.getEmail_cliente());


    }

    private double getPrecioTotalArticulosPartePpto(ArrayList<ArticuloParte> listaArticulos) {
        double precio = 0;
        try {
            //Si el articulo no está en garantia entonces sesuma el precio de ese articulo

            for (ArticuloParte articulo : listaArticulos) {

                Articulo art;
                art = ArticuloDAO.buscarArticuloPorID(getContext(), articulo.getFk_articulo());

                if (articulo.getGarantia() != true && articulo.getPresupuestar()) {
                    precio = precio + art.getTarifa() * articulo.getUsados();
                }
            }
        } catch (SQLException e) {


            Log.w("getPrecioTotal", "error al sumar precio articulos parte");
        }

        return precio;
    }


    private double getPrecioTotalArticulosParteFacturar(ArrayList<ArticuloParte> listaArticulos) {
        double precio = 0;
        try {
            //Si el articulo no está en garantia entonces sesuma el precio de ese articulo

            for (ArticuloParte articulo : listaArticulos) {

                Articulo art;
                art = ArticuloDAO.buscarArticuloPorID(getContext(), articulo.getFk_articulo());
                if (articulo.getGarantia() != true && articulo.getFacturar()) {
                    precio = precio + art.getTarifa() * articulo.getUsados();
                }
            }
        } catch (SQLException e) {


            Log.w("getPrecioTotal", "error al sumar precio articulos parte");
        }

        return precio;
    }

    //OVERRIDE
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getContext();
        vista = inflater.inflate(R.layout.tab_fragment4_finalizacion_nuevo_v2, container, false);
        JSONObject jsonObject = null;
        int idParte = 0;
        try {
            jsonObject = GestorSharedPreferences.getJsonParte(GestorSharedPreferences.getSharedPreferencesParte(getContext()));
            idParte = jsonObject.getInt("id");
            parte = ParteDAO.buscarPartePorId(getContext(), idParte);
            usuario = UsuarioDAO.buscarUsuarioPorFkEntidad(getContext(), parte.getFk_tecnico());
            if (DatosAdicionalesDAO.buscarDatosAdicionalesPorFkParte(getContext(), parte.getId_parte()) != null) {
                datos = DatosAdicionalesDAO.buscarDatosAdicionalesPorFkParte(getContext(), parte.getId_parte());
            } else {
                DatosAdicionales datosAdicionales = new DatosAdicionales();
                datosAdicionales.setFk_parte(parte.getId_parte());
                datos = DatosAdicionalesDAO.crearDatosAdicionalesRet(datosAdicionales, getContext());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        inicializar();
        //recalcular();


        return vista;

    }


    public void recalcular() {

        try {
            ArrayList<ArticuloParte> articuloPartes = new ArrayList<>();
            if (ArticuloParteDAO.buscarArticuloParteFkParte(getContext(), parte.getId_parte()) != null) {
                articuloPartes.addAll(ArticuloParteDAO.buscarArticuloParteFkParte(getContext(), parte.getId_parte()));
                if (!articuloPartes.isEmpty()) {
                    precioTotalArticulos = getPrecioTotalArticulosPartePpto(articuloPartes);
                    et_preeu_materiales.setText(String.format("%.2f", (precioTotalArticulos)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            preeu_disposicion_servicio = DatosAdicionalesDAO.buscarDisposicionDeServicio(getContext(), datos.getId_rel());
            et_preeu_total_disposicion_servicio.setText(String.valueOf(preeu_disposicion_servicio));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            preeu_mano_de_obra_precio = DatosAdicionalesDAO.buscarPrecioManoDeObra(getContext(), datos.getId_rel());
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            preeu_mano_de_obra_horas = DatosAdicionalesDAO.buscarHorasManoDeObra(getContext(), datos.getId_rel());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        preeu_total_mano_de_obra_horas = preeu_mano_de_obra_precio * preeu_mano_de_obra_horas;

        if (!et_preeu_materiales.getText().toString().equals("")) {
            String str = et_preeu_materiales.getText().toString();
            str = str.replace(',', '.');
            preeu_materiales = Double.valueOf(str);
        }

        if (!et_preeu_puesta_marcha.getText().toString().equals("")) {
            preeu_puesta_marcha = Double.valueOf(et_preeu_puesta_marcha.getText().toString());
        }

        if (!et_preeu_servicio_urgencia.getText().toString().equals("")) {
            preeu_servicio_urgencia = Double.valueOf(et_preeu_servicio_urgencia.getText().toString());
        }

        if (!et_preeu_km.getText().toString().equals("")) {
            preeu_km = Double.valueOf(et_preeu_km.getText().toString());
        }

        if (!et_preeu_km_precio.getText().toString().equals("")) {
            preeu_km_precio = Double.valueOf(et_preeu_km_precio.getText().toString());
        }
        preeu_km_precio_total = preeu_km * preeu_km_precio;

        if (!et_preeu_analisis_combustion.getText().toString().equals("")) {
            preeu_analisis_combustion = Double.valueOf(et_preeu_analisis_combustion.getText().toString());
        }

        if (!et_preeu_adicional.getText().toString().equals("")) {
            preeu_adicional = Double.valueOf(et_preeu_adicional.getText().toString());
        }


        double SubTotal = preeu_adicional + preeu_analisis_combustion +
                preeu_km_precio_total + preeu_materiales + preeu_total_mano_de_obra_horas + preeu_disposicion_servicio + preeu_puesta_marcha + preeu_servicio_urgencia;


        etSubTotal.setText(String.valueOf(String.format("%.2f", SubTotal)));
        double preeu_iva_aplicado = SubTotal * 21 / 100;
        et_preeu_iva_aplicado.setText(String.format("%.2f", preeu_iva_aplicado));
        double total = SubTotal + preeu_iva_aplicado;
        et_total.setText(String.format("%.2f", total));

    }

    public void calcularTiempo() throws SQLException, ParseException {

        int estadoEjecucionParte = parte.getEstadoEjecucion();
        List<HorasParte> horasParte= HorasParteDAO.getAllByFkParte(getContext(),parte.getId_parte());
        String horaInicio = "";
        String fechaInicio = "";
        String fechaHoraInicio = "";
        for (HorasParte hora : horasParte) {
            if(hora.getTipo() == 1){
                horaInicio = hora.getHora_inicio();
                fechaInicio = hora.getFecha();
                fechaHoraInicio = fechaInicio+"T"+horaInicio;
            }
        }

        Calendar c = Calendar.getInstance();
        Date dateFechaHoraInicio = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(fechaHoraInicio);
        long inicio = dateFechaHoraInicio.getTime();
        Utils u = new Utils();
        int horasTotales=0;
        int minutosTotales=0;
        String tiempo = "";
        Date fechaActual = c.getTime();
        long current = fechaActual.getTime();
        DateFormat dtf = new SimpleDateFormat("HH:mm:ss");
        String now = dtf.format(current);
        switch (estadoEjecucionParte){
            case 1:
                //Nunca ha sido pausado

                long millisTranscurridos = current - inicio;


                List<String> duracion = u.getDuration(millisTranscurridos);
                horasTotales = Integer.valueOf(duracion.get(0));
                minutosTotales = Integer.valueOf(duracion.get(1));
                tiempo = duracion.get(3);
                break;
            case 3:
            case 4:
                //Ha sido Reiniciado
               HorasParte firstPausa = HorasParteDAO.getFirstHoraPausa(context,parte.getId_parte());
                Date horafirstPausa = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(firstPausa.getFecha()+"T"+firstPausa.getHora_inicio());

                long inicioPausa = horafirstPausa.getTime();
                long hastaFirstPausa =  inicioPausa -inicio;

                List<String> duracionPrimeraPausa = u.getDuration(hastaFirstPausa);

                horasTotales+=Integer.valueOf(duracionPrimeraPausa.get(0));
                minutosTotales+=Integer.valueOf(duracionPrimeraPausa.get(1));

                for (HorasParte hora : horasParte) {
                    long duracionPeriodo = 0;
                    if(hora.getTipo() == 3){
                        String horaReinicio = hora.getHora_inicio();
                        String fechaReinicio = hora.getFecha();
                        String fechaHoraReinicio = fechaReinicio+"T"+horaReinicio;


                        HorasParte nextHoraPausa = HorasParteDAO.getPausaByFkReinicio(context,parte.getId_parte(),hora.getId_hora());

                        if(nextHoraPausa!=null){
                            String fechaHoraPausa = nextHoraPausa.getFecha()+"T"+nextHoraPausa.getHora_inicio();
                            Date nextPausa = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(fechaHoraPausa);

                            Date Reinicio = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(fechaHoraReinicio);

                            long pausaLong = nextPausa.getTime();
                            long reinicioLong = Reinicio.getTime();
                            duracionPeriodo =  pausaLong - reinicioLong;
                        }else{

                            Date Reinicio = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(fechaHoraReinicio);

                            long pausaLong = current;
                            long reinicioLong = Reinicio.getTime();
                            duracionPeriodo =  pausaLong - reinicioLong;
                        }

                        List<String> duracionPausa = u.getDuration(duracionPeriodo);
                        horasTotales+=Integer.valueOf(duracionPausa.get(0));
                        minutosTotales+=Integer.valueOf(duracionPausa.get(1));
                        /*if(minutosTotales > 60){
                            double doubleminutosTotales = minutosTotales/60;
                            String[] stringminutosTotales = String.valueOf(doubleminutosTotales).split(",");
                            horasTotales+=Integer.valueOf(duracionPausa.get(0))+Integer.valueOf(stringminutosTotales[0]);
                            minutosTotales = Integer.valueOf(stringminutosTotales[1]);
                        }else{
                            horasTotales+=Integer.valueOf(duracionPausa.get(0));
                        }*/
                    }
                }
                String strMinutosTotales = String.format("%.2f", minutosTotales/60.0) ;

                String[] stringMin = strMinutosTotales.toString().split(",");
                stringMin[1] = "0."+stringMin[1];
                int intHoras= horasTotales+Integer.valueOf(stringMin[0]);
                String strHorasTotales =String.valueOf(intHoras);

                double minutosDecToHex =  Double.valueOf(stringMin[1]);

                minutosDecToHex =minutosDecToHex*60;
                int intMinutos = (int) minutosDecToHex;
                String strMinutos = String.valueOf(intMinutos);
                tiempo = strHorasTotales+" horas " + strMinutos + " minutos";

                break;

        }

        btn_preeu_mano_de_obra.setText(tiempo);
        textoBoton =tiempo;

        double mins = Double.parseDouble(String.format("%.2f", minutosTotales/60.0).replace(",","."));
        preeu_mano_de_obra_horas = Double.valueOf(horasTotales + mins);

        try {
            ParteDAO.actualizarTextoDuracion(getContext(), parte.getId_parte(), textoBoton);
            DatosAdicionalesDAO.actualizarHorasManoDeObra(getContext(), datos.getId_rel(), preeu_mano_de_obra_horas);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (sp_preeu_mano_de_obra_precio.getSelectedItemPosition() != 0) {

            double mH = 0;
            try {
                mH = ManoObraDAO.buscarPrecioManoObraPorNombre(getContext(), sp_preeu_mano_de_obra_precio.getSelectedItem().toString());
            } catch (SQLException e) {
                e.printStackTrace();
            }

            et_preeu_total_mano_de_obra_horas.setText(String.format("%.2f", mH * preeu_mano_de_obra_horas).replace(",","."));

        }

        DateFormat dformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String dateCierreHoras =  dformat.format(current);
        parte.setDateCierreHoras(dateCierreHoras);
        ParteDAO.actualizarDateCierreHoras(context,parte.getId_parte(),dateCierreHoras);



        HorasParte cierre = HorasParteDAO.getCierreParte(context,parte.getId_parte());
        if(cierre != null){
            int borrado = HorasParteDAO.borrarHoraParteById(context,cierre.getId_hora());
            if(borrado == 1){
                new HiloHorasParte(getContext(), parte.getFk_tecnico(),parte.getId_parte(),now,now,parte.getFecha_visita(),4,this ).execute();
            }
        }else{
            new HiloHorasParte(getContext(), parte.getFk_tecnico(),parte.getId_parte(),now,now,parte.getFecha_visita(),4,this ).execute();
        }
      /* Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        preeu_mano_de_obra_horas = 0;
        String horaEntrada = datos.getMatem_hora_entrada();
        String[] tiempos = horaEntrada.split(":");
        int hour = Integer.valueOf(tiempos[0]);
        int minute = Integer.valueOf(tiempos[1]);
        int second = Integer.valueOf(tiempos[2]);
        int segDesdeElInicio = hour * 60 * 60 + minute * 60 + second;
        Calendar c2 = Calendar.getInstance();
        SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss");
        String formattedDate2 = df.format(c.getTime());
        String[] tiempos2 = formattedDate2.split(":");
        int hourFin = Integer.valueOf(tiempos2[0]);
        int minuteFin = Integer.valueOf(tiempos2[1]);
        int secondFin = Integer.valueOf(tiempos2[2]);
        int segDesdeFin = hourFin * 60 * 60 + minuteFin * 60 + secondFin;
        int segTotales = segDesdeFin - segDesdeElInicio;
        int horaTotal = segTotales / 3600;
        int segTotal = segTotales % 3600;
        int minTotal = segTotal / 60;
        btn_preeu_mano_de_obra.setText(horaTotal + " horas " + minTotal + " minutos");
        textoBoton = horaTotal + " horas " + minTotal + " minutos";
        double mins = Double.parseDouble(String.format("%.2f", minTotal/60.0).replace(",","."));
        preeu_mano_de_obra_horas = Double.valueOf(horaTotal + mins);

        try {
            ParteDAO.actualizarTextoDuracion(getContext(), parte.getId_parte(), textoBoton);
            DatosAdicionalesDAO.actualizarHorasManoDeObra(getContext(), datos.getId_rel(), preeu_mano_de_obra_horas);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (sp_preeu_mano_de_obra_precio.getSelectedItemPosition() != 0) {

            double mH = 0;
            try {
                mH = ManoObraDAO.buscarPrecioManoObraPorNombre(getContext(), sp_preeu_mano_de_obra_precio.getSelectedItem().toString());
            } catch (SQLException e) {
                e.printStackTrace();
            }

            et_preeu_total_mano_de_obra_horas.setText(String.format("%.2f", mH * preeu_mano_de_obra_horas).replace(",","."));

        }*/
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == BtnVerMateriales.getId()) {
            Intent i = new Intent(getContext(), GestionMateriales.class);
            i.putExtra("fk_parte", parte.getId_parte());
            startActivityForResult(i, 654);
        } else {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            String formattedDate = df.format(c.getTime());
            if (view.getId() == R.id.btnFirmar) {
                Intent i = new Intent(getContext(), FirmaCliente.class);
                startActivityForResult(i, 99);
            } else if (view.getId() == R.id.btn_calcular_tiempo) {

                try {
                    calcularTiempo();
                } catch (SQLException | ParseException e) {
                    e.printStackTrace();
                }
            } else if (view.getId() == R.id.btn_preeu_mano_de_obra) {
                preeu_mano_de_obra_horas = 0;
                int hour = 0;
                final int minute = 0;
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), (timePicker, selectedHour, selectedMinute) -> {

                    String HorasMinutos = selectedHour + " horas " + selectedMinute + " minutos";
                    btn_preeu_mano_de_obra.setText(HorasMinutos);
                    textoBoton = HorasMinutos;
                    double minutos = selectedMinute;
                    minutos = minutos / 60;
                    preeu_mano_de_obra_horas = selectedHour + minutos;
                    try {
                        ParteDAO.actualizarTextoDuracion(getContext(), parte.getId_parte(), textoBoton);
                        DatosAdicionalesDAO.actualizarHorasManoDeObra(getContext(), datos.getId_rel(), preeu_mano_de_obra_horas);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (sp_preeu_mano_de_obra_precio.getSelectedItemPosition() != 0) {
                        double mH = 0;
                        try {
                            mH = ManoObraDAO.buscarPrecioManoObraPorNombre(getContext(), sp_preeu_mano_de_obra_precio.getSelectedItem().toString());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        et_preeu_total_mano_de_obra_horas.setText(String.valueOf(mH * preeu_mano_de_obra_horas));
                    }

                }, hour, minute, true);
                mTimePicker.setTitle("Selecciona la duración");
                mTimePicker.show();
            } else if (view.getId() == R.id.btnFinalizar) {
                recalcular();
                if (spFormaPago.getSelectedItemPosition() != 0 ) {
                //if (spFormaPago.getSelectedItemPosition() != 0 && spTiposOS.getSelectedItemPosition() != 0) {
                //if (1==1){
                    if (!acepta_presupuesto) {
                        new AlertDialog.Builder(getContext()).setMessage("No ha aceptado el presupuesto, si ha solicitado materiales no entrarán en el almacén.\n\n¿Desea continuar?")
                                .setCancelable(false)
                                .setPositiveButton("Aceptar", (hi, dd) -> {
                                            Calendar c1 = Calendar.getInstance();
                                            SimpleDateFormat df1 = new SimpleDateFormat("HH:mm:ss");
                                            String formattedDate1 = df1.format(c1.getTime());

                                            //if (parte.getFirma64() != null && !parte.getFirma64().equals("")) { QUITAR CONDICION DE FIRMAR PARA FINALIZAR PARTE
                                    if(1==1){
                                                ArrayList<ArticuloParte> articuloPartes = new ArrayList<>();
                                                try {
                                                    if (ArticuloParteDAO.buscarArticuloParteFkParte(getContext(), parte.getId_parte()) != null) {
                                                        articuloPartes.addAll(ArticuloParteDAO.buscarArticuloParteFkParte(getContext(), parte.getId_parte()));
                                                    }
                                                } catch (SQLException e) {
                                                    e.printStackTrace();
                                                }
                                                String operacionEfectuada = "";
                                                if (!etOperacionEfectuada.getText().toString().equals("")) {
                                                    operacionEfectuada = etOperacionEfectuada.getText().toString();
                                                }
                                                String preeu_otros_nombre = "";
                                                if (!et_preeu_otros_nombre.getText().toString().equals("")) {
                                                    preeu_otros_nombre = et_preeu_otros_nombre.getText().toString();
                                                }
                                                preeu_total_mano_de_obra_horas = preeu_mano_de_obra_precio * preeu_mano_de_obra_horas;

                                                if (!et_preeu_materiales.getText().toString().equals("")) {
                                                    String str = et_preeu_materiales.getText().toString();
                                                    str = str.replace(',', '.');
                                                    //preeu_materiales = Double.parseDouble(et_preeu_materiales.getText().toString());
                                                    preeu_materiales = Double.parseDouble(str);
                                                }
                                                if (!et_preeu_puesta_marcha.getText().toString().equals("")) {
                                                    preeu_puesta_marcha = Double.parseDouble(et_preeu_puesta_marcha.getText().toString());
                                                }

                                                if (!et_preeu_servicio_urgencia.getText().toString().equals("")) {
                                                    preeu_servicio_urgencia = Double.parseDouble(et_preeu_servicio_urgencia.getText().toString());
                                                }

                                                if (!et_preeu_km.getText().toString().equals("")) {
                                                    preeu_km = Double.parseDouble(et_preeu_km.getText().toString());
                                                }

                                                if (!et_preeu_km_precio.getText().toString().equals("")) {
                                                    preeu_km_precio = Double.parseDouble(et_preeu_km_precio.getText().toString());
                                                }
                                                preeu_km_precio_total = preeu_km * preeu_km_precio;

                                                if (!et_preeu_analisis_combustion.getText().toString().equals("")) {
                                                    preeu_analisis_combustion = Double.parseDouble(et_preeu_analisis_combustion.getText().toString());
                                                }

                                                if (!et_preeu_adicional.getText().toString().equals("")) {
                                                    preeu_adicional = Double.parseDouble(et_preeu_adicional.getText().toString());
                                                }
                                                double SubTotal =
                                                        preeu_adicional
                                                                + preeu_analisis_combustion
                                                                + preeu_km_precio_total
                                                                + preeu_materiales
                                                                + preeu_total_mano_de_obra_horas
                                                                + preeu_disposicion_servicio
                                                                + preeu_puesta_marcha
                                                                + preeu_servicio_urgencia;
                                                etSubTotal.setText(String.valueOf(SubTotal));
                                                double preeu_iva_aplicado = SubTotal * 21 / 100;
                                                et_preeu_iva_aplicado.setText(String.format("%.2f", preeu_iva_aplicado));
                                                double total = SubTotal + preeu_iva_aplicado;
                                                et_total.setText(String.format("%.2f", total));
                                                double fact_materiales,
                                                        fact_subtotal,
                                                        fact_por_iva_aplicado,
                                                        fact_total_con_iva;
                                                fact_materiales = getPrecioTotalArticulosParteFacturar(articuloPartes);
                                                fact_subtotal = preeu_adicional + preeu_analisis_combustion +
                                                        preeu_km_precio_total + fact_materiales + preeu_total_mano_de_obra_horas + preeu_disposicion_servicio + preeu_puesta_marcha + preeu_servicio_urgencia;

                                                fact_por_iva_aplicado = fact_subtotal * 21 / 100;
                                                fact_total_con_iva = fact_por_iva_aplicado + fact_subtotal;


                                                try {
                                                    DatosAdicionalesDAO.actualizarDatosAdicionalesParteFacturable(getContext(), datos.getId_rel(), fact_materiales, fact_por_iva_aplicado, fact_total_con_iva);
                                                } catch (SQLException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    DatosAdicionalesDAO.actualizarDatosAdicionales(getContext(), datos.getId_rel(),
                                                            operacionEfectuada,
                                                            preeu_materiales,
                                                            preeu_disposicion_servicio,
                                                            preeu_mano_de_obra_precio,
                                                            preeu_mano_de_obra_horas,
                                                            preeu_total_mano_de_obra_horas,
                                                            preeu_puesta_marcha,
                                                            preeu_servicio_urgencia,
                                                            preeu_km,
                                                            preeu_km_precio,
                                                            preeu_km_precio_total,
                                                            preeu_analisis_combustion,
                                                            preeu_otros_nombre,
                                                            preeu_adicional,
                                                            SubTotal,
                                                            21,
                                                            total,
                                                            acepta_presupuesto,
                                                            forma_pago

                                                    );
                                                    datos.setMatem_hora_salida(formattedDate1);
                                                    DatosAdicionalesDAO.actualizarHoraSalida(getContext(), datos.getId_rel(), formattedDate1);
                                                    ParteDAO.actualizarParteDuracion(getContext(), parte.getId_parte(), String.valueOf(preeu_mano_de_obra_horas));
                                                    new HiloCerrarParte(getContext(), parte.getId_parte()).execute();
                                                } catch (SQLException e) {
                                                    e.printStackTrace();
                                                }


                                            } else {
                                                Dialogo.dialogoError("Es necesario la firma del cliente para finalizar.(Pestaña de Documentos)", getContext());
                                            }


                                        }
                                )
                                .setNegativeButton("Cancelar", (hi, dd) -> {

                                        }
                                ).show();
                    } else {
                        Log.e("finaliza entra", "entro aqui");
                        if (parte.getFirma64() != null && !parte.getFirma64().equals("")) {
                            ArrayList<ArticuloParte> articuloPartes = new ArrayList<>();
                            try {
                                if (ArticuloParteDAO.buscarArticuloParteFkParte(getContext(), parte.getId_parte()) != null) {
                                    articuloPartes.addAll(ArticuloParteDAO.buscarArticuloParteFkParte(getContext(), parte.getId_parte()));
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            String operacionEfectuada = "";
                            if (!etOperacionEfectuada.getText().toString().equals("")) {
                                operacionEfectuada = etOperacionEfectuada.getText().toString();
                            }
                            String preeu_otros_nombre = "";
                            if (!et_preeu_otros_nombre.getText().toString().equals("")) {
                                preeu_otros_nombre = et_preeu_otros_nombre.getText().toString();
                            }
                            preeu_total_mano_de_obra_horas = preeu_mano_de_obra_precio * preeu_mano_de_obra_horas;
                            if (!et_preeu_materiales.getText().toString().equals("")) {
                                String str = et_preeu_materiales.getText().toString();
                                str = str.replace(',', '.');
                                preeu_materiales = Double.parseDouble(str);
                            }
                            if (!et_preeu_puesta_marcha.getText().toString().equals("")) {
                                preeu_puesta_marcha = Double.parseDouble(et_preeu_puesta_marcha.getText().toString());
                            }

                            if (!et_preeu_servicio_urgencia.getText().toString().equals("")) {
                                preeu_servicio_urgencia = Double.parseDouble(et_preeu_servicio_urgencia.getText().toString());
                            }
                            if (!et_preeu_km.getText().toString().equals("")) {
                                preeu_km = Double.parseDouble(et_preeu_km.getText().toString());
                            }
                            if (!et_preeu_km_precio.getText().toString().equals("")) {
                                preeu_km_precio = Double.parseDouble(et_preeu_km_precio.getText().toString());
                            }
                            preeu_km_precio_total = preeu_km * preeu_km_precio;
                            if (!et_preeu_analisis_combustion.getText().toString().equals("")) {
                                preeu_analisis_combustion = Double.parseDouble(et_preeu_analisis_combustion.getText().toString());
                            }
                            if (!et_preeu_adicional.getText().toString().equals("")) {
                                preeu_adicional = Double.parseDouble(et_preeu_adicional.getText().toString());
                            }
                            double SubTotal = preeu_adicional + preeu_analisis_combustion +
                                    preeu_km_precio_total + preeu_materiales + preeu_total_mano_de_obra_horas + preeu_disposicion_servicio + preeu_puesta_marcha + preeu_servicio_urgencia;
                            etSubTotal.setText(String.valueOf(SubTotal));
                            double preeu_iva_aplicado = SubTotal * 21 / 100;
                            et_preeu_iva_aplicado.setText(String.format("%.2f", preeu_iva_aplicado));
                            double total = SubTotal + preeu_iva_aplicado;
                            et_total.setText(String.format("%.2f", total));
                            double fact_materiales,
                                    fact_subtotal,
                                    fact_por_iva_aplicado,
                                    fact_total_con_iva;
                            fact_materiales = getPrecioTotalArticulosParteFacturar(articuloPartes);
                            fact_subtotal = preeu_adicional + preeu_analisis_combustion +
                                    preeu_km_precio_total + fact_materiales + preeu_total_mano_de_obra_horas + preeu_disposicion_servicio + preeu_puesta_marcha + preeu_servicio_urgencia;

                            fact_por_iva_aplicado = fact_subtotal * 21 / 100;
                            fact_total_con_iva = fact_por_iva_aplicado + fact_subtotal;
                            try {
                                DatosAdicionalesDAO.actualizarDatosAdicionalesParteFacturable(getContext(), datos.getId_rel(), fact_materiales, fact_por_iva_aplicado, fact_total_con_iva);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            try {
                                DatosAdicionalesDAO.actualizarDatosAdicionales(getContext(), datos.getId_rel(),
                                        operacionEfectuada,
                                        preeu_materiales,
                                        preeu_disposicion_servicio,
                                        preeu_mano_de_obra_precio,
                                        preeu_mano_de_obra_horas,
                                        preeu_total_mano_de_obra_horas,
                                        preeu_puesta_marcha,
                                        preeu_servicio_urgencia,
                                        preeu_km,
                                        preeu_km_precio,
                                        preeu_km_precio_total,
                                        preeu_analisis_combustion,
                                        preeu_otros_nombre,
                                        preeu_adicional,
                                        SubTotal,
                                        21,
                                        total,
                                        acepta_presupuesto,
                                        forma_pago
                                );
                                datos.setMatem_hora_salida(formattedDate);
                                DatosAdicionalesDAO.actualizarHoraSalida(getContext(), datos.getId_rel(), formattedDate);
                                ParteDAO.actualizarParteDuracion(getContext(), parte.getId_parte(), String.valueOf(preeu_mano_de_obra_horas));
                                new HiloCerrarParte(getContext(), parte.getId_parte()).execute();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Dialogo.dialogoError("Es necesario la firma del cliente para finalizar.(Pestaña de Documentos)\n\nGracias.", getContext());
                        }
                    }
                } else {
                    //Dialogo.dialogoError("Es necesario seleccionar una forma de pago y un Tipo de Orden de Servicio para poder finalizar el aviso\n\nGracias.", getContext());
                    Dialogo.dialogoError("Es necesario seleccionar una forma de pago para poder finalizar el aviso\n\nGracias.", getContext());
                }
            }
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        etOperacionEfectuada.clearFocus();

        if (parent.getId() == sp_preeu_disposicion_servicio.getId()) {
            if (sp_preeu_disposicion_servicio.getSelectedItemPosition() != 0) {
                String selected = sp_preeu_disposicion_servicio.getSelectedItem().toString();
                Log.d("SELECTED",selected);
                try {
                    preeu_disposicion_servicio = DisposicionesDAO.buscarPrecioDisposicionPorNombre(getContext(), sp_preeu_disposicion_servicio.getSelectedItem().toString());
                    DatosAdicionalesDAO.actualizarDisposicionDeServicio(getContext(), datos.getId_rel(), preeu_disposicion_servicio);
                    et_preeu_total_disposicion_servicio.setText(String.valueOf(preeu_disposicion_servicio));

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else if (parent.getId() == sp_preeu_mano_de_obra_precio.getId()) {
            if (sp_preeu_mano_de_obra_precio.getSelectedItemPosition() != 0) {
                try {
                    preeu_mano_de_obra_precio = ManoObraDAO.buscarPrecioManoObraPorNombre(getContext(), sp_preeu_mano_de_obra_precio.getSelectedItem().toString());
                    et_preeu_total_mano_de_obra_horas.setText(String.valueOf(preeu_mano_de_obra_precio * preeu_mano_de_obra_horas));
                    DatosAdicionalesDAO.actializarManoDeObraPrecio(getContext(), datos.getId_rel(), preeu_mano_de_obra_precio);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        } else if (parent.getId() == spFormaPago.getId()) {
            if (spFormaPago.getSelectedItemPosition() != -1) {
                try {
                    forma_pago = FormasPagoDAO.buscarIdFormaPagoPorNombre(getContext(), spFormaPago.getSelectedItem().toString());
                    if (forma_pago == -1) forma_pago = 0;
                    DatosAdicionalesDAO.actualizarFormaPago(getContext(), datos.getId_rel(), forma_pago);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }else if (parent.getId() == spTiposOS.getId()) {
            if (spTiposOS.getSelectedItemPosition() != 0) {
                Log.d("Posicion Spinner", String.valueOf(position));
                String selected = spTiposOS.getSelectedItem().toString();

               try {
                    tipoOsId = TiposOsDAO.buscarTipoOsPorNombreTipoOs(getContext(),selected);
                    ParteDAO.actualizarFk_tipo_os(getContext(),parte.getId_parte(),tipoOsId);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    ParteDAO.actualizarFk_tipo_os(getContext(),parte.getId_parte(),-1);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }

        try {
            ArrayList<ArticuloParte> articuloPartes = new ArrayList<>();
            if (ArticuloParteDAO.buscarArticuloParteFkParte(getContext(), parte.getId_parte()) != null) {
                articuloPartes.addAll(ArticuloParteDAO.buscarArticuloParteFkParte(getContext(), parte.getId_parte()));
                if (!articuloPartes.isEmpty()) {
                    precioTotalArticulos = getPrecioTotalArticulosPartePpto(articuloPartes);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        recalcular();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == cb_acepta_presupuesto.getId())
            acepta_presupuesto = isChecked;
        else if (buttonView.getId() == cb_enviar_por_correo.getId()) {
            enviar_correo = isChecked;
            try {
                ParteDAO.enviarPorCorreo(getContext(), parte.getId_parte(), isChecked);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        if (isChecked)
            vista.findViewById(R.id.llEnviarPorCorreo).setVisibility(View.VISIBLE);
        else vista.findViewById(R.id.llEnviarPorCorreo).setVisibility(View.GONE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 99) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    String path = data.getStringExtra("path");
                    Bitmap bitmap;
                    bitmap = loadFirmaClienteFromStorage(parte.getId_parte(), getContext());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    ParteDAO.actualizarFirma64(getContext(), parte.getId_parte(), encodedImage);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                btnFirmar.setEnabled(false);

            }
        } else if (requestCode == 654) {
            ArrayList<ArticuloParte> articuloPartes = new ArrayList<>();
            try {
                if (ArticuloParteDAO.buscarArticuloParteFkParte(getContext(), parte.getId_parte()) != null) {
                    articuloPartes.addAll(ArticuloParteDAO.buscarArticuloParteFkParte(getContext(), parte.getId_parte()));
                    et_preeu_materiales.setText(String.format("%.2f", (getPrecioTotalArticulosPartePpto(articuloPartes))));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }

    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }


    public static Bitmap loadFirmaClienteFromStorage(int id, Context context) throws SQLException {
        Bitmap b = null;
        try {
            File f = new File(Constantes.PATH, "firmaCliente" + id + ".png");
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }
    TextWatcher watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //YOUR CODE
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //YOUR CODE
        }

        @Override
        public void afterTextChanged(Editable s) {

            recalcular();

        }
    };
    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        recalcular();
        if (!hasFocus) {
            try {
                recalcular();
                DatosAdicionalesDAO.actualizarOperacionEfectuada(getContext(), datos.getId_rel(), etOperacionEfectuada.getText().toString());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}