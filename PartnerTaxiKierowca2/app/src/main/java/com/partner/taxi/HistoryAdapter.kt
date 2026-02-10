package com.partner.taxi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(
    private val items: List<HistoryEntry>,
    private val onEditLastRideClicked: () -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDateTime: TextView = view.findViewById(R.id.tvDateTime)
        val tvSource: TextView = view.findViewById(R.id.tvSource)
        val tvPaymentType: TextView = view.findViewById(R.id.tvPaymentType)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val buttonEditLastRide: Button = view.findViewById(R.id.buttonEditLastRide)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = items[position]
        holder.tvDateTime.text = entry.dateTime
        holder.tvSource.text = entry.source
        holder.tvPaymentType.text = entry.paymentType
        holder.tvAmount.text = "${entry.amount} zł"

        if (position == 0) {
            holder.buttonEditLastRide.visibility = View.VISIBLE
            holder.buttonEditLastRide.setOnClickListener { onEditLastRideClicked() }
        } else {
            holder.buttonEditLastRide.visibility = View.GONE
            holder.buttonEditLastRide.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int = items.size
}
