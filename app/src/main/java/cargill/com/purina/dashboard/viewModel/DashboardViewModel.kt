package cargill.com.purina.dashboard.viewModel

import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cargill.com.purina.dashboard.Model.Articles.Article
import cargill.com.purina.dashboard.Model.Campaign.Campaign
import cargill.com.purina.dashboard.Model.Campaign.Campaigns
import cargill.com.purina.dashboard.Model.Home.Animal
import cargill.com.purina.dashboard.Model.Home.FAQs
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetail
import cargill.com.purina.dashboard.Repository.DashboardRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: DashboardRepository) :ViewModel(), Observable{
    val animals = repository.animals
    val selectedAnimal = repository.selectedAnimal

    val faqResponse = repository.faqResponseRemote

    var campaignsData = MutableLiveData<Campaigns>()
    var campaignsOfflineData = MutableLiveData<List<Campaign>>()
    var articles = MutableLiveData<List<Article>>()
    val pathWithToken = repository.pathWithToken


    fun getData(languageCode:String): Job =viewModelScope.launch {
        repository.getdata(languageCode)
    }
    fun updateUserSelection(animalName: String, newAnimal: Animal):Job = viewModelScope.launch {
        repository.updateAnimalSelected(animalName, newAnimal)
    }

    fun getFaqViewModel(queryFilter:Map<String, String>):Job = viewModelScope.launch {
        repository.getFaqRepository(queryFilter)
    }

    fun getOfflineFAQList(): List<FAQs> {
        return repository.getFAQListRepository()
    }


    fun campaignCacheData(query: Map<String, String>): Job =viewModelScope.launch {
        repository.getProductCampaignData(query)
    }
    fun campaignData(): LiveData<Campaigns>{
        campaignsData = repository.campaignData
        return campaignsData
    }
    fun getCampaignData(code: String): Job =viewModelScope.launch {
        repository.getCampignData(code)
    }

    fun offlineCampaignData():LiveData<List<Campaign>>{
        campaignsOfflineData = repository.campaignOfflineData
        return campaignsOfflineData
    }

    fun getArticles(query: Map<String, String>): Job =viewModelScope.launch {
        repository.getArticle(query)
    }

    fun articles():LiveData<List<Article>>{
        articles = repository.articles
        return articles
    }
    fun getProductPDF(path:String): Job =viewModelScope.launch {
        repository.getProductPDF(path)
    }


    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}