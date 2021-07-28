package com.smartcity.client.ui.main.blog.products

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.smartcity.client.R
import com.smartcity.client.models.product.OfferType
import com.smartcity.client.models.product.Product
import com.smartcity.client.util.Constants
import com.smartcity.client.util.GenericViewHolder
import kotlinx.android.synthetic.main.layout_store_product_list_item.view.*
import java.math.BigDecimal
import java.math.RoundingMode

class ProductGridAdapter (
    private val requestManager: RequestManager,
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG: String = "AppDebug"
    private val NO_MORE_RESULTS = -1
    private val PRODUCT_ITEM = 0
    private val NO_MORE_RESULTS_PRODUCT_MARKER = Product(
        NO_MORE_RESULTS.toLong(),
        "" ,
        "",
        listOf(),
        listOf(),
        setOf(),
        NO_MORE_RESULTS,
        null,
        "",
        -1,
        0
    )

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }
    private val differ =
        AsyncListDiffer(
            ProductGridRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when(viewType){

            NO_MORE_RESULTS ->{
                Log.e(TAG, "onCreateViewHolder: No more results...")
                return GenericViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_no_more_results,
                        parent,
                        false
                    )
                )
            }

            PRODUCT_ITEM ->{
                return ProductViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_store_product_list_item,
                        parent,
                        false
                    ),
                    interaction = interaction,
                    requestManager = requestManager
                )
            }
            else -> {
                return ProductViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_store_product_list_item,
                        parent,
                        false
                    ),
                    interaction = interaction,
                    requestManager = requestManager
                )
            }
        }
    }

    internal inner class ProductGridRecyclerChangeCallback(
        private val adapter: ProductGridAdapter
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductViewHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(differ.currentList.get(position).id > -1){
            return PRODUCT_ITEM
        }
        return differ.currentList.get(position).id.toInt()
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // Prepare the images that will be displayed in the RecyclerView.
    // This also ensures if the network connection is lost, they will be in the cache
    /* fun preloadGlideImages(
         requestManager: RequestManager,
         list: List<BlogPost>
     ){
         for(blogPost in list){
             requestManager
                 .load(blogPost.image)
                 .preload()
         }
     }*/

    fun submitList(
        productList: List<Product>?,
        isQueryExhausted: Boolean
    ){
        val newList = productList?.toMutableList()
        if (isQueryExhausted)
            newList?.add(NO_MORE_RESULTS_PRODUCT_MARKER)
        val commitCallback = Runnable {
            // if process died must restore list position
            // very annoying
            interaction?.restoreGridListPosition()
        }
        differ.submitList(newList, commitCallback)
    }

    class ProductViewHolder
    constructor(
        itemView: View,
        val requestManager: RequestManager,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bind(item: Product) = with(itemView) {
            try {
                val image= Constants.PRODUCT_IMAGE_URL +item.images.first().image
                requestManager
                    .load(image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(itemView.product_image)

                itemView.setOnClickListener {
                    interaction?.onGridItemSelected(bindingAdapterPosition,item)
                }


                val name=item.name
                name.replace("\n","").replace("\r","")
                if(name.length>70){
                    itemView.product_name.text=name.subSequence(0,70).padEnd(73,'.')
                }else{
                    itemView.product_name.text=name
                }

                val allPrices = mutableListOf<Double>()
                item.productVariants.map {
                    val offer=it.offer
                    if (offer!=null){
                        when(offer.type){
                            OfferType.PERCENTAGE ->{
                                allPrices.add(BigDecimal(it.price-(it.price*offer.percentage!!/100)).setScale(2, RoundingMode.HALF_EVEN).toDouble())
                            }

                            OfferType.FIXED ->{
                                allPrices.add(it.price-offer.newPrice!!)
                            }
                            null -> {}
                        }
                    }else{
                        allPrices.add(it.price)
                    }
                }
                itemView.product_price.text=allPrices.min().toString()+ Constants.DOLLAR


                val percentages= mutableListOf<Int>()
                val fixed = mutableListOf<Double>()
                item.productVariants.map {
                    val offer=it.offer
                    if(offer!=null){
                        when(offer.type){
                            OfferType.PERCENTAGE ->{
                                percentages.add(offer.percentage!!)
                            }

                            OfferType.FIXED ->{
                                fixed.add(offer.newPrice!!)
                            }
                        }
                    }
                }

                sale_container.visibility= View.GONE

                if(fixed.isNotEmpty() || percentages.isNotEmpty()){
                    sale_container.visibility= View.VISIBLE
                }
            }catch (e: Exception){

            }

        }
    }

    interface Interaction {

        fun onGridItemSelected(position: Int, item: Product)

        fun restoreGridListPosition()
    }
}