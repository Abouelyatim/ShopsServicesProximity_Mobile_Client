package com.smartcity.client.ui.main.blog.viewProduct.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.smartcity.client.R
import com.smartcity.client.models.product.Attribute
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.option_value_item_header.view.*


class OptionsAdapter (
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private  var  valuesRecyclerAdapter: ValuesAdapter?=null

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
        private val adapter: OptionsAdapter
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
                .inflate(R.layout.option_value_item_header, parent, false),
            interaction = interaction,
            valuesRecyclerAdapter = valuesRecyclerAdapter
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
        private val interaction: Interaction?,
        private var valuesRecyclerAdapter: ValuesAdapter?
    ) : RecyclerView.ViewHolder(itemView),
        ValuesAdapter.Interaction {

        fun initOptionsRecyclerView(recyclerview:RecyclerView){
            recyclerview.apply {
                layoutManager = GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false)
                val topSpacingDecorator = TopSpacingItemDecoration(0)
                removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
                addItemDecoration(topSpacingDecorator)

                valuesRecyclerAdapter =
                    ValuesAdapter(
                        this@OptionHolder
                    )
                addOnScrollListener(object: RecyclerView.OnScrollListener(){

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    }
                })
                adapter = valuesRecyclerAdapter
            }

        }

        fun bind(item: Attribute) = with(itemView) {
            itemView.product_option_header.text=item.name
            initOptionsRecyclerView(itemView.value_recyclerview_variant)
            valuesRecyclerAdapter?.let {
                it.submitList(
                    item.attributeValues,
                    itemView.product_option_header_text
                )
            }

        }


        override fun onItemSelected(option: String, value: String){
            itemView.value_recyclerview_variant.adapter!!.notifyDataSetChanged()
            interaction?.onItemSelected(option,value)
        }
    }

    interface Interaction {
        fun onItemSelected(option: String, value: String)
    }


}