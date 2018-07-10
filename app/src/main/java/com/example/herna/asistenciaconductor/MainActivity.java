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

// Geocerca https://www.mytrendin.com/android-geofences-google-api/

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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.location.Location;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.example.herna.asistenciaconductor.AsyncTask_BT_RX.Datos_Recibidos_BT;
import static com.example.herna.asistenciaconductor.Ubicacion.Latitud_GPS;
import static com.example.herna.asistenciaconductor.Ubicacion.Longitud_GPS;
import static com.example.herna.asistenciaconductor.ConexionBluetooth.Cantidad_Infracciones_RX_BT;
import static com.example.herna.asistenciaconductor.ConexionBluetooth.Velocidad_infraccion_RX_BT;
import static com.example.herna.asistenciaconductor.ConexionBluetooth.Latitud_Infraccion_RX_BT;
import static com.example.herna.asistenciaconductor.ConexionBluetooth.Longitud_Infraccion_RX_BT;

public class MainActivity extends AppCompatActivity
{
    static public ArrayList<DatosRecyclerViewPrincipal> ListaUsuariosPrincipal;
    static public RecyclerView recyclerUsuarios;
    static  public AdaptadorRecyclerViewPrincipal adapter;
    private static final int ENABLE_BLUETOOTH = 1;
    private static final int SOLICITA_CONEXION = 2;
    static public String MAC = null;
    static public BluetoothDevice mDevice = null;
    BluetoothAdapter mBluetoothAdapter = null;
    AsyncTask_BT_init_Dialog Bluetooth_init;
    static public boolean Bluetooth_Conectado = false;
    static public boolean Bluetooth_Encendido = false;
    static public UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    static public ProgressDialog pDialog;
    static public NotificationCompat.Builder mBuilder;
    static final int NOTIF_ALERTA_ID = 1;
    static public int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    static public Context contexto_gral;
    static public Ubicacion ubicacion;
    static public NotificationManager mNotificationManager;
    private Toolbar toolbar_MainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerUsuarios = (RecyclerView) findViewById(R.id.recycler_view);

        // Referencio los recursos del XML
        toolbar_MainActivity = findViewById(R.id.toolbar);

        // Genero el toolbar
        setSupportActionBar(toolbar_MainActivity);
        getSupportActionBar().setTitle("       D R I V E R  A S I S T");

        ListaUsuariosPrincipal = new ArrayList<>();

        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(this));

        // Lleno el RecyclerView con los Usuarios
        LlenarUsuarios();

        contexto_gral = getApplicationContext();
        ubicacion = new Ubicacion(this);

        // Registramos el BroadcastReceiver que instanciamos previamente para
        // detectar los distintos eventos que queremos recibir del Bluetooth
        IntentFilter filtro = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(bReceiver, filtro);


        //*****************************************************************************
        // Creo la notificacion con sus atributos
        //*****************************************************************************
        mBuilder = new NotificationCompat.Builder(MainActivity.this);
        mBuilder.setAutoCancel(true);
        mBuilder.setSmallIcon(R.drawable.icono_camion);
        mBuilder.setTicker("Bluetooth");
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setContentTitle("Bluetooth");
        mBuilder.setContentText("Ud a recibido nuevos datos ");

        Intent Intent = new Intent(MainActivity.this, MainActivity.class);
        PendingIntent contIntent = PendingIntent.getActivity(MainActivity.this, 0, Intent, 0);
        mBuilder.setContentIntent(contIntent);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Creo el adapter y se lo paso al RecyclerView para iniciarlo
        adapter = new AdaptadorRecyclerViewPrincipal(ListaUsuariosPrincipal);
        recyclerUsuarios.setAdapter(adapter);

        adapter.setOnItemClickListener(new AdaptadorRecyclerViewPrincipal.OnItemClickListener()
        {
            @Override
            public void onItemClick(int position)
            {
                if(Datos_Recibidos_BT==true)
                {
                 //   finish();
                    Intent Activity2 = new Intent(MainActivity.this, ActivitySecundaria.class);

                    // Le paso los datos del Chofer a la segunda activity para mostrarla
                    String Lat_aux = new String(Latitud_Infraccion_RX_BT);
                    String Long_aux = new String(Longitud_Infraccion_RX_BT);

                    Activity2.putExtra("Cantidad_infracciones", Cantidad_Infracciones_RX_BT);
                    Activity2.putExtra("Velocidad_infraccion", Velocidad_infraccion_RX_BT);
                    Activity2.putExtra("Latitud_infraccion", Lat_aux);
                    Activity2.putExtra("Longitud_infraccion", Long_aux);
                    startActivity(Activity2);
                }
            }

            @Override
            public void onDownloadClick (int position)
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

                    if (Bluetooth_Conectado == false)
                    {
                        // Muerta la Activity de la lista de dispositivos para vincularlos
                        Intent activityListaDispositivos = new Intent(MainActivity.this, ListaDispositivos.class);
                        startActivityForResult(activityListaDispositivos, SOLICITA_CONEXION);
                    } else
                        {
                        Toast.makeText(MainActivity.this, "Ya posee una conexion activa", Toast.LENGTH_SHORT).show();
                    }
                }

                if(Bluetooth_Conectado == true)
                {
                    switch (position)
                    {
                        case 0:

                            Enviar_String_Bluetooth("<S34235547>");
                            Toast.makeText(MainActivity.this, "Envie dato", Toast.LENGTH_SHORT).show();
                            break;

                        case 1:
                            Enviar_String_Bluetooth("<S12345678>");
                            Toast.makeText(MainActivity.this, "Envie dato", Toast.LENGTH_SHORT).show();
                            break;

                        case 2:
                            Enviar_String_Bluetooth("<S34500600>");
                            Toast.makeText(MainActivity.this, "Envie dato", Toast.LENGTH_SHORT).show();
                            break;

                        case 3:
                            Enviar_String_Bluetooth("<S30266999>");
                            Toast.makeText(MainActivity.this, "Envie dato", Toast.LENGTH_SHORT).show();
                            break;

                    }
                }
            }
        });
    }

    public void Enviar_String_Bluetooth(String datos)
    {
        ConexionBluetooth conexionBluetooth_enviar_datos;

        if (Bluetooth_Conectado == true && Bluetooth_Encendido == true)
        {
            conexionBluetooth_enviar_datos = Bluetooth_init.Get_Conexion();
            conexionBluetooth_enviar_datos.enviar_string(datos);
        }
    }

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
                // Si tengo permiso para usar el GPS lo inicio
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
                    // Muerta la Activity de la lista de dispositivos para vincularlos
                    Intent activityListaDispositivos = new Intent(MainActivity.this, ListaDispositivos.class);
                    startActivityForResult(activityListaDispositivos, SOLICITA_CONEXION);
                }
                break;

            case SOLICITA_CONEXION:

                if(resultCode == RESULT_OK)
                {
                    // Inicio una Async Task para iniciar el Bluetooth con una barra de estado
                    pDialog = new ProgressDialog(MainActivity.this);
                    pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pDialog.setMessage("Conectando dispositivo");
                    pDialog.setCancelable(true);
                    pDialog.setMax(100);

                    // Tomo la MAC de la lista de dispositivos aparejados para conectarme
                    MAC = data.getExtras().getString(ListaDispositivos.ENDERECO_MAC);

                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                    mDevice = mBluetoothAdapter.getRemoteDevice(MAC);

                    // Inicio la tarea para iniciar la comunicacion Bluetooth
                    Bluetooth_init = new AsyncTask_BT_init_Dialog();
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
        ListaUsuariosPrincipal.add(new DatosRecyclerViewPrincipal("Hernan","34235547",R.drawable.ic_file_download_red));
        ListaUsuariosPrincipal.add(new DatosRecyclerViewPrincipal("German","12345678",R.drawable.ic_file_download_red));
        ListaUsuariosPrincipal.add(new DatosRecyclerViewPrincipal("Facundo","34500600",R.drawable.ic_file_download_red));
        ListaUsuariosPrincipal.add(new DatosRecyclerViewPrincipal("Gaston","30266999",R.drawable.ic_file_download_red));
    }
}
