package com.smartcity.client.ui.main.cart.address

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.smartcity.client.R
import com.smartcity.client.models.Address
import kotlinx.android.synthetic.main.layout_address_list_item.view.*

class PickAddressAdapter (
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Address>() {

        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
        AsyncListDiffer(
            AddressRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )
    internal inner class AddressRecyclerChangeCallback(
        private val adapter: PickAddressAdapter
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
    ): AddressHolder {
        return AddressHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_address_list_item, parent, false),
            interaction = interaction
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddressHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(attributeValueList: List<Address>?){
        val newList = attributeValueList?.toMutableList()
        differ.submitList(newList)
    }

    class AddressHolder(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView){


        @SuppressLint("SetTextI18n")
        fun bind(item: Address) = with(itemView) {
            itemView.address_.text=item.fullAddress
            itemView.delete_address.visibility=View.GONE
            address_container.setOnClickListener {
                interaction?.selectedAddress(item)
            }
        }
    }

    interface Interaction {
        fun selectedAddress(address: Address)
    }
}