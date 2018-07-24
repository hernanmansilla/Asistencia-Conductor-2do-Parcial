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


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Key;
import java.util.ArrayList;
import java.util.UUID;

import static com.example.herna.asistenciaconductor.AsyncTask_BT_RX.Datos_Recibidos_BT;
import static com.example.herna.asistenciaconductor.AsyncTask_BT_RX.conexionBluetooth;
import static com.example.herna.asistenciaconductor.ConexionBluetooth.Usuario_habilitado;
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
    AsyncTask_BT_Dialog Bluetooth_init;
    static public boolean Bluetooth_Conectado = false;
    static public boolean Bluetooth_Encendido = false;
    static public UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    static public ProgressDialog pDialog;
    static public NotificationCompat.Builder mBuilder;
    static final int NOTIF_ALERTA_ID = 1;
    static public int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    static public Context contexto_gral;
    static public NotificationManager mNotificationManager;
    static public Toolbar toolbar_MainActivity;
    static public ConexionBluetooth conexionBluetooth_aux;
    static public AsyncTask_BT_RX Bluetooth_RX_aux;
    static public DatabaseReference dbUsuarios;
    String DNI_Seleccionado;

    //*****************************************************************************
    // Constructor de la Activity Principal
    //*****************************************************************************
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
        getSupportActionBar().setTitle("Asistencia Conductor");

        // Obtengo una referencia a la base de datos
        dbUsuarios = FirebaseDatabase.getInstance().getReference("Usuarios");

        ListaUsuariosPrincipal = new ArrayList<>();

        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(this));

        contexto_gral = getApplicationContext();

        // Registramos el BroadcastReceiver que instanciamos previamente para detectar los distintos eventos que queremos recibir del Bluetooth
        IntentFilter filtro = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(bReceiver, filtro);

        // Creo la notificacion con sus atributos
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

        // Funcion para atender la presion de algun item de la lista del RecyclerView
        adapter.setOnItemClickListener(new AdaptadorRecyclerViewPrincipal.OnItemClickListener()
        {
            @Override
            public void onItemClick(int position)
            {
                // Obtengo la informacion del item seleccionado
                DNI_Seleccionado = ListaUsuariosPrincipal.get(position).getDNI();

                Intent Activity2 = new Intent(MainActivity.this, ActivitySecundaria.class);

                // Le paso los datos del Chofer a la segunda activity para mostrarla
                finish();
                Activity2.putExtra("DNI_Seleccionado", DNI_Seleccionado);
                startActivity(Activity2);
            }

            // Funcion para atender la presion de algun icono de descarga de los item del RecyclerView
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
                    }
                }

                if(Bluetooth_Conectado == true)
                {
                    DNI_Seleccionado = ListaUsuariosPrincipal.get(position).getDNI();

                    Enviar_String_Bluetooth("<S" + DNI_Seleccionado + ">");
                }
            }
        });

        // Este listener se lanza por una unica vez para no consumir recursos
        dbUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long i;

                long Cantidad_Children = dataSnapshot.getChildrenCount();

                ListaUsuariosPrincipal.removeAll(ListaUsuariosPrincipal);

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String DNI = snapshot.getKey();
                    String nombre = snapshot.child("Nombre").getValue().toString();
                    ListaUsuariosPrincipal.add(new DatosRecyclerViewPrincipal(nombre,DNI,R.drawable.ic_download));
                }

                // Notiicamos que se cambiaron los datos del RecyclerView
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.e(TAGLOG, "Error!", databaseError.toException());
            }
        });
    }

    //*****************************************************************************
    // Funcion para enviar un String via Bluetooth
    //*****************************************************************************
    public void Enviar_String_Bluetooth(String datos)
    {
        if (Bluetooth_Conectado == true && Bluetooth_Encendido == true)
        {
            conexionBluetooth.enviar_string(datos);
        }
    }

    //*****************************************************************************
    // BroadcastReceiver para detectar los estados del Bbluetooth
    //*****************************************************************************
    private final BroadcastReceiver bReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            final String action = intent.getAction();

            // Filtramos por la accion, nos interesa detectar BluetoothAdapter.ACTION_STATE_CHANGED
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

    //*****************************************************************************
    // Metodo que atiende el resultado del estado de las conexiones
    //*****************************************************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case ENABLE_BLUETOOTH:

                if (resultCode == RESULT_OK)
                {
                    // Si entre aca es porque active el bluetooth y muestro la Activity con la lista de dispositivos emparejados
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
                    Bluetooth_init = new AsyncTask_BT_Dialog();
                    Bluetooth_init.execute();
                }
                break;

            case 3:
                Toast.makeText(MainActivity.this, "GPS encendido " , Toast.LENGTH_SHORT).show();
                break;
        }
    }

    //*****************************************************************************
    // Inflo el toolbar para que aparezcan los iconos
    //*****************************************************************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    //*****************************************************************************
    // Funcion que se ejecuta cuando retorna a la activity principal
    //*****************************************************************************
    @Override
    protected void onResume()
    {
        super.onResume();

        // Vuelvo a llamar a la tarea para que siga escuchando lo que llega por Bluetooth
        if(Bluetooth_Conectado == true && Bluetooth_Encendido == true)
        {
            Bluetooth_RX_aux = new AsyncTask_BT_RX(conexionBluetooth);
            Bluetooth_RX_aux.execute();
        }
    }
}
