package com.example.herna.asistenciaconductor;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import static android.support.v4.app.ActivityCompat.requestPermissions;
import static com.example.herna.asistenciaconductor.MainActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.herna.asistenciaconductor.MainActivity.contexto_gral;

public class Ubicacion implements LocationListener
{
    private Context contexto;
    static public LocationManager LocationManager;
    //String proveedor;
    static public boolean gps_enabled;
    static public boolean network_enabled;
    static public String Latitud_GPS=null;
    static public String Longitud_GPS=null;
    static public Location lc;
    public PendingIntent onLocationChanged;

    public Ubicacion(Context ctx)
    {
        this.contexto = ctx;
        LocationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions((Activity) this.contexto, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                Toast.makeText(ctx, "No tiene permisos para usar el GPS ", Toast.LENGTH_SHORT).show();
                return;
            }
            else
                {
                if(checkLocation())
                {
                    if(gps_enabled)
                    {
                        // Hago la peticion de tomar 1 segundo de muestras de GPS
                        LocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
                        // Obtengo la ultima posicion valida
                        lc = LocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                    else if (network_enabled)
                    {
                        // Hago la peticion de tomar 1 segundo de muestras de GPS
                        LocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);
                        // Obtengo la ultima posicion valida
                        lc = LocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }

                    if(lc != null)
                    {
                        Latitud_GPS = Double.toString(lc.getLatitude());
                        Longitud_GPS = Double.toString(lc.getLongitude());
                    }

                    Toast.makeText(ctx, "Ultima posicion valida" + Latitud_GPS, Toast.LENGTH_SHORT).show();
                }
            }
    }

    @Override
    public void onLocationChanged (Location location)
    {
        if (checkLocation() && location != null) {
            Latitud_GPS = Double.toString(location.getLatitude());
            Longitud_GPS = Double.toString(location.getLongitude());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        switch (status)
        {
            case LocationProvider.AVAILABLE:
           //     Toast.makeText(contexto_gral, "GPS habilitado", Toast.LENGTH_SHORT).show();
                break;

            case LocationProvider.OUT_OF_SERVICE:
            //    Toast.makeText(contexto_gral, "GPS deshabilitado", Toast.LENGTH_SHORT).show();
                break;

            case LocationProvider.TEMPORARILY_UNAVAILABLE:
            //    Toast.makeText(contexto_gral, "GPS temporalmente deshabilitado", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        Toast.makeText(contexto_gral, "GPS habilitado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(contexto_gral, "GPS Deshabilitado", Toast.LENGTH_SHORT).show();

    }

    private boolean checkLocation()
    {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert()
    {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this.contexto);
        dialog.setTitle("Enable Location")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                        "usa esta app")
                .setPositiveButton("SI", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt)
                    {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        contexto_gral.startActivity(myIntent);
                      // Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                      // mainActivity.startActivityForResult(myIntent,2);
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

    private boolean isLocationEnabled()
    {
        gps_enabled= LocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled=LocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        return gps_enabled || network_enabled;
    }
}
