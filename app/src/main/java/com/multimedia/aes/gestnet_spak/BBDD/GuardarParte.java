package com.multimedia.aes.gestnet_spak.BBDD;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.multimedia.aes.gestnet_spak.dao.ArticuloDAO;
import com.multimedia.aes.gestnet_spak.dao.ArticuloParteDAO;
import com.multimedia.aes.gestnet_spak.dao.ClienteDAO;
import com.multimedia.aes.gestnet_spak.dao.ParteDAO;
import com.multimedia.aes.gestnet_spak.entidades.Articulo;
import com.multimedia.aes.gestnet_spak.entidades.Cliente;
import com.multimedia.aes.gestnet_spak.entidades.Parte;
import com.multimedia.aes.gestnet_spak.nucleo.Index;
import com.multimedia.aes.gestnet_spak.nucleo.Login;
import com.multimedia.aes.gestnet_spak.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GuardarParte extends AsyncTask<Void,Void,Void> {

    private static String json;
    private static Context context;
    private static boolean bien=false;
    private static ArrayList<Parte> partes = new ArrayList<Parte>();;
    private static ArrayList<Articulo> articulos = new ArrayList<Articulo>();
    private ProgressDialog dialog;
    private static int estado = 0, tipoAviso = 2;
    private static Cliente c;
    private static Utils ut;

    public GuardarParte(Context context, String json,int tipoAviso) {
        this.context = context;
        this.json = json;
        this.tipoAviso = tipoAviso;
        this.ut = new Utils();
        try{
            c = ClienteDAO.buscarCliente(context);
        }catch( SQLException sqlE){
            sqlE.printStackTrace();
        }

    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setTitle("Guardando Partes.");
        dialog.setMessage("Conectando con el servidor, porfavor espere..." + "\n" + "Esto puede tardar unos minutos si la cobertura es baja.");
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        dialog.show();
        super.onPreExecute();
    }
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            guardarJsonParte();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dialog.dismiss();
        if (bien){
            new GuardarMaquina(context,json).execute();
        }else{
            if (estado==1){
                if (context.getClass()==Login.class){
                    ((Login)context).sacarMensaje("error al guardar partes");
                }else if (context.getClass()==Index.class){
                    ((Index)context).sacarMensaje("error al guardar partes");
                }

            }
        }
    }
    private static void guardarJsonParte() throws JSONException, SQLException {
        JSONObject jsonObject = new JSONObject(json);
        estado = Integer.parseInt(jsonObject.getString("estado"));
        JSONArray jsonArray = jsonObject.getJSONArray("partes");


        if (jsonArray.length()!=0){
            for (int i = 0; i < jsonArray.length(); i++) {

                int id_parte;
                if (jsonArray.getJSONObject(i).getString("id_parte").equals("null") || jsonArray.getJSONObject(i).getString("id_parte").equals("")) {
                    id_parte = -1;
                } else {
                    id_parte = jsonArray.getJSONObject(i).getInt("id_parte");
                }

                // RECOGER LOS MATERIALES QUE VENGAN EN EL PARTE Y AÑADIRLOS A LOS ARTICULOS

                // CLIMASAT Y TRENC
                if(c.getId_cliente()==28 || c.getId_cliente()==21){
                JSONArray jsonArrayArticulos = jsonArray.getJSONObject(i).getJSONArray("daitems");
                if (jsonArrayArticulos.length() != 0) {
                    for (int j = 0; j < jsonArrayArticulos.length(); j++) {
                        JSONObject materialParte = jsonArrayArticulos.getJSONObject(j);
                        int id_articulo = -1;
                        int id_item_gestnet = -1;
                        String id_item_gestnetJson = materialParte.getString("id_item");

                        if (!id_item_gestnetJson.isEmpty()) {
                            id_item_gestnet = Integer.valueOf(materialParte.getString("id_item"));
                        }

                       // Boolean bExistente = ArticuloDAO.existeArticuloPorIdItem(context, id_item_gestnet);
                        Boolean bExistente = ArticuloParteDAO.existeArticuloPartePorIdItemGestnet(context, id_item_gestnet);

                        if (!bExistente) {
                            //SI NO EXISTE YA SE CREO EL ARTÍCULO PARTE
                        int Proveedor = -1;
                        Double iva = Double.parseDouble(materialParte.getString("fk_iva"));
                        Double tarifa = Double.parseDouble(materialParte.getString("precio_tarifa"));
                        Double undItem = Double.parseDouble(materialParte.getString("unidadesITEM"));
                        Double dto = 0.0;
                        Double coste = 0.0;
                        String id_articuloJson = materialParte.getString("id_articulo");



                        if (!id_articuloJson.isEmpty()) {

                            id_articulo = Integer.valueOf(materialParte.getString("id_articulo"));

                            if (!materialParte.getString("fk_proveedor").equals("null") && !materialParte.getString("fk_proveedor").isEmpty()) {
                                Proveedor = Integer.valueOf(materialParte.getString("fk_proveedor"));
                            }
                            dto = Double.parseDouble(materialParte.getString("dto_prov"));
                            coste = Double.parseDouble(materialParte.getString("coste_actual"));
                        }

                        String imagen = materialParte.getString("articulo_imagen");
                        int bImg = 0;
                        if (!imagen.isEmpty()) {
                            bImg = 1;
                        } else {
                            bImg = 0;
                        }

                            List<Articulo> a = ArticuloDAO.buscarPorFkArticulo(context,id_articulo);
                            Articulo material = null;
                            if(a == null){
                                material = ArticuloDAO.montarArticulo(id_item_gestnet, id_articulo, materialParte.getString("nombre"), undItem, materialParte.getString("referencia"), materialParte.getString("referencia_aux"), materialParte.getString("nombre_familia"), materialParte.getString("nombre_marca"), materialParte.getString("nombre_modelo"), Proveedor, iva, tarifa, dto, coste, materialParte.getString("ean"), bImg);
                                ArticuloDAO.crearArticulo(material, context);
                            }else{
                                material = a.get(0);
                            }
                            int idArticuloCreado = material.getId_articulo();


                        ArticuloParteDAO.newArticuloParte(context, idArticuloCreado,id_parte,id_item_gestnet, undItem,false,false);
                      }

                        // GUARDAR LOS MATERIALES DEL PARTE SI HAY
                    }
                }
            }

                List<Parte> partesActuales = null;
               /* if(tipoAviso == 2){
                    partesActuales= ParteDAO.buscarTodosLosPartesPorFecha(context,ut.getDateTime());
                }else{
                    partesActuales= ParteDAO.buscarTodosLosPartes(context);
                }*/


                /*if (partesActuales!=null){
                    partes.addAll(partesActuales);
                }*/

                boolean esta = false;
                boolean estaEmpezado = false;
                Parte parteInDevice = ParteDAO.buscarPartePorId(context,id_parte);
                if(parteInDevice !=null){
                        esta = true;
                        if (parteInDevice.getFk_estado()!=2 || parteInDevice.getEstadoEjecucion() > 0) {
                            estaEmpezado = true;
                        }
                }

                /*if (partes != null) {
                    for (int j = 0; j < partes.size(); j++) {
                        if (partes.get(j).getId_parte() == id_parte) {
                            esta = true;
                            if (partes.get(j).getFk_estado()!=2 || partes.get(j).getEstadoEjecucion() > 0) {
                                estaEmpezado = true;
                            }
                            break;
                        } else {
                            estaEmpezado = false;
                            esta = false;
                        }
                    }
                }*/

                if (!estaEmpezado) {
                    int estadoEjecucion;
                    if (jsonArray.getJSONObject(i).getString("estadoEjecucion").equals("null") || jsonArray.getJSONObject(i).getString("estadoEjecucion").equals("")) {
                        estadoEjecucion =0;
                    } else {
                        estadoEjecucion = jsonArray.getJSONObject(i).getInt("estadoEjecucion");
                    }
                    int fk_user_creador;
                    if (jsonArray.getJSONObject(i).getString("fk_user_creador").equals("null") || jsonArray.getJSONObject(i).getString("fk_user_creador").equals("")) {
                        fk_user_creador = -1;
                    } else {
                        fk_user_creador = jsonArray.getJSONObject(i).getInt("fk_user_creador");
                    }
                    int fk_compania;
                    if (jsonArray.getJSONObject(i).getString("fk_compania").equals("null") || jsonArray.getJSONObject(i).getString("fk_compania").equals("")) {
                        fk_compania = -1;
                    } else {
                        fk_compania = jsonArray.getJSONObject(i).getInt("fk_compania");
                    }
                    int fk_tecnico;
                    if (jsonArray.getJSONObject(i).getString("fk_tecnico").equals("null") || jsonArray.getJSONObject(i).getString("fk_tecnico").equals("")) {
                        fk_tecnico = -1;
                    } else {
                        fk_tecnico = jsonArray.getJSONObject(i).getInt("fk_tecnico");
                    }
                    int fk_usuario;
                    if (jsonArray.getJSONObject(i).getString("fk_usuario").equals("null") || jsonArray.getJSONObject(i).getString("fk_usuario").equals("")) {
                        fk_usuario = -1;
                    } else {
                        fk_usuario = jsonArray.getJSONObject(i).getInt("fk_usuario");
                    }
                    int fk_direccion;
                    if (jsonArray.getJSONObject(i).getString("fk_direccion").equals("null") || jsonArray.getJSONObject(i).getString("fk_direccion").equals("")) {
                        fk_direccion = -1;
                    } else {
                        fk_direccion = jsonArray.getJSONObject(i).getInt("fk_direccion");
                    }
                    int fk_maquina;
                    if (jsonArray.getJSONObject(i).getString("fk_maquina").equals("null") || jsonArray.getJSONObject(i).getString("fk_maquina").equals("")) {
                        fk_maquina = -1;
                    } else {
                        fk_maquina = jsonArray.getJSONObject(i).getInt("fk_maquina");
                    }
                    String fecha_creacion;
                    if (jsonArray.getJSONObject(i).getString("fecha_creacion").equals("null")) {
                        fecha_creacion = "";
                    } else {
                        fecha_creacion = jsonArray.getJSONObject(i).getString("fecha_creacion");
                    }
                    String fecha_aviso;
                    if (jsonArray.getJSONObject(i).getString("fecha_aviso").equals("null")) {
                        fecha_aviso = "";
                    } else {
                        fecha_aviso = jsonArray.getJSONObject(i).getString("fecha_aviso");
                    }
                    String fecha_visita;
                    if (jsonArray.getJSONObject(i).getString("fecha_visita").equals("null")) {
                        fecha_visita = "";
                    } else {
                        fecha_visita = jsonArray.getJSONObject(i).getString("fecha_visita");
                    }
                    boolean visita_duplicada;
                    if (jsonArray.getJSONObject(i).getString("visita_duplicada").equals("null")) {
                        visita_duplicada = false;
                    } else {
                        if (jsonArray.getJSONObject(i).getString("visita_duplicada").equals("0")) {
                            visita_duplicada = false;
                        } else {
                            visita_duplicada = true;
                        }
                    }
                    String fecha_reparacion;
                    if (jsonArray.getJSONObject(i).getString("fecha_reparacion").equals("null")) {
                        fecha_reparacion = "";
                    } else {
                        fecha_reparacion = jsonArray.getJSONObject(i).getString("fecha_reparacion");
                    }
                    int num_parte;
                    if (jsonArray.getJSONObject(i).getString("num_parte").equals("null") || jsonArray.getJSONObject(i).getString("num_parte").equals("")) {
                        num_parte = -1;
                    } else {
                        num_parte = jsonArray.getJSONObject(i).getInt("num_parte");
                    }
                    int fk_tipo;
                    if (jsonArray.getJSONObject(i).getString("fk_tipo").equals("null") || jsonArray.getJSONObject(i).getString("fk_tipo").equals("")) {
                        fk_tipo = -1;
                    } else {
                        fk_tipo = jsonArray.getJSONObject(i).getInt("fk_tipo");
                    }
                    int fk_user_asignacion;
                    if (jsonArray.getJSONObject(i).getString("fk_user_asignacion").equals("null") || jsonArray.getJSONObject(i).getString("fk_user_asignacion").equals("")) {
                        fk_user_asignacion = -1;
                    } else {
                        fk_user_asignacion = jsonArray.getJSONObject(i).getInt("fk_user_asignacion");
                    }
                    int fk_horario;
                    if (jsonArray.getJSONObject(i).getString("fk_horario").equals("null") || jsonArray.getJSONObject(i).getString("fk_horario").equals("")) {
                        fk_horario = -1;
                    } else {
                        fk_horario = jsonArray.getJSONObject(i).getInt("fk_horario");
                    }
                    String horario;
                    if (jsonArray.getJSONObject(i).getString("horario").equals("null")) {
                        horario = "";
                    } else {
                        horario = jsonArray.getJSONObject(i).getString("horario");
                    }
                    String duracion;
                    if (jsonArray.getJSONObject(i).getString("duracion").equals("null")) {
                        duracion = "";
                    } else {
                        duracion = jsonArray.getJSONObject(i).getString("duracion");
                    }
                    String firmante;
                    if (jsonArray.getJSONObject(i).getString("firmante").equals("null")) {
                        firmante = "";
                    } else {
                        firmante = jsonArray.getJSONObject(i).getString("firmante");
                    }
                    String sobre;
                    if (jsonArray.getJSONObject(i).getString("sobre").equals("null")) {
                        sobre = "";
                    } else {
                        sobre = jsonArray.getJSONObject(i).getString("sobre");
                    }



                    String estado_parte;
                    if (jsonArray.getJSONObject(i).getString("estado").equals("null")) {
                        estado_parte = "";
                    } else {
                        estado_parte = jsonArray.getJSONObject(i).getString("estado");
                    }



                    int franja_horaria;
                    if (jsonArray.getJSONObject(i).getString("franja_horaria").equals("null") || jsonArray.getJSONObject(i).getString("franja_horaria").equals("")) {
                        franja_horaria = -1;
                    } else {
                        franja_horaria = jsonArray.getJSONObject(i).getInt("franja_horaria");
                    }
                    int fk_estado;
                    if (jsonArray.getJSONObject(i).getString("fk_estado").equals("null") || jsonArray.getJSONObject(i).getString("fk_estado").equals("")) {
                        fk_estado = -1;
                    } else {
                        fk_estado = jsonArray.getJSONObject(i).getInt("fk_estado");
                    }
                    int fk_estado_interno;
                    if (jsonArray.getJSONObject(i).getString("fk_estado_interno").equals("null") || jsonArray.getJSONObject(i).getString("fk_estado_interno").equals("")) {
                        fk_estado_interno = -1;
                    } else {
                        fk_estado_interno = jsonArray.getJSONObject(i).getInt("fk_estado_interno");
                    }
                    String observaciones;
                    if (jsonArray.getJSONObject(i).getString("observaciones").equals("null")) {
                        observaciones = "";
                    } else {
                        observaciones = jsonArray.getJSONObject(i).getString("observaciones");
                    }
                    String observacionesasignacion;
                    if (jsonArray.getJSONObject(i).getString("observacionesAsignacion").equals("null")) {
                        observacionesasignacion = "";
                    } else {
                        observacionesasignacion = jsonArray.getJSONObject(i).getString("observacionesAsignacion");
                    }
                    int confirmado;
                    if (jsonArray.getJSONObject(i).getString("confirmado").equals("null") || jsonArray.getJSONObject(i).getString("confirmado").equals("")) {
                        confirmado = -1;
                    } else {
                        confirmado = jsonArray.getJSONObject(i).getInt("confirmado");
                    }
                    String entregado_por;
                    if (jsonArray.getJSONObject(i).getString("entregado_por").equals("null")) {
                        entregado_por = "";
                    } else {
                        entregado_por = jsonArray.getJSONObject(i).getString("entregado_por");
                    }
                    String recogido_por;
                    if (jsonArray.getJSONObject(i).getString("recogido_por").equals("null")) {
                        recogido_por = "";
                    } else {
                        recogido_por = jsonArray.getJSONObject(i).getString("recogido_por");
                    }
                    String comentarios_entrega;
                    if (jsonArray.getJSONObject(i).getString("comentarios_entrega").equals("null")) {
                        comentarios_entrega = "";
                    } else {
                        comentarios_entrega = jsonArray.getJSONObject(i).getString("comentarios_entrega");
                    }
                    int fk_fabricante;
                    if (jsonArray.getJSONObject(i).getString("fk_fabricante").equals("null") || jsonArray.getJSONObject(i).getString("fk_fabricante").equals("")) {
                        fk_fabricante = -1;
                    } else {
                        fk_fabricante = jsonArray.getJSONObject(i).getInt("fk_fabricante");
                    }
                    String aprobado_fabricante;
                    if (jsonArray.getJSONObject(i).getString("aprobado_fabricante").equals("null")) {
                        aprobado_fabricante = "";
                    } else {
                        aprobado_fabricante = jsonArray.getJSONObject(i).getString("aprobado_fabricante");
                    }
                    boolean imprimir;
                    if (jsonArray.getJSONObject(i).getString("imprimir").equals("null")) {
                        imprimir = false;
                    } else {
                        if (jsonArray.getJSONObject(i).getString("imprimir").equals("0")) {
                            imprimir = false;
                        } else {
                            imprimir = true;
                        }
                    }
                    String fecha_factura;
                    if (jsonArray.getJSONObject(i).getString("fecha_factura").equals("null")) {
                        fecha_factura = "";
                    } else {
                        fecha_factura = jsonArray.getJSONObject(i).getString("fecha_factura");
                    }
                    String num_factura;
                    if (jsonArray.getJSONObject(i).getString("num_factura").equals("null")) {
                        num_factura = "";
                    } else {
                        num_factura = jsonArray.getJSONObject(i).getString("num_factura");
                    }
                    String fecha_factura_rectificativa;
                    if (jsonArray.getJSONObject(i).getString("fecha_factura_rectificativa").equals("null")) {
                        fecha_factura_rectificativa = "";
                    } else {
                        fecha_factura_rectificativa = jsonArray.getJSONObject(i).getString("fecha_factura_rectificativa");
                    }
                    String num_factura_rectificativa;
                    if (jsonArray.getJSONObject(i).getString("num_factura_rectificativa").equals("null")) {
                        num_factura_rectificativa = "";
                    } else {
                        num_factura_rectificativa = jsonArray.getJSONObject(i).getString("num_factura_rectificativa");
                    }
                    int fk_pend_fact;
                    if (jsonArray.getJSONObject(i).getString("fk_pend_fact").equals("null") || jsonArray.getJSONObject(i).getString("fk_pend_fact").equals("")) {
                        fk_pend_fact = -1;
                    } else {
                        fk_pend_fact = jsonArray.getJSONObject(i).getInt("fk_pend_fact");
                    }
                    String num_orden_endesa;
                    if (jsonArray.getJSONObject(i).getString("num_orden_endesa").equals("null")) {
                        num_orden_endesa = "";
                    } else {
                        num_orden_endesa = jsonArray.getJSONObject(i).getString("num_orden_endesa");
                    }
                    String fecha_maxima_endesa;
                    if (jsonArray.getJSONObject(i).getString("fecha_maxima_endesa").equals("null")) {
                        fecha_maxima_endesa = "";
                    } else {
                        fecha_maxima_endesa = jsonArray.getJSONObject(i).getString("fecha_maxima_endesa");
                    }
                    int fk_estado_endesa;
                    if (jsonArray.getJSONObject(i).getString("fk_estado_endesa").equals("null") || jsonArray.getJSONObject(i).getString("fk_estado_endesa").equals("")) {
                        fk_estado_endesa = -1;
                    } else {
                        fk_estado_endesa = jsonArray.getJSONObject(i).getInt("fk_estado_endesa");
                    }
                    int insistencia_endesa;
                    if (jsonArray.getJSONObject(i).getString("insistencia_endesa").equals("null") || jsonArray.getJSONObject(i).getString("insistencia_endesa").equals("")) {
                        insistencia_endesa = -1;
                    } else {
                        insistencia_endesa = jsonArray.getJSONObject(i).getInt("insistencia_endesa");
                    }
                    String contrato_endesa;
                    if (jsonArray.getJSONObject(i).getString("contrato_endesa").equals("null")) {
                        contrato_endesa = "";
                    } else {
                        contrato_endesa = jsonArray.getJSONObject(i).getString("contrato_endesa");
                    }
                    String producto_endesa;
                    if (jsonArray.getJSONObject(i).getString("producto_endesa").equals("null")) {
                        producto_endesa = "";
                    } else {
                        producto_endesa = jsonArray.getJSONObject(i).getString("producto_endesa");
                    }
                    int fk_tipo_os;
                    if (jsonArray.getJSONObject(i).getString("fk_tipo_os").equals("null") || jsonArray.getJSONObject(i).getString("fk_tipo_os").equals("")) {
                        fk_tipo_os = -1;
                    } else {
                        fk_tipo_os = jsonArray.getJSONObject(i).getInt("fk_tipo_os");
                    }
                    int fk_tipo_producto;
                    if (jsonArray.getJSONObject(i).getString("fk_tipo_producto").equals("null") || jsonArray.getJSONObject(i).getString("fk_tipo_producto").equals("")) {
                        fk_tipo_producto = -1;
                    } else {
                        fk_tipo_producto = jsonArray.getJSONObject(i).getInt("fk_tipo_producto");
                    }
                    boolean pagado_endesa;
                    if (jsonArray.getJSONObject(i).getString("pagado_endesa").equals("null")) {
                        pagado_endesa = false;
                    } else {
                        if (jsonArray.getJSONObject(i).getString("pagado_endesa").equals("0")) {
                            pagado_endesa = false;
                        } else {
                            pagado_endesa = true;
                        }
                    }
                    String ciclo_liq_endesa;
                    if (jsonArray.getJSONObject(i).getString("ciclo_liq_endesa").equals("null")) {
                        ciclo_liq_endesa = "";
                    } else {
                        ciclo_liq_endesa = jsonArray.getJSONObject(i).getString("ciclo_liq_endesa");
                    }
                    double importe_pago_endesa;
                    if (jsonArray.getJSONObject(i).getString("importe_pago_endesa").equals("null") || jsonArray.getJSONObject(i).getString("importe_pago_endesa").equals("")) {
                        importe_pago_endesa = -1;
                    } else {
                        importe_pago_endesa = jsonArray.getJSONObject(i).getDouble("importe_pago_endesa");
                    }
                    String fecha_pagado_endesa;
                    if (jsonArray.getJSONObject(i).getString("fecha_pagado_endesa").equals("null")) {
                        fecha_pagado_endesa = "";
                    } else {
                        fecha_pagado_endesa = jsonArray.getJSONObject(i).getString("fecha_pagado_endesa");
                    }
                    boolean pagado_operario;
                    if (jsonArray.getJSONObject(i).getString("pagado_operario").equals("null")) {
                        pagado_operario = false;
                    } else {
                        if (jsonArray.getJSONObject(i).getString("pagado_operario").equals("0")) {
                            pagado_operario = false;
                        } else {
                            pagado_operario = true;
                        }
                    }
                    String fecha_anulado;
                    if (jsonArray.getJSONObject(i).getString("fecha_anulado").equals("null")) {
                        fecha_anulado = "";
                    } else {
                        fecha_anulado = jsonArray.getJSONObject(i).getString("fecha_anulado");
                    }
                    String fecha_modificacion_tecnico;
                    if (jsonArray.getJSONObject(i).getString("fecha_modificacion_tecnico").equals("null")) {
                        fecha_modificacion_tecnico = "";
                    } else {
                        fecha_modificacion_tecnico = jsonArray.getJSONObject(i).getString("fecha_modificacion_tecnico");
                    }
                    int fk_remoto_central;
                    if (jsonArray.getJSONObject(i).getString("fk_remoto_central").equals("null") || jsonArray.getJSONObject(i).getString("fk_remoto_central").equals("")) {
                        fk_remoto_central = -1;
                    } else {
                        fk_remoto_central = jsonArray.getJSONObject(i).getInt("fk_remoto_central");
                    }
                    String fac_nombre;
                    if (jsonArray.getJSONObject(i).getString("fac_nombre").equals("null")) {
                        fac_nombre = "";
                    } else {
                        fac_nombre = jsonArray.getJSONObject(i).getString("fac_nombre");
                    }
                    String fac_direccion;
                    if (jsonArray.getJSONObject(i).getString("fac_direccion").equals("null")) {
                        fac_direccion = "";
                    } else {
                        fac_direccion = jsonArray.getJSONObject(i).getString("fac_direccion");
                    }
                    String fac_cp;
                    if (jsonArray.getJSONObject(i).getString("fac_cp").equals("null")) {
                        fac_cp = "";
                    } else {
                        fac_cp = jsonArray.getJSONObject(i).getString("fac_cp");
                    }
                    String fac_poblacion;
                    if (jsonArray.getJSONObject(i).getString("fac_poblacion").equals("null")) {
                        fac_poblacion = "";
                    } else {
                        fac_poblacion = jsonArray.getJSONObject(i).getString("fac_poblacion");
                    }
                    String fac_provincia;
                    if (jsonArray.getJSONObject(i).getString("fac_provincia").equals("null")) {
                        fac_provincia = "";
                    } else {
                        fac_provincia = jsonArray.getJSONObject(i).getString("fac_provincia");
                    }
                    String fac_dni;
                    if (jsonArray.getJSONObject(i).getString("fac_DNI").equals("null")) {
                        fac_dni = "";
                    } else {
                        fac_dni = jsonArray.getJSONObject(i).getString("fac_DNI");
                    }
                    String fac_email;
                    if (jsonArray.getJSONObject(i).getString("fac_email").equals("null")) {
                        fac_email = "";
                    } else {
                        fac_email = jsonArray.getJSONObject(i).getString("fac_email");
                    }
                    String fac_telefonos;
                    if (jsonArray.getJSONObject(i).getString("fac_telefonos").equals("null")) {
                        fac_telefonos = "";
                    } else {
                        fac_telefonos = jsonArray.getJSONObject(i).getString("fac_telefonos");
                    }
                    String otros_sintomas;
                    if (jsonArray.getJSONObject(i).getString("otros_sintomas").equals("null")) {
                        otros_sintomas = "";
                    } else {
                        otros_sintomas = jsonArray.getJSONObject(i).getString("otros_sintomas");
                    }
                    String fecha_baja;
                    if (jsonArray.getJSONObject(i).getString("fecha_baja").equals("null")) {
                        fecha_baja = "";
                    } else {
                        fecha_baja = jsonArray.getJSONObject(i).getString("fecha_baja");
                    }
                    boolean fac_baja_stock;
                    if (jsonArray.getJSONObject(i).getString("fac_baja_stock").equals("null")) {
                        fac_baja_stock = false;
                    } else {
                        if (jsonArray.getJSONObject(i).getString("fac_baja_stock").equals("0")) {
                            fac_baja_stock = false;
                        } else {
                            fac_baja_stock = true;
                        }
                    }
                    int estado_android;
                    if (jsonArray.getJSONObject(i).getString("estado_android").equals("null")||jsonArray.getJSONObject(i).getString("estado_android").equals("")) {
                        estado_android = -1;
                    } else {
                       estado_android = jsonArray.getJSONObject(i).getInt("estado_android");
                    }
                    boolean urgencias;
                    if (jsonArray.getJSONObject(i).getString("urgencias").equals("null")) {
                        urgencias = false;
                    } else {
                        if (jsonArray.getJSONObject(i).getString("urgencias").equals("0")) {
                            urgencias = false;
                        } else {
                            urgencias = true;
                        }
                    }
                    String lote;
                    if (jsonArray.getJSONObject(i).getString("lote").equals("null")) {
                        lote = "";
                    } else {
                        lote = jsonArray.getJSONObject(i).getString("lote");
                    }
                    boolean validar;
                    if (jsonArray.getJSONObject(i).getString("validar").equals("null")) {
                        validar = false;
                    } else {
                        if (jsonArray.getJSONObject(i).getString("validar").equals("0")) {
                            validar = false;
                        } else {
                            validar = true;
                        }
                    }
                    boolean liquidado_a_proveedor;
                    if (jsonArray.getJSONObject(i).getString("liquidado_a_proveedor").equals("null")) {
                        liquidado_a_proveedor = false;
                    } else {
                        if (jsonArray.getJSONObject(i).getString("liquidado_a_proveedor").equals("0")) {
                            liquidado_a_proveedor = false;
                        } else {
                            liquidado_a_proveedor = true;
                        }
                    }
                    int fk_instalacion;
                    if (jsonArray.getJSONObject(i).getString("fk_instalacion").equals("null") || jsonArray.getJSONObject(i).getString("fk_instalacion").equals("")) {
                        fk_instalacion = -1;
                    } else {
                        fk_instalacion = jsonArray.getJSONObject(i).getInt("fk_instalacion");
                    }
                    int fk_emergencia;
                    if (jsonArray.getJSONObject(i).getString("fk_emergencia").equals("null") || jsonArray.getJSONObject(i).getString("fk_emergencia").equals("")) {
                        fk_emergencia = -1;
                    } else {
                        fk_emergencia = jsonArray.getJSONObject(i).getInt("fk_emergencia");
                    }
                    String motivo_cambio_fecha_maxima;
                    if (jsonArray.getJSONObject(i).getString("motivo_cambio_fecha_maxima").equals("null")) {
                        motivo_cambio_fecha_maxima = "";
                    } else {
                        motivo_cambio_fecha_maxima = jsonArray.getJSONObject(i).getString("motivo_cambio_fecha_maxima");
                    }
                    boolean btodoslosequipos;
                    if (jsonArray.getJSONObject(i).getString("bTodosLosEquipos").equals("null")) {
                        btodoslosequipos = false;
                    } else {
                        if (jsonArray.getJSONObject(i).getString("bTodosLosEquipos").equals("0")) {
                            btodoslosequipos = false;
                        } else {
                            btodoslosequipos = true;
                        }
                    }
                    int fk_tipo_instalacion;
                    if (jsonArray.getJSONObject(i).getString("fk_tipo_instalacion").equals("null") || jsonArray.getJSONObject(i).getString("fk_tipo_instalacion").equals("")) {
                        fk_tipo_instalacion = -1;
                    } else {
                        fk_tipo_instalacion = jsonArray.getJSONObject(i).getInt("fk_tipo_instalacion");
                    }
                    boolean parte_finalizado_android;
                    if (jsonArray.getJSONObject(i).getString("parte_finalizado_android").equals("null")) {
                        parte_finalizado_android = false;
                    } else {
                        if (jsonArray.getJSONObject(i).getString("parte_finalizado_android").equals("0")) {
                            parte_finalizado_android = false;
                        } else {
                            parte_finalizado_android = true;
                        }
                    }
                    String comercializadora;
                    if (jsonArray.getJSONObject(i).getString("comercializadora").equals("null")) {
                        comercializadora = "";
                    } else {
                        comercializadora = jsonArray.getJSONObject(i).getString("comercializadora");
                    }
                    String persona_contacto;
                    if (jsonArray.getJSONObject(i).getString("persona_contacto").equals("null")) {
                        persona_contacto = "";
                    } else {
                        persona_contacto = jsonArray.getJSONObject(i).getString("persona_contacto");
                    }
                    String tel_contacto;
                    if (jsonArray.getJSONObject(i).getString("tel_contacto").equals("null")) {
                        tel_contacto = "";
                    } else {
                        tel_contacto = jsonArray.getJSONObject(i).getString("tel_contacto");
                    }
                    String cnae;
                    if (jsonArray.getJSONObject(i).getString("CNAE").equals("null")) {
                        cnae = "";
                    } else {
                        cnae = jsonArray.getJSONObject(i).getString("CNAE");
                    }
                    int fk_compania_parte;
                    if (jsonArray.getJSONObject(i).getString("fk_compania_parte").equals("null") || jsonArray.getJSONObject(i).getString("fk_compania_parte").equals("")) {
                        fk_compania_parte = -1;
                    } else {
                        fk_compania_parte = jsonArray.getJSONObject(i).getInt("fk_compania_parte");
                    }
                    String fecha_cierre;
                    if (jsonArray.getJSONObject(i).getString("fecha_cierre").equals("null")) {
                        fecha_cierre = "";
                    } else {
                        fecha_cierre = jsonArray.getJSONObject(i).getString("fecha_cierre");
                    }
                    String num_presupuesto;
                    if (jsonArray.getJSONObject(i).getString("num_presupuesto").equals("null")) {
                        num_presupuesto = "";
                    } else {
                        num_presupuesto = jsonArray.getJSONObject(i).getString("num_presupuesto");
                    }
                    String defectos;
                    if (jsonArray.getJSONObject(i).getString("defectos").equals("null")) {
                        defectos = "";
                    } else {
                        defectos = jsonArray.getJSONObject(i).getString("defectos");
                    }
                    int fk_periocidad;
                    if (jsonArray.getJSONObject(i).getString("fk_periocidad").equals("null") || jsonArray.getJSONObject(i).getString("fk_periocidad").equals("")) {
                        fk_periocidad = -1;
                    } else {
                        fk_periocidad = jsonArray.getJSONObject(i).getInt("fk_periocidad");
                    }
                    double franquicia;
                    if (jsonArray.getJSONObject(i).getString("franquicia").equals("null") || jsonArray.getJSONObject(i).getString("franquicia").equals("")) {
                        franquicia = -1;
                    } else {
                        franquicia = jsonArray.getJSONObject(i).getInt("franquicia");
                    }
                    String inspeccion_visual;
                    if (jsonArray.getJSONObject(i).getString("inspeccion_visual").equals("null")) {
                        inspeccion_visual = "";
                    } else {
                        inspeccion_visual = jsonArray.getJSONObject(i).getString("inspeccion_visual");
                    }
                    String otros_mataux;
                    if (jsonArray.getJSONObject(i).getString("otros_matAux").equals("null")) {
                        otros_mataux = "";
                    } else {
                        otros_mataux = jsonArray.getJSONObject(i).getString("otros_matAux");
                    }
                    boolean binspeccionvisual;
                    if (jsonArray.getJSONObject(i).getString("bInspeccionVisual").equals("null")) {
                        binspeccionvisual = false;
                    } else {
                        if (jsonArray.getJSONObject(i).getString("bInspeccionVisual").equals("0")) {
                            binspeccionvisual = false;
                        } else {
                            binspeccionvisual = true;
                        }
                    }
                    boolean botrosmataux;
                    if (jsonArray.getJSONObject(i).getString("bOtrosMatAux").equals("null")) {
                        botrosmataux = false;
                    } else {
                        if (jsonArray.getJSONObject(i).getString("bOtrosMatAux").equals("0")) {
                            botrosmataux = false;
                        } else {
                            botrosmataux = true;
                        }
                    }

                    String tipo_via;
                    if (jsonArray.getJSONObject(i).getJSONObject("direccion").getString("via").equals("null")) {
                        tipo_via = "";
                    } else {
                        tipo_via = jsonArray.getJSONObject(i).getJSONObject("direccion").getString("via");
                    }
                    String via;
                    if (jsonArray.getJSONObject(i).getJSONObject("direccion").getString("direccion").equals("null")) {
                        via = "";
                    } else {
                        via = jsonArray.getJSONObject(i).getJSONObject("direccion").getString("direccion");
                    }
                    String numero_direccion;
                    if (jsonArray.getJSONObject(i).getJSONObject("direccion").getString("numero").equals("null")) {
                        numero_direccion = "";
                    } else {
                        numero_direccion = jsonArray.getJSONObject(i).getJSONObject("direccion").getString("numero");
                    }
                    String escalera_direccion;
                    if (jsonArray.getJSONObject(i).getJSONObject("direccion").getString("escalera").equals("null")) {
                        escalera_direccion = "";
                    } else {
                        escalera_direccion = jsonArray.getJSONObject(i).getJSONObject("direccion").getString("escalera");
                    }
                    String piso_direccion;
                    if (jsonArray.getJSONObject(i).getJSONObject("direccion").getString("piso").equals("null")) {
                        piso_direccion = "";
                    } else {
                        piso_direccion = jsonArray.getJSONObject(i).getJSONObject("direccion").getString("piso");
                    }
                    String puerta_direccion;
                    if (jsonArray.getJSONObject(i).getJSONObject("direccion").getString("puerta").equals("null")) {
                        puerta_direccion = "";
                    } else {
                        puerta_direccion = jsonArray.getJSONObject(i).getJSONObject("direccion").getString("puerta");
                    }
                    String cp_direccion;
                    if (jsonArray.getJSONObject(i).getJSONObject("direccion").getString("cp").equals("null")) {
                        cp_direccion = "";
                    } else {
                        cp_direccion = jsonArray.getJSONObject(i).getJSONObject("direccion").getString("cp");
                    }
                    String municipio_direccion="";
                    if (jsonArray.getJSONObject(i).getJSONObject("direccion").getString("municipio").equals("null")) {
                        municipio_direccion = "";
                    } else {
                        municipio_direccion = jsonArray.getJSONObject(i).getJSONObject("direccion").getString("municipio");
                    }
                    String provincia_direccion="";
                    if (jsonArray.getJSONObject(i).getJSONObject("direccion").getString("provincia").equals("null")) {
                        provincia_direccion = "";
                    } else {
                        provincia_direccion = jsonArray.getJSONObject(i).getJSONObject("direccion").getString("provincia");
                    }
                    String latitud_direccion;
                    if (jsonArray.getJSONObject(i).getJSONObject("direccion").getString("lat").equals("null")) {
                        latitud_direccion = "";
                    } else {
                        latitud_direccion = jsonArray.getJSONObject(i).getJSONObject("direccion").getString("lat");
                    }
                    String longitud_direccion;
                    if (jsonArray.getJSONObject(i).getJSONObject("direccion").getString("lng").equals("null")) {
                        longitud_direccion = "";
                    } else {
                        longitud_direccion = jsonArray.getJSONObject(i).getJSONObject("direccion").getString("lng");
                    }
                    String nombre_cliente;
                    if (jsonArray.getJSONObject(i).getJSONObject("cliente").getString("nombre_usuario").equals("null")) {
                        nombre_cliente = "";
                    } else {
                        nombre_cliente = jsonArray.getJSONObject(i).getJSONObject("cliente").getString("nombre_usuario");
                    }

                    String dni_cliente;
                    if (jsonArray.getJSONObject(i).getJSONObject("cliente").getString("DNI").equals("null")) {
                        dni_cliente = "";
                    } else {
                        dni_cliente = jsonArray.getJSONObject(i).getJSONObject("cliente").getString("DNI");
                    }
                    String telefono1_cliente;
                    if (jsonArray.getJSONObject(i).getJSONObject("cliente").getString("telefono1").equals("null")) {
                        telefono1_cliente = "";
                    } else {
                        telefono1_cliente = jsonArray.getJSONObject(i).getJSONObject("cliente").getString("telefono1");
                    }
                    String telefono2_cliente;
                    if (jsonArray.getJSONObject(i).getJSONObject("cliente").getString("telefono2").equals("null")) {
                        telefono2_cliente = "";
                    } else {
                        telefono2_cliente = jsonArray.getJSONObject(i).getJSONObject("cliente").getString("telefono2");
                    }
                    String telefono3_cliente;
                    if (jsonArray.getJSONObject(i).getJSONObject("cliente").getString("tel_contacto").equals("null")) {
                        telefono3_cliente = "";
                    } else {
                        telefono3_cliente = jsonArray.getJSONObject(i).getJSONObject("cliente").getString("tel_contacto");
                    }
                    String telefono4_cliente;
                    if (jsonArray.getJSONObject(i).getJSONObject("cliente").getString("otros_telefonos").equals("null")) {
                        telefono4_cliente = "";
                    } else {
                        telefono4_cliente = jsonArray.getJSONObject(i).getJSONObject("cliente").getString("otros_telefonos");
                    }
                    String email_cliente;
                    if (jsonArray.getJSONObject(i).getJSONObject("cliente").getString("email").equals("null")) {
                        email_cliente = "";
                    } else {
                        email_cliente = jsonArray.getJSONObject(i).getJSONObject("cliente").getString("email");
                    }

                    String observaciones_cliente;
                    if (jsonArray.getJSONObject(i).getJSONObject("cliente").getString("observaciones").equals("null")) {
                        observaciones_cliente = "";
                    } else {
                        observaciones_cliente = jsonArray.getJSONObject(i).getJSONObject("cliente").getString("observaciones");
                    }

                    String numero_cliente;
                    if (jsonArray.getJSONObject(i).getJSONObject("cliente").getString("numero_usuario").equals("null")) {
                        numero_cliente = "";
                    } else {

                        numero_cliente = jsonArray.getJSONObject(i).getJSONObject("cliente").getString("numero_usuario");
                    }

                    String user_creador;
                    if (jsonArray.getJSONObject(i).getString("userCreador").equals("null")) {
                        user_creador = "";
                    } else {
                        user_creador = jsonArray.getJSONObject(i).getString("userCreador");
                    }

                    String tipo;
                    if (jsonArray.getJSONObject(i).getString("tipo").equals("null")) {
                        tipo = "";
                    } else {
                        tipo = jsonArray.getJSONObject(i).getString("tipo");
                    }

                    String dni_firmante="";
                    String firma64="";
                    String ticket="";
                    String nombre_compania;
                    if (jsonArray.getJSONObject(i).getJSONObject("empresa").getString("nombre_compania").equals("null")) {
                        nombre_compania = "";
                    } else {
                        nombre_compania = jsonArray.getJSONObject(i).getJSONObject("empresa").getString("nombre_compania");
                    }
                    String direccion;
                    if (jsonArray.getJSONObject(i).getJSONObject("empresa").getString("direccion").equals("null")) {
                        direccion = "";
                    } else {
                        direccion = jsonArray.getJSONObject(i).getJSONObject("empresa").getString("direccion");
                    }
                    String CIF;
                    if (jsonArray.getJSONObject(i).getJSONObject("empresa").getString("CIF").equals("null")) {
                        CIF = "";
                    } else {
                        CIF = jsonArray.getJSONObject(i).getJSONObject("empresa").getString("CIF");
                    }
                    String telefono1;
                    if (jsonArray.getJSONObject(i).getJSONObject("empresa").getString("telefono1").equals("null")) {
                        telefono1 = "";
                    } else {
                        telefono1 = jsonArray.getJSONObject(i).getJSONObject("empresa").getString("telefono1");
                    }
                    String telefono2;
                    if (jsonArray.getJSONObject(i).getJSONObject("empresa").getString("telefono2").equals("null")) {
                        telefono2 = "";
                    } else {
                        telefono2 = jsonArray.getJSONObject(i).getJSONObject("empresa").getString("telefono2");
                    }
                    String email;
                    if (jsonArray.getJSONObject(i).getJSONObject("empresa").getString("email").equals("null")) {
                        email = "";
                    } else {
                        email = jsonArray.getJSONObject(i).getJSONObject("empresa").getString("email");
                    }
                    String politicaPrivacidad;
                    if (jsonArray.getJSONObject(i).getJSONObject("empresa").getString("politicaPrivacidad").equals("null")) {
                        politicaPrivacidad = "";
                    } else {
                        politicaPrivacidad = jsonArray.getJSONObject(i).getJSONObject("empresa").getString("politicaPrivacidad");
                    }

                    String sintomas = "";
                    if (jsonArray.getJSONObject(i).getJSONArray("sintomas").length() == 0 ) {
                        sintomas = "";
                    } else {
                        JSONArray aux = jsonArray.getJSONObject(i).getJSONArray("sintomas");
                        for (int j = 0; j < aux.length(); j++) {
                            String sintoma = aux.getJSONObject(j).getString("nombre_sintoma") + "\n";
                            sintomas += sintoma;
                        }
                       // sintomas = jsonArray.getJSONObject(i).getString("sintomas");
                    }
                    String url_presupuesto ="";
                    if (esta){
                        ParteDAO.actualizarParte(context, id_parte, fk_user_creador, fk_compania, fk_tecnico, fk_usuario,
                                fk_direccion, fk_maquina, fecha_creacion, fecha_aviso,
                                fecha_visita, visita_duplicada, fecha_reparacion, num_parte,
                                fk_tipo, fk_user_asignacion, fk_horario, horario, duracion,
                                firmante, sobre, franja_horaria, fk_estado, fk_estado_interno,
                                observaciones, observacionesasignacion, confirmado, entregado_por,
                                recogido_por, comentarios_entrega, fk_fabricante, aprobado_fabricante,
                                imprimir, fecha_factura, num_factura, fecha_factura_rectificativa,
                                num_factura_rectificativa, fk_pend_fact, num_orden_endesa,
                                fecha_maxima_endesa, fk_estado_endesa, insistencia_endesa,
                                contrato_endesa, producto_endesa, fk_tipo_os, fk_tipo_producto,
                                pagado_endesa, ciclo_liq_endesa, importe_pago_endesa,
                                fecha_pagado_endesa, pagado_operario, fecha_anulado,
                                fecha_modificacion_tecnico, fk_remoto_central, fac_nombre,
                                fac_direccion, fac_cp, fac_poblacion, fac_provincia,
                                fac_dni, fac_email, fac_telefonos, otros_sintomas,
                                fecha_baja, fac_baja_stock, estado_android, urgencias,
                                lote, validar, liquidado_a_proveedor, fk_instalacion,
                                fk_emergencia, motivo_cambio_fecha_maxima, btodoslosequipos,
                                fk_tipo_instalacion, parte_finalizado_android, comercializadora,
                                persona_contacto, tel_contacto, cnae, fk_compania_parte,
                                fecha_cierre, num_presupuesto, defectos, fk_periocidad,
                                franquicia, inspeccion_visual, otros_mataux, binspeccionvisual,
                                botrosmataux, tipo_via, via, numero_direccion,
                                escalera_direccion, piso_direccion, puerta_direccion,
                                cp_direccion, municipio_direccion, provincia_direccion,
                                latitud_direccion, longitud_direccion, nombre_cliente,
                                dni_cliente, telefono1_cliente, telefono2_cliente,
                                telefono3_cliente, telefono4_cliente, email_cliente,
                                observaciones_cliente,user_creador,tipo,dni_firmante,firma64,
                                ticket,numero_cliente,estado_parte,url_presupuesto);
                        ParteDAO.actualizarEstadoEjecucion(context,id_parte,estadoEjecucion);
                        bien=true;
                    }else{
                        if (ParteDAO.newParte(context, id_parte, fk_user_creador, fk_compania, fk_tecnico, fk_usuario,
                                fk_direccion, fk_maquina, fecha_creacion, fecha_aviso,
                                fecha_visita, visita_duplicada, fecha_reparacion, num_parte,
                                fk_tipo, fk_user_asignacion, fk_horario, horario, duracion,
                                firmante, sobre, franja_horaria, fk_estado, fk_estado_interno,
                                observaciones, observacionesasignacion, confirmado, entregado_por,
                                recogido_por, comentarios_entrega, fk_fabricante, aprobado_fabricante,
                                imprimir, fecha_factura, num_factura, fecha_factura_rectificativa,
                                num_factura_rectificativa, fk_pend_fact, num_orden_endesa,
                                fecha_maxima_endesa, fk_estado_endesa, insistencia_endesa,
                                contrato_endesa, producto_endesa, fk_tipo_os, fk_tipo_producto,
                                pagado_endesa, ciclo_liq_endesa, importe_pago_endesa,
                                fecha_pagado_endesa, pagado_operario, fecha_anulado,
                                fecha_modificacion_tecnico, fk_remoto_central, fac_nombre,
                                fac_direccion, fac_cp, fac_poblacion, fac_provincia,
                                fac_dni, fac_email, fac_telefonos, otros_sintomas,
                                fecha_baja, fac_baja_stock, estado_android, urgencias,
                                lote, validar, liquidado_a_proveedor, fk_instalacion,
                                fk_emergencia, motivo_cambio_fecha_maxima, btodoslosequipos,
                                fk_tipo_instalacion, parte_finalizado_android, comercializadora,
                                persona_contacto, tel_contacto, cnae, fk_compania_parte,
                                fecha_cierre, num_presupuesto, defectos, fk_periocidad,
                                franquicia, inspeccion_visual, otros_mataux, binspeccionvisual,
                                botrosmataux, tipo_via, via, numero_direccion,
                                escalera_direccion, piso_direccion, puerta_direccion,
                                cp_direccion, municipio_direccion, provincia_direccion,
                                latitud_direccion, longitud_direccion, nombre_cliente,
                                dni_cliente, telefono1_cliente, telefono2_cliente,
                                telefono3_cliente, telefono4_cliente, email_cliente,
                                observaciones_cliente,user_creador,tipo,dni_firmante,firma64,
                                ticket,nombre_compania,direccion,CIF,telefono1,telefono2,email,
                                sintomas,politicaPrivacidad,numero_cliente,estado_parte,url_presupuesto)) {
                            ParteDAO.actualizarEstadoEjecucion(context,id_parte,estadoEjecucion);
                            bien = true;
                        } else {
                            bien = false;
                        }
                    }

                }
                partes.clear();
            }
        }else{
            bien = true;
        }
    }
}
