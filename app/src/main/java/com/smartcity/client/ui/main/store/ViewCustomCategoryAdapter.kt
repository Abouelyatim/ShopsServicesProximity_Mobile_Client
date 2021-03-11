package com.smartcity.client.ui.main.store

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.*
import com.smartcity.client.R
import com.smartcity.client.models.CustomCategory
import kotlinx.android.synthetic.main.layout_view_custom_category_list_item.view.*


class ViewCustomCategoryAdapter (
    private val interaction: Interaction? = null,
    private val interactionAll: InteractionAll? =null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_DEFAULT = 0
    private val VIEW_TYPE_TEXT = 1

    companion object{
        var selectedPosition = 0

        fun getSelectedPositions():Int{
            return selectedPosition
        }

        fun setSelectedPositions(position:Int){
             selectedPosition=position
        }
    }

    fun resetSelectedPosition(){
        selectedPosition=0
    }
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
        private val adapterView: ViewCustomCategoryAdapter
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

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_DEFAULT else VIEW_TYPE_TEXT
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        when(viewType){
            VIEW_TYPE_TEXT->{
                return CustomCategoryHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_view_custom_category_list_item, parent, false),
                    interaction = interaction
                )
            }

            VIEW_TYPE_DEFAULT->{
                return AllProductHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_view_custom_category_list_item, parent, false),
                    interactionAll = interactionAll
                )
            }

            else->{
                return CustomCategoryHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_view_custom_category_list_item, parent, false),
                    interaction = interaction
                )
            }
        }


    }





    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CustomCategoryHolder -> {
                holder.bind(differ.currentList.get(position-1),position)
            }

            is AllProductHolder ->{
                holder.bind(position)
            }
        }
    }

    interface InteractionAll{
        fun onItemAddSelected()
    }



    override fun getItemCount(): Int {
        return differ.currentList.size+1
    }

    fun submitList(customCategoryList: List<CustomCategory>?){
        val newList = customCategoryList?.toMutableList()
        differ.submitList(newList)
    }


    class AllProductHolder(
        itemView: View,
        private val interactionAll: InteractionAll?
    ) : RecyclerView.ViewHolder(itemView) {


        fun bind(position: Int) = with(itemView) {
            if(selectedPosition==position){
                itemView.custom_category_container.background= ResourcesCompat.getDrawable(resources,R.drawable.raduis_selector_selected_white,null)
                itemView.view_custom_category_name.setTextColor(Color.parseColor("#ffffff"))
            }
            else{
                itemView.custom_category_container.background=ResourcesCompat.getDrawable(resources,R.drawable.raduis_selector_white,null)
                itemView.view_custom_category_name.setTextColor(Color.parseColor("#000000"))
            }

            itemView.setOnClickListener {
                selectedPosition=position
                interactionAll?.onItemAddSelected()
            }

        }
    }

    class CustomCategoryHolder(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {


        fun bind(item: CustomCategory,position: Int) = with(itemView) {

            if(selectedPosition==position){
                itemView.custom_category_container.background= ResourcesCompat.getDrawable(resources,R.drawable.raduis_selector_selected_white,null)
                itemView.view_custom_category_name.setTextColor(Color.parseColor("#ffffff"))

            }
            else{
                itemView.custom_category_container.background=ResourcesCompat.getDrawable(resources,R.drawable.raduis_selector_white,null)
                itemView.view_custom_category_name.setTextColor(Color.parseColor("#000000"))

            }

            itemView.setOnClickListener {
                selectedPosition=position;
                interaction?.onItemSelected(adapterPosition,item)
            }
            itemView.view_custom_category_name.text=item.name

        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: CustomCategory)
    }

}