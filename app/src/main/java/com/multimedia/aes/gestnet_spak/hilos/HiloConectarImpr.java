package com.multimedia.aes.gestnet_spak.hilos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.multimedia.aes.gestnet_spak.R;
import com.multimedia.aes.gestnet_spak.clases.Impresora;
import com.multimedia.aes.gestnet_spak.clases.PrinterCommunicator;
import com.multimedia.aes.gestnet_spak.clases.Ticket;
import com.multimedia.aes.gestnet_spak.constantes.Constantes;
import com.multimedia.aes.gestnet_spak.dialogo.Dialogo;
import com.multimedia.aes.gestnet_spak.printer_0554_0553.PrinterFactory;
import com.multimedia.aes.gestnet_spak.printer_0554_0553.PrinterHelper;
import com.sewoo.request.android.RequestHandler;
//import com.sewoo.request.android.RequestHandler;

import java.io.IOException;

public class HiloConectarImpr extends AsyncTask<BluetoothDevice, Void, String> {

	private Impresora impresora;
	private Activity activity;
	private Context context;
	private Ticket ticket;
    private ProgressDialog dialog;

	public HiloConectarImpr(Activity activity, Impresora impresora,Context context,Ticket ticket) {
		super();
        this.activity = activity;
        this.impresora=impresora;
        this.context=context;
        this.ticket=ticket;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(activity);
        dialog.setTitle(activity.getResources().getString(R.string.bluetooth));
        dialog.setMessage(activity.getResources().getString(R.string.conectando));
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        dialog.show();
    }
	@Override
	protected String doInBackground(BluetoothDevice... params) {
          try {
          impresora.bp.connect(params[0]);
            RequestHandler rh = new RequestHandler();
            impresora.hThread = new Thread(rh);
            impresora.hThread.start();
            impresora.realizarImpresionSeitron(ticket);
            return Constantes.SUCCES;
        } catch (IOException e) {
            return Constantes.ERROR;
        }

    }
    private void connectPrinter(String address) {
        try {
            PrinterCommunicator.getInstance().printerMacAddress = address;
            PrinterCommunicator.getInstance().btPrinterHelper
                    .connectTo(PrinterCommunicator.getInstance().printerMacAddress);
            if (PrinterCommunicator.getInstance().btPrinterHelper.isConnected()) {
                PrinterHelper helper = (PrinterHelper) PrinterCommunicator
                        .getInstance().btPrinterHelper;
                PrinterFactory factory = new PrinterFactory(helper);
                try {
                    PrinterCommunicator.getInstance().printer = factory
                            .getPrinter(address);
                } catch (Exception e) {
                    Toast.makeText(activity, "No supported Printer",
                            Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception ex) {
            Log.e("ERROR", "connectPrinter: cannot connect to device " + address
                    + ". Error: " + ex);
        }
    }
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
        if (result.equals(Constantes.SUCCES)) {
            Dialogo.impresionOk(activity);
            //((Index)context).datosActualizados();
        } else {
            Dialogo.errorConectarImpresora(activity);

        }
        dialog.dismiss();

    }

}
