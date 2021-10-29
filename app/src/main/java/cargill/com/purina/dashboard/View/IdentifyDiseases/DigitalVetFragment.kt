package cargill.com.purina.dashboard.View.IdentifyDiseases

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import cargill.com.purina.Database.PurinaDataBase
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.Service.PurinaService
import cargill.com.purina.dashboard.Model.IdentifyDisease.DiseaseImageList
import cargill.com.purina.dashboard.Model.IdentifyDisease.Symptoms
import cargill.com.purina.dashboard.Repository.IdentifyDiseaseRepository
import cargill.com.purina.dashboard.viewModel.IdentifyDiseaseViewModel
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.IdentifyDiseaseViewModelFactory
import cargill.com.purina.databinding.FragmentDigitalVetBinding
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_contact_us.view.*


class DigitalVetFragment : Fragment() {
  lateinit var myPreference: AppPreference
  lateinit var binding: FragmentDigitalVetBinding
  var sharedViewmodel: SharedViewModel? = null
  private lateinit var identifyDiseaseViewModel: IdentifyDiseaseViewModel
  private var etSymptoms1Id: Int = 0
  private var etSymptoms2Id: Int = 0
  private var etSymptoms3Id: Int = 0
  private var etSymptoms4Id: Int = 0
  private var etSymptoms5Id: Int = 0
  private var etIsClicked1: Boolean = false
  private var etIsClicked2: Boolean = false
  private var etIsClicked3: Boolean = false
  private var etIsClicked4: Boolean = false
  private var etIsClicked5: Boolean = false
  private var sourceIds:String = ""

  var symptomsList: MutableList<Symptoms> = mutableListOf()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentDigitalVetBinding.inflate(inflater)
    return binding.root
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    myPreference = AppPreference(context)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val dao = PurinaDataBase.invoke(requireActivity().applicationContext).dao
    val repository = IdentifyDiseaseRepository(dao, PurinaService.getDevInstance(), requireActivity())
    val factory = IdentifyDiseaseViewModelFactory(repository)

    identifyDiseaseViewModel = ViewModelProvider(this, factory).get(IdentifyDiseaseViewModel::class.java)
    binding.digitalVetViewModel = identifyDiseaseViewModel
    binding.lifecycleOwner = this
    etIsClicked1 = true
    identifyDiseaseViewModel?.symptomsFilteredList?.observe(binding.lifecycleOwner!!, Observer {
      if (it.isSuccessful) {
        if(symptomsList.size>0){
          symptomsList.clear()
        }
        Log.i("data commingng", it.body().toString())
        if (!it.body()!!.symptoms.isNullOrEmpty()) {
          val adapter = PoiAdapter(requireActivity(), android.R.layout.simple_list_item_1, it.body()!!.symptoms)

          if(etIsClicked1){
            binding.etSymptoms1.setAdapter(adapter)
            binding.etSymptoms1.threshold = 0
          }else
          if(etIsClicked2){
            binding.etSymptoms2.setAdapter(adapter)
            binding.etSymptoms2.threshold = 3
          }else
          if(etIsClicked3){
            binding.etSymptoms3.setAdapter(adapter)
            binding.etSymptoms3.threshold = 3
          }else
          if(etIsClicked4){
            binding.etSymptoms4.setAdapter(adapter)
            binding.etSymptoms4.threshold = 3
          }else
          if(etIsClicked5){
            binding.etSymptoms5.setAdapter(adapter)
            binding.etSymptoms5.threshold = 3
          }

        } else {
          displayNodata()
          //findNavController().navigate(R.id.action_fragment_digital_vet_back)
        }
      } else {
        displayNodata()
        //findNavController().navigate(R.id.action_fragment_digital_vet_back)
      }
    })

    identifyDiseaseViewModel!!.msg.observe(binding.lifecycleOwner!!, Observer {
      Snackbar.make(binding!!.root, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()
      displayNodata()
    })

    binding.back.setOnClickListener {
      findNavController().navigate(R.id.action_fragment_digital_vet_back)
    }

    binding.etSymptoms1.setOnItemClickListener() { parent, _, position, id ->
      val selectedPoi = parent.adapter.getItem(position) as Symptoms?
      etSymptoms1Id = selectedPoi?.id!!
      binding.etSymptoms1.setText(selectedPoi?.name)
      binding.etSymptoms1.setSelection(selectedPoi?.name.length)

      etIsClicked1 = false
      etIsClicked2 = true
      sourceIds = etSymptoms1Id.toString()
      Log.i("TotalIds", sourceIds)
      getData()
    }

    binding.ivSymptoms1.setOnClickListener {
      etIsClicked1 = true
      getData()
      binding.etSymptoms1.showDropDown();
    }

    binding.etSymptoms2.setOnItemClickListener() { parent, _, position, id ->
      val selectedPoi = parent.adapter.getItem(position) as Symptoms?
      etSymptoms2Id = selectedPoi?.id!!
      binding.etSymptoms2.setText(selectedPoi?.name)
      binding.etSymptoms2.setSelection(selectedPoi?.name.length)

      etIsClicked1 = false
      etIsClicked2 = false
      etIsClicked3 = true
      sourceIds = sourceIds.plus(",").plus(selectedPoi?.id)
      Log.i("TotalIds", sourceIds)
      getData()
    }

    binding.ivSymptoms2.setOnClickListener {
      etIsClicked1 = false
      etIsClicked2 = true
      getData()
      binding.etSymptoms2.showDropDown();
    }

    binding.etSymptoms3.setOnItemClickListener() { parent, _, position, id ->
      val selectedPoi = parent.adapter.getItem(position) as Symptoms?
      etSymptoms3Id = selectedPoi?.id!!
      binding.etSymptoms3.setText(selectedPoi?.name)
      binding.etSymptoms3.setSelection(selectedPoi?.name.length)

      etIsClicked1 = false
      etIsClicked2 = false
      etIsClicked3 = false
      etIsClicked4 = true
      sourceIds = sourceIds.plus(",").plus(selectedPoi?.id)
      Log.i("TotalIds", sourceIds)
      getData()
    }

    binding.ivSymptoms3.setOnClickListener {
      etIsClicked1 = false
      etIsClicked2 = false
      etIsClicked3 = false
      etIsClicked4 = true
      getData()
      binding.etSymptoms3.showDropDown();
    }

    binding.etSymptoms4.setOnItemClickListener() { parent, _, position, id ->
      val selectedPoi = parent.adapter.getItem(position) as Symptoms?
      etSymptoms4Id = selectedPoi?.id!!
      binding.etSymptoms4.setText(selectedPoi?.name)
      binding.etSymptoms4.setSelection(selectedPoi?.name.length)

      etIsClicked1 = false
      etIsClicked2 = false
      etIsClicked3 = false
      etIsClicked4 = false
      etIsClicked5 = true
      sourceIds = sourceIds.plus(",").plus(selectedPoi?.id)
      Log.i("TotalIds", sourceIds)
      getData()
    }

    binding.ivSymptoms4.setOnClickListener {
      etIsClicked1 = false
      etIsClicked2 = false
      etIsClicked3 = false
      etIsClicked4 = false
      etIsClicked5 = true
      getData()
      binding.etSymptoms4.showDropDown();
    }

    binding.etSymptoms5.setOnItemClickListener() { parent, _, position, id ->
      val selectedPoi = parent.adapter.getItem(position) as Symptoms?
      etSymptoms5Id = selectedPoi?.id!!
      binding.etSymptoms5.setText(selectedPoi?.name)
      binding.etSymptoms5.setSelection(selectedPoi?.name.length)

      sourceIds = sourceIds.plus(",").plus(selectedPoi?.id)
      Log.i("TotalIds", sourceIds)
      getData()
    }

    binding.ivSymptoms5.setOnClickListener {
      getData()
      binding.etSymptoms5.showDropDown();
    }

    binding.btnAddSymptoms.setOnClickListener {
      binding.btnAddSymptoms.visibility = View.GONE
      binding.rlSymptoms3.visibility = View.VISIBLE
      binding.rlSymptoms4.visibility = View.VISIBLE
      binding.rlSymptoms5.visibility = View.VISIBLE
    }

    binding.btnSubmit.setOnClickListener {
      val sourceIds = "${etSymptoms1Id}" + ",${etSymptoms2Id!!}" + ",${etSymptoms3Id!!}" + ",${etSymptoms4Id!!}" + ",${etSymptoms5Id!!}"
      Log.i("TotalIds", sourceIds)

      if (Network.isAvailable(requireActivity())) {
        val bundle = bundleOf(Constants.DISEASES_ID to sourceIds)
        Constants.DISEASES_IDS = sourceIds
        findNavController().navigate(R.id.action_fragment_digital_vet_details, bundle)
      }else{
        binding.let { Snackbar.make(it.root, R.string.no_internet, Snackbar.LENGTH_LONG).show() }
      }
    }

    getData()
  }

  private fun getData() {
    if (Network.isAvailable(requireActivity())) {
      if(sourceIds==""){
        identifyDiseaseViewModel!!.getFilteredSymptomsList(
          mapOf(
            Constants.LANGUAGE to myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString(),
            Constants.SPECIES_ID to myPreference.getStringValue(Constants.USER_ANIMAL_CODE).toString()
          )
        )
      }else{
        identifyDiseaseViewModel!!.getFilteredSymptomsList(
          mapOf(Constants.SYMPTOMS_ID to sourceIds,
            Constants.LANGUAGE to myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString(),
            Constants.SPECIES_ID to myPreference.getStringValue(Constants.USER_ANIMAL_CODE).toString()
          )
        )
      }

    } else {
      binding.btnSubmit.visibility = View.GONE
    }
  }

  private fun displayNodata() {
    binding.let { Snackbar.make(it.root, R.string.no_data_found, Snackbar.LENGTH_LONG).show() }
    binding.btnSubmit.visibility = View.GONE
    binding.btnSubmit.setBackgroundColor(resources.getColor(R.color.grey))
  }

  inner class PoiAdapter(
    context: Context,
    @LayoutRes private val layoutResource: Int,
    private val allPois: List<Symptoms>
  ) : ArrayAdapter<Symptoms>(context, layoutResource, allPois), Filterable {
    private var mPois: List<Symptoms> = allPois

    override fun getCount(): Int {
      return mPois.size
    }

    override fun getItem(p0: Int): Symptoms? {
      return mPois.get(p0)
    }

    override fun getItemId(p0: Int): Long {
      // Or just return p0
      return mPois.get(p0).id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
      val view: TextView = convertView as TextView? ?: LayoutInflater.from(context)
        .inflate(layoutResource, parent, false) as TextView
      view.text = "${mPois[position].name}"
      return view
    }

    override fun getFilter(): Filter {
      return object : Filter() {
        override fun publishResults(
          charSequence: CharSequence?,
          filterResults: Filter.FilterResults
        ) {
          mPois = filterResults.values as List<Symptoms>
          notifyDataSetChanged()
        }

        override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
          val queryString = charSequence?.toString()?.toLowerCase()

          val filterResults = Filter.FilterResults()
          filterResults.values = if (queryString == null || queryString.isEmpty())
            allPois
          else
            allPois.filter {
              it.name.toLowerCase().contains(queryString)
            }
          return filterResults
        }
      }
    }
  }

}