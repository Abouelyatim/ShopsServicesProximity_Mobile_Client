package com.smartcity.client.ui.main.custom_category.state

import android.net.Uri
import android.os.Parcelable
import com.smartcity.client.models.CustomCategory
import com.smartcity.client.models.product.Attribute
import com.smartcity.client.models.product.Product
import com.smartcity.client.models.product.ProductVariants
import kotlinx.android.parcel.Parcelize

const val CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY = "com.smartcity.provider.ui.main.custom_category.state.CustomCategoryViewState"

@Parcelize
class CustomCategoryViewState(
    var customCategoryFields:CustomCategoryFields=CustomCategoryFields(),
    var selectedCustomCategory:SelectedCustomCategory=SelectedCustomCategory(),

    var newOption:NewOption=NewOption(),

    var selectedProductVariant:SelectedProductVariant=SelectedProductVariant(),

    var productFields:ProductFields=ProductFields(),

    var productList:ProductList=ProductList(),

    var viewProductFields:ViewProductFields=ViewProductFields(),

    var choisesMap:ChoisesMap=ChoisesMap(),

    var copyImage:CopyImage=CopyImage()

) : Parcelable {
    @Parcelize
    data class CustomCategoryFields(
        var customCategoryList: List<CustomCategory> = ArrayList<CustomCategory>(),
        var layoutManagerState: Parcelable? = null
    ) : Parcelable


    @Parcelize
    data class SelectedCustomCategory(
        var customCategory: CustomCategory? =null
    ) : Parcelable

    @Parcelize
    data class NewOption(
        var attribute: Attribute? =null
    ) : Parcelable


    @Parcelize
    data class SelectedProductVariant(
        var variante:ProductVariants ? =null
    ) : Parcelable

    @Parcelize
    data class ProductList(
        var products:List<Product> = ArrayList()
    ) : Parcelable

    @Parcelize
    data class ViewProductFields(
        var product: Product? = null
    ) : Parcelable

    @Parcelize
    data class CopyImage(
        var copyImage :Uri?=null
    ) : Parcelable

    @Parcelize
    data class ProductFields(
        var description: String = "",
        var name: String = "",
        var price: String= "",
        var quantity:String= "",
        var productImageList: MutableList<Uri> = ArrayList<Uri>(),
        var productVariantList: MutableList<ProductVariants> = ArrayList<ProductVariants>(),
        var attributeList: HashSet <Attribute> = LinkedHashSet ()
    ) : Parcelable{
        class CreateProductError {

            companion object{

                fun mustFillAllFields(): String{
                    return "You can't create product without fill all information."
                }

                fun none():String{
                    return "None"
                }

            }
        }


        fun isValidForCreation(): String{

            if(description.isEmpty()
                || name.isEmpty()
                || productVariantList.isEmpty()
                || productVariantList.map { it.price==0.0 }.all{!it}.not()
                || productVariantList.map { it.unit==0 }.all{!it}.not()){

                return CreateProductError.mustFillAllFields()
            }
            return CreateProductError.none()
        }

    }

    @Parcelize
    data class ChoisesMap(
        var choises:MutableMap<String, String> = mutableMapOf()
    ) : Parcelable

}