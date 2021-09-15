package cargill.com.purina.dashboard.View.IdentifyDiseases

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import cargill.com.purina.dashboard.Model.IdentifyDisease.Diseases
import cargill.com.purina.dashboard.Repository.IdentifyDiseaseRepository
import cargill.com.purina.dashboard.viewModel.IdentifyDiseaseViewModel
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.IdentifyDiseaseViewModelFactory
import cargill.com.purina.databinding.FragmentDigitalVetDetailsBinding
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import com.google.android.material.snackbar.Snackbar

class DigitalVetDetailsFragment : Fragment() {
  lateinit var myPreference: AppPreference
  lateinit var binding: FragmentDigitalVetDetailsBinding
  var sharedViewmodel: SharedViewModel? = null
  private lateinit var identifyDiseaseViewModel: IdentifyDiseaseViewModel
  private var disease_id: String = ""
  private lateinit var adapter: DigitalVetDetailsListAdapter
  private var counter: Int = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentDigitalVetDetailsBinding.inflate(inflater)
    return binding.root
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    myPreference = AppPreference(context)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val dao = PurinaDataBase.invoke(requireActivity().applicationContext).dao
    val repository =
      IdentifyDiseaseRepository(dao, PurinaService.getDevInstance(), requireActivity())
    val factory = IdentifyDiseaseViewModelFactory(repository)

    identifyDiseaseViewModel =
      ViewModelProvider(this, factory).get(IdentifyDiseaseViewModel::class.java)
    binding.digitalVetViewModelList = identifyDiseaseViewModel
    binding.lifecycleOwner = this
    if (arguments != null) {
      if (requireArguments().containsKey(Constants.DISEASES_ID)) {
        disease_id = arguments?.getString(Constants.DISEASES_ID)!!
        countSelectionofInputs(disease_id)
      }
    } else {
      disease_id = Constants.DISEASES_IDS
      countSelectionofInputs(disease_id)
    }

    identifyDiseaseViewModel?.digitalVetDetailsList?.observe(binding.lifecycleOwner!!, Observer {
      if (it.isSuccessful) {
        Log.i("data commingng", it.body().toString())
        if (it.body()!!.diseases.size != 0) {
          binding.rvDigitalVet.hideShimmer()
          adapter.setList(it.body()!!.diseases, counter)
          adapter.notifyDataSetChanged()
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


    binding.back.setOnClickListener {
      findNavController().navigate(R.id.action_fragment_digital_vet_details_back)
    }

    initRecyclerView()
    getData()
  }

  private fun countSelectionofInputs(diseaseId: String) {
    val values = disease_id
    val lstValues: List<String> = values.split(",").map { it -> it.trim() }
    lstValues.forEach { it ->
      if (it != "0") {
        counter++
        Log.i("No Zero", "value=$it " + counter)
      }
    }
  }

  private fun initRecyclerView() {
    binding.rvDigitalVet.layoutManager =
      LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
    adapter = DigitalVetDetailsListAdapter { disease: Diseases -> onItemClick(disease) }
    binding.rvDigitalVet.adapter = adapter
    binding.rvDigitalVet.showShimmer()
  }

  private fun onItemClick(disease: Diseases) {
    if (disease != null) {
      val bundle = bundleOf(Constants.DISEASE_ID to disease.diseaseId)
      if (Network.isAvailable(requireContext())) {
        findNavController().navigate(R.id.action_fragment_digital_vet_item_details, bundle)
      } else {
        binding.let {
          findNavController().navigate(R.id.action_locate_Store_details, bundle)
        }
      }
    }
  }

  private fun displayNodata() {
    binding.rvDigitalVet.hideShimmer()
    //binding.ivNoDigitalVet.visibility = View.VISIBLE
    //Need to chance once SVG files received
    binding.rvDigitalVet.visibility = View.GONE
    binding.let { Snackbar.make(it.root, R.string.no_data_found, Snackbar.LENGTH_LONG).show() }
  }

  private fun getData() {
    if (Network.isAvailable(requireActivity())) {
      identifyDiseaseViewModel!!.getDigitalVetDetailList(
        mapOf(
          Constants.SYMPTOMS_ID to disease_id,
          Constants.LANGUAGE to myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString()
        )
      )
    } else {
      binding.let { Snackbar.make(it.root, R.string.no_internet, Snackbar.LENGTH_LONG).show() }
    }
  }

}