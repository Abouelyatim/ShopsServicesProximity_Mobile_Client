package com.smartcity.client.ui.interest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.google.android.flexbox.*
import com.smartcity.client.R
import com.smartcity.client.models.product.Category
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.layout_interest_category_item.view.*

class CategoriesAdapter (
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private  var  categoryValuesRecyclerAdapter: CategoriesValueAdapter?=null

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Category>() {

        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
        AsyncListDiffer(
            CategoryRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class CategoryRecyclerChangeCallback(
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
    ): CategoryHolder {

        return CategoryHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_interest_category_item, parent, false),
            interaction,
            categoryValuesRecyclerAdapter
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CategoryHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(categoryList: List<Category>?){
        val newList = categoryList?.toMutableList()
        differ.submitList(newList)
    }

    class CategoryHolder(
        itemView: View,
        private val interaction: Interaction?,
        private var categoryValuesRecyclerAdapter: CategoriesValueAdapter?
    ) : RecyclerView.ViewHolder(itemView),
        CategoriesValueAdapter.Interaction{

        fun initOptionsRecyclerView(recyclerview:RecyclerView){
            recyclerview.apply {
                layoutManager = FlexboxLayoutManager(context)
                (layoutManager as FlexboxLayoutManager).justifyContent = JustifyContent.CENTER
                (layoutManager as FlexboxLayoutManager).flexWrap= FlexWrap.WRAP


                val topSpacingDecorator = TopSpacingItemDecoration(0)
                removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
                addItemDecoration(topSpacingDecorator)

                categoryValuesRecyclerAdapter =
                    CategoriesValueAdapter(
                        this@CategoryHolder
                    )
                addOnScrollListener(object: RecyclerView.OnScrollListener(){

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    }
                })
                adapter = categoryValuesRecyclerAdapter
            }

        }

        fun bind(item: Category) = with(itemView) {
            itemView.category_header_text.text=item.name
            initOptionsRecyclerView(itemView.value_recyclerview_category)
            categoryValuesRecyclerAdapter?.let {
                it.submitList(
                    item.subCategorys,
                    item.name
                )
            }

        }

        override fun onItemSelected(option: String, value: String){
            itemView.value_recyclerview_category.adapter!!.notifyDataSetChanged()
            interaction?.onItemSelected(option,value)
        }
    }

    interface Interaction {
        fun onItemSelected(option: String, value: String)
    }

}