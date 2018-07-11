package com.example.herna.asistenciaconductor;

import android.app.NotificationManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.example.herna.asistenciaconductor.AsyncTask_BT_RX.Datos_Recibidos_BT;
import static com.example.herna.asistenciaconductor.MainActivity.ListaUsuariosPrincipal;
import static com.example.herna.asistenciaconductor.MainActivity.NOTIF_ALERTA_ID;
import static com.example.herna.asistenciaconductor.MainActivity.adapter;
import static com.example.herna.asistenciaconductor.MainActivity.mBuilder;
import static com.example.herna.asistenciaconductor.MainActivity.mNotificationManager;

public class ConexionBluetooth extends Thread
{
    public byte[] buffer_rx_BT = new byte[256];  // buffer store for the stream
    static public int estado_rx_bluetooth=0;
    int Indice_RX_BT=0;
    private String DNI_Chofer=null;
    private String Latitud_Infraccion=null;
    private String Longitud_Infraccion=null;
    static byte [] DNI_RX_BT = new byte[8];
    static public int Cantidad_Infracciones_RX_BT;
    static public byte []  Velocidad_infraccion_RX_BT = new byte[10];
    static public byte [] Latitud_Infraccion_RX_BT= new byte[10];
    static public byte [] Longitud_Infraccion_RX_BT= new byte[10];
    static public int CANTIDAD_MAXIMA_CHOFERES = 4;
    static public byte [] Usuario_habilitado = new byte[CANTIDAD_MAXIMA_CHOFERES];

    int i;

    static public InputStream mmInStream;
    static public OutputStream mmOutStream;

    // Constructor de la clase
    public ConexionBluetooth(BluetoothSocket socket)
    {
        // Creo los flujos donde se van a transferir la comunicacion Bluetooth
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    // Funcion que queda a la escucha de la llegada de datos Bluetooth
    public void run()
    {
        boolean espero_datos = true;
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
                        Recepcion_Datos_Bluetooth(buffer_rx_BT[i]);
                        buffer_rx_BT[i] = 0;
                    }
                    bytes_recibidos=0;
                    estado_rx_bluetooth = 0;
                   // espero_datos=false;
                    return;
                }
            } catch (IOException e) {
                //    break;
            }
        }
        return;
    }

    // Funcion para enviar un array de bytes
    public void enviar_byte(byte[] bytes)
    {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
        }
    }

    // Funcion para enviar un String
    public void enviar_string(String datos)
    {
        byte[] msgBuffer = datos.getBytes();
        try {
            mmOutStream.write(msgBuffer);
        } catch (IOException e) {
        }
    }

    // Maquina de estados para la recepcion de cada byte entrante
    public void Recepcion_Datos_Bluetooth(byte dato)
    {
        switch (estado_rx_bluetooth) {
            case 0:
                if (dato == '<') {
                    estado_rx_bluetooth = 1;
                }
                break;

            case 1:

                if (dato == 'R') {
                    estado_rx_bluetooth = 2;
                } else {
                    estado_rx_bluetooth = 0;
                    i = 100;
                }
                break;

            case 2:

                // Aca recibo la cantidad de bites, no me interesan
                estado_rx_bluetooth = 3;
                break;

            case 3:

                // Recibo el numero de dni
                DNI_RX_BT[Indice_RX_BT] = dato;
                DNI_Chofer = new String(DNI_RX_BT);
                Indice_RX_BT++;

                if (Indice_RX_BT >= 8) {
                    estado_rx_bluetooth = 4;
                    Indice_RX_BT = 0;
                }
                break;

            case 4:

                Latitud_Infraccion_RX_BT[Indice_RX_BT] = dato;

                Indice_RX_BT++;

                if (Indice_RX_BT >= 10) {
                    Latitud_Infraccion = new String(Latitud_Infraccion_RX_BT);
                    estado_rx_bluetooth = 5;
                    Indice_RX_BT = 0;
                }
                break;

            case 5:

                Longitud_Infraccion_RX_BT[Indice_RX_BT] = dato;
                Indice_RX_BT++;

                if (Indice_RX_BT >= 10) {
                    Longitud_Infraccion = new String(Longitud_Infraccion_RX_BT);
                    estado_rx_bluetooth = 6;
                    Indice_RX_BT = 0;
                }
                break;

            case 6:

                Cantidad_Infracciones_RX_BT = dato;
                estado_rx_bluetooth = 7;

                break;

            case 7:

                Velocidad_infraccion_RX_BT[Indice_RX_BT] = dato;
                Indice_RX_BT++;

                if (Indice_RX_BT > Cantidad_Infracciones_RX_BT) {
                    estado_rx_bluetooth = 8;
                    Indice_RX_BT = 0;
                }
                break;

            case 8:

                if (dato == '>') {
                    estado_rx_bluetooth = 0;
                    Datos_Recibidos_BT = true;
                    mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());
                    Analizar_datos_Bluetooth();
                } else {
                    estado_rx_bluetooth = 0;
                    i = 100;
                }
                break;

            default:
                break;
        }
    }

    public void Analizar_datos_Bluetooth() {
        int i;

        for (i = 0; i < CANTIDAD_MAXIMA_CHOFERES; i++)
        {
            if (DNI_Chofer.equals(ListaUsuariosPrincipal.get(i).getDNI()))
            {
                switch (i)
                {
                    case 0:
                        Usuario_habilitado[0] = 1;
                        i = CANTIDAD_MAXIMA_CHOFERES;
                        break;

                    case 1:
                        Usuario_habilitado[1] = 1;
                        i = CANTIDAD_MAXIMA_CHOFERES;
                        break;
                    case 2:
                        Usuario_habilitado[2] = 1;
                        i = CANTIDAD_MAXIMA_CHOFERES;
                        break;
                    case 3:
                        Usuario_habilitado[3] = 1;
                        i = CANTIDAD_MAXIMA_CHOFERES;
                        break;
                }
            }
        }
    }
}
