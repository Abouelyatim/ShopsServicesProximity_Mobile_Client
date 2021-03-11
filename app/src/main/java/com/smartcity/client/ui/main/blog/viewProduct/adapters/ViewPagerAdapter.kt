package com.smartcity.client.ui.main.blog.viewProduct.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.smartcity.client.util.Constants

class ViewPagerAdapter internal constructor(
    requestManager: RequestManager
) : PagerAdapter() {

    private val requestManager: RequestManager

    private lateinit var imageUrls: List<String>

    fun setImageUrls(imageUrls: List<String>){
        this.imageUrls=imageUrls
    }
    override fun getCount(): Int {
        return imageUrls.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(container.context)
        imageView.adjustViewBounds=true
        imageView.scaleType=ImageView.ScaleType.FIT_XY

        val image= Constants.PRODUCT_IMAGE_URL +imageUrls[position]
        requestManager
            .load(image)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)

        container.addView(imageView)
        return imageView
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {
        container.removeView(`object` as View)
    }

    init {
        this.requestManager=requestManager
    }
}