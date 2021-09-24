package cargill.com.purina.dashboard.viewModel

import androidx.databinding.Observable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cargill.com.purina.dashboard.Model.Home.Animal
import cargill.com.purina.dashboard.Model.Home.FAQs
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetail
import cargill.com.purina.dashboard.Repository.DashboardRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: DashboardRepository) :ViewModel(), Observable{
    val animals = repository.animals
    val selectedAnimal = repository.selectedAnimal
    val faqResponse = repository.faqResponseRemote

    fun getData(languageCode:String): Job =viewModelScope.launch {
        repository.getdata(languageCode)
    }
    fun updateUserSelection(animalName: String, newAnimal: Animal):Job = viewModelScope.launch {
        repository.updateAnimalSelected(animalName, newAnimal)
    }
    fun getFaqViewModel(queryFilter:Map<String, String>):Job = viewModelScope.launch {
        repository.getFaqRepository(queryFilter)
    }

    fun getOfflineFAQList(): List<FAQs> {
        return repository.getFAQListRepository()
    }


    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}