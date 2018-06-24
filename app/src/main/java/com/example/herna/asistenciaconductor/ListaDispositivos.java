package com.example.herna.asistenciaconductor;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

public class ListaDispositivos extends ListActivity
{
    private BluetoothAdapter BluetoothAdapter = null;
    public ArrayAdapter<String> ArrayBluetooth;
    static String ENDERECO_MAC = null;
    // Register the BroadcastReceiver
  //  IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ArrayBluetooth = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

        BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        BluetoothAdapter.startDiscovery();

        Set<BluetoothDevice> dispositivosAsociados = BluetoothAdapter.getBondedDevices();

        if(dispositivosAsociados.size() >0)
        {
            for(BluetoothDevice dispositivo : dispositivosAsociados)
            {
                String nombreBT = dispositivo.getName();
                String macBT = dispositivo.getAddress();
                ArrayBluetooth.add(nombreBT + "\n" + macBT);
            }
            setListAdapter(ArrayBluetooth);
        }
    //    registerReceiver(bReciever, filter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l,v,position,id);

        String infogral = ((TextView)v).getText().toString();

        String infoMac = infogral.substring(infogral.length()-17);

        Intent retornoMac = new Intent();
        retornoMac.putExtra(ENDERECO_MAC, infoMac);
        setResult(RESULT_OK,retornoMac);
        finish();
    }

/*
    private final BroadcastReceiver bReciever = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Create a new device item
                //  DeviceItem newDevice = new DeviceItem(device.getName(), device.getAddress(), "false");
                // Add it to our adapter
                //  ArrayBluetooth.add(newDevice);
                String nombreBT = device.getName();
                String macBT = device.getAddress();
                ArrayBluetooth.add(nombreBT + "\n" + macBT);
            }
            setListAdapter(ArrayBluetooth);
        }
    };*/
}
