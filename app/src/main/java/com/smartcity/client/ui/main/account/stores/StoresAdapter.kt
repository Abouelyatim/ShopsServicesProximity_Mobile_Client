package com.smartcity.client.ui.main.account.stores

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.models.Store
import com.smartcity.client.ui.main.flash_notification.flash.FlashDealsAdapter
import kotlinx.android.synthetic.main.layout_store_list_item.view.*

class StoresAdapter (
    private val interaction: Interaction? = null,
    private val requestManager: RequestManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Store>() {

        override fun areItemsTheSame(oldItem: Store, newItem: Store): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Store, newItem: Store): Boolean {
            return oldItem == newItem
        }
    }

    private val differ =
        AsyncListDiffer(
            StoresRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class StoresRecyclerChangeCallback(
        private val adapter: StoresAdapter
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
    ): StoresHolder {
        return StoresHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_store_list_item, parent, false),
            requestManager = requestManager,
            interaction = interaction
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StoresHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(attributeValueList: List<Store>?){
        val newList = attributeValueList?.toMutableList()
        differ.submitList(newList)
    }

    class StoresHolder(
        itemView: View,
        private val interaction: Interaction?,
        val requestManager: RequestManager
    ) : RecyclerView.ViewHolder(itemView){


        @SuppressLint("SetTextI18n")
        fun bind(item: Store) = with(itemView) {
            itemView.store_name.text=item.name

            itemView.setOnClickListener {
                interaction?.onItemSelected(item)
            }
        }

    }

    interface Interaction{
        fun onItemSelected(item: Store)
    }
}