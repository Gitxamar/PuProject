package cargill.com.purina.splash.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.liveData
import cargill.com.purina.Database.Event
import cargill.com.purina.splash.Model.Country
import cargill.com.purina.Database.PurinaDAO
import cargill.com.purina.R
import cargill.com.purina.Service.PurinaService
import cargill.com.purina.splash.Model.Languages
import retrofit2.Response

class LanguageRepository(private val dao:PurinaDAO) {
    val purinaApi = PurinaService.getDevInstance()
    val counties = dao.getCountries()
    val country = dao.getUserSelection()
    private val statusMessage= MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    suspend fun getData(){
        val response = purinaApi.getLanguages()
        if(response.isSuccessful()){
            insert(setData(response.body()!!))
        }else{
            onError("Error : ${response.message()}")
        }
    }

    fun getLanguageCount(): Int{
        return dao.getCountriesCount()
    }

    suspend fun insert(counties: ArrayList<Country>){
        dao.insertCountry(counties)
    }

    fun insertLocaly(counties: ArrayList<Country>){
        dao.insertCountryLocally(counties)
    }

    suspend fun update(code: String, newCountry: Country){
        dao.updateUserSelection(newCountry)
        if(!code.isEmpty()){
            updateOldLanguge(code)
        }
    }
    suspend fun updateOldLanguge(code:String){
        dao.updateOldUserSelection(code)
    }
    private fun setData(langs: Languages): ArrayList<Country>{
        var items:ArrayList<Country> = ArrayList()
        var i = 1;
        for(itemTemp in langs.data){
            items.add(Country(i, itemTemp.languageImageUrl, itemTemp.language_name, itemTemp.language_code, 0,itemTemp.modeActive))
            i++
        }
        return items
    }

    private fun onError(message: String) {
        statusMessage.value = Event(message)
    }
}