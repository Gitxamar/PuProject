package cargill.com.purina.splash.Repository

import cargill.com.purina.splash.Model.Country
import cargill.com.purina.database.PurinaDAO

class LanguageRepository(private val dao:PurinaDAO) {
    val counties = dao.getCountries()
    val country = dao.getUserSelection()

    suspend fun insert(counties: ArrayList<Country>){
        dao.insertCountry(counties)
    }

    suspend fun update(country: Country){
        dao.updateUserSelection(country)
    }

}