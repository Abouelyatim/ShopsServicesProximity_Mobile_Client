package com.smartcity.client.ui.main.blog.viewProduct.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.smartcity.client.R
import com.smartcity.client.util.Constants
import kotlinx.android.synthetic.main.layout_view_product_image_list_item.view.*

class VariantImageAdapter (
    private val requestManager: RequestManager,
    private val interaction: Interaction? = null
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return (oldItem == newItem)
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
        AsyncListDiffer(
            VariantImagRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class VariantImagRecyclerChangeCallback(
        private val adapter: VariantImageAdapter
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
    ): VariantImageHolder {

        return VariantImageHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_view_product_image_list_item, parent, false),
            interaction = interaction,
            requestManager = requestManager
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VariantImageHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(productList: List<String>?){
        val newList = productList?.toMutableList()
        differ.submitList(newList)
    }



    class VariantImageHolder(
        itemView: View,
        val requestManager: RequestManager,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {


        fun bind(item: String) = with(itemView) {

            itemView.setOnClickListener {
                interaction?.onItemSelected()
            }
            val image= Constants.PRODUCT_IMAGE_URL +item
            requestManager
                .load(image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(itemView.product_variant_image)


        }
    }

    interface Interaction {
        fun onItemSelected()
    }

}