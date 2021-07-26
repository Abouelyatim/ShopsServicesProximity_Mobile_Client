package com.smartcity.client.ui.interest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.smartcity.client.R
import com.smartcity.client.models.City
import kotlinx.android.synthetic.main.layout_city_list_item.view.*

class CityAdapter(
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<City>() {

        override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
        AsyncListDiffer(
            CityRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class CityRecyclerChangeCallback(
        private val productAdapter: CityAdapter
    ) : ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            productAdapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onInserted(position: Int, count: Int) {
            productAdapter.notifyItemRangeChanged(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            productAdapter.notifyDataSetChanged()
        }

        override fun onRemoved(position: Int, count: Int) {
            productAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CityHolder {

        return CityHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_city_list_item, parent, false),
            interaction = interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CityHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(cityList: List<City>?){
        val newList = cityList?.toMutableList()
        differ.submitList(newList)
    }

    class CityHolder(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: City) = with(itemView) {

            itemView.city_name.text=item.name

            itemView.setOnClickListener {
                interaction?.onItemSelected(item)
            }
        }

    }

    interface Interaction {
        fun onItemSelected(city: City)
    }
}