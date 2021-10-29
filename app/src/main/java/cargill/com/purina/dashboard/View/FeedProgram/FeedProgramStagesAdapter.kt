package cargill.com.purina.dashboard.View.FeedProgram

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import cargill.com.purina.R
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedProgramStages
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedprogramRow
import cargill.com.purina.databinding.FeedProgramStageItemBinding
import cargill.com.purina.utils.Constants
import cargill.com.purina.utils.Utils
import coil.load
import coil.request.CachePolicy
import kotlin.math.log

class FeedProgramStagesAdapter(private val clickListener: (FeedProgramStages,FeedprogramRow, Int) -> Unit, private val save: (FeedprogramRow) -> Unit, private val updateTotal: FragFeedProgramUpdateTotal): RecyclerView.Adapter<StagesViewHolder>() {

  private var programStages = ArrayList<FeedprogramRow>()
  private var program : FeedProgramStages? = null
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StagesViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding: FeedProgramStageItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.feed_program_stage_item, parent, false)
    return StagesViewHolder(binding, parent.context)
  }

  override fun onBindViewHolder(holder: StagesViewHolder, position: Int) {
    if(program!!.feedprogram_row[position].stage_no > 0){
      holder.itemView.visibility = View.VISIBLE
      holder.itemView.layoutParams =
        RecyclerView.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT
        )
      holder.bind(this.program!!,clickListener, save, updateTotal)
    }else{
      holder.itemView.visibility =View.GONE
      holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
    }
  }
  override fun getItemCount(): Int {
    return programStages.size
  }
  fun setList(program: FeedProgramStages){
    this.program = program
    this.programStages.clear()
    this.programStages.addAll(program.feedprogram_row)
  }
}
class StagesViewHolder(val binding: FeedProgramStageItemBinding, val ctx: Context): RecyclerView.ViewHolder(binding.root){
  fun bind(program:FeedProgramStages, clickListener: (FeedProgramStages,FeedprogramRow, Int) -> Unit, save: (FeedprogramRow)->Unit, updateTotal: FragFeedProgramUpdateTotal){
    val stage = program.feedprogram_row[layoutPosition]
    if(program.feedprogram_row[0].stage_no == 0){
      program.feedprogram_row[0].numberOfAnimals = program.numberOfAnimals.toDouble()
    }
    if (stage.stage_no > 0){
      binding.productImage.load(Constants.DEV_BASE_URL+stage.image_url){
        memoryCachePolicy(CachePolicy.ENABLED)
        diskCachePolicy(CachePolicy.READ_ONLY)
      }
      binding.feedProgramStageName.text = stage.recipe_name
      binding.recipeCodeText.text = stage.recipe_code
      binding.stageNumber.text = stage.stage_no.toString()
      binding.finishDayData.text = stage.age_days.toString()
      /*Feed Required = (Feed norms  kg per head daily * Age Days Finish Feeding) * Heads initial */
      val feedNormsKgPerHead:Double?
      if(stage.stage_no == 1){
        feedNormsKgPerHead = stage.feed_norms * stage.age_days
      }else{
        feedNormsKgPerHead = (stage.feed_norms * (stage.age_days.minus(program.feedprogram_row[position.minus(1)].age_days)))
      }

      /*Heads Initial*/
      //Position is 1 when we have stage0 or else position is 0
      if(position == 0){
        program.feedprogram_row[0].numberOfAnimals = program.numberOfAnimals.toDouble()//.times(1.minus(stage.mortality_rate))
        stage.numberOfAnimals = program.feedprogram_row[0].numberOfAnimals // stage0 Number of animals if exist or stage 1
      }else{
        stage.numberOfAnimals = program.feedprogram_row[position.minus(1)].numberOfAnimals.times(1.minus(stage.mortality_rate))
      }
      stage.feed_required = Utils.roundOffDecimal(feedNormsKgPerHead * stage.numberOfAnimals)!!
      binding.feedRequiredData.text = stage.feed_required.toString().plus(" ").plus(ctx.getString(R.string.kg))
      binding.additionalFeedData.setText(stage.additional_feed.toString())
      binding.bagPriceData.setText(stage.bag_price.toString())
      binding.clear.setOnClickListener {
        binding.additionalFeedData.text!!.clear()
        binding.bagPriceData.text!!.clear()
        stage.additional_feed = 0
        stage.bag_price = 0
      }
      binding.save.setOnClickListener {
        save(stage)
      }
      binding.stageContainer.setOnClickListener {
        clickListener(program,stage,layoutPosition)
      }
      binding.additionalFeedData.setOnClickListener {
        binding.additionalFeedData.setText("")
      }
      binding.bagPriceData.setOnClickListener {
        binding.bagPriceData.setText("")
      }
      binding.additionalFeedData.doAfterTextChanged {
        if(it.toString().isNotEmpty()) {
          stage.additional_feed = binding.additionalFeedData.text.toString().toInt()
        }
      }

      binding.bagPriceData.doAfterTextChanged {
        val numberOfStages = program.feedprogram_row.size
        if(it.toString().isNotEmpty()){
          stage.bag_price = binding.bagPriceData.text.toString().toInt()
          /*Feed Cost = feedRequired * Price of 1 KG rub*/
          stage.feed_cost = stage.feed_required.times(stage.bag_price).toInt()
          /* Accumulated cost for head = (Add all feed cost of stage) / Heads initial*/
          var sumOfStageFeedCost = 0
          program.feedprogram_row.forEach { stage ->
            if(stage.stage_no <= layoutPosition.plus(1)){
              sumOfStageFeedCost += stage.feed_cost
            }
          }
          stage.accumulated_cost_head =
            Utils.roundOffDecimal(sumOfStageFeedCost / stage.numberOfAnimals)!!
          if(stage.accumulated_cost_head != 0.0){
            stage.accumulated_cost_kg = Math.round(stage.accumulated_cost_head.div(stage.expected_wt) * 100.0) / 100.0
          }

          /*Stage Complete Feed Equivalent = Stage feed Required / Stage inclusion rate*/
          if(stage.inclusion_rate != 0) {
            stage.completed_feed_equivalent = Utils.roundOffDecimal((stage.feed_required.div(stage.inclusion_rate)) * 100)?.toInt()!!
          }

          /*Program Feed Cost = Sum of all stage feed cost*/
          program.purinaFeedCost = program.feedprogram_row.sumOf { stageFeedCost->
            stageFeedCost.feed_cost
          }
          /*Program other Expenses = Sum of all stage additional Feed expenses*/
          program.otherExpenses = program.feedprogram_row.sumOf { otherExpense->
            otherExpense.additional_feed
          }

          /*Program feed required per Kg = Sum of all stage Feed Required*/
          program.purinaFeedRequiredPerKg = program.feedprogram_row.sumOf { totalFeedRequired->
            totalFeedRequired.feed_required
          }.toInt()

          /*program expected weight = MAX of stage expected weight * program heads initial*/
          program.expectedMeatKg = program.feedprogram_row.maxOf { expectedMeatTotal->
            expectedMeatTotal.expected_wt
          }.times(program.numberOfAnimals).toInt()

          /**/
          program.completeFeedEquivalentKg = program.feedprogram_row.sumOf { totalCompleteFeedEquivalentKg->
            totalCompleteFeedEquivalentKg.completed_feed_equivalent
          }
        }
        updateTotal.updateTotal(program)
      }
    }
  }
}