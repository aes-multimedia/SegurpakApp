package com.multimedia.aes.gestnet_spak.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.multimedia.aes.gestnet_spak.dbhelper.DBHelperMOS;
import com.multimedia.aes.gestnet_spak.entidades.Articulo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArticuloDAO extends DBHelperMOS {
    static Dao<Articulo, Integer> dao;

    public static void cargarDao(Context context) throws SQLException {
        dao = getHelper(context).getArticuloDAO();
    }


    //__________FUNCIONES DE CREACIÓN________________________//

    public static boolean newArticulo(Context context,int id_item_gestnet,int id_articulo, String nombre_articulo,double stock, String referencia, String referencia_aux, String familia,
                                      String marca, String modelo, int proveedor, double iva, double tarifa, double descuento, double coste, String ean,int imagen) {
        Articulo a = montarArticulo(id_item_gestnet,id_articulo,nombre_articulo,stock,referencia, referencia_aux, familia, marca,  modelo, proveedor, iva, tarifa, descuento, coste, ean,imagen);
        return crearArticulo(a,context);
    }
    public static Articulo newArticuloRet(Context context,int id_item_gestnet,int id_articulo, String nombre_articulo,double stock, String referencia, String referencia_aux, String familia,
                                      String marca, String modelo, int proveedor, double iva, double tarifa, double descuento, double coste, String ean,int imagen) {
        Articulo a = montarArticulo(id_item_gestnet,id_articulo,nombre_articulo,stock,referencia, referencia_aux, familia, marca,  modelo, proveedor, iva, tarifa, descuento, coste, ean,imagen);
        return crearArticuloRet(a,context);
    }



    public static Articulo newArticuloDialogFragment(Context context,int id_articulo, String nombre_articulo,double stock, String referencia, String referencia_aux, String familia,
                                          String marca, String modelo, int proveedor, double iva, double tarifa, double descuento, double coste, String ean,int imagen,boolean entregado, boolean garantia) {
        Articulo a = montarArticuloDialogFragment(id_articulo,nombre_articulo,stock,referencia, referencia_aux, familia, marca,  modelo, proveedor, iva, tarifa, descuento, coste, ean,imagen,entregado,garantia);
        return crearArticuloRet(a,context);
    }




    public static boolean newArticuloP(Context context,int id_articulo, String nombre_articulo,double stock, double coste,String referencia, String referencia_aux,String ean,double iva, double tarifa, double descuento) {
        Articulo a = montarArticuloP( id_articulo, nombre_articulo, stock, coste,referencia,referencia_aux,ean,iva,tarifa,descuento);
        return crearArticulo(a,context);
    }

    public static boolean crearArticulo(Articulo a,Context context) {
        try {
            cargarDao(context);
            dao.create(a);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static Articulo crearArticuloRet(Articulo a,Context context) {
        try {
            cargarDao(context);
            dao.create(a);
            return a;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Articulo montarArticuloDialogFragment(int id_articulo, String nombre_articulo, double stock, String referencia, String referencia_aux, String familia, String marca, String modelo, int proveedor, double iva, double tarifa, double descuento, double coste, String ean, int imagen, boolean entregado, boolean garantia) {

       return new Articulo(id_articulo,nombre_articulo,stock,referencia, referencia_aux, familia, marca,  modelo, proveedor, iva, tarifa, descuento, coste, ean,imagen,entregado,garantia);

    }


    public static Articulo  montarArticulo(int id_item_gestnet,int id_articulo, String nombre_articulo, double stock, String referencia, String referencia_aux, String familia,
                                           String marca, String modelo, int proveedor, double iva, double tarifa, double descuento, double coste, String ean, int imagen) {
        Articulo a =new Articulo(id_item_gestnet,id_articulo,nombre_articulo,stock,referencia, referencia_aux, familia, marca,  modelo, proveedor, iva, tarifa, descuento, coste, ean,imagen);
        return a;
    }


    public static Articulo  montarArticuloP(int id_articulo, String nombre_articulo,double stock, double coste,String referencia, String referencia_aux,String ean,double iva, double tarifa, double descuento) {
        Articulo a =new Articulo( id_articulo, nombre_articulo, stock, coste,referencia,referencia_aux,ean,iva,tarifa,descuento);
        return a;
    }


    //__________FUNCIONES DE BORRADO______________________//

    public static void borrarTodosLosArticulos(Context context) throws SQLException {
        cargarDao(context);
        DeleteBuilder<Articulo, Integer> deleteBuilder = dao.deleteBuilder();
        deleteBuilder.delete();
    }

    public static void borrarArticuloPorID(Context context, int id) throws SQLException {
        cargarDao(context);
        DeleteBuilder<Articulo, Integer> deleteBuilder = dao.deleteBuilder();
        deleteBuilder.where().eq(Articulo.ID_ARTICULO, id);
        deleteBuilder.delete();
    }
//__________FUNCIONES DE BUSQUEDA______________________//


    public static List<Articulo> buscarTodosLosArticulos(Context context) throws SQLException {
        cargarDao(context);
        List<Articulo> listadoArticulo= dao.queryForAll();
        if(listadoArticulo.isEmpty()) {
            return null;
        }else{
            return listadoArticulo;
        }
    }

    public static List<Articulo> buscarArticulosPorNombre(Context context,String cadena) throws SQLException {
        cargarDao(context);
        List<Articulo> listadoArticulo= dao.queryBuilder().where().like(Articulo.NOMBRE_ARTICULO,"%"+cadena+"%").query();
        if(listadoArticulo.isEmpty()) {
            return null;
        }else{
            return listadoArticulo;
        }
    }
    public static List<Articulo> buscarPorFkArticulo(Context context,int fk) throws SQLException {
        cargarDao(context);
        List<Articulo> listadoArticulo= dao.queryBuilder().where().eq(Articulo.FK_ARTICULO,fk).query();
        if(listadoArticulo.isEmpty()) {
            return null;
        }else{
            return listadoArticulo;
        }
    }

    public static List<Articulo> buscarArticulosPorReferencia(Context context,String ref) throws SQLException {
        cargarDao(context);
        List<Articulo> listadoArticulo= dao.queryBuilder().where().eq(Articulo.REFERENCIA,ref).query();
        if(listadoArticulo.isEmpty()) {
            return null;
        }else{
            return listadoArticulo;
        }
    }
    public static ArrayList<String> buscarNombreArticulosPorNombre(Context context, String cadena) throws SQLException {
        cargarDao(context);
        List<Articulo> listadoArticulo= dao.queryBuilder().where().like(Articulo.NOMBRE_ARTICULO,"%"+cadena+"%").or().like(Articulo.REFERENCIA,"%"+cadena+"%").query();
        if(listadoArticulo.isEmpty()) {
            return null;
        }else{
            ArrayList<String> nombres = new ArrayList<>();
            for (Articulo articulo:listadoArticulo) {
                nombres.add(articulo.getReferencia()+" <-> "+articulo.getNombre_articulo());
            }
            return nombres;
        }
    }

    public static Articulo buscarArticuloPorID(Context context, int id) throws SQLException {
        cargarDao(context);
        List<Articulo> listadoArticulo= dao.queryForEq(Articulo.ID_ARTICULO, id);


        if(listadoArticulo.isEmpty()) {
            return null;
        }else{
            return listadoArticulo.get(0);
        }
    }
    public static Boolean existeArticuloPorFkArticulo(Context context, int id) throws SQLException {
        cargarDao(context);
        List<Articulo> listadoArticulo= dao.queryForEq(Articulo.FK_ARTICULO, id);

        if(listadoArticulo.isEmpty()) {
            return false;
        }else{
            return true;
        }
    }
    public static Boolean existeArticuloPorIdItem(Context context, int id) throws SQLException {
        cargarDao(context);
        List<Articulo> listadoArticulo= dao.queryForEq(Articulo.ID_ITEM_GESTNET, id);

        if(listadoArticulo.isEmpty()) {
            return false;
        }else{
            return true;
        }
    }

    //____________________________FUNCIONES DE ACTUALIZAR_________________________________________//

public  static void actualizarIdItemGestnet(Context context, int id_item_gestnet,int id_articulo) throws SQLException {
    cargarDao(context);
    UpdateBuilder<Articulo, Integer> updateBuilder = dao.updateBuilder();
    updateBuilder.where().eq(Articulo.ID_ARTICULO,id_articulo);
    updateBuilder.updateColumnValue(Articulo.ID_ITEM_GESTNET,id_item_gestnet);
    updateBuilder.update();
}
    public static void actualizarArticulo(Context context, Articulo articulo ) throws SQLException {
        int id_articulo=articulo.getId_articulo();
        String nombre_articulo=articulo.getNombre_articulo();
        double stock=articulo.getStock();
        String referencia=articulo.getReferencia();
        String referencia_aux=articulo.getReferencia_aux();
        String familia=articulo.getFamilia();
        String marca=articulo.getMarca();
        String modelo=articulo.getModelo();
        int proveedor=articulo.getProveedor();
        double iva=articulo.getIva();
        double tarifa=articulo.getTarifa();
        double descuento=articulo.getDescuento();
        double coste=articulo.getCoste();
        String ean=articulo.getEan();
        int imagen=articulo.getImagen();
        cargarDao(context);
        UpdateBuilder<Articulo, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Articulo.ID_ARTICULO,id_articulo);
        updateBuilder.updateColumnValue(Articulo.NOMBRE_ARTICULO,nombre_articulo);
        updateBuilder.updateColumnValue(Articulo.STOCK, stock);
        updateBuilder.updateColumnValue(Articulo.REFERENCIA, referencia);
        updateBuilder.updateColumnValue(Articulo.REFERENCIA_AUX, referencia_aux);
        updateBuilder.updateColumnValue(Articulo.FAMILIA, familia);
        updateBuilder.updateColumnValue(Articulo.MARCA, marca);
        updateBuilder.updateColumnValue(Articulo.MODELO, modelo);
        updateBuilder.updateColumnValue(Articulo.PROVEEDOR, proveedor);
        updateBuilder.updateColumnValue(Articulo.IVA, iva);
        updateBuilder.updateColumnValue(Articulo.TARIFA, tarifa);
        updateBuilder.updateColumnValue(Articulo.DESCUENTO, descuento);
        updateBuilder.updateColumnValue(Articulo.COSTE, coste);
        updateBuilder.updateColumnValue(Articulo.EAN, ean);
        updateBuilder.updateColumnValue(Articulo.IMAGEN, imagen);
        updateBuilder.update();
    }


    public static void actualizarArticulo(Context context, int id_articulo, String nombre_articulo,double stock, double coste)throws SQLException {
        cargarDao(context);
        UpdateBuilder<Articulo, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Articulo.ID_ARTICULO,id_articulo);
        updateBuilder.updateColumnValue(Articulo.NOMBRE_ARTICULO,nombre_articulo);
        updateBuilder.updateColumnValue(Articulo.STOCK, stock);
        updateBuilder.updateColumnValue(Articulo.TARIFA, coste);
        updateBuilder.update();
    }
    public static void actualizarArticuloP(Context context, int id_articulo, String nombre_articulo,double stock, double coste,String referencia, String referencia_aux,String ean,double iva, double tarifa, double descuento)throws SQLException {
        cargarDao(context);
        UpdateBuilder<Articulo, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Articulo.ID_ARTICULO,id_articulo);
        updateBuilder.updateColumnValue(Articulo.NOMBRE_ARTICULO,nombre_articulo);
        updateBuilder.updateColumnValue(Articulo.STOCK, stock);
        updateBuilder.updateColumnValue(Articulo.COSTE, coste);
        updateBuilder.updateColumnValue(Articulo.REFERENCIA, referencia);
        updateBuilder.updateColumnValue(Articulo.REFERENCIA_AUX, referencia_aux);
        updateBuilder.updateColumnValue(Articulo.EAN, ean);
        updateBuilder.updateColumnValue(Articulo.IVA, iva);
        updateBuilder.updateColumnValue(Articulo.TARIFA, tarifa);
        updateBuilder.updateColumnValue(Articulo.DESCUENTO, descuento);
        updateBuilder.update();
    }


    public static void actualizarGarantia(Context context, int id_articulo,boolean garantia) throws SQLException {

        cargarDao(context);
        UpdateBuilder<Articulo, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Articulo.ID_ARTICULO,id_articulo);
        updateBuilder.updateColumnValue(Articulo.GARANTIA,garantia);
        updateBuilder.update();


    }

    public synchronized static void actualizarEntregado(Context context, int id_articulo) throws SQLException {

        cargarDao(context);
        UpdateBuilder<Articulo, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Articulo.ID_ARTICULO,id_articulo);
        updateBuilder.updateColumnValue(Articulo.ENTREGADO,true);
        updateBuilder.update();


    }

    public synchronized static void actualizarUtilizado(Context context, int id_articulo) throws SQLException {

        cargarDao(context);
        UpdateBuilder<Articulo, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Articulo.ID_ARTICULO,id_articulo);
        updateBuilder.updateColumnValue(Articulo.ENTREGADO,false);
        updateBuilder.update();


    }

    public synchronized static void actualizarStock(Context context, int id_articulo,double cantidad) throws SQLException {

        cargarDao(context);
        UpdateBuilder<Articulo, Integer> updateBuilder = dao.updateBuilder();

        updateBuilder.where().eq(Articulo.ID_ARTICULO,id_articulo);

        updateBuilder.updateColumnValue(Articulo.STOCK,cantidad);
        updateBuilder.update();


    }



    public static void actualizarFacturar(Context context, int id_articulo,boolean facturar) throws SQLException {

        cargarDao(context);
        UpdateBuilder<Articulo, Integer> updateBuilder = dao.updateBuilder();
        updateBuilder.where().eq(Articulo.ID_ARTICULO,id_articulo);
        updateBuilder.updateColumnValue(Articulo.FACTURAR,facturar);
        updateBuilder.update();


    }


    public static void actualizarPresupuestar(Context context, int id_articulo,boolean presupuestar) throws SQLException {

        cargarDao(context);
        UpdateBuilder<Articulo, Integer> updateBuilder = dao.updateBuilder();

        updateBuilder.where().eq(Articulo.ID_ARTICULO,id_articulo);
        updateBuilder.updateColumnValue(Articulo.PRESUPUESTAR,presupuestar);
        updateBuilder.update();


    }

    public synchronized static void actualizarStockPorfK(Context context, int fk_articulo,double cantidad) throws SQLException {

        cargarDao(context);
        UpdateBuilder<Articulo, Integer> updateBuilder = dao.updateBuilder();

        updateBuilder.where().eq(Articulo.FK_ARTICULO,fk_articulo);

        updateBuilder.updateColumnValue(Articulo.STOCK,cantidad);
        updateBuilder.update();


    }





    }

