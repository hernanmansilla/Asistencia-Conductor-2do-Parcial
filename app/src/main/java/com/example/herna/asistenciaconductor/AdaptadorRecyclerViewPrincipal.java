package com.example.herna.asistenciaconductor;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

//*****************************************************************************
// Clase del Adaptador del RecyclerView
//*****************************************************************************
public class AdaptadorRecyclerViewPrincipal extends RecyclerView.Adapter<AdaptadorRecyclerViewPrincipal.ViewHolderPrincipal> implements View.OnClickListener
{
    ArrayList<DatosRecyclerViewPrincipal> ListaUsuariosPrincipal;
    private View.OnClickListener listener;
    private OnItemClickListener itemListener;

    public AdaptadorRecyclerViewPrincipal(ArrayList<DatosRecyclerViewPrincipal> ListaUsuariosPrincipal )
    {
        this.ListaUsuariosPrincipal = ListaUsuariosPrincipal;
    }

    @Override
    public ViewHolderPrincipal onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);

        view.setOnClickListener(this);

        ViewHolderPrincipal holder = new ViewHolderPrincipal(view,itemListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolderPrincipal holder, final int position)
    {
        holder.imageName.setText(ListaUsuariosPrincipal.get(position).getNombre());
        holder.imageDNI.setText(ListaUsuariosPrincipal.get(position).getDesc());
        holder.imagen_download.setImageResource(ListaUsuariosPrincipal.get(position).getImagen());
    }

    @Override
    public int getItemCount()
    {
        return ListaUsuariosPrincipal.size();
    }

    public interface OnItemClickListener
    {
        void onItemClick(int position);
        void onDownloadClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.itemListener = listener;
    }

    public void setOnClickListener(View.OnClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onClick(View view)
    {
        if(listener != null)
            listener.onClick(view);
    }

    public class ViewHolderPrincipal extends RecyclerView.ViewHolder
    {
        CircleImageView imagen;
        TextView imageName;
        TextView imageDNI;
        ImageView imagen_download;

        public ViewHolderPrincipal(View itemView, final OnItemClickListener listener)
        {
            super(itemView);
            imagen = itemView.findViewById(R.id.imagen);
            imageName = itemView.findViewById(R.id.imagen_nombre);
            imageDNI = itemView.findViewById(R.id.DNI);
            imagen_download = itemView.findViewById(R.id.imagen_download);

            // Funcion para atender la presion de los items de la lista del RecyclerView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            // Funcion para atender la presion de los iconos de descarga en cada item
            imagen_download.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onDownloadClick(position);
                        }
                    }
                }
            });
        }
    }
}
