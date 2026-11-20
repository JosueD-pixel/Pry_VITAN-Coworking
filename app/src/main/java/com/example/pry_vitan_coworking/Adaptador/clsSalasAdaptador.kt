package com.example.pry_vitan_coworking.Adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.pry_vitan_coworking.Modelo.RetrofitCliente
import com.example.pry_vitan_coworking.Modelo.dataSala
import com.example.pry_vitan_coworking.R
import java.util.Locale

class clsSalasAdaptador(
    private var listaSalas: List<dataSala>,
    private val onSalaClick: (dataSala) -> Unit) : RecyclerView.Adapter<clsSalasAdaptador.SalaViewHolder>() {

    class SalaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgSala: ImageView = itemView.findViewById(R.id.imgSala)
        val txtDescuento: TextView = itemView.findViewById(R.id.txtDescuento)
        val txtNombreSala: TextView = itemView.findViewById(R.id.txtNombreSala)
        val txtTipoCapacidad: TextView = itemView.findViewById(R.id.txtTipoCapacidad)
        val txtPromocion: TextView = itemView.findViewById(R.id.txtPromocion)
        val txtPrecioHora: TextView = itemView.findViewById(R.id.txtPrecioHora)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalaViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_sala, parent, false)
        return SalaViewHolder(vista)
    }

    override fun onBindViewHolder(holder: SalaViewHolder, position: Int) {
        val sala = listaSalas[position]
        holder.txtNombreSala.text = sala.nombre
        holder.txtTipoCapacidad.text = "${sala.tipo} · ${sala.capacidad} personas"
        holder.txtPrecioHora.text = "$" + String.format(Locale.US, "%,.0f", sala.precio_hora)

        //la promocion solo se muestra si la sala tiene descuento hoy
        if (sala.descuento > 0) {
            val descuento = String.format(Locale.US, "%.0f", sala.descuento)
            holder.txtDescuento.text = "-$descuento %"
            var promocion = "Promoción $descuento %"
            //si la API manda la fecha fin 2026-11-30 se convierte a "hasta el 30/11"
            val fechaFin = sala.fecha_fin
            if (fechaFin.length >= 10) {
                promocion += " hasta el ${fechaFin.substring(8, 10)}/${fechaFin.substring(5, 7)}"
            }
            holder.txtPromocion.text = promocion
            holder.txtDescuento.visibility = View.VISIBLE
            holder.txtPromocion.visibility = View.VISIBLE
        } else {
            holder.txtDescuento.visibility = View.GONE
            holder.txtPromocion.visibility = View.GONE
        }

        //La BD guarda la ruta relativa: img/salas/...
        Glide.with(holder.itemView.context)
            .load(RetrofitCliente.BASE_URL + sala.imagen)
            .placeholder(R.color.grisImagen)
            .error(R.color.grisImagen)
            .transform(CenterCrop(), RoundedCorners(24))
            .into(holder.imgSala)

        holder.itemView.setOnClickListener { onSalaClick(sala) }
    }

    override fun getItemCount(): Int = listaSalas.size

    fun actualizarLista(nuevaLista: List<dataSala>) {
        listaSalas = nuevaLista
        notifyDataSetChanged()
    }
}
