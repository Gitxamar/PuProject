package cargill.com.purina.dashboard.View.FeedProgram

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cargill.com.purina.R
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedprogramRow
import cargill.com.purina.databinding.FragmentDetailFeedProgramBinding
import cargill.com.purina.utils.Constants

class FragmentDetailFeedProgram : Fragment() {
  var binding: FragmentDetailFeedProgramBinding? = null
  private val _binding get() = binding!!
  private lateinit var stage:FeedprogramRow
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
    if(arguments != null){
      if(requireArguments().containsKey(Constants.FEED_PROGRAM_STAGE)){
        stage = arguments?.get(Constants.FEED_PROGRAM_STAGE)!! as FeedprogramRow
      }
    }
    populateData()
  }

  private fun populateData(){
    _binding.feedName.text = stage.recipe_name
    _binding.recipeCode.text = requireContext().getString(R.string.recipe_code).plus(" "+stage.recipe_code)
    _binding.feedRequiredData.text = stage.feed_required.toString()
    _binding.finishDayData.text = stage.age_days.toString()
    _binding.additionalFeedEditText.setText(stage.additional_feed.toString())
    _binding.bagPriceEdittext.setText(stage.bag_price.toString())
    _binding.feedingNormsData.text = stage.feed_norms.toString()
    _binding.expectedWeightData.text = stage.expected_wt.toString()
    _binding.mortalityRateData.text = stage.mortality_rate.toString()
    _binding.feedingNormsStageData.text = ""
    _binding.feedCostData.text = stage.feed_cost.toString()
    _binding.accumulatedCostkgData.text = stage.accumulated_cost_kg.toString()
    _binding.accumulatedCostheadData.text = stage.accumulated_cost_head.toString()
    _binding.inclusionRateData.text = stage.inclusion_rate.toString()
    _binding.completeFeedData.text = stage.completed_feed_equivalent.toString()

  }
}