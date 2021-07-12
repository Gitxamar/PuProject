package cargill.com.purina.dashboard.Repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import cargill.com.purina.Database.Event
import cargill.com.purina.Database.PurinaDAO
import cargill.com.purina.Service.PurinaApi
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

    private val statusMessage= MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        myPreference = AppPreference(ctx)
        languageCode = myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString()
        animalCode = myPreference.getStringValue(Constants.USER_ANIMAL_CODE).toString()
    }
    val productsOfflineCatalogue = dao.getProductsCatalogue(languageCode, animalCode)

    suspend fun getRemotedata(queryFilter:Map<String, String>) {
        val data = purinaApi.getProducts(queryFilter)
        if(data.isSuccessful)
        {
            productsRemoteCatalogue.value = data
            insertData(data.body()!!.product)
        }else{
            statusMessage.value = Event("Something went wrong")
        }
    }
    suspend fun insertData(products:List<Product>){
        dao.insertProductsCatalogue(products)
    }
    fun getCacheData(){
        val productCacheCatalogue = dao.getProductsCatalogue(languageCode, animalCode)
    }
}