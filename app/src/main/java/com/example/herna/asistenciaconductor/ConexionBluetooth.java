package com.example.herna.asistenciaconductor;

import android.app.NotificationManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static com.example.herna.asistenciaconductor.AsyncTask_BT_RX.Datos_Recibidos_BT;
import static com.example.herna.asistenciaconductor.MainActivity.NOTIF_ALERTA_ID;
import static com.example.herna.asistenciaconductor.MainActivity.mBuilder;
import static com.example.herna.asistenciaconductor.MainActivity.mNotificationManager;

public class ConexionBluetooth extends Thread
{

    public byte[] buffer_rx_BT = new byte[256];  // buffer store for the stream
    public int estado_rx_bluetooth=0;
    int Indice_RX_BT=0;
    private String DNI_Chofer=null;
    private String Latitud_Infraccion=null;
    private String Longitud_Infraccion=null;
    static byte [] DNI_RX_BT = new byte[8];
    static public int Cantidad_Infracciones_RX_BT;
    static public byte []  Velocidad_infraccion_RX_BT = new byte[10];
    static public byte [] Latitud_Infraccion_RX_BT= new byte[10];
    static public byte [] Longitud_Infraccion_RX_BT= new byte[10];
    public int CANTIDAD_MAXIMA_CHOFERES=4;
    int i;

    private final InputStream mmInStream;
    private final OutputStream mmOutStream;

    public ConexionBluetooth(BluetoothSocket socket)
    {
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
        }

        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    public void run()
    {
        boolean espero_datos = true;
        int bytes_recibidos; // bytes returned from read()

        // Keep listening to the InputStream until an exception occurs
        while (espero_datos == true) {
            try {
                // Read from the InputStream
                bytes_recibidos = mmInStream.read(buffer_rx_BT);

                if (bytes_recibidos > 0)
                {
                    bytes_recibidos = buffer_rx_BT[2];
                    // Recorro los datos hasta el byte que me indica la cantidad que tiene el buffer
                    for (i = 0; i <= bytes_recibidos; i++)
                    {
                        Recepcion_Datos_Bluetooth(buffer_rx_BT[i]);
                        buffer_rx_BT[i] = 0;
                    }
                    estado_rx_bluetooth = 0;
                }

            } catch (IOException e) {
                //    break;
            }
            return;
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void enviar_byte(byte[] bytes)
    {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void enviar_string(String datos)
    {
        byte[] msgBuffer = datos.getBytes();
        try {
            mmOutStream.write(msgBuffer);
        } catch (IOException e) {
        }
    }

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
                    //Analizar_datos_Bluetooth();
                } else {
                    estado_rx_bluetooth = 0;
                    i = 100;
                }
                break;

            default:
                break;
        }
    }
/*
    public void Analizar_datos_Bluetooth() {
        int i;

        for (i = 0; i < CANTIDAD_MAXIMA_CHOFERES; i++) {
            if (DNI_Chofer.equals(ListaUsuariosPrincipal.get(i).getDNI()))
            {
                switch (i) {
                    case 0:
                        //                 adapter = new AdaptadorRecyclerViewPrincipal(ListaUsuariosPrincipal);
                        //                 ListaUsuariosPrincipal.add(new DatosRecyclerViewPrincipal("Hernan","34235547",R.drawable.ic_check_box));
                        //                 recyclerUsuarios.setAdapter(adapter);
                        i = CANTIDAD_MAXIMA_CHOFERES;
                        break;

                    case 1:
                        ListaUsuariosPrincipal.add(new DatosRecyclerViewPrincipal("German", "12345678", R.drawable.ic_check_box));
                        recyclerUsuarios.setAdapter(adapter);
                        i = CANTIDAD_MAXIMA_CHOFERES;
                        break;
                    case 2:
                        ListaUsuariosPrincipal.add(new DatosRecyclerViewPrincipal("Facundo", "34500600", R.drawable.ic_check_box));
                        recyclerUsuarios.setAdapter(adapter);
                        i = CANTIDAD_MAXIMA_CHOFERES;
                        break;
                    case 3:
                        ListaUsuariosPrincipal.add(new DatosRecyclerViewPrincipal("Gaston", "30266999", R.drawable.ic_check_box));
                        recyclerUsuarios.setAdapter(adapter);
                        i = CANTIDAD_MAXIMA_CHOFERES;
                        break;
                }
            }
        }
    }*/
}
