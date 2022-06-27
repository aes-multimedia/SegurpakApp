package com.multimedia.aes.gestnet_spak.entidades;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "mos_tipos_reparaciones")
public class OperacionesTiposElementos {

    public static final String ID  = "_id_elemento";
    public static final String FK_TIPO  = "fk_tipo";
    public static final String NOMBRE_TIPO = "nombreTipo";
    public static final String CAMPOS  = "campos";

    @DatabaseField(generatedId = true, columnName = ID)                     private int id;
    @DatabaseField(columnName = FK_TIPO)                                    private int fk_tipo;
    @DatabaseField(columnName = NOMBRE_TIPO)                                private String nombreTipo;
    @DatabaseField(columnName = CAMPOS)                                     private String campos;


    public OperacionesTiposElementos(){}

    public OperacionesTiposElementos(int fk_tipo, String nombreTipo, String campos) {
        this.fk_tipo = fk_tipo;
        this.nombreTipo = nombreTipo;
        this.campos = campos;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getFk_tipo() {
        return fk_tipo;
    }
    public void setFk_tipo(int fk_tipo) {
        this.fk_tipo = fk_tipo;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }
    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public String getCampos() {
        return campos;
    }
    public void setFCampos(String campos) {
        this.campos = campos;
    }

}
