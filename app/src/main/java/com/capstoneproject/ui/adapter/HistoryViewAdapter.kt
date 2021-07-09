package com.capstoneproject.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capstoneproject.R
import com.capstoneproject.databinding.ItemScreeningHistoryBinding
import com.capstoneproject.model.History
import com.capstoneproject.utils.GlideLoader

class HistoryViewAdapter(private val historyList: List<History>):
    RecyclerView.Adapter<HistoryViewAdapter.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemBinding = ItemScreeningHistoryBinding.bind(itemView)

        internal fun bind(history: History) {
            itemBinding.apply {
                diagnosis.text = history.models[0]
                date.text = history.request_date
                result.text = "Positive %.2f %%, Negative %.2f %%".format(
                    history.positivePercentage, history.negativePercentage)
                GlideLoader(itemView.context).loadImage(Uri.parse(history.imageUrl), imageView2)

                root.setOnClickListener {
                    onItemClickCallback.onItemClicked(history)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_screening_history, parent, false)

        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val history = historyList[position]
        holder.bind(history)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: History)
    }

    private lateinit var onItemClickCallback : OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
}