package cargill.com.purina.dashboard.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cargill.com.purina.Database.Event
import cargill.com.purina.Service.PurinaService
import cargill.com.purina.dashboard.Model.FilterOptions.FilterOptions
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CatalogueFilterViewModel(private val ctx:Context): ViewModel(), Observable {
    lateinit var myPreference: AppPreference
    private var languageCode: String = ""
    private var species_code: String = ""

    private val filterResult= MutableLiveData<FilterOptions>()
    val filterData: LiveData<FilterOptions>
        get() = filterResult

    val purinaApi = PurinaService.getDevInstance()
    init {
        myPreference = AppPreference(ctx)
        languageCode = myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString()
        species_code = myPreference.getStringValue(Constants.USER_ANIMAL_CODE).toString()
    }
    fun getData(): Job =viewModelScope.launch {
        val response = purinaApi.getFilterOptions(mapOf("lang" to languageCode))
        if(response.isSuccessful){
            filterResult.value = response.body()
        }else{

        }
    }









    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        TODO("Not yet implemented")
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        TODO("Not yet implemented")
    }
}