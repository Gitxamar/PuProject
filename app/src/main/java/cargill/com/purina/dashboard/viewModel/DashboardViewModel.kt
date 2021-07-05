package cargill.com.purina.dashboard.viewModel

import androidx.databinding.Observable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cargill.com.purina.dashboard.Model.Home.Animal
import cargill.com.purina.dashboard.Repository.DashboardRepository
import cargill.com.purina.splash.Model.Country
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: DashboardRepository) :ViewModel(), Observable{
    val animals = repository.animals
    val selectedAnimal = repository.selectedAnimal

    fun getData(languageCode:String): Job =viewModelScope.launch {
        repository.getdata(languageCode)
    }
    fun updateUserSelection(animalName: String, newAnimal: Animal):Job = viewModelScope.launch {
        repository.updateAnimalSelected(animalName, newAnimal)
        //statusMessage.value = Event("User AnimalSelected updated")
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}