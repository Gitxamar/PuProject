package cargill.com.purina.dashboard.View.IdentifyDiseases

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
import cargill.com.purina.dashboard.Model.IdentifyDisease.DiseaseImageList
import cargill.com.purina.dashboard.Model.IdentifyDisease.DiseasesDetail
import cargill.com.purina.dashboard.Repository.IdentifyDiseaseRepository
import cargill.com.purina.dashboard.View.DashboardActivity
import cargill.com.purina.dashboard.viewModel.IdentifyDiseaseViewModel
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.IdentifyDiseaseViewModelFactory
import cargill.com.purina.databinding.FragmentDigitalVetDetailsItemBinding
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator

class DigitalVetDiseaseDetailsFragment : Fragment() {
  lateinit var myPreference: AppPreference
  lateinit var binding: FragmentDigitalVetDetailsItemBinding
  var sharedViewmodel: SharedViewModel? = null
  private lateinit var identifyDiseaseViewModel: IdentifyDiseaseViewModel
  private var disease_id: Int = 0
  private var is_disease: Boolean = false
  private lateinit var adapter: DiseaseSymptomsAdapter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentDigitalVetDetailsItemBinding.inflate(inflater, container, false)
    val view = binding!!.root
    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val dao = PurinaDataBase.invoke(requireActivity().applicationContext).dao
    val repository =
      IdentifyDiseaseRepository(dao, PurinaService.getDevInstance(), requireActivity())
    val factory = IdentifyDiseaseViewModelFactory(repository)

    identifyDiseaseViewModel =
      ViewModelProvider(this, factory).get(IdentifyDiseaseViewModel::class.java)
    binding.diseaseDetailViewModel = identifyDiseaseViewModel
    binding.lifecycleOwner = this
    if (arguments != null) {
      if (requireArguments().containsKey(Constants.DISEASE_ID)) {
        disease_id = arguments?.getInt(Constants.DISEASE_ID)!!
      }
    }

    binding.back.setOnClickListener {
      (requireActivity() as DashboardActivity).closeIfOpen()
      findNavController().navigate(R.id.action_fragment_digital_vet_detail_back)
    }

    (requireActivity() as DashboardActivity).disableBottomMenu()

    identifyDiseaseViewModel?.remoteDiseaseDetail?.observe(binding.lifecycleOwner!!, Observer {
      if (it.isSuccessful) {
        Log.i("data commingng", it.body().toString())
        if (it.body()!!.DiseasesDetail != null) {
          displayData(it.body()!!.DiseasesDetail)
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

    getData()

  }

  private fun getData() {
    if (Network.isAvailable(requireActivity())) {
      identifyDiseaseViewModel!!.getDiseaseDetail(disease_id)
    } else {
      var details: DiseasesDetail = identifyDiseaseViewModel!!.getOfflineDiseaseDetail(disease_id)
      if (details.id != null) {
        displayData(details)
      } else {
        displayNodata()
      }
    }
  }

  private fun displayData(diseasesDetail: DiseasesDetail) {
    binding.tvDisesaseName.text = diseasesDetail.name
    binding.tvDiseaseDescription.text = diseasesDetail.description
    if (diseasesDetail.diseaseImages.size < 0) {
      binding.imageViewPager.visibility = View.GONE
    } else {

    }
    if (diseasesDetail.symptomsList.size > 0) {
      binding.rvSymptoms.layoutManager =
        LinearLayoutManager(activity?.applicationContext, LinearLayoutManager.VERTICAL, false)
      adapter = DiseaseSymptomsAdapter()
      binding.rvSymptoms.adapter = adapter
      binding.rvSymptoms.showShimmer()

      binding.rvSymptoms.hideShimmer()
      adapter.setList(ArrayList(diseasesDetail.symptomsList))
      adapter.notifyDataSetChanged()
    } else {
      binding.tvListofSymptomsHeader.visibility = View.GONE
      binding.rvSymptoms.visibility = View.GONE
    }

    if (diseasesDetail.diseaseImages.size > 0) {

      if(diseasesDetail.diseaseImages.size == 1){
        binding.imageViewPager?.adapter = DiseaseImageViewAdapter(diseasesDetail.diseaseImages, {
            images: List<DiseaseImageList> -> null })
        binding.imageTabLayout.visibility = View.GONE
      }else{
        binding.imageViewPager?.adapter = DiseaseImageViewAdapter(
          diseasesDetail.diseaseImages,
          { images: List<DiseaseImageList> -> null })
        binding.imageTabLayout?.let {
          binding.imageViewPager?.let { it1 ->
            TabLayoutMediator(it, it1) { tab, position ->
            }.attach()
          }
        }
      }
    } else {
      binding.imageViewPager.visibility = View.GONE
    }

  }

  private fun displayNodata() {
    binding.let { Snackbar.make(it.root, R.string.no_data_found, Snackbar.LENGTH_LONG).show() }
  }

}