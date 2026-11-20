package com.example.pry_vitan_coworking.Adaptador

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pry_vitan_coworking.Modelo.Cls_Usuario
import com.example.pry_vitan_coworking.R

// Christian · D02 Usuarios
// Adaptador del RecyclerView: repite item_usuario_admin.xml por cada usuario.
// El administrador solo consulta, por eso las tarjetas no tienen botones.
class Cls_AdaptadorAdminUsuarios(
    private var listaUsuarios: List<Cls_Usuario>
) : RecyclerView.Adapter<Cls_AdaptadorAdminUsuarios.UsuarioViewHolder>() {

    class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombre)
        val imgFavorito: ImageView = itemView.findViewById(R.id.imgFavorito)
        val txtContacto: TextView = itemView.findViewById(R.id.txtContacto)
        val txtRol: TextView = itemView.findViewById(R.id.txtRol)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario_admin, parent, false)
        return UsuarioViewHolder(vista)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = listaUsuarios[position]
        val contexto = holder.itemView.context

        holder.txtNombre.text = nombreParaMostrar(usuario)

        // La estrella solo aparece si el administrador lo marcó como favorito
        holder.imgFavorito.visibility = if (usuario.favorito) View.VISIBLE else View.GONE

        // "cliente@vitan.mx · 771 123 4567" (si no tiene teléfono, solo el correo)
        holder.txtContacto.text = listOfNotNull(usuario.correo, formatearTelefono(usuario.telefono)).joinToString(" · ")

        // Etiqueta del rol: el administrador va en azul marino, los demás en gris
        holder.txtRol.text = usuario.rol.replaceFirstChar { it.uppercase() }
        val esAdministrador = usuario.id_rol == 1
        val colorFondo = if (esAdministrador) R.color.admin_azul_marino else R.color.admin_rol_fondo
        val colorTexto = if (esAdministrador) android.R.color.white else R.color.admin_azul_marino
        holder.txtRol.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(contexto, colorFondo))
        holder.txtRol.setTextColor(ContextCompat.getColor(contexto, colorTexto))
    }

    override fun getItemCount(): Int = listaUsuarios.size

    fun actualizarLista(nuevaLista: List<Cls_Usuario>) {
        listaUsuarios = nuevaLista
        notifyDataSetChanged()
    }

    // Empresa: solo su nombre ("Consultores Huastecos"), como en el mockup.
    // Persona: nombre y apellidos ("Mariana Hernández Cruz"); el materno puede venir vacío.
    private fun nombreParaMostrar(usuario: Cls_Usuario): String {
        if (usuario.id_rol == 3) return usuario.nombre
        return listOfNotNull(usuario.nombre, usuario.ap, usuario.am)
            .filter { it.isNotBlank() }
            .joinToString(" ")
    }

    private fun formatearTelefono(telefono: String?): String? {
        if (telefono.isNullOrBlank()) return null
        return if (telefono.length == 10) {
            "${telefono.substring(0, 3)} ${telefono.substring(3, 6)} ${telefono.substring(6)}"
        } else {
            telefono
        }
    }
}
