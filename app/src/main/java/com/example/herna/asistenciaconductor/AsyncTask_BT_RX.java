package com.example.herna.asistenciaconductor;

import android.os.AsyncTask;

import java.io.IOException;

import static com.example.herna.asistenciaconductor.ConexionBluetooth.mmInStream;
import static com.example.herna.asistenciaconductor.ConexionBluetooth.estado_rx_bluetooth;

//*****************************************************************************
// Clase para manejar la tarea que escucha los datos de Bluetooth entrantes
//*****************************************************************************
public class AsyncTask_BT_RX extends AsyncTask<Void, Integer, Boolean>
{
    static public AsyncTask_BT_RX Bluetooth_RX;
    static public boolean Datos_Recibidos_BT;
    public byte[] buffer_rx_BT = new byte[256];  // buffer store for the stream
    static public ConexionBluetooth conexionBluetooth;
    int i;

    //*****************************************************************************
    // Constructor de la clase
    //*****************************************************************************
    public AsyncTask_BT_RX (ConexionBluetooth conexion)
    {
       conexionBluetooth = conexion;
    }

    //*****************************************************************************
    // Metodo que ejecuta la tarea principal de la AsyncTask
    //*****************************************************************************
    @Override
    protected Boolean doInBackground(Void... voids)
    {
        // Me quedo escuchando lo que viene por Bluetooth
        conexionBluetooth.run();
        return true;
    }

    //*****************************************************************************
    // // Metodo que se ejecuta una vez finalizado la ejecucion de la tarea principal
    //*****************************************************************************
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
