package cargill.com.purina.dashboard.View.FeedProgram

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import cargill.com.purina.R
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedProgramStages
import cargill.com.purina.dashboard.Model.FeedingProgram.FeedprogramRow
import cargill.com.purina.databinding.FeedProgramStageItemBinding
import cargill.com.purina.utils.Constants
import coil.load
import coil.request.CachePolicy

class FeedProgramStagesAdapter(private val clickListener: (FeedprogramRow, Int) -> Unit, private val save: (FeedprogramRow) -> Unit): RecyclerView.Adapter<StagesViewHolder>() {

  private var programStages = ArrayList<FeedprogramRow>()
  private var program : FeedProgramStages? = null
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StagesViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding: FeedProgramStageItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.feed_program_stage_item, parent, false)
    return StagesViewHolder(binding, parent.context)
  }

  override fun onBindViewHolder(holder: StagesViewHolder, position: Int) {
    holder.bind(this.program!!, programStages[position],clickListener, save)
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
  fun bind(program:FeedProgramStages, stage: FeedprogramRow, clickListener: (FeedprogramRow, Int) -> Unit, save: (FeedprogramRow)->Unit){
    binding.productImage.load(Constants.DEV_BASE_URL+stage.image_url){
      memoryCachePolicy(CachePolicy.ENABLED)
      diskCachePolicy(CachePolicy.READ_ONLY)
    }
    binding.feedProgramStageName.text = stage.recipe_name
    binding.finishDayData.text = stage.age_days.toString()
    stage.feed_required = ((stage.feed_norms * stage.age_days) * program.numberOfAnimals).toInt()
    binding.feedRequiredData.text = stage.feed_required.toString().plus(" Kg")
    binding.animalFeedData.setText(stage.additional_feed.toString())
    binding.bagPriceData.setText(stage.bag_price.toString())
    binding.clear.setOnClickListener {
      binding.animalFeedData.text!!.clear()
      binding.bagPriceData.text!!.clear()
      stage.additional_feed = 0
      stage.bag_price = 0
    }
    binding.save.setOnClickListener {
      stage.additional_feed = binding.animalFeedData.text.toString().toInt()
      stage.bag_price = binding.bagPriceData.text.toString().toInt()
      save(stage)
    }
    binding.stageContainer.setOnClickListener {
      clickListener(stage,position)
    }
  }
}