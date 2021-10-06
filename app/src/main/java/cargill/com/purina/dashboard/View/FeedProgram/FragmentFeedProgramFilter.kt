package cargill.com.purina.dashboard.View.FeedProgram

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.ColorInt
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
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedProgram
import cargill.com.purina.dashboard.Repository.FeedProgramRepository
import cargill.com.purina.dashboard.View.DashboardActivity
import cargill.com.purina.dashboard.viewModel.FeedProgramViewModel
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.FeedProgramViewModelFactory
import cargill.com.purina.databinding.FragmentFeedProgramFilterBinding
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants
import cargill.com.purina.utils.Utils
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_feeding_programs.*
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
  fun SearchView.setHintTextColor(@ColorInt color: Int) {
    findViewById<EditText>(R.id.search_src_text).setHintTextColor(color)
  }
  fun SearchView.setTextColor(@ColorInt color: Int) {
    findViewById<EditText>(R.id.search_src_text).setTextColor(color)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    init()
    _binding.searchFilterView.setHintTextColor(getResources().getColor(R.color.white))
    _binding.searchFilterView.setTextColor(getResources().getColor(R.color.white))
    _binding.searchFilterView.setOnSearchClickListener {
      (requireActivity() as DashboardActivity).closeIfOpen()
    }
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
      Utils.hideSoftKeyBoard(requireContext(), _binding.root)
      (requireActivity() as DashboardActivity).closeIfOpen()
      findNavController().navigate(R.id.action_fragmentFeedProgramFilter_to_home)
    }
    _binding.applyFilterBtn.setOnClickListener {
      (requireActivity() as DashboardActivity).closeIfOpen()
      var programId: String? = ""
      var programName:String? = ""
      for (i in 0 until _binding.feedProgramChipGroup.childCount){
        val program = _binding.feedProgramChipGroup.getChildAt(i) as Chip
        if(program.isChecked){
          programId = program.id.toString()
          programName = program.text.toString()
        }
      }
      _binding.root.clearFocus()
      if(programId!!.isNotEmpty()){
        if(_binding.noOfAnimals.text.toString().isNotEmpty()){
          val bundle = bundleOf(
            Constants.PROGRAM_ID to programId,
            Constants.PROGRAM_NAME to programName,
            Constants.NUMBER_ANIMALS to _binding.noOfAnimals.text.toString())
          findNavController().navigate(R.id.action_fragmentFeedProgramFilter_to_fragmentFeedingProgram, bundle)
        }else{
          Snackbar.make(_binding.root, getString(R.string.please_enter_animals), Snackbar.LENGTH_LONG).show()
        }
      }else{
        Snackbar.make(_binding.root, getString(R.string.please_select_feed_program), Snackbar.LENGTH_LONG).show()
      }
    }
    sharedViewmodel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    sharedViewmodel?.selectedItem?.observe(_binding.lifecycleOwner!!, Observer {
      sharedViewmodel!!.navigate("")
      if(dataLoaded){
        getData()
      }
    })
    feedProgramViewModel!!.bookmarkData.observe(_binding.lifecycleOwner!!, Observer {
      Log.i("data Coming ", it.toString())
      if(it.isNotEmpty()){
        /*_binding.bookmarkViewPager?.adapter = BookmarkViewPagerAdapter(it,{program-> bookmarkClick(program)})
        _binding.bookmarkTabLayout?.let {
          _binding.bookmarkViewPager?.let { it1 ->
            TabLayoutMediator(it, it1){ tab, position->
            }.attach()
          }
        }*/
      }
    })
    feedProgramViewModel!!.response.observe(_binding.lifecycleOwner!!, Observer {
      if(it.FeedingPrograms.isNotEmpty()){
        dataLoaded = true
        _binding.nodata.visibility = View.GONE
        _binding.filterContainer.visibility = View.VISIBLE
        _binding.feedProgramCard.visibility = View.VISIBLE
        Log.i("data",it.toString())
        _binding.feedProgramChipGroup.removeAllViewsInLayout()
        _binding.feedProgramChipGroup.isSingleSelection = true
        _binding.feedProgramChipGroup.isSelectionRequired = true
        val inflaterFeedPrograms = LayoutInflater.from(this.context)
        for (programs in it.FeedingPrograms){
          if(programs.mode_active){
            val programs_Chipitem = inflaterFeedPrograms.inflate(R.layout.chip_item, _binding.feedProgramChipGroup, false) as Chip
            programs_Chipitem.text = programs.program_name
            programs_Chipitem.tag = programs.program_id
            programs_Chipitem.id = programs.program_id
            programs_Chipitem.isCheckable = true
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
        _binding.filterContainer.visibility = View.GONE
        _binding.nodata.visibility = View.VISIBLE
        Snackbar.make(_binding.root, R.string.no_data_found, Snackbar.LENGTH_LONG).show()
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
    getData()
  }
  val broadCastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      if(Network.isAvailable(requireContext())){
        if(dataLoaded)
          getData()
      }
    }
  }
  override fun onAttach(context: Context) {
    super.onAttach(context)
    myPreference = AppPreference(context)
  }
  override fun onResume() {
    super.onResume()
    val filter = IntentFilter()
    filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
    requireActivity().registerReceiver(broadCastReceiver, filter)
  }

  override fun onPause() {
    super.onPause()
    requireActivity().unregisterReceiver(broadCastReceiver);
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
      ))
    }else{
      Snackbar.make(_binding.root, R.string.working_offline, Snackbar.LENGTH_LONG).show()
      feedProgramViewModel!!.getCacheData(myPreference.getStringValue(Constants.USER_LANGUAGE_CODE).toString(), myPreference.getStringValue(Constants.USER_ANIMAL_CODE).toString())
    }
  }
  private fun bookmarkClick(program:FeedProgram){
  }
}