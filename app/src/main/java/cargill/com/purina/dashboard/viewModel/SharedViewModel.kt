package cargill.com.purina.dashboard.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cargill.com.purina.dashboard.Model.Home.Animal

class SharedViewModel : ViewModel() {
    private val animalSelected = MutableLiveData<Animal>()
    val selectedItem: LiveData<Animal> get() = animalSelected

    fun animalSelected(animal: Animal){
        animalSelected.value = animal
    }
}