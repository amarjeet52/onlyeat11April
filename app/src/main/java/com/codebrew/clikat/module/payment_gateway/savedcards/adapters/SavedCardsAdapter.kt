package com.codebrew.clikat.module.payment_gateway.savedcards.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.R
import com.codebrew.clikat.app_utils.AppUtils
import com.codebrew.clikat.data.model.api.SavedCardList
import com.codebrew.clikat.databinding.ItemSavedCardBinding
import com.codebrew.clikat.utils.configurations.Configurations
import kotlinx.android.synthetic.main.item_saved_card.view.*
import net.authorize.acceptsdk.datamodel.transaction.CardData

class SavedCardsAdapter(private val saveCardList: List<com.codebrew.clikat.module.cart.CardData>,
                        private val appUtils: AppUtils) : RecyclerView.Adapter<SavedCardsAdapter.Viewholder>() {
var first=0
    lateinit var cardClick: OnCardClickListener
var selected_position=0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {

        val binding: ItemSavedCardBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_saved_card, parent, false)
        binding.colors = Configurations.colors
        binding.strings = appUtils.loadAppConfig(0).strings
        return Viewholder(binding.root)
    }

    override fun getItemCount(): Int {
        return saveCardList.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        if(first==0)
        {

        }else {
            if (selected_position == position) {
                holder.itemView.ivDeleteCard.visibility = View.VISIBLE
            } else {
                holder.itemView.ivDeleteCard.visibility = View.GONE
            }
        }
       holder. itemView.setOnClickListener {
            selected_position=holder.adapterPosition
            cardClick.onCardClick(saveCardList[holder.adapterPosition], holder.adapterPosition)
            notifyDataSetChanged()
        }
        holder.bind(saveCardList[position])
    }

    fun setCardListener(card: OnCardClickListener) {
        this.cardClick = card
    }


    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(list: com.codebrew.clikat.module.cart.CardData?) {
           itemView.front_card_number.text=list?.card_number

        }


        init {
//            itemView.ivDeleteCard.setOnClickListener {
//                cardClick.onDeleteCard(saveCardList[adapterPosition], adapterPosition)
//            }


        }
    }


    interface OnCardClickListener {
        fun onCardClick(savedCard: com.codebrew.clikat.module.cart.CardData, position: Int)
        fun onDeleteCard(savedCard:  com.codebrew.clikat.module.cart.CardData, position: Int)
    }

}


