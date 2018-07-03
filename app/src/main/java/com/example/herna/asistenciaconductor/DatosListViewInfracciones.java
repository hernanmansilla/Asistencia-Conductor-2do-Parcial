package com.example.herna.asistenciaconductor;

import android.widget.ImageView;

public class DatosListViewInfracciones
{
    private int Id;
    private int Velocidad_Infraccion;
    private String Latitud_Infraccion;
    private String Longitud_Infraccion;
    private ImageView Imagen_Maps;

    //**********************************************************************************************
    // Constructor de la clase
    //**********************************************************************************************
    public DatosListViewInfracciones(int vel, String lat, String longi, ImageView imagen)
    {
        Velocidad_Infraccion = vel;
        Latitud_Infraccion = lat;
        Longitud_Infraccion = longi;
        Imagen_Maps = imagen;
    }

    //**********************************************************************************************
    // Obtengo el id
    //**********************************************************************************************
    public int getId()
    {
        return Id;
    }

    public int getVelocidad_Infraccion()
    {
        return Velocidad_Infraccion;
    }

    //**********************************************************************************************
    // Obtengo el nombre
    //**********************************************************************************************
    public String getLatitud_Infraccion()
    {
        return Latitud_Infraccion;
    }

    //**********************************************************************************************
    // Obtengo la descripcion
    //**********************************************************************************************
    public String getLongitud_Infraccion()
    {
        return Longitud_Infraccion;
    }

    //**********************************************************************************************
    // Obtengo el monto a favor
    //**********************************************************************************************
    public ImageView getImagen()
    {
        return Imagen_Maps;
    }

}
