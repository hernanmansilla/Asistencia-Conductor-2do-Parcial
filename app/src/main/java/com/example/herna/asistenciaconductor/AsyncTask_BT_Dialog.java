package com.example.herna.asistenciaconductor;

import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.AsyncTask;
import java.io.IOException;
import static com.example.herna.asistenciaconductor.MainActivity.mDevice;
import static com.example.herna.asistenciaconductor.MainActivity.mUUID;
import static com.example.herna.asistenciaconductor.MainActivity.pDialog;

//*****************************************************************************
// Clase para crear una nueva tarea que inicia la comunicacion Bluettoth
//*****************************************************************************
public class AsyncTask_BT_Dialog extends AsyncTask<Void, Integer, Boolean>
{
    BluetoothSocket mSocket = null;
    public ConexionBluetooth conexionBluetooth;
    static public AsyncTask_BT_RX Bluetooth_RX;

    //*****************************************************************************
    // Metodo que ejecuta la tarea principal de la AsyncTask
    //*****************************************************************************
    @Override
    protected Boolean doInBackground(Void... params)
    {
        publishProgress(1*10);
        try
        {
            // Inicio la conexion Bluetooth
            mSocket = mDevice.createRfcommSocketToServiceRecord(mUUID);
            mSocket.connect();
            conexionBluetooth = new ConexionBluetooth(mSocket);
            conexionBluetooth.start();

        } catch (IOException e)
        {
            pDialog.dismiss();
            return false;
        }

        // Seteo el progreso de la barra
        publishProgress(10*10);

        if(isCancelled())
            return false;

        return true;
    }

    //*****************************************************************************
    // Metodo para actualizar el estado de la barra
    //*****************************************************************************
    @Override
    protected void onProgressUpdate(Integer... values)
    {
        int progreso = values[0].intValue();

        pDialog.setProgress(progreso);
    }

    //*****************************************************************************
    // Metodo que se ejecuta antes de ejecutar la tarea principal
    //*****************************************************************************
    @Override
    protected void onPreExecute()
    {
        // Metodo que se ejecuta antes de la tarea, creo la barra de progreso
        pDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog) {
                AsyncTask_BT_Dialog.this.cancel(true);
            }
        });
        pDialog.setProgress(0);
        pDialog.show();
    }

    //*****************************************************************************
    // Metodo que se ejecuta una vez finalizado la ejecucion de la tarea principal
    //*****************************************************************************
    @Override
    protected void onPostExecute(Boolean result)
    {
        if(result)
        {
            // Una vez que se conecte lanzo una nueva AsyncTask para escuchar los datos
            // Bluetooth que recibo
            pDialog.dismiss();
            MainActivity.Bluetooth_Conectado=true;

            // Inicio la escucha de lo que me llega por Bluetooth
            Bluetooth_RX = new AsyncTask_BT_RX(conexionBluetooth);
            Bluetooth_RX.execute();
        }
    }

    //*****************************************************************************
    // Metodo para cancelar la tarea
    //*****************************************************************************
    @Override
    protected void onCancelled() {
    }

}

