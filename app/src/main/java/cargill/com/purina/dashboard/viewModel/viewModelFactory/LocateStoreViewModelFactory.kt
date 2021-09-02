package cargill.com.purina.dashboard.viewModel.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cargill.com.purina.dashboard.Repository.LocateStoreRepository
import cargill.com.purina.dashboard.viewModel.LocateStoreViewModel

class LocateStoreViewModelFactory(private val repository: LocateStoreRepository):  ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if(modelClass.isAssignableFrom(LocateStoreViewModel::class.java)){
      return LocateStoreViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown view model class")
  }
}