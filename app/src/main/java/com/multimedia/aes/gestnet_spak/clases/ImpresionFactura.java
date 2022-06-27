package com.multimedia.aes.gestnet_spak.clases;

import android.content.Context;

import com.multimedia.aes.gestnet_spak.dao.AnalisisDAO;
import com.multimedia.aes.gestnet_spak.dao.ArticuloDAO;
import com.multimedia.aes.gestnet_spak.dao.ArticuloParteDAO;
import com.multimedia.aes.gestnet_spak.dao.ClienteDAO;
import com.multimedia.aes.gestnet_spak.dao.DatosAdicionalesDAO;
import com.multimedia.aes.gestnet_spak.dao.FormasPagoDAO;
import com.multimedia.aes.gestnet_spak.dao.MaquinaDAO;
import com.multimedia.aes.gestnet_spak.dao.MarcaDAO;
import com.multimedia.aes.gestnet_spak.dao.ParteDAO;
import com.multimedia.aes.gestnet_spak.dao.UsuarioDAO;
import com.multimedia.aes.gestnet_spak.entidades.Analisis;
import com.multimedia.aes.gestnet_spak.entidades.Articulo;
import com.multimedia.aes.gestnet_spak.entidades.ArticuloParte;
import com.multimedia.aes.gestnet_spak.entidades.Cliente;
import com.multimedia.aes.gestnet_spak.entidades.DatosAdicionales;
import com.multimedia.aes.gestnet_spak.entidades.Maquina;
import com.multimedia.aes.gestnet_spak.entidades.Parte;
import com.multimedia.aes.gestnet_spak.entidades.Usuario;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ImpresionFactura extends Ticket {






    @Override
    public String encabezado() throws SQLException {
        String result = "\n";
        result+="--------------------------------"+"\n";
        result+="------PARTE DE TRABAJO------"+"\n";
        result+="--------------------------------"+"\n"+"\n";
        return result;
    }

    @Override
    public String cuerpo(int id, Context context) throws SQLException {

        //BAJAR TODA LA INFORMACION QUE FALTA DE LA BBDD Y GUARDARLA EN LA TABLA CORRESPONDIENTE

        Cliente cliente = ClienteDAO.buscarCliente(context);
        Usuario usuario = UsuarioDAO.buscarUsuario(context);
        Parte parte = ParteDAO.buscarPartePorId(context,id);
        DatosAdicionales datosAdicionales = DatosAdicionalesDAO.buscarDatosAdicionalesPorFkParte(context,id);


        Maquina maquina = MaquinaDAO.buscarMaquinaPorFkMaquina(context,parte.getFk_maquina());
        List<ArticuloParte> articuloPartes = ArticuloParteDAO.buscarArticuloParteFkParte(context,id);


        ArrayList<Articulo> articulos = new ArrayList<>();
        if(articuloPartes != null) {
            int [] ids = new int [articuloPartes.size()];
            for (int i = 0; i < articuloPartes.size(); i++) {
                ids[i] = articuloPartes.get(i).getFk_articulo();
            }

            for (int i = 0; i < ids.length; i++) {
                articulos.add(ArticuloDAO.buscarArticuloPorID(context,ids[i]));
            }
        }

        String result = "\n";
        result+="------DATOS DE LA EMPRESA-------"+"\n";
        String nomEmpresa = cliente.getNombre_cliente();
        result+=nomEmpresa+"\n";
        String dirEmpresa = parte.getNombre_compania();
        result+=dirEmpresa+"\n";
        String cifEmpresa = parte.getCif();
        result+="NIF/CIF: "+cifEmpresa+"\n";
        String telfEmpresa = parte.getTelefono1();
        result+="TFNO: "+telfEmpresa+"\n";
        String emailEmpresa = parte.getEmail();
        result+="EMAIL: "+emailEmpresa+"\n"+"\n";

        result+="--------DATOS DEL PARTE---------"+"\n";
        String numParte = String.valueOf(parte.getNum_parte());
        result+="Num. Parte: "+numParte+"\n";
        String fechaAvisoParte =FormatearfechaTimeStamp( parte.getFecha_aviso());
        result+="Fecha aviso: "+fechaAvisoParte+"\n";
        if(clienteId != 28){
            result+="Hora de entrada: "+datosAdicionales.getMatem_hora_entrada()+"\n";
            result+="Hora de salida: "+datosAdicionales.getMatem_hora_salida()+"\n";
        }
        String fechaIntervParte = FormatearfechaDate(parte.getFecha_visita());
        result+="Fecha intervencion: "+fechaIntervParte+"\n";
        result+="Nombre tecnico: "+usuario.getNombreUsuario()+"\n";
        if(clienteId == 28){
            result+="Detalle Avería: "+parte.getOtros_sintomas()+"\n";
        }
        result+="\n";
        result+="-------DATOS DEL CLIENTE--------"+"\n";
        String numCliente = parte.getNumero_cliente();
        result+="Num. Cliente: "+numCliente+"\n";
        String nomCliente = parte.getNombre_cliente();
        result+="Nombre: "+nomCliente+"\n";
        String telfCliente = "";
        if (!parte.getTelefono1_cliente().equals("")){
            telfCliente += parte.getTelefono1_cliente()+" ";
        }
        if (!parte.getTelefono2_cliente().equals("")){
            telfCliente += parte.getTelefono2_cliente()+" ";
        }
        if (!parte.getTelefono3_cliente().equals("")) {
            telfCliente += parte.getTelefono3_cliente()+" ";
        }
        if (!parte.getTelefono4_cliente().equals("")){
            telfCliente += parte.getTelefono4_cliente()+" ";
        }
        result+="TFNS: "+telfCliente+"\n";
        String dirCliente = "";
        if (!parte.getTipo_via().trim().equals("")&&!parte.getTipo_via().trim().equals("null")){
            dirCliente+=parte.getTipo_via()+" ";
        }
        if (!parte.getVia().trim().equals("")&&!parte.getVia().trim().equals("null")){
            dirCliente+=parte.getVia()+" ";
        }
        if (!parte.getNumero_direccion().trim().equals("")&&!parte.getNumero_direccion().trim().equals("null")){
            dirCliente+="Nº "+parte.getNumero_direccion()+" ";
        }
        if (!parte.getEscalera_direccion().trim().equals("")&&!parte.getEscalera_direccion().trim().equals("null")){
            dirCliente+="Esc. "+parte.getEscalera_direccion()+" ";
        }
        if (!parte.getPiso_direccion().trim().equals("")&&!parte.getPiso_direccion().trim().equals("null")){
            dirCliente+="Piso "+parte.getPiso_direccion()+" ";
        }
        if (!parte.getPuerta_direccion().trim().equals("")&&!parte.getPuerta_direccion().trim().equals("null")){
            dirCliente+=parte.getPuerta_direccion()+" ";
        }
        if (!parte.getMunicipio_direccion().trim().equals("")&&!parte.getMunicipio_direccion().trim().equals("null")){
            dirCliente+="("+parte.getMunicipio_direccion()+"-";
        }
        if (!parte.getProvincia_direccion().trim().equals("")&&!parte.getProvincia_direccion().trim().equals("null")){
            dirCliente+=parte.getProvincia_direccion()+")";
        }
        result+=dirCliente+"\n";
        String cifCliente = parte.getDni_cliente();
        result+="CIF/DNI: "+cifCliente+"\n"+"\n";
        result+="------DATOS DE LA MAQUINA-------"+"\n";
        String marca = "";
        if (MarcaDAO.buscarMarcaPorId(context,maquina.getFk_marca())!=null){
            marca = MarcaDAO.buscarMarcaPorId(context,maquina.getFk_marca()).getNombre_marca();
        }else{
            marca = "Sin Marca";
        }
        result+="Marca: "+marca+"\n";
        String modelo = maquina.getModelo();
        result+="Modelo: "+modelo+"\n";
        String contMant = parte.getContrato_endesa();
        result+="Ctrto. Man: "+contMant+"\n";
        String numSerie = maquina.getNum_serie();
        result+="N. Serie: "+numSerie+"\n";
        String puestaMarchaMaquina = FormatearfechaDate(maquina.getPuesta_marcha());
        if(clienteId != 28){
            result+="Puesta Marcha: "+puestaMarchaMaquina+"\n";
        }
        if(clienteId == 28){
            result+="Ubicacion: "+maquina.getUbicacion()+"\n";
        }
        result+="\n";

        ArrayList<Analisis> analisises = new ArrayList<>();
        if (AnalisisDAO.buscarAnalisisPorFkMaquinaFkParte(context, parte.getId_parte(), maquina.getId_maquina()) != null) {
            analisises.addAll(AnalisisDAO.buscarAnalisisPorFkMaquinaFkParte(context, parte.getId_parte(), maquina.getId_maquina()));
        }
        if (!analisises.isEmpty()) {
            for (int j = 0; j < analisises.size(); j++) {
                String co_amb = analisises.get(j).getCo_ambiente();
                String co_ambiente = "CO ambiente: " + co_amb + " ppm \n";
                String observaciones_tecnico = "-------ANALISIS--------" + "\n";
                String nom_med = analisises.get(j).getNombre_medicion();
                String nombre_medicion = "Tipo: " + nom_med + "\n";
                String tem_max_acs = maquina.getTemperatura_max_acs();
                String temperatura_max_acs = "T. Max. ACS: " + tem_max_acs + " ºC \n";
                String caud_acs = maquina.getCaudal_acs();
                String caudal_acs = "Caudal ACS: " + caud_acs + " l/min\n";
                String pot_uti = maquina.getPotencia_util();
                String potencia_util = "Potencia útil: " + pot_uti + " Kw\n";
                String tem_agu_ent = maquina.getTemperatura_agua_generador_calor_entrada();
                String temp_agua_entrada = "T. agua entrada: " + tem_agu_ent + " ºC \n";
                String tem_agu_sal = maquina.getTemperatura_agua_generador_calor_salida();
                String temp_agua_salida = "T. agua salida: " + tem_agu_sal + " ºC \n";
                String tem_gas_comb = analisises.get(j).getTemperatura_gases_combustion();
                String temp_gases_combust = "T. gases PDC: " + tem_gas_comb + " ºC \n";
                String rend_apar = analisises.get(j).getRendimiento_aparato();
                String rendimiento_aparato = "Rendimiento aparato: " + rend_apar + " %" + "\n";
                String co_cor = analisises.get(j).getCo_corregido();
                String co_corregido = "CO corregido: " + co_cor + " ppm \n";
                String co2_amb = analisises.get(j).getCo2_ambiente();
                String co2_ambiente = "";
                if (!co2_amb.equals("")) {
                    co2_ambiente = "CO2 ambiente: " + co2_amb + " ppm \n";
                }
                String co = analisises.get(j).getC0_maquina();
                String cO = "CO: " + co + " ppm \n";
                String tir = analisises.get(j).getTiro();
                String tiro = "Tiro: " + tir + " mbar \n";
                String c2 = analisises.get(j).getCo2();
                String co2 = "CO2: " + c2 + " % \n";
                String o02 = analisises.get(j).getO2();
                String o2 = "O2: " + o02 + " % \n";
                String lamb = analisises.get(j).getLambda();
                String lambda = "Lambda: " + lamb + "\n";
                String tmp_amb = analisises.get(j).getTemperatura_ambiente_local();
                String temperatura_Ambiente = "T. Amb.: " + tmp_amb + " ºC\n";
                String num_serie_tex = "";
                String numero_serie_texto = "Num.Serie Equip.Testo: " + "\n" + num_serie_tex + "\n";
                result += observaciones_tecnico + nombre_medicion +
                        temperatura_max_acs + caudal_acs + potencia_util + temp_agua_entrada + temp_agua_salida +
                        temp_gases_combust + co_corregido + o2 + cO + lambda + co2 + temperatura_Ambiente + tiro + rendimiento_aparato + co_ambiente +
                        co2_ambiente;
            }
        }


        result+="----------INTERVENCION----------"+"\n";
        String operacion = datosAdicionales.getOperacion_efectuada();
        result+="Operacion efectuada: "+operacion+"\n";
        String tipoInter = parte.getTipo();
        result+="Tipo Intervencion: "+tipoInter+"\n";

        String zero = "0.0";
        String duracion = parte.getDuracion();
        if(!duracion.equals(zero)){
            result+="Duracion: "+duracion+"\n";
        }

        String manoObra = String.valueOf(datosAdicionales.getPreeu_mano_de_obra_precio()*datosAdicionales.getPreeu_mano_de_obra());
        if(!manoObra.equals(zero)){
            result+="Mano Obra: "+manoObra+" €"+"\n";
        }
        String dispServi = String.valueOf(datosAdicionales.getPreeu_disposicion_servicio());
        if(!dispServi.equals(zero)){
            result+="Disp. servicio: "+dispServi+" €"+"\n";
        }

        String otros = String.valueOf(datosAdicionales.getPreeu_adicional());
        if(!otros.equals(zero)){
            result+="Otros: "+otros+" €"+"\n";
        }

        String analisiscombustion = String.valueOf(datosAdicionales.getPreeu_analisis_combustion());
        if(!analisiscombustion.equals(zero)){
            result+="Analisis de combustion: "+analisiscombustion+"\n";
        }

        String puestaMarcha = String.valueOf(datosAdicionales.getPreeu_puesta_marcha());
        if(!puestaMarcha.equals(zero)){
            result+="Puesta en marcha: "+puestaMarcha+"\n";
        }

        String servicioUrgencia = String.valueOf(datosAdicionales.getPreeu_servicio_urgencia());
        if(!servicioUrgencia.equals(zero)){
            result+="Servicio de urgencia: "+servicioUrgencia+"\n";
        }

        String desplazamiento = "("+datosAdicionales.getPreeu_km()+"KM/"+datosAdicionales.getPreeu_km_precio()+"): "+datosAdicionales.getPreeu_km_precio_total();
        if(datosAdicionales.getPreeu_km_precio_total() != 0){
            result+="Desplazamiento "+desplazamiento+"\n";
        }

        if (FormasPagoDAO.buscarFormasPagoPorId(context,datosAdicionales.getFk_forma_pago())!=null){
            String formaPago = FormasPagoDAO.buscarFormasPagoPorId(context,datosAdicionales.getFk_forma_pago()).getForma_pago();
            result+="Forma pago: "+formaPago+"\n";
        }
        double ba = (datosAdicionales.getPreeu_mano_de_obra_precio()*datosAdicionales.getPreeu_mano_de_obra())+datosAdicionales.getPreeu_disposicion_servicio()+
                datosAdicionales.getPreeu_adicional_coste()+datosAdicionales.getPreeu_adicional()+datosAdicionales.getPreeu_analisis_combustion()+datosAdicionales.getPreeu_puesta_marcha()+
                datosAdicionales.getPreeu_servicio_urgencia()+(datosAdicionales.getPreeu_km()*datosAdicionales.getPreeu_km_precio());

        if(clienteId != 28){
            result+="TOTAL INTERVENCIONES:  "+String.valueOf(ba)+"\n";
        }

        if (!articulos.isEmpty()){
            result+="\n"+"-----------MATERIALES-----------"+"\n";
            double totalArticulos = 0;
            for (Articulo art:articulos) {
                ArticuloParte articuloParte = ArticuloParteDAO.buscarArticuloPartePorFkParteFkArticulo(context, art.getId_articulo(), parte.getId_parte());
                double usados = articuloParte.getUsados();
                double coste = 0;
                if(articuloParte.getFacturar()) {
                    if (!articuloParte.getGarantia()) coste = art.getTarifa();
                    double totalArt = usados * coste;
                    totalArticulos += totalArt;
                    result += "-" + art.getNombre_articulo() + "\n";
                    result += " Uds:" + usados + " PVP:" + coste + " Total:" + totalArt + "\n" ;
                    if(articuloParte.getEntregado())result += "(pedido)"+ "\n" + "\n";
                    else result += "\n";
                }
            }
            result+="TOTAL MATERIALES: "+totalArticulos+" €"+"\n";
            if(clienteId != 28){
                result+="TOTAL INTERVENCIONES:  "+String.valueOf(ba)+" €"+"\n";
            }

            ba+=totalArticulos;
        }
        if(clienteId == 28){
            result+="\n"+"------SUMA TOTAL DEL AVISO------"+"\n";
            result+="SUMA TOTAL:  "+String.valueOf(ba)+" €"+"\n";
        }
            String base = String.valueOf(ba);
            result+="BASE IMPONIBLE: "+base+" €"+"\n";
            result+="I.V.A:"+"21.00%"+"\n";
            DecimalFormat df2 = new DecimalFormat(".##");
            double totIva = ba*0.21;
            String totalIva = String.valueOf(df2.format(totIva));
            result+="TOTAL I.V.A: "+totalIva+" €"+"\n";
            String total = String.valueOf(df2.format(totIva+ba));
            result+="TOTAL: "+total+" €"+"\n"+"\n";


        result+="\n"+"--CONFORME FINAL DEL FIRMANTE--"+"\n";
        result+="*Renuncio a presupuesto previo "+"\n"+
                "autorizando reparacion."+"\n";
        result+=""+"\n";
        result+=""+"\n";
        return result;
    }

    @Override
    public String pie(int id, Context context) {

        Parte parte;
        try {

             parte = ParteDAO.buscarPartePorId(context,id);
        }catch (Exception e){

            return e.getMessage();
        }

        String politicaPrivacidad=parte.getPoliticaPrivacidad();
        String result = "\n";
        result+="*Esta reparacion tiene una "+"\n"+
                "garantia de 3 meses. DECRETO "+"\n"+
                "139/1999 DE 7 DE MAYO: DOGAN "+"\n"+
                "No95 DE 20 DE MAYO."+"\n"+"\n"+politicaPrivacidad+"\n";



        return result;

    }




}
