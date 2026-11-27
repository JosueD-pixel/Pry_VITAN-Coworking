package com.example.pry_vitan_coworking.Adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pry_vitan_coworking.R

// Fotos de la sala en una lista horizontal (item_galeria.xml)
class Cls_GaleriaAdaptador(
    private var fotos: List<String> = emptyList()
) : RecyclerView.Adapter<Cls_GaleriaAdaptador.FotoVH>() {

    class FotoVH(vista: View) : RecyclerView.ViewHolder(vista) {
        val imgFoto: ImageView = vista.findViewById(R.id.imgFoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FotoVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_galeria, parent, false)
        return FotoVH(v)
    }

    override fun onBindViewHolder(holder: FotoVH, position: Int) {
        Glide.with(holder.imgFoto)
            .load(fotos[position])
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .centerCrop()
            .into(holder.imgFoto)
    }

    override fun getItemCount() = fotos.size

    fun actualizar(nuevas: List<String>) {
        fotos = nuevas
        notifyDataSetChanged()
    }
}