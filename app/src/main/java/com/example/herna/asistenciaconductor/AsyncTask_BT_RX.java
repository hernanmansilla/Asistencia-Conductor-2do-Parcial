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

    // Funcion que ejecuta la tarea principal de la AsyncTask
    @Override
    protected Boolean doInBackground(Void... voids)
    {
        // Me quedo escuchando lo que viene por Bluetooth
        conexionBluetooth.run();
        return true;
    }

    // Funcion que se ejecuta una vez finalizado la ejecucion de la tarea principal
    @Override
    protected void onPostExecute(Boolean result)
    {
        if(result)
        {
            Datos_Recibidos_BT = true;

            // Inicio una nueva escucha de lo que me llega por Bluetooth
            Bluetooth_RX = new AsyncTask_BT_RX(conexionBluetooth);
            Bluetooth_RX.execute();
        }
    }
}
