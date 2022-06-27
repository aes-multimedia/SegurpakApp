package com.multimedia.aes.gestnet_spak.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.multimedia.aes.gestnet_spak.dbhelper.DBHelperMOS;
import com.multimedia.aes.gestnet_spak.entidades.OperacionesTiposElementos;

import java.sql.SQLException;
import java.util.List;

public class OperacionTipoElementoDAO extends DBHelperMOS {
	static Dao<OperacionesTiposElementos, Integer> dao;

	public static void cargarDao(Context context) throws SQLException {
		dao = getHelper(context).getOperacionTipoElementoDAO();
	}


	//__________FUNCIONES DE CREACIÓN________________________//
	
	public static boolean newOperacionTipoElemento(Context context, int fk_tipo, String nombre, String campos) {
		OperacionesTiposElementos m = montarOperacionesTiposElementos( fk_tipo,   nombre,   campos);
		return crearOperacionesTiposElementos(m, context);
	}
	
	public static boolean crearOperacionesTiposElementos(OperacionesTiposElementos m, Context context) {
		try {
			cargarDao(context);
			dao.create(m);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}


	public static OperacionesTiposElementos montarOperacionesTiposElementos(int fk_tipo, String nombre, String campos) {
		OperacionesTiposElementos m = new OperacionesTiposElementos(fk_tipo,nombre, campos);
		return m;
	}

	//__________FUNCIONES DE BORRADO______________________//

	public static void borrarTodos(Context context) throws SQLException {
		cargarDao(context);
		DeleteBuilder<OperacionesTiposElementos, Integer> deleteBuilder = dao.deleteBuilder();
		deleteBuilder.delete();
	}


	//__________FUNCIONES DE BUSQUEDA______________________//


	public static List<OperacionesTiposElementos> buscarTodas(Context context) throws SQLException {
		cargarDao(context);
		List<OperacionesTiposElementos> listadoOperacionesTipos = dao.queryForAll();
		if (listadoOperacionesTipos.isEmpty()) {
			return null;
		} else {
			return listadoOperacionesTipos;
		}
	}

	public static OperacionesTiposElementos buscarOperacionesTiposElementoPorFkTipo(Context context, int fk) throws SQLException {
		cargarDao(context);
		List <OperacionesTiposElementos> listadoOperacionesTipos = dao.queryForEq(OperacionesTiposElementos.FK_TIPO, fk);
		if (listadoOperacionesTipos.isEmpty()) {
			return null;
		} else {
			return listadoOperacionesTipos.get(0);
		}
	}


}
