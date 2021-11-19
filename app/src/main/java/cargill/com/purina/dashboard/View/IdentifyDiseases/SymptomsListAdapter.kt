package cargill.com.purina.dashboard.View.IdentifyDiseases

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import cargill.com.purina.R
import cargill.com.purina.dashboard.Model.IdentifyDisease.Symptoms
import cargill.com.purina.databinding.SymptomsItemBinding

class SymptomsListAdapter() : RecyclerView.Adapter<SymptomsListAdapter.SymptomsViewHolder>() {

  private var diseaseList = ArrayList<Symptoms>()
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomsViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val binding: SymptomsItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.symptoms_item, parent, false)
    return SymptomsViewHolder(binding, parent.context)
  }

  override fun onBindViewHolder(holder: SymptomsViewHolder, position: Int) {
    holder.bind(diseaseList[position])
  }

  override fun getItemCount(): Int {
    return diseaseList.size
  }

  fun setList(diseaseData: ArrayList<Symptoms>) {
    diseaseList.clear()
    diseaseList.addAll(diseaseData)
  }

  fun clear(){
    diseaseList.clear()
  }

  class SymptomsViewHolder(val binding: SymptomsItemBinding, val ctx: Context) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(dataObj: Symptoms) {
      binding.tvSymptomsName.text = dataObj.name
      if(dataObj.description.length>0){
        binding.tvSymptomsDesc.text = dataObj.description
      }else{
        binding.tvSymptomsDesc.visibility = View.GONE
      }
    }
  }
}