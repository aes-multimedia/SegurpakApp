package com.multimedia.aes.gestnet_spak.entidades;

import com.j256.ormlite.field.DatabaseField;

public class Articulo {

    public static final String ID_ARTICULO = "_id_articulo";
    public static final String FK_ARTICULO = "fk_articulo";
    public static final String NOMBRE_ARTICULO = "nombre_articulo";
    public static final String STOCK = "stock";
    public static final String REFERENCIA = "referencia";
    public static final String REFERENCIA_AUX = "referencia_aux";
    public static final String FAMILIA = "familia";
    public static final String MARCA = "marca";
    public static final String MODELO = "modelo";
    public static final String PROVEEDOR = "proveedor";
    public static final String IVA = "iva";
    public static final String TARIFA = "tarifa";
    public static final String DESCUENTO = "descuento";
    public static final String COSTE = "coste";
    public static final String EAN = "ean";
    public static final String IMAGEN = "imagen";
    public static final String GARANTIA = "garantia";
    public static final String ENTREGADO ="entregado";
    public static final String PRESUPUESTAR="presupuestar";
    public static final String FACTURAR = "facturar";
    public static final String ID_ITEM_GESTNET = "id_item_gestnet";

    @DatabaseField(generatedId = true, columnName = ID_ARTICULO)        private int id_articulo;
    @DatabaseField(columnName = ID_ITEM_GESTNET)                        private int id_item_gestnet;
    @DatabaseField(columnName = FK_ARTICULO)                            private int fk_articulo;
    @DatabaseField(columnName = NOMBRE_ARTICULO)                        private String nombre_articulo;
    @DatabaseField(columnName = STOCK)                                  private double stock;
    @DatabaseField(columnName = REFERENCIA)                             private String referencia;
    @DatabaseField(columnName = REFERENCIA_AUX)                         private String referencia_aux;
    @DatabaseField(columnName = FAMILIA)                                private String familia;
    @DatabaseField(columnName = MARCA)                                  private String marca;
    @DatabaseField(columnName = MODELO)                                 private String modelo;
    @DatabaseField(columnName = PROVEEDOR)                              private int proveedor;
    @DatabaseField(columnName = IVA)                                    private double iva;
    @DatabaseField(columnName = TARIFA)                                 private double tarifa;
    @DatabaseField(columnName = DESCUENTO)                              private double descuento;
    @DatabaseField(columnName = COSTE)                                  private double coste;
    @DatabaseField(columnName = EAN)                                    private String ean;
    @DatabaseField(columnName = IMAGEN)                                 private int imagen;
    @DatabaseField(columnName = GARANTIA)                               private boolean garantia;
    @DatabaseField(columnName = ENTREGADO)                              private boolean entregado;
    @DatabaseField(columnName = PRESUPUESTAR)                           private boolean presupuestar;
    @DatabaseField(columnName = FACTURAR)                               private boolean facturar;

    public Articulo() {
    }

    public Articulo(int id_item_gestnet,int fk_articulo, String nombre_articulo,double stock, String referencia, String referencia_aux, String familia,
                    String marca, String modelo, int proveedor, double iva, double tarifa, double descuento, double coste, String ean,int imagen) {
        this.id_item_gestnet = id_item_gestnet;
        this.fk_articulo = fk_articulo;
        this.nombre_articulo = nombre_articulo;
        this.stock = stock;
        this.referencia = referencia;
        this.referencia_aux = referencia_aux;
        this.familia = familia;
        this.marca = marca;
        this.modelo = modelo;
        this.proveedor = proveedor;
        this.iva = iva;
        this.tarifa = tarifa;
        this.descuento = descuento;
        this.coste = coste;
        this.ean = ean;
        this.imagen = imagen;
        this.garantia= false;
        this.entregado=false;
        this.presupuestar=true;
        this.facturar=true;
    }


    public Articulo(int fk_articulo, String nombre_articulo,double stock, double coste,String referencia, String referencia_aux,String ean,double iva, double tarifa, double descuento) {
        this.fk_articulo = fk_articulo;
        this.nombre_articulo = nombre_articulo;
        this.stock = stock;
        this.coste = coste;
        this.referencia = referencia;
        this.referencia_aux = referencia_aux;
        this.ean = ean;
        this.iva = iva;
        this.tarifa = tarifa;
        this.descuento = descuento;
        this.garantia=false;
        this.entregado=false;
        this.presupuestar=true;
        this.facturar=true;
    }

    public Articulo(int fk_articulo, String nombre_articulo,double stock, String referencia, String referencia_aux, String familia,
                    String marca, String modelo, int proveedor, double iva, double tarifa, double descuento, double coste, String ean,int imagen,boolean entregado, boolean garantia) {
        this.fk_articulo = fk_articulo;
        this.nombre_articulo = nombre_articulo;
        this.stock = stock;
        this.referencia = referencia;
        this.referencia_aux = referencia_aux;
        this.familia = familia;
        this.marca = marca;
        this.modelo = modelo;
        this.proveedor = proveedor;
        this.iva = iva;
        this.tarifa = tarifa;
        this.descuento = descuento;
        this.coste = coste;
        this.ean = ean;
        this.imagen = imagen;
        this.garantia= garantia;
        this.entregado=entregado;
        this.presupuestar=true;
        this.facturar=true;
    }

    public boolean isGarantia() {
        return garantia;
    }

    public void setGarantia(boolean garantia) {
        this.garantia = garantia;
    }

    public int  getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public int getId_articulo() {
        return id_articulo;
    }

    public void setId_articulo(int id_articulo) {
        this.id_articulo = id_articulo;
    }

    public int getFk_articulo() {return fk_articulo;}

    public int getId_item_gestnet() {return id_item_gestnet;}

    public void setFk_articulo(int fk_articulo) {
        this.fk_articulo = fk_articulo;
    }

    public String getNombre_articulo() {
        return nombre_articulo;
    }

    public void setNombre_articulo(String nombre_articulo) {
        this.nombre_articulo = nombre_articulo;
    }
    public void setId_item_gestnet(int id_item_gestnet) {
        this.id_item_gestnet = id_item_gestnet;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getReferencia_aux() {
        return referencia_aux;
    }

    public void setReferencia_aux(String referencia_aux) {
        this.referencia_aux = referencia_aux;
    }

    public String getFamilia() {
        return familia;
    }

    public void setFamilia(String familia) {
        this.familia = familia;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getProveedor() {
        return proveedor;
    }

    public void setProveedor(int proveedor) {
        this.proveedor = proveedor;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva = iva;
    }

    public double getTarifa() {
        return tarifa;
    }

    public void setTarifa(double tarifa) {
        this.tarifa = tarifa;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getCoste() {
        return coste;
    }

    public void setCoste(double coste) {
        this.coste = coste;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public int isEntregado() {

        if(!entregado)return 0;
            else return 1;


    }

    public void setEntregado(boolean entregado) {
        this.entregado = entregado;
    }


    public boolean isPresupuestar() {
        return presupuestar;
    }

    public void setPresupuestar(boolean presupuestar) {
        this.presupuestar = presupuestar;
    }

    public boolean isFacturar() {
        return facturar;
    }

    public void setFacturar(boolean facturar) {
        this.facturar = facturar;
    }
}
