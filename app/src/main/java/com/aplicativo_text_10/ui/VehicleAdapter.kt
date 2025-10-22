package com.aplicativo_text_10.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aplicativo_text_10.R
import com.aplicativo_text_10.model.VehicleEntity

class VehicleAdapter(
    private var items: List<VehicleEntity>,
    private val onCheck: (VehicleEntity) -> Unit,
    private val onEdit: (VehicleEntity) -> Unit,
    private val onDelete: (VehicleEntity) -> Unit,
    private val modo: String = "visualizar"
) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

    inner class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNome: TextView = itemView.findViewById(R.id.tvNome)
        val tvPlaca: TextView = itemView.findViewById(R.id.tvPlaca)
        val tvData: TextView = itemView.findViewById(R.id.tvData)
        val tvHora: TextView = itemView.findViewById(R.id.tvHora)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vehicle, parent, false)
        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val vehicle = items[position]

        holder.tvNome.text = vehicle.nomeCompleto
        holder.tvPlaca.text = vehicle.placaCarro
        holder.tvData.text = vehicle.data
        holder.tvHora.text = vehicle.horarioEntrada

        if (modo == "visualizar") {
            holder.btnEdit.visibility = View.GONE
            holder.btnDelete.visibility = View.GONE
        } else {
            holder.btnEdit.setOnClickListener { onEdit(vehicle) }
            holder.btnDelete.setOnClickListener { onDelete(vehicle) }
        }

        holder.itemView.setOnClickListener { onCheck(vehicle) }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<VehicleEntity>) {
        items = newItems
        notifyDataSetChanged()
    }
}
