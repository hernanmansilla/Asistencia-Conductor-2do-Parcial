package com.example.herna.asistenciaconductor;

import android.os.AsyncTask;

public class AsyncTask_BT_RX extends AsyncTask<Void, Integer, Boolean>
{
    static public AsyncTask_BT_RX Bluetooth_RX;
    static public boolean Datos_Recibidos_BT;
    ConexionBluetooth conexionBluetooth;

    public AsyncTask_BT_RX (ConexionBluetooth conexion)
    {
       conexionBluetooth = conexion;
    }

    @Override
    protected Boolean doInBackground(Void... voids)
    {
        // Me quedo escuchando lo que viene por Bluetooth
        conexionBluetooth.run();
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        if(result)
        {
            // Inicio la escucha de lo que me llega por Bluetooth
      //      mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());
            Datos_Recibidos_BT = true;
            Bluetooth_RX = new AsyncTask_BT_RX(conexionBluetooth);
            Bluetooth_RX.execute();
        }
    }
}
