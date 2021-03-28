package com.smartcity.client.ui.main.cart

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.models.product.Cart
import com.smartcity.client.util.Constants
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.layout_cart_item_header.view.*

class CartAdapter (
    private val requestManager: RequestManager,
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private  var  cartProductRecyclerAdapter: CartProductAdapter?=null

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Cart>() {

        override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem.store == newItem.store
        }

        override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
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
                .inflate(R.layout.layout_cart_item_header, parent, false),
            interaction = interaction,
            cartProductRecyclerAdapter = cartProductRecyclerAdapter,
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

    fun submitList(attributeValueList: Set<Cart>?){
        val newList = attributeValueList?.toMutableList()
        differ.submitList(newList)
    }


    class CartHolder(
        itemView: View,
        private val interaction: Interaction?,
        private var cartProductRecyclerAdapter: CartProductAdapter?,
        val requestManager: RequestManager
    ) : RecyclerView.ViewHolder(itemView),
        CartProductAdapter.Interaction {

        fun initProductsRecyclerView(recyclerview:RecyclerView){
            recyclerview.apply {
                layoutManager = LinearLayoutManager(context)
                val topSpacingDecorator = TopSpacingItemDecoration(0)
                removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
                addItemDecoration(topSpacingDecorator)

                cartProductRecyclerAdapter =
                    CartProductAdapter(
                        requestManager,
                        this@CartHolder
                    )
                addOnScrollListener(object: RecyclerView.OnScrollListener(){

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    }
                })
                adapter = cartProductRecyclerAdapter
            }

        }

        @SuppressLint("SetTextI18n")
        fun bind(item: Cart) = with(itemView) {
            itemView.store_name_cart.text=item.store


            var total=0.0
            item.cartProductVariants.map {
                total=it.productVariant.price*it.unit+total
            }
            itemView.cart_product_total_price.text=total.toString()+ Constants.DINAR_ALGERIAN


            initProductsRecyclerView(itemView.products_cart_recycler_view)
            cartProductRecyclerAdapter?.let {
                it.submitList(
                    item.cartProductVariants.sortedBy { it.productVariant.price }
                )
            }

        }


        override fun addQuantity(variantId: Long, quantity: Int) {
            interaction?.addQuantity(variantId,quantity)
        }

        override fun deleteCartProduct(variantId: Long) {
            interaction?.deleteCartProduct(variantId)
        }
    }

    interface Interaction {
        fun addQuantity(variantId: Long, quantity: Int)
        fun deleteCartProduct(variantId: Long)
    }

}