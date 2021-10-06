package cargill.com.purina.dashboard.viewModel

import androidx.databinding.Observable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetail
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetailsModel
import cargill.com.purina.dashboard.Repository.LocateStoreRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LocateStoreViewModel(private val repository: LocateStoreRepository) : ViewModel(),
  Observable {

  val remoteStoreDetail = repository.storeDetailsRemote
  val remoteStoreList = repository.storesListRemote
  val msg = repository.message


  fun getRemoteData(queryFilter:Map<String, String>): Job =viewModelScope.launch {
    repository.getSearchLocation(queryFilter)
  }

  fun getRemoteRadialSearchData(queryFilter:Map<String, String>): Job =viewModelScope.launch {
    repository.getRadialSearchLocation(queryFilter)
  }

  fun getRemoteStoreDetail(storeId: Int): Job = viewModelScope.launch {
    repository.getStoreDetail(storeId)
  }

  fun getOfflineStoreDetail(storeId: Int): StoreDetail {
    return repository.getLocalStoreDetail(storeId)
  }

  fun getOfflineStoreList(): List<StoreDetail> {
    return repository.getLocalStoreList()
  }

  override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    TODO("Not yet implemented")
  }

  override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
    TODO("Not yet implemented")
  }
}