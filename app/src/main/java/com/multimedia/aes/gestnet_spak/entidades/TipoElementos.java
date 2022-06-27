package com.multimedia.aes.gestnet_spak.entidades;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "mos_tipos_elementos")
public class TipoElementos {
    public static final String ID  = "id_elemento";
    public static final String FK_TIPO = "fk_tipo";
    public static final String TIPO  = "tipo";
    public static final String TABLA = "tabla";
    public static final String CAMPO_ID  = "campo_id";
    public static final String CAMPOS  = "campos";

    @DatabaseField(generatedId = true, columnName = ID)                     private int id;
    @DatabaseField(columnName = TIPO)                                       private String tipo;
    @DatabaseField(columnName = TABLA)                                      private String tabla;
    @DatabaseField(columnName = CAMPO_ID)                                   private String campo_id;
    @DatabaseField(columnName = CAMPOS)                                     private String campos;
    @DatabaseField(columnName = FK_TIPO)                                    private int fk_tipo;

    public TipoElementos(){}

    public TipoElementos(String tipo, String tabla, String campo_id, String campos, int fk_tipo) {
        this.tipo = tipo;
        this.tabla = tabla;
        this.campo_id = campo_id;
        this.campos = campos;
        this.fk_tipo = fk_tipo;
    }

    public int getIdElemento() { return id; }
    public void setIdElemento(int id) { this.id = id; }

    public String getTipoElemento() { return tipo; }
    public void setTipoElemento(String tipo) { this.tipo = tipo; }

    public String getTablaElemento() { return tabla; }
    public void setTablaElemento(String tabla) { this.tabla = tabla; }

    public String getCampoIdElemento() { return campo_id; }
    public void setCampoIdElemento(String campo_id) { this.campo_id = campo_id; }

    public String getCamposElemento() { return campos; }
    public void setCamposElemento(String campos) { this.campos = campos; }

    public int getFkTipoElemento() { return fk_tipo; }
    public void setFkTipoElemento(int fk_tipo) { this.fk_tipo = fk_tipo; }
}
