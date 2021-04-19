package com.eseo.a2.davidapp.ui.parametre.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eseo.a2.davidapp.R
import com.eseo.a2.davidapp.data.parametre.ParametreItem

class ParametreAdapter(private val paramsList: Array<ParametreItem>) :
    RecyclerView.Adapter<ParametreAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun showItem(param: ParametreItem) {
            // bind de l'icon
            itemView.findViewById<ImageView>(R.id.param_vec).setImageResource(param.icon)

            // bind du text
            itemView.findViewById<TextView>(R.id.param_text).text = param.name

            //bind de l'action à faire lors du clique
            itemView.findViewById<ImageView>(R.id.forward).setOnClickListener {
                param.onClick.invoke()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.parametre_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.showItem(paramsList[position])
    }

    override fun getItemCount(): Int {
        return paramsList.size
    }
}