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

    suspend fun insert(counties: ArrayList<Country>){
        dao.insertCountry(counties)
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
        items.add(Country(1, R.drawable.ic_english, langs.data[0].language_name, langs.data[0].language_code,0))
        items.add(Country(2, R.drawable.ic_russian, langs.data[1].language_name, langs.data[1].language_code,0))
        items.add(Country(3, R.drawable.ic_hungarian, langs.data[2].language_name, langs.data[2].language_code,-1))
        items.add(Country(4, R.drawable.ic_polish, langs.data[3].language_name, langs.data[3].language_code,-1))
        items.add(Country(5, R.drawable.ic_italian, langs.data[4].language_name,langs.data[4].language_code ,-1))
        items.add(Country(6, R.drawable.ic_romana, langs.data[5].language_name,langs.data[5].language_code ,-1))
        return items
    }

    private fun onError(message: String) {
        statusMessage.value = Event(message)
    }
}