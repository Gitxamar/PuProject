package cargill.com.purina.dashboard.Repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cargill.com.purina.Database.Event
import cargill.com.purina.Database.PurinaDAO
import cargill.com.purina.Service.PurinaApi
import cargill.com.purina.dashboard.Model.ProductDetails.DetailProduct
import cargill.com.purina.dashboard.Model.ProductDetails.ProductDetail
import cargill.com.purina.dashboard.Model.Products.Product
import cargill.com.purina.dashboard.Model.Products.ProductCatalogue
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import retrofit2.Response

class ProductCatalogueRepository(private val dao: PurinaDAO,private val purinaApi: PurinaApi, val ctx: Context) {
    lateinit var myPreference: AppPreference
    private var languageCode: String = ""
    private var animalCode: String = ""
    val productsRemoteCatalogue = MutableLiveData<Response<ProductCatalogue>>()
    val productsDetailsRemote = MutableLiveData<Response<DetailProduct>>()

    private val statusMessage= MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        myPreference = AppPreference(ctx)
        languageCode = myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString()
        animalCode = myPreference.getStringValue(Constants.USER_ANIMAL_CODE).toString()
    }
    suspend fun getRemotedata(queryFilter:Map<String, String>) {
        val data = purinaApi.getProducts(queryFilter)
        if(data.isSuccessful)
        {
            productsRemoteCatalogue.value = data
            insertData(data.body()!!.product)
        }else{
            statusMessage.value = Event("Failure")
        }
    }
    fun getChacheData(languageCode:String, animalCode:String): List<Product>{
        return dao.getProductsCatalogue(languageCode, animalCode)
    }
    suspend fun insertData(products:List<Product>){
        dao.insertProductsCatalogue(products)
    }
    suspend fun getRemoteProductDetail(productId: Int){
        val data  = purinaApi.getProductDetails(productId)
        if(data.isSuccessful){
            productsDetailsRemote.value = data
            data.body()?.ProductDetail?.let { insertProductDetail(it) }
        }else{
            statusMessage.value = Event("Failure")
        }
    }
    suspend fun insertProductDetail(detail: ProductDetail){
        dao.insertProductDetail(detail)
    }

    fun getProductDetails(productId:Int): ProductDetail{
        return dao.getProductDetail(productId)
    }
}