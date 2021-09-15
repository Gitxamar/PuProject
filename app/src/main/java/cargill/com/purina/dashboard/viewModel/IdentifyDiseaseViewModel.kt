package cargill.com.purina.dashboard.viewModel

import androidx.databinding.Observable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cargill.com.purina.dashboard.Model.IdentifyDisease.Disease
import cargill.com.purina.dashboard.Model.IdentifyDisease.DiseasesDetail
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetail
import cargill.com.purina.dashboard.Repository.IdentifyDiseaseRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class IdentifyDiseaseViewModel(private val repository: IdentifyDiseaseRepository) : ViewModel(),
  Observable {

  val remoteDiseaseDetail = repository.diseaseDetailsRemote
  val remoteDiseaseList = repository.diseaseListRemote
  val digitalVetList = repository.digitalVetRemote
  val digitalVetDetailsList = repository.digitalVetDetailsRemote
  val msg = repository.message

  //Get Remote Disease List
  fun getRemoteData(queryFilter: Map<String, String>): Job = viewModelScope.launch {
    repository.getRemoteData(queryFilter)
  }

  //Get Remote Disease Details List
  fun getDiseaseDetail(diseaseId: Int): Job = viewModelScope.launch {
    repository.getDiseaseDetail(diseaseId)
  }

  //Get Remote Disease Details List
  fun getDigitalVetDetailList(queryFilter: Map<String, String>): Job = viewModelScope.launch {
    repository.getRemoteDigitalVetDetailsData(queryFilter)
  }

  //Set Offline Diseases
  fun setOfflineDisease(disease: Disease) {
    return repository.setDiseaseLocal(disease)
  }

  //Get offline Disease List
  fun getOfflineDiseaseList(): List<Disease> {
    return repository.getLocalDiseaseList()
  }

  //Get offline Disease List
  fun getOfflineDiseaseSearch(searchTxt: String): List<Disease> {
    return repository.getLocalDiseaseSearchList(searchTxt)
  }

  //Get Offline Disease Details
  fun getOfflineDiseaseDetail(diseaseId: Int): DiseasesDetail{
    return repository.getDiseaseDetailLocal(diseaseId)
  }

  //Get Remote Digital Vet List
  fun getDigitalVet(queryFilter: Map<String, String>): Job = viewModelScope.launch {
    repository.getRemoteDigitalVetData(queryFilter)
  }

  override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

  }

  override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

  }

}