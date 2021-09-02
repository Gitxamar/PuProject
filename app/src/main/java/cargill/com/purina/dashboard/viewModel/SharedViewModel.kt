package cargill.com.purina.dashboard.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cargill.com.purina.Database.Event
import cargill.com.purina.dashboard.Model.Home.Animal
import cargill.com.purina.dashboard.Model.LocateStore.LocationDetails

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

    //Used to get the Location Loaded from GPS/Last Location
    private val _locationLoaded = MutableLiveData<LocationDetails>()
    val locationItem: LiveData<LocationDetails> get() = _locationLoaded
    fun locationLoaded(locationDetails: LocationDetails){
        _locationLoaded.value = locationDetails
    }
}