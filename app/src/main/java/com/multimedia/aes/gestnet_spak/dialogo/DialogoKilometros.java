package com.multimedia.aes.gestnet_spak.dialogo;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.multimedia.aes.gestnet_spak.R;
import com.multimedia.aes.gestnet_spak.SharedPreferences.GestorSharedPreferences;
import com.multimedia.aes.gestnet_spak.hilos.HiloKilometros;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DialogoKilometros extends DialogFragment implements View.OnClickListener {

    private View v;
    private Button btnAceptar,btnCancelar;
    private EditText etKilometros,etLitros;
    int id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    public static DialogoKilometros newInstance(int id) {
        DialogoKilometros fragment = new DialogoKilometros();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.dialogo_kilometros, container, false);
        id = getArguments().getInt("id");
        etKilometros = v.findViewById(R.id.etKilometros);
        etLitros = v.findViewById(R.id.etLitros);
        btnAceptar = v.findViewById(R.id.btnAceptar);
        btnCancelar = v.findViewById(R.id.btnCancelar);
        btnAceptar.setOnClickListener(this);
        btnCancelar.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnAceptar) {
            if (!comprobarRelleno()){
                Dialogo.dialogoError("Rellene todos los campos.",getContext());
            }
        }else if (view.getId() == R.id.btnCancelar) {
            getDialog().dismiss();
        }

    }

    private boolean comprobarRelleno(){
        if (etKilometros.getText().toString().equals(""))return false;
        if (etLitros.getText().toString().equals(""))return false;
        new HiloKilometros(getContext(),this,id,Double.parseDouble(etLitros.getText().toString()),Double.parseDouble(etKilometros.getText().toString())).execute();
        return true;
    }
    public void errorHilo(String mensaje) throws JSONException {
        JSONObject jsonObject = new JSONObject(mensaje);
        Dialogo.dialogoError(jsonObject.getString("mensaje"),getContext());
        getDialog().dismiss();
    }
    public void hiloBien(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setMessage("Se han subido correctamente los datos");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        String strDate = sdf.format(c.getTime());
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("fecha",strDate);
                            GestorSharedPreferences.setJsonKilometros(GestorSharedPreferences.getSharedPreferencesKilometros(getContext()),jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getDialog().dismiss();
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.setCanceledOnTouchOutside(false);
        alert11.show();
    }
}
