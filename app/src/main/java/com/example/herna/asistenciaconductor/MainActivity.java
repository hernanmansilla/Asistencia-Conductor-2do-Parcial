package com.example.herna.asistenciaconductor;

// https://www.youtube.com/watch?v=Vyqz_-sJGFk
// https://danielggarcia.wordpress.com/2013/10/19/bluetooth-i-activando-y-desactivando-el-bluetooth-en-android/
// https://code.tutsplus.com/es/tutorials/create-a-bluetooth-scanner-with-androids-bluetooth-api--cms-24084
// https://www.youtube.com/watch?v=q8b5WMnUO04
// http://yuliana.lecturer.pens.ac.id/Android/Buku/professional_android_4_application_development.pdf

//http://cursoandroidstudio.blogspot.com/2015/10/conexion-bluetooth-android-con-arduino.html

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
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

        AdaptadorRecyclerViewPrincipal adapter = new AdaptadorRecyclerViewPrincipal(ListaUsuariosPrincipal);

   /*    adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
               // Log.i("DemoRecView", "Pulsado el elemento " + recyclerUsuarios.getChildPosition(v));
                Toast.makeText(MainActivity.this, "Pulsado el elemento " + recyclerUsuarios.getChildPosition(v), Toast.LENGTH_SHORT).show();
            }
        });
*/
        adapter.setOnItemClickListener(new AdaptadorRecyclerViewPrincipal.OnItemClickListener()
        {
            @Override
            public void onItemClick(int position)
            {
                 //   OutputStream outputStream;
                    OutputStream mOutputStream = null;
                    BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

                    if (!bluetooth.isEnabled())
                    {
                        // Si el Bluetooth no esta activado, pregunto si quiero activarlo
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, ENABLE_BLUETOOTH);

                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "bluetooth ya encendido", Toast.LENGTH_SHORT).show();
                    }
            }
            @Override
            public void onDeleteClick(int position)
            {
                // ListaUsuariosPrincipal.get(position);
                Toast.makeText(MainActivity.this, "Borre el elemento " + position, Toast.LENGTH_SHORT).show();
            }
        });

        recyclerUsuarios.setAdapter(adapter);

        // Linea de separacion entre items de la lista
        recyclerUsuarios.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
     //   super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case ENABLE_BLUETOOTH:

                if (resultCode == RESULT_OK)
                {
                    // Si entre aca es porque active el bluetooth
                    Toast.makeText(MainActivity.this, "bluetooth encendido", Toast.LENGTH_SHORT).show();
                    Intent activityListaDispositivos = new Intent(MainActivity.this, ListaDispositivos.class);
                    startActivityForResult(activityListaDispositivos, SOLICITA_CONEXION);
                }
                break;

            case SOLICITA_CONEXION:

                if(resultCode == RESULT_OK)
                {
                    // Tomo la MAC de la lista de dispositivos aparejados para conectarme
                    MAC = data.getExtras().getString(ListaDispositivos.ENDERECO_MAC);
                    //Get MAC address from DeviceListActivity via intent
                 //   Intent intent = getIntent();

                    //Get the MAC address from the DeviceListActivty via EXTRA
                //    MAC = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

                    mDevice = mBluetoothAdapter.getRemoteDevice(MAC);

                    try
                    {
                        mSocket = mDevice.createRfcommSocketToServiceRecord(mUUID);

                        mSocket.connect();

                        Toast.makeText(MainActivity.this, "CONECTADO CON: " + MAC, Toast.LENGTH_SHORT).show();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error al conectar", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    //}
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

}
