package com.example.pry_vitan_coworking.Adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pry_vitan_coworking.Modelo.dataCancelacion
import com.example.pry_vitan_coworking.R
import java.util.Locale

class clsCancelacionesAdaptador(
    private var listaCancelaciones: List<dataCancelacion>
) : RecyclerView.Adapter<clsCancelacionesAdaptador.CancelacionViewHolder>() {

    class CancelacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtReservaSala: TextView = itemView.findViewById(R.id.txtReservaSala)
        val txtCliente: TextView = itemView.findViewById(R.id.txtCliente)
        val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
        val txtMotivo: TextView = itemView.findViewById(R.id.txtMotivo)
        val txtPenalizacion: TextView = itemView.findViewById(R.id.txtPenalizacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CancelacionViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_cancelacion, parent, false)
        return CancelacionViewHolder(vista)
    }

    override fun onBindViewHolder(holder: CancelacionViewHolder, position: Int) {
        val cancelacion = listaCancelaciones[position]
        holder.txtReservaSala.text = "#${cancelacion.id_reserva} · ${cancelacion.sala}"
        holder.txtCliente.text = cancelacion.cliente
        holder.txtMotivo.text = cancelacion.motivo
        holder.txtPenalizacion.text = "$" + String.format(Locale.US, "%,.0f", cancelacion.penalizacion)

        //parte la fecha "2026-11-05" en año, mes, dia y la pone como "05/11/2026"
        val f = cancelacion.fecha.split("-")
        var fecha = if (f.size == 3) "${f[2]}/${f[1]}/${f[0]}" else cancelacion.fecha

        //si trae horario lo agrega a la fecha sin el cero de adelante: "05/11/2026  9:00 a 12:00"
        if (cancelacion.hora_inicio.isNotEmpty() && cancelacion.hora_fin.isNotEmpty()) {
            fecha += " · ${cancelacion.hora_inicio.removePrefix("0")} a ${cancelacion.hora_fin.removePrefix("0")}"
        }
        holder.txtFecha.text = fecha
    }

    override fun getItemCount(): Int = listaCancelaciones.size

    fun actualizarLista(nuevaLista: List<dataCancelacion>) {
        listaCancelaciones = nuevaLista
        notifyDataSetChanged()
    }
}
