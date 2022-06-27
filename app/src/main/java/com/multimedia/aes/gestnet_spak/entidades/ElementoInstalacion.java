package com.multimedia.aes.gestnet_spak.entidades;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "mos_elementosinstalacion")
public class ElementoInstalacion {

    public static final String ID_ELEMENTO  = "_id_elemento";
    public static final String FK_MAQUINA  = "fk_maquina";
    public static final String FK_PARTE  = "fk_parte";
    public static final String FK_TIPO  = "fk_tipo";
    public static final String NOMBRE_TIPO  = "nombre_tipo";
    public static final String TABLA = "tabla";
    public static final String VALORES  = "valores";
    public static final String REGISTRO  = "registro";
    public static final String VALORESGESTNET  = "valores_gestnet";
    public static final String FK_ELEMENTO_GESTNET  = "fk_elemento_gestnet";
    public static final String FK_OPERACION  = "fk_operacion";
    public static final String BFINALIZADO  = "bfinalizado";
    public static final String ACTIVO  = "activo";



    @DatabaseField(generatedId = true, columnName = ID_ELEMENTO)            private int id_elemento;
    @DatabaseField(columnName = FK_MAQUINA)                                 private int fk_maquina;
    @DatabaseField(columnName = FK_PARTE)                                   private int fk_parte;
    @DatabaseField(columnName = FK_TIPO)                                    private int fk_tipo;
    @DatabaseField(columnName = NOMBRE_TIPO)                                private String nombre_tipo;
    @DatabaseField(columnName = TABLA)                                      private String tabla;
    @DatabaseField(columnName = VALORES)                                    private String valores;
    @DatabaseField(columnName = REGISTRO)                                   private String registro;
    @DatabaseField(columnName = VALORESGESTNET)                             private String valores_gestnet;
    @DatabaseField(columnName = FK_ELEMENTO_GESTNET)                        private int fk_elemento_gestnet;
    @DatabaseField(columnName = FK_OPERACION)                               private int fk_operacion;
    @DatabaseField(columnName = BFINALIZADO)                                private boolean bfinalizado;
    @DatabaseField(columnName = ACTIVO)                                     private boolean activo;

    public ElementoInstalacion(){}

    public ElementoInstalacion(int fk_maquina, int fk_parte, int fk_tipo, String nombre_tipo, String tabla,
                               String valores, int fk_elemento_gestnet , String registro, String valores_gestnet,int fk_operacion,boolean bfinalizado,boolean activo) {
        this.fk_maquina = fk_maquina;
        this.fk_parte = fk_parte;
        this.fk_tipo = fk_tipo;
        this.nombre_tipo = nombre_tipo;
        this.tabla = tabla;
        this.valores =  valores;
        this.fk_elemento_gestnet = fk_elemento_gestnet;
        this.registro = registro;
        this.valores_gestnet = valores_gestnet;
        this.fk_operacion = fk_operacion;
        this.bfinalizado = bfinalizado;
        this.activo = activo;

    }



    public int getId_elemento() {
        return id_elemento;
    }
    public void setId_elemento(int id_elemento) {
        this.id_elemento = id_elemento;
    }
    public int getFk_operacion() {
        return fk_operacion;
    }
    public void setFk_operacion(int fk_operacion) {
        this.fk_operacion = fk_operacion;
    }
    public boolean getbfinalizado() {
        return bfinalizado;
    }
    public void setbfinalizado(boolean bfinalizado) {
        this.bfinalizado = bfinalizado;
    }
    public int getFk_maquina() {
        return fk_maquina;
    }
    public void setFk_maquina(int fk_maquina) {
        this.fk_maquina = fk_maquina;
    }

    public int getFk_parte() {
        return fk_parte;
    }
    public void setFk_parte(int fk_parte) {
        this.fk_parte = fk_parte;
    }

    public int getFk_tipo() {
        return fk_tipo;
    }
    public void setFk_tipo(int fk_direccion) {
        this.fk_tipo = fk_tipo;
    }

    public String getNombre_tipo() {
        return nombre_tipo;
    }
    public void setNombre_tipo(String nombre_tipo) {
        this.nombre_tipo = nombre_tipo;
    }

    public String getTabla() {
        return tabla;
    }
    public void setTabla(String tabla) {
        this.tabla = tabla;
    }
    public  String getValores() {
        return valores;
    }
    public void setValores(String valores) {

     this.valores =  valores;

    }
    public  String getRegistro() {
        return registro;
    }
    public void setRegitro(String registro) {

        this.registro =  registro;

    }
    public  String getValoresgestnet() {
        return valores_gestnet;
    }
    public void setRValoresgestnet(String valores_gestnet) {

        this.valores_gestnet =  valores_gestnet;

    }
    public int getFk_elemento_gestnet() {
        return fk_elemento_gestnet;
    }
    public void setFk_elemento_gestnet(int fk_elemento_gestnet) {
        this.fk_elemento_gestnet = fk_elemento_gestnet;
    }
    public boolean getActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

}