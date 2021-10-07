package cargill.com.purina.dashboard.View.IdentifyDiseases

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cargill.com.purina.Database.PurinaDataBase
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.Service.PurinaService
import cargill.com.purina.dashboard.Model.IdentifyDisease.Symptoms
import cargill.com.purina.dashboard.Model.LocateStore.StoreDetail
import cargill.com.purina.dashboard.Repository.IdentifyDiseaseRepository
import cargill.com.purina.dashboard.viewModel.IdentifyDiseaseViewModel
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.IdentifyDiseaseViewModelFactory
import cargill.com.purina.databinding.FragmentSymptomsListBinding
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import com.google.android.material.snackbar.Snackbar
import kotlin.reflect.KMutableProperty1


class SymptomsListFragment : Fragment() {

  lateinit var myPreference: AppPreference
  lateinit var binding: FragmentSymptomsListBinding
  private lateinit var identifyDiseaseViewModel: IdentifyDiseaseViewModel
  var sharedViewmodel: SharedViewModel? = null
  private lateinit var adapter: SymptomsListAdapter
  var symptomsList: MutableList<Symptoms> = mutableListOf()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentSymptomsListBinding.inflate(inflater)
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
    binding.symptomsListViewModel = identifyDiseaseViewModel
    binding.lifecycleOwner = this

    binding.back.setOnClickListener {
      findNavController().navigate(R.id.action_fragment_symptoms_list_back)
    }

    identifyDiseaseViewModel?.symptomsFilteredList?.observe(binding.lifecycleOwner!!, Observer {
      if (it.isSuccessful) {
        Log.i("data commingng", it.body().toString())
        if (it.body()!!.symptoms.size>0) {
          fun <T> updateWorkerData(property: KMutableProperty1<Symptoms, T>, value: T) {
            val workers = it.body()!!.symptoms
            workers.forEach {
              if (it.speciesid == 0) {
                property.set(it, value)
              }
            }
          }

          updateWorkerData(Symptoms::speciesid, myPreference.getStringValue(Constants.USER_ANIMAL_CODE)!!.toInt())
          displayData(it.body()!!.symptoms)

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
    getSymptomsData()
  }

  private fun getSymptomsData() {
    if (Network.isAvailable(requireActivity())) {
      identifyDiseaseViewModel!!.getFilteredSymptomsList(
        mapOf(
          Constants.LANGUAGE to myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString(),
          Constants.SPECIES_ID to myPreference.getStringValue(Constants.USER_ANIMAL_CODE).toString(),
        )
      )
    } else {

      var details: List<Symptoms> = identifyDiseaseViewModel!!.getOfflineSymptomsList()
      if (details.size > 0) {
        displayData(details)
      } else {
        displayNodata()
      }
    }
  }

  private fun initRecyclerView() {
    binding.rvSymptomsList.layoutManager = LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
    adapter = SymptomsListAdapter()
    binding.rvSymptomsList.adapter = adapter
    binding.rvSymptomsList.showShimmer()
  }

  private fun displayNodata() {
    //binding.let { Snackbar.make(it.root, R.string.no_data_found, Snackbar.LENGTH_LONG).show() }

    binding.rvSymptomsList.visibility = View.GONE
    binding.ivNoData.visibility = View.VISIBLE
  }

  private fun displayData(data: List<Symptoms>) {
    binding.rvSymptomsList.hideShimmer()
    adapter.setList(ArrayList(data))
    adapter.notifyDataSetChanged()
  }


}
