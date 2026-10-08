package com.example.myapplication123.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication123.R
import com.example.myapplication123.models.RegistroVentilador

// Adaptador para mostrar la lista de registros en el RecyclerView
class RegistroAdapter(
    private val listaRegistros: MutableList<RegistroVentilador>,
    private val onEliminarClick: (RegistroVentilador, Int) -> Unit
) : RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder>() {

    // ViewHolder que enlaza las vistas de la fila con el código
    class RegistroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAmbienteTemperatura: TextView = itemView.findViewById(R.id.tvAmbienteTemperatura)
        val tvEstadoFecha: TextView = itemView.findViewById(R.id.tvEstadoFecha)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_registro, parent, false)
        return RegistroViewHolder(view)
    }

    // Vincula los datos del registro con los componentes visuales de la fila
    override fun onBindViewHolder(holder: RegistroViewHolder, position: Int) {
        val registro = listaRegistros[position]
        val estadoVentilador = if (registro.ventiladorEncendido) "Encendido" else "Apagado"

        holder.tvAmbienteTemperatura.text = "${registro.ambiente}: ${registro.temperatura} °C"
        holder.tvEstadoFecha.text = "Ventilador: $estadoVentilador  ${registro.fechaHora}"

        holder.btnEliminar.setOnClickListener {
            onEliminarClick(registro, position)
        }
    }

    override fun getItemCount(): Int = listaRegistros.size
}
