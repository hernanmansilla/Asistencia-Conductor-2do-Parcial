package com.example.herna.asistenciaconductor;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

public class Ubicacion implements LocationListener
{
    private Context contexto;
    LocationManager LocationManager;
    //String proveedor;
    private boolean gps_enabled;
    private boolean network_enabled;
    static public String Latitud_GPS=null;
    static public String Longitud_GPS=null;

    public Ubicacion(Context ctx)
    {
        this.contexto = ctx;
        LocationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);

        try
        {
            gps_enabled = LocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch(Exception ex)
        {
            try
            {
                network_enabled = LocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }
            catch(Exception ex2)
            {
                Toast.makeText(ctx, "Error al iniciar la ubicacion ", Toast.LENGTH_SHORT).show();
             //   toast.show();

            }
        }

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, this);

        if(gps_enabled)
        {
            Location lc = LocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Latitud_GPS = Double.toString(lc.getLatitude());
            Longitud_GPS = Double.toString(lc.getLongitude());

            Toast.makeText(ctx, "Latitud:" + Latitud_GPS, Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onLocationChanged(Location location)
    {
        if(gps_enabled || network_enabled)
        {
            Latitud_GPS = Double.toString(location.getLatitude());
            Longitud_GPS = Double.toString(location.getLongitude());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
