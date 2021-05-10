package com.smartcity.client.ui.main.account.orders

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.smartcity.client.R
import com.smartcity.client.models.OrderProductVariant
import com.smartcity.client.util.Constants
import kotlinx.android.synthetic.main.layout_product_order_item.view.*

class OrderProductAdapter (
    private val requestManager: RequestManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<OrderProductVariant>() {

        override fun areItemsTheSame(oldItem: OrderProductVariant, newItem: OrderProductVariant): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderProductVariant, newItem: OrderProductVariant): Boolean {
            return oldItem == newItem
        }
    }

    private val differ =
        AsyncListDiffer(
            OrderProductRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class OrderProductRecyclerChangeCallback(
        private val productAdapter: OrderProductAdapter
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
    ): OrderProductHolder {

        return OrderProductHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_product_order_item, parent, false),
            requestManager = requestManager
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OrderProductHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(orderList: List<OrderProductVariant>?){
        val newList = orderList?.toMutableList()
        differ.submitList(newList)
    }

    class OrderProductHolder(
        itemView: View,
        val requestManager: RequestManager
    ) : RecyclerView.ViewHolder(itemView) {


        @SuppressLint("SetTextI18n")
        fun bind(item: OrderProductVariant) = with(itemView) {

            val name=item.productName
            name.replace("\n","").replace("\r","")
            if(name.length>70){
                itemView.order_product_name.text=name.subSequence(0,70).padEnd(73,'.')
            }else{
                itemView.order_product_name.text=name
            }
            itemView.order_product_price.text=item.productVariant.price.toString()+ Constants.DINAR_ALGERIAN

            itemView.order_product_quantity.text=item.quantity.toString()

            var image=item.productImage.image
            item.productVariant.image?.let {
                image=it
            }

            requestManager
                .load(Constants.PRODUCT_IMAGE_URL+image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(itemView.order_product_image)


            var options=""
            item.productVariant.productVariantAttributeValuesProductVariant.map {
                options=options+" , "+it.attributeValue.value+" "+it.attributeValue.attribute
            }
            itemView.order_product_variant.text=options.drop(2)
        }
    }
}