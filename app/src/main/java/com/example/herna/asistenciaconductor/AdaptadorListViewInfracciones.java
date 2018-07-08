package com.example.herna.asistenciaconductor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorListViewInfracciones extends BaseAdapter
{
    Context contexto;
    List<DatosListViewInfracciones> ListaObjetosPrincipal;

    //**********************************************************************************************
    // Constructor de la clase
    //**********************************************************************************************
    public AdaptadorListViewInfracciones(Context contexto, ArrayList<DatosListViewInfracciones> listaObjetos)
    {
        this.contexto = contexto;
        ListaObjetosPrincipal = listaObjetos;
    }

    //**********************************************************************************************
    // Obtengo el tamaño
    //**********************************************************************************************
    @Override
    public int getCount()
    {
        return ListaObjetosPrincipal.size();
    }

    //**********************************************************************************************
    // Obtengo el item seleccionado
    //**********************************************************************************************
    @Override
    public Object getItem(int Position)
    {
        return ListaObjetosPrincipal.get(Position);
    }

    //**********************************************************************************************
    // Obtengo el id del item seleccionado del listview
    //**********************************************************************************************
    @Override
    public long getItemId (int Position)
    {
        return ListaObjetosPrincipal.get(Position).getId();
    }

    //**********************************************************************************************
    // Funcion donde se crea la vista de cada item del listview
    //**********************************************************************************************
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(contexto);
        View item = inflater.inflate(R.layout.layout_listitem_infracciones, null);

        TextView Lista_Velocidad = item.findViewById(R.id.Velocidad);
        TextView Lista_Latitud = item.findViewById(R.id.Latitud);
        TextView Lista_Longitud = item.findViewById(R.id.Longitud);
    //    ImageView Imagen_mapa = item.findViewById(R.id.imagen_ubicar);

        Lista_Velocidad.setText(String.valueOf(ListaObjetosPrincipal.get(position).getVelocidad_Infraccion()));
        Lista_Latitud.setText(ListaObjetosPrincipal.get(position).getLatitud_Infraccion());
        Lista_Longitud.setText(ListaObjetosPrincipal.get(position).getLongitud_Infraccion());
    //    Imagen_mapa.setImageResource(ListaObjetosPrincipal.get(position).getImagen());

        return(item);
    }
}
