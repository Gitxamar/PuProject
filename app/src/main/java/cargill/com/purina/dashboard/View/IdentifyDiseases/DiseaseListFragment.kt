package cargill.com.purina.dashboard.View.IdentifyDiseases

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cargill.com.purina.Database.PurinaDataBase
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.Service.PurinaService
import cargill.com.purina.dashboard.Model.IdentifyDisease.Disease
import cargill.com.purina.dashboard.Model.IdentifyDisease.DiseasesDetail
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetail
import cargill.com.purina.dashboard.Repository.IdentifyDiseaseRepository
import cargill.com.purina.dashboard.View.LocateStore.LocateManager
import cargill.com.purina.dashboard.viewModel.IdentifyDiseaseViewModel
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.IdentifyDiseaseViewModelFactory
import cargill.com.purina.databinding.FragmentDiseasesListBinding
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import com.google.android.material.snackbar.Snackbar

class DiseaseListFragment : Fragment() {
  lateinit var myPreference: AppPreference
  lateinit var binding: FragmentDiseasesListBinding
  private lateinit var identifyDiseaseViewModel: IdentifyDiseaseViewModel
  var sharedViewmodel: SharedViewModel? = null
  private lateinit var adapter: DiseaseListAdapter
  private var PAGENUMBER:Int = 1
  private var searchQuery:String = ""
  private var category_id:String = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentDiseasesListBinding.inflate(inflater)
    return binding.root
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    myPreference = AppPreference(context)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initRecyclerView()

    val dao = PurinaDataBase.invoke(requireActivity().applicationContext).dao
    val repository = IdentifyDiseaseRepository(dao, PurinaService.getDevInstance(), requireActivity())
    val factory = IdentifyDiseaseViewModelFactory(repository)

    identifyDiseaseViewModel = ViewModelProvider(this, factory).get(IdentifyDiseaseViewModel::class.java)
    binding.identifyDiseaseViewModel = identifyDiseaseViewModel
    binding.lifecycleOwner = this

    /*binding.searchFilterView.setHintTextColor(resources.getColor(R.color.white))
    binding.searchFilterView.setTextColor(resources.getColor(R.color.white))*/
    binding.searchFilterView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
      override fun onQueryTextSubmit(query: String?): Boolean {
        if (Network.isAvailable(requireActivity())) {
          identifyDiseaseViewModel!!.getRemoteData(mapOf(
            Constants.SEARCH_TEXT to query.toString(),
            Constants.LANGUAGE to myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString(),
            Constants.SPECIES_ID to myPreference.getStringValue(Constants.USER_ANIMAL_CODE).toString(),
            Constants.PAGE to PAGENUMBER.toString(),
            Constants.PER_PAGE to 100.toString()))
        }else{
          var diseaseList: List<Disease> = identifyDiseaseViewModel!!.getOfflineDiseaseSearch(query.toString())
          if (!diseaseList.isEmpty() || diseaseList.size > 0) {
            binding.rvDiseaseList.hideShimmer()
            adapter.setList(ArrayList(diseaseList))
            adapter.notifyDataSetChanged()
          }else{
            displayNodata()
          }
        }
        return true
      }
      override fun onQueryTextChange(newText: String?): Boolean {
        //adapter.filter.filter(newText)
        return true
      }
    })

    binding.back.setOnClickListener {
      findNavController().navigate(R.id.action_fragment_disease_list_back)
    }

    identifyDiseaseViewModel?.remoteDiseaseList?.observe(binding.lifecycleOwner!!, Observer {
      if (it.isSuccessful) {
        Log.i("data commingng", it.body().toString())
        if (it.body()!!.disease.size != 0) {
          displayData(it.body()!!.disease)
        } else {
          displayNodata()
        }
      } else {
        displayNodata()
      }
    })

    identifyDiseaseViewModel!!.msg.observe(binding.lifecycleOwner!!, Observer {
      Snackbar.make(binding!!.root, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()
      displayNodata()
    })

    getDiseaseData()
  }

  private fun displayData(data: List<Disease>) {
    binding.rvDiseaseList.hideShimmer()
    adapter.setList(ArrayList(data))
    adapter.notifyDataSetChanged()
  }

  private fun displayNodata() {
    binding.let { Snackbar.make(it.root, R.string.no_data_found, Snackbar.LENGTH_LONG).show() }
  }

  private fun getDiseaseData() {
    if (Network.isAvailable(requireActivity())) {
      identifyDiseaseViewModel!!.getRemoteData(mapOf(
        Constants.LANGUAGE to myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString(),
        Constants.SPECIES_ID to myPreference.getStringValue(Constants.USER_ANIMAL_CODE).toString(),
        Constants.PAGE to PAGENUMBER.toString(),
        Constants.PER_PAGE to 100.toString()))
    } else {
      binding.searchFilterView.visibility = View.GONE
      displayOfflineData()
    }
  }

  private fun displayOfflineData() {
    var diseaseList: List<Disease> = identifyDiseaseViewModel!!.getOfflineDiseaseList()
    if (!diseaseList.isEmpty() || diseaseList.size > 0) {
      binding.rvDiseaseList.hideShimmer()
      adapter.setList(ArrayList(diseaseList))
      adapter.notifyDataSetChanged()
    }
  }

  private fun initRecyclerView() {
    binding.rvDiseaseList.layoutManager =
      LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
    adapter = DiseaseListAdapter { data: Disease -> onItemClick(data) }
    binding.rvDiseaseList.adapter = adapter
    binding.rvDiseaseList.showShimmer()
  }

  private fun onItemClick(dataObj: Disease) {
    //navigate to DiseaseDetails
    identifyDiseaseViewModel!!.setOfflineDisease(dataObj)
    if (dataObj != null) {
      val bundle = bundleOf(Constants.DISEASE_ID to dataObj.diseaseId,
        Constants.IS_DISEASESS to true )
      findNavController().navigate(R.id.action_fragment_disease_detail, bundle)
    }
  }

}