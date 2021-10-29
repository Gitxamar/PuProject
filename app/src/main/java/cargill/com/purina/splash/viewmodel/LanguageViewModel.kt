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
        items.add(Country(1,R.drawable.ic_russian, "Россия", "language/ru",0))
        items.add(Country(2,R.drawable.ic_english, ctx.getString(R.string.language_english), "language/en",0))
        items.add(Country(3,R.drawable.ic_italian, "Italiana", "it",-1))
        items.add(Country(4,R.drawable.ic_hungarian, "Magyar", "hu",-1))
        items.add(Country(5,R.drawable.ic_polish, "Polskie","pl" ,-1))
        items.add(Country(6,R.drawable.ic_romana, "Română","language/ro" ,-1))
        return items
    }


    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        
    }
}