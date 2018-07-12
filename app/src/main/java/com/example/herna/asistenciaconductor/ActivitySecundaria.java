package com.example.herna.asistenciaconductor;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import static com.example.herna.asistenciaconductor.AsyncTask_BT_RX.Datos_Recibidos_BT;
import static com.example.herna.asistenciaconductor.ConexionBluetooth.Usuario_habilitado;
import static com.example.herna.asistenciaconductor.Ubicacion.GPS_Habilitado;
import static com.example.herna.asistenciaconductor.Ubicacion.GPS_Habilitado_Primera_Vez;
import static com.example.herna.asistenciaconductor.Ubicacion.Latitud_GPS;
import static com.example.herna.asistenciaconductor.Ubicacion.Longitud_GPS;

//*****************************************************************************
// Activity Secundaria
//*****************************************************************************
public class ActivitySecundaria extends AppCompatActivity {

    ArrayList<DatosListViewInfracciones> ListaDesc;
    private ListView ListaInfracciones;
    EditText Latitud_editText;
    EditText Longitud_editText;
    public Button Boton_GPS;
    static public Ubicacion ubicacion;
    static public Toolbar toolbar_ActivitySecundaria;
    private int i;

    //*****************************************************************************
    // Constructor de la clase
    //*****************************************************************************
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

        Latitud_editText = findViewById(R.id.Latitud);
        Longitud_editText = findViewById(R.id.Longitud);
        Boton_GPS = findViewById(R.id.button_GPS);
        ListaInfracciones = findViewById(R.id.ListaInfracciones);
        toolbar_ActivitySecundaria = findViewById(R.id.toolbar2);

        ubicacion = new Ubicacion(this);

        // Genero el toolbar
        setSupportActionBar(toolbar_ActivitySecundaria);
        getSupportActionBar().setTitle("Asistencia Conductor");

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

        // Boton para actualizar el GPS
        Boton_GPS.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Latitud_editText.setText(Latitud_GPS);
                Longitud_editText.setText(Longitud_GPS);
            }
        });
    }

    //*****************************************************************************
    // Metodo para inflar el toolbar con los botones
    //*****************************************************************************
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    //*****************************************************************************
    // Metodo que atiende la accion de permiso cuando se habilita el GPS
    //*****************************************************************************
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[]grantResults)
    {
        switch (requestCode)
        {
            case 1:
                // Si tengo permiso para usar el GPS lo inicio
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
           //         Ubicacion ubicacion = new Ubicacion(this);
                }
                break;
        }
    }

    @Override
    protected void onResume()
    {
        //  Para activar el GPS por una vez sola
        super.onResume();
        if((GPS_Habilitado == 1)&&(GPS_Habilitado_Primera_Vez==0))
            ubicacion = new Ubicacion(this);
        else
        {
            GPS_Habilitado_Primera_Vez=0;
        }
    }

    //**********************************************************************************************
    // Metodo que se ejecuta al presionar atras, y vuelve a la activity principal
    //**********************************************************************************************
    @Override
    public void onBackPressed()
    {
        finish();

        // Hago esto para que no inicie de nuevo el servicio de GPS
        GPS_Habilitado=0;

        Usuario_habilitado[0]=0;
        Usuario_habilitado[1]=0;
        Usuario_habilitado[2]=0;
        Usuario_habilitado[3]=0;
        Datos_Recibidos_BT=false;
        Intent Activity_Main = new Intent(ActivitySecundaria.this, MainActivity.class);
        startActivity(Activity_Main);
        super.onBackPressed();
    }
}
