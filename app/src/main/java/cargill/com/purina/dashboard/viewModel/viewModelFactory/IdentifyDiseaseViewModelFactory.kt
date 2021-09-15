package cargill.com.purina.dashboard.viewModel.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cargill.com.purina.dashboard.Repository.IdentifyDiseaseRepository
import cargill.com.purina.dashboard.viewModel.IdentifyDiseaseViewModel

class IdentifyDiseaseViewModelFactory(private val repository: IdentifyDiseaseRepository) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(IdentifyDiseaseViewModel::class.java)) {
      return IdentifyDiseaseViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown view model class")
  }
}