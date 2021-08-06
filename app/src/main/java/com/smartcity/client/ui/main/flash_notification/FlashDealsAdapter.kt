package com.smartcity.client.ui.main.flash_notification

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.smartcity.client.R
import com.smartcity.client.models.FlashDeal
import com.smartcity.client.util.DateUtils
import kotlinx.android.synthetic.main.layout_flash_deal_list_item.view.*

class FlashDealsAdapter (
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FlashDeal>() {

        override fun areItemsTheSame(oldItem: FlashDeal, newItem: FlashDeal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FlashDeal, newItem: FlashDeal): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
        AsyncListDiffer(
            CustomCategoryRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class CustomCategoryRecyclerChangeCallback(
        private val adapter: FlashDealsAdapter
    ) : ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged()
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FlashDealHolder {
        return FlashDealHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_flash_deal_list_item, parent, false),
            interaction=interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FlashDealHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(flashDealsList: List<FlashDeal>?){
        val newList = flashDealsList?.toMutableList()
        differ.submitList(newList)
    }

    class FlashDealHolder(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(item: FlashDeal) = with(itemView) {
            itemView.flash_title.text=item.title
            itemView.flash_content.text=item.content
            itemView.flash_date.text= DateUtils.convertStringToStringDateSimpleFormat(item.createAt!!)

            itemView.setOnClickListener {
                interaction?.onItemSelected(item)
            }
        }
    }

    interface Interaction{
        fun onItemSelected(item:FlashDeal)
    }
}