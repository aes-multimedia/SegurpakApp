package com.multimedia.aes.gestnet_spak.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.multimedia.aes.gestnet_spak.dbhelper.DBHelperMOS;
import com.multimedia.aes.gestnet_spak.entidades.HorasParte;

import java.sql.SQLException;
import java.util.List;


public class HorasParteDAO extends DBHelperMOS {
    static Dao<HorasParte, Integer> dao;

//__________FUNCIONES DE CREACIÓN________________________//

    public static void cargarDao(Context context) throws SQLException {
        dao = getHelper(context).getHorasParteDAO();
    }

   public static boolean newHorasParte(Context context, int id_gestnet, int fk_parte,int fk_tecnico,int fk_pausa,String hora_inicio,String hora_fin,String fecha,String fecha_visita, int tipo){
        HorasParte t = montarHorasParte( id_gestnet, fk_parte, fk_tecnico,fk_pausa,hora_inicio,hora_fin,fecha,fecha_visita,tipo);
        return crearHorasParte(t, context);
    }

    public static HorasParte montarHorasParte( int id_gestnet, int fk_parte,int fk_tecnico,int fk_pausa,String hora_inicio,String hora_fin,String fecha,String fecha_visita, int tipo ) {
        HorasParte t = new HorasParte(id_gestnet, fk_parte, fk_tecnico,fk_pausa,hora_inicio,hora_fin,fecha,fecha_visita,tipo);
        return t;
    }

    public static boolean  borrarTodos(Context context) throws SQLException{

        cargarDao(context);
        DeleteBuilder<HorasParte, Integer> deleteBuilder = dao.deleteBuilder();
        deleteBuilder.delete();
        return true;
    }

    public static int  borrarHoraParteById(Context context,int id_hora) throws SQLException{
     cargarDao(context);
     DeleteBuilder<HorasParte, Integer> deleteBuilder = dao.deleteBuilder();
     deleteBuilder.where().eq(HorasParte.ID_HORA, id_hora);
        int result= deleteBuilder.delete();
        return result;
    }

    public static List<HorasParte> getAllByFkParte(Context c,int fk_parte) throws SQLException {
        cargarDao(c);
        List<HorasParte> result= dao.queryBuilder().where().eq(HorasParte.FK_PARTE,fk_parte).query();
        if(result.isEmpty()) {
            return null;
        }else{
            return result;
        }

    }
    public static HorasParte getCierreParte(Context c,int fk_parte) throws SQLException {
        cargarDao(c);
        QueryBuilder<HorasParte, Integer> queryBuilder = dao.queryBuilder();
        Where where = queryBuilder.where();
        where.eq(HorasParte.FK_PARTE, fk_parte);
        where.and();
        where.eq(HorasParte.TIPO, 4);
        PreparedQuery<HorasParte> preparedQuery = queryBuilder.prepare();
        List<HorasParte> result = dao.query(preparedQuery);
        if(result.isEmpty()) {
            return null;
        }else{
            return result.get(0);
        }

    }
    public static HorasParte getFirstHoraPausa(Context c,int fk_parte) throws SQLException {
        cargarDao(c);
        QueryBuilder<HorasParte, Integer> queryBuilder = dao.queryBuilder();
        Where where = queryBuilder.where();
        where.eq(HorasParte.FK_PARTE, fk_parte);
        where.and();
        where.eq(HorasParte.TIPO, 2);
        queryBuilder.orderBy(HorasParte.ID_HORA,true);
        long limit = 1;
        queryBuilder.limit(limit);
        PreparedQuery<HorasParte> preparedQuery = queryBuilder.prepare();
        List<HorasParte> result = dao.query(preparedQuery);

        if(result.isEmpty()) {
            return null;
        }else{
            return result.get(0);
        }
    }
    public static HorasParte getLastHoraReinicio(Context c,int fk_parte) throws SQLException {
        cargarDao(c);
        QueryBuilder<HorasParte, Integer> queryBuilder = dao.queryBuilder();
        Where where = queryBuilder.where();
        where.eq(HorasParte.FK_PARTE, fk_parte);
        where.and();
        where.eq(HorasParte.TIPO, 3);
        queryBuilder.orderBy(HorasParte.ID_HORA,false);
        long limit = 1;
        queryBuilder.limit(limit);
        PreparedQuery<HorasParte> preparedQuery = queryBuilder.prepare();
        List<HorasParte> result = dao.query(preparedQuery);

        if(result.isEmpty()) {
            return null;
        }else{
            return result.get(0);
        }
    }
    public static HorasParte getPausaByFkReinicio(Context c,int fk_parte,int fk_reinicio) throws SQLException {
        cargarDao(c);
        QueryBuilder<HorasParte, Integer> queryBuilder = dao.queryBuilder();
        Where where = queryBuilder.where();
        where.eq(HorasParte.FK_PARTE, fk_parte);
        where.and();
        where.eq(HorasParte.FK_REINICIO, fk_reinicio);
        long limit = 1;
        queryBuilder.limit(limit);
        PreparedQuery<HorasParte> preparedQuery = queryBuilder.prepare();
        List<HorasParte> result = dao.query(preparedQuery);

        if(result.isEmpty()) {
            return null;
        }else{
            return result.get(0);
        }
    }
    public static boolean crearHorasParte(HorasParte t, Context context) {
        try {
            cargarDao(context);
            dao.create(t);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }





}
