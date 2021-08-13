package com.smartcity.client.ui.main.account.stores.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.*
import com.smartcity.client.R
import kotlinx.android.synthetic.main.layout_order_filter_item.view.*

class CategoriesAdapter (
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
        AsyncListDiffer(
            CategoryValuesRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class CategoryValuesRecyclerChangeCallback(
        private val adapter: CategoriesAdapter
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
    ): ValueHolder {
        return ValueHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_order_filter_item, parent, false),
            interaction = interaction
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ValueHolder -> {
                holder.bind(differ.currentList.get(position),position,selectedCategoriesValue)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var selectedCategoriesValue:String = ""

    fun submitList(attributeValueList: List<String>?,selectedCategory:String){
        this.selectedCategoriesValue=selectedCategory
        val newList = attributeValueList?.toMutableList()
        differ.submitList(newList)
    }

    class ValueHolder(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bind(item: String, position: Int,selectedCategoriesValue:String) = with(itemView) {

            if(item == selectedCategoriesValue){
                itemView.category_value_container.background= ResourcesCompat.getDrawable(resources,
                    R.drawable.round_corner_bleu_light,null)
                itemView.category_value_name.setTextColor(
                    ResourcesCompat.getColor(resources,
                        R.color.bleu,null))
            }else{
                itemView.category_value_container.background= ResourcesCompat.getDrawable(resources,
                    R.drawable.round_corner_grey,null)
                itemView.category_value_name.setTextColor(
                    ResourcesCompat.getColor(resources,
                        R.color.dark,null))
            }


            itemView.category_value_name.text=item
            itemView.setOnClickListener {
                if(item in selectedCategoriesValue){
                    interaction?.onItemDeSelected(item)
                }else{
                    interaction?.onItemSelected(item)
                }
            }
        }
    }

    interface Interaction {
        fun onItemSelected(item: String)
        fun onItemDeSelected(item: String)
    }
}