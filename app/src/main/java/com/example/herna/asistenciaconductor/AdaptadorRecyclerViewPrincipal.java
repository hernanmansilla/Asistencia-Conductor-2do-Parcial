package com.example.herna.asistenciaconductor;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

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
        holder.imagen.setImageResource(ListaUsuariosPrincipal.get(position).getImagen());
        holder.imageName.setText(ListaUsuariosPrincipal.get(position).getNombre());
        holder.imageDesc.setText(ListaUsuariosPrincipal.get(position).getDesc());
    }

    @Override
    public int getItemCount()
    {
        return ListaUsuariosPrincipal.size();
    }

    public interface OnItemClickListener
    {
        void onItemClick(int position);
        void onDeleteClick(int position);
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
        TextView imageDesc;
        ImageView imagen_delete;
        RelativeLayout parentLayout;

        public ViewHolderPrincipal(View itemView, final OnItemClickListener listener)
        {
            super(itemView);
            imagen = itemView.findViewById(R.id.imagen);
            imageName = itemView.findViewById(R.id.imagen_nombre);
            imageDesc = itemView.findViewById(R.id.imagen_descripcion);
            imagen_delete = itemView.findViewById(R.id.imagen_delete);
            parentLayout = itemView.findViewById(R.id.parent_layout);

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

            imagen_delete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(listener != null)
                    {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION)
                        {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });


        }
    }
}
