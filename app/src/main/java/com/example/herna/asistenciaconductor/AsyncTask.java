package com.example.herna.asistenciaconductor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;

import static com.example.herna.asistenciaconductor.AsyncTask_BTinit_Dialog.connectedThread;
import static com.example.herna.asistenciaconductor.MainActivity.MAC;
import static com.example.herna.asistenciaconductor.MainActivity.mDevice;
import static com.example.herna.asistenciaconductor.MainActivity.mUUID;
import static com.example.herna.asistenciaconductor.MainActivity.pDialog;


class AsyncTask_BTinit_Dialog extends AsyncTask<Void, Integer, Boolean>
{
    BluetoothSocket mSocket = null;
    static public MainActivity.ConnectedThread connectedThread;
    static public AsyncTask_BT_RX Bluetooth_RX;

    @Override
    protected Boolean doInBackground(Void... params)
    {
        publishProgress(1*10);
        try
        {
            // Inicio la coexion Bluetooth
            mSocket = mDevice.createRfcommSocketToServiceRecord(mUUID);
            mSocket.connect();
            connectedThread = new MainActivity.ConnectedThread(mSocket);
            connectedThread.start();

        } catch (IOException e)
        {
            //      e.printStackTrace();
         //   Toast.makeText(MainActivity, "Error al conectar", Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
            return false;
        }

        publishProgress(10*10);

        if(isCancelled())
            return false;

        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values)
    {
        int progreso = values[0].intValue();

        pDialog.setProgress(progreso);
    }

    @Override
    protected void onPreExecute()
    {

        pDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog) {
                AsyncTask_BTinit_Dialog.this.cancel(true);
            }
        });

        pDialog.setProgress(0);
        pDialog.show();
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        if(result)
        {
            pDialog.dismiss();
            MainActivity.Bluetooth_Conectado=true;

            // Inicio la escucha de lo que me llega por Bluetooth
            Bluetooth_RX = new AsyncTask_BT_RX();
            Bluetooth_RX.execute();
        }
      //  else
     //       Toast.makeText(this., "Error al conectar", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCancelled() {
        //Toast.makeText(MainActivity.this, "Tarea cancelada!", Toast.LENGTH_SHORT).show();
    }
}

class AsyncTask_BT_RX extends AsyncTask<Void, Integer, Boolean>
{
    @Override
    protected Boolean doInBackground(Void... voids)
    {
        // Me quedo escuchando lo que viene por Bluetooth
        connectedThread.run();
        return null;
    }
}
