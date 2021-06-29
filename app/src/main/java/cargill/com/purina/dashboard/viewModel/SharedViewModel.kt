package cargill.com.purina.dashboard.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cargill.com.purina.dashboard.Model.Home.Animal

class SharedViewModel : ViewModel() {
    val animalSelected = MutableLiveData<Animal>()

    fun animalSelected(animal: Animal){
        animalSelected.value = animal
    }
}