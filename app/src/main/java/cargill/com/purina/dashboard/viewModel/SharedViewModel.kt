package cargill.com.purina.dashboard.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cargill.com.purina.Database.Event
import cargill.com.purina.dashboard.Model.Home.Animal

class SharedViewModel : ViewModel() {
    //Used to get the user selected animal
    private val animalSelected = MutableLiveData<Animal>()
    val selectedItem: LiveData<Animal> get() = animalSelected
    fun animalSelected(animal: Animal){
        animalSelected.value = animal
    }


    //Used for only navigation screen
    //After navigation need to reset the value
    private val _navigateToDetails = MutableLiveData<Event<String>>()
    val navigateToDetails : LiveData<Event<String>>
        get() = _navigateToDetails
    fun navigate(navigate:String){
        _navigateToDetails.value = Event(navigate)
    }
}