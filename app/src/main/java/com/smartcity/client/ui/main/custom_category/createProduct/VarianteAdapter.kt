package com.smartcity.client.ui.main.custom_category.createProduct

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.models.product.ProductVariants
import com.smartcity.client.util.ActionConstants
import kotlinx.android.synthetic.main.layout_variant_list_item.view.*

class VarianteAdapter(
    private val requestManager: RequestManager,
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProductVariants>() {

        override fun areItemsTheSame(oldItem: ProductVariants, newItem: ProductVariants): Boolean {
            return oldItem.productVariantAttributeValuesProductVariant == newItem.productVariantAttributeValuesProductVariant
        }

        override fun areContentsTheSame(oldItem: ProductVariants, newItem: ProductVariants): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
        AsyncListDiffer(
            VarianteRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )
    internal inner class VarianteRecyclerChangeCallback(
        private val adapter: VarianteAdapter
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
    ): VarianteHolder {

        return VarianteHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_variant_list_item, parent, false),
            interaction = interaction,
            requestManager=requestManager
        )

    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VarianteHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(productVariantsList: List<ProductVariants>?){
        val newList = productVariantsList?.toMutableList()
        differ.submitList(newList)
    }

    class VarianteHolder(
        itemView: View,
        val requestManager: RequestManager,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {


        @SuppressLint("SetTextI18n")
        fun bind(item: ProductVariants) = with(itemView) {

            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition,item, ActionConstants.SELECTED)
            }

            itemView.delete_variant.setOnClickListener {
                interaction?.onItemSelected(adapterPosition,item, ActionConstants.DELETE)
            }

            if(item.imageUri != null){
                requestManager
                    .load(item.imageUri)
                    .into(variant_image_item)
            }

            itemView.variant_price.text=item.price.toString()

            val quantity=item.unit.toString()
            itemView.variant_units.text="$quantity in stock"

            var optionValues=""
            item.productVariantAttributeValuesProductVariant.map {
                optionValues= optionValues+" / "+it.attributeValue.value
            }
            itemView.variant_option_values.text=optionValues.drop(2)
        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: ProductVariants, action:Int)
    }
}