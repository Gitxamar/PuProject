package cargill.com.purina.dashboard.viewModel.viewModelFactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cargill.com.purina.dashboard.viewModel.CatalogueFilterViewModel
import java.lang.IllegalArgumentException

class CatalogueFilterViewModelFactory(private val ctx:Context):  ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CatalogueFilterViewModel::class.java)){
            return CatalogueFilterViewModel(ctx) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }
}