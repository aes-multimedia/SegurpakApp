package com.multimedia.aes.gestnet_spak.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;
import com.multimedia.aes.gestnet_spak.dbhelper.DBHelperMOS;
import com.multimedia.aes.gestnet_spak.entidades.ElementoInstalacion;
import com.multimedia.aes.gestnet_spak.entidades.TipoElementos;

import org.json.JSONArray;
import org.json.JSONException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ElementoInstDAO extends DBHelperMOS {
	static Dao<ElementoInstalacion, Integer> dao;

	public static void cargarDao(Context context) throws SQLException {
		dao = getHelper(context).getElementoInstDAO();
	}


	//__________FUNCIONES DE CREACIÓN________________________//
	
	public static boolean newElementoInstalacion(Context context, int fk_maquina, int fk_parte, int fk_tipo, String nombre_tipo, String tabla,String valores,int fk_elemento_gestnet, String registro,String valores_gestnet,int fk_operacion,boolean bfinalizado, boolean activo) {
		ElementoInstalacion m = montarElementoInst(fk_maquina,fk_parte,   fk_tipo,   nombre_tipo,   tabla,
				valores, fk_elemento_gestnet,registro,valores_gestnet,fk_operacion, bfinalizado,activo);
		return crearElementoInstalacion(m, context);
	}
	
	public static boolean crearElementoInstalacion(ElementoInstalacion m, Context context) {
		try {
			cargarDao(context);
			dao.create(m);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}



	public static ElementoInstalacion montarElementoInst(int fk_maquina, int fk_parte, int fk_tipo, String nombre_tipo, String tabla,String valores,int fk_elemento_gestnet, String registro,String valores_gestnet,int fk_operacion, boolean bfinalizado,boolean activo) {
		ElementoInstalacion m = new ElementoInstalacion(fk_maquina,fk_parte,   fk_tipo,   nombre_tipo,   tabla,
				valores, fk_elemento_gestnet,registro,valores_gestnet, fk_operacion, bfinalizado,activo);
		return m;
	}

	//__________FUNCIONES DE BORRADO______________________//

	public static void borrarTodos(Context context) throws SQLException {
		cargarDao(context);
		DeleteBuilder<ElementoInstalacion, Integer> deleteBuilder = dao.deleteBuilder();
		deleteBuilder.delete();
	}

	public static void borrarPorId(Context context, int id) throws SQLException {
		cargarDao(context);
		DeleteBuilder<ElementoInstalacion, Integer> deleteBuilder = dao.deleteBuilder();
		deleteBuilder.where().eq(ElementoInstalacion.ID_ELEMENTO, id);
		deleteBuilder.delete();
	}

	public static void borrarPorFkParte(Context context, int id) throws SQLException {
		cargarDao(context);
		DeleteBuilder<ElementoInstalacion, Integer> deleteBuilder = dao.deleteBuilder();
		deleteBuilder.where().eq(ElementoInstalacion.FK_PARTE, id);
		deleteBuilder.delete();
	}

	//__________FUNCIONES DE BUSQUEDA______________________//


	public static List<ElementoInstalacion> buscarTodas(Context context) throws SQLException {
		cargarDao(context);
		List<ElementoInstalacion> listadoElementos = dao.queryForAll();
		if (listadoElementos.isEmpty()) {
			return null;
		} else {
			return listadoElementos;
		}
	}

	public static List<ElementoInstalacion> buscarElementoInstalacionPorFkMaquina(Context context, int fk) throws SQLException {
		cargarDao(context);
		List<ElementoInstalacion> listadoElementos = dao.queryForEq(ElementoInstalacion.FK_MAQUINA, fk);
		if (listadoElementos.isEmpty()) {
			return null;
		} else {
			return listadoElementos;
		}
	}

	public static List <String[]> buscarTiposElementosParte (Context context, int fkParte) throws SQLException {
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
	}
	public static ElementoInstalacion buscarElementoInstalacionPorIdElemento(Context context, int id) throws SQLException {
		cargarDao(context);
		QueryBuilder<ElementoInstalacion, Integer> queryBuilder = dao.queryBuilder();
		Where where = queryBuilder.where();
		where.eq(ElementoInstalacion.ID_ELEMENTO, id);
		PreparedQuery<ElementoInstalacion> preparedQuery = queryBuilder.prepare();
		List<ElementoInstalacion> listadoElementos = dao.query(preparedQuery);
		if (listadoElementos.isEmpty()) {
			return null;
		} else {
			return listadoElementos.get(0);
		}
	}
	public static ElementoInstalacion buscarElementoInstalacionPorFkGestnetFkParte(Context context,   int fk, int fkParte,int fk_tipo) throws SQLException {
		cargarDao(context);
		QueryBuilder<ElementoInstalacion, Integer> queryBuilder = dao.queryBuilder();
		Where where = queryBuilder.where();
		where.eq(ElementoInstalacion.FK_PARTE, fkParte);
		where.and();
		where.eq(ElementoInstalacion.FK_ELEMENTO_GESTNET, fk);
		where.and();
		where.eq(ElementoInstalacion.FK_TIPO, fk_tipo);
		PreparedQuery<ElementoInstalacion> preparedQuery = queryBuilder.prepare();
		List<ElementoInstalacion> listadoElementos = dao.query(preparedQuery);
		if (listadoElementos.isEmpty()) {
			return null;
		} else {
			return listadoElementos.get(0);
		}
	}
	public static List<ElementoInstalacion> buscarElementoInstalacionPorFkParte(Context context, int fk) throws SQLException {
		cargarDao(context);
		List<ElementoInstalacion> listadoElementos = dao.queryForEq(ElementoInstalacion.FK_PARTE, fk);
		if (listadoElementos.isEmpty()) {
			return null;
		} else {
			return listadoElementos;
		}
	}
	public static List<ElementoInstalacion> buscarElementoInstalacionPorFkParteFkTipo(Context context, int fk,int fkTipo) throws SQLException {
		cargarDao(context);
		QueryBuilder<ElementoInstalacion, Integer> queryBuilder = dao.queryBuilder();
		Where where = queryBuilder.where();
		where.eq(ElementoInstalacion.FK_PARTE, fk);
		where.and();
		where.eq(ElementoInstalacion.FK_TIPO, fkTipo);
		PreparedQuery<ElementoInstalacion> preparedQuery = queryBuilder.prepare();
		List<ElementoInstalacion> listadoElementos = dao.query(preparedQuery);
		if (listadoElementos.isEmpty()) {
			return null;
		} else {
			return listadoElementos;
		}
	}

	public static int buscarUltimoIdElemento(Context context) throws SQLException {
		List<ElementoInstalacion> listadoElementos = buscarTodas(context);
		int id_elemento = -1;
		for (int i=0;i<listadoElementos.size();i++){
			if (id_elemento < listadoElementos.get(i).getId_elemento()){
				id_elemento = listadoElementos.get(i).getId_elemento();
			}
		}
		return id_elemento;
	}

	public static List<String> busquedaFiltros (Context context,int fk_parte, int fk_tipo, String str) throws SQLException, JSONException {
		cargarDao(context);

		TipoElementos tipoElemento = TipoElementosDAO.buscarTiposElementoPorFkTipo(context,fk_tipo);
		String p = tipoElemento.getCamposElemento();
		JSONArray jsonArray = new JSONArray(p);
		ArrayList<String> camposFiltro = new ArrayList<String>();
		String condicion1 = "";
		String condicion2 = "";
		for(int i=0;i<jsonArray.length();i++){
			condicion1 = jsonArray.getJSONObject(i).getString("bCampoTit");
			condicion2 = jsonArray.getJSONObject(i).getString("tipo");
			if((condicion1.equals("1"))&&(condicion2.equals("estatico"))){
				camposFiltro.add(jsonArray.getJSONObject(i).getString("campo"));
			}
		}
		return camposFiltro;
	}


	//____________________________FUNCIONES DE ACTUALIZAR_________________________________________//
	public static void actualizarElementoInstalacion(Context context,int id,int fk_maquina, int fk_parte, int fk_tipo, String nombre_tipo, String tabla,String valores,String valoresgestnet,int fk_elemento_gestnet) throws SQLException {
		cargarDao(context);
		UpdateBuilder<ElementoInstalacion, Integer> updateBuilder = dao.updateBuilder();
		updateBuilder.where().eq(ElementoInstalacion.ID_ELEMENTO,id);
		updateBuilder.updateColumnValue(ElementoInstalacion.FK_MAQUINA,fk_maquina);
		updateBuilder.updateColumnValue(ElementoInstalacion.FK_PARTE,fk_parte);
		updateBuilder.updateColumnValue(ElementoInstalacion.FK_TIPO,fk_tipo);
		updateBuilder.updateColumnValue(ElementoInstalacion.NOMBRE_TIPO,nombre_tipo);
		updateBuilder.updateColumnValue(ElementoInstalacion.TABLA,tabla);
		updateBuilder.updateColumnValue(ElementoInstalacion.VALORES,valores);
		updateBuilder.updateColumnValue(ElementoInstalacion.VALORESGESTNET,valoresgestnet);
		updateBuilder.updateColumnValue(ElementoInstalacion.FK_ELEMENTO_GESTNET,fk_elemento_gestnet);

		updateBuilder.update();
	}
	public static void actualizaValores(Context context, int id, String valores) throws SQLException {
		cargarDao(context);
		UpdateBuilder<ElementoInstalacion, Integer> updateBuilder = dao.updateBuilder();
		updateBuilder.where().eq(ElementoInstalacion.ID_ELEMENTO,id);
		updateBuilder.updateColumnValue(ElementoInstalacion.VALORES, valores);
		updateBuilder.update();

	}

	public static void actualizaOperacion(Context context, int id, String valores) throws SQLException {
		cargarDao(context);
		UpdateBuilder<ElementoInstalacion, Integer> updateBuilder = dao.updateBuilder();
		updateBuilder.where().eq(ElementoInstalacion.ID_ELEMENTO,id);
		updateBuilder.updateColumnValue(ElementoInstalacion.FK_OPERACION, valores);
		updateBuilder.update();

	}

	public static void actualizaValoresGestnet(Context context, int id, String valores_gestnet) throws SQLException {
		cargarDao(context);
		UpdateBuilder<ElementoInstalacion, Integer> updateBuilder = dao.updateBuilder();
		updateBuilder.where().eq(ElementoInstalacion.ID_ELEMENTO,id);
		updateBuilder.updateColumnValue(ElementoInstalacion.VALORESGESTNET, valores_gestnet);
		updateBuilder.update();

	}
	public static void actualizaBfinalizado(Context context, int id, boolean bFinalizado) throws SQLException {
		cargarDao(context);
		UpdateBuilder<ElementoInstalacion, Integer> updateBuilder = dao.updateBuilder();
		updateBuilder.where().eq(ElementoInstalacion.ID_ELEMENTO,id);
		updateBuilder.updateColumnValue(ElementoInstalacion.BFINALIZADO, bFinalizado);
		updateBuilder.update();

	}
	public static void actualizaActivo(Context context, int id, boolean activo) throws SQLException {
		cargarDao(context);
		UpdateBuilder<ElementoInstalacion, Integer> updateBuilder = dao.updateBuilder();
		updateBuilder.where().eq(ElementoInstalacion.ID_ELEMENTO,id);
		updateBuilder.updateColumnValue(ElementoInstalacion.ACTIVO, activo);
		updateBuilder.update();

	}
	public static void actualizaRegistro(Context context, int id, String registro) throws SQLException {
		cargarDao(context);
		UpdateBuilder<ElementoInstalacion, Integer> updateBuilder = dao.updateBuilder();
		updateBuilder.where().eq(ElementoInstalacion.ID_ELEMENTO,id);
		updateBuilder.updateColumnValue(ElementoInstalacion.REGISTRO, registro);
		updateBuilder.update();

	}
}
