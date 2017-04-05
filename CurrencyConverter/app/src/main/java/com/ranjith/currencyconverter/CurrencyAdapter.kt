package com.ranjith.currencyconverter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.ranjith.currencyconverter.models.Rate
import org.jetbrains.anko.find
import java.util.*

/**
 * Created by ranjith on 2017-03-27.
 */
class CurrencyAdapter (var rates: List<Rate>?) : RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CurrencyViewHolder? {
        return CurrencyViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.currency_item, parent, false) as ViewGroup?)
    }

    override fun getItemCount(): Int {
        return rates?.count() ?: 0
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder?, position: Int) {
        val entry = rates?.elementAt(position)
        holder?.currency?.text = entry?.currency
        holder?.value?.text = entry?.displayRate()
    }

    class CurrencyViewHolder(parent: ViewGroup?) : RecyclerView.ViewHolder(parent) {
        var currency: TextView? = parent?.find<TextView>(R.id.currency)
        var value: TextView? = parent?.find<TextView>(R.id.value)
    }
}