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

    private val filterResult= MutableLiveData<FilterOptions>()
    val filterData: LiveData<FilterOptions>
        get() = filterResult

    val purinaApi = PurinaService.getDevInstance()
    init {
        myPreference = AppPreference(ctx)
    }
    fun getfilterData(lanCode:String , species_code:String): Job =viewModelScope.launch {
        val response = purinaApi.getFilterOptions(mapOf(Constants.LANGUAGE to lanCode, Constants.SPECIES_ID to species_code))
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