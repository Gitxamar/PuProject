package cargill.com.purina.dashboard.viewModel.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cargill.com.purina.dashboard.Repository.FeedProgramRepository
import cargill.com.purina.dashboard.viewModel.FeedProgramViewModel
import java.lang.IllegalArgumentException

class FeedProgramViewModelFactory(private val repository: FeedProgramRepository):  ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if(modelClass.isAssignableFrom(FeedProgramViewModel::class.java)){
      return FeedProgramViewModel(repository) as T
    }
    throw IllegalArgumentException("Unknown view model class")
  }
}