package com.eseo.a2.davidapp.ui.historique.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eseo.a2.davidapp.R
import com.eseo.a2.davidapp.data.location.MyLocation
import kotlinx.serialization.ExperimentalSerializationApi
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalSerializationApi
class HistoriqueAdapter(private val historicList: Array<MyLocation>, private val map_click: Array<() -> Unit>) :
    RecyclerView.Adapter<HistoriqueAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun showItem(his: MyLocation, cb: () -> Unit) {
            itemView.findViewById<TextView>(R.id.historique_coord).text =
                itemView.context.getString(
                    R.string.lat_string,
                    String.format("%.2f", his.latitude),
                    String.format("%.2f", his.longitude)
                )

            itemView.findViewById<TextView>(R.id.historique_date).text =
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(his.date)

            if (his.address != null)
                itemView.findViewById<TextView>(R.id.historique_text).text = his.address
            else
                itemView.findViewById<TextView>(R.id.historique_text).text =
                    itemView.context.getString(R.string.unknow_address)

            itemView.findViewById<ImageView>(R.id.open_maps_button).setOnClickListener {
                cb.invoke()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.historique_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.showItem(historicList[position], map_click[position])
    }

    override fun getItemCount(): Int {
        return historicList.size
    }
}