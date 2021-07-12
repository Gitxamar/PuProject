package cargill.com.purina.dashboard.viewModel

import androidx.databinding.Observable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cargill.com.purina.dashboard.Repository.ProductCatalogueRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProductCatalogueViewModel(private val repository: ProductCatalogueRepository): ViewModel(),
    Observable {
    val remotedata = repository.productsRemoteCatalogue
    val offlinedata = repository.productsOfflineCatalogue

    val msg = repository.message

    fun getRemoteData(queryFilter:Map<String, String>): Job =viewModelScope.launch {
        repository.getRemotedata(queryFilter)
    }
    fun getOfflineData(): Job =viewModelScope.launch {
        repository.getCacheData()
    }
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        TODO("Not yet implemented")
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        TODO("Not yet implemented")
    }
}