package com.smartcity.client.ui.main.custom_category.customCategory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.smartcity.client.R
import com.smartcity.client.models.CustomCategory
import com.smartcity.client.util.ActionConstants
import kotlinx.android.synthetic.main.layout_custom_category_list_item.view.*

class CustomCategoryAdapter (
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CustomCategory>() {

        override fun areItemsTheSame(oldItem: CustomCategory, newItem: CustomCategory): Boolean {
            return oldItem.pk == newItem.pk
        }

        override fun areContentsTheSame(oldItem: CustomCategory, newItem: CustomCategory): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
        AsyncListDiffer(
            CustomCategoryRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class CustomCategoryRecyclerChangeCallback(
        private val adapter: CustomCategoryAdapter
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
    ): CustomCategoryHolder {

        return CustomCategoryHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_custom_category_list_item, parent, false),
            interaction = interaction
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CustomCategoryHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(customCategoryList: List<CustomCategory>?){
        val newList = customCategoryList?.toMutableList()
        differ.submitList(newList)
    }

    class CustomCategoryHolder(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {


        fun bind(item: CustomCategory) = with(itemView) {

            itemView.setOnClickListener {
                interaction?.onItemSelected(
                    adapterPosition,
                    item,
                    ActionConstants.SELECTED)
            }

            itemView.update_custom_category.setOnClickListener {
                interaction?.onItemSelected(
                    adapterPosition,
                    item,
                    ActionConstants.UPDATE)
            }

            itemView.delete_custom_category.setOnClickListener {
                interaction?.onItemSelected(
                    adapterPosition,
                    item,
                    ActionConstants.DELETE)
            }
            itemView.custom_category_name.text=item.name
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: CustomCategory,action:Int)
    }

}