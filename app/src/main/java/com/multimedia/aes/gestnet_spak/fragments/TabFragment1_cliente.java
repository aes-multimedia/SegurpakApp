package com.multimedia.aes.gestnet_spak.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.multimedia.aes.gestnet_spak.Mapa;
import com.multimedia.aes.gestnet_spak.R;
import com.multimedia.aes.gestnet_spak.SharedPreferences.GestorSharedPreferences;
import com.multimedia.aes.gestnet_spak.constantes.Constantes;

import com.multimedia.aes.gestnet_spak.dao.ClienteDAO;
import com.multimedia.aes.gestnet_spak.dao.ConfiguracionDAO;
import com.multimedia.aes.gestnet_spak.dao.DatosAdicionalesDAO;
import com.multimedia.aes.gestnet_spak.dao.ElementoInstDAO;
import com.multimedia.aes.gestnet_spak.dao.MaquinaDAO;
import com.multimedia.aes.gestnet_spak.dao.ParteDAO;
import com.multimedia.aes.gestnet_spak.dao.UsuarioDAO;
import com.multimedia.aes.gestnet_spak.dialogo.Dialogo;

import com.multimedia.aes.gestnet_spak.entidades.Cliente;
import com.multimedia.aes.gestnet_spak.entidades.Configuracion;
import com.multimedia.aes.gestnet_spak.entidades.DatosAdicionales;
import com.multimedia.aes.gestnet_spak.entidades.Maquina;
import com.multimedia.aes.gestnet_spak.entidades.Parte;
import com.multimedia.aes.gestnet_spak.entidades.Usuario;
import com.multimedia.aes.gestnet_spak.hilos.HiloHorasParte;
import com.multimedia.aes.gestnet_spak.hilos.HiloIniciarParte;
import com.multimedia.aes.gestnet_spak.nucleo.DocumentosParte;

import com.multimedia.aes.gestnet_spak.nucleo.FormularioCrearElemento;
import com.multimedia.aes.gestnet_spak.nucleo.GaleriaV2;
import com.multimedia.aes.gestnet_spak.nucleo.Index;
import com.multimedia.aes.gestnet_spak.nucleo.IntervencionesAnteriores;
import com.multimedia.aes.gestnet_spak.nucleo.Presupuestos;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TabFragment1_cliente extends Fragment implements View.OnClickListener {

    private View vista;
    private static Parte parte = null;
    private Usuario usuario = null;
    private Maquina maquina = null;
    private DatosAdicionales datos;
    private Switch swEdicion;
    private TextView txtNumParte, txtCreadoPor, txtMaquina, txtTipoIntervencion, txtSituacionEquipo, txtDierccionTitular,
            txtSintomas, txtHoraInicio, txtSintomaLista, txtNombreContrato, txtEstadoParte, txtNumOrden, txtVerPresupuesto;
    private EditText etNombreTitular, etDni, etTelefono1, etTelefono2, etTelefono3, etTelefono4, etObservaciones, etCorreoElectronico;
    private Button btnIniciarParte,btnPausarParte,btnResumeParte, btnClienteAusente, btnImprimir, btnVerDocumentos, btnImagenes,
            btnAñadirPresupuesto, btnVerPresupuesto, btnVerIntervenciones, btnGuardarDatos, btnCrearElemento;
    private ImageButton ibLocation, ibIr;
    private ImageView ivLlamar1, ivLlamar2, ivLlamar3, ivLlamar4;
    private String horaInicio;
    private Configuracion configuracion;

    private static Activity activity;
    private static ArrayList<String[]> arrayListElemInst = new ArrayList<>();
    private static TextView txtNum;
    private static TextView txtNom;

    //METODO
    private void inicializarVariables() throws SQLException {
        //TEXT VIEWS
        txtNumParte = vista.findViewById(R.id.txtNumParte);
        txtCreadoPor = vista.findViewById(R.id.txtCreadoPor);
        txtMaquina = vista.findViewById(R.id.txtMaquina);
        txtTipoIntervencion = vista.findViewById(R.id.txtTipoIntervencion);
        txtSituacionEquipo = vista.findViewById(R.id.txtSituacionEquipo);
        txtNombreContrato = vista.findViewById(R.id.txtNombreContrato);
        txtDierccionTitular = vista.findViewById(R.id.txtDierccionTitular);
        txtSintomaLista = vista.findViewById(R.id.txtSintomaLista);
        txtSintomas = vista.findViewById(R.id.txtSintomas);
        txtHoraInicio = vista.findViewById(R.id.txtHoraInicio);
        txtEstadoParte = vista.findViewById(R.id.txtEstadoParte);
        txtNumOrden = vista.findViewById(R.id.txtNumOrden);
        txtVerPresupuesto = vista.findViewById(R.id.txtVerPresupuesto);

        txtNom = vista.findViewById(R.id.textView53);
        txtNum = vista.findViewById(R.id.textView54);
        //tvHoraInicio=vista.findViewById(R.id.txtHoraInicio);

        //EDIT TEXTS
        etNombreTitular = vista.findViewById(R.id.etNombreTitular);
        etDni = vista.findViewById(R.id.etDni);
        etTelefono1 = vista.findViewById(R.id.etTelefono1);
        etTelefono2 = vista.findViewById(R.id.etTelefono2);
        etTelefono3 = vista.findViewById(R.id.etTelefono3);
        etTelefono4 = vista.findViewById(R.id.etTelefono4);
        etObservaciones = vista.findViewById(R.id.etObservaciones);
        etCorreoElectronico = vista.findViewById(R.id.etCorreoElectronico);

        //BOTONES
        btnImagenes = vista.findViewById(R.id.btnAñadirImagen);
        btnIniciarParte = vista.findViewById(R.id.btnIniciarParte);
        btnPausarParte = vista.findViewById(R.id.btnPausarParte);
        btnResumeParte = vista.findViewById(R.id.btnResumeParte);
        btnClienteAusente = vista.findViewById(R.id.btnClienteAusente);
        btnImprimir = vista.findViewById(R.id.btnImprimir);
        btnVerDocumentos = vista.findViewById(R.id.btnVerDocumentos);
        btnAñadirPresupuesto = vista.findViewById(R.id.btnAñadirPresupuesto);
        btnVerPresupuesto = vista.findViewById(R.id.btnVerPresupuesto);
        btnVerIntervenciones = vista.findViewById(R.id.btnIntervencionesAnteriotes);
        btnGuardarDatos = vista.findViewById(R.id.btnGuardarDatos);
        btnCrearElemento = vista.findViewById(R.id.btnCrearElemento);

        //IMAGEBUTTON
        ibLocation = vista.findViewById(R.id.ibLocation);
        ibIr = vista.findViewById(R.id.ibIr);
        //IMAGEVIEW
        ivLlamar1 = vista.findViewById(R.id.ivLlamar1);
        ivLlamar2 = vista.findViewById(R.id.ivLlamar2);
        ivLlamar3 = vista.findViewById(R.id.ivLlamar3);
        ivLlamar4 = vista.findViewById(R.id.ivLlamar4);
        //ONCLICK
        btnAñadirPresupuesto.setOnClickListener(this);
        btnVerPresupuesto.setOnClickListener(this);
        btnImagenes.setOnClickListener(this);

        btnIniciarParte.setOnClickListener(this);
        btnResumeParte.setOnClickListener(this);
        btnPausarParte.setOnClickListener(this);
        btnResumeParte.setVisibility(View.GONE);
        btnPausarParte.setVisibility(View.GONE);

        btnClienteAusente.setOnClickListener(this);
        btnImprimir.setOnClickListener(this);
        btnVerDocumentos.setOnClickListener(this);
        btnVerIntervenciones.setOnClickListener(this);
        btnGuardarDatos.setOnClickListener(this);
        btnCrearElemento.setOnClickListener(this);
        ibLocation.setOnClickListener(this);
        ibIr.setOnClickListener(this);
        ivLlamar1.setOnClickListener(this);
        ivLlamar2.setOnClickListener(this);
        ivLlamar3.setOnClickListener(this);
        ivLlamar4.setOnClickListener(this);
        txtVerPresupuesto.setOnClickListener(this);
        //SWITCH
        swEdicion = vista.findViewById(R.id.swEdicion);
        swEdicion.setChecked(false);




        //TEXTWATCHER
        etNombreTitular.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    ParteDAO.actualizarNobreCliente(getContext(), parte.getId_parte(), etNombreTitular.getText().toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        etDni.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    ParteDAO.actualizarDniCliente(getContext(), parte.getId_parte(), etDni.getText().toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        etTelefono1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    ParteDAO.actualizarTelefono1Cliente(getContext(), parte.getId_parte(), etTelefono1.getText().toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        etTelefono2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    ParteDAO.actualizarTelefono2Cliente(getContext(), parte.getId_parte(), etTelefono2.getText().toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        etTelefono3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    ParteDAO.actualizarTelefono3Cliente(getContext(), parte.getId_parte(), etTelefono3.getText().toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        etTelefono4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    ParteDAO.actualizarTelefono4Cliente(getContext(), parte.getId_parte(), etTelefono4.getText().toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        etCorreoElectronico.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    ParteDAO.actualizarCorreCliente(getContext(), parte.getId_parte(), etCorreoElectronico.getText().toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });


        etObservaciones.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    ParteDAO.actualizarObservaciones(getContext(), parte.getId_parte(), etObservaciones.getText().toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        //CHECKEDCHANGE
        swEdicion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (swEdicion.isChecked()) {

                etNombreTitular.setEnabled(true);
                etDni.setEnabled(true);
                etTelefono1.setEnabled(true);
                etTelefono2.setEnabled(true);
                etTelefono3.setEnabled(true);
                etTelefono4.setEnabled(true);
                etCorreoElectronico.setEnabled(true);
                etObservaciones.setEnabled(true);
            } else {
                etNombreTitular.setEnabled(false);
                etDni.setEnabled(false);
                etTelefono1.setEnabled(false);
                etTelefono2.setEnabled(false);
                etTelefono3.setEnabled(false);
                etTelefono4.setEnabled(false);
                etCorreoElectronico.setEnabled(false);
                etObservaciones.setEnabled(false);
            }


        });


    }

    private void darValoresVariables() {
        if (maquina != null) {
            txtMaquina.setText(String.valueOf(maquina.getModelo()));
            if (!maquina.getSituacion().equals("")) {
                txtSituacionEquipo.setText(String.valueOf(maquina.getSituacion()));
            }
            if (!maquina.getNombre_contr_man().equals("")) {
                txtNombreContrato.setText(String.valueOf(maquina.getNombre_contr_man()));
            }
        }
        etNombreTitular.setEnabled(false);
        etDni.setEnabled(false);
        etTelefono1.setEnabled(false);
        etTelefono2.setEnabled(false);
        etTelefono3.setEnabled(false);
        etTelefono4.setEnabled(false);
        etObservaciones.setEnabled(false);
        txtNumParte.setText(String.valueOf(parte.getNum_parte()));
        txtCreadoPor.setText(String.valueOf(parte.getUser_creador()));
        txtTipoIntervencion.setText(String.valueOf(parte.getTipo()));
        txtEstadoParte.setText(String.valueOf(parte.getEstado_parte()));
        txtNumOrden.setText(String.valueOf(parte.getNum_orden_endesa()));
        if (parte.getFk_instalacion() != -1 && configuracion.isPresupuestar()) {
            txtVerPresupuesto.setVisibility(View.VISIBLE);
            txtVerPresupuesto.setText("VER PRESUPUESTO (" + parte.getFk_instalacion() + ")");
        } else {
            txtVerPresupuesto.setVisibility(View.GONE);
        }
        String dir = "";
        if (!parte.getTipo_via().trim().equals("") && !parte.getTipo_via().trim().equals("null")) {
            dir += parte.getTipo_via() + " ";
        }
        if (!parte.getVia().trim().equals("") && !parte.getVia().trim().equals("null")) {
            dir += parte.getVia() + " ";
        }
        if (!parte.getNumero_direccion().trim().equals("") && !parte.getNumero_direccion().trim().equals("null")) {
            dir += "Nº " + parte.getNumero_direccion() + " ";
        }
        if (!parte.getEscalera_direccion().trim().equals("") && !parte.getEscalera_direccion().trim().equals("null")) {
            dir += "Esc. " + parte.getEscalera_direccion() + " ";
        }
        if (!parte.getPiso_direccion().trim().equals("") && !parte.getPiso_direccion().trim().equals("null")) {
            dir += "Piso " + parte.getPiso_direccion() + " ";
        }
        if (!parte.getPuerta_direccion().trim().equals("") && !parte.getPuerta_direccion().trim().equals("null")) {
            dir += parte.getPuerta_direccion() + " ";
        }
        if (!parte.getMunicipio_direccion().trim().equals("") && !parte.getMunicipio_direccion().trim().equals("null")) {
            dir += "(" + parte.getMunicipio_direccion() + "-";
        }
        if (!parte.getProvincia_direccion().trim().equals("") && !parte.getProvincia_direccion().trim().equals("null")) {
            dir += parte.getProvincia_direccion() + ")";
        }
        if (!datos.getMatem_hora_entrada().trim().equals("") && !datos.getMatem_hora_entrada().trim().equals("null")) {
            horaInicio = datos.getMatem_hora_entrada();

        }
        txtHoraInicio.setText(horaInicio);
        txtDierccionTitular.setText(dir);
        txtSintomas.setText(String.valueOf(parte.getOtros_sintomas()));
        txtSintomaLista.setText(String.valueOf(parte.getSintomas()));
        etNombreTitular.setText(parte.getNombre_cliente());
        etDni.setText(parte.getDni_cliente());
        etTelefono1.setText(parte.getTelefono1_cliente());
        etTelefono2.setText(parte.getTelefono2_cliente());
        etTelefono3.setText(parte.getTelefono3_cliente());
        etTelefono4.setText(parte.getTelefono4_cliente());
        etCorreoElectronico.setText(parte.getEmail_cliente());

        etObservaciones.setText(parte.getObservaciones());

    }

    @SuppressLint("MissingPermission")
    public void llamar(String tel) {
        getContext().startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel)));
    }


    //OVERRIDE
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.tab_fragment1_cliente, container, false);
        JSONObject jsonObject;
        int idParte = 0;

        try {
            jsonObject = GestorSharedPreferences.getJsonParte(GestorSharedPreferences.getSharedPreferencesParte(getContext()));
            idParte = jsonObject.getInt("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            parte = ParteDAO.buscarPartePorId(getContext(), idParte);
            usuario = UsuarioDAO.buscarUsuarioPorFkEntidad(getContext(), parte.getFk_tecnico());
            maquina = MaquinaDAO.buscarMaquinaPorFkMaquina(getContext(), parte.getFk_maquina());
            datos = DatosAdicionalesDAO.buscarDatosAdicionalesPorFkParte(getContext(), parte.getId_parte());
            configuracion = ConfiguracionDAO.buscarTodasLasConfiguraciones(getContext()).get(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            inicializarVariables();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        darValoresVariables();
        presupuestoVisible();

        if (parte.getEstado_android() == 3 || parte.getEstado_android() == 1 || parte.getEstado_android() == 4 || parte.getEstado_android() == 436) {
            btnClienteAusente.setVisibility(View.GONE);

            btnImprimir.setVisibility(View.VISIBLE);
            txtHoraInicio.setVisibility(View.VISIBLE);

        } else if (parte.getEstado_android() == 2) {
            btnImprimir.setVisibility(View.GONE);
            btnClienteAusente.setVisibility(View.GONE);
            btnImprimir.setVisibility(View.GONE);
        } else {
            btnClienteAusente.setVisibility(View.VISIBLE);
            btnImprimir.setVisibility(View.GONE);
        }

        int estadoEjecucion = parte.getEstadoEjecucion();

        switch (estadoEjecucion){
            case 0 ://SIN INICIAR
                btnIniciarParte.setVisibility(View.VISIBLE);
                btnPausarParte.setVisibility(View.GONE);
                btnResumeParte.setVisibility(View.GONE);
                btnImprimir.setVisibility(View.GONE);
                break;
            case 1 :// INICIADO
                btnIniciarParte.setVisibility(View.GONE);
                btnPausarParte.setVisibility(View.VISIBLE);
                btnResumeParte.setVisibility(View.GONE);
                btnImprimir.setVisibility(View.GONE);
                break;
            case 2 ://PAUSADO
                btnIniciarParte.setVisibility(View.GONE);
                btnPausarParte.setVisibility(View.GONE);
                btnResumeParte.setVisibility(View.VISIBLE);
                btnImprimir.setVisibility(View.GONE);
                break;
            case 3://REINICIADO
                btnIniciarParte.setVisibility(View.GONE);
                btnPausarParte.setVisibility(View.VISIBLE);
                btnResumeParte.setVisibility(View.GONE);
                btnImprimir.setVisibility(View.GONE);
                break;
            case 4://FINALIZADO
                btnIniciarParte.setVisibility(View.GONE);
                btnPausarParte.setVisibility(View.GONE);
                btnResumeParte.setVisibility(View.GONE);
                btnImprimir.setVisibility(View.VISIBLE);
                break;
        }

        añadirElemInst();


        return vista;
    }

    public void añadirElemInst(){
        arrayListElemInst.clear();
        activity = getActivity();
        String txtNombre = "";
        String txtNumero = "";
        try {
            List <String[]> a = ElementoInstDAO.buscarTiposElementosParte(activity,parte.getId_parte());
            if (a!=null) {
                arrayListElemInst.addAll(a);
                    for (int i = 0; i < arrayListElemInst.size(); i++) {
                        txtNombre += arrayListElemInst.get(i)[1] + "\n\n";
                        txtNumero += arrayListElemInst.get(i)[2] + "\n\n";
                    }
                    if ((txtNumero != "") || (txtNumero!= null)){
                        txtNum.setText(txtNumero);
                        txtNom.setText(txtNombre);
                    }else{
                        txtNom.setVisibility(View.GONE);
                        txtNum.setVisibility(View.GONE);
                    }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == btnImagenes.getId()) {
            Intent i = new Intent(getContext(), GaleriaV2.class);
            getContext().startActivity(i);

        } else if (view.getId() == btnVerIntervenciones.getId()) {
            Intent e = new Intent(getContext(), IntervencionesAnteriores.class);
            e.putExtra("fk_maquina", parte.getFk_maquina());
            e.putExtra("fk_usuario", parte.getFk_usuario());
            startActivityForResult(e, 104);

        } else if (view.getId() == btnVerDocumentos.getId()) {


            if (hayConexion()) {

                Intent i = new Intent(getContext(), DocumentosParte.class);
                i.putExtra("fk_parte", parte.getId_parte());
                getContext().startActivity(i);
            } else {
                Dialogo.dialogoError("Es necesario estar conectado a internet", getContext());
            }

        } else if (view.getId() == R.id.btnIniciarParte) {
            iniciarParte();
        }else if (view.getId() == R.id.btnPausarParte) {
            pausarParte();
        }else if (view.getId() == R.id.btnResumeParte) {
            reiniciarParte();
        } else if (view.getId() == R.id.btnClienteAusente) {

            new HiloIniciarParte(getContext(), parte, 2, 13,this).execute();

        } else if (view.getId() == R.id.btnImprimir) {
            Bitmap bit = null;
            try {
                bit = loadFirmaTecnicoFromStorage(parte.getId_parte(), getActivity());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (bit != null) {
                if (parte.getEstado_android() != 3 && parte.getEstado_android() != 436) {

                    Toast.makeText(getContext(), R.string.imprimir_finalizados, Toast.LENGTH_SHORT).show();
                } else {
                    ((Index) getContext()).impresion();
                }
            } else {
                Toast.makeText(getContext(), "Es necesaria la firma del tecnico", Toast.LENGTH_SHORT).show();
            }

        } else if (view.getId() == R.id.ibLocation) {
            Intent i = new Intent(getContext(), Mapa.class);
            Double a = Double.parseDouble(parte.getLatitud_direccion());
            Double b = Double.parseDouble(parte.getLongitud_direccion());
            i.putExtra("destino", new double[]{a, b});
            getContext().startActivity(i);
        } else if (view.getId() == R.id.ibIr) {
            String geoUri = null;
            geoUri = "http://maps.google.com/maps?q=loc:" + parte.getLatitud_direccion() + "," + parte.getLongitud_direccion() + " (" + parte.getNombre_cliente() + ")";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
            getContext().startActivity(intent);
        } else if (view.getId() == R.id.ivLlamar1) {
            if (etTelefono1.getText().toString().equals("") || etTelefono1.getText().toString().equals("null")) {
                Dialogo.dialogoError("Movil no valido", getContext());
            } else {
                llamar(etTelefono1.getText().toString());
            }
        } else if (view.getId() == R.id.ivLlamar2) {
            if (etTelefono2.getText().toString().equals("") || etTelefono2.getText().toString().equals("null")) {
                Dialogo.dialogoError("Movil no valido", getContext());
            } else {
                llamar(etTelefono2.getText().toString());
            }
        } else if (view.getId() == R.id.ivLlamar3) {
            if (etTelefono3.getText().toString().equals("") || etTelefono3.getText().toString().equals("null")) {
                Dialogo.dialogoError("Movil no valido", getContext());
            } else {
                llamar(etTelefono3.getText().toString());
            }
        } else if (view.getId() == R.id.ivLlamar4) {
            if (etTelefono4.getText().toString().equals("") || etTelefono4.getText().toString().equals("null")) {
                Dialogo.dialogoError("Movil no valido", getContext());
            } else {
                llamar(etTelefono4.getText().toString());
            }
        } else if (view.getId() == R.id.btnAñadirPresupuesto) {
            Intent i = new Intent(getContext(), Presupuestos.class);
            i.putExtra("id_parte", parte.getId_parte());
            startActivityForResult(i,111);
        }else if (view.getId() == R.id.btnVerPresupuesto) {
            Uri uri = Uri.parse(parte.getUrl_presupuesto());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } else if (view.getId() == R.id.btnGuardarDatos) {
            guardarDatosParte();
            new HiloIniciarParte(getContext(),parte,parte.getEstado_android(),parte.getFk_estado(),this).execute();
        } else if (view.getId() == R.id.txtVerPresupuesto) {
            try {
                Cliente cliente = ClienteDAO.buscarCliente(getContext());
                String url = "http://" + cliente.getIp_cliente() + "/perso_impresiones/" + cliente.getDir_documentos() + "/presupuesto/imprimir_presupuesto_sat.php?id_presupuesto=" + parte.getFk_instalacion() + "&sendForEmail=0&tipo_imprimir=0";
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else if(view.getId() == R.id.btnCrearElemento){
            Intent i = new Intent(getContext(), FormularioCrearElemento.class);
            int fk_parte = -1;
            int fk_maquina = -1;
            fk_parte = parte.getId_parte();
            fk_maquina = parte.getFk_maquina();
            i.putExtra("fk_parte", fk_parte);
            i.putExtra("fk_maquina", fk_maquina);
            startActivity(i);
        }
    }



    private void iniciarParte(){
        if (parte.getEstado_android()==0 || parte.getEstado_android()==2){
            guardarDatosParte();
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            String formattedDate = df.format(c.getTime());
            datos.setMatem_hora_entrada(formattedDate);
            try {
                DatosAdicionalesDAO.actualizarHoraEntrada(getContext(), datos.getId_rel(), formattedDate);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            new HiloIniciarParte(getContext(), parte, 1, 2,this).execute();
        }
    }
    private void pausarParte(){

        DateFormat dtf = new SimpleDateFormat("HH:mm:ss");
        String now = dtf.format(Calendar.getInstance().getTime());

        new HiloHorasParte(getContext(), parte.getFk_tecnico(),parte.getId_parte(),now,now,parte.getFecha_visita(),2,this).execute();
    }
    private void reiniciarParte(){
        DateFormat dtf = new SimpleDateFormat("HH:mm:ss");
        String now = dtf.format(Calendar.getInstance().getTime());

        new HiloHorasParte(getContext(), parte.getFk_tecnico(),parte.getId_parte(),now,now,parte.getFecha_visita(),3,this ).execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 111){
            presupuestoVisible();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void presupuestoVisible(){
        if (parte !=null){
            try {
                parte = ParteDAO.buscarPartePorId(getContext(), parte.getId_parte());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            try {
                int idParte = GestorSharedPreferences.getJsonParte(GestorSharedPreferences.getSharedPreferencesParte(getContext())).getInt("id");
                parte = ParteDAO.buscarPartePorId(getContext(), idParte);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        if (!parte.getUrl_presupuesto().equals("")){
            btnVerPresupuesto.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onPause() {
        guardarDatosParte();
        super.onPause();
    }

    public void guardarDatosParte() {
        try {
            String observaciones = etObservaciones.getText().toString();
            String nombre = etNombreTitular.getText().toString();
            String dni = etDni.getText().toString();
            String tel1 = etTelefono1.getText().toString();
            String tel2 = etTelefono2.getText().toString();
            String tel3 = etTelefono3.getText().toString();
            String tel4 = etTelefono4.getText().toString();
            String correo = etCorreoElectronico.getText().toString();
            parte.setObservaciones(observaciones);
            parte.setNombre_cliente(nombre);
            parte.setDni_cliente(dni);
            parte.setTelefono1_cliente(tel1);
            parte.setTelefono2_cliente(tel2);
            parte.setTelefono3_cliente(tel3);
            parte.setTelefono4_cliente(tel4);
            parte.setEmail_cliente(correo);
            ParteDAO.actualizarParte(getContext(), parte.getId_parte(), nombre, dni, tel1, tel2, tel3, tel4, correo, observaciones);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hayConexion() {

        boolean connected = false;

        ConnectivityManager connec = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();

        for (NetworkInfo rede : redes) {
            // Si alguna red tiene conexión, se devuelve true
            if (rede.getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }

    public Bitmap loadFirmaTecnicoFromStorage(int id, Context context) throws SQLException {
        Bitmap b = null;
        try {
            File f = new File(Constantes.PATH, "firmaTecnico.png");
            b = BitmapFactory.decodeStream(new FileInputStream(f));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }

}