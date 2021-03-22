package com.smartcity.client.ui.main.cart

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.smartcity.client.R
import com.smartcity.client.models.product.CartProductVariant
import com.smartcity.client.util.Constants
import kotlinx.android.synthetic.main.layout_product_cart_list_item.view.*

class CartAdapter (
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
        private val adapter: CartAdapter
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
            itemView.cart_product_price.text=item.productVariant.price.toString()+ Constants.DINAR_ALGERIAN

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

        }
    }

    interface Interaction {
        fun addQuantity(variantId: Long, quantity: Int)
    }

}