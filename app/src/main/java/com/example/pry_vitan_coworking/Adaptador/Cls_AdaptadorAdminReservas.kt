package com.example.pry_vitan_coworking.Adaptador

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pry_vitan_coworking.Modelo.Cls_Reserva
import com.example.pry_vitan_coworking.R

// Christian · D01 Reservas
// Adaptador del RecyclerView: repite item_reserva_admin.xml por cada reserva.
// El administrador solo consulta, por eso las tarjetas no tienen botones.
class Cls_AdaptadorAdminReservas(
    private var listaReservas: List<Cls_Reserva>
) : RecyclerView.Adapter<Cls_AdaptadorAdminReservas.ReservaViewHolder>() {

    class ReservaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitulo: TextView = itemView.findViewById(R.id.txtTitulo)
        val txtEstado: TextView = itemView.findViewById(R.id.txtEstado)
        val txtCliente: TextView = itemView.findViewById(R.id.txtCliente)
        val txtFechaHora: TextView = itemView.findViewById(R.id.txtFechaHora)
        val txtMotivo: TextView = itemView.findViewById(R.id.txtMotivo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_reserva_admin, parent, false)
        return ReservaViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        val reserva = listaReservas[position]
        val contexto = holder.itemView.context

        holder.txtTitulo.text = "#${reserva.id_reserva} · ${reserva.sala}"
        holder.txtCliente.text = reserva.cliente
        holder.txtFechaHora.text = "${formatearFecha(reserva.fecha_uso)} · ${formatearHorario(reserva)}"
        holder.txtMotivo.text = "Motivo: ${reserva.motivo}"

        // Etiqueta del estado con su color, como en el mockup ("pendiente" → "Pendiente")
        holder.txtEstado.text = reserva.estado.replaceFirstChar { it.uppercase() }
        val (colorFondo, colorTexto) = when (reserva.estado) {
            "pendiente" -> R.color.admin_pendiente_fondo to R.color.admin_pendiente_texto
            "confirmada" -> R.color.admin_confirmada_fondo to R.color.admin_confirmada_texto
            "cancelada" -> R.color.admin_cancelada_fondo to R.color.admin_cancelada_texto
            else -> R.color.admin_finalizada_fondo to R.color.admin_finalizada_texto
        }
        holder.txtEstado.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(contexto, colorFondo))
        holder.txtEstado.setTextColor(ContextCompat.getColor(contexto, colorTexto))
    }

    override fun getItemCount(): Int = listaReservas.size

    fun actualizarLista(nuevaLista: List<Cls_Reserva>) {
        listaReservas = nuevaLista
        notifyDataSetChanged()
    }

    // "2026-11-20" → "20/11/2026"
    private fun formatearFecha(fecha: String?): String {
        val partes = fecha?.split("-")
        return if (partes != null && partes.size == 3) partes.reversed().joinToString("/") else "Sin fecha"
    }

    // Por día: "día completo"; por hora: "11:00 a 15:00" (se quitan los segundos)
    private fun formatearHorario(reserva: Cls_Reserva): String {
        val inicio = reserva.hora_inicio
        val fin = reserva.hora_fin
        return when {
            reserva.modalidad == "dia" -> "día completo"
            inicio != null && fin != null -> "${inicio.take(5)} a ${fin.take(5)}"
            else -> "sin horario"
        }
    }
}
