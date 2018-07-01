package com.example.herna.asistenciaconductor;

// https://www.youtube.com/watch?v=Vyqz_-sJGFk
// https://danielggarcia.wordpress.com/2013/10/19/bluetooth-i-activando-y-desactivando-el-bluetooth-en-android/
// https://code.tutsplus.com/es/tutorials/create-a-bluetooth-scanner-with-androids-bluetooth-api--cms-24084
// https://www.youtube.com/watch?v=q8b5WMnUO04
// http://yuliana.lecturer.pens.ac.id/Android/Buku/professional_android_4_application_development.pdf
//http://cursoandroidstudio.blogspot.com/2015/10/conexion-bluetooth-android-con-arduino.html

//https://www.youtube.com/watch?v=0tT6zKFfrfg
//https://medium.com/@victor.garibayy/obteniendo-mi-ubicaci%C3%B3n-en-android-studio-377226910823
//https://medium.com/@victor.garibayy/obteniendo-mi-ubicaci%C3%B3n-en-android-studio-377226910823

//https://www.youtube.com/watch?v=ipr7Y7lcYgA

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.location.Location;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.example.herna.asistenciaconductor.AsyncTask_BT_RX.Datos_Recibidos_BT;
import static com.example.herna.asistenciaconductor.AsyncTask_BTinit_Dialog.connectedThread;
import static com.example.herna.asistenciaconductor.Ubicacion.Latitud_GPS;
import static com.example.herna.asistenciaconductor.Ubicacion.Longitud_GPS;
import static com.example.herna.asistenciaconductor.Ubicacion.LocationManager;
import static com.example.herna.asistenciaconductor.Ubicacion.lc;

public class MainActivity extends AppCompatActivity {
    private ArrayList<DatosRecyclerViewPrincipal> ListaUsuariosPrincipal;
    private RecyclerView recyclerUsuarios;
    private static final int ENABLE_BLUETOOTH = 1;
    private static final int SOLICITA_CONEXION = 2;
    static public String MAC = null;
    static public BluetoothDevice mDevice = null;
    BluetoothAdapter mBluetoothAdapter = null;
    AsyncTask_BTinit_Dialog Bluetooth_init;
    // static public AsyncTask_BT_RX Bluetooth_RX;
    static public boolean Bluetooth_Conectado = false;
    static public boolean Bluetooth_Encendido = false;
    static public byte[] buffer_rx_BT = new byte[256];  // buffer store for the stream
    static public int Cant_bytes_rx_BT = 0; // bytes returned from read()
    static public UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    static public ProgressDialog pDialog;
    NotificationCompat.Builder mBuilder;
    static final int NOTIF_ALERTA_ID = 1;
    EditText Latitud_editText;
    EditText Longitud_editText;
    boolean gps_enabled = false, network_enabled = false;
    public Button Boton_GPS;
    static public int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    static public Context contexto_gral;
    static public Ubicacion ubicacion;
    static public int estado_rx_bluetooth=0;
    static byte [] Chofer_RX_BT;
    static byte [] DNI_RX_BT;
    static byte [] KM_Recorridos_RX_BT;
    static byte[] Cantidad_Infracciones_RX_BT;
    static byte [] Latitud_Infraccion_RX_BT;
    static byte [] Longitud_Infraccion_RX_BT;
    static int Indice_RX_BT=0;
    static int i;
    static String Nombre_Chofer=null;
    static String DNI_Chofer=null;
    static String Latitud_Infraccion=null;
    static String Longitud_Infraccion=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Latitud_editText = findViewById(R.id.Latitud);
        Longitud_editText = findViewById(R.id.Longitud);
        Boton_GPS = findViewById(R.id.button_GPS);

        ListaUsuariosPrincipal = new ArrayList<>();
        recyclerUsuarios = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(this));

        LlenarUsuarios();

        contexto_gral = getApplicationContext();
        ubicacion = new Ubicacion(this);

        // Registramos el BroadcastReceiver que instanciamos previamente para
        // detectar los distintos eventos que queremos recibir
        IntentFilter filtro = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(bReceiver, filtro);

        mBuilder = new NotificationCompat.Builder(MainActivity.this);
        mBuilder.setAutoCancel(true);
        //     mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        //     mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setSmallIcon(android.R.drawable.stat_sys_warning);
        mBuilder.setTicker("Datos Recibidos Bluetooth");
        mBuilder.setWhen(System.currentTimeMillis());
        //    mBuilder.setLargeIcon((((BitmapDrawable)getResources();
        //    mBuilder.getDrawable(R.drawable.ic_launcher_foreground)).getBitmap()));
        mBuilder.setContentTitle("Datos Recibidos Bluetooth");
        mBuilder.setContentText("Ejemplo de notificación.");
        //   mBuilder.setContentInfo("4");

        Chofer_RX_BT = new byte[10];
        DNI_RX_BT = new byte[8];
        KM_Recorridos_RX_BT = new byte[2];
        Cantidad_Infracciones_RX_BT = new byte[2];
        Latitud_Infraccion_RX_BT = new byte[11];
        Longitud_Infraccion_RX_BT = new byte[11];

        Intent Intent = new Intent(MainActivity.this, MainActivity.class);

        PendingIntent contIntent = PendingIntent.getActivity(MainActivity.this, 0, Intent, 0);

        mBuilder.setContentIntent(contIntent);

        final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        AdaptadorRecyclerViewPrincipal adapter = new AdaptadorRecyclerViewPrincipal(ListaUsuariosPrincipal);

        adapter.setOnItemClickListener(new AdaptadorRecyclerViewPrincipal.OnItemClickListener() {
            @Override
            public void onItemClick(int position)
            {
                BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

                if (!bluetooth.isEnabled())
                {
                    // Si el Bluetooth no esta activado, pregunto si quiero activarlo
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, ENABLE_BLUETOOTH);

                } else
                    {
                    Bluetooth_Encendido = true;

                    if (Bluetooth_Conectado == false) {
                        Intent activityListaDispositivos = new Intent(MainActivity.this, ListaDispositivos.class);
                        startActivityForResult(activityListaDispositivos, SOLICITA_CONEXION);
                    } else {
                        Toast.makeText(MainActivity.this, "Ya posee una conexion activa", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onDeleteClick(int position) {
                switch (position) {
                    case 0:
                        if (Bluetooth_Conectado == true && Bluetooth_Encendido == true) {
                            connectedThread.enviar_string("hola");

                            Toast.makeText(MainActivity.this, "Envie dato", Toast.LENGTH_SHORT).show();
                        } else {
                            mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());
                            Toast.makeText(MainActivity.this, "No posee una conexion Bluetooth " + position, Toast.LENGTH_SHORT).show();
                        }

                        break;

                    case 1:

                        if (Datos_Recibidos_BT == true) {
                            //   String datos_recibidos = Arrays.toString(buffer_rx_BT);
                            mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());
                            String datos_recibidos = new String(buffer_rx_BT);
                            Toast.makeText(MainActivity.this, "Datos recibidos: " + datos_recibidos, Toast.LENGTH_SHORT).show();
                            Datos_Recibidos_BT = false;
                            Cant_bytes_rx_BT = 0;
                        }
                        break;
                }
            }
        });

        recyclerUsuarios.setAdapter(adapter);

        // Linea de separacion entre items de la lista
        recyclerUsuarios.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        Boton_GPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Latitud_editText.setText(Latitud_GPS);
                Longitud_editText.setText(Longitud_GPS);
            }
        });
    }
/*
    @Override
    protected void onResume() {
        super.onResume();

        // Por si no estaba encendido el GPS en el momento de iniciar la aplicacion
        if ((!gps_enabled) && (LocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))) {
            // Si se encendio inicio los servicios para obtener la posicion
            gps_enabled = true;
            // Hago el chequeo de tener permiso para usar el gps
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                Toast.makeText(contexto_gral, "Sin permiso GPS", Toast.LENGTH_SHORT).show();
                return;
            }
       //     lc = LocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // Hago la peticion de tomar 1 segundo de muestras de GPS
       //     LocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, ubicacion.onLocationChanged(lc));
            Toast.makeText(MainActivity.this, "GPS encendido", Toast.LENGTH_SHORT).show();
        }
    }
*/
    // Instanciamos un BroadcastReceiver que se encargara de detectar si el estado
    // del Bluetooth del dispositivo ha cambiado mediante su handler onReceive
    private final BroadcastReceiver bReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            // Filtramos por la accion. Nos interesa detectar BluetoothAdapter.ACTION_STATE_CHANGED
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action))
            {
                // Solicitamos la informacion extra del intent etiquetada como BluetoothAdapter.EXTRA_STATE
                // El segundo parametro indicara el valor por defecto que se obtendra si el dato extra no existe
                final int estado = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (estado)
                {
                    case BluetoothAdapter.STATE_OFF:
                        Bluetooth_Encendido=false;
                        Bluetooth_Conectado=false;
                        Toast.makeText(MainActivity.this, "bluetooth apagado", Toast.LENGTH_SHORT).show();
                        break;

                    case BluetoothAdapter.STATE_ON:
                        Bluetooth_Encendido=true;
                        Toast.makeText(MainActivity.this, "Bluetooth encendido", Toast.LENGTH_SHORT).show();
                        break;

                    case BluetoothAdapter.STATE_DISCONNECTED:
                        Bluetooth_Conectado=false;
                        Toast.makeText(MainActivity.this, "Dispositivo desconectado", Toast.LENGTH_SHORT).show();
                        break;

                    case BluetoothAdapter.STATE_CONNECTED:
                        Bluetooth_Conectado=true;
                        Toast.makeText(MainActivity.this, "CONECTADO CON: " + MAC, Toast.LENGTH_SHORT).show();
                        break;

                    case BluetoothAdapter.ERROR:
                        Toast.makeText(MainActivity.this, "Error BT", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[]grantResults)
    {
        switch (requestCode)
        {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Ubicacion ubicacion = new Ubicacion(this);
                    // Toast.makeText(MainActivity.this, "Inicio Ubicacion", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case ENABLE_BLUETOOTH:

                if (resultCode == RESULT_OK)
                {
                    // Si entre aca es porque active el bluetooth
                    //Toast.makeText(MainActivity.this, "bluetooth encendido", Toast.LENGTH_SHORT).show();
                    Intent activityListaDispositivos = new Intent(MainActivity.this, ListaDispositivos.class);
                    startActivityForResult(activityListaDispositivos, SOLICITA_CONEXION);
                }
                break;

            case SOLICITA_CONEXION:

                if(resultCode == RESULT_OK)
                {
                    pDialog = new ProgressDialog(MainActivity.this);
                    pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pDialog.setMessage("Conectando dispositivo");
                    pDialog.setCancelable(true);
                    pDialog.setMax(100);

                    // Tomo la MAC de la lista de dispositivos aparejados para conectarme
                    MAC = data.getExtras().getString(ListaDispositivos.ENDERECO_MAC);

                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                    mDevice = mBluetoothAdapter.getRemoteDevice(MAC);

                    Bluetooth_init = new AsyncTask_BTinit_Dialog();
                    Bluetooth_init.execute();
                }
                break;

            case 3:
                Toast.makeText(MainActivity.this, "GPS encendido " , Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void LlenarUsuarios()
    {
        ListaUsuariosPrincipal.add(new DatosRecyclerViewPrincipal("Paisaje 1","Vamos Argentina 1",R.drawable.ic_launcher_foreground));
        ListaUsuariosPrincipal.add(new DatosRecyclerViewPrincipal("Paisaje 2","Vamos Argentina 2",R.drawable.ic_launcher_foreground));
        ListaUsuariosPrincipal.add(new DatosRecyclerViewPrincipal("Paisaje 3","Vamos Argentina 3",R.drawable.ic_launcher_foreground));
        ListaUsuariosPrincipal.add(new DatosRecyclerViewPrincipal("Paisaje 4","Vamos Argentina 4",R.drawable.ic_launcher_foreground));
        ListaUsuariosPrincipal.add(new DatosRecyclerViewPrincipal("Paisaje 1","Vamos Argentina 1",R.drawable.ic_launcher_foreground));
        ListaUsuariosPrincipal.add(new DatosRecyclerViewPrincipal("Paisaje 2","Vamos Argentina 2",R.drawable.ic_launcher_foreground));
        ListaUsuariosPrincipal.add(new DatosRecyclerViewPrincipal("Paisaje 3","Vamos Argentina 3",R.drawable.ic_launcher_foreground));
        ListaUsuariosPrincipal.add(new DatosRecyclerViewPrincipal("Paisaje 4","Vamos Argentina 4",R.drawable.ic_launcher_foreground));

    }

    /*
        private void updateUI(Location loc)
        {
            if (loc != null) {
                Latitud.setText("Latitud: " + String.valueOf(loc.getLatitude()));
                Longitud.setText("Longitud: " + String.valueOf(loc.getLongitude()));
            } else {
                Latitud.setText("Latitud: (desconocida)");
                Longitud.setText("Longitud: (desconocida)");
            }
        }
    */
    public static class ConnectedThread extends Thread
    {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket)
        {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run()
        {
            boolean espero_datos=true;
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes_recibidos; // bytes returned from read()


            // Keep listening to the InputStream until an exception occurs
            while (espero_datos == true)
            {
                try {
                    // Read from the InputStream
                    bytes_recibidos = mmInStream.read(buffer_rx_BT);
                  //  String datos_recibidos = new String(buffer, "UTF-8");

                    //if(bytes_recibidos >=56)
                    if(bytes_recibidos>0)
                    {
                     //   String datos_recibidos = new String(buffer_rx_BT);
                        for(i=0;i<=47;i++)
                        {
                            Recepcion_Datos_Bluetooth(buffer_rx_BT[i]);
                            buffer_rx_BT[i]=0;
                        }
                        bytes_recibidos=0;
        //                Toast.makeText(contexto_gral, "Datos recibidos: " + datos_recibidos, Toast.LENGTH_SHORT).show();
                    }

                //    String readMessage = new String(buffer, 0, bytes_recibidos);
                    // Send the obtained bytes to the UI Activity via handler
                //    Log.i("logging", readMessage + "");

           //         if(Cant_bytes_rx_BT>=10)
             //       {
             //           espero_datos=false;
                    //    Cant_bytes_rx_BT=0;
                        //Send the obtained bytes to the UI activity
                   //    Handler mHandler;
                   //     mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                   //     datos_recibidos = buffer.toString();
                    //    Toast.makeText(MainActivity.this, "Dato recibido: " , Toast.LENGTH_SHORT).show();
                     //   break;
         //           }

                } catch (IOException e)
                {
                //    break;
                }
          //      return espero_datos;
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void enviar_byte(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to send data to the remote device */
        public void enviar_string(String datos)
        {
            byte[]  msgBuffer = datos.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) { }
        }

        public void Recepcion_Datos_Bluetooth(byte dato)
        {
            switch (estado_rx_bluetooth)
            {
                case 0:
                    if(dato == '<')
                    {
                        estado_rx_bluetooth = 1;
                    }
                    break;

                case 1:

                    if(dato == 'S')
                    {
                        estado_rx_bluetooth = 2;
                    }
                    else
                    {
                        estado_rx_bluetooth = 0;
                        i=100;
                    }
                    break;

                case 2:

                    // Recibo el nombre del conductor
                    Chofer_RX_BT[Indice_RX_BT] = dato;
                    Nombre_Chofer = new String(Chofer_RX_BT);
                    Indice_RX_BT++;

                    if(Indice_RX_BT>=10)
                    {
                        estado_rx_bluetooth = 3;
                        Indice_RX_BT=0;
                    }
                    break;

                case 3:

                    // Recibo el numero de dni
                    DNI_RX_BT[Indice_RX_BT] = dato;
                    DNI_Chofer = new String(DNI_RX_BT);
                    Indice_RX_BT++;

                    if(Indice_RX_BT>=8)
                    {
                        estado_rx_bluetooth = 4;
                        Indice_RX_BT=0;
                    }
                    break;

                case 4:

                    KM_Recorridos_RX_BT[Indice_RX_BT] = dato;
                    Indice_RX_BT++;

                    if(Indice_RX_BT>=2)
                    {
                        estado_rx_bluetooth=5;
                        Indice_RX_BT=0;
                    }
                    break;

                case 5:

                    Cantidad_Infracciones_RX_BT[Indice_RX_BT] = dato;
                    Indice_RX_BT++;

                    if(Indice_RX_BT>=2)
                    {
                        estado_rx_bluetooth=6;
                        Indice_RX_BT=0;
                    }
                    break;

                case 6:

                    Latitud_Infraccion_RX_BT[Indice_RX_BT] = dato;
                    Latitud_Infraccion = new String(Latitud_Infraccion_RX_BT);
                    Indice_RX_BT++;

                    if(Indice_RX_BT>=11)
                    {
                        estado_rx_bluetooth=7;
                        Indice_RX_BT=0;
                    }
                    break;

                case 7:

                    Longitud_Infraccion_RX_BT[Indice_RX_BT] = dato;
                    Longitud_Infraccion = new String(Longitud_Infraccion_RX_BT);
                    Indice_RX_BT++;

                    if(Indice_RX_BT>=11)
                    {
                        estado_rx_bluetooth=8;
                        Indice_RX_BT=0;
                    }
                    break;

                case 8:

                    if(dato == '>')
                    {
                        estado_rx_bluetooth=0;
                        // Recibi bien los datos
                    }
                    else
                        {
                            estado_rx_bluetooth=0;
                            i = 100;
                    }
                    break;

                    default: break;


            }
        }

    }



}
