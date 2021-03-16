package com.smartcity.client.ui.interest

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.*
import com.smartcity.client.R
import kotlinx.android.synthetic.main.layout_interest_category_value_item.view.*

class CategoriesValueAdapter (
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    companion object{
        var selectedPositionMap:MutableMap<String,List<Int>> = mutableMapOf()
    }
    var category=""

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
        private val adapter: CategoriesValueAdapter
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
                .inflate(R.layout.layout_interest_category_value_item, parent, false),
            interaction = interaction
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ValueHolder -> {
                holder.bind(differ.currentList.get(position),position,category)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    fun submitList(attributeValueList: List<String>?,category:String){
        this.category=category
        val newList = attributeValueList?.toMutableList()
        differ.submitList(newList)
    }

    class ValueHolder(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bind(item: String, position: Int,category: String) = with(itemView) {
            selectedPositionMap[category]?.let {
                if(position in selectedPositionMap[category]!!){
                    if(selectedPositionMap[category]!!.count { it == position } > 1){
                        itemView.category_value_container.background=ResourcesCompat.getDrawable(resources,R.drawable.raduis_selector_white,null)
                        itemView.category_value_name.setTextColor(Color.parseColor("#000000"))
                        val list=selectedPositionMap[category]!!.toMutableList()
                        list.removeAll { it==position }
                        list.remove(position)
                        selectedPositionMap.put(category,list)

                    }else{
                        itemView.category_value_container.background=ResourcesCompat.getDrawable(resources,R.drawable.raduis_selector_selected_white,null)
                        itemView.category_value_name.setTextColor(Color.parseColor("#ffffff"))
                    }
                } else{
                    itemView.category_value_container.background=
                        ResourcesCompat.getDrawable(resources,R.drawable.raduis_selector_white,null)
                    itemView.category_value_name.setTextColor(Color.parseColor("#000000"))
                }
            }

            itemView.category_value_name.text=item
                itemView.setOnClickListener {
                    if(selectedPositionMap[category]==null){
                        val list= listOf<Int>(position)
                        selectedPositionMap.put(category,list)
                    }else{
                        val list=selectedPositionMap[category]!!.toMutableList()
                        list.add(position)
                        selectedPositionMap.put(category,list)
                    }
                    interaction?.onItemSelected(category,item)

                }
        }
    }

    interface Interaction {
        fun onItemSelected(option: String, value: String)
    }
}