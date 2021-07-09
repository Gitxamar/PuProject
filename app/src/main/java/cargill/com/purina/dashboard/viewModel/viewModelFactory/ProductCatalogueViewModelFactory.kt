package cargill.com.purina.dashboard.viewModel.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cargill.com.purina.dashboard.Repository.ProductCatalogueRepository
import cargill.com.purina.dashboard.viewModel.ProductCatalogueViewModel
import java.lang.IllegalArgumentException

class ProductCatalogueViewModelFactory(private val repository: ProductCatalogueRepository):  ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ProductCatalogueViewModel::class.java)){
            return ProductCatalogueViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }
}