package com.multimedia.aes.gestnet_spak.dialogo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

import com.multimedia.aes.gestnet_spak.R;

public class Dialogo {

    public static void dialogoErrorHtml(String texto, Context context) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(Html.fromHtml(texto, Html.FROM_HTML_MODE_LEGACY));
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });

        AlertDialog alert11 = builder1.create();
        //alert11.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert11.setCanceledOnTouchOutside(false);
        alert11.show();
    }
    public static void dialogoError(String texto, Context context) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(texto);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });

        AlertDialog alert11 = builder1.create();
        //alert11.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert11.setCanceledOnTouchOutside(false);
        alert11.show();
    }

    public static void errorConectarImpresora(Context context){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(R.string.err_impr);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.setCanceledOnTouchOutside(false);
        alert11.show();
    }

    public static void impresionOk(Context context){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(R.string.imp_ok);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.setCanceledOnTouchOutside(false);
        alert11.show();
    }


    public static void dialogHaySinSubir(final Context context) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("No se puede cambiar la fecha del listado de Mantenimientos debido a que tiene partes finalizados que no han sido informados a la central" + "\n" + "Porfavor informe de dichos partes");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Informar Partes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton(
                "Cerrar Ventana",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.setCanceledOnTouchOutside(false);
        alert11.show();
    }
    public static void impresoraSeleccionada(Context context){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Impresora seleccionada.");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.setCanceledOnTouchOutside(false);
        alert11.show();
    }
    public static void errorDuranteImpresion(Context context){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(R.string.err_durante_impr);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.setCanceledOnTouchOutside(false);
        alert11.show();
    }



    public static void errorCrearMaterial(Context context){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(R.string.err_crear_material);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.setCanceledOnTouchOutside(false);
        alert11.show();

    }

    public static void zonaEnConstruccion(final Context context) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Zona en construccion");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Continuar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton(
                "Volver",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.setCanceledOnTouchOutside(false);
        alert11.show();
    }


}