package cargill.com.purina.dashboard.View.IdentifyDiseases

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import cargill.com.purina.R
import cargill.com.purina.dashboard.Model.IdentifyDisease.Disease
import cargill.com.purina.databinding.DiseaseItemBinding

class DiseaseListAdapter(private val clickListener: (Disease) -> Unit) :
  RecyclerView.Adapter<DiseaseListAdapter.DiseaseViewHolder>() {

  private var diseaseList = ArrayList<Disease>()
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiseaseViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding: DiseaseItemBinding =
      DataBindingUtil.inflate(layoutInflater, R.layout.disease_item, parent, false)
    return DiseaseViewHolder(binding, parent.context)
  }

  override fun onBindViewHolder(holder: DiseaseViewHolder, position: Int) {
    holder.bind(diseaseList[position], clickListener)
  }

  override fun getItemCount(): Int {
    return diseaseList.size
  }

  fun setList(diseaseData: ArrayList<Disease>) {
    diseaseList.clear()
    diseaseList.addAll(diseaseData)
  }

  fun clear(){
    diseaseList.clear()
  }

  class DiseaseViewHolder(val binding: DiseaseItemBinding, val ctx: Context) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(dataObj: Disease, clickListener: (Disease) -> Unit) {
      binding.tvDiseaseName.text = dataObj.diseaseName.trim().toString()
      binding.llDiseaseLayout.setOnClickListener {
        clickListener(dataObj)
      }
    }

  }

}