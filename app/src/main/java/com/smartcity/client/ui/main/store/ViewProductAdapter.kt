package com.smartcity.client.ui.main.store

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.smartcity.client.R
import com.smartcity.client.models.product.Product
import com.smartcity.client.util.ActionConstants

import com.smartcity.client.util.Constants
import com.smartcity.client.util.Constants.Companion.DINAR_ALGERIAN
import kotlinx.android.synthetic.main.layout_product_list_item.view.*

class ViewProductAdapter (
    private val requestManager: RequestManager,
    private val interaction: Interaction? = null
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return (oldItem.name == newItem.name).and(oldItem.description == newItem.description)
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
        AsyncListDiffer(
            ProductRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class  ProductRecyclerChangeCallback(
        private val adapter: ViewProductAdapter
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
    ):  ProductHolder {

        return  ProductHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_product_list_item, parent, false),
            interaction = interaction,
            requestManager = requestManager
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(productList: List<Product>?){
        val newList = productList?.toMutableList()
        differ.submitList(newList)
    }



    class ProductHolder(
        itemView: View,
        val requestManager: RequestManager,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {


        @SuppressLint("SetTextI18n")
        fun bind(item: Product) = with(itemView) {

            val image= Constants.PRODUCT_IMAGE_URL +item.images.first().image
            requestManager
                .load(image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(itemView.product_image)

            itemView.setOnClickListener {
                interaction?.onItemSelected(item, ActionConstants.SELECTED)
            }


            delete_product.visibility=View.GONE
            val name=item.name
            name.replace("\n","").replace("\r","")
            if(name.length>70){
                itemView.product_name.text=name.subSequence(0,70).padEnd(73,'.')
            }else{
                itemView.product_name.text=name
            }

            itemView.product_price.text=item.productVariants.first().price.toString()+DINAR_ALGERIAN

            itemView.product_quantity.text=item.productVariants.first().unit.toString()+" sold"
        }
    }

    interface Interaction {
        fun onItemSelected(item: Product,action:Int)


    }

}