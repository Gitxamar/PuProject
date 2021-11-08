package cargill.com.purina.dashboard.View.FeedProgram

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cargill.com.purina.Database.PurinaDataBase
import cargill.com.purina.R
import cargill.com.purina.Service.PurinaService
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedProgramStages
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedprogramRow
import cargill.com.purina.dashboard.Repository.FeedProgramRepository
import cargill.com.purina.dashboard.View.DashboardActivity
import cargill.com.purina.dashboard.viewModel.FeedProgramViewModel
import cargill.com.purina.dashboard.viewModel.viewModelFactory.FeedProgramViewModelFactory
import cargill.com.purina.databinding.FragmentDetailFeedProgramBinding
import cargill.com.purina.utils.Constants
import cargill.com.purina.utils.Utils
import coil.load
import coil.request.CachePolicy

class FragmentDetailFeedProgram(private val program:FeedProgramStages, private val stage:FeedprogramRow, private val change:FragFeedProgramNotifyDataChange) : Fragment() {
  var binding: FragmentDetailFeedProgramBinding? = null
  private val _binding get() = binding!!
  private var feedProgramViewModel: FeedProgramViewModel? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentDetailFeedProgramBinding.inflate(inflater, container, false)
    val view = binding!!.root
    return view
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    init()
    _binding.back.setOnClickListener {
      (requireActivity() as DashboardActivity).closeIfOpen()
      stage.additional_feed = _binding.additionalFeedEditText.text.toString().toInt()
      stage.bag_price = _binding.bagPriceEdittext.text.toString().toInt()
      change.onChanged()
      requireFragmentManager().popBackStack()
    }
  }
  private fun init(){
    val dao = PurinaDataBase.invoke(requireActivity().applicationContext).dao
    val repository = FeedProgramRepository(dao, PurinaService.getDevInstance(),requireActivity())
    val factory = FeedProgramViewModelFactory(repository)
    feedProgramViewModel = ViewModelProvider(this,factory).get(FeedProgramViewModel::class.java)
    binding!!.feedProgramStageDetailViewModel = feedProgramViewModel
    binding!!.lifecycleOwner = this
    populateData()
  }

  private fun populateData(){
    _binding.feedName.text = stage.recipe_name
    _binding.code.text = stage.recipe_code
    _binding.stageNumber.text = stage.stage_no.toString()
    _binding.feedRequiredData.text = stage.feed_required.toString().plus(" ").plus(getString(R.string.kg))
    _binding.finishDayData.text = stage.age_days.toString()
    if(stage.comments.isNotEmpty()){
      _binding.additionalFeedText.text = getString(R.string.additional_feed).plus(" ( ").plus(stage.comments).plus(" )")
    }else{
      _binding.additionalFeedText.text = getString(R.string.additional_feed).plus(" ( ").plus(getString(R.string.expense)).plus(" )")
    }

    _binding.additionalFeedEditText.setText(stage.additional_feed.toString())

    _binding.bagPriceEdittext.setText(stage.bag_price.toString())
    _binding.bagPriceEdittext.doAfterTextChanged {
      if(it.toString().isNotEmpty()){
        stage.bag_price = it.toString().toInt()
        stage.feed_cost = stage.feed_required.times(stage.bag_price).toInt()
        /* Accumulated cost for head = (Add all feed cost of stage) / Heads initial*/
        var sumOfStageFeedCost = 0
        program.feedprogram_row.forEach { s ->
          if(s.stage_no <= stage.stage_no){
            sumOfStageFeedCost += s.feed_cost
          }
        }
        stage.accumulated_cost_head = Math.round((sumOfStageFeedCost / stage.numberOfAnimals) * 100.0) / 100.0
        if(stage.accumulated_cost_head != 0.0){
          stage.accumulated_cost_kg = Math.round(stage.accumulated_cost_head.div(stage.expected_wt)* 100.0) / 100.0
        }
        _binding.feedCostData.text = stage.feed_cost.toString()
        _binding.accumulatedCostkgData.text = stage.accumulated_cost_kg.toString()
        _binding.accumulatedCostheadData.text = stage.accumulated_cost_head.toString()
      }
    }
    _binding.feedingNormsData.text = stage.feed_norms.toString()
    _binding.expectedWeightData.text = stage.expected_wt.toString()
    _binding.mortalityRateData.text = stage.mortality_rate.toString()
    /*Heads Initial*/
    _binding.headsRemainingData.text = stage.numberOfAnimals.toInt().toString()
    /*Feeding norms for the stage kg/head = (Feed norms  kg per head daily * Age Days Finish Feeding) */
    _binding.feedingNormsStageData.text =
      (Math.round((stage.feed_norms.times(stage.age_days)) * 100.0 ) / 100.0).toString()
    /*Feed Cost = feedRequired * Price of 1 KG rub*/
    _binding.feedCostData.text = stage.feed_cost.toString()
    _binding.accumulatedCostkgData.text = stage.accumulated_cost_kg.toString()
    _binding.accumulatedCostheadData.text = stage.accumulated_cost_head.toString()
    _binding.inclusionRateData.text = stage.inclusion_rate.toString()
    if(stage.inclusion_rate > 0){
      val c = (stage.feed_required.div(stage.inclusion_rate)) * 100
      stage.completed_feed_equivalent =
        //Utils.roundOffDecimal((stage.feed_required.div(stage.inclusion_rate)) * 100)?.toInt()!!
        (Math.round(c * 100.0) / 100.0).toInt()
    }
    _binding.completeFeedData.text = stage.completed_feed_equivalent.toString()
  }
}