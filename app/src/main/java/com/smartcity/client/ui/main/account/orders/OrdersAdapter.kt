package com.smartcity.client.ui.main.account.orders

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.models.Order
import com.smartcity.client.models.OrderType
import com.smartcity.client.util.Constants
import com.smartcity.client.util.DateUtils.Companion.convertStringToStringDate
import kotlinx.android.synthetic.main.layout_order_store_list_item.view.*

class OrdersAdapter (
    private val requestManager: RequestManager,
    private val interaction: Interaction? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Order>() {

        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    private val differ =
        AsyncListDiffer(
            OrdersRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )
    internal inner class OrdersRecyclerChangeCallback(
        private val adapter: OrdersAdapter
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
    ): OrdersHolder {
        return OrdersHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_order_store_list_item, parent, false),
            interaction = interaction,
            requestManager = requestManager
        )

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OrdersHolder -> {
                holder.bind(differ.currentList.get(position))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(attributeValueList: List<Order>?){
        val newList = attributeValueList?.toMutableList()
        differ.submitList(newList)
    }

    class OrdersHolder(
        itemView: View,
        private val interaction: Interaction?,
        val requestManager: RequestManager
    ) : RecyclerView.ViewHolder(itemView){


        @SuppressLint("SetTextI18n")
        fun bind(item: Order) = with(itemView) {
            itemView.order_date.text=convertStringToStringDate(item.createAt!!)

            /*val image= Constants.PRODUCT_IMAGE_URL +item.images.first().image
            requestManager
                .load(image)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(itemView.order_store_image)*/

            itemView.order_store_name.text=item.storeName
            itemView.order_store_address.text=item.storeAddress

            itemView.order_total.text=item.bill!!.total.toString()+ Constants.DINAR_ALGERIAN
            itemView.order_paid.text=item.bill!!.alreadyPaid.toString()+ Constants.DINAR_ALGERIAN
            itemView.order_rest.text=(item.bill!!.total-item.bill!!.alreadyPaid).toString()+ Constants.DINAR_ALGERIAN

            itemView.order_id.text=item.id.toString()

            when(item.orderType){
                OrderType.DELIVERY ->{
                    itemView.order_type.text="Delivery"
                    itemView.order_type_delivery.visibility=View.VISIBLE
                    order_store_address.visibility=View.GONE
                }

                OrderType.SELFPICKUP ->{
                    itemView.order_type.text="Self pickup"
                    itemView.order_type_self_pickup.visibility=View.VISIBLE
                }
            }

            itemView.address_container.setOnClickListener {
                interaction?.selectedOrder(item)
            }
        }
    }

    interface Interaction {
        fun selectedOrder(item: Order)
    }
}