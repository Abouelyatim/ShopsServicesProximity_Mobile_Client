package com.smartcity.client.ui.main.custom_category.createProduct

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.smartcity.client.R
import kotlinx.android.synthetic.main.layout_product_image_list_item.view.*

class ProductImageAdapter(
    private val requestManager: RequestManager,
    private val interaction: Interaction? = null,
    private val interactionAdd: InteractionAdd? =null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    private val VIEW_TYPE_DEFAULT = 0
    private val VIEW_TYPE_IMAGE = 1

   //  var newList:List<Uri>?=null
   val newListLiveData: MutableLiveData<List<Uri>?> = MutableLiveData()

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Uri>() {

        override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
        AsyncListDiffer(
            ProductImageRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    internal inner class ProductImageRecyclerChangeCallback(
        private val adapter: ProductImageAdapter
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
    override fun getItemViewType(position: Int): Int {
        return if (position == differ.currentList.size) VIEW_TYPE_DEFAULT else VIEW_TYPE_IMAGE
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder  {

        when(viewType){
            VIEW_TYPE_IMAGE->{
                return ProductImageHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_product_image_list_item, parent, false),
                    interaction = interaction,
                    requestManager = requestManager
                )
            }

            VIEW_TYPE_DEFAULT->{
                return AddProductImageHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_product_add_image_list_item, parent, false),
                    interactionAdd = interactionAdd
                )
            }
            else->{
                return ProductImageHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.layout_product_image_list_item, parent, false),
                    interaction = interaction,
                    requestManager = requestManager
                )
            }
            }

        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ProductImageHolder -> {
                holder.bind(differ.currentList.get(position))
            }

            is AddProductImageHolder ->{
                holder.bind()
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size+1
    }

    fun currentListSize():Int{
        return differ.currentList.size
    }

    fun moveItem(from: Int, to: Int) {
        if (from!=differ.currentList.size){
            val fromEmoji = differ.currentList[from]
            val list=differ.currentList.toMutableList()
            list.removeAt(from)
            list.add(to , fromEmoji)
            newListLiveData.value=list
            differ.submitList(list)
        }
    }



    fun getListLivedata(): LiveData<List<Uri>?>{
        return newListLiveData

    }


    fun submitList(productImageList: List<Uri>?){
        val newList = productImageList?.toMutableList()
        differ.submitList(newList)
    }

    class ProductImageHolder(
        itemView: View,
        val requestManager: RequestManager,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {


        @SuppressLint("SetTextI18n")
        fun bind(item: Uri) = with(itemView) {

            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition,item)
            }
            requestManager
                .load(item)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(itemView.product_image)



        }
    }

    class AddProductImageHolder(
        itemView: View,
        private val interactionAdd: InteractionAdd?
    ) : RecyclerView.ViewHolder(itemView) {


        fun bind() = with(itemView) {

            itemView.setOnClickListener {
                interactionAdd?.onItemAddSelected()
            }

        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: Uri)
    }

    interface InteractionAdd{
        fun onItemAddSelected()
    }

    fun getItemTouchHelper():ItemTouchHelper?{
        return itemTouchHelperr
    }

    fun clearItemTouchHelper(){
        itemTouchHelperr=null
    }

    private  var itemTouchHelperr:ItemTouchHelper?
    init {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or
                        ItemTouchHelper.DOWN or
                        ItemTouchHelper.START or
                        ItemTouchHelper.END, 0) {

                override fun onMove(recyclerView: RecyclerView,
                                    viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {

                    val adapter = recyclerView.adapter as ProductImageAdapter
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition
                    if(adapter.currentListSize()!=from &&  adapter.currentListSize()!=to){
                        // 2. Update the backing model. Custom implementation in
                        //    MainRecyclerViewAdapter. You need to implement
                        //    reordering of the backing model inside the method.
                        adapter.moveItem(from, to)

                        // 3. Tell adapter to render the model update.
                        adapter.notifyItemMoved(from, to)

                        return true
                    }
                    return false
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder,
                                      direction: Int) {
                    // 4. Code block for horizontal swipe.
                    //    ItemTouchHelper handles horizontal swipe as well, but
                    //    it is not relevant with reordering. Ignoring here.
                }

                override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                    super.onSelectedChanged(viewHolder, actionState)

                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = 0.5f
                    }
                }

                override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                    super.clearView(recyclerView, viewHolder)

                    viewHolder?.itemView?.alpha = 1.0f
                }
            }
        itemTouchHelperr=ItemTouchHelper(simpleItemTouchCallback)
    }

}