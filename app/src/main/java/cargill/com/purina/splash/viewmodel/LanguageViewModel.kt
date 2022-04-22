package cargill.com.purina.splash.viewmodel

import android.content.Context
import androidx.databinding.Observable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cargill.com.purina.R
import cargill.com.purina.splash.Repository.LanguageRepository
import cargill.com.purina.splash.Model.Country
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LanguageViewModel(private val repository: LanguageRepository, val ctx: Context):ViewModel(),Observable {

    val countries = repository.counties
    val selectedLanguage = repository.country
    val message = repository.message

    fun getLanguages(): Job =viewModelScope.launch {
        repository.getData()
        //statusMessage.value = Event("Supported countries inserted to database")
    }

    fun getLanguagesCount(): Int {
        return repository.getLanguageCount()
    }

    fun setLanguagesLocally(counties: ArrayList<Country>):Job = viewModelScope.launch{
        repository.insertLocaly(counties)
        //statusMessage.value = Event("Supported countries inserted to database")
    }

    fun updateUserSelection(code: String, newCountry: Country):Job = viewModelScope.launch {
        repository.update(code, newCountry)
        //statusMessage.value = Event("User Country updated")
    }
    private fun setData(): ArrayList<Country>{
        var items:ArrayList<Country> = ArrayList()
        return items
    }


    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        
    }
}