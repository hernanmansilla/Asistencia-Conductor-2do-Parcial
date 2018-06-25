package com.example.herna.asistenciaconductor;

// https://www.youtube.com/watch?v=Vyqz_-sJGFk
// https://danielggarcia.wordpress.com/2013/10/19/bluetooth-i-activando-y-desactivando-el-bluetooth-en-android/
// https://code.tutsplus.com/es/tutorials/create-a-bluetooth-scanner-with-androids-bluetooth-api--cms-24084
// https://www.youtube.com/watch?v=q8b5WMnUO04
// http://yuliana.lecturer.pens.ac.id/Android/Buku/professional_android_4_application_development.pdf

//http://cursoandroidstudio.blogspot.com/2015/10/conexion-bluetooth-android-con-arduino.html

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
{
   private ArrayList<DatosRecyclerViewPrincipal> ListaUsuariosPrincipal;
   private RecyclerView recyclerUsuarios;
   private static final int ENABLE_BLUETOOTH = 1;
   private static final int SOLICITA_CONEXION =2;
   private static String MAC = null;
   BluetoothSocket mSocket = null;
   BluetoothDevice mDevice = null;
   BluetoothAdapter mBluetoothAdapter = null;
   ConnectedThread connectedThread;
   ProgressDialog pDialog;
   MiTareaAsincronaDialog tarea2;
   boolean Bluetooth_Conectado=false;
   boolean Bluetooth_Encendido=false;

    UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListaUsuariosPrincipal = new ArrayList<>();
        recyclerUsuarios = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(this));

        LlenarUsuarios();

        // Registramos el BroadcastReceiver que instanciamos previamente para
        // detectar los distintos eventos que queremos recibir
        IntentFilter filtro = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(bReceiver, filtro);

        AdaptadorRecyclerViewPrincipal adapter = new AdaptadorRecyclerViewPrincipal(ListaUsuariosPrincipal);

        adapter.setOnItemClickListener(new AdaptadorRecyclerViewPrincipal.OnItemClickListener()
        {
            @Override
            public void onItemClick(int position)
            {
                    BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

                    if (!bluetooth.isEnabled())
                    {
                        // Si el Bluetooth no esta activado, pregunto si quiero activarlo
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, ENABLE_BLUETOOTH);

                    }
                    else
                    {
                        Bluetooth_Encendido = true;
                        Intent activityListaDispositivos = new Intent(MainActivity.this, ListaDispositivos.class);
                        startActivityForResult(activityListaDispositivos, SOLICITA_CONEXION);
                    }
            }
            @Override
            public void onDeleteClick(int position)
            {
                if(Bluetooth_Conectado==true && Bluetooth_Encendido==true) {
                    connectedThread.enviar_string("hola");

                    Toast.makeText(MainActivity.this, "Borre el elemento " + position, Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(MainActivity.this, "No posee una conexion Bluetooth " + position, Toast.LENGTH_SHORT).show();

            }
        });

        recyclerUsuarios.setAdapter(adapter);

        // Linea de separacion entre items de la lista
        recyclerUsuarios.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
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

                    tarea2 = new MiTareaAsincronaDialog();
                    tarea2.execute();
                }
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

    private class ConnectedThread extends Thread {

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
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                  //  mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                  //          .sendToTarget();
                    Toast.makeText(MainActivity.this, bytes, Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    break;
                }
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
    }

    private class MiTareaAsincronaDialog extends AsyncTask<Void, Integer, Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... params)
        {
                publishProgress(1*10);
                try
                {
                    mSocket = mDevice.createRfcommSocketToServiceRecord(mUUID);
                    mSocket.connect();
                    connectedThread = new ConnectedThread(mSocket);
                    connectedThread.start();

               //     Toast.makeText(MainActivity.this, "CONECTADO CON: " + MAC, Toast.LENGTH_SHORT).show();

                } catch (IOException e)
                {
             //      e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error al conectar", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                    return false;
                }

                publishProgress(10*10);

                if(isCancelled())
                    return false;

            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            int progreso = values[0].intValue();

            pDialog.setProgress(progreso);
        }

        @Override
        protected void onPreExecute()
        {

            pDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
            {
                @Override
                public void onCancel(DialogInterface dialog) {
                    MiTareaAsincronaDialog.this.cancel(true);
                }
            });

            pDialog.setProgress(0);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            if(result)
            {
                pDialog.dismiss();
                Bluetooth_Conectado=true;
             //   Toast.makeText(MainActivity.this, "Tarea finalizada!",
             //           Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "CONECTADO CON: " + MAC, Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(MainActivity.this, "Error al conectar", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            Toast.makeText(MainActivity.this, "Tarea cancelada!", Toast.LENGTH_SHORT).show();
        }
    }
}
