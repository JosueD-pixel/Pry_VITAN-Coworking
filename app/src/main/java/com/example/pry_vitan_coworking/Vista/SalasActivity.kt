package com.example.pry_vitan_coworking.Vista

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pry_vitan_coworking.Adaptador.clsSalasAdaptador
import com.example.pry_vitan_coworking.Modelo.dataSala
import com.example.pry_vitan_coworking.Modelo.dataTipoSala
import com.example.pry_vitan_coworking.Presentador.clsSalasPresentador
import com.example.pry_vitan_coworking.R

class SalasActivity : AppCompatActivity() {
    private lateinit var presentador: clsSalasPresentador
    private lateinit var spnTiposSala: Spinner
    private lateinit var rvSalas: RecyclerView
    private lateinit var txtSinSalas: TextView
    private lateinit var btnCerrarSesion: ImageButton
    private lateinit var adaptador: clsSalasAdaptador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_salas)
        //Encabezado azul hasta arriba (detras de la hora y bateria), igual que Christian
        val tbBarra: View = findViewById(R.id.tbBarra)
        val paddingSuperior = tbBarra.paddingTop
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            tbBarra.updatePadding(top = paddingSuperior + systemBars.top)
            insets
        }
        //Hora y bateria en blanco para que se vean sobre el azul
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
        //enlazar componentes XML
        spnTiposSala = findViewById(R.id.spnTiposSala)
        rvSalas = findViewById(R.id.rvSalas)
        txtSinSalas = findViewById(R.id.txtSinSalas)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        //inicializar presentador
        presentador = clsSalasPresentador(this)

        //recyclerView en una columna
        adaptador = clsSalasAdaptador(emptyList(), this::seleccionarSala)
        rvSalas.layoutManager = LinearLayoutManager(this)
        rvSalas.adapter = adaptador

        btnCerrarSesion.setOnClickListener(this::ejecutarCerrarSesion)

        //al cambiar el tipo, el presentador vuelve a pedir las salas
        spnTiposSala.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                presentador.seleccionarTipo(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        presentador.iniciar()
    }

    fun mostrarTiposSala(tipos: List<dataTipoSala>) {
        val adaptadorSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        adaptadorSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnTiposSala.adapter = adaptadorSpinner
    }

    fun mostrarSalas(salas: List<dataSala>) {
        txtSinSalas.visibility = View.GONE
        rvSalas.visibility = View.VISIBLE
        adaptador.actualizarLista(salas)
    }

    fun mostrarSinSalas() {
        adaptador.actualizarLista(emptyList())
        rvSalas.visibility = View.GONE
        txtSinSalas.visibility = View.VISIBLE
    }

    fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    //al tocar una tarjeta, la Vista le avisa al presentador cual sala se eligio
    fun seleccionarSala(sala: dataSala) {
        presentador.seleccionarSala(sala)
    }

    //pendiente pantalla de detalle
    fun abrirDetalleSala(id_sala: Int) {
        //val intent = Intent(this, DetalleSalaActivity::class.java)
        //intent.putExtra("EXTRA_ID_SALA", id_sala)
        //startActivity(intent)
        Toast.makeText(this, "Sala $id_sala", Toast.LENGTH_SHORT).show()
    }

    //pendiente borrar la sesion guardada y regresar al login
    fun ejecutarCerrarSesion(v: View) {
        finish()
    }
}
