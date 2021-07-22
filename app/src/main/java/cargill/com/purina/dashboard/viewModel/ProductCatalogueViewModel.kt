package cargill.com.purina.dashboard.viewModel

import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cargill.com.purina.dashboard.Model.Home.Animal
import cargill.com.purina.dashboard.Model.ProductDetails.ProductDetail
import cargill.com.purina.dashboard.Model.Products.Product
import cargill.com.purina.dashboard.Repository.ProductCatalogueRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProductCatalogueViewModel(private val repository: ProductCatalogueRepository): ViewModel(),
    Observable {
    val remotedata = repository.productsRemoteCatalogue
    val remoteProductDetail = repository.productsDetailsRemote
    val msg = repository.message

    fun getRemoteData(queryFilter:Map<String, String>): Job =viewModelScope.launch {
        repository.getRemotedata(queryFilter)
    }
    fun getOfflineData(languageCode:String, animalCode:String):List<Product> {
        return repository.getChacheData(languageCode, animalCode)
    }

    fun getRemoteProductDetail(productId:Int): Job =viewModelScope.launch {
        repository.getRemoteProductDetail(productId)
    }

    fun getCacheProductDetail(productId: Int): ProductDetail{
        return repository.getProductDetails(productId)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        TODO("Not yet implemented")
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        TODO("Not yet implemented")
    }
}