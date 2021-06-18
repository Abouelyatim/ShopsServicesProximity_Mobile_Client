package com.smartcity.client.ui.main.cart.order

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.smartcity.client.R
import com.smartcity.client.models.product.CartProductVariant
import com.smartcity.client.models.product.OfferType
import com.smartcity.client.models.product.ProductVariants
import com.smartcity.client.util.Constants
import kotlinx.android.synthetic.main.layout_product_order_list_item.view.*

class OrderProductAdapter (
    private val requestManager: RequestManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CartProductVariant>() {

        override fun areItemsTheSame(oldItem: CartProductVariant, newItem: CartProductVariant): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartProductVariant, newItem: CartProductVariant): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
        AsyncListDiffer(
            OrderRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class OrderRecyclerChangeCallback(
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
    ): OrderHolder {

        return OrderHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_product_order_list_item, parent, false),
            requestManager = requestManager
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OrderHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(cartList: List<CartProductVariant>?){
        val newList = cartList?.toMutableList()
        differ.submitList(newList)
    }

    class OrderHolder(
        itemView: View,
        val requestManager: RequestManager
    ) : RecyclerView.ViewHolder(itemView) {


        @SuppressLint("SetTextI18n")
        fun bind(item: CartProductVariant) = with(itemView) {

            val name=item.productName
            name.replace("\n","").replace("\r","")
            if(name.length>70){
                itemView.order_product_name.text=name.subSequence(0,70).padEnd(73,'.')
            }else{
                itemView.order_product_name.text=name
            }
            itemView.order_product_price.text=getPrice(item.productVariant)

            itemView.order_product_quantity.text=item.unit.toString()

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

        private fun getPrice(productVariants: ProductVariants):String {
            var prices = 0.0
            productVariants.let {
                val offer=it.offer
                if (offer!=null){
                    when(offer.type){
                        OfferType.PERCENTAGE ->{
                            prices=it.price-(it.price*offer.percentage!!/100)
                        }

                        OfferType.FIXED ->{
                            prices=it.price-offer.newPrice!!
                        }
                        null -> {}
                    }
                }else{
                    prices=it.price
                }
            }

            return "${prices}${Constants.DOLLAR}"
        }
    }
}