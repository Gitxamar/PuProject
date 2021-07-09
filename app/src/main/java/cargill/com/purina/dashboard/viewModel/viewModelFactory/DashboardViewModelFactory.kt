package cargill.com.purina.dashboard.viewModel.viewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cargill.com.purina.dashboard.Repository.DashboardRepository
import cargill.com.purina.dashboard.viewModel.DashboardViewModel
import java.lang.IllegalArgumentException

class DashboardViewModelFactory(private val repository: DashboardRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DashboardViewModel::class.java)){
            return DashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown view model class")
    }
}