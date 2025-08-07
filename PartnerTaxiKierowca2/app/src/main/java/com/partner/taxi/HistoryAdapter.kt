package com.partner.taxi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(
    private val items: List<HistoryEntry>
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDateTime: TextView    = view.findViewById(R.id.tvDateTime)
        val tvSource: TextView      = view.findViewById(R.id.tvSource)
        val tvPaymentType: TextView = view.findViewById(R.id.tvPaymentType)
        val tvAmount: TextView      = view.findViewById(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = items[position]
        holder.tvDateTime.text    = entry.dateTime
        holder.tvSource.text      = entry.source
        holder.tvPaymentType.text = entry.paymentType
        holder.tvAmount.text      = "${entry.amount} zł"
    }

    override fun getItemCount(): Int = items.size
}
