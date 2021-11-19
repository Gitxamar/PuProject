package cargill.com.purina.dashboard.View.IdentifyDiseases

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import cargill.com.purina.R
import cargill.com.purina.dashboard.Model.IdentifyDisease.Diseases
import cargill.com.purina.databinding.DigitalVetDiseaseBinding

class DigitalVetDetailsListAdapter(private val clickListener: (Diseases) -> Unit) :
  RecyclerView.Adapter<DigitalVetDetailsListAdapter.DigitalVetDetailsViewHolder>() {

  private var diseaseList = ArrayList<Diseases>()
  private var countValue: Int = 0

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DigitalVetDetailsViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding: DigitalVetDiseaseBinding =
      DataBindingUtil.inflate(layoutInflater, R.layout.digital_vet_disease, parent, false)
    return DigitalVetDetailsViewHolder(binding, parent.context, countValue)
  }

  override fun onBindViewHolder(holder: DigitalVetDetailsViewHolder, position: Int) {
    holder.bind(diseaseList[position], clickListener)
  }

  override fun getItemCount(): Int {
    return diseaseList.size
  }

  fun setList(diseaseData: List<Diseases>, count: Int) {
    diseaseList.clear()
    countValue = count
    diseaseList.addAll(diseaseData)
  }

  fun Clear(){
    diseaseList.clear()
  }

  class DigitalVetDetailsViewHolder(
    val binding: DigitalVetDiseaseBinding,
    val ctx: Context,
    val cnt: Int
  ) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(dataObj: Diseases, clickListener: (Diseases) -> Unit) {
      binding.tvDiseaseName.text = dataObj.diseaseName
      binding.tvCounter.text = dataObj.count.toString() + "/" + cnt.toString()
      binding.llDiseaseLayout.setOnClickListener {
        clickListener(dataObj)
      }
    }

  }

}