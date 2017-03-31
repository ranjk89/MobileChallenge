package com.ranjith.currencyconverter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import org.jetbrains.anko.find
import java.util.*

/**
 * Created by ranjith on 2017-03-27.
 */
class CurrencyAdapter (val amount: Double,  val rates: HashMap<String, Double>?) : RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CurrencyViewHolder? {
        return CurrencyViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.currency_item, parent, false) as ViewGroup?)
    }

    override fun getItemCount(): Int {
        return rates?.count() ?: 0
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder?, position: Int) {
        val entry = rates?.entries?.elementAt(position)
        holder?.currency?.text = entry?.key
        holder?.value?.text = "%.2f".format(entry?.value?.times(amount))
    }

    class CurrencyViewHolder(parent: ViewGroup?) : RecyclerView.ViewHolder(parent) {
        var currency: TextView? = parent?.find<TextView>(R.id.currency)
        var value: TextView? = parent?.find<TextView>(R.id.value)
    }


}