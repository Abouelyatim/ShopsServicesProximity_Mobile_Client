package com.smartcity.client.ui.main.cart.cart

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.smartcity.client.R
import com.smartcity.client.models.product.CartProductVariant
import com.smartcity.client.models.product.OfferType
import com.smartcity.client.models.product.ProductVariants
import com.smartcity.client.util.Constants
import kotlinx.android.synthetic.main.layout_product_cart_list_item.view.*

class CartProductAdapter (
    private val requestManager: RequestManager,
    private val interaction: Interaction? = null
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
            CartRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class CartRecyclerChangeCallback(
        private val productAdapter: CartProductAdapter
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
    ): CartHolder {

        return CartHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_product_cart_list_item, parent, false),
            interaction = interaction,
            requestManager = requestManager
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CartHolder -> {
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

    class CartHolder(
        itemView: View,
        val requestManager: RequestManager,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {


        @SuppressLint("SetTextI18n")
        fun bind(item: CartProductVariant) = with(itemView) {

            val name=item.productName
            name.replace("\n","").replace("\r","")
            if(name.length>70){
                itemView.cart_product_name.text=name.subSequence(0,70).padEnd(73,'.')
            }else{
                itemView.cart_product_name.text=name
            }



            itemView.cart_product_price.text=getPrice(item.productVariant,itemView)



            itemView.cart_product_quantity.number=item.unit.toString()
            itemView.cart_product_quantity.setRange(1, item.productVariant.unit)

            var image=item.productImage.image
            item.productVariant.image?.let {
                image=it
            }

            requestManager
                .load(Constants.PRODUCT_IMAGE_URL+image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(itemView.cart_product_image)


            var options=""
            item.productVariant.productVariantAttributeValuesProductVariant.map {
                options=options+" , "+it.attributeValue.value+" "+it.attributeValue.attribute
            }
            itemView.cart_product_variant.text=options.drop(2)

            itemView.cart_product_quantity.setOnValueChangeListener { view: ElegantNumberButton?, oldValue: Int, newValue: Int ->
                interaction?.addQuantity(item.id.cartProductVariantId,newValue)
            }

            itemView.delete_cart_product.setOnClickListener {
                interaction?.deleteCartProduct(item.id.cartProductVariantId)
            }

        }

        @SuppressLint("SetTextI18n")
        private fun getPrice(productVariants: ProductVariants, itemView: View):String {
            var prices = 0.0
            productVariants.let {
                val offer=it.offer
                if (offer!=null){
                    itemView.cart_discount_container.visibility=View.VISIBLE
                    itemView.cart_product_old_price.text="${productVariants.price}${Constants.DOLLAR}"
                    when(offer.type){
                        OfferType.PERCENTAGE ->{
                            prices=it.price-(it.price*offer.percentage!!/100)
                            itemView.cart_discount_value.text="-${offer.percentage}%"
                        }

                        OfferType.FIXED ->{
                            prices=it.price-offer.newPrice!!
                            itemView.cart_discount_value.text="-${offer.newPrice} ${Constants.DOLLAR}"
                        }
                        null -> {}
                    }
                }else{
                    prices=it.price
                    itemView.cart_discount_container.visibility=View.GONE
                }
            }
            return "${prices}${Constants.DOLLAR}"
        }
    }

    interface Interaction {
        fun addQuantity(variantId: Long, quantity: Int)
        fun deleteCartProduct(variantId: Long)
    }

}