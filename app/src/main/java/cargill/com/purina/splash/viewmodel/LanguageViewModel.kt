package cargill.com.purina.splash.viewmodel

import android.content.Context
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cargill.com.purina.R
import cargill.com.purina.splash.Repository.LanguageRepository
import cargill.com.purina.splash.Model.Country
import cargill.com.purina.Database.Event
import cargill.com.purina.splash.Model.Languages
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LanguageViewModel(private val repository: LanguageRepository, val ctx: Context):ViewModel(),Observable {

    val countries = repository.counties
    val selectedLanguage = repository.country

    private val statusMessage= MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
    get() = statusMessage

    fun getLanguages(): Job =viewModelScope.launch {
        repository.getData()
        statusMessage.value = Event("Supported countries inserted to database")
    }
    fun saveLanguages():Job = viewModelScope.launch {
        repository.insert(setData())
        //statusMessage.value = Event("Supported countries inserted to database")
    }

    fun updateUserSelection(country: Country):Job = viewModelScope.launch {
        repository.update(country)
        //statusMessage.value = Event("User Country updated")
    }
    private fun setData(): ArrayList<Country>{
        var items:ArrayList<Country> = ArrayList()
        items.add(Country(1,R.drawable.ic_russian, "Россия", "ru",0))
        items.add(Country(2,R.drawable.ic_english, ctx.getString(R.string.language_english), "en",1))
        items.add(Country(3,R.drawable.ic_italian, "Italiana", "it",0))
        items.add(Country(4,R.drawable.ic_hungarian, "Magyar", "hu",0))
        items.add(Country(5,R.drawable.ic_polish, "Polskie","pl" ,0))
        items.add(Country(6,R.drawable.ic_romana, "Română","ro" ,0))
        return items
    }


    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        
    }
}