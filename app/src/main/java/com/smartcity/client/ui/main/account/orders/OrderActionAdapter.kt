package com.smartcity.client.ui.main.account.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.smartcity.client.R
import kotlinx.android.synthetic.main.layout_view_order_actions_item.view.*

class OrderActionAdapter (
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object{
        var selectedActionPosition = 0

        fun getSelectedActionPositions():Int{
            return selectedActionPosition
        }

        fun setSelectedActionPositions(position:Int){
            selectedActionPosition =position
        }
    }

    fun resetSelectedActionPosition(){
        selectedActionPosition =0
    }

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Triple<String,Int,Int>>() {

        override fun areItemsTheSame(oldItem: Triple<String,Int,Int>, newItem: Triple<String,Int,Int>): Boolean {
            return oldItem.first == newItem.first
        }

        override fun areContentsTheSame(oldItem: Triple<String,Int,Int>, newItem: Triple<String,Int,Int>): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
        AsyncListDiffer(
            OrderActionRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class OrderActionRecyclerChangeCallback(
        private val adapterView: OrderActionAdapter
    ) : ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapterView.notifyItemRangeChanged(position, count, payload)
        }

        override fun onInserted(position: Int, count: Int) {
            adapterView.notifyItemRangeChanged(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapterView.notifyDataSetChanged()
        }

        override fun onRemoved(position: Int, count: Int) {
            adapterView.notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return OrderActionHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_view_order_actions_item, parent, false),
            interaction = interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OrderActionHolder -> {
                holder.bind(differ.currentList.get(position),position)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(orderActionList: List<Triple<String,Int,Int>>?){
        val newList = orderActionList?.toMutableList()
        differ.submitList(newList)
    }

    class OrderActionHolder(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {


        fun bind(item:Triple<String,Int,Int>,position: Int) = with(itemView) {

            if(selectedActionPosition ==position){
                itemView.order_action_icon.background=androidx.core.content.res.ResourcesCompat.getDrawable(resources,
                    item.second,null)

                itemView.order_action_container.background= androidx.core.content.res.ResourcesCompat.getDrawable(resources,
                    R.drawable.raduis_selector_selected_bleu,null)

                itemView.order_action_name.setTextColor(android.graphics.Color.parseColor("#ffffff"))

            }
            else{
                itemView.order_action_icon.background=androidx.core.content.res.ResourcesCompat.getDrawable(resources,
                    item.third,null)

                itemView.order_action_container.background= androidx.core.content.res.ResourcesCompat.getDrawable(resources,
                    R.drawable.raduis_selector_not_selected_white,null)

                itemView.order_action_name.setTextColor(android.graphics.Color.parseColor("#000000"))

            }

            itemView.setOnClickListener {
                selectedActionPosition =position
                interaction?.onActionItemSelected(bindingAdapterPosition,item.first)
            }
            itemView.order_action_name.text=item.first

        }
    }

    interface Interaction {
        fun onActionItemSelected(position: Int, item: String)
    }
}