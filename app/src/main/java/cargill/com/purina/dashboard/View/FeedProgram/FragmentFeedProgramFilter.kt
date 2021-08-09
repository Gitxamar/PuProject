package cargill.com.purina.dashboard.View.FeedProgram

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cargill.com.purina.Database.PurinaDataBase
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.Service.PurinaService
import cargill.com.purina.dashboard.Repository.FeedProgramRepository
import cargill.com.purina.dashboard.viewModel.FeedProgramViewModel
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.FeedProgramViewModelFactory
import cargill.com.purina.databinding.FragmentFeedProgramFilterBinding
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.view.*

class FragmentFeedProgramFilter : Fragment() {
  var binding: FragmentFeedProgramFilterBinding? = null
  private val _binding get() = binding!!
  private var feedProgramViewModel: FeedProgramViewModel? = null
  lateinit var myPreference: AppPreference
  private var searchQuery:String = ""
  private var dataLoaded:Boolean = false
  var sharedViewmodel: SharedViewModel? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentFeedProgramFilterBinding.inflate(inflater, container, false)
    val view = binding!!.root
    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    init()
    _binding.searchFilterView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
      override fun onQueryTextSubmit(query: String?): Boolean {
        if(Network.isAvailable(requireContext())){
          if (query != null) {
            _binding.searchFilterView.clearFocus()
            searchQuery = query
          }
          getData()
        }else{
          Snackbar.make(_binding.root,R.string.no_internet, Snackbar.LENGTH_LONG).show()
        }
        return true
      }
      override fun onQueryTextChange(newText: String?): Boolean {
        return true
      }
    })
    _binding.back.setOnClickListener {
      findNavController().navigate(R.id.action_fragmentFeedProgramFilter_to_home)
    }
    _binding.applyFilterBtn.setOnClickListener {
      var programId:String = ""
      var programName:String = ""
      for (i in 0 until _binding.feedProgramChipGroup.childCount){
        val program = _binding.feedProgramChipGroup.getChildAt(i) as Chip
        if(program.isChecked){
          programId = program.tag.toString()
          programName = program.text.toString()
        }
      }
      val bundle = bundleOf(
        Constants.PROGRAM_ID to programId,
        Constants.PROGRAM_NAME to programName,
        Constants.NUMBER_ANIMALS to _binding.noOfAnimals.text.toString())
      findNavController().navigate(R.id.action_fragmentFeedProgramFilter_to_fragmentFeedingProgram, bundle)
    }

    sharedViewmodel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    sharedViewmodel?.selectedItem?.observe(_binding.lifecycleOwner!!, Observer {
      sharedViewmodel!!.navigate("")
      if(dataLoaded){
        getData()
      }
    })
  }
  private fun init(){
    val dao = PurinaDataBase.invoke(requireActivity().applicationContext).dao
    val repository = FeedProgramRepository(dao, PurinaService.getDevInstance(),requireActivity())
    val factory = FeedProgramViewModelFactory(repository)
    feedProgramViewModel = ViewModelProvider(this,factory).get(FeedProgramViewModel::class.java)
    binding!!.feedProgramFilterViewModel = feedProgramViewModel
    binding!!.lifecycleOwner = this
    observerResponse()
  }
  private fun observerResponse(){
    getData()
    feedProgramViewModel!!.response.observe(_binding.lifecycleOwner!!, Observer {
      if(it.FeedingPrograms.isNotEmpty()){
        dataLoaded = true
        _binding.container.visibility = View.VISIBLE
        _binding.feedProgramCard.visibility = View.VISIBLE
        Log.i("data",it.toString())
        _binding.feedProgramChipGroup.removeAllViewsInLayout()
        val inflaterFeedPrograms = LayoutInflater.from(this.context)
        for (programs in it.FeedingPrograms){
          if(programs.mode_active){
            val programs_Chipitem = inflaterFeedPrograms.inflate(R.layout.chip_item, null, false) as Chip
            programs_Chipitem.text = programs.program_name
            programs_Chipitem.tag = programs.program_id
            programs_Chipitem.checkedIcon?.let {it1->
              val wrappedDrawable =
                DrawableCompat.wrap(it1)
              DrawableCompat.setTint(wrappedDrawable, Color.WHITE)
              programs_Chipitem.checkedIcon = wrappedDrawable
            }
            _binding.feedProgramChipGroup.addView(programs_Chipitem)
          }
        }
      }else{
        dataLoaded = true
        _binding.feedProgramCard.visibility = View.GONE
      }
    })
  }
  override fun onAttach(context: Context) {
    super.onAttach(context)
    myPreference = AppPreference(context)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
  }
  private fun getData(){
    if(Network.isAvailable(requireActivity())){
      feedProgramViewModel!!.getRemoteData(mapOf(
        Constants.LANGUAGE_CODE to myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString(),
        Constants.SEARCH_QUERY to searchQuery,
        Constants.SPECIES_ID to myPreference.getStringValue(Constants.USER_ANIMAL_CODE).toString()
        //Constants.SPECIES_ID to ""
      ))
    }else{
      Snackbar.make(_binding.root, R.string.working_offline, Snackbar.LENGTH_LONG).show()
      feedProgramViewModel!!.getCacheData(myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString(), myPreference.getStringValue(Constants.USER_ANIMAL_CODE).toString())
    }
  }
}