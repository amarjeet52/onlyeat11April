package com.codebrew.clikat.module.feedback.feedback_new

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.codebrew.clikat.data.model.api.SuggestionDataItem
import com.codebrew.clikat.databinding.ItemFeedbackSuggestionsBinding


class FeedbackSuggestionsAdapter(val callback: OnSubmitSuggestion) : RecyclerView.Adapter<FeedbackSuggestionsAdapter.ViewHolder>() {
    private var binding: ItemFeedbackSuggestionsBinding? = null
    private var list = ArrayList<SuggestionDataItem>()

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = ItemFeedbackSuggestionsBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(binding?.root)
    }


    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView as View) {

        init {
            binding?.clLayout?.setOnClickListener {
                callback.onSubmitSuggestion(list[adapterPosition])
            }
        }

        fun bind(item: SuggestionDataItem) = with(itemView) {
            binding?.suggestionData = item
            binding?.executePendingBindings()
        }
    }

    fun addList(list: ArrayList<SuggestionDataItem>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    interface OnSubmitSuggestion {
        fun onSubmitSuggestion(item: SuggestionDataItem)
    }
}

