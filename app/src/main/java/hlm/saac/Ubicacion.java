package hlm.saac;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import static hlm.saac.MainActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static hlm.saac.MainActivity.contexto_gral;

public class Ubicacion implements LocationListener
{
    private Context contexto;
    static public LocationManager LocationManager;
    static public boolean gps_enabled;
    static public boolean network_enabled;
    static public String Latitud_GPS=null;
    static public String Longitud_GPS=null;
    static public Location lc;
    static public int GPS_Habilitado=0;
    static public int GPS_Habilitado_Primera_Vez=0;

    //*****************************************************************************
    // Constructor de la clase
    //*****************************************************************************
    public Ubicacion(Context ctx)
    {
        this.contexto = ctx;
        LocationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        // Pregunto si tengo permiso para usar el GPS
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions((Activity) this.contexto, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            Toast.makeText(ctx, "No tiene permisos para usar el GPS ", Toast.LENGTH_SHORT).show();
            return;
        }
        else
            {
                // Si tengo permiso, pregunto si esta habilitado el GPS
            if(checkLocation())
            {
                // Si tengo el GPS habilitado
                if(gps_enabled)
                {
                    // Hago la peticion para tomar muestras cada 1 segundo por GPS
                    LocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
                    // Obtengo la ultima posicion valida
                    lc = LocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                else if (network_enabled)
                {
                    // Hago la peticion para tomar muestras cada 1 segundo por Internet
                    LocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
                    // Obtengo la ultima posicion valida
                    lc = LocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }

                // Obtengo la ultima posicion valida
                if(lc != null)
                {
                    Latitud_GPS = Double.toString(lc.getLatitude());
                    Longitud_GPS = Double.toString(lc.getLongitude());
                }

                Toast.makeText(ctx, "Ultima posicion valida", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //*****************************************************************************
    // Metodo que se llama cada vez que cambio la posicion
    //*****************************************************************************
    @Override
    public void onLocationChanged (Location location)
    {
        if (checkLocation() && location != null) {
            Latitud_GPS = Double.toString(location.getLatitude());
            Longitud_GPS = Double.toString(location.getLongitude());
        }
    }

    //*****************************************************************************
    // Funcion que monitore los cambios de estado del GPS
    //*****************************************************************************
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        switch (status)
        {
            case LocationProvider.AVAILABLE:
           //     Toast.makeText(contexto_gral, "GPS habilitado", Toast.LENGTH_SHORT).show();
                break;

            case LocationProvider.OUT_OF_SERVICE:
                Toast.makeText(contexto_gral, "GPS deshabilitado", Toast.LENGTH_SHORT).show();
                break;

            case LocationProvider.TEMPORARILY_UNAVAILABLE:
          //      Toast.makeText(contexto_gral, "GPS temporalmente deshabilitado", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    //*****************************************************************************
    // Metodo que se llama cuando el GPS se habilita
    //*****************************************************************************
    @Override
    public void onProviderEnabled(String provider)
    {
        Toast.makeText(contexto_gral, "GPS habilitado", Toast.LENGTH_SHORT).show();
    }

    //*****************************************************************************
    // Metodo que se llama cuando el GPS se deshabilita
    //*****************************************************************************
    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(contexto_gral, "GPS Deshabilitado", Toast.LENGTH_SHORT).show();

    }

    //*****************************************************************************
    // Funcion para testear si esta habilitado el GPS
    //*****************************************************************************
    private boolean checkLocation()
    {
        // En caso de que no este habilitado el GPS llamo a un metodo para ir a la pantalla Setting y habilitarlo
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }


    //*****************************************************************************
    // Funcion para llamar a un AlertDialog para permitir la habilitacion del GPS
    //*****************************************************************************
    private void showAlert()
    {
        GPS_Habilitado_Primera_Vez=1;
        GPS_Habilitado = 1;

        final AlertDialog.Builder dialog = new AlertDialog.Builder(this.contexto);
                 dialog.setTitle("Enable Location")
                .setMessage("Su ubicación esta desactivada.\nPor favor active su ubicación")
                .setPositiveButton("Activar", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt)
                    {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        contexto_gral.startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt)
                    {
                    }
                });
        dialog.show();
    }

    //*****************************************************************************
    // Funcion para testear el proveedor de la ubicacion de GPS
    //*****************************************************************************
    private boolean isLocationEnabled()
    {
        gps_enabled= LocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled=LocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        return gps_enabled || network_enabled;
    }
}
