package com.smartcity.client.ui.main.blog.viewProduct.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.*
import com.smartcity.client.R
import com.smartcity.client.models.product.AttributeValue
import kotlinx.android.synthetic.main.option_value_item.view.*

class ValuesAdapter (
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    companion object{
        var selectedPositionMap:MutableMap<String,Int> = mutableMapOf()
        var oldPositionMap:MutableMap<String,Int> = mutableMapOf()
        var availableOptionValues= listOf<AttributeValue>()

        fun getMapSelectedPosition():MutableMap<String,Int>{
            return selectedPositionMap
        }

        fun getOldPosition():MutableMap<String,Int>{
            return oldPositionMap
        }

        fun setAvailableOptionValue(map:List<AttributeValue>){
            availableOptionValues =map
        }
    }
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
            ValuesRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )
    internal inner class ValuesRecyclerChangeCallback(
        private val adapter: ValuesAdapter
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
                .inflate(R.layout.option_value_item, parent, false),
            interaction = interaction
        )

    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ValueHolder -> {
                holder.bind(differ.currentList.get(position),position,viewText)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    lateinit var viewText:TextView
    fun submitList(attributeValueList: Set<AttributeValue>?,view: View){
        viewText=view as TextView
        val newList = attributeValueList?.toMutableList()
        differ.submitList(newList)
    }


    class ValueHolder(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bind(item: AttributeValue, position: Int, view: TextView) = with(itemView) {



            if(selectedPositionMap[item.attribute] == position){
                if(oldPositionMap[item.attribute]== selectedPositionMap[item.attribute]){
                    itemView.option_value_container.background=ResourcesCompat.getDrawable(resources,R.drawable.raduis_selector,null)
                    selectedPositionMap.put(item.attribute,-1)
                    view.text="Please select"
                }else{
                    itemView.option_value_container.background= ResourcesCompat.getDrawable(resources,R.drawable.raduis_selector_selected,null)
                }
            } else{
                itemView.option_value_container.background=ResourcesCompat.getDrawable(resources,R.drawable.raduis_selector,null)

            }





            itemView.option_value_name.text=item.value


            if(availableOptionValues.isNotEmpty()){
                if(item in availableOptionValues){
                    itemView.option_value_name.setTextColor(Color.parseColor("#000000"))
                    itemView.setOnClickListener {
                        oldPositionMap.put(item.attribute,
                            selectedPositionMap[item.attribute]!!)
                        interaction?.onItemSelected(item.attribute,item.value)
                        selectedPositionMap.put(item.attribute,position)
                        view.text=item.value

                    }
                }else{
                    itemView.option_value_name.setTextColor(Color.parseColor("#c4c4c4"))
                    itemView.setOnClickListener(null)
                }

            }else{
                itemView.setOnClickListener {
                    oldPositionMap.put(item.attribute,
                        selectedPositionMap[item.attribute]!!)
                    interaction?.onItemSelected(item.attribute,item.value)
                    selectedPositionMap.put(item.attribute,position)
                    view.text=item.value
                }
            }

        }
    }

    interface Interaction {
        fun onItemSelected(option: String, value: String)
    }
}