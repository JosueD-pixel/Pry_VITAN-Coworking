package com.example.pry_vitan_coworking.Adaptador

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pry_vitan_coworking.Modelo.Cls_HoraDisponible
import com.example.pry_vitan_coworking.R

// Horas de 9:00 a 22:00 en cuadrícula (item_hora.xml).
// Verde = libre, gris = ocupada, azul = elegida.
class Cls_HorasAdaptador(
    private val alTocar: (Cls_HoraDisponible) -> Unit
) : RecyclerView.Adapter<Cls_HorasAdaptador.HoraVH>() {

    private var horas: List<Cls_HoraDisponible> = emptyList()
    private var inicio: Int? = null
    private var fin: Int? = null

    class HoraVH(vista: View) : RecyclerView.ViewHolder(vista) {
        val txtHora: TextView = vista.findViewById(R.id.txtHora)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoraVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_hora, parent, false)
        return HoraVH(v)
    }

    override fun onBindViewHolder(holder: HoraVH, position: Int) {
        val h = horas[position]
        holder.txtHora.text = h.inicio
        val i = inicio
        val f = fin
        val elegida = i != null && f != null && h.hora in i..f
        when {
            elegida -> {
                holder.txtHora.setBackgroundColor(Color.parseColor("#1565C0"))
                holder.txtHora.setTextColor(Color.WHITE)
            }
            h.libre -> {
                holder.txtHora.setBackgroundColor(Color.parseColor("#C8E6C9"))
                holder.txtHora.setTextColor(Color.parseColor("#1B5E20"))
            }
            else -> {
                holder.txtHora.setBackgroundColor(Color.parseColor("#E0E0E0"))
                holder.txtHora.setTextColor(Color.parseColor("#9E9E9E"))
            }
        }
        holder.itemView.setOnClickListener { alTocar(h) }
    }

    override fun getItemCount() = horas.size

    fun actualizar(nuevas: List<Cls_HoraDisponible>) {
        horas = nuevas
        inicio = null
        fin = null
        notifyDataSetChanged()
    }

    fun marcarSeleccion(desde: Int?, hasta: Int?) {
        inicio = desde
        fin = hasta
        notifyDataSetChanged()
    }
}