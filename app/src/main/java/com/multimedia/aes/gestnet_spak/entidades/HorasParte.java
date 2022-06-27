package com.multimedia.aes.gestnet_spak.entidades;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "mos_horas_parte")
public class HorasParte {

    public static final String ID_HORA="id_hora";
    public static final String ID_GESTNET="id_gestnet";
    public static final String FK_PARTE="fk_parte";
    public static final String FK_TECNICO="fk_tecnico";
    public static final String FK_REINICIO="fk_reinicio";
    public static final String HORA_INICIO="hora_inicio";
    public static final String HORA_FIN="hora_fin";
    public static final String FECHA="fecha";
    public static final String FECHA_VISITA="fecha_visita";
    public static final String TIPO="tipo";

    @DatabaseField(generatedId=true,columnName = ID_HORA) private int id_hora;
    @DatabaseField(columnName = ID_GESTNET) private int id_gestnet;
    @DatabaseField(columnName = FK_PARTE) private int fk_parte;
    @DatabaseField(columnName = FK_TECNICO) private int fk_tecnico;
    @DatabaseField(columnName = FK_REINICIO) private int fk_reinicio;
    @DatabaseField(columnName = HORA_INICIO) private String hora_inicio;
    @DatabaseField(columnName = HORA_FIN) private String hora_fin;
    @DatabaseField(columnName = FECHA) private String fecha;
    @DatabaseField(columnName = FECHA_VISITA) private String fecha_visita;
    @DatabaseField(columnName = TIPO) private int tipo;

    public HorasParte(){}

    public HorasParte(int id_gestnet,int fk_parte,int fk_tecnico ,int fk_reinicio,String hora_inicio, String hora_fin, String fecha,String fecha_visita ,int tipo) {
        this.id_gestnet = id_gestnet;
        this.fk_parte = fk_parte;
        this.fk_tecnico = fk_tecnico;
        this.fk_reinicio= fk_reinicio;
        this.hora_inicio = hora_inicio;
        this.hora_fin = hora_fin;
        this.fecha = fecha;
        this.fecha_visita = fecha_visita;
        this.tipo = tipo;
    }

    public int getId_hora() {
        return id_hora;
    }



    public void setId_gestnet(int id_gestnet) {
        this.id_gestnet = id_gestnet;
    }
    public int getId_gestnet() {
        return id_gestnet;
    }

    public void setFk_parte(int fk_parte) {
        this.fk_parte = fk_parte;
    }

    public int getFk_parte() {
        return fk_parte;
    }

    public void setFkTecnico(int fk_tecnico) {
        this.fk_tecnico = fk_tecnico;
    }

    public int getFk_tecnico() {
        return fk_tecnico;
    }

    public void setFk_reinicio(int fk_pausa) {
        this.fk_reinicio = fk_pausa;
    }

    public int getFk_reinicio() {
        return fk_reinicio;
    }

    public void setHora_inicio(String hora_inicio) {
        this.hora_inicio = hora_inicio;
    }

    public String getHora_inicio() {
        return hora_inicio;
    }

    public void setHora_fin(String hora_fin) {
        this.hora_fin = hora_fin;
    }

    public String getHora_fin() {
        return hora_fin;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFechaVisita() {
        return fecha_visita;
    }

    public void setFechaVisita(String fechaVisita) {
        this.fecha_visita = fechaVisita;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}
