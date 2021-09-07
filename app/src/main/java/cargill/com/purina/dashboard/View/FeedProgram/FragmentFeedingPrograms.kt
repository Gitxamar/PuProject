package cargill.com.purina.dashboard.View.FeedProgram

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cargill.com.purina.Database.PurinaDataBase
import cargill.com.purina.R
import cargill.com.purina.Service.Network
import cargill.com.purina.Service.PurinaService
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedProgramStages
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedprogramRow
import cargill.com.purina.dashboard.Repository.FeedProgramRepository
import cargill.com.purina.dashboard.viewModel.FeedProgramViewModel
import cargill.com.purina.dashboard.viewModel.SharedViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.FeedProgramViewModelFactory
import cargill.com.purina.databinding.FragmentFeedingProgramsBinding
import cargill.com.purina.utils.AppPreference
import cargill.com.purina.utils.Constants

class FragmentFeedingPrograms : Fragment(),FragFeedProgramNotifyDataChange, FragFeedProgramUpdateTotal {
  var binding:FragmentFeedingProgramsBinding? = null
  private val _binding get() = binding!!
  private var feedProgramViewModel: FeedProgramViewModel? = null
  private lateinit var adapter:FeedProgramStagesAdapter
  lateinit var myPreference: AppPreference
  private var dataLoaded:Boolean = false
  var sharedViewmodel: SharedViewModel? = null
  private var programId:String = ""
  private var programName:String = ""
  private var animalsInNumber:String = ""
  var userClickedPosition : Int = 0
  val stages = MutableLiveData<List<FeedprogramRow>>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentFeedingProgramsBinding.inflate(inflater, container, false)
    val view = binding!!.root
    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    if(arguments != null){
      if(requireArguments().containsKey(Constants.PROGRAM_ID)){
        programId = arguments?.getString(Constants.PROGRAM_ID).toString()
      }
      if(requireArguments().containsKey(Constants.NUMBER_ANIMALS)){
        animalsInNumber = arguments?.getString(Constants.NUMBER_ANIMALS).toString()
      }
      if(requireArguments().containsKey(Constants.PROGRAM_NAME)){
        programName = arguments?.getString(Constants.PROGRAM_NAME).toString()
      }
    }
    _binding.calender.setOnClickListener {
      FragmentFeedReminderDialog(stages.value!!).show(requireFragmentManager(),"FragmentFeedReminderDialog")
    }
    _binding.bookmarkFeedPro.setOnClickListener {
      feedProgramViewModel!!.addRemoveBookmark(programId.toInt(), animalsInNumber.toInt())
    }
    init()
    sharedViewmodel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    sharedViewmodel?.selectedItem?.observe(_binding.lifecycleOwner!!, Observer {
      sharedViewmodel!!.navigate("")
      if(dataLoaded){
        findNavController().navigate(R.id.action_fragmentFeedingProgram_to_fragmentFeedProgramFilter)
      }
    })
    _binding.back.setOnClickListener {
      findNavController().navigate(R.id.action_fragmentFeedingProgram_to_fragmentFeedProgramFilter)
    }
  }
  override fun onAttach(context: Context) {
    super.onAttach(context)
    myPreference = AppPreference(context)
  }

  private fun init(){
    val dao = PurinaDataBase.invoke(requireActivity().applicationContext).dao
    val repository = FeedProgramRepository(dao, PurinaService.getDevInstance(),requireActivity())
    val factory = FeedProgramViewModelFactory(repository)
    feedProgramViewModel = ViewModelProvider(this,factory).get(FeedProgramViewModel::class.java)
    binding!!.feedProgramViewModel = feedProgramViewModel
    binding!!.lifecycleOwner = this
    initRecyclerView()
  }
  private fun initRecyclerView(){
    _binding.feedProgramStageList.layoutManager = LinearLayoutManager(requireContext())
    adapter = FeedProgramStagesAdapter ({program:FeedProgramStages,stage: FeedprogramRow, position:Int ->onItemClick(program,stage, position)},{ saveStage: FeedprogramRow -> saveData(saveStage)}, this)
    _binding.feedProgramStageList.adapter = adapter
    getData()
  }

  private fun getData(){
    if(Network.isAvailable(requireActivity())){
      feedProgramViewModel!!.getFeedProgramStageDetails(programId.toInt())
    }else{
      feedProgramViewModel!!.getStageCacheData(programId.toInt())
    }
    observerResponse()
  }
  @SuppressLint("NotifyDataSetChanged")
  private fun observerResponse(){
    feedProgramViewModel!!.stageData().observe(_binding.lifecycleOwner!!, Observer {allStages ->
      Log.i("getting data", allStages.toString())
      stages.value = allStages
      if(allStages[0].stage_no == 0){
        _binding.ageOfStartingFeedData.text = allStages[0].age_days.toString().plus("days")
        _binding.expectedWeightData.text = allStages[0].expected_wt.toString().plus("kg")
        _binding.additionalFeedText.text = getString(R.string.additional_feed_expenses).plus(" ( ").plus(allStages[0].comments).plus(" )")
      }
      _binding.stageZeroExpenses.doAfterTextChanged {
        if(allStages[0].stage_no == 0){
          allStages[0].additional_feed = _binding.stageZeroExpenses.text.toString().toInt()
          adapter.setList(FeedProgramStages(allStages as ArrayList<FeedprogramRow>, true, programName, 0, animalsInNumber.toInt(),0,0,0,0,0,0))
          adapter.notifyDataSetChanged()
        }
      }
      adapter.setList(FeedProgramStages(allStages as ArrayList<FeedprogramRow>, true, programName, 0, animalsInNumber.toInt(),0,0,0,0,0,0))
      adapter.notifyDataSetChanged()
      _binding.FeedingProgramName.text = programName
      _binding.animalNumber.text = animalsInNumber.plus(" "+myPreference.getStringValue(Constants.USER_ANIMAL).toString())
      dataLoaded = true
    })
  }
  private fun onItemClick(program:FeedProgramStages, stage:FeedprogramRow,position: Int){
    userClickedPosition = position
    requireFragmentManager().beginTransaction().add(R.id.fragmentDashboard, FragmentDetailFeedProgram(program,stage,this )).addToBackStack(null).commit()
  }
  private fun saveData(program:FeedprogramRow){
    feedProgramViewModel!!.updateFeedProgramStageUnits(animalsInNumber.toInt(),program)
  }
  override fun onChanged() {
    adapter.notifyItemChanged(userClickedPosition)
  }

  override fun updateTotal(program: FeedProgramStages) {
    /*Program Feed Cost = Sum of all stage feed cost*/
    _binding.feedCostData.text = program.purinaFeedCost.toString()
    /*Program other Expenses = Sum of all stage additional Feed expenses*/
    _binding.otherExpensesData.text = program.otherExpenses.toString()
    /*Program total Expenses = program feed cost + Program other expenses*/
    _binding.totalExpensesData.text = (program.purinaFeedCost.plus(program.otherExpenses)).toString()

    /*Total cost of meat per kg =  Total Expenses / porgram expected meat per kg*/
    if(_binding.totalExpensesData.text.toString().toInt() > 0 && program.expectedMeatKg > 0){
      var totalCostOfMeatKg = _binding.totalExpensesData.text.toString().toInt().div(program.expectedMeatKg)
      _binding.totalCostData.text = totalCostOfMeatKg.toString()
    }

    _binding.feedRequiredData.text = program.purinaFeedRequiredPerKg.toString()
    _binding.completeFeedData.text = program.completeFeedEquivalentKg.toString()
    _binding.expectedMeatData.text = program.expectedMeatKg.toString()
    /*Converstional rate = Program complete feed equivalent / program expected meat per kg*/
    if(program.completeFeedEquivalentKg >0 && program.expectedMeatKg > 0){
      _binding.converstionRateData.text = program.completeFeedEquivalentKg.div(program.expectedMeatKg).toString()
    }
  }
}
interface FragFeedProgramNotifyDataChange {
  fun onChanged()
}
interface FragFeedProgramUpdateTotal {
  fun updateTotal(program: FeedProgramStages)
}