package com.multimedia.aes.gestnet_spak.dao;

import android.content.Context;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.multimedia.aes.gestnet_spak.dbhelper.DBHelperMOS;
import com.multimedia.aes.gestnet_spak.entidades.TipoElementos;

import org.json.JSONArray;
import org.json.JSONException;

import java.sql.SQLException;
import java.util.List;

public class TipoElementosDAO extends DBHelperMOS{
    static Dao<TipoElementos, Integer> dao;

    public static void cargarDao(Context context) throws SQLException {
        dao = getHelper(context).getTipoElementosDAO();
    }

    //__________FUNCIONES DE CREACIÓN________________________//
    public static boolean newTipoElemento(Context context, String tipo, String tabla, String campo_id, String campos, int fk_tipo) {
        TipoElementos e = montarTipoElemento(tipo, tabla,campo_id,campos,fk_tipo);
        return crearTipoElemento(e, context);
    }

    public static boolean crearTipoElemento(TipoElementos m, Context context) {
        try {
            cargarDao(context);
            dao.create(m);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static TipoElementos montarTipoElemento( String tipo, String tabla, String campo_id, String campos, int fk_tipo) {
        TipoElementos e = new TipoElementos(tipo,tabla,campo_id,campos, fk_tipo);
        return e;
    }

    //__________FUNCIONES DE BUSQUEDA______________________//
    public static List<TipoElementos> buscarTodos(Context context) throws SQLException {
        cargarDao(context);
        List<TipoElementos> listadoTipoElementos = dao.queryForAll();
        if (listadoTipoElementos.isEmpty()) {
            return null;
        } else {
            return listadoTipoElementos;
        }
    }

    public static TipoElementos buscarTiposElementoPorFkTipo(Context context, int fk_tipo) throws SQLException {
        cargarDao(context);
        List <TipoElementos> listadoTiposElementos = dao.queryForEq(TipoElementos.FK_TIPO, fk_tipo);
        if (listadoTiposElementos.isEmpty()) {
            return null;
        } else {
            return listadoTiposElementos.get(0);
        }
    }


    public static String buscarCampoOrdenporFkTipo(Context context, int fk_tipo) throws SQLException, JSONException {
        cargarDao(context);
        List <TipoElementos> listadoTiposElementos = dao.queryForEq(TipoElementos.ID, fk_tipo);
        boolean encontrado = false;
        int i=0;
        String nombreOrden = "";
        String p = listadoTiposElementos.get(i).getCamposElemento();
        JSONArray jsonArray = new JSONArray(p);
        for(i=0;i<jsonArray.length();i++){
            String condicion = jsonArray.getJSONObject(i).getString("bCampoOrden");
            if(Integer.parseInt(condicion)==1){
                nombreOrden = jsonArray.getJSONObject(i).getString("campo");
                encontrado = true;
            }
        }
           if(encontrado){
               return nombreOrden;
           }else{
               return "";
           }

    }

    public static String buscarTablaporFkTipo(Context context, int fk_tipo) throws SQLException, JSONException {
        cargarDao(context);
        List <TipoElementos> listadoTiposElementos = dao.queryForEq(TipoElementos.ID, fk_tipo);
        boolean encontrado = false;
        int i=0;
        String tabla = listadoTiposElementos.get(i).getTablaElemento();

        return tabla;

    }

    public static String buscadorPorFkTipo(Context context, int fk_tipo) throws SQLException {
        cargarDao(context);
        List <TipoElementos> listadoTiposElementos = dao.queryForEq(TipoElementos.ID, fk_tipo);
        boolean encontrado = false;
        int i=0;
        String nombreOrden = "";
        do{
            try {
                String p = listadoTiposElementos.get(i).getCamposElemento();
                JSONArray jsonArray = new JSONArray(p);
                String condicion = jsonArray.getJSONObject(i).getString("bCampoOrden");
                if(Integer.parseInt(condicion)==1){
                    nombreOrden = jsonArray.getJSONObject(i).getString("campo");
                    encontrado = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            i++;
        }while(encontrado==false);
        return nombreOrden;
    }

    //__________FUNCIONES DE BORRADO______________________//

    public static void borrarTodosLosElementos(Context context) throws SQLException {
        cargarDao(context);
        DeleteBuilder<TipoElementos, Integer> deleteBuilder = dao.deleteBuilder();
        deleteBuilder.delete();
    }
}
/*
cargarDao(context);
		QueryBuilder<ElementoInstalacion, Integer> queryBuilder = dao.queryBuilder();
		queryBuilder.selectRaw(ElementoInstalacion.FK_TIPO+","+ElementoInstalacion.NOMBRE_TIPO+", COUNT("+ElementoInstalacion.ID_ELEMENTO+")");
		Where where = queryBuilder.where();
		where.eq(ElementoInstalacion.FK_PARTE, fkParte);
		queryBuilder.groupBy(ElementoInstalacion.FK_TIPO);
		GenericRawResults<String[]> resultado = dao.queryRaw(queryBuilder.prepareStatementString());
		List <String[]> listadoElementos= resultado.getResults();
		if (listadoElementos.isEmpty()) {
			return null;
		} else {
			return listadoElementos;
		}
 */