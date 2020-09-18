package com.simon.yoga_statica.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.simon.yoga_statica.classes.Card
import com.simon.yoga_statica.interfaces.OnRecyclerItemClickListener
import com.simon.yoga_statica.views.CardItemView

class CardAdapter(private val list: List<Card>) : RecyclerView.Adapter<CardItemView>() {
    private var listener: OnRecyclerItemClickListener? = null
    fun setOnDeleteListener(listener: OnRecyclerItemClickListener) {
        this.listener = listener

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardItemView {
        val inflater = LayoutInflater.from(parent.context)
        return CardItemView(inflater, parent)
     }



    override fun onBindViewHolder(holder: CardItemView, position: Int) {
        val card: Card = list[position]
        card.allCardCount = itemCount
        card.currentCardNum = position + 1
        holder.bind(card)

        holder.addAsuna.setOnClickListener {
            listener?.onItemClicked(card.id, position)
        }
    }
    override fun getItemCount(): Int = list.size
}