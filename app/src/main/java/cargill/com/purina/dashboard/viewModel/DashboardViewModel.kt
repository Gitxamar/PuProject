package cargill.com.purina.dashboard.viewModel

import androidx.databinding.Observable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cargill.com.purina.dashboard.Repository.DashboardRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: DashboardRepository) :ViewModel(), Observable{
    val animals = repository.animals

    fun getData(languageCode:String): Job =viewModelScope.launch {
        repository.getdata(languageCode)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}