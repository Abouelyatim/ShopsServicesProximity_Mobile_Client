package com.smartcity.client.ui.main.custom_category.optionValue

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.smartcity.client.R
import com.smartcity.client.models.product.AttributeValue
import kotlinx.android.synthetic.main.layout_option_value_list_item.view.*

class OptionValuesAdapter(
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AttributeValue>() {

        override fun areItemsTheSame(oldItem: AttributeValue, newItem: AttributeValue): Boolean {
            return oldItem.value == newItem.value
        }

        override fun areContentsTheSame(oldItem: AttributeValue, newItem: AttributeValue): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
        AsyncListDiffer(
            OptionValuesRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )
    internal inner class OptionValuesRecyclerChangeCallback(
        private val adapter: OptionValuesAdapter
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
    ): OptionValuesHolder {

        return OptionValuesHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_option_value_list_item, parent, false),
            interaction = interaction
        )

    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OptionValuesHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(attributeValueList: Set<AttributeValue>?){
        val newList = attributeValueList?.toMutableList()
        differ.submitList(newList)
    }

    class OptionValuesHolder(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {


        fun bind(item: AttributeValue) = with(itemView) {

            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition,item)
            }
            itemView.delete_option_value.setOnClickListener {
                interaction?.onItemSelected(adapterPosition,item)
            }
            itemView.option_value_name.text=item.value
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: AttributeValue)
    }
}