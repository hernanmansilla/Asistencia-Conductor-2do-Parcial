package com.example.herna.asistenciaconductor;

import android.os.AsyncTask;

import java.io.IOException;

import static com.example.herna.asistenciaconductor.ConexionBluetooth.mmInStream;
import static com.example.herna.asistenciaconductor.ConexionBluetooth.estado_rx_bluetooth;

public class AsyncTask_BT_RX extends AsyncTask<Void, Integer, Boolean>
{
    static public AsyncTask_BT_RX Bluetooth_RX;
    static public boolean Datos_Recibidos_BT;
    public byte[] buffer_rx_BT = new byte[256];  // buffer store for the stream
    static public ConexionBluetooth conexionBluetooth;
    int i;

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
      /*  boolean espero_datos = true;
        int bytes_recibidos; // bytes returned from read()

        while (espero_datos == true)
        {
            try {
                // Leo del InputStream
                bytes_recibidos = mmInStream.read(buffer_rx_BT);

                if (bytes_recibidos > 0)
                {
                    bytes_recibidos = buffer_rx_BT[2];

                    // Recorro los datos hasta el byte que me indica la cantidad que tiene el buffer
                    for (i = 0; i <= bytes_recibidos; i++)
                    {
                        // Maquina de estado para la recepcion de los bytes
                        conexionBluetooth.Recepcion_Datos_Bluetooth(buffer_rx_BT[i]);
                        buffer_rx_BT[i] = 0;
                    }
                    bytes_recibidos=0;
                    estado_rx_bluetooth = 0;
                    // espero_datos=false;
                    return true;
                }
            } catch (IOException e) {
                //    break;
            }
        }*/
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
