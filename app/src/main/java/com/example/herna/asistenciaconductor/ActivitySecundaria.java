package com.example.herna.asistenciaconductor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;



public class ActivitySecundaria extends AppCompatActivity {

    ArrayList<DatosListViewInfracciones> ListaDesc;
    private ListView ListaInfracciones;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secundaria);

        // Obtengo el usuario que corresponde al item seleciconado
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        int Cantidad_infracciones = extras.getInt("Cantidad_infracciones");
        byte [] Velocidad_infraccion = extras.getByteArray("Velocidad_infraccion");
        String Latitud_infraccion = extras.getString("Latitud_infraccion");
        String Longitud_infraccion = extras.getString("Longitud_infraccion");

        ListaInfracciones = findViewById(R.id.ListaInfracciones);

        ListaDesc = new ArrayList<DatosListViewInfracciones>();

        for(i=0;i<Cantidad_infracciones;i++)
        {
            // Inserto en mi objeto para mostrar en el listview
            ListaDesc.add(new DatosListViewInfracciones(Velocidad_infraccion[i], Latitud_infraccion, Longitud_infraccion));
        }

        // Instancio mi clase creada adaptador con los datos ya precargados
        AdaptadorListViewInfracciones adaptador1 = new AdaptadorListViewInfracciones(getApplicationContext(),ListaDesc);

        // Referencio el adaptador con la lista del XML
        ListaInfracciones.setAdapter(adaptador1);

        // Registro los controles para el menu contextual, detecta la pulsacion prolongada
      //  registerForContextMenu(ListaInfracciones);
    }
}
