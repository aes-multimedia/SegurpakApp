package com.multimedia.aes.gestnet_spak.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.multimedia.aes.gestnet_spak.dbhelper.DBHelperMOS;
import com.multimedia.aes.gestnet_spak.entidades.Imagen;

import java.sql.SQLException;
import java.util.List;

public class ImagenDAO extends DBHelperMOS {
	static Dao<Imagen, Integer> dao;

	public static void cargarDao(Context context) throws SQLException {
		dao = getHelper(context).getImagenDAO();
	}

	//__________FUNCIONES DE CREACIÓN________________________//

	public static boolean newImagen(Context context, String nombre_imagen, String ruta_imagen, int fk_parte,int fk_accion_protocolo,boolean galeria,boolean enviado) {
		Imagen i = montarImagen(nombre_imagen, ruta_imagen, fk_parte,fk_accion_protocolo, galeria, enviado);
		return crearImagen(i,context);
	}
	public static boolean crearImagen(Imagen i, Context context) {
		try {
			cargarDao(context);
			dao.create(i);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	public static Imagen montarImagen(String nombre_imagen, String ruta_imagen, int fk_parte,int fk_accion_protocolo,boolean galeria,boolean enviado) {
		Imagen i =new Imagen(nombre_imagen, ruta_imagen, fk_parte,fk_accion_protocolo, galeria, enviado);
		return i;
	}

	//__________FUNCIONES DE BORRADO______________________//

	public static void borrarTodasLasImagenes(Context context) throws SQLException {
		cargarDao(context);
		DeleteBuilder<Imagen, Integer> deleteBuilder = dao.deleteBuilder();
		deleteBuilder.delete();
	}
	public static void borrarImagenPorID(Context context, int id) throws SQLException {
		cargarDao(context);
		DeleteBuilder<Imagen, Integer> deleteBuilder = dao.deleteBuilder();
		deleteBuilder.where().eq(Imagen.ID_IMAGEN, id);
		deleteBuilder.delete();
	}


	public static void borrarImagenPorRuta(Context context, String ruta) throws SQLException {
		cargarDao(context);
		DeleteBuilder<Imagen, Integer> deleteBuilder = dao.deleteBuilder();
		deleteBuilder.where().eq(Imagen.RUTA_IMAGEN, ruta);
		deleteBuilder.delete();
	}

	public static synchronized void borrarImagenesPresupuestoPorFk_parte(Context context, int fk_parte) throws SQLException {
		cargarDao(context);
		DeleteBuilder<Imagen, Integer> deleteBuilder = dao.deleteBuilder();
		deleteBuilder.where().eq(Imagen.FK_PARTE, fk_parte).and().like(Imagen.NOMBRE_IMAGEN,"%"+"presupuesto_"+"%");
		deleteBuilder.delete();
	}

	//__________FUNCIONES DE BUSQUEDA______________________//


	public static List<Imagen> buscarTodasLasImagenes(Context context) throws SQLException {
		cargarDao(context);
		List<Imagen> listadoImagenes= dao.queryForAll();
		if(listadoImagenes.isEmpty()) {
			return null;
		}else{
			return listadoImagenes;
		}
	}
	public static Imagen buscarImagenPorId(Context context, int id) throws SQLException {
		cargarDao(context);
		List<Imagen> listadoImagenes= dao.queryForEq(Imagen.ID_IMAGEN, id);
		if(listadoImagenes.isEmpty()){
			return  null;
		}else{
			return listadoImagenes.get(0);
		}
	}

	public static List<Imagen> buscarImagenPorFk_parte(Context context, int fk_parte) throws SQLException {
		cargarDao(context);
		List<Imagen> listadoImagenes= dao.queryForEq(Imagen.FK_PARTE, fk_parte);
		if(listadoImagenes.isEmpty()){
			return  null;
		}else{
			return listadoImagenes;
		}
	}
	public static List<Imagen> buscarImagenPorFkProtocoloAccion(Context context, int protocolo) throws SQLException {
		cargarDao(context);
		List<Imagen> listadoImagenes= dao.queryForEq(Imagen.FK_ACCION_PROTOCOLO, protocolo);
		if(listadoImagenes.isEmpty()){
			return  null;
		}else{
			return listadoImagenes;
		}
	}
	public static List<Imagen> buscarImagenPorFkProtocoloAccionNoEnviados(Context context, int protocolo) throws SQLException {
		cargarDao(context);
		List<Imagen> listadoImagenes= dao.queryBuilder().where().eq(Imagen.FK_ACCION_PROTOCOLO, protocolo).and().eq(Imagen.ENVIADO,false).query();
		if(listadoImagenes.isEmpty()){
			return  null;
		}else{
			return listadoImagenes;
		}
	}

	public static synchronized List<Imagen> buscarImagenPresupuestoPorFk_parte(Context context, int fk_parte) throws SQLException {
		cargarDao(context);
		List<Imagen> listadoImagenes= dao.queryBuilder().where().like(Imagen.NOMBRE_IMAGEN,"%"+"presupuesto_"+"%").and().eq(Imagen.FK_PARTE,fk_parte).query();
		if(listadoImagenes.isEmpty()){
			return  null;
		}else{
			return listadoImagenes;
		}
	}

	public static int buscarUltimoIdImagen(Context context) throws SQLException {
		cargarDao(context);
		List<Imagen> listadoImagenes= dao.queryForAll();
		int size = listadoImagenes.size();
		if(listadoImagenes.isEmpty()){
			return 0;
		}else{
			listadoImagenes.get(size-1);
			return listadoImagenes.get(0).getId_imagen();
		}
	}

	//____________________________FUNCIONES DE ACTUALIZAR_________________________________________//
	public static void actualizarEnviado(Context context,int id,boolean enviado) throws java.sql.SQLException {
		cargarDao(context);
		UpdateBuilder<Imagen, Integer> updateBuilder = dao.updateBuilder();
		updateBuilder.where().eq(Imagen.ID_IMAGEN,id);
		updateBuilder.updateColumnValue(Imagen.ENVIADO,enviado);
		updateBuilder.update();

	}
}
