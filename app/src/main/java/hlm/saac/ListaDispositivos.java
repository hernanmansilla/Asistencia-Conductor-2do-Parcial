package hlm.saac;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

//**********************************************************************************************
// Clase para manejar la activity de los dispositivos Bluetooth emparejados
//**********************************************************************************************
public class ListaDispositivos extends ListActivity
{
    private BluetoothAdapter BluetoothAdapter = null;
    public ArrayAdapter<String> ArrayBluetooth;
    static String ENDERECO_MAC = null;

    //**********************************************************************************************
    // Constructor de la clase
    //**********************************************************************************************
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
    }

    //**********************************************************************************************
    // Metodo para manejar el click de cada item
    //**********************************************************************************************
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
}
