package com.smartcity.client.ui.main.custom_category.createOption

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.smartcity.client.R
import com.smartcity.client.models.product.Attribute
import com.smartcity.client.util.ActionConstants
import kotlinx.android.synthetic.main.layout_options_list_item.view.*

class OptionAdapter(
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Attribute>() {

        override fun areItemsTheSame(oldItem: Attribute, newItem: Attribute): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Attribute, newItem: Attribute): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
        AsyncListDiffer(
            OptionRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )
    internal inner class OptionRecyclerChangeCallback(
        private val adapter: OptionAdapter
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
    ): OptionHolder {

        return OptionHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_options_list_item, parent, false),
            interaction = interaction
        )

    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OptionHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(attributeValueList: Set<Attribute>?){
        val newList = attributeValueList?.toMutableList()
        differ.submitList(newList)
    }

    class OptionHolder(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {


        fun bind(item: Attribute) = with(itemView) {

            itemView.setOnClickListener {
                interaction?.onItemSelected(
                    adapterPosition,
                    item,
                    ActionConstants.SELECTED
                )
            }

            itemView.update_option_values.setOnClickListener {
                interaction?.onItemSelected(
                    adapterPosition,
                    item,
                    ActionConstants.UPDATE
                )
            }

            itemView.delete_option.setOnClickListener {
                interaction?.onItemSelected(
                    adapterPosition,
                    item,
                    ActionConstants.DELETE
                )
            }

            itemView.option_name.text=item.name
            val values=item.attributeValues.map { attributeValue -> attributeValue.value
            }.concat()

            itemView.option_values_name.text=values
        }
    }



    interface Interaction {
        fun onItemSelected(position: Int, item: Attribute,action:Int)
    }
}
fun List<String>.concat() = this.joinToString(", ") { it }